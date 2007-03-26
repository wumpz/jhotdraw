/*
 * @(#)ClearRecentFilesAction.java  1.0  June 15, 2006
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

import org.jhotdraw.application.*;
import org.jhotdraw.util.*;
import java.awt.event.ActionEvent;
import java.beans.*;
/**
 * ClearRecentFilesAction.
 *
 * @author Werner Randelshofer.
 * @version 1.0 June 15, 2006 Created.
 */
public class ClearRecentFilesAction extends AbstractApplicationAction {
    public final static String ID = "clearRecentFiles";
    
    private PropertyChangeListener applicationListener;
    
    /** Creates a new instance. */
    public ClearRecentFilesAction() {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.application.Labels");
        labels.configureAction(this, "clearMenu");
        updateEnabled();
    }
    
    /**
     * Installs listeners on the application object.
     */
    @Override protected void installApplicationListeners(DocumentOrientedApplication app) {
        super.installApplicationListeners(app);
        if (applicationListener == null) {
            applicationListener = createApplicationListener();
        }
        WindowManager.getInstance().addPropertyChangeListener(applicationListener);
    }
    private PropertyChangeListener createApplicationListener() {
        return new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName() == "recentFiles") { // Strings get interned
                    updateEnabled();
                }
            }
        };
    }
    /**
     * Installs listeners on the application object.
     */
    @Override protected void uninstallApplicationListeners(DocumentOrientedApplication app) {
        super.uninstallApplicationListeners(app);
        WindowManager.getInstance().removePropertyChangeListener(applicationListener);
    }
    
    public void actionPerformed(ActionEvent e) {
        WindowManager.getInstance().clearRecentFiles();
    }
    
    private void updateEnabled() {
        setEnabled(WindowManager.getInstance().recentFiles().size() > 0);
        
    }
    
}
