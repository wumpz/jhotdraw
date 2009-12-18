/*
 * @(#)OSXOpenFileAction.java
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.app.action;

import org.jhotdraw.gui.Worker;
import org.jhotdraw.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.PrintableView;
import org.jhotdraw.app.View;

/**
 * Opens a new view for each file dropped on the dock icon of the application
 * and then prints it.
 *
 * @author  Werner Randelshofer
 * @version $Id: OSXOpenFileAction.java 588 2009-12-16 17:10:14Z rawcoder $
 */
public class OSXPrintFileAction extends PrintAction {

    public final static String ID = "application.printFile";
    private JFileChooser fileChooser;
    private int entries;

    /** Creates a new instance. */
    public OSXPrintFileAction(Application app) {
        super(app);
        putValue(Action.NAME, "OSX Print File");
    }

    public void actionPerformed(ActionEvent evt) {
        final Application app = getApplication();
        final String filename = evt.getActionCommand();
        View v = app.createView();
        if (!(v instanceof PrintableView)) {
            return;
        }
        final PrintableView p = (PrintableView) v;
        p.setEnabled(false);
        app.add(p);
//            app.show(p);
        p.execute(new Worker() {

            @Override
            public Object construct() throws IOException {
                p.read(new File(filename).toURI());
                return null;
            }

            @Override
            protected void done(Object value) {
                p.setURI(new File(filename).toURI());
                p.setEnabled(false);
                if (System.getProperty("apple.awt.graphics.UseQuartz", "false").equals("true")) {
                    printQuartz(p);
                } else {
                    printJava2D(p);
                }
                p.setEnabled(true);
                app.dispose(p);
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
