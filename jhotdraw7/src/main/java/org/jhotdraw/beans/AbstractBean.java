/*
 * @(#)AbstractBean.java  1.2  2006-06-21
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
package org.jhotdraw.beans;

import java.beans.*;
import java.io.*;

/**
 * Abstract class for models that have to support property change listeners.<p>
 * Implements the methods required for adding and removing property change
 * listeners.
 *
 * @author Werner Randelshofer
 * @version 1.2 2006-06-21 Implemented Cloneable. The cloned object has no listeners
 * assigned to it.
 * <br>1.1 2004-01-18
 * <br>1.0 2001-08-04
 */
public class AbstractBean extends Object implements java.io.Serializable, Cloneable {
    protected PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener( propertyName, listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(propertyName, listener);
    }
    
    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    public AbstractBean clone() {
        AbstractBean that;
        try {
            that = (AbstractBean) super.clone();
        } catch (CloneNotSupportedException ex) {
            InternalError error =  new InternalError("Clone failed");
            error.initCause(ex);
            throw error;
        }
        that.propertySupport = new PropertyChangeSupport(that);
        return that;
    }
}