/*
 * @(#)SelectSameAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import java.util.*;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.utils.util.ResourceBundleUtil;

/** SelectSameAction. */
public class SelectSameAction extends AbstractSelectedAction {

  private static final long serialVersionUID = 1L;
  public static final String ID = "edit.selectSame";

  public SelectSameAction(DrawingEditor editor) {
    super(editor);
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    labels.configureAction(this, ID);
    updateEnabledState();
  }

  @Override
  public void actionPerformed(java.awt.event.ActionEvent e) {
    selectSame();
  }

  public void selectSame() {
    HashSet<Class<?>> selectedClasses = new HashSet<>();
    for (Figure selected : getView().getSelectedFigures()) {
      selectedClasses.add(selected.getClass());
    }
    for (Figure f : getDrawing().getChildren()) {
      if (selectedClasses.contains(f.getClass())) {
        getView().addToSelection(f);
      }
    }
  }
}
