/*
 * @(#)AbstractDocumentViewAction.java  1.1  2007-03-22
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
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import org.jhotdraw.application.AbstractDocumentOrientedApplication;
import org.jhotdraw.application.DocumentOrientedApplication;
import org.jhotdraw.application.DocumentView;

/**
 * An Action that acts on on the current <code>DocumentView</code> of an
 * <code>DocumentOrientedApplication</code>.
 * If the current DocumentView object is disabled or is null, the
 * AbstractDocumentViewAction is disabled as well.
 * <p>
 * A property name can be specified. When the specified property 
 * changes or when the current documentView changes, method updateProperty
 * is invoked.
 * 
 * 
 * 
 * 
 * @author Werner Randelshofer
 * @version 1.0 October 9, 2005 Created.
 * @see org.jhotdraw.application.PrDocumentViewsee org.jhotdraw.application.ApDocumentOrientedApplication
 */
public abstract class AbstractDocumentViewAction extends AbstractApplicationAction {
    private String propertyName;
    
    private PropertyChangeListener applicationListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName() == 
                    AbstractDocumentOrientedApplication.PROP_CURRENT_VIEW) { // Strings get interned
                updateProject((DocumentView) evt.getOldValue(), (DocumentView) evt.getNewValue());
            }
        }
    };
    private PropertyChangeListener viewListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name == "enabled") { // Strings get interned
                updateEnabled((Boolean) evt.getOldValue(), (Boolean) evt.getNewValue());
            } else if (name == propertyName) {
                updateProperty();
            }
        }
    };
    
    /** Creates a new instance. */
    public AbstractDocumentViewAction() {
        this.enabled = true;
        DocumentOrientedApplication application = getApplication();
        if (application != null) {
            application.addPropertyChangeListener(applicationListener);
            updateProject(null, application.getCurrentView());
        }
    }
    
    /**
     * Updates the listeners of this action depending on the current documentView
     * of the application.
     */
    protected void updateProject(DocumentView oldValue, DocumentView newValue) {
        if (oldValue != null) {
            uninstallProjectListeners(oldValue);
        }
        if (newValue != null) {
            installProjectListeners(newValue);
        }
        firePropertyChange("documentView", oldValue, newValue);
        updateEnabled(oldValue != null && oldValue.isEnabled(),
                newValue != null && newValue.isEnabled());
        updateProperty();
    }
    
    /**
     * Sets the property name.
     */
    protected void setPropertyName(String name) {
        this.propertyName = name;
        if (name != null) {
            updateProperty();
        }
    }
    /**
     * Gets the property name.
     */
    protected String getPropertyName() {
        return propertyName;
    }
    
    /**
     * This method is invoked, when the property changed and when
     * the documentView changed.
     */
    protected void updateProperty() {
        
    }
    
    /**
     * Installs listeners on the documentView object.
     */
    protected void installProjectListeners(DocumentView p) {
        p.addPropertyChangeListener(viewListener);
    }
    /**
     * Installs listeners on the documentView object.
     */
    protected void uninstallProjectListeners(DocumentView p) {
        p.removePropertyChangeListener(viewListener);
    }
    
    /**
     * Updates the enabled state of this action depending on the new enabled
     * state of the documentView.
     */
    protected void updateEnabled(boolean oldValue, boolean newValue) {
       // System.out.println("AbstractDocumentViewAction updateEnabled"+oldValue+","+newValue);
        firePropertyChange("enabled", oldValue, newValue);
    }
    
    public DocumentView getCurrentView() {
        return getApplication().getCurrentView();
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
        return getCurrentView() != null && 
                getCurrentView().isEnabled() &&
                this.enabled;
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
        
        boolean projIsEnabled = getCurrentView() != null && getCurrentView().isEnabled();
        
        firePropertyChange("enabled",
                Boolean.valueOf(oldValue && projIsEnabled),
                Boolean.valueOf(newValue && projIsEnabled)
                );
        
    }
}
