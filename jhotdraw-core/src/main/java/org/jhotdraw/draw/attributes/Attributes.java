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

package org.jhotdraw.draw.attributes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;

/** implementation of Attribute storage and processing. */
public class Attributes {
  private HashMap<AttributeKey<?>, Object> attributes = new HashMap<>();
  /**
   * Forbidden attributes can't be put by the put() operation. They can only be changed by put().
   */
  private HashSet<AttributeKey<?>> forbiddenAttributes;

  private AttributeListener listener;

  public Attributes() {}

  public Attributes(AttributeListener listener) {
    this.listener = listener;
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

  public boolean isAttributeEnabled(AttributeKey<?> key) {
    return forbiddenAttributes == null || !forbiddenAttributes.contains(key);
  }

  @SuppressWarnings("unchecked")
  public void setAttributes(Map<AttributeKey<?>, Object> map) {
    for (Map.Entry<AttributeKey<?>, Object> entry : map.entrySet()) {
      set((AttributeKey<Object>) entry.getKey(), entry.getValue());
    }
  }

  public Map<AttributeKey<?>, Object> getAttributes() {
    return (Map<AttributeKey<?>, Object>) new HashMap<>(attributes);
  }

  public Object getAttributesRestoreData() {
    return getAttributes();
  }

  public void restoreAttributesTo(Object restoreData) {
    attributes.clear();
    @SuppressWarnings("unchecked")
    HashMap<AttributeKey<?>, Object> restoreDataHashMap =
        (HashMap<AttributeKey<?>, Object>) restoreData;
    setAttributes(restoreDataHashMap);
  }

  /**
   * Sets an attribute of the figure. AttributeKey name and semantics are defined by the class
   * implementing the figure interface.
   */
  public <T> void set(AttributeKey<T> key, T newValue) {
    if (forbiddenAttributes == null || !forbiddenAttributes.contains(key)) {
      T oldValue = key.put(attributes, newValue);
      fireAttributeChanged(key, oldValue, newValue);
    }
  }

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
}
