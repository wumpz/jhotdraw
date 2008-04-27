/**
 * @(#)AbstractColorSlidersModel.java  1.0  Apr 25, 2008
 *
 * Copyright (c) 2008 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package org.jhotdraw.color;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.jhotdraw.beans.*;

/**
 * AbstractColorSlidersModel.
 *
 * @author Werner Randelshofer
 * @version 1.0 Apr 25, 2008 Created.
 */
public abstract class AbstractColorSlidersModel extends AbstractBean implements ColorSliderModel {

    /**
     * ChangeListener's listening to changes in this AbstractColorSystem.
     */
    protected LinkedList<ChangeListener> listeners;

    public void addChangeListener(ChangeListener l) {
        if (listeners == null) {
            listeners = new LinkedList<ChangeListener>();
        }
        listeners.add(l);
    }

    public void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }

    public void fireStateChanged() {
        if (listeners != null) {
            ChangeEvent event = new ChangeEvent(this);
            for (ChangeListener l : listeners) {
                l.stateChanged(event);
            }
        }
    }
}
