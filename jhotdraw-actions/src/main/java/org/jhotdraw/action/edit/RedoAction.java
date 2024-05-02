/*
 * @(#)RedoAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.action.edit;

import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import org.jhotdraw.action.AbstractViewAction;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.View;
import org.jhotdraw.util.*;

/**
 * Redoes the last user action on the active view.
 *
 * <p>This action requires that the View returns a project specific redo action when invoking
 * getActionMap("redo") on a View.
 *
 * <p>This action is called when the user selects the Redo item in the Edit menu. The menu item is
 * automatically created by the application.
 *
 * <p>If you want this behavior in your application, you have to create an action with this ID and
 * put it in your {@code ApplicationModel} in method {@link
 * org.jhotdraw.app.ApplicationModel#initApplication}.
 */
public class RedoAction extends AbstractViewAction {

  private static final long serialVersionUID = 1L;
  public static final String ID = "edit.redo";
  private ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.action.Labels");
  private PropertyChangeListener redoActionPropertyListener = new PropertyChangeListener() {
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      String name = evt.getPropertyName();
      if ((name == null && AbstractAction.NAME == null)
          || (name != null && name.equals(AbstractAction.NAME))) {
        putValue(AbstractAction.NAME, evt.getNewValue());
      } else if ("enabled".equals(name)) {
        updateEnabledState();
      }
    }
  };

  public RedoAction(Application app, View view) {
    super(app, view);
    labels.configureAction(this, ID);
  }

  protected void updateEnabledState() {
    boolean isEnabled = false;
    Action realRedoAction = getRealRedoAction();
    if (realRedoAction != null && realRedoAction != this) {
      isEnabled = realRedoAction.isEnabled();
    }
    setEnabled(isEnabled);
  }

  @Override
  protected void updateView(View oldValue, View newValue) {
    super.updateView(oldValue, newValue);
    if (newValue != null
        && newValue.getActionMap().get(ID) != null
        && newValue.getActionMap().get(ID) != this) {
      putValue(AbstractAction.NAME, newValue.getActionMap().get(ID).getValue(AbstractAction.NAME));
      updateEnabledState();
    }
  }

  /** Installs listeners on the view object. */
  @Override
  protected void installViewListeners(View p) {
    super.installViewListeners(p);
    Action redoActionInView = p.getActionMap().get(ID);
    if (redoActionInView != null && redoActionInView != this) {
      redoActionInView.addPropertyChangeListener(redoActionPropertyListener);
    }
  }

  /** Installs listeners on the view object. */
  @Override
  protected void uninstallViewListeners(View p) {
    super.uninstallViewListeners(p);
    Action redoActionInView = p.getActionMap().get(ID);
    if (redoActionInView != null && redoActionInView != this) {
      redoActionInView.removePropertyChangeListener(redoActionPropertyListener);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Action realAction = getRealRedoAction();
    if (realAction != null && realAction != this) {
      realAction.actionPerformed(e);
    }
  }

  private Action getRealRedoAction() {
    return (getActiveView() == null) ? null : getActiveView().getActionMap().get(ID);
  }
}
