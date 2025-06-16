/*
 * @(#)LoadRecentFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.View;
import org.jhotdraw.app.action.AbstractSaveUnsavedChangesAction;
import org.jhotdraw.gui.JSheet;
import org.jhotdraw.gui.event.SheetEvent;
import org.jhotdraw.gui.event.SheetListener;
import org.jhotdraw.utils.net.URIUtil;
import org.jhotdraw.utils.util.ResourceBundleUtil;

/**
 * Lets the user save unsaved changes of the active view, and then loads the specified URI into the
 * active view.
 *
 * <p>If there is no active view, this action creates a new view and thus acts the same like {@link
 * OpenRecentFileAction}.
 *
 * <p>This action is called when the user selects an item in the Recent Files submenu of the File
 * menu. The action and the menu item is automatically created by the application, when the {@code
 * ApplicationModel} provides a {@code LoadFileAction}. <hr> <b>Features</b>
 *
 * <p><em>Open last URI on launch</em><br>
 * {@code LoadRecentFileAction} supplies data for this feature by calling {@link
 * Application#addRecentURI} when it successfully loaded a file. See {@link org.jhotdraw.app} for a
 * description of the feature.
 *
 * <p><em>Allow multiple views per URI</em><br>
 * When the feature is disabled, {@code LoadRecentFileAction} prevents loading an URI which is
 * opened in another view.<br>
 * See {@link org.jhotdraw.app} for a description of the feature.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class LoadRecentFileAction extends AbstractSaveUnsavedChangesAction {

  private static final long serialVersionUID = 1L;
  public static final String ID = "file.loadRecent";
  private URI uri;

  public LoadRecentFileAction(Application app, View view, URI uri) {
    super(app, view);
    this.uri = uri;
    setMayCreateView(true);
    putValue(Action.NAME, URIUtil.getName(uri));
  }

  @Override
  public void doIt(View v) {
    final Application app = getApplication();
    // Prevent same URI from being opened more than once
    if (!getApplication().getModel().isAllowMultipleViewsPerURI()) {
      for (View vw : getApplication().getViews()) {
        if (vw.getURI() != null && vw.getURI().equals(uri)) {
          vw.getComponent().requestFocus();
          return;
        }
      }
    }
    // Search for an empty view
    if (v == null) {
      View emptyView = app.getActiveView();
      if (emptyView == null || emptyView.getURI() != null || emptyView.hasUnsavedChanges()) {
        emptyView = null;
      }
      if (emptyView == null) {
        v = app.createView();
        app.add(v);
        app.show(v);
      } else {
        v = emptyView;
      }
    }
    final View view = v;
    app.setEnabled(true);
    view.setEnabled(false);
    // If there is another view with the same file we set the multiple open
    // id of our view to max(multiple open id) + 1.
    int multipleOpenId = 1;
    for (View aView : app.views()) {
      if (aView != view && aView.getURI() != null && aView.getURI().equals(uri)) {
        multipleOpenId = Math.max(multipleOpenId, aView.getMultipleOpenId() + 1);
      }
    }
    view.setMultipleOpenId(multipleOpenId);
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
          throw new IOException(
              labels.getFormatted("file.load.fileDoesNotExist.message", URIUtil.getName(uri)));
        }
        return null;
      }

      @Override
      protected void done() {
        try {
          get();
          final Application app = getApplication();
          view.setURI(uri);
          app.addRecentURI(uri);
          Frame w = (Frame) SwingUtilities.getWindowAncestor(view.getComponent());
          if (w != null) {
            w.setExtendedState(w.getExtendedState() & ~Frame.ICONIFIED);
            w.toFront();
          }
          view.getComponent().requestFocus();
          app.setEnabled(true);
        } catch (InterruptedException | ExecutionException ex) {
          Logger.getLogger(LoadRecentFileAction.class.getName()).log(Level.SEVERE, null, ex);
          failed(ex);
        }
        finished();
      }

      protected void failed(Throwable error) {
        error.printStackTrace();
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        JSheet.showMessageSheet(
            view.getComponent(),
            "<html>"
                + UIManager.getString("OptionPane.css")
                + "<b>"
                + labels.getFormatted("file.load.couldntLoad.message", URIUtil.getName(uri))
                + "</b><p>"
                + error,
            JOptionPane.ERROR_MESSAGE,
            new SheetListener() {
              @Override
              public void optionSelected(SheetEvent evt) {
                // app.dispose(view);
              }
            });
      }

      protected void finished() {
        view.setEnabled(true);
      }
    }.execute();
  }
}
