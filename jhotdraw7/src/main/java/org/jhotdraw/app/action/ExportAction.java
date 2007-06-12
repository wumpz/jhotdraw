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

package org.jhotdraw.application.action;

import org.jhotdraw.gui.*;
import org.jhotdraw.gui.event.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import org.jhotdraw.application.*;

/**
 * Presents a file chooser to the user and then exports the view to the
 * chosen file.
 * <p>
 * This action requires that the documentView implements the ExportableDocumentView interface.
 * 
 * @author Werner Randelshofer
 * @version 2.0 2007-01-02 Revised to support an interface rather than relying
 * on Reflection. 
 * <br>1.0 2006-04-07 Created.
 */
public class ExportAction extends AbstractDocumentViewAction {
    public final static String ID = "File.export";
    private Component oldFocusOwner;
    
    /** Creates a new instance. */
    public ExportAction() {
        initActionProperties(ID);
    }
    
    
    public void actionPerformed(ActionEvent evt) {
        final ExportableDocumentView documentView = (ExportableDocumentView) getCurrentView();
        if (documentView.isEnabled()) {
            ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.application.Labels");
            
            oldFocusOwner = SwingUtilities.getWindowAncestor(documentView.getComponent()).getFocusOwner();
            documentView.setEnabled(false);
            
            File saveToFile;
            JFileChooser fileChooser = documentView.getExportChooser();
            
            JSheet.showSheet(fileChooser, documentView.getComponent(), labels.getString("filechooser.export"), new SheetListener() {
                public void optionSelected(final SheetEvent evt) {
                    if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
                        final File file = evt.getFileChooser().getSelectedFile();
                        exportToFile(documentView, file, evt.getFileChooser().getFileFilter(), evt.getFileChooser().getAccessory()
                                );
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
    
    protected void exportToFile(final ExportableDocumentView documentView, final File file,
            final javax.swing.filechooser.FileFilter filter,
            final Component accessory) {
        documentView.execute(new Worker() {
            public Object construct() {
                try {
                    documentView.export(file, filter, accessory);
                    return null;
                } catch (Throwable e) {
                    return e;
                }
            }
            public void finished(Object value) {
                fileExported(documentView, file, value);
            }
        });
    }
    protected void fileExported(ExportableDocumentView documentView, File file, Object value) {
        if (value == null) {
            /*
            documentView.setFile(file);
            documentView.markChangesAsSaved();
            int multiOpenId = 1;
            for (Project p : documentView.getApplication().documents()) {
                if (p != documentView && p.getFile() != null && p.getFile().equals(file)) {
                    multiOpenId = Math.max(multiOpenId, p.getMultipleOpenId() + 1);
                }
            }
            documentView.setMultipleOpenId(multiOpenId);
             */
        } else {
            System.out.flush();
            ((Throwable) value).printStackTrace();
            // FIXME localize this error messsage
            JSheet.showMessageSheet(documentView.getComponent(),
                    "<html>"+UIManager.getString("OptionPane.css")+
                    "<b>Couldn't export to the file \""+file+"\".<p>"+
                    "Reason: "+value,
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