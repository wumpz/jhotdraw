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

package org.jhotdraw.app.action;

import org.jhotdraw.util.*;

//import net.roydesign.app.*;
import net.roydesign.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.Project;

/**
 * Opens a new project for each file dropped on the dock icon of the application.
 * This action must be registered with net.roydesign.app.Application.
 *
 * @author  Werner Randelshofer
 * @version 1.0.1 2005-07-14 Show frame of project after it has been created.
 * <br>1.0  04 January 2005  Created.
 */
public class OSXDropOnDockAction extends AbstractApplicationAction {
    public final static String ID = "osxdrop";
    private JFileChooser fileChooser;
    private int entries;
    
    /** Creates a new instance. */
    public OSXDropOnDockAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        putValue(Action.NAME, "OSX Drop On Dock");
    }
    
    public void actionPerformed(ActionEvent evt) {
        final Application app = getApplication();
        if (evt instanceof ApplicationEvent) {
            final ApplicationEvent ae = (ApplicationEvent) evt;
            final Project p = app.createProject();
            p.setEnabled(false);
            app.add(p);
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
                        app.dispose(p);
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