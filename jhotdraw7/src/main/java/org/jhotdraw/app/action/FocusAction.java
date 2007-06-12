/*
 * @(#)FocusAction.java  2.0  2006-05-05
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

import application.ResourceMap;
import org.jhotdraw.util.*;

import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import org.jhotdraw.application.DocumentView;
/**
 * Requests focus for a DocumentView.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2006-05-05 Reworked.
 * <br>1.0  2005-06-10 Created.
 */
public class FocusAction extends AbstractApplicationAction {
    public final static String ID = "View.focus";
    private DocumentView documentView;
    
    /** Creates a new instance. */
    public FocusAction(DocumentView documentView) {
        this.documentView = documentView;
        initActionProperties(ID);
        setEnabled(documentView != null);
        
        documentView.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                ResourceMap labels = getResourceMap();
                String name = evt.getPropertyName();
                if (name.equals("file")) {
                    putValue(Action.NAME,
                            (evt.getNewValue() == null) ?
                                labels.getString("File.unnamedFile") :
                                ((File) evt.getNewValue()).getName()
                                );
                }
            }
        });
    }
    
    public Object getValue(String key) {
        if (key == Action.NAME && documentView != null) {
            return getTitle();
        } else {
            return super.getValue(key);
        }
    }
    
    private String getTitle() {
        String title = documentView.getName();
        if (documentView.isModified()) {
            title += " *";
        }
        return title;
    }
    private JFrame getFrame() {
        return (JFrame) SwingUtilities.getWindowAncestor(
                documentView.getComponent()
                );
    }
    private Component getRootPaneContainer() {
        return SwingUtilities.getRootPane(
                documentView.getComponent()
                ).getParent();
    }
    
    public void actionPerformed(ActionEvent evt) {
        /*
        JFrame frame = getFrame();
        if (frame != null) {
            frame.setExtendedState(frame.getExtendedState() & ~Frame.ICONIFIED);
            frame.toFront();
            frame.requestFocus();
            JRootPane rp = SwingUtilities.getRootPane(documentView.getComponent());
            if (rp != null && (rp.getParent() instanceof JInternalFrame)) {
                ((JInternalFrame) rp.getParent()).toFront();
            }
            documentView.getComponent().requestFocus();
        } else {
            Toolkit.getDefaultToolkit().beep();
        }*/
        Component rpContainer = getRootPaneContainer();
        if (rpContainer instanceof Frame) {
            Frame frame = (Frame) rpContainer;
            frame.setExtendedState(frame.getExtendedState() & ~Frame.ICONIFIED);
            frame.toFront();
        } else if (rpContainer instanceof JInternalFrame) {
            JInternalFrame frame = (JInternalFrame) rpContainer;
            frame.toFront();
            try {
                frame.setSelected(true);
            } catch (PropertyVetoException e) {
                // Don't care.
            }
        }
        documentView.getComponent().requestFocusInWindow();
    }
}
