/*
 * @(#)DefaultDOMFactory.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.xml;

import java.util.*;

/**
 * {@code DefaultDOMFactory} can be used to serialize DOMStorable objects in a
 * DOM with the use of a mapping between Java class names and DOM element names.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class DefaultDOMFactory extends JavaPrimitivesDOMFactory {

    private static final HashMap<Class<?>, String> CLASS_TO_NAME = new HashMap<Class<?>, String>();
    private static final HashMap<String, Object> NAME_TO_PROTOTYPE = new HashMap<String, Object>();
    private static final HashMap<Class<?>, String> ENUM_TO_NAME = new HashMap<Class<?>, String>();
    private static final HashMap<String, Class<?>> NAME_TO_ENUM = new HashMap<String, Class<?>>();
    @SuppressWarnings("rawtypes")
    private static final HashMap<Enum, String> ENUM_TO_VALUE = new HashMap<Enum, String>();
    @SuppressWarnings("rawtypes")
    private static final HashMap<String, Set<Enum>> VALUE_TO_ENUM = new HashMap<String, Set<Enum>>();

    /**
     * Creates a new instance.
     */
    public DefaultDOMFactory() {
    }

    /**
     * Adds a DOMStorable class to the DOMFactory.
     */
    public void addStorableClass(String name, Class<?> c) {
        NAME_TO_PROTOTYPE.put(name, c);
        CLASS_TO_NAME.put(c, name);
    }

    /**
     * Adds a DOMStorable prototype to the DOMFactory.
     */
    public void addStorable(String name, DOMStorable prototype) {
        NAME_TO_PROTOTYPE.put(name, prototype);
        CLASS_TO_NAME.put(prototype.getClass(), name);
    }

    /**
     * Adds an Enum class to the DOMFactory.
     */
    public void addEnumClass(String name, Class<?> c) {
        ENUM_TO_NAME.put(c, name);
        NAME_TO_ENUM.put(name, c);
    }

    /**
     * Adds an Enum value to the DOMFactory.
     */
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

    /**
     * Creates a DOMStorable object.
     */
    @Override
    public Object create(String name) {
        Object o = NAME_TO_PROTOTYPE.get(name);
        if (o == null) {
            throw new IllegalArgumentException("Storable name not known to factory: " + name);
        }
        if (o instanceof Class<?>) {
            try {
                return ((Class<?>) o).newInstance();
            } catch (Exception e) {
                IllegalArgumentException error = new IllegalArgumentException("Storable class not instantiable by factory: " + name);
                error.initCause(e);
                throw error;
            }
        } else {
            try {
                return o.getClass().getMethod("clone", (Class<?>[]) null).
                        invoke(o, (Object[]) null);
            } catch (Exception e) {
                IllegalArgumentException error = new IllegalArgumentException("Storable prototype not cloneable by factory. Name: " + name);
                error.initCause(e);
                throw error;
            }
        }
    }

    @Override
    public String getName(Object o) {
        String name = (o == null) ? null : CLASS_TO_NAME.get(o.getClass());
        if (name == null) {
            name = super.getName(o);
        }
        if (name == null) {
            throw new IllegalArgumentException("Storable class not known to factory. Storable class:" + o.getClass() + " Factory:" + this.getClass());
        }
        return name;
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
}
