/*
 * @(#)ExportAction.java  1.0  2006-04-07
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
import org.jhotdraw.app.Application;
import org.jhotdraw.app.Project;

/**
 * ExportAction.
 * <p>
 * This action requires that the project has the following additional methods:
 * <pre>
 * public JFileChooser getExportChooser();
 *
 * // By convention this method is never invoked on the AWT Event Dispatcher Thread.
 * public void export(File f, javax.swing.filechooser.FileFilter filter, Component accessory) throws IOException;
 * </pre>
 *
 * @author Werner Randelshofer
 * @version 1.0 2006-04-07 Created.
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
    
    private JFileChooser getExportChooser(Project project) {
        try {
            return (JFileChooser) Methods.invoke(project,"getExportChooser");
        } catch (Throwable e) {
            InternalError error = new InternalError("Project does not support exporting");
            error.initCause(e);
            throw error;
        }
    }
    
    
    public void actionPerformed(ActionEvent evt) {
        final Project project = getCurrentProject();
        if (project.isEnabled()) {
            ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
            
            oldFocusOwner = SwingUtilities.getWindowAncestor(project.getComponent()).getFocusOwner();
            project.setEnabled(false);
            
            File saveToFile;
            JFileChooser fileChooser = getExportChooser(project);
            
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
    
    protected void exportToFile(final Project project, final File file,
            final javax.swing.filechooser.FileFilter filter,
            final Component accessory) {
        project.execute(new Worker() {
            public Object construct() {
                try {
                    Methods.invoke(project, "export",
                            new Class[] {
                        File.class,
                        javax.swing.filechooser.FileFilter.class,
                        Component.class
                    },
                            file, filter, accessory
                            );
                    return null;
                } catch (InternalError e) {
                    return (e.getCause() != null) ? e.getCause() : e;
                } catch (Throwable e) {
                    return e;
                }
            }
            public void finished(Object value) {
                fileExported(project, file, value);
            }
        });
    }
    protected void fileExported(Project project, File file, Object value) {
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