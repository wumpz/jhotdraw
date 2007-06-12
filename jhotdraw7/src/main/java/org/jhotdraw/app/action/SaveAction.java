/*
 * @(#)SaveAction.java  1.2.1  2006-07-25
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import org.jhotdraw.application.*;
import org.jhotdraw.gui.Worker;
import org.jhotdraw.io.*;
import org.jhotdraw.util.*;
import org.jhotdraw.gui.*;
import org.jhotdraw.gui.event.*;

/**
 * SaveAction.
 *
 * @author  Werner Randelshofer
 * @version 1.2.1 2006-07-25 Add saved file to recent file list of application.
 * <br>1.2 2006-05-19 Make filename acceptable by ExtensionFileFilter.
 * <br>1.1 2006-02-23 Support multiple open id.
 * <br>1.0 28. September 2005 Created.
 */
public class SaveAction extends AbstractDocumentViewAction {
    public final static String ID = "File.save";
    private boolean saveAs;
    private Component oldFocusOwner;
    
    /** Creates a new instance. */
    public SaveAction() {
        this(false);
    }
    /** Creates a new instance. */
    public SaveAction(boolean saveAs) {
        this.saveAs = saveAs;
        initActionProperties(ID);
    }
    
    
    public void actionPerformed(ActionEvent evt) {
        final DocumentView documentView = getCurrentView();
        if (documentView.isEnabled()) {
            oldFocusOwner = SwingUtilities.getWindowAncestor(documentView.getComponent()).getFocusOwner();
            documentView.setEnabled(false);
            
            File saveToFile;
            if (!saveAs && documentView.getFile() != null) {
                saveToFile(documentView, documentView.getFile());
            } else {
                JFileChooser fileChooser = documentView.getSaveChooser();
                
                JSheet.showSaveSheet(fileChooser, documentView.getComponent(), new SheetListener() {
                    public void optionSelected(final SheetEvent evt) {
                        if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
                            final File file;
                            if (evt.getFileChooser().getFileFilter() instanceof ExtensionFileFilter) {
                                file = ((ExtensionFileFilter) evt.getFileChooser().getFileFilter()).
                                        makeAcceptable(evt.getFileChooser().getSelectedFile());
                            } else {
                                file = evt.getFileChooser().getSelectedFile();
                            }
                            saveToFile(documentView, file);
                        } else {
                            documentView.setEnabled(true);
                            if (oldFocusOwner != null) {
                                oldFocusOwner.requestFocus();
                            }
                        }
                    }
                });
            }
        }
    }
    
    protected void saveToFile(final DocumentView documentView, final File file) {
        documentView.execute(new Worker() {
            public Object construct() {
                try {
                    documentView.write(file);
                    return null;
                } catch (IOException e) {
                    return e;
                }
            }
            public void finished(Object value) {
                fileSaved(documentView, file, value);
            }
        });
    }
    /**
     * XXX - Change type of value to Throwable
     *
     * @param value is either null for success or a Throwable on failure.
     */
    protected void fileSaved(final DocumentView documentView, File file, Object value) {
        if (value == null) {
            documentView.setFile(file);
            documentView.setModified(false);
            getApplication().addRecentFile(file);
        } else {
            ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.application.Labels");
            JSheet.showMessageSheet(documentView.getComponent(),
                    "<html>"+UIManager.getString("OptionPane.css")+
                    labels.getFormatted("couldntSave", file, value),
                    JOptionPane.ERROR_MESSAGE
                    );
        }
        documentView.setEnabled(true);
        SwingUtilities.getWindowAncestor(documentView.getComponent()).toFront();
        if (oldFocusOwner != null) {
            oldFocusOwner.requestFocus();
        }
    }
}