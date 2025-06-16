/*
 * @(#)TogglePropertiesPanelAction.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.odg.action;

import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.action.AbstractViewAction;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.View;
import org.jhotdraw.samples.odg.ODGView;
import org.jhotdraw.utils.util.*;

/** TogglePropertiesPanelAction. */
public class TogglePropertiesPanelAction extends AbstractViewAction {

  private static final long serialVersionUID = 1L;

  public TogglePropertiesPanelAction(Application app, View view) {
    super(app, view);
    setPropertyName("propertiesPanelVisible");
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.odg.Labels");
    putValue(AbstractAction.NAME, labels.getString("propertiesPanel"));
  }

  /** This method is invoked, when the property changed and when the view changed. */
  @Override
  protected void updateView() {
    putValue(
        ActionUtil.SELECTED_KEY,
        getActiveView() != null && !getActiveView().isPropertiesPanelVisible());
  }

  @Override
  public ODGView getActiveView() {
    return (ODGView) super.getActiveView();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    getActiveView().setPropertiesPanelVisible(!getActiveView().isPropertiesPanelVisible());
  }
}
