/*
 * @(#)OpenApplicationFileAction.java
 *
 * Copyright (c) 1996-2009 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.app.action.app;

import org.jhotdraw.gui.Worker;
import org.jhotdraw.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractApplicationAction;

/**
 * Opens a file for which an open-request was sent to the application.
 * <p>
 * The file name is passed in the action command of the action event.
 * <p>
 * This action is called when the user drops a file on the dock icon of
 * {@code DefaultOSXApplication} or onto the desktop area of
 * {@code DefaultMDIApplication}. 
 * <p>
 * If you want this behavior in your application, you have to create an action
 * with this ID and put it in your {@code ApplicationModel} in method
 * {@link org.jhotdraw.app.ApplicationModel#initApplication}.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class OpenApplicationFileAction extends AbstractApplicationAction {

    public final static String ID = "application.openFile";
    private JFileChooser fileChooser;
    private int entries;

    /** Creates a new instance. */
    public OpenApplicationFileAction(Application app) {
        super(app);
        putValue(Action.NAME, "OSX Open File");
    }

    /**
     * Opens a new view.
     * <p>
     * The file name is passed in the action command of the action event.
     *
     */
    public void actionPerformed(ActionEvent evt) {
        final Application app = getApplication();
        final String filename = evt.getActionCommand();
        final View p = app.createView();
        p.setEnabled(false);
        app.add(p);
        app.show(p);
        p.execute(new Worker() {

            @Override
            public Object construct() throws IOException {
                p.read(new File(filename).toURI());
                return null;
            }

            @Override
            protected void done(Object value) {
                p.setURI(new File(filename).toURI());
                p.setEnabled(true);
            }

            @Override
            protected void failed(Throwable value) {
                ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
                app.dispose(p);
                JOptionPane.showMessageDialog(
                        null,
                        "<html>" + UIManager.getString("OptionPane.css")
                        + "<b>" + labels.getFormatted("file.open.couldntOpen.message", new File(filename).getName()) + "</b><p>"
                        + value,
                        "",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
