/*
 * Copyright (C) 2023 JHotDraw.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.jhotdraw.draw.figure;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;

/** implementation of Attribute storage and processing. */
public final class Attributes {

  private HashMap<AttributeKey<?>, Object> attributes = new HashMap<>();
  /**
   * Forbidden attributes can't be put by the put() operation. They can only be changed by put().
   */
  private HashSet<AttributeKey<?>> forbiddenAttributes;

  private AttributeListener listener;

  private Supplier<List<Attributes>> DEPENDENT;

  public Attributes() {
    this(null, null);
  }

  public Attributes(AttributeListener listener) {
    this(listener, null);
  }

  public Attributes(AttributeListener listener, Supplier<List<Attributes>> dependent) {
    this.listener = listener;
    this.DEPENDENT = dependent == null ? () -> Collections.emptyList() : dependent;
  }

  public void setAttributeEnabled(AttributeKey<?> key, boolean b) {
    if (forbiddenAttributes == null) {
      forbiddenAttributes = new HashSet<>();
    }
    if (b) {
      forbiddenAttributes.remove(key);
    } else {
      forbiddenAttributes.add(key);
    }
  }

  /**
   * Is this attribute enabled for this figure to be processed.
   *
   * @param key
   * @return
   */
  public boolean isAttributeEnabled(AttributeKey<?> key) {
    return forbiddenAttributes == null || !forbiddenAttributes.contains(key);
  }

  /** Set attributes from map. */
  public void setAttributes(Map<AttributeKey<?>, Object> map) {
    for (Map.Entry<AttributeKey<?>, Object> entry : map.entrySet()) {
      set((AttributeKey<Object>) entry.getKey(), entry.getValue());
    }
  }

  /**
   * Returns a view to all attributes of this figure. By convention, an unmodifiable map is
   * returned.
   */
  public Map<AttributeKey<?>, Object> getAttributes() {
    return (Map<AttributeKey<?>, Object>) new HashMap<>(attributes);
  }

  /**
   * Gets data which can be used to restore the attributes of the figure after a set has been
   * applied to it.
   */
  public Object getAttributesRestoreData() {
    List<Attributes> dependent = DEPENDENT.get();
    if (dependent.isEmpty()) {
      return getAttributes();
    } else {
      List<Map<AttributeKey<?>, Object>> list = new ArrayList<>();
      list.add(getAttributes());
      for (Attributes attr : dependent) {
        list.add(attr.getAttributes());
      }
      return list;
    }
  }

  /** Restores the attributes of the figure to a previously stored state. */
  public void restoreAttributesTo(Object restoreData) {
    if (restoreData instanceof List) {
      List<Map<AttributeKey<?>, Object>> list = (List<Map<AttributeKey<?>, Object>>) restoreData;
      restoreAttributesTo(list.get(0));
      int idx = 1;
      for (Attributes attr : DEPENDENT.get()) {
        attr.restoreAttributesTo(list.get(idx));
        idx++;
      }
    } else {
      attributes.clear();
      HashMap<AttributeKey<?>, Object> restoreDataHashMap =
          (HashMap<AttributeKey<?>, Object>) restoreData;
      setAttributes(restoreDataHashMap);
    }
  }

  /**
   * Sets an attribute on the figure and calls {@code attributeChanged} on all registered {@code
   * FigureListener}s if the attribute value has changed.
   *
   * <p>For efficiency reasons, the drawing is not automatically repainted. If you want the drawing
   * to be repainted when the attribute is changed, you can either use {@code key.set(figure,
   * value); } or
   *
   * <pre>
   * figure.willChange();
   * figure.set(...);
   * figure.changed();
   * </pre>
   *
   * @see AttributeKey#set
   */
  public <T> Attributes set(AttributeKey<T> key, T newValue) {
    if (forbiddenAttributes == null || !forbiddenAttributes.contains(key)) {
      T oldValue = key.put(attributes, newValue);
      fireAttributeChanged(key, oldValue, newValue);
    }

    DEPENDENT.get().forEach(a -> a.set(key, newValue));
    return this;
  }

  /**
   * Gets an attribute from the Figure.
   *
   * @see AttributeKey#get
   * @return Returns the attribute value. If the Figure does not have an attribute with the
   *     specified key, returns key.getDefaultValue().
   */
  public <T> T get(AttributeKey<T> key) {
    return key.get(attributes);
  }

  public static AttributeKey<?> getAttributeKey(String name) {
    return AttributeKeys.SUPPORTED_ATTRIBUTES_MAP.get(name);
  }

  public <T> void removeAttribute(AttributeKey<T> key) {
    if (hasAttribute(key)) {
      T oldValue = get(key);
      attributes.remove(key);
      fireAttributeChanged(key, oldValue, key.getDefaultValue());
    }
  }

  /**
   * Is this attribute set within this container.
   *
   * @param key
   * @return
   */
  public boolean hasAttribute(AttributeKey<?> key) {
    return attributes.containsKey(key);
  }

  private <T> void fireAttributeChanged(AttributeKey<T> attribute, T oldValue, T newValue) {
    if (listener != null) {
      listener.attributeChanged(attribute, oldValue, newValue);
    }
  }

  @FunctionalInterface
  public static interface AttributeListener {
    <T> void attributeChanged(AttributeKey<T> attribute, T oldValue, T newValue);
  }

  public static Attributes from(Attributes source) {
    return from(source, null, null);
  }

  public static Attributes from(Attributes source, AttributeListener listener) {
    return from(source, listener, null);
  }

  public static Attributes from(
      Attributes source, AttributeListener listener, Supplier<List<Attributes>> dependent) {
    Attributes attr = new Attributes(listener, dependent);
    attr.attributes.putAll(source.attributes);
    if (source.forbiddenAttributes != null) {
      attr.forbiddenAttributes = new HashSet<>(source.forbiddenAttributes);
    }
    return attr;
  }

  public static Supplier<List<Attributes>> attrSupplier(Supplier<List<Figure>> dependent) {
    return () -> dependent.get().stream().map(f -> f.attr()).collect(toList());
  }
}
