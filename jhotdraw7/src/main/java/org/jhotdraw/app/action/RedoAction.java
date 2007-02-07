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

package org.jhotdraw.app.action;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.beans.*;
import java.util.*;
import org.jhotdraw.util.*;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.Project;
/**
 * Redoes the last user action.
 * In order to work, this action requires that the Project returns a project
 * specific undo action when invoking getAction("redo") on the Project.
 *
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-06-15 Reworked.
 * <br>1.0 October 9, 2005 Created.
 */
public class RedoAction extends AbstractProjectAction {
    public final static String ID = "redo";
    private ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
    
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
    public RedoAction(Application app) {
        super(app);
        labels.configureAction(this, ID);
    }
    
    protected void updateEnabledState() {
        boolean isEnabled = false;
        Action realRedoAction = getRealRedoAction();
        if (realRedoAction != null) {
            isEnabled = realRedoAction.isEnabled();
        }
        setEnabled(isEnabled);
    }
    
    @Override protected void updateProject(Project oldValue, Project newValue) {
        super.updateProject(oldValue, newValue);
        if (newValue != null && newValue.getAction("redo") !=  null) {
            putValue(AbstractAction.NAME, newValue.getAction("redo").
                    getValue(AbstractAction.NAME));
            updateEnabledState();
        }
    }
    /**
     * Installs listeners on the project object.
     */
    @Override protected void installProjectListeners(Project p) {
        super.installProjectListeners(p);
        if (p.getAction("redo") != null) {
        p.getAction("redo").addPropertyChangeListener(redoActionPropertyListener);
        }
    }
    /**
     * Installs listeners on the project object.
     */
    @Override protected void uninstallProjectListeners(Project p) {
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
        return (getCurrentProject() == null) ? null : getCurrentProject().getAction("redo");
    }
    
}
