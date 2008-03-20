/*
 * @(#)EditDrawingAction.java  1.0  2007-12-18
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
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
    private JFrame frame;
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
        getFrame().setVisible(true);
    }
    
   @Override protected void updateViewState() {
        if (getView() != null && settingsPanel != null) {
            settingsPanel.setDrawing(getView().getDrawing());
        }
    }
    
    protected Application getApplication() {
        return app;
    }
    
    protected JFrame getFrame() {
        if (frame == null) {
            ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
            frame = new JFrame();
            frame.setTitle(labels.getString("canvasSettings"));
            frame.setResizable(false);
            settingsPanel = new EditDrawingPanel();
            frame.add(settingsPanel);
            frame.pack();
            Preferences prefs = Preferences.userNodeForPackage(getClass());
            PreferencesUtil.installFramePrefsHandler(prefs, "canvasSettings", frame);
            getApplication().addWindow(frame, null);
        }
            settingsPanel.setDrawing(getView().getDrawing());
        return frame;
    }
}
