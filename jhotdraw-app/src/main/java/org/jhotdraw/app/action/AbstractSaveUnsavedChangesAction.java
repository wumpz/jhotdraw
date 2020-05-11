/*
 * @(#)AbstractSaveUnsavedChangesAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app.action;

import java.awt.Component;
import java.awt.Window;
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
import org.jhotdraw.api.app.View;
import org.jhotdraw.api.gui.URIChooser;
import org.jhotdraw.gui.JSheet;
import org.jhotdraw.gui.event.SheetEvent;
import org.jhotdraw.gui.event.SheetListener;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * This abstract class can be extended to implement an {@code Action} that asks
 * to save unsaved changes of a {@link org.jhotdraw.api.app.View} before a destructive
 * action is performed.
 * <p>
 * If the view has no unsaved changes, method {@code doIt} is invoked immediately.
 * If unsaved changes are present, a dialog is shown asking whether the user
 * wants to discard the changes, cancel or save the changes before doing it.
 * If the user chooses to discard the changes, {@code doIt} is invoked immediately.
 * If the user chooses to cancel, the action is aborted.
 * If the user chooses to save the changes, the view is saved, and {@code doIt}
 * is only invoked after the view was successfully saved.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractSaveUnsavedChangesAction extends AbstractViewAction {

    private static final long serialVersionUID = 1L;
    private Component oldFocusOwner;

    /**
     * Creates a new instance.
     */
    public AbstractSaveUnsavedChangesAction(Application app, View view) {
        super(app, view);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Application app = getApplication();
        View av = getActiveView();
        if (av == null) {
            if (isMayCreateView()) {
                av = app.createView();
                app.add(av);
                app.show(av);
            } else {
                return;
            }
        }
        final View v = av;
        if (v.isEnabled()) {
            final ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
            Window wAncestor = SwingUtilities.getWindowAncestor(v.getComponent());
            oldFocusOwner = (wAncestor == null) ? null : wAncestor.getFocusOwner();
            v.setEnabled(false);
            if (v.hasUnsavedChanges()) {
                URI unsavedURI = v.getURI();
                JOptionPane pane = new JOptionPane(
                        "<html>" + UIManager.getString("OptionPane.css")
                        + "<b>" + labels.getFormatted("file.saveBefore.doYouWantToSave.message",
                                                      (unsavedURI == null) ? labels.getString("unnamedFile") : URIUtil.
                                                              getName(unsavedURI)) + "</b><p>"
                        + labels.getString("file.saveBefore.doYouWantToSave.details"),
                        JOptionPane.WARNING_MESSAGE);
                Object[] options = {
                    labels.getString("file.saveBefore.saveOption.text"),
                    labels.getString("file.saveBefore.cancelOption.text"),
                    labels.getString("file.saveBefore.dontSaveOption.text")
                };
                pane.setOptions(options);
                pane.setInitialValue(options[0]);
                pane.putClientProperty("Quaqua.OptionPane.destructiveOption", 2);
                JSheet.showSheet(pane, v.getComponent(), new SheetListener() {
                             @Override
                             public void optionSelected(SheetEvent evt) {
                                 Object value = evt.getValue();
                                 if (value == null || value.
                                         equals(labels.getString("file.saveBefore.cancelOption.text"))) {
                                     v.setEnabled(true);
                                 } else if (value.equals(labels.getString("file.saveBefore.dontSaveOption.text"))) {
                                     doIt(v);
                                     v.setEnabled(true);
                                 } else if (value.equals(labels.getString("file.saveBefore.saveOption.text"))) {
                                     saveView(v);
                                 }
                             }
                         });
            } else {
                doIt(v);
                v.setEnabled(true);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            }
        }
    }

    protected URIChooser getChooser(View view) {
        URIChooser chsr = (URIChooser) (view.getComponent()).getClientProperty("saveChooser");
        if (chsr == null) {
            chsr = getApplication().getModel().createSaveChooser(getApplication(), view);
            view.getComponent().putClientProperty("saveChooser", chsr);
        }
        return chsr;
    }

    protected void saveView(final View v) {
        if (v.getURI() == null) {
            URIChooser chooser = getChooser(v);
            //int option = fileChooser.showSaveDialog(this);
            JSheet.showSaveSheet(chooser, v.getComponent(), new SheetListener() {
                             @Override
                             public void optionSelected(final SheetEvent evt) {
                                 if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
                                     saveViewToURI(v, evt.getChooser().getSelectedURI(), evt.getChooser());
                                 } else {
                                     v.setEnabled(true);
                                     if (oldFocusOwner != null) {
                                         oldFocusOwner.requestFocus();
                                     }
                                 }
                             }
                         });
        } else {
            saveViewToURI(v, v.getURI(), null);
        }
    }

    protected void saveViewToURI(final View v, final URI uri, final URIChooser chooser) {
        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                v.write(uri, chooser);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    v.setURI(uri);
                    v.markChangesAsSaved();
                    doIt(v);
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(AbstractSaveUnsavedChangesAction.class.getName()).log(Level.SEVERE, null, ex);
                    String message = (ex.getMessage() != null) ? ex.getMessage() : ex.toString();
                    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
                    JSheet.showMessageSheet(getActiveView().getComponent(),
                                            "<html>" + UIManager.getString("OptionPane.css")
                                            + "<b>" + labels.getFormatted("file.save.couldntSave.message", URIUtil.
                                                                          getName(uri)) + "</b><p>"
                                            + ((message == null) ? "" : message),
                                            JOptionPane.ERROR_MESSAGE);
                }
                v.setEnabled(true);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            }
        }.execute();
    }

    protected abstract void doIt(View p);
}
