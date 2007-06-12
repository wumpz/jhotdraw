/*
 * @(#)AbstractApplicationAction.java  1.0  June 15, 2006
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

package org.jhotdraw.application.action;

import application.*;
import java.beans.*;
import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.application.*;
/**
 * An Action that acts on an <code>DocumentOrientedApplication</code> object.
 * If the DocumentOrientedApplication object is disabled, the AbstractApplicationAction is disabled
 * as well.
 *
 * @author Werner Randelshofer.
 * @version 1.0 June 15, 2006 Created.
 * @see org.jhotdraw.application.DocumentOrientedApplication
 */
public abstract class AbstractApplicationAction extends AbstractAction {
    private PropertyChangeListener applicationListener;
    /* The corresponding javax.swing.Action constants are only
     * defined in Mustang (1.6), see
     * http://download.java.net/jdk6/docs/api/javax/swing/Action.html
     */
    private static final String SELECTED_KEY = "SwingSelectedKey";
    private static final String DISPLAYED_MNEMONIC_INDEX_KEY = "SwingDisplayedMnemonicIndexKey";
    private static final String LARGE_ICON_KEY = "SwingLargeIconKey";
    
    
    /** Creates a new instance. */
    public AbstractApplicationAction() {
        installApplicationListeners(getApplication());
        updateApplicationEnabled();
    }
    
    /**
     * Installs listeners on the application object.
     */
    protected void installApplicationListeners(DocumentOrientedApplication application) {
        if (applicationListener == null) {
            applicationListener = createApplicationListener();
        }
        application.addPropertyChangeListener(applicationListener);
    }
    
    /**
     * Installs listeners on the application object.
     */
    protected void uninstallApplicationListeners(DocumentOrientedApplication application) {
        application.removePropertyChangeListener(applicationListener);
    }
    
    private PropertyChangeListener createApplicationListener() {
        return new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName() == "enabled") { // Strings get interned
                    updateApplicationEnabled();
                }
            }
        };
    }
    
    public DocumentOrientedApplication getApplication() {
        return (DocumentOrientedApplication) ApplicationContext.getInstance().getApplication();
    }
    
    public ResourceMap getResourceMap() {
        return ApplicationContext.getInstance().getResourceMap();
    }
    
    /**
     * Updates the enabled state of this action depending on the new enabled
     * state of the application.
     */
    protected void updateApplicationEnabled() {
        firePropertyChange("enabled",
                Boolean.valueOf(! isEnabled()),
                Boolean.valueOf(isEnabled())
                );
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
        return getApplication().isEnabled() && enabled;
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
        
        firePropertyChange("enabled",
                Boolean.valueOf(oldValue && getApplication().isEnabled()),
                Boolean.valueOf(newValue && getApplication().isEnabled())
                );
    }
    /* Init all of the javax.swing.Action properties for the @Action
     * named actionName.
     */
    public void initActionProperties(String baseName) {
        initActionProperties(getResourceMap(), baseName);
    }
    /* Init all of the javax.swing.Action properties for the @Action
     * named actionName.
     */
    public void initActionProperties(ResourceMap resourceMap, String baseName) {
        boolean iconOrNameSpecified = false;  // true if Action's icon/name properties set
        String typedName = null;
        
        // Action.text => Action.NAME,MNEMONIC_KEY,DISPLAYED_MNEMONIC_INDEX_KEY
        String text = resourceMap.getString(baseName + ".Action.text");
        if (text != null) {
            MyMnemonicText.configure(this, text);
            iconOrNameSpecified = true;
        }
        // Action.mnemonic => Action.MNEMONIC_KEY
        Integer mnemonic = resourceMap.getKeyCode(baseName + ".Action.mnemonic");
        if (mnemonic != null) {
            putValue(javax.swing.Action.MNEMONIC_KEY, mnemonic);
        }
        // Action.mnemonic => Action.DISPLAYED_MNEMONIC_INDEX_KEY
        Integer index = resourceMap.getKeyCode(baseName + ".Action.displayedMnemonicIndex");
        if (index != null) {
            putValue(DISPLAYED_MNEMONIC_INDEX_KEY, index);
        }
        // Action.accelerator => Action.ACCELERATOR_KEY
        KeyStroke key = resourceMap.getKeyStroke(baseName + ".Action.accelerator");
        if (key != null) {
            putValue(javax.swing.Action.ACCELERATOR_KEY, key);
        }
        // Action.icon => Action.SMALL_ICON,LARGE_ICON_KEY
        Icon icon = resourceMap.getIcon(baseName + ".Action.icon");
        if (icon != null) {
            putValue(javax.swing.Action.SMALL_ICON, icon);
            putValue(LARGE_ICON_KEY, icon);
            iconOrNameSpecified = true;
        }
        // Action.smallIcon => Action.SMALL_ICON
        Icon smallIcon = resourceMap.getIcon(baseName + ".Action.smallIcon");
        if (smallIcon != null) {
            putValue(javax.swing.Action.SMALL_ICON, smallIcon);
            iconOrNameSpecified = true;
        }
        // Action.largeIcon => Action.LARGE_ICON
        Icon largeIcon = resourceMap.getIcon(baseName + ".Action.largeIcon");
        if (largeIcon != null) {
            putValue(LARGE_ICON_KEY, largeIcon);
            iconOrNameSpecified = true;
        }
        // Action.shortDescription => Action.SHORT_DESCRIPTION
        putValue(javax.swing.Action.SHORT_DESCRIPTION,
                resourceMap.getString(baseName + ".Action.shortDescription"));
        // Action.longDescription => Action.LONG_DESCRIPTION
        putValue(javax.swing.Action.LONG_DESCRIPTION,
                resourceMap.getString(baseName + ".Action.longDescription"));
        // Action.command => Action.ACTION_COMMAND_KEY
        putValue(javax.swing.Action.ACTION_COMMAND_KEY,
                resourceMap.getString(baseName + ".Action.command"));
    }
    public ResourceMap getFrameworkResourceMap() {
        return ApplicationContext.getInstance().getResourceMap(AbstractDocumentOrientedApplication.class);
    }
}
