/**
 * @(#)AbstractColorSlidersModel.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.color;

import java.util.*;
import javax.swing.event.*;
import org.jhotdraw.beans.AbstractBean;

/**
 * AbstractColorSlidersModel.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractColorSlidersModel extends AbstractBean implements ColorSliderModel {

    private static final long serialVersionUID = 1L;
    /**
     * ChangeListener's listening to changes in this model.
     */
    protected LinkedList<ChangeListener> listeners;

    @Override
    /**
     * add new change listener 
     */
    public void addChangeListener(ChangeListener l) {
        if (listeners == null)
            listeners = new LinkedList<>();
        listeners.add(l);
    }

    @Override
    /**
     * remove a specific change listener from listeners 
     */
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }
    /**
     * alerts all listeners to a fire state change event 
     */
    public void fireStateChanged() {
        if (listeners != null) {
            ChangeEvent event = new ChangeEvent(this);
            for (ChangeListener l : listeners)
                l.stateChanged(event);
        }
    }
}
