/*
 * @(#)AbstractConstrainer.java  1.0  August 1, 2007
 *
 * Copyright (c) 2007 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.draw;

import javax.swing.*;
import javax.swing.event.*;
import org.jhotdraw.beans.*;

/**
 * AbstractConstrainer.
 *
 * @author Werner Randelshofer
 * @version 1.0 August 1, 2007 Created.
 */
public abstract class AbstractConstrainer extends AbstractBean implements Constrainer {
    /** The listeners waiting for model changes. */
    protected EventListenerList listenerList = new EventListenerList();
    /**
     * Only one <code>ChangeEvent</code> is needed per model instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this".
     */
    protected transient ChangeEvent changeEvent = null;

    
    /** Creates a new instance. */
    public AbstractConstrainer() {
    }
    
    /**
     * Adds a <code>ChangeListener</code>.
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    
    /**
     * Removes a <code>ChangeListener</code>.
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }
    
    /**
     * Runs each <code>ChangeListener</code>'s <code>stateChanged</code> method.
     *
     * @see #setRangeProperties
     * @see EventListenerList
     */
    protected void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -=2 ) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
    }
    
    public AbstractConstrainer clone() {
        AbstractConstrainer that = (AbstractConstrainer) super.clone();
        that.listenerList = new EventListenerList();
        return that;
    }
}
