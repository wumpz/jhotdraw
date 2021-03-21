/*
 * @(#)OpenApplicationFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app.action.app;

import java.awt.Frame;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.jhotdraw.action.AbstractApplicationAction;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.View;
import org.jhotdraw.gui.JSheet;
import org.jhotdraw.gui.event.SheetEvent;
import org.jhotdraw.gui.event.SheetListener;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.*;

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
 * <hr>
 * <b>Features</b>
 * <p>
 * <p>
 * <em>Allow multiple views per URI</em><br>
 * When the feature is disabled, {@code OpenApplicationFileAction} prevents
 * opening an URI which is opened in another view.<br>
 * See {@link org.jhotdraw.app} for a description of the feature.
 * </p>
 * <p>
 * <p>
 * <em>Open last URI on launch</em><br>
 * {@code OpenApplicationFileAction} supplies data for this feature by calling
 * {@link Application#addRecentURI} when it successfully loaded a file.
 * See {@link org.jhotdraw.app} for a description of the feature.
 * </p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class OpenApplicationFileAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "application.openFile";

    /**
     * Creates a new instance.
     */
    public OpenApplicationFileAction(Application app) {
        super(app);
        putValue(Action.NAME, "OSX Open File");
    }

    /**
     * Opens a new view.
     * <p>
     * The file name is passed in the action command of the action event.
     * <p>
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        final Application app = getApplication();
        final String filename = evt.getActionCommand();
        if (app.isEnabled()) {
            URI uri = new File(filename).toURI();
            // Prevent same URI from being opened more than once
            if (!app.getModel().isAllowMultipleViewsPerURI()) {
                for (View v : app.getViews()) {
                    if (v.getURI() != null && v.getURI().equals(uri)) {
                        v.getComponent().requestFocus();
                        return;
                    }
                }
            }
            app.setEnabled(false);
            // Search for an empty view
            View emptyView = app.getActiveView();
            if (emptyView == null
                || emptyView.getURI() != null
                || emptyView.hasUnsavedChanges()) {
                emptyView = null;
            }
            final View p;
            if (emptyView == null) {
                p = app.createView();
                app.add(p);
                app.show(p);
            } else {
                p = emptyView;
            }
            openView(p, uri);
        }
    }

    protected void openView(final View view, final URI uri) {
        final Application app = getApplication();
        app.setEnabled(true);
        // If there is another view with the same URI we set the multiple open
        // id of our view to max(multiple open id) + 1.
        int multipleOpenId = 1;
        for (View aView : app.views()) {
            if (aView != view
                && aView.getURI() != null
                && aView.getURI().equals(uri)) {
                multipleOpenId = Math.max(multipleOpenId, aView.getMultipleOpenId() + 1);
            }
        }
        view.setMultipleOpenId(multipleOpenId);
        view.setEnabled(false);
        // Open the file
        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                boolean exists = true;
                try {
                    File f = new File(uri);
                    exists = f.exists();
                } catch (IllegalArgumentException e) {
                    // The URI does not denote a file, thus we can not check whether the file exists.
                }
                if (exists) {
                    view.read(uri, null);
                } else {
                    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
                    throw new IOException(labels.
                            getFormatted("file.open.fileDoesNotExist.message", URIUtil.getName(uri)));
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    view.setURI(uri);
                    app.addRecentURI(uri);
                    Frame w = (Frame) SwingUtilities.getWindowAncestor(view.getComponent());
                    if (w != null) {
                        w.setExtendedState(w.getExtendedState() & ~Frame.ICONIFIED);
                        w.toFront();
                    }
                    view.setEnabled(true);
                    view.getComponent().requestFocus();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(OpenApplicationFileAction.class.getName()).log(Level.SEVERE, null, ex);
                    ex.printStackTrace();
                    String message = ex.getMessage() != null ? ex.getMessage() : ex.toString();
                    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
                    JSheet.showMessageSheet(view.getComponent(),
                                            "<html>" + UIManager.getString("OptionPane.css")
                                            + "<b>" + labels.getFormatted("file.open.couldntOpen.message", URIUtil.
                                                                          getName(uri)) + "</b><p>"
                                            + (message == null ? "" : message),
                                            JOptionPane.ERROR_MESSAGE, new SheetListener() {
                                        @Override
                                        public void optionSelected(SheetEvent evt) {
                                            view.setEnabled(true);
                                        }
                                    });
                }
            }
        }.execute();
    }
}
