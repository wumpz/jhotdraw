/*
 * @(#)LoadFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import java.awt.Component;
import java.awt.Window;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.View;
import org.jhotdraw.api.gui.URIChooser;
import org.jhotdraw.app.action.AbstractSaveUnsavedChangesAction;
import org.jhotdraw.gui.JSheet;
import org.jhotdraw.gui.event.SheetEvent;
import org.jhotdraw.gui.event.SheetListener;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * Lets the user save unsaved changes of the active view, then presents
 * an {@code URIChooser} and loads the selected URI into the active view.
 * <p>
 * This action is called when the user selects the Load item in the File
 * menu. The menu item is automatically created by the application.
 * A Recent Files sub-menu is also automatically generated.
 * <p>
 * If you want this behavior in your application, you have to create it
 * and put it in your {@code ApplicationModel} in method
 * {@link org.jhotdraw.app.ApplicationModel#initApplication}.
 * <p>
 * This action is designed for applications which do not automatically
 * create a new view for each opened file. This action goes together with
 * {@link ClearFileAction}, {@link NewWindowAction}, {@link LoadFileAction},
 * {@link LoadDirectoryAction} and {@link CloseFileAction}.
 * This action should not be used together with {@link OpenFileAction}.
 *
 * <hr>
 * <b>Features</b>
 *
 * <p>
 * <em>Open last URI on launch</em><br>
 * When the application is started, the last opened URI is opened in a view.<br>
 * {@code LoadFileAction} supplies data for this feature by calling
 * {@link Application#addRecentURI} when it successfully loaded a file.
 * See {@link org.jhotdraw.app} for a description of the feature.
 * </p>
 *
 * <p>
 * <em>Allow multiple views per URI</em><br>
 * When the feature is disabled, {@code LoadFileAction} prevents exporting to an URI which
 * is opened in another view.<br>
 * See {@link org.jhotdraw.app} for a description of the feature.
 * </p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LoadFileAction extends AbstractSaveUnsavedChangesAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "file.load";

    /**
     * Creates a new instance.
     */
    public LoadFileAction(Application app, View view) {
        super(app, view);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }

    @Override
    protected URIChooser getChooser(View view) {
        URIChooser chsr = (URIChooser) (view.getComponent()).getClientProperty("loadChooser");
        if (chsr == null) {
            chsr = getApplication().getModel().createOpenChooser(getApplication(), view);
            view.getComponent().putClientProperty("loadChooser", chsr);
        }
        return chsr;
    }

    @Override
    public void doIt(final View view) {
        URIChooser fileChooser = getChooser(view);
        Window wAncestor = SwingUtilities.getWindowAncestor(view.getComponent());
        final Component oldFocusOwner = (wAncestor == null) ? null : wAncestor.getFocusOwner();
        JSheet.showOpenSheet(fileChooser, view.getComponent(), new SheetListener() {
            @Override
            public void optionSelected(final SheetEvent evt) {
                if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
                    final URI uri = evt.getChooser().getSelectedURI();
                    // Prevent same URI from being opened more than once
                    if (!getApplication().getModel().isAllowMultipleViewsPerURI()) {
                        for (View v : getApplication().getViews()) {
                            if (v != view && v.getURI() != null && v.getURI().equals(uri)) {
                                v.getComponent().requestFocus();
                                return;
                            }
                        }
                    }
                    loadViewFromURI(view, uri, evt.getChooser());
                } else {
                    view.setEnabled(true);
                    if (oldFocusOwner != null) {
                        oldFocusOwner.requestFocus();
                    }
                }
            }
        });
    }

    public void loadViewFromURI(final View view, final URI uri, final URIChooser chooser) {
        view.setEnabled(false);
        // Open the file
        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                view.read(uri, chooser);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    view.setURI(uri);
                    view.setEnabled(true);
                    getApplication().addRecentURI(uri);
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(LoadFileAction.class.getName()).log(Level.SEVERE, null, ex);
                    failed(ex);
                }
            }

            protected void failed(Throwable value) {
                value.printStackTrace();
                ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
                JSheet.showMessageSheet(view.getComponent(),
                        "<html>" + UIManager.getString("OptionPane.css")
                        + "<b>" + labels.getFormatted("file.load.couldntLoad.message", URIUtil.getName(uri)) + "</b><p>"
                        + (value),
                        JOptionPane.ERROR_MESSAGE, new SheetListener() {
                    @Override
                    public void optionSelected(SheetEvent evt) {
                        view.clear();
                        view.setEnabled(true);
                    }
                });
            }
        }.execute();
    }
}
