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

package org.jhotdraw.application.action;

import org.jhotdraw.gui.Worker;
import org.jhotdraw.util.*;
import org.jhotdraw.gui.*;
import org.jhotdraw.gui.event.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import org.jhotdraw.application.DocumentOrientedApplication;
import org.jhotdraw.application.DocumentView;

/**
 * Loads a file into the current documentView.
 *
 * @author  Werner Randelshofer
 * @version 1.0  2005-10-16  Created.
 */
public class LoadAction extends AbstractSaveBeforeAction {
    public final static String ID = "File.load";
    
    /** Creates a new instance. */
    public LoadAction() {
        initActionProperties("File.open");
    }
    
    public void doIt(DocumentView documentView) {
        JFileChooser fileChooser = documentView.getOpenChooser();
        if (fileChooser.showOpenDialog(documentView.getComponent()) == JFileChooser.APPROVE_OPTION) {
            openFile(documentView, fileChooser);
        } else {
            documentView.setEnabled(true);
        }
    }
    
    protected void openFile(final DocumentView documentView, JFileChooser fileChooser) {
        final File file = fileChooser.getSelectedFile();
        
        documentView.setEnabled(false);
        
        // Open the file
        documentView.execute(new Worker() {
            public Object construct() {
                try {
                    documentView.read(file);
                    return null;
                } catch (IOException e) {
                    return e;
                }
            }
            public void finished(Object value) {
                fileOpened(documentView, file, value);
            }
        });
    }
    
    protected void fileOpened(final DocumentView documentView, File file, Object value) {
        if (value == null) {
            documentView.setFile(file);
            documentView.setEnabled(true);
            getApplication().addRecentFile(file);
        } else {
            JSheet.showMessageSheet(documentView.getComponent(),
                    "<html>"+UIManager.getString("OptionPane.css")+
                    "<b>Couldn't open the file \""+file+"\".</b><br>"+
                    value,
                    JOptionPane.ERROR_MESSAGE, new SheetListener() {
                public void optionSelected(SheetEvent evt) {
                    documentView.execute(new Worker() {
                        public Object construct() {
                            try {
                                documentView.clear();
                                return null;
                            } catch (IOException ex) {
                                return ex;
                            }
                        }
                        public void finished(Object result) {
                            documentView.setEnabled(true);
                        }
                    });
                }
            }
            );
        }
    }
}