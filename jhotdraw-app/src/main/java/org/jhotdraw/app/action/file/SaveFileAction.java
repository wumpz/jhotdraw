/*
 * @(#)SaveFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import org.jhotdraw.action.AbstractViewAction;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.ApplicationModel;
import org.jhotdraw.api.app.View;
import org.jhotdraw.api.gui.URIChooser;
import org.jhotdraw.gui.JSheet;
import org.jhotdraw.gui.event.SheetEvent;
import org.jhotdraw.gui.event.SheetListener;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * Saves the changes in the active view. If the active view has not an URI,
 * an {@code URIChooser} is presented.
 * <p>
 * This action is called when the user selects the Save item in the File
 * menu. The menu item is automatically created by the application.
 * <p>
 * If you want this behavior in your application, you have to create it
 * and put it in your {@code ApplicationModel} in method
 * {@link ApplicationModel#initApplication}.
 * <hr>
 * <b>Features</b>
 *
 * <p>
 * <em>Allow multiple views per URI</em><br>
 * When the feature is disabled, {@code SaveFileAction} prevents saving to
 * an URI which is opened in another view.<br>
 * See {@link org.jhotdraw.app} for a description of the feature.
 * </p>
 *
 * <p>
 * <em>Open last URI on launch</em><br>
 * {@code SaveFileAction} supplies data for this feature by calling
 * {@link Application#addRecentURI} when it successfully saved a file.
 * See {@link org.jhotdraw.app} for a description of the feature.
 * </p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SaveFileAction extends AbstractViewAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "file.save";
    private boolean saveAs;
    private Component oldFocusOwner;

    /**
     * Creates a new instance.
     */
    public SaveFileAction(Application app, View view) {
        this(app, view, false);
    }

    /**
     * Creates a new instance.
     */
    public SaveFileAction(Application app, View view, boolean saveAs) {
        super(app, view);
        this.saveAs = saveAs;
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }

    protected URIChooser getChooser(View view) {
        URIChooser chsr = (URIChooser) (view.getComponent()).getClientProperty("saveChooser");
        if (chsr == null) {
            chsr = getApplication().getModel().createSaveChooser(getApplication(), view);
            view.getComponent().putClientProperty("saveChooser", chsr);
        }
        return chsr;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        final View view = getActiveView();
        if (view == null) {
            return;
        }
        if (view.isEnabled()) {
            oldFocusOwner = SwingUtilities.getWindowAncestor(view.getComponent()).getFocusOwner();
            view.setEnabled(false);
            if (!saveAs && view.getURI() != null && view.canSaveTo(view.getURI())) {
                saveViewToURI(view, view.getURI(), null);
            } else {
                URIChooser fileChooser = getChooser(view);
                JSheet.showSaveSheet(fileChooser, view.getComponent(), new SheetListener() {
                    @Override
                    public void optionSelected(final SheetEvent evt) {
                        if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
                            final URI uri = evt.getChooser().getSelectedURI();
                            // Prevent same URI from being opened more than once
                            if (!getApplication().getModel().isAllowMultipleViewsPerURI()) {
                                for (View v : getApplication().getViews()) {
                                    if (v != view && v.getURI() != null && v.getURI().equals(uri)) {
                                        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
                                        JSheet.showMessageSheet(view.getComponent(), labels.getFormatted("file.saveAs.couldntSaveIntoOpenFile.message", evt.getFileChooser().getSelectedFile().getName()));
                                        view.setEnabled(true);
                                        return;
                                    }
                                }
                            }
                            saveViewToURI(view, uri, evt.getChooser());
                        } else {
                            view.setEnabled(true);
                            if (oldFocusOwner != null) {
                                oldFocusOwner.requestFocus();
                            }
                        }
                    }
                });
            }
        }
    }

    protected void saveViewToURI(final View view, final URI file,
            final URIChooser chooser) {
        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                view.write(file, chooser);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    view.setURI(file);
                    view.markChangesAsSaved();
                    int multiOpenId = 1;
                    for (View p : view.getApplication().views()) {
                        if (p != view && p.getURI() != null && p.getURI().equals(file)) {
                            multiOpenId = Math.max(multiOpenId, p.getMultipleOpenId() + 1);
                        }
                    }
                    getApplication().addRecentURI(file);
                    view.setMultipleOpenId(multiOpenId);
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(SaveFileAction.class.getName()).log(Level.SEVERE, null, ex);
                    failed(ex);
                }
                finished();
            }
           
            protected void failed(Throwable value) {
                value.printStackTrace();
                String message = value.getMessage() != null ? value.getMessage() : value.toString();
                ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
                JSheet.showMessageSheet(getActiveView().getComponent(),
                        "<html>" + UIManager.getString("OptionPane.css")
                        + "<b>" + labels.getFormatted("file.save.couldntSave.message", URIUtil.getName(file)) + "</b><p>"
                        + ((message == null) ? "" : message),
                        JOptionPane.ERROR_MESSAGE);
            }

            protected void finished() {
                view.setEnabled(true);
                SwingUtilities.getWindowAncestor(view.getComponent()).toFront();
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            }
        }.execute();
    }
}
