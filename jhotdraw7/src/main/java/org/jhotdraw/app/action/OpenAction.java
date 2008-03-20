/*
 * @(#)OpenAction.java  2.1  2008-03-19
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
 * Opens a file in new project, or in the current project, if it is empty.
 *
 * @author  Werner Randelshofer
 * @version 2.1 2008-03-19 Check whether file exists before opening it. 
 * <br>2.0.2 2008-02-23 Project and application was not enabled after
 * unsuccessful file open. 
 * <br>2.0.1 2006-05-18 Print stack trace added.
 * <br>2.0 2006-02-16 Support for preferences added.
 * <br>1.0.1 2005-07-14 Make project explicitly visible after creating it.
 * <br>1.0  04 January 2005  Created.
 */
public class OpenAction extends AbstractApplicationAction {

    public final static String ID = "open";

    /** Creates a new instance. */
    public OpenAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
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
            /*
            for (Project aProject : app.projects()) {
            if (aProject.getFile() == null &&
            ! aProject.hasUnsavedChanges()) {
            emptyProject = aProject;
            break;
            }
            }*/
            }

            final Project p;
            boolean removeMe;
            if (emptyProject == null) {
                p = app.createProject();
                app.add(p);
                removeMe = true;
            } else {
                p = emptyProject;
                removeMe = false;
            }
            JFileChooser fileChooser = p.getOpenChooser();
            if (fileChooser.showOpenDialog(app.getComponent()) == JFileChooser.APPROVE_OPTION) {
                app.show(p);
                openFile(fileChooser, p);
            } else {
                if (removeMe) {
                    app.remove(p);
                }
                app.setEnabled(true);
            }
        }
    }

    protected void openFile(JFileChooser fileChooser, final Project project) {
        final Application app = getApplication();
        final File file = fileChooser.getSelectedFile();
        app.setEnabled(true);
        project.setEnabled(false);

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
            app.addRecentFile(file);
            app.setEnabled(true);
        } else {
            project.setEnabled(true);
            app.setEnabled(true);
            String message;
            if ((value instanceof Throwable) && ((Throwable) value).getMessage() != null) {
                message = ((Throwable) value).getMessage();
            } else {
                message = value.toString();
            }
            ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
            JSheet.showMessageSheet(project.getComponent(),
                    "<html>" + UIManager.getString("OptionPane.css") +
                    "<b>" + labels.getFormatted("couldntOpen", file.getName()) + "</b><br>" +
                    ((message == null) ? "" : message),
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
