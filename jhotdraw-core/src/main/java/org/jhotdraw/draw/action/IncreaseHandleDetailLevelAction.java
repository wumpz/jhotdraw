/*
 * @(#)SelectSameAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.*;
import org.jhotdraw.utils.util.ResourceBundleUtil;

/** SelectSameAction. */
public class IncreaseHandleDetailLevelAction extends AbstractSelectedAction {

  private static final long serialVersionUID = 1L;
  public static final String ID = "view.increaseHandleDetailLevel";

  public IncreaseHandleDetailLevelAction(DrawingEditor editor) {
    super(editor);
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    labels.configureAction(this, ID);
    // putValue(AbstractAction.NAME, labels.getString("editSelectSame"));
    //  putValue(AbstractAction.MNEMONIC_KEY, labels.getString("editSelectSameMnem"));
    updateEnabledState();
  }

  @Override
  public void actionPerformed(java.awt.event.ActionEvent e) {
    increaseHandleDetaiLevel();
  }

  public void increaseHandleDetaiLevel() {
    DrawingView view = getView();
    if (view != null) {
      view.setHandleDetailLevel(view.getHandleDetailLevel() + 1);
    }
  }
}
