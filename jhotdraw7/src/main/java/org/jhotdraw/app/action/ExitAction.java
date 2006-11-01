/*
 * @(#)ExitAction.java  1.0  04 January 2005
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
import java.util.*;
import javax.swing.*;
import java.io.*;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.Project;
/**
 * Exits the application after letting the user save or close unsaved projects.
 *
 * @author  Werner Randelshofer
 * @version 1.0  04 January 2005  Created.
 */
public class ExitAction extends AbstractApplicationAction {
    public final static String ID = "exit";
    private Component oldFocusOwner;
    private Project unsavedProject;
    
    /** Creates a new instance. */
    public ExitAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }
    
    public void actionPerformed(ActionEvent evt) {
        final Application app = getApplication();
        if (app.isEnabled()) {
            app.setEnabled(false);
            int unsavedProjectsCount = 0;
            Project documentToBeReviewed = null;
            for (Project p : app.projects()) {
                if (p.hasUnsavedChanges()) {
                    if (p.isEnabled()) {
                        documentToBeReviewed = p;
                    }
                    unsavedProjectsCount++;
                }
            }
            if (unsavedProjectsCount > 0 && documentToBeReviewed == null) {
                // Silently abort, if no project can be reviewed.
                app.setEnabled(true);
                return;
            }
            
            switch (unsavedProjectsCount) {
                case 0 : {
                    doExit();
                    break;
                }
                case 1 : {
                    unsavedProject = documentToBeReviewed;
                    oldFocusOwner = SwingUtilities.getWindowAncestor(unsavedProject.getComponent()).getFocusOwner();
                    unsavedProject.setEnabled(false);
                    JOptionPane pane = new JOptionPane(
                            "<html>"+UIManager.getString("OptionPane.css")+
                            "<b>Do you want to save changes to this document "+
                            "before exiting?</b><p>"+
                            "If you don't save, your changes will be lost.",
                            JOptionPane.WARNING_MESSAGE
                            );
                    Object[] options = { "Save", "Cancel", "Don't Save" };
                    pane.setOptions(options);
                    pane.setInitialValue(options[0]);
                    pane.putClientProperty("Quaqua.OptionPane.destructiveOption", new Integer(2));
                    JSheet.showSheet(pane, unsavedProject.getComponent(), new SheetListener() {
                        public void optionSelected(SheetEvent evt) {
                            Object value = evt.getValue();
                            if (value == null || value.equals("Cancel")) {
                                unsavedProject.setEnabled(true);
                                app.setEnabled(true);
                            } else if (value.equals("Don't Save")) {
                                doExit();
                                unsavedProject.setEnabled(true);
                            } else if (value.equals("Save")) {
                                saveChanges();
                            }
                        }
                    });
                    
                    break;
                }
                default : {
                    JOptionPane pane = new JOptionPane(
                            "<html>"+UIManager.get("OptionPane.css")+
                            "<b>You have "+unsavedProjectsCount+" documents with unsaved changes. "+
                            "Do you want to "+
                            "review these changes before quitting?</b><p>"+
                            "If you don't review your documents, "+
                            "all your changes will be lost.",
                            JOptionPane.QUESTION_MESSAGE
                            );
                    Object[] options = {
                        "Review Changes", "Cancel", "Discard Changes"
                    };
                    pane.setOptions(options);
                    pane.setInitialValue(options[0]);
                    pane.putClientProperty(
                            "Quaqua.OptionPane.destructiveOption", new Integer(2)
                            );
                    JDialog dialog = pane.createDialog(app.getComponent(), null);
                    dialog.setVisible(true);
                    Object value = pane.getValue();
                    if (value == null || value.equals("Cancel")) {
                        app.setEnabled(true);
                    } else if (value.equals("Discard Changes")) {
                        doExit();
                        app.setEnabled(true);
                    } else if (value.equals("Review Changes")) {
                        unsavedProject = documentToBeReviewed;
                        reviewChanges();
                    }
                }
            }
        }
    }
    
    protected void saveChanges() {
        if (unsavedProject.getFile() == null) {
            JFileChooser fileChooser = unsavedProject.getSaveChooser();
            //int option = fileChooser.showSaveDialog(this);
            JSheet.showSaveSheet(fileChooser, unsavedProject.getComponent(), new SheetListener() {
                public void optionSelected(final SheetEvent evt) {
                    if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
                        final File file = evt.getFileChooser().getSelectedFile();
                        saveToFile(file);
                    } else {
                        unsavedProject.setEnabled(true);
                        if (oldFocusOwner != null) {
                            oldFocusOwner.requestFocus();
                        }
                        getApplication().setEnabled(true);
                    }
                }
            });
        } else {
            saveToFile(unsavedProject.getFile());
        }
    }
    
    protected void reviewChanges() {
        if (unsavedProject.isEnabled()) {
            oldFocusOwner = SwingUtilities.getWindowAncestor(unsavedProject.getComponent()).getFocusOwner();
            unsavedProject.setEnabled(false);
            JOptionPane pane = new JOptionPane(
                    "<html>"+UIManager.getString("OptionPane.css")+
                    "<b>Do you want to save changes to this document "+
                    "before exiting?</b><p>"+
                    "If you don't save, your changes will be lost.",
                    JOptionPane.WARNING_MESSAGE
                    );
            Object[] options = { "Save", "Cancel", "Don't Save" };
            pane.setOptions(options);
            pane.setInitialValue(options[0]);
            pane.putClientProperty("Quaqua.OptionPane.destructiveOption", new Integer(2));
            JSheet.showSheet(pane, unsavedProject.getComponent(), new SheetListener() {
                public void optionSelected(SheetEvent evt) {
                    Object value = evt.getValue();
                    if (value == null || value.equals("Cancel")) {
                        unsavedProject.setEnabled(true);
                        getApplication().setEnabled(true);
                    } else if (value.equals("Don't Save")) {
                        getApplication().dispose(unsavedProject);
                        reviewNext();
                    } else if (value.equals("Save")) {
                        saveChangesAndReviewNext();
                    }
                }
            });
        } else {
            getApplication().setEnabled(true);
            System.out.println("review silently aborted");
        }
    }
    
    
    
    protected void saveChangesAndReviewNext() {
        if (unsavedProject.getFile() == null) {
            JFileChooser fileChooser = unsavedProject.getSaveChooser();
            //int option = fileChooser.showSaveDialog(this);
            JSheet.showSaveSheet(fileChooser, unsavedProject.getComponent(), new SheetListener() {
                public void optionSelected(final SheetEvent evt) {
                    if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
                        final File file = evt.getFileChooser().getSelectedFile();
                        saveToFileAndReviewNext(file);
                    } else {
                        unsavedProject.setEnabled(true);
                        if (oldFocusOwner != null) {
                            oldFocusOwner.requestFocus();
                        }
                        getApplication().setEnabled(true);
                    }
                }
            });
        } else {
            saveToFileAndReviewNext(unsavedProject.getFile());
        }
    }
    
    protected void reviewNext() {
        int unsavedProjectsCount = 0;
        Project documentToBeReviewed = null;
        for (Project p : getApplication().projects()) {
            if (p.hasUnsavedChanges()) {
                if (p.isEnabled()) {
                    documentToBeReviewed = p;
                }
                unsavedProjectsCount++;
            }
        }
        if (unsavedProjectsCount == 0) {
            doExit();
        } else if (documentToBeReviewed != null) {
            unsavedProject = documentToBeReviewed;
            reviewChanges();
        } else {
            getApplication().setEnabled(true);
            //System.out.println("exit silently aborted");
        }
    }
    
    protected void saveToFile(final File file) {
        unsavedProject.execute(new Worker() {
            public Object construct() {
                try {
                    unsavedProject.write(file);
                    return null;
                } catch (IOException e) {
                    return e;
                }
            }
            public void finished(Object value) {
                fileSaved(unsavedProject, file, value);
            }
        });
    }
    protected void saveToFileAndReviewNext(final File file) {
        unsavedProject.execute(new Worker() {
            public Object construct() {
                try {
                    unsavedProject.write(file);
                    return null;
                } catch (IOException e) {
                    return e;
                }
            }
            public void finished(Object value) {
                fileSavedAndReviewNext(unsavedProject, file, value);
            }
        });
    }
    
    protected void fileSaved(Project unsavedProject, File file, Object value) {
        if (value == null) {
            unsavedProject.setFile(file);
            doExit();
        } else {
            JSheet.showMessageSheet(unsavedProject.getComponent(),
                    "<html>"+UIManager.getString("OptionPane.css")+
                    "<b>Couldn't save to the file \""+file+"\".<p>"+
                    "Reason: "+value,
                    JOptionPane.ERROR_MESSAGE
                    );
        }
        unsavedProject.setEnabled(true);
        if (oldFocusOwner != null) {
            oldFocusOwner.requestFocus();
        }
        getApplication().setEnabled(true);
    }
    protected void fileSavedAndReviewNext(Project unsavedProject, File file, Object value) {
        if (value == null) {
            unsavedProject.setFile(file);
            getApplication().dispose(unsavedProject);
            reviewNext();
            return;
        } else {
            JSheet.showMessageSheet(unsavedProject.getComponent(),
                    "<html>"+UIManager.getString("OptionPane.css")+
                    "<b>Couldn't save to the file \""+file+"\".<p>"+
                    "Reason: "+value,
                    JOptionPane.ERROR_MESSAGE
                    );
        }
        unsavedProject.setEnabled(true);
        if (oldFocusOwner != null) {
            oldFocusOwner.requestFocus();
        }
        getApplication().setEnabled(true);
    }
    
    protected void doExit() {
        getApplication().stop();
        System.exit(0);
    }
}
