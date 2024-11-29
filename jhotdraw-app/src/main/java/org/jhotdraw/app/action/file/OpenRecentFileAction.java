/*
 * @(#)OpenRecentFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import java.awt.*;
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
import org.jhotdraw.utils.net.URIUtil;
import org.jhotdraw.utils.util.*;

/**
 * Loads the specified URI into an empty view. If no empty view is available, a new view is created.
 *
 * <p>This action is called when the user selects an item in the Recent Files submenu of the File
 * menu. The action and the menu item is automatically created by the application, when the {@code
 * ApplicationModel} provides a {@code OpenFileAction}. <hr> <b>Features</b>
 *
 * <p><em>Allow multiple views per URI</em><br>
 * When the feature is disabled, {@code OpenRecentFileAction} prevents opening an URI which is
 * opened in another view.<br>
 * See {@link org.jhotdraw.app} for a description of the feature.
 *
 * <p><em>Open last URI on launch</em><br>
 * {@code OpenRecentFileAction} supplies data for this feature by calling {@link
 * Application#addRecentURI} when it successfully opened a file. See {@link org.jhotdraw.app} for a
 * description of the feature.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class OpenRecentFileAction extends AbstractApplicationAction {

  private static final long serialVersionUID = 1L;
  public static final String ID = "file.openRecent";
  private URI uri;

  public OpenRecentFileAction(Application app, URI uri) {
    super(app);
    this.uri = uri;
    putValue(Action.NAME, URIUtil.getName(uri));
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    final Application app = getApplication();
    if (app.isEnabled()) {
      // Prevent same URI from being opened more than once
      if (!getApplication().getModel().isAllowMultipleViewsPerURI()) {
        for (View vw : getApplication().getViews()) {
          if (vw.getURI() != null && vw.getURI().equals(uri)) {
            vw.getComponent().requestFocus();
            return;
          }
        }
      }
      app.setEnabled(false);
      // Search for an empty view
      View emptyView = app.getActiveView();
      if (emptyView == null || !emptyView.isEmpty() || !emptyView.isEnabled()) {
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
      openView(p);
    }
  }

  protected void openView(final View view) {
    final Application app = getApplication();
    app.setEnabled(true);
    // If there is another view with the same URI we set the multiple open
    // id of our view to max(multiple open id) + 1.
    int multipleOpenId = 1;
    for (View aView : app.views()) {
      if (aView != view && aView.isEmpty()) {
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
          throw new IOException(
              labels.getFormatted("file.open.fileDoesNotExist.message", URIUtil.getName(uri)));
        }
        return null;
      }

      @Override
      protected void done() {
        try {
          get();
          view.setURI(uri);
          Frame w = (Frame) SwingUtilities.getWindowAncestor(view.getComponent());
          if (w != null) {
            w.setExtendedState(w.getExtendedState() & ~Frame.ICONIFIED);
            w.toFront();
          }
          app.addRecentURI(uri);
          view.setEnabled(true);
          view.getComponent().requestFocus();
        } catch (InterruptedException | ExecutionException ex) {
          Logger.getLogger(OpenRecentFileAction.class.getName()).log(Level.SEVERE, null, ex);
          failed(ex);
        }
      }

      protected void failed(Throwable value) {
        value.printStackTrace();
        String message = value.getMessage() != null ? value.getMessage() : value.toString();
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        JSheet.showMessageSheet(
            view.getComponent(),
            "<html>"
                + UIManager.getString("OptionPane.css")
                + "<b>"
                + labels.getFormatted("file.open.couldntOpen.message", URIUtil.getName(uri))
                + "</b><p>"
                + (message == null ? "" : message),
            JOptionPane.ERROR_MESSAGE,
            new SheetListener() {
              @Override
              public void optionSelected(SheetEvent evt) {
                view.setEnabled(true);
              }
            });
      }
    }.execute();
  }
}
