/*
 * @(#)LoadAction.java  1.0  2005-10-16
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
import javax.swing.*;
import java.io.*;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.Project;

/**
 * Loads a file into the current project.
 *
 * @author  Werner Randelshofer
 * @version 1.0  2005-10-16  Created.
 */
public class LoadAction extends SaveBeforeAction {
    public final static String ID = "load";
    
    /** Creates a new instance. */
    public LoadAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, "open");
    }
    
    public void doIt(Project project) {
        JFileChooser fileChooser = project.getOpenChooser();
        if (fileChooser.showOpenDialog(project.getComponent()) == JFileChooser.APPROVE_OPTION) {
            openFile(project, fileChooser);
        } else {
            project.setEnabled(true);
        }
    }
    
    protected void openFile(final Project project, JFileChooser fileChooser) {
        final File file = fileChooser.getSelectedFile();
        
        // Open the file
        project.execute(new Worker() {
            public Object construct() {
                try {
                    project.read(file);
                    return null;
                } catch (IOException e) {
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
            project.setEnabled(true);
                getApplication().addRecentFile(file);
        } else {
            JSheet.showMessageSheet(project.getComponent(),
                    "<html>"+UIManager.getString("OptionPane.css")+
                    "<b>Couldn't open the file \""+file+"\".</b><br>"+
                    value,
                    JOptionPane.ERROR_MESSAGE, new SheetListener() {
                public void optionSelected(SheetEvent evt) {
                    project.clear();
                    project.setEnabled(true);
                }
            }
            );
        }
    }
}