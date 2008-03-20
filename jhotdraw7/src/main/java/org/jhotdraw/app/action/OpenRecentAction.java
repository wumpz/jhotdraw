/*
 * @(#)OpenRecentAction.java  1.1  2008-03-19
 *
 * Copyright (c) 1996-2008 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.app.action;

import org.jhotdraw.gui.Worker;
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
 * @version 1.1 2008-03-19 Check whether file exists before attempting to
 * open it. 
 * <br>1.0 June 15, 2006 Created.
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
            Project emptyProject = app.getActiveProject();
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
        project.setEnabled(false);

        // Open the file
        project.execute(new Worker() {

            public Object construct() {
                try {
                    if (file.exists()) {
                        project.read(file);
                        return null;
                    } else {
                        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
                        return new IOException(labels.getFormatted("errorFileDoesNotExist", file.getName()));
                    }
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
        if (value == null) {
            project.setFile(file);
            Frame w = (Frame) SwingUtilities.getWindowAncestor(project.getComponent());
            if (w != null) {
                w.setExtendedState(w.getExtendedState() & ~Frame.ICONIFIED);
                w.toFront();
            }
            project.setEnabled(true);
            project.getComponent().requestFocus();
        } else {
            String message = null;
            if (value instanceof Throwable) {
                ((Throwable) value).printStackTrace();
                message = ((Throwable) value).getMessage();
                if (message == null) {
                    message = value.toString();
                }
            }
            ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
            JSheet.showMessageSheet(project.getComponent(),
                    "<html>" + UIManager.getString("OptionPane.css") +
                    "<b>" + labels.getFormatted("couldntOpen", file.getName()) + "</b><br>" +
                    (message == null ? "" : message),
                    JOptionPane.ERROR_MESSAGE, new SheetListener() {

                public void optionSelected(SheetEvent evt) {
                    project.setEnabled(true);
                }
            });
        }
    }
}
