/*
 * @(#)ToggleLineNumbersAction.java
 *
 * Copyright (c) 2005 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.teddy.action;

import java.awt.event.*;
import javax.swing.Action;
import org.jhotdraw.action.AbstractViewAction;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.View;
import org.jhotdraw.samples.teddy.TeddyView;
import org.jhotdraw.utils.util.*;

/** ToggleLineNumbersAction. */
public class ToggleLineNumbersAction extends AbstractViewAction {

  private static final long serialVersionUID = 1L;
  public static final String ID = "view.toggleLineNumbers";
  private ResourceBundleUtil labels =
      ResourceBundleUtil.getBundle("org.jhotdraw.samples.teddy.Labels");

  public ToggleLineNumbersAction(Application app, View view) {
    super(app, view);
    labels.configureAction(this, ID);
    setPropertyName("lineNumbersVisible");
  }

  @Override
  public TeddyView getActiveView() {
    return (TeddyView) super.getActiveView();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    getActiveView().setLineNumbersVisible(!getActiveView().isLineNumbersVisible());
  }

  @Override
  protected void updateView() {
    putValue(
        Action.SELECTED_KEY, getActiveView() != null && getActiveView().isLineNumbersVisible());
  }
}
