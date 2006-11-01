/*
 * @(#)OpenRecentAction.java  1.0  June 15, 2006
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
import org.jhotdraw.gui.*;
import org.jhotdraw.gui.event.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import java.io.*;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.Project;
/**
 * OpenRecentAction.
 *
 * @author Werner Randelshofer.
 * @version 1.0 June 15, 2006 Created.
 */
public class OpenRecentAction extends AbstractApplicationAction {
    public final static String ID = "openRecent";
    private File file;
    
    /** Creates a new instance. */
    public OpenRecentAction(Application app, File file) {
        super(app);
        this.file = file;
        putValue(Action.NAME, file.getName());
    }
    
    public void actionPerformed(ActionEvent evt) {
        final Application app = getApplication();
        if (app.isEnabled()) {
            app.setEnabled(false);
            // Search for an empty project
            Project emptyProject = app.getCurrentProject();
            if (emptyProject == null ||
                    emptyProject.getFile() != null ||
                    emptyProject.hasUnsavedChanges()) {
                emptyProject = null;
            }
            
            final Project p;
            if (emptyProject == null) {
                p = app.createProject();
                app.add(p);
                app.show(p);
            } else {
                p = emptyProject;
            }
            openFile(p);
        }
    }
    
    protected void openFile(final Project project) {
        final Application app = getApplication();
        app.setEnabled(true);
        
        // If there is another project with we set the multiple open
        // id of our project to max(multiple open id) + 1.
        int multipleOpenId = 1;
        for (Project aProject : app.projects()) {
            if (aProject != project &&
                    aProject.getFile() != null &&
                    aProject.getFile().equals(file)) {
                multipleOpenId = Math.max(multipleOpenId, aProject.getMultipleOpenId() + 1);
            }
        }
        project.setMultipleOpenId(multipleOpenId);
        
        // Open the file
        project.execute(new Worker() {
            public Object construct() {
                try {
                    project.read(file);
                    return null;
                } catch (Throwable e) {
                    return e;
                }
            }
            public void finished(Object value) {
                fileOpened(project, file, value);
            }
        });
    }
    protected void fileOpened(final Project project, File file, Object value) {
        final Application app = getApplication();
        if (value == null) {
            project.setFile(file);
            project.setEnabled(true);
            Frame w = (Frame) SwingUtilities.getWindowAncestor(project.getComponent());
            if (w != null) {
                w.setExtendedState(w.getExtendedState() & ~Frame.ICONIFIED);
                w.toFront();
            }
            project.getComponent().requestFocus();
            if (app != null) {
                app.setEnabled(true);
            }
        } else {
            if (value instanceof Throwable) {
                ((Throwable) value).printStackTrace();
            }
            JSheet.showMessageSheet(project.getComponent(),
                    "<html>"+UIManager.getString("OptionPane.css")+
                    "<b>Couldn't open the file \""+file+"\".</b><br>"+
                    value,
                    JOptionPane.ERROR_MESSAGE, new SheetListener() {
                public void optionSelected(SheetEvent evt) {
                    // app.dispose(project);
                }
            }
            );
        }
    }
}
