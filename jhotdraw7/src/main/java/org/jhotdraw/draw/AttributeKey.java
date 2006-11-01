/*
 * @(#)AttributeKey.java  1.0.1  2006-07-14
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.draw;

import java.lang.reflect.*;
/**
 * Provides typesafe getter and setter for a Figure attribute.
 * An AttributeKey has a name, a type and a default value. The default value
 * is returned by Figure.getAttribute, if a Figure does not have an attribute
 * of the specified key.
 *
 * @author Werner Randelshofer
 * @version 1.0.1 2006-07-14 Null values are not returned anymore when null
 * values are not allowed. 
 * <br>1.0 7. Juni 2006 Created.
 */
public class AttributeKey<T> {
    private String key;
    private T defaultValue;
    private boolean isNullValueAllowed;
    
    /** Creates a new instance. */
    public AttributeKey(String key) {
        this(key, null, true);
    }
    public AttributeKey(String key, T defaultValue) {
        this(key, defaultValue, true);
    }
    public AttributeKey(String key, T defaultValue, boolean isNullValueAllowed) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.isNullValueAllowed = isNullValueAllowed;
    }
    
    public String getKey() {
        return key;
    }
    public T getDefaultValue() {
        return defaultValue;
    }
    
    public T get(Figure f) {
        T value = (T) f.getAttribute(this);
        return (value == null && ! isNullValueAllowed) ? defaultValue : value;
    }
    
    public void set(Figure f, T value) {
        if (value == null && ! isNullValueAllowed) {
            throw new NullPointerException("Null value not allowed for AttributeKey "+key);
        }
        f.setAttribute(this, value);
    }
    public void basicSet(Figure f, T value) {
        if (value == null && ! isNullValueAllowed) {
            throw new NullPointerException("Null value not allowed for AttributeKey "+key);
        }
        f.basicSetAttribute(this, value);
    }
    
    public boolean equals(Object o) {
        if (o instanceof AttributeKey) {
            AttributeKey that = (AttributeKey) o;
            return that.key.equals(this.key);
        }
        return false;
    }
    
    public int hashCode() {
        return key.hashCode();
    }
    
    public String toString() {
        return key;
    }
    
    public boolean isNullValueAllowed() {
        return isNullValueAllowed;
    }
    
    public static void main(String[] args) {
        TypeVariable v = new AttributeKey<Double>("hey").getClass().getTypeParameters()[0];
    }
    
    public boolean isAssignable(Object value) {
        if (value == null) {
            return isNullValueAllowed();
        }
        
        // XXX - This works, but maybe there is an easier way to do this?
        try {
            T a = (T) value;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }
}
