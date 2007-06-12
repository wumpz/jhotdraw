/*
 * @(#)RedoAction.java  2.0  2006-06-15
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.application.action;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.beans.*;
import java.util.*;
import org.jhotdraw.util.*;
import org.jhotdraw.application.DocumentOrientedApplication;
import org.jhotdraw.application.DocumentView;
/**
 * Redoes the last user action.
 * In order to work, this action requires that the DocumentView returns a documentView
 * specific undo action when invoking getAction("redo") on the DocumentView.
 * 
 * @author Werner Randelshofer
 * @version 2.0 2006-06-15 Reworked.
 * <br>1.0 October 9, 2005 Created.
 */
public class RedoAction extends AbstractDocumentViewAction {
    public final static String ID = "Edit.redo";
    
    private PropertyChangeListener redoActionPropertyListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name == AbstractAction.NAME) {
                putValue(AbstractAction.NAME, evt.getNewValue());
            } else if (name == "enabled") {
                updateEnabledState();
            }
        }
    };
    
    /** Creates a new instance. */
    public RedoAction() {
        initActionProperties(ID);
    }
    
    protected void updateEnabledState() {
        boolean isEnabled = false;
        Action realRedoAction = getRealRedoAction();
        if (realRedoAction != null) {
            isEnabled = realRedoAction.isEnabled();
        }
        setEnabled(isEnabled);
    }
    
    @Override protected void updateProject(DocumentView oldValue, DocumentView newValue) {
        super.updateProject(oldValue, newValue);
        if (newValue != null && newValue.getAction("redo") !=  null) {
            putValue(AbstractAction.NAME, newValue.getAction("redo").
                    getValue(AbstractAction.NAME));
            updateEnabledState();
        }
    }
    /**
     * Installs listeners on the documentView object.
     */
    @Override protected void installProjectListeners(DocumentView p) {
        super.installProjectListeners(p);
        if (p.getAction("redo") != null) {
        p.getAction("redo").addPropertyChangeListener(redoActionPropertyListener);
        }
    }
    /**
     * Installs listeners on the documentView object.
     */
    @Override protected void uninstallProjectListeners(DocumentView p) {
        super.uninstallProjectListeners(p);
        if (p.getAction("redo") != null) {
        p.getAction("redo").removePropertyChangeListener(redoActionPropertyListener);
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        Action realRedoAction = getRealRedoAction();
        if (realRedoAction != null) {
            realRedoAction.actionPerformed(e);
        }
    }
    
    private Action getRealRedoAction() {
        return (getCurrentView() == null) ? null : getCurrentView().getAction("redo");
    }
    
}
