/*
 * @(#)OSXDropOnDockAction.java  1.0.1  2005-07-14
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

//import net.roydesign.application.*;
import net.roydesign.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import org.jhotdraw.application.*;

/**
 * Opens a new documentView for each file dropped on the dock icon of the application.
 * This action must be registered with net.roydesign.application.DocumentOrientedApplication.
 * 
 * @author Werner Randelshofer
 * @version 1.0.1 2005-07-14 Show frame of documentView after it has been created.
 * <br>1.0  04 January 2005  Created.
 */
public class OSXDropOnDockAction extends AbstractApplicationAction {
    public final static String ID = "Application.dropOnDock";
    private JFileChooser fileChooser;
    private int entries;
    
    /** Creates a new instance. */
    public OSXDropOnDockAction() {
        initActionProperties(ID);
    }
    
    public void actionPerformed(ActionEvent evt) {
        final DocumentOrientedApplication application = getApplication();
        if (evt instanceof ApplicationEvent) {
            final ApplicationEvent ae = (ApplicationEvent) evt;
            final DocumentView p = application.createView();
            p.setEnabled(false);
            application.add(p);
            p.execute(new Worker() {
                public Object construct() {
                    try {
                        p.read(ae.getFile());
                        return null;
                    } catch (IOException e) {
                        return e;
                    }
                }
                public void finished(Object value) {
                    if (value == null) {
                        p.setFile(ae.getFile());
                        p.setEnabled(true);
                    } else {
                        application.remove(p);
                        JOptionPane.showMessageDialog(
                        null,
                        "<html>"+UIManager.getString("OptionPane.css")+
                        "<b>Can't open file "+ae.getFile()+"</b><p>"+
                        value,
                        "",
                        JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            });
        }
    }
}