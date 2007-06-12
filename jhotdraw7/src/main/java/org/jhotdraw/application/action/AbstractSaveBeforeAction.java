/*
 * @(#)AbstractSaveBeforeAction.java  2.0  2006-06-15
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.application.action;

import application.ResourceMap;
import org.jhotdraw.gui.Worker;
import org.jhotdraw.io.*;
import org.jhotdraw.gui.*;
import org.jhotdraw.gui.event.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import org.jhotdraw.application.DocumentOrientedApplication;
import org.jhotdraw.application.DocumentView;
/**
 * Base class for actions that can only be safely performed when the documentView
 * has no unsaved changes.
 * <p>
 * If the documentView has no unsaved changes, method doIt is invoked immediately.
 * If unsaved changes are present, a dialog is shown asking whether the user
 * wants to discard the changes, cancel or save the changes before doing it.
 * If the user chooses to discard the chanegs, toIt is invoked immediately.
 * If the user chooses to cancel, the action is aborted.
 * If the user chooses to save the changes, the documentView is saved, and doIt
 * is only invoked after the documentView was successfully saved.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2006-06-15 Reworked. 
 * <br>1.2 2006-05-19 Make filename acceptable by ExtensionFileFilter.
 * <br>1.1 2006-05-03 Localized messages.
 * <br>1.0 27. September 2005 Created.
 */
public abstract class AbstractSaveBeforeAction extends AbstractDocumentViewAction {
    private Component oldFocusOwner;
    
    /** Creates a new instance. */
    public AbstractSaveBeforeAction() {
    }
    
    public void actionPerformed(ActionEvent evt) {
       final DocumentView p = getCurrentView();
        if (p.isEnabled()) {
           final ResourceMap labels = getFrameworkResourceMap();
            Window wAncestor = SwingUtilities.getWindowAncestor(p.getComponent());
            oldFocusOwner = (wAncestor == null) ? null : wAncestor.getFocusOwner();
            p.setEnabled(false);
            if (p.isModified()) {
                JOptionPane pane = new JOptionPane(
                        "<html>"+UIManager.getString("OptionPane.css")+
                        labels.getString("File.saveBefore.OptionPane.message"),
                        JOptionPane.WARNING_MESSAGE
                        );
                Object[] options = { labels.getString("File.save.Button.text"), 
                labels.getString("OptionPane.cancel.Button.text"), 
                labels.getString("File.dontSave.Button.text") };
                pane.setOptions(options);
                pane.setInitialValue(options[0]);
                pane.putClientProperty("Quaqua.OptionPane.destructiveOption", new Integer(2));
                JSheet.showSheet(pane, p.getComponent(), new SheetListener() {
                    public void optionSelected(SheetEvent evt) {
                        Object value = evt.getValue();
                        if (value == null || value.equals(labels.getString("OptionPane.cancel.Button.text"))) {
                            p.setEnabled(true);
                        } else if (value.equals(labels.getString("File.dontSave.Button.text"))) {
                            doIt(p);
                            p.setEnabled(true);
                        } else if (value.equals(labels.getString("File.save.Button.text"))) {
                            saveChanges(p);
                        }
                    }
                });
                
            } else {
                doIt(p);
                p.setEnabled(true);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            }
        }
    }
    
    protected void saveChanges(final DocumentView p) {
        if (p.getFile() == null) {
            JFileChooser fileChooser = p.getSaveChooser();
            //int option = fileChooser.showSaveDialog(this);
            JSheet.showSaveSheet(fileChooser, p.getComponent(), new SheetListener() {
                public void optionSelected(final SheetEvent evt) {
                    if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
                        final File file;
                        if (evt.getFileChooser().getFileFilter() instanceof ExtensionFileFilter) {
                            file = ((ExtensionFileFilter) evt.getFileChooser().getFileFilter()).
                                    makeAcceptable(evt.getFileChooser().getSelectedFile());
                        } else {
                            file = evt.getFileChooser().getSelectedFile();
                        }
                        saveToFile(p, file);
                    } else {
                        p.setEnabled(true);
                        if (oldFocusOwner != null) {
                            oldFocusOwner.requestFocus();
                        }
                    }
                }
            });
        } else {
            saveToFile(p, p.getFile());
        }
    }
    
    protected void saveToFile(final DocumentView p, final File file) {
        p.execute(new Worker() {
            public Object construct() {
                try {
                    p.write(file);
                    return null;
                } catch (IOException e) {
                    return e;
                }
            }
            public void finished(Object value) {
                fileSaved(p, file, value);
            }
        });
    }
    
    protected void fileSaved(DocumentView p, File file, Object value) {
        if (value == null) {
            p.setFile(file);
            p.setModified(false);
            doIt(p);
        } else {
            ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.application.Labels");
            JSheet.showMessageSheet(p.getComponent(),
                    "<html>"+UIManager.getString("OptionPane.css")+
                    labels.getFormatted("couldntSave", file, value),
                    JOptionPane.ERROR_MESSAGE
                    );
        }
        p.setEnabled(true);
        if (oldFocusOwner != null) {
            oldFocusOwner.requestFocus();
        }
    }
    
    protected abstract void doIt(DocumentView p);
}
