/*
 * @(#)LoadRecentAction.java  1.0  June 15, 2006
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
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import java.io.*;
import org.jhotdraw.application.DocumentOrientedApplication;
import org.jhotdraw.application.DocumentView;
/**
 * LoadRecentAction.
 *
 * @author Werner Randelshofer.
 * @version 1.0 June 15, 2006 Created.
 */
public class LoadRecentAction extends AbstractSaveBeforeAction {
    public final static String ID = "File.loadRecent";
    private File file;
    
    /** Creates a new instance. */
    public LoadRecentAction(File file) {
        this.file = file;
        putValue(Action.NAME, file.getName());
    }
    
    public void doIt(final DocumentView documentView) {
        final DocumentOrientedApplication application = getApplication();
        application.setEnabled(true);
        
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
            if (application != null) {
                application.setEnabled(true);
            }
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
