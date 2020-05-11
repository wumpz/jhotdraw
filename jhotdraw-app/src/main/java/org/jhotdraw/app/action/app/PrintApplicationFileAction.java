/*
 * @(#)OSXOpenFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app.action.app;

import java.awt.event.*;
import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.View;
import org.jhotdraw.app.PrintableView;
import org.jhotdraw.app.action.file.PrintFileAction;
import org.jhotdraw.util.*;

/**
 * Prints a file for which a print request was sent to the application.
 * <p>
 * The file name is passed in the action command of the action event.
 * <p>
 * This action is called when {@code DefaultOSXApplication} receives a print
 * request from another application. The file name is passed in the action
 * command of the action event.
 * <p>
 * If you want this behavior in your application, you have to create an action
 * with this ID and put it in your {@code ApplicationModel} in method
 * {@link org.jhotdraw.app.ApplicationModel#initApplication}. The views created by
 * {@code ApplicationModel} must implement the {@link PrintableView} interface.
 * <p>
 * You should also create a {@link PrintFileAction} when you create this action.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PrintApplicationFileAction extends PrintFileAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "application.printFile";
    private JFileChooser fileChooser;
    private int entries;

    /**
     * Creates a new instance.
     */
    public PrintApplicationFileAction(Application app) {
        super(app, null);
        putValue(Action.NAME, "OSX Print File");
    }

    @Override
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
        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                p.read(new File(filename).toURI(), null);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    p.setURI(new File(filename).toURI());
                    p.setEnabled(false);
                    if ("true".equals(System.getProperty("apple.awt.graphics.UseQuartz", "false"))) {
                        printQuartz(p);
                    } else {
                        printJava2D(p);
                    }
                    p.setEnabled(true);
                    app.dispose(p);
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(PrintApplicationFileAction.class.getName()).log(Level.SEVERE, null, ex);
                    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
                    app.dispose(p);
                    JOptionPane.showMessageDialog(
                            null,
                            "<html>" + UIManager.getString("OptionPane.css")
                            + "<b>" + labels.getFormatted("file.open.couldntOpen.message", new File(filename).getName()) + "</b><p>"
                            + ex,
                            "",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
