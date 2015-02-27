/*
 * @(#)BeansBinding.java
 * 
 * Copyright (c) 2013 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the  
 * license agreement you entered into with the copyright holders. For details
 * see accompanying license terms.
 */
package org.jhotdraw.beans;

import javax.annotation.Nullable;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * Can bind a property of a JavaBean to a property of another JavaBean. <p> The
 * binding can be unidirectional or bidirectional.
 *
 *
 * @author Werner Randelshofer
 * @version 1.0 2013-06-13 Created.
 */
public class BeansBinding {

    private @Nullable String sourceProperty;
    private @Nullable String targetProperty;
    private @Nullable Object source;
    private @Nullable Object target;
    private boolean bidirectional;
    private @Nullable Method targetWriteMethod;
    private @Nullable Method sourceWriteMethod;
    private @Nullable Method sourceReadMethod;

    private class Handler implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() == source) {
                if (sourceProperty.equals(evt.getPropertyName())) {
                    if (target != null) {
                        try {
                            getTargetWriteMethod().invoke(target, evt.getNewValue());
                        } catch (Exception ex) {
                            InternalError ie = new InternalError("Could not set property \"" + targetProperty + "\" on " + target);
                            ie.initCause(ex);
                            throw ie;
                        }
                    }
                }
            } else if (bidirectional && evt.getSource() == target) {
                if (targetProperty.equals(evt.getPropertyName())) {
                    if (source != null) {
                        try {
                            getSourceWriteMethod().invoke(source, evt.getNewValue());
                        } catch (Exception ex) {
                            InternalError ie = new InternalError("Could not set property \"" + targetProperty + "\" on " + target);
                            ie.initCause(ex);
                            throw ie;
                        }
                    }
                }
            }
        }
    }
    private Handler handler = new Handler();

    /**
     * Creates a bidirectional binding from a source bean to a target bean.
     * Updates the value of the target bean.
     *
     * @param source The source bean.
     * @param sourceProperty The name of the source property.
     * @param target The target bean.
     * @param targetProperty The name of the target property.
     */
    public void bind(@Nullable Object source, String sourceProperty, @Nullable Object target, String targetProperty) {
        setSource(source, sourceProperty);
        setTarget(target, targetProperty);
        bidirectional = true;
        updateTarget();
    }
    
    /** Removes the binding. */
    public void unbind() {
        setSource(null, sourceProperty);
        setTarget(null, targetProperty);
    }

    private void addPropertyChangeListener(Object bean, PropertyChangeListener listener) {
        try {
            Method m = bean.getClass().getMethod("addPropertyChangeListener", PropertyChangeListener.class);
            m.invoke(bean, listener);
        } catch (Exception ex) {
            InternalError ie = new InternalError("Could not add property change listener to " + bean);
            ie.initCause(ex);
            throw ie;
        }
    }

    private void removePropertyChangeListener(Object bean, PropertyChangeListener listener) {
        try {
            Method m = bean.getClass().getMethod("removePropertyChangeListener", PropertyChangeListener.class);
            m.invoke(bean, listener);
        } catch (Exception ex) {
            InternalError ie = new InternalError("Could not remove property change listener from " + bean);
            ie.initCause(ex);
            throw ie;
        }
    }

    /**
     * Sets the source bean.
     *
     * @param source
     * @param sourceProperty
     */
    private void setSource(@Nullable Object source, String sourceProperty) {
        if (this.source != null) {
            removePropertyChangeListener(this.source, handler);
        }

        this.source = source;
        this.sourceProperty = sourceProperty;
        sourceWriteMethod = null;
        sourceReadMethod = null;
        if (this.source != null) {
            addPropertyChangeListener(this.source, handler);
        }
    }

    private void setTarget(@Nullable Object target, String targetProperty) {
        if (this.target != null) {
            removePropertyChangeListener(this.target, handler);
        }
        this.target = target;
        this.targetProperty = targetProperty;
        targetWriteMethod = null;
        if (this.target != null) {
            addPropertyChangeListener(this.target, handler);
        }
    }

    public void updateTarget() {
        try {
            Object value = getSourceReadMethod().invoke(source);
            getTargetWriteMethod().invoke(target, value);
        } catch (Exception ex) {
            InternalError ie = new InternalError("Could not update target from source.");
            ie.initCause(ex);
            throw ie;
        }
    }

    private Method getTargetWriteMethod() {
        if (targetWriteMethod == null) {
            try {
                PropertyDescriptor pd = new PropertyDescriptor(targetProperty, target.getClass());
                targetWriteMethod = pd.getWriteMethod();
            } catch (IntrospectionException ex) {
                InternalError ie = new InternalError("Could not create target property descriptor for " + target);
                ie.initCause(ex);
                throw ie;
            }
        }
        return targetWriteMethod;
    }

    private Method getSourceWriteMethod() {
        if (sourceWriteMethod == null) {
            try {
                PropertyDescriptor pd = new PropertyDescriptor(sourceProperty, source.getClass());
                sourceWriteMethod = pd.getWriteMethod();
            } catch (IntrospectionException ex) {
                InternalError ie = new InternalError("Could not create source property descriptor for " + source);
                ie.initCause(ex);
                throw ie;
            }
        }
        return sourceWriteMethod;
    }

    private Method getSourceReadMethod() {
        if (sourceReadMethod == null) {
            try {
                PropertyDescriptor pd = new PropertyDescriptor(sourceProperty, source.getClass());
                sourceReadMethod = pd.getReadMethod();
            } catch (IntrospectionException ex) {
                InternalError ie = new InternalError("Could not create source property descriptor for " + source);
                ie.initCause(ex);
                throw ie;
            }
        }
        return sourceReadMethod;
    }
}
