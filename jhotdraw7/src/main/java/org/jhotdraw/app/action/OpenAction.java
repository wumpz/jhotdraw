/*
 * @(#)OpenAction.java  2.2  2009-02-08
 *
 * Copyright (c) 1996-2008 by the original authors of JHotDraw
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

import org.jhotdraw.util.*;
import org.jhotdraw.gui.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;

/**
 * Opens a file in new view, or in the current view, if it is empty.
 *
 * @author  Werner Randelshofer
 * @version 2.2 2009-03-08 Moved call to getOpenChooser into separate method.
 * <br>2.1 2008-03-19 Check whether file exists before opening it.
 * <br>2.0.2 2008-02-23 View and application was not enabled after
 * unsuccessful file open. 
 * <br>2.0.1 2006-05-18 Print stack trace added.
 * <br>2.0 2006-02-16 Support for preferences added.
 * <br>1.0.1 2005-07-14 Make view explicitly visible after creating it.
 * <br>1.0  04 January 2005  Created.
 */
public class OpenAction extends AbstractApplicationAction {

    public final static String ID = "file.open";

    /** Creates a new instance. */
    public OpenAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }

    protected JFileChooser getFileChooser(View view) {
        return view.getOpenChooser();
    }

    public void actionPerformed(ActionEvent evt) {
        final Application app = getApplication();
        if (app.isEnabled()) {
            app.setEnabled(false);
            // Search for an empty view
            View emptyView = app.getActiveView();
            if (emptyView == null ||
                    emptyView.getFile() != null ||
                    emptyView.hasUnsavedChanges()) {
                emptyView = null;
            }

            final View view;
            boolean removeMe;
            if (emptyView == null) {
                view = app.createView();
                app.add(view);
                removeMe = true;
            } else {
                view = emptyView;
                removeMe = false;
            }
            JFileChooser fileChooser = getFileChooser(view);
            if (fileChooser.showOpenDialog(app.getComponent()) == JFileChooser.APPROVE_OPTION) {
                app.show(view);
                openFile(fileChooser, view);
            } else {
                if (removeMe) {
                    app.remove(view);
                }
                app.setEnabled(true);
            }
        }
    }

    protected void openFile(JFileChooser fileChooser, final View view) {
        final Application app = getApplication();
        final File file = fileChooser.getSelectedFile();
        app.setEnabled(true);
        view.setEnabled(false);

        // If there is another view with we set the multiple open
        // id of our view to max(multiple open id) + 1.
        int multipleOpenId = 1;
        for (View aView : app.views()) {
            if (aView != view &&
                    aView.getFile() != null &&
                    aView.getFile().equals(file)) {
                multipleOpenId = Math.max(multipleOpenId, aView.getMultipleOpenId() + 1);
            }
        }
        view.setMultipleOpenId(multipleOpenId);
        view.setEnabled(false);

        // Open the file
        view.execute(new Worker() {

            public Object construct() {
                try {
                    if (file.exists()) {
                        view.read(file);
                        return null;
                    } else {
                        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
                        return new IOException(labels.getFormatted("file.open.fileDoesNotExist.message", file.getName()));
                    }
                } catch (Throwable e) {
                    return e;
                }
            }

            public void finished(Object value) {
                fileOpened(view, file, value);
            }
        });
    }

    protected void fileOpened(final View view, File file, Object value) {
        final Application app = getApplication();
        if (value == null) {
            view.setFile(file);
            view.setEnabled(true);
            Frame w = (Frame) SwingUtilities.getWindowAncestor(view.getComponent());
            if (w != null) {
                w.setExtendedState(w.getExtendedState() & ~Frame.ICONIFIED);
                w.toFront();
            }
            view.getComponent().requestFocus();
            app.addRecentFile(file);
            app.setEnabled(true);
        } else {
            view.setEnabled(true);
            app.setEnabled(true);
            String message;
            if ((value instanceof Throwable) && ((Throwable) value).getMessage() != null) {
                message = ((Throwable) value).getMessage();
                ((Throwable) value).printStackTrace();
            } else if ((value instanceof Throwable)) {
                message = value.toString();
                ((Throwable) value).printStackTrace();
            } else {
                message = value.toString();
            }
            ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
            JSheet.showMessageSheet(view.getComponent(),
                    "<html>" + UIManager.getString("OptionPane.css") +
                    "<b>" + labels.getFormatted("file.open.couldntOpen.message", file.getName()) + "</b><br>" +
                    ((message == null) ? "" : message),
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
