/*
 * @(#)DefaultDOMFactory.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.xml;

import java.io.IOException;
import java.util.*;

/**
 * {@code DefaultDOMFactory} can be used to serialize DOMStorable objects in a DOM with the use of a
 * mapping between Java class names and DOM element names.
 */
public class DefaultDOMFactory extends JavaPrimitivesDOMFactory {

  private record ClassRegistration<T>(
      String tagName,
      Class<T> prototype,
      BiConsumerWithIOException<T, DOMInput> read,
      BiConsumerWithIOException<T, DOMOutput> write) {}

  private final HashMap<String, ClassRegistration> REGISTRATION = new HashMap<>();

  private final HashMap<Class<?>, String> ENUM_TO_NAME = new HashMap<Class<?>, String>();
  private final HashMap<String, Class<?>> NAME_TO_ENUM = new HashMap<String, Class<?>>();

  @SuppressWarnings("rawtypes")
  private static final HashMap<Enum, String> ENUM_TO_VALUE = new HashMap<Enum, String>();

  @SuppressWarnings("rawtypes")
  private static final HashMap<String, Set<Enum>> VALUE_TO_ENUM = new HashMap<String, Set<Enum>>();

  public DefaultDOMFactory() {}

  /** register a dom tag processor */
  public <T> void register(
      String tagName,
      Class<T> prototype,
      BiConsumerWithIOException<T, DOMInput> read,
      BiConsumerWithIOException<T, DOMOutput> write) {
    final ClassRegistration reg = new ClassRegistration(tagName, prototype, read, write);
    // to avoid double hashmaps we register each registration twice, one for the key tagName and one
    // for the key className.
    REGISTRATION.put(tagName, reg);
    REGISTRATION.put(reg.prototype.getName(), reg);
  }

  /**
   * register a dom tag processor assuming the the instance of this prototype is able to write /
   * read itself.
   */
  public <T extends DOMStorable> void register(String tagName, Class<T> prototype) {
    if (!prototype.isInstance(DOMStorable.class)) {
      throw new IllegalArgumentException(prototype.getName() + " does not implement DOMStorable");
    }
    register(
        tagName,
        prototype,
        (t, domInput) -> ((DOMStorable) t).read(domInput),
        (t, domOutput) -> ((DOMStorable) t).write(domOutput));
  }

  //  /** Adds a DOMStorable class to the DOMFactory. */
  //  public void addStorableClass(String name, Class<?> c) {
  //    NAME_TO_PROTOTYPE.put(name, c);
  //    CLASS_TO_NAME.put(c, name);
  //  }
  //
  //  /** Adds a DOMStorable prototype to the DOMFactory. */
  //  public void addStorable(String name, DOMStorable prototype) {
  //    NAME_TO_PROTOTYPE.put(name, prototype);
  //    CLASS_TO_NAME.put(prototype.getClass(), name);
  //  }

  /** Adds an Enum class to the DOMFactory. */
  public void addEnumClass(String name, Class<?> c) {
    ENUM_TO_NAME.put(c, name);
    NAME_TO_ENUM.put(name, c);
  }

  /** Adds an Enum value to the DOMFactory. */
  @SuppressWarnings("rawtypes")
  public <T extends Enum<T>> void addEnum(String value, Enum<T> e) {
    ENUM_TO_VALUE.put(e, value);
    Set<Enum> enums;
    if (VALUE_TO_ENUM.containsKey(value)) {
      enums = VALUE_TO_ENUM.get(value);
    } else {
      enums = new HashSet<Enum>();
      VALUE_TO_ENUM.put(value, enums);
    }
    enums.add(e);
  }

  /** Creates a DOMStorable object and reads it in. */
  @Override
  public Object createPrototype(String name) {
    // Object o = NAME_TO_PROTOTYPE.get(name);
    ClassRegistration reg = REGISTRATION.get(name);
    if (reg == null) {
      throw new IllegalArgumentException("Storable name not known to factory: " + name);
    }
    try {
      return reg.prototype().getConstructor().newInstance();
    } catch (Exception e) {
      IllegalArgumentException error =
          new IllegalArgumentException("Storable class not instantiable by factory: " + name);
      error.initCause(e);
      throw error;
    }
  }

  @Override
  public void write(DOMOutput out, Object o) throws IOException {
    if (o == null) {
      super.write(out, o);
      return;
    }
    ClassRegistration reg = REGISTRATION.get(o.getClass().getName());
    if (reg != null) {
      reg.write().accept(o, out);
    } else {
      super.write(out, o);
    }
  }

  @Override
  public Object read(DOMInput in) throws IOException {
    String tagName = in.getTagName();
    ClassRegistration reg = REGISTRATION.get(tagName);

    if (reg != null) {
      Object instance;
      try {
        instance = reg.prototype().getConstructor().newInstance();
      } catch (Exception ex) {
        throw new IllegalArgumentException("could not create class", ex);
      }
      reg.read().accept(instance, in);
      return instance;
    }

    return super.read(in);
  }

  @Override
  public String getName(Object o) {
    if (o == null) {
      return super.getName(o);
    }
    return Optional.ofNullable(REGISTRATION.get(o.getClass().getName()))
        .map(reg -> reg.tagName())
        .or(() -> Optional.ofNullable(super.getName(o)))
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Storable class not known to factory. Storable class:"
                        + o.getClass()
                        + " Factory:"
                        + DefaultDOMFactory.this.getClass()));
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected String getEnumName(Enum e) {
    String name = ENUM_TO_NAME.get(e.getClass());
    if (name == null) {
      throw new IllegalArgumentException("Enum class not known to factory:" + e.getClass());
    }
    return name;
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected String getEnumValue(Enum e) {
    return (ENUM_TO_VALUE.containsKey(e)) ? ENUM_TO_VALUE.get(e) : e.toString();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  protected <T extends Enum<T>> Enum<T> createEnum(String name, String value) {
    Class<T> enumClass = (Class<T>) NAME_TO_ENUM.get(name);
    if (enumClass == null) {
      throw new IllegalArgumentException("Enum name not known to factory:" + name);
    }
    Set<Enum> enums = VALUE_TO_ENUM.get(value);
    if (enums == null) {
      return Enum.valueOf(enumClass, value);
    }
    for (Enum e : enums) {
      if (e.getClass() == enumClass) {
        return e;
      }
    }
    throw new IllegalArgumentException("Enum value not known to factory:" + value);
  }

  @FunctionalInterface
  public static interface BiConsumerWithIOException<T, U> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     */
    void accept(T t, U u) throws IOException;

    /**
     * Returns a composed {@code BiConsumer} that performs, in sequence, this operation followed by
     * the {@code after} operation. If performing either operation throws an exception, it is
     * relayed to the caller of the composed operation. If performing this operation throws an
     * exception, the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code BiConsumer} that performs in sequence this operation followed by
     *     the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default BiConsumerWithIOException<T, U> andThen(
        BiConsumerWithIOException<? super T, ? super U> after) throws IOException {
      Objects.requireNonNull(after);

      return (l, r) -> {
        accept(l, r);
        after.accept(l, r);
      };
    }
  }
}
