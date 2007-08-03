/*
 * @(#)EditGridAction.java  1.0  July 31, 2007
 *
 * Copyright (c) 2007 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.draw.action;

import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.prefs.Preferences;
import javax.swing.*;
import org.jhotdraw.app.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.gui.GridSettingsPanel;
import org.jhotdraw.util.*;
import org.jhotdraw.util.prefs.PreferencesUtil;

/**
 * EditGridAction.
 * <p>
 * XXX - We shouldn't have a dependency to the application framework
 * from within the drawing framework.
 *
 * @author Werner Randelshofer
 * @version 1.0 July 31, 2007 Created.
 */
public class EditGridAction extends AbstractProjectAction {
    public final static String ID = "editGrid";
    private JDialog dialog;
    private GridSettingsPanel settingsPanel;
    private PropertyChangeListener propertyChangeHandler;
    
    /** Creates a new instance. */
    public EditGridAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.svg.Labels");
        labels.configureAction(this, ID);
    }
    
    public void actionPerformed(ActionEvent e) {
        getDialog().setVisible(true);
    }
    
    public GridProject getCurrentProject() {
        return (GridProject) super.getCurrentProject();
    }
    
    protected void updateProperty() {
        if (getCurrentProject() != null && settingsPanel != null) {
            settingsPanel.setConstrainer(getCurrentProject().getGridConstrainer());
        }
    }
    
    protected JDialog getDialog() {
        if (dialog == null) {
            ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.svg.Labels");
            dialog = new JDialog();
            dialog.setTitle(labels.getString("gridSettings"));
            dialog.setResizable(false);
            settingsPanel = new GridSettingsPanel();
            dialog.add(settingsPanel);
            dialog.pack();
            Preferences prefs = Preferences.userNodeForPackage(getClass());
            PreferencesUtil.installFramePrefsHandler(prefs, "gridSettings", dialog);
            getApplication().addWindow(dialog, null);
        }
            settingsPanel.setConstrainer(((GridProject) getCurrentProject()).getGridConstrainer());
        return dialog;
    }
}
