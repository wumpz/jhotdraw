/*
 * @(#)OpenAction.java  2.0.1  2006-05-18
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
import org.jhotdraw.application.*;


/**
 * Opens a file in new documentView, or in the current documentView, if it is empty.
 *
 * @author  Werner Randelshofer
 * @version 2.0.1 2006-05-18 Print stack trace added.
 * <br>2.0 2006-02-16 Support for preferences added.
 * <br>1.0.1 2005-07-14 Make documentView explicitly visible after creating it.
 * <br>1.0  04 January 2005  Created.
 */
public class OpenAction extends AbstractApplicationAction {
    public final static String ID = "File.open";
    
    /** Creates a new instance. */
    public OpenAction() {
        initActionProperties(ID);
    }
    
    public void actionPerformed(ActionEvent evt) {
        final DocumentOrientedApplication application = getApplication();
        if (application.isEnabled()) {
            application.setEnabled(false);
            // Search for an empty documentView
            DocumentView emptyProject = application.getCurrentView();
            if (emptyProject == null ||
                    emptyProject.getFile() != null ||
                    emptyProject.isModified()) {
                emptyProject = null;
                /*
                for (DocumentView aProject : application.getViews()) {
                    if (aProject.getFile() == null &&
                            ! aProject.isModified()) {
                        emptyProject = aProject;
                        break;
                    }
                }*/
            }
            
            final DocumentView p;
            boolean removeMe;
            if (emptyProject == null) {
                p = application.createView();
                application.add(p);
                removeMe = true;
                removeMe = true;
            } else {
                p = emptyProject;
                removeMe = false;
            }
            JFileChooser fileChooser = p.getOpenChooser();
            if (fileChooser.showOpenDialog(application.getComponent()) == JFileChooser.APPROVE_OPTION) {
                application.show(p);
                openFile(fileChooser, p);
            } else {
                if (removeMe) {
                application.remove(p);
                }
                application.setEnabled(true);
            }
        }
    }
    
    protected void openFile(JFileChooser fileChooser, final DocumentView documentView) {
        final DocumentOrientedApplication application = getApplication();
        final File file = fileChooser.getSelectedFile();
        application.setEnabled(true);
        documentView.setEnabled(false);
        
        // Open the file
        documentView.execute(new Worker() {
            public Object construct() {
                try {
                    documentView.read(file);
                    return null;
                } catch (Throwable e) {
                    return e;
                }
            }
            public void finished(Object value) {
                fileOpened(documentView, file, value);
            }
        });
    }
    protected void fileOpened(final DocumentView documentView, File file, Object value) {
        final DocumentOrientedApplication application = getApplication();
        if (value == null) {
            documentView.setFile(file);
            documentView.setEnabled(true);
            Frame w = (Frame) SwingUtilities.getWindowAncestor(documentView.getComponent());
            if (w != null) {
            w.setExtendedState(w.getExtendedState() & ~Frame.ICONIFIED);
            w.toFront();
            }
            documentView.getComponent().requestFocus();
                application.addRecentFile(file);
                application.setEnabled(true);
        } else {
            if (value instanceof Throwable) {
                ((Throwable) value).printStackTrace();
            }
            JSheet.showMessageSheet(documentView.getComponent(),
                    "<html>"+UIManager.getString("OptionPane.css")+
                    "<b>Couldn't open the file \""+file+"\".</b><br>"+
                    value,
                    JOptionPane.ERROR_MESSAGE, new SheetListener() {
                public void optionSelected(SheetEvent evt) {
                   // application.dispose(documentView);
                }
            }
            );
        }
    }
}
