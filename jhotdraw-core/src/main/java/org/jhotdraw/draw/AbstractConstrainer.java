/*
 * @(#)AbstractConstrainer.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.draw;

import edu.umd.cs.findbugs.annotations.Nullable;
import javax.swing.event.*;
import org.jhotdraw.beans.*;

/**
 * This abstract class can be extended to implement a {@link Constrainer}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractConstrainer extends AbstractBean implements Constrainer {
    private static final long serialVersionUID = 1L;
    /** The listeners waiting for model changes. */
    protected EventListenerList listenerList = new EventListenerList();
    /**
     * Only one <code>ChangeEvent</code> is needed per model instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this".
     */
    @Nullable protected transient ChangeEvent changeEvent = null;

    
    /** Creates a new instance. */
    public AbstractConstrainer() {
    }
    
    /**
     * Adds a <code>ChangeListener</code>.
     */
    @Override
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    
    /**
     * Removes a <code>ChangeListener</code>.
     */
    @Override
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }
    
    /**
     * Runs each <code>ChangeListener</code>'s <code>stateChanged</code> method.
     *
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
    
    @Override
    public AbstractConstrainer clone() {
        AbstractConstrainer that = (AbstractConstrainer) super.clone();
        that.listenerList = new EventListenerList();
        return that;
    }
}
