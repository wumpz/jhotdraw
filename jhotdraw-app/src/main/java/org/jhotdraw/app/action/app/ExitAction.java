/*
 * @(#)ExitAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app.action.app;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import org.jhotdraw.action.AbstractApplicationAction;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.View;
import org.jhotdraw.api.gui.URIChooser;
import org.jhotdraw.gui.JSheet;
import org.jhotdraw.gui.event.SheetEvent;
import org.jhotdraw.gui.event.SheetListener;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * Exits the application after letting the user review all unsaved views.
 * <p>
 * This action is called when the user selects the Exit item in the Application
 * menu, or when the application receives a Quit event from Mac OS X Finder.
 * The menu item is automatically created by the application.
 * <p>
 * This action is automatically created by the application and put into
 * the {@code ApplicationModel} before {@link org.jhotdraw.app.ApplicationModel#initApplication}
 * is called.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ExitAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "application.exit";
    private Component oldFocusOwner;
    private View unsavedView;

    /**
     * Creates a new instance.
     */
    public ExitAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        final Application app = getApplication();
        if (app.isEnabled()) {
            app.setEnabled(false);
            int unsavedViewsCount = 0;
            View documentToBeReviewed = null;
            URI unsavedURI = null;
            for (View p : app.views()) {
                if (p.hasUnsavedChanges()) {
                    if (p.isEnabled()) {
                        documentToBeReviewed = p;
                    }
                    unsavedURI = p.getURI();
                    unsavedViewsCount++;
                }
            }
            if (unsavedViewsCount > 0 && documentToBeReviewed == null) {
                // Silently abort, if no view can be reviewed.
                app.setEnabled(true);
                return;
            }
            final ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
            switch (unsavedViewsCount) {
                case 0:
                    doExit();
                    break;
                case 1:
                    unsavedView = documentToBeReviewed;
                    oldFocusOwner = SwingUtilities.getWindowAncestor(unsavedView.getComponent()).getFocusOwner();
                    unsavedView.setEnabled(false);
                    JOptionPane pane = new JOptionPane(
                            "<html>" + UIManager.getString("OptionPane.css")
                            + "<b>" + labels.getFormatted("application.exit.doYouWantToSave.message",
                                                          (unsavedURI == null) ? labels.getString("unnamedFile") : URIUtil.
                                                                  getName(unsavedURI)) + "</b><p>"
                            + labels.getString("application.exit.doYouWantToSave.details"),
                            JOptionPane.WARNING_MESSAGE);
                    Object[] options = {
                        labels.getString("application.exit.saveOption"),
                        labels.getString("application.exit.cancelOption"),
                        labels.getString("application.exit.dontSaveOption")
                    };
                    pane.setOptions(options);
                    pane.setInitialValue(options[0]);
                    pane.putClientProperty("Quaqua.OptionPane.destructiveOption", 2);
                    JSheet.showSheet(pane, unsavedView.getComponent(), new SheetListener() {
                                 @Override
                                 public void optionSelected(SheetEvent evt) {
                                     Object value = evt.getValue();
                                     if (value == null || value.
                                             equals(labels.getString("application.exit.cancelOption"))) {
                                         unsavedView.setEnabled(true);
                                         app.setEnabled(true);
                                     } else if (value.equals(labels.getString("application.exit.dontSaveOption"))) {
                                         doExit();
                                         unsavedView.setEnabled(true);
                                     } else if (value.equals(labels.getString("application.exit.saveOption"))) {
                                         saveChanges();
                                     }
                                 }
                             });
                    break;
                default:
                    pane = new JOptionPane(
                            "<html>" + UIManager.get("OptionPane.css")
                            + "<b>" + labels.getFormatted("application.exit.doYouWantToReview.message",
                                                          unsavedViewsCount) + "</b><p>"
                            + labels.getString("application.exit.doYouWantToReview.details"),
                            JOptionPane.QUESTION_MESSAGE);
                    Object[] options2 = {
                        labels.getString("application.exit.reviewChangesOption"),
                        labels.getString("application.exit.cancelOption"),
                        labels.getString("application.exit.discardChangesOption")
                    };
                    pane.setOptions(options2);
                    pane.setInitialValue(options2[0]);
                    pane.putClientProperty(
                            "Quaqua.OptionPane.destructiveOption", 2);
                    JDialog dialog = pane.createDialog(app.getComponent(), null);
                    Rectangle screenBounds = dialog.getGraphicsConfiguration().getBounds();
                    if (app.getComponent() == null || !screenBounds.contains(app.getComponent().getBounds())) {
                        // place dialog in center and in upper third of screen
                        dialog.setLocation((screenBounds.width - dialog.getWidth()) / 2, (screenBounds.height - dialog.
                                           getHeight()) / 3);
                    }
                    dialog.setVisible(true);
                    Object value = pane.getValue();
                    if (value == null || value.equals(labels.getString("application.exit.cancelOption"))) {
                        app.setEnabled(true);
                    } else if (value.equals(labels.getString("application.exit.discardChangesOption"))) {
                        doExit();
                        app.setEnabled(true);
                    } else if (value.equals(labels.getString("application.exit.reviewChangesOption"))) {
                        unsavedView = documentToBeReviewed;
                        reviewChanges();
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

    protected void saveChanges() {
        View v = unsavedView;
        if (v.getURI() == null) {
            URIChooser chooser = getChooser(v);
            JSheet.showSaveSheet(chooser, v.getComponent(), new SheetListener() {
                             @Override
                             public void optionSelected(final SheetEvent evt) {
                                 if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
                                     final URI uri = evt.getChooser().getSelectedURI();
                                     saveToFile(uri, evt.getChooser());
                                 } else {
                                     unsavedView.setEnabled(true);
                                     if (oldFocusOwner != null) {
                                         oldFocusOwner.requestFocus();
                                     }
                                     getApplication().setEnabled(true);
                                 }
                             }
                         });
        } else {
            saveToFile(v.getURI(), null);
        }
    }

    protected void reviewChanges() {
        if (unsavedView.isEnabled()) {
            final ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
            oldFocusOwner = SwingUtilities.getWindowAncestor(unsavedView.getComponent()).getFocusOwner();
            unsavedView.setEnabled(false);
            URI unsavedURI = unsavedView.getURI();
            JOptionPane pane = new JOptionPane(
                    "<html>" + UIManager.getString("OptionPane.css")
                    + labels.getFormatted("application.exit.doYouWantToSave.message",
                                          (unsavedURI == null) ? unsavedView.getTitle() : URIUtil.getName(unsavedURI)),
                    JOptionPane.WARNING_MESSAGE);
            Object[] options = {labels.getString("application.exit.saveOption"), labels.getString(
                                "application.exit.cancelOption"), labels.getString("application.exit.dontSaveOption")};
            pane.setOptions(options);
            pane.setInitialValue(options[0]);
            pane.putClientProperty("Quaqua.OptionPane.destructiveOption", 2);
            JSheet.showSheet(pane, unsavedView.getComponent(), new SheetListener() {
                         @Override
                         public void optionSelected(SheetEvent evt) {
                             Object value = evt.getValue();
                             if (value == null || value.equals(labels.getString("application.exit.cancelOption"))) {
                                 unsavedView.setEnabled(true);
                                 getApplication().setEnabled(true);
                             } else if (value.equals(labels.getString("application.exit.dontSaveOption"))) {
                                 getApplication().dispose(unsavedView);
                                 reviewNext();
                             } else if (value.equals(labels.getString("application.exit.saveOption"))) {
                                 saveChangesAndReviewNext();
                             }
                         }
                     });
        } else {
            getApplication().setEnabled(true);
        }
    }

    protected void saveChangesAndReviewNext() {
        final View v = unsavedView;
        if (v.getURI() == null) {
            URIChooser chooser = getChooser(v);
            JSheet.showSaveSheet(chooser, unsavedView.getComponent(), new SheetListener() {
                             @Override
                             public void optionSelected(final SheetEvent evt) {
                                 if (evt.getOption() == URIChooser.APPROVE_OPTION) {
                                     final URI uri = evt.getChooser().getSelectedURI();
                                     saveToFileAndReviewNext(uri, evt.getChooser());
                                 } else {
                                     v.setEnabled(true);
                                     if (oldFocusOwner != null) {
                                         oldFocusOwner.requestFocus();
                                     }
                                     getApplication().setEnabled(true);
                                 }
                             }
                         });
        } else {
            saveToFileAndReviewNext(v.getURI(), null);
        }
    }

    protected void reviewNext() {
        int unsavedViewsCount = 0;
        View documentToBeReviewed = null;
        for (View p : getApplication().views()) {
            if (p.hasUnsavedChanges()) {
                if (p.isEnabled()) {
                    documentToBeReviewed = p;
                }
                unsavedViewsCount++;
            }
        }
        if (unsavedViewsCount == 0) {
            doExit();
        } else if (documentToBeReviewed != null) {
            unsavedView = documentToBeReviewed;
            reviewChanges();
        } else {
            getApplication().setEnabled(true);
        }
    }

    protected void saveToFile(final URI uri, final URIChooser chooser) {
        final View v = unsavedView;
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
                    doExit();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(ExitAction.class.getName()).log(Level.SEVERE, null, ex);
                    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
                    JSheet.showMessageSheet(v.getComponent(),
                                            "<html>" + UIManager.getString("OptionPane.css")
                                            + "<b>" + labels.format("file.save.couldntSave.message", URIUtil.
                                                                    getName(uri)) + "</b><p>"
                                            + ex,
                                            JOptionPane.ERROR_MESSAGE);
                }
                v.setEnabled(true);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
                getApplication().setEnabled(true);
            }
        }.execute();
    }

    protected void saveToFileAndReviewNext(final URI uri, final URIChooser chooser) {
        final View v = unsavedView;
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
                    getApplication().dispose(unsavedView);
                    reviewNext();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(ExitAction.class.getName()).log(Level.SEVERE, null, ex);
                    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
                    JSheet.showMessageSheet(v.getComponent(),
                                            "<html>" + UIManager.getString("OptionPane.css")
                                            + "<b>" + labels.format("file.save.couldntSave.message", uri) + "</b><p>"
                                            + ex,
                                            JOptionPane.ERROR_MESSAGE);
                    v.setEnabled(true);
                    if (oldFocusOwner != null) {
                        oldFocusOwner.requestFocus();
                    }
                    getApplication().setEnabled(true);
                }
            }
        }.execute();
    }

    protected void doExit() {
        getApplication().destroy();
    }
}
