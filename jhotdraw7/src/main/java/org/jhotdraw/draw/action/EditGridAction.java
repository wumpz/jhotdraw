/*
 * @(#)EditGridAction.java  2.0  2007-09-15
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
public class EditGridAction extends AbstractEditorAction {
    public final static String ID = "editGrid";
    private JDialog dialog;
    private GridSettingsPanel settingsPanel;
    private PropertyChangeListener propertyChangeHandler;
    private Application app;
    
    /** Creates a new instance. */
    public EditGridAction(Application app, DrawingEditor editor) {
        super(editor);
        this.app = app;
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.svg.Labels");
        labels.configureAction(this, ID);
    }
    
    public void actionPerformed(ActionEvent e) {
        getDialog().setVisible(true);
    }
    
    protected void updateProperty() {
        if (getView() != null && settingsPanel != null) {
            settingsPanel.setConstrainer((GridConstrainer) getView().getVisibleConstrainer());
        }
    }
    
    protected Application getApplication() {
        return app;
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
            settingsPanel.setConstrainer((GridConstrainer) getView().getVisibleConstrainer());
        return dialog;
    }
}
