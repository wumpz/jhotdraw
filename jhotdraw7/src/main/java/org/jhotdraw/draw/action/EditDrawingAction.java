/*
 * @(#)EditDrawingAction.java  1.0  2007-12-18
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
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
import org.jhotdraw.draw.action.EditGridPanel;
import org.jhotdraw.util.*;
import org.jhotdraw.util.prefs.PreferencesUtil;

/**
 * EditDrawingAction.
 * <p>
 * XXX - We shouldn't have a dependency to the application framework
 * from within the drawing framework.
 *
 * @author Werner Randelshofer
 * @version 1.0 2007-12-18 Created.
 */
public class EditDrawingAction extends AbstractEditorAction {
    public final static String ID = "editDrawing";
    private JDialog dialog;
    private EditDrawingPanel settingsPanel;
    private PropertyChangeListener propertyChangeHandler;
    private Application app;
    
    /** Creates a new instance. */
    public EditDrawingAction(Application app, DrawingEditor editor) {
        super(editor);
        this.app = app;
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        labels.configureAction(this, ID);
    }
    
    public void actionPerformed(ActionEvent e) {
        getDialog().setVisible(true);
    }
    
   @Override protected void updateViewState() {
        if (getView() != null && settingsPanel != null) {
            settingsPanel.setDrawing(getView().getDrawing());
        }
    }
    
    protected Application getApplication() {
        return app;
    }
    
    protected JDialog getDialog() {
        if (dialog == null) {
            ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
            dialog = new JDialog();
            dialog.setTitle(labels.getString("drawingSettings"));
            dialog.setResizable(false);
            settingsPanel = new EditDrawingPanel();
            dialog.add(settingsPanel);
            dialog.pack();
            Preferences prefs = Preferences.userNodeForPackage(getClass());
            PreferencesUtil.installFramePrefsHandler(prefs, "drawingSettings", dialog);
            getApplication().addWindow(dialog, null);
        }
            settingsPanel.setDrawing(getView().getDrawing());
        return dialog;
    }
}
