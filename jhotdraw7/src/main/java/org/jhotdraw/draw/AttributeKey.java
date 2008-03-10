/*
 * @(#)AttributeKey.java  2.0.1  2008-02-13
 *
 * Copyright (c) 1996-2008 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.draw;

import java.lang.reflect.*;
import java.util.*;
import javax.swing.undo.*;
import org.jhotdraw.util.*;
/**
 * AttributeKey provides typesafe access to figure attributes.
 * <p>
 * An AttributeKey has a name, a type and a default value. The default value
 * is returned by Figure.getAttribute, if a Figure does not have an attribute
 * of the specified key.
 * <p>
 * The following code example shows how to basicSet and get an attribute on a Figure.
 * <pre>
 * Figure aFigure;
 * AttributeKeys.STROKE_COLOR.set(aFigure, Color.blue);
 * </pre>
 * <p>
 * See {@link AttributeKeys} for a list of useful attribute keys.
 * 
 * @author Werner Randelshofer
 * @version 2.0.1 2008-02-13 Fixed comments. Removed equals and hashCode.
 * <br>2.0 2007-05-12 Removed basicSet methods.
 * <br>1.2 2007-04-10 Convenience methods for getting and setting a clone
 * of an attribute added.
 * <br>1.1 2006-12-29 Support for getting/setting attribute keys on a
 * Map added.
 * <br>1.0.1 2006-07-14 Null values are not returned anymore when null
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
    /**
     * Gets a clone of the value from the Figure.
     */
    public T getClone(Figure f) {
        T value = get(f);
        try {
            return value == null ? null : (T) Methods.invoke(value,"clone");
        } catch (NoSuchMethodException ex) {
            InternalError e = new InternalError();
            e.initCause(ex);
            throw e;
        }
    }
    
    /**
     * Gets the value of the attribute denoted by this AttributeKey from
     * a Figure.
     * 
     * @param f A figure.
     * @return The value of the attribute.
     */
    public T get(Figure f) {
        T value = (T) f.getAttribute(this);
        return (value == null && ! isNullValueAllowed) ? defaultValue : value;
    }
    /**
     * Gets the value of the attribute denoted by this AttributeKey from
     * a Map.
     * 
     * @param a A Map.
     * @return The value of the attribute.
     */
    public T get(Map<AttributeKey,Object> a) {
        T value = (T) a.get(this);
        return (value == null && ! isNullValueAllowed) ? defaultValue : value;
    }
    
    /**
     * Convenience method for setting a value on the 
     * specified figure and calling willChange before and changed 
     * after setting the value.
     *
     * @param f the Figure
     * @param value the attribute value
     */
    public void set(Figure f, T value) {
        f.willChange();
        basicSet(f, value);
        f.changed();
    }
    /**
     * Sets a value on the specified figure without invoking {@code willChange}
     * and {@code changed} on the figure.
     * <p>
     * This method can be used to efficiently build a drawing from an 
     * {@link InputFormat}.
     *
     * @param f the Figure
     * @param value the attribute value
     */
    public void basicSet(Figure f, T value) {
        if (value == null && ! isNullValueAllowed) {
            throw new NullPointerException("Null value not allowed for AttributeKey "+key);
        }
        f.setAttribute(this, value);
    }
    
    /**
     * Sets the attribute and returns an UndoableEditEvent which can be used
     * to undo it.
     */
    public UndoableEdit setUndoable(final Figure figure, final T value, final ResourceBundleUtil labels) {
        if (value == null && ! isNullValueAllowed) {
            throw new NullPointerException("Null value not allowed for AttributeKey "+key);
        }
        
        final Object restoreData = figure.getAttributesRestoreData();
        figure.willChange();
        figure.setAttribute(this, value);
        figure.changed();
        
        UndoableEdit edit = new AbstractUndoableEdit() {
            public String getPresentationName() {
                return labels.getString(getKey());
            }
            public void undo() {
                super.undo();
                figure.willChange();
                figure.restoreAttributesTo(restoreData);
                figure.changed();
            }
            public void redo() {
                super.redo();
                figure.willChange();
                figure.setAttribute(AttributeKey.this, value);
                figure.changed();
            }
        };
        return edit;
        
    }
    /**
     * Convenience method for seting a clone of a value on the 
     * specified figure and calling willChange before and changed 
     * after setting the value.
     *
     * @param f the Figure
     * @param value the attribute value
     */
    public void setClone(Figure f, T value) {
        f.willChange();
        basicSetClone(f, value);
        f.changed();
    }
    /**
     * Sets a clone of a value on the specified figure, without invoking
     * {@code willChange} and {@code changed} on the figure.
     * <p>
     * This method can be used to efficiently build a drawing from an 
     * {@link InputFormat}.
     *
     * @param f the Figure
     * @param value the attribute value
     */
    public void basicSetClone(Figure f, T value) {
        try {
            basicSet(f, value == null ? null : (T) Methods.invoke(value,"clone"));
        } catch (NoSuchMethodException ex) {
            InternalError e = new InternalError();
            e.initCause(ex);
            throw e;
        }
    }
    public void set(Map<AttributeKey,Object> a, T value) {
        if (value == null && ! isNullValueAllowed) {
            throw new NullPointerException("Null value not allowed for AttributeKey "+key);
        }
        a.put(this, value);
    }
    /**
     * Sets a clone of the value to the Figure without firing events.
     */
    public void setClone(Map<AttributeKey,Object> a, T value) {
        try {
            set(a, value == null ? null : (T) Methods.invoke(value,"clone"));
        } catch (NoSuchMethodException ex) {
            InternalError e = new InternalError();
            e.initCause(ex);
            throw e;
        }
    }
    
    public String toString() {
        return key;
    }
    
    public boolean isNullValueAllowed() {
        return isNullValueAllowed;
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
