/*
 * @(#)ExportAction.java  2.0  2007-01-02
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

import org.jhotdraw.gui.*;
import org.jhotdraw.gui.event.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import org.jhotdraw.app.*;

/**
 * Presents a file chooser to the user and then exports the Project to the
 * chosen file.
 * <p>
 * This action requires that the project implements the ExportableProject interface.
 *
 * @author Werner Randelshofer
 * @version 2.0 2007-01-02 Revised to support an interface rather than relying
 * on Reflection. 
 * <br>1.0 2006-04-07 Created.
 */
public class ExportAction extends AbstractProjectAction {
    public final static String ID = "export";
    private Component oldFocusOwner;
    
    /** Creates a new instance. */
    public ExportAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }
    
    
    public void actionPerformed(ActionEvent evt) {
        final ExportableProject project = (ExportableProject) getCurrentProject();
        if (project.isEnabled()) {
            ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
            
            oldFocusOwner = SwingUtilities.getWindowAncestor(project.getComponent()).getFocusOwner();
            project.setEnabled(false);
            
            File saveToFile;
            JFileChooser fileChooser = project.getExportChooser();
            
            JSheet.showSheet(fileChooser, project.getComponent(), labels.getString("filechooser.export"), new SheetListener() {
                public void optionSelected(final SheetEvent evt) {
                    if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
                        final File file = evt.getFileChooser().getSelectedFile();
                        exportToFile(project, file, evt.getFileChooser().getFileFilter(), evt.getFileChooser().getAccessory()
                                );
                    } else {
                        project.setEnabled(true);
                        if (oldFocusOwner != null) {
                            oldFocusOwner.requestFocus();
                        }
                    }
                }
            });
        }
    }
    
    protected void exportToFile(final ExportableProject project, final File file,
            final javax.swing.filechooser.FileFilter filter,
            final Component accessory) {
        project.execute(new Worker() {
            public Object construct() {
                try {
                    project.export(file, filter, accessory);
                    return null;
                } catch (Throwable e) {
                    return e;
                }
            }
            public void finished(Object value) {
                fileExported(project, file, value);
            }
        });
    }
    protected void fileExported(ExportableProject project, File file, Object value) {
        if (value == null) {
            /*
            project.setFile(file);
            project.markChangesAsSaved();
            int multiOpenId = 1;
            for (Project p : project.getApplication().documents()) {
                if (p != project && p.getFile() != null && p.getFile().equals(file)) {
                    multiOpenId = Math.max(multiOpenId, p.getMultipleOpenId() + 1);
                }
            }
            project.setMultipleOpenId(multiOpenId);
             */
        } else {
            System.out.flush();
            ((Throwable) value).printStackTrace();
            // FIXME localize this error messsage
            JSheet.showMessageSheet(project.getComponent(),
                    "<html>"+UIManager.getString("OptionPane.css")+
                    "<b>Couldn't export to the file \""+file+"\".<p>"+
                    "Reason: "+value,
                    JOptionPane.ERROR_MESSAGE
                    );
        }
        project.setEnabled(true);
        SwingUtilities.getWindowAncestor(project.getComponent()).toFront();
        if (oldFocusOwner != null) {
            oldFocusOwner.requestFocus();
        }
    }
}