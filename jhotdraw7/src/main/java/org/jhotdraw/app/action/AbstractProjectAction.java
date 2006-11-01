/*
 * @(#)AbstractProjectAction.java  1.0  October 9, 2005
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

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.Project;

/**
 * An Action that acts on on the current <code>Project</code> of an
 * <code>Application</code>.
 * If the current Project object is disabled or is null, the
 * AbstractProjectAction is disabled as well.
 * 
 * @author Werner Randelshofer
 * @version 1.0 October 9, 2005 Created.
 * @see org.jhotdraw.app.Project
 * @see org.jhotdraw.app.Application
 */
public abstract class AbstractProjectAction extends AbstractAction {
    private Application app;
    
    private PropertyChangeListener applicationListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName() == "currentProject") { // Strings get interned
                updateProject((Project) evt.getOldValue(), (Project) evt.getNewValue());
            }
        }
    };
    private PropertyChangeListener projectListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName() == "enabled") { // Strings get interned
                updateEnabled((Boolean) evt.getOldValue(), (Boolean) evt.getNewValue());
            }
        }
    };
    
    /** Creates a new instance. */
    public AbstractProjectAction(Application app) {
        this.app = app;
        if (app != null) {
            app.addPropertyChangeListener(applicationListener);
            updateProject(null, app.getCurrentProject());
        }
    }
    
    /**
     * Updates the project of this action depending on the current project
     * of the application.
     */
    protected void updateProject(Project oldValue, Project newValue) {
        if (oldValue != null) {
            uninstallProjectListeners(oldValue);
        }
        if (newValue != null) {
            installProjectListeners(newValue);
        }
        firePropertyChange("project", oldValue, newValue);
        updateEnabled(oldValue != null && oldValue.isEnabled(),
                newValue != null && newValue.isEnabled());
    }
    
    /**
     * Installs listeners on the project object.
     */
    protected void installProjectListeners(Project p) {
        p.addPropertyChangeListener(projectListener);
    }
    /**
     * Installs listeners on the project object.
     */
    protected void uninstallProjectListeners(Project p) {
        p.removePropertyChangeListener(projectListener);
    }
    
    /**
     * Updates the enabled state of this action depending on the new enabled
     * state of the project.
     */
    protected void updateEnabled(boolean oldValue, boolean newValue) {
        setEnabled(super.isEnabled());
        firePropertyChange("projectEnabled", oldValue, newValue);
    }
    
    public Application getApplication() {
        return app;
    }
    public Project getCurrentProject() {
        return app.getCurrentProject();
    }
    
    /**
     * Returns true if the action is enabled.
     * The enabled state of the action depends on the state that has been set
     * using setEnabled() and on the enabled state of the application.
     *
     * @return true if the action is enabled, false otherwise
     * @see Action#isEnabled
     */
    @Override public boolean isEnabled() {
        return getCurrentProject() != null && getCurrentProject().isEnabled() || super.isEnabled();
    }
    
    /**
     * Enables or disables the action. The enabled state of the action
     * depends on the value that is set here and on the enabled state of
     * the application.
     *
     * @param newValue  true to enable the action, false to
     *                  disable it
     * @see Action#setEnabled
     */
    @Override public void setEnabled(boolean newValue) {
        boolean oldValue = this.enabled;
        this.enabled = newValue;
        
        boolean projIsEnabled = getCurrentProject() != null && getCurrentProject().isEnabled();
        
        firePropertyChange("enabled",
                Boolean.valueOf(oldValue && projIsEnabled),
                Boolean.valueOf(newValue && projIsEnabled)
                );
        
    }
}
