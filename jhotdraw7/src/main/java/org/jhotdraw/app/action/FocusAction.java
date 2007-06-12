/*
 * @(#)FocusAction.java  2.0  2006-05-05
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

import org.jhotdraw.util.*;

import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import org.jhotdraw.app.Project;
/**
 * Requests focus for a Frame.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2006-05-05 Reworked.
 * <br>1.0  2005-06-10 Created.
 */
public class FocusAction extends AbstractAction {
    public final static String ID = "focus";
    private Project project;
    
    /** Creates a new instance. */
    public FocusAction(Project project) {
        this.project = project;
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
        //setEnabled(false);
        setEnabled(project != null);
        
        project.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
                String name = evt.getPropertyName();
                if (name.equals("file")) {
                    putValue(Action.NAME,
                            (evt.getNewValue() == null) ?
                                labels.getString("unnamedFile") :
                                ((File) evt.getNewValue()).getName()
                                );
                }
            }
        });
    }
    
    public Object getValue(String key) {
        if (key == Action.NAME && project != null) {
            return getTitle();
        } else {
            return super.getValue(key);
        }
    }
    
    private String getTitle() {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        String title = labels.getString("unnamedFile");
        if (project != null) {
            File file = project.getFile();
            if (file == null) {
                title = labels.getString("unnamedFile");
            } else {
                title = file.getName();
            }
            if (project.hasUnsavedChanges()) {
                title += "*";
            }
            title = (labels.getFormatted("internalFrameTitle", title, project.getApplication().getName(), project.getMultipleOpenId()));
        }
        return title;
        
    }
    private JFrame getFrame() {
        return (JFrame) SwingUtilities.getWindowAncestor(
                project.getComponent()
                );
    }
    private Component getRootPaneContainer() {
        return SwingUtilities.getRootPane(
                project.getComponent()
                ).getParent();
    }
    
    public void actionPerformed(ActionEvent evt) {
        /*
        JFrame frame = getFrame();
        if (frame != null) {
            frame.setExtendedState(frame.getExtendedState() & ~Frame.ICONIFIED);
            frame.toFront();
            frame.requestFocus();
            JRootPane rp = SwingUtilities.getRootPane(project.getComponent());
            if (rp != null && (rp.getParent() instanceof JInternalFrame)) {
                ((JInternalFrame) rp.getParent()).toFront();
            }
            project.getComponent().requestFocus();
        } else {
            Toolkit.getDefaultToolkit().beep();
        }*/
        Component rpContainer = getRootPaneContainer();
        if (rpContainer instanceof Frame) {
            Frame frame = (Frame) rpContainer;
            frame.setExtendedState(frame.getExtendedState() & ~Frame.ICONIFIED);
            frame.toFront();
        } else if (rpContainer instanceof JInternalFrame) {
            JInternalFrame frame = (JInternalFrame) rpContainer;
            frame.toFront();
            try {
                frame.setSelected(true);
            } catch (PropertyVetoException e) {
                // Don't care.
            }
        }
        project.getComponent().requestFocusInWindow();
    }
}
