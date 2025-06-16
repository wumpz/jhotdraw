/*
 * @(#)EditGridAction.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.gui.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.action.AbstractDrawingViewAction;
import org.jhotdraw.draw.constrainer.GridConstrainer;
import org.jhotdraw.utils.util.ResourceBundleUtil;
import org.jhotdraw.utils.util.prefs.PreferencesUtil;

/**
 * EditGridAction.
 *
 * <p>XXX - We shouldn't have a dependency to the application framework from within the drawing
 * framework.
 */
public class EditGridAction extends AbstractDrawingViewAction {

  private static final long serialVersionUID = 1L;
  public static final String ID = "view.editGrid";
  private JDialog dialog;
  private EditGridPanel settingsPanel;
  private PropertyChangeListener propertyChangeHandler;
  private Application app;

  public EditGridAction(Application app, DrawingEditor editor) {
    super(editor);
    this.app = app;
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    labels.configureAction(this, ID);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    getDialog().setVisible(true);
  }

  @Override
  protected void updateViewState() {
    if (getView() != null && settingsPanel != null) {
      settingsPanel.setConstrainer((GridConstrainer) getView().getVisibleConstrainer());
    }
  }

  protected Application getApplication() {
    return app;
  }

  protected JDialog getDialog() {
    if (dialog == null) {
      ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
      dialog = new JDialog();
      dialog.setTitle(labels.getString("editGrid"));
      dialog.setResizable(false);
      settingsPanel = new EditGridPanel();
      dialog.add(settingsPanel);
      dialog.pack();
      Preferences prefs = PreferencesUtil.userNodeForPackage(getClass());
      PreferencesUtil.installFramePrefsHandler(prefs, "editGrid", dialog);
      getApplication().addWindow(dialog, null);
    }
    settingsPanel.setConstrainer((GridConstrainer) getView().getVisibleConstrainer());
    return dialog;
  }
}
