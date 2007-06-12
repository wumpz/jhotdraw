/*
 * @(#)ToggleToolBarAction.java  1.0  13. Februar 2006
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

package org.jhotdraw.app.action;

import org.jhotdraw.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.beans.*;
/**
 * ToggleToolBarAction.
 * 
 * 
 * @author Werner Randelshofer
 * @version 1.0 13. Februar 2006 Created.
 */
public class ToggleToolBarAction extends AbstractAction {
    private JToolBar toolBar;
    private PropertyChangeListener propertyHandler;
    
    /** Creates a new instance. */
    public ToggleToolBarAction(JToolBar toolBar, String label) {
        super(label);
        
        propertyHandler = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name.equals("visible")) {
                    putValue(Actions.SELECTED_KEY, evt.getNewValue());
                }
            }            
        };
        
        putValue(Actions.SELECTED_KEY, true);
        setToolBar(toolBar);
    }
    
    public void putValue(String key, Object newValue) {
        super.putValue(key, newValue);
        if (key == Actions.SELECTED_KEY) {
            if (toolBar != null) {
                toolBar.setVisible((Boolean) newValue);
            }
        }
    }
    
    public void setToolBar(JToolBar newValue) {
        if (toolBar != null) {
            toolBar.removePropertyChangeListener(propertyHandler);
        }
        
        toolBar = newValue;
 
        if (toolBar != null) {
            toolBar.addPropertyChangeListener(propertyHandler);
            putValue(Actions.SELECTED_KEY, toolBar.isVisible());
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        if (toolBar != null) {
            putValue(Actions.SELECTED_KEY, ! toolBar.isVisible());
        }
    }
}
