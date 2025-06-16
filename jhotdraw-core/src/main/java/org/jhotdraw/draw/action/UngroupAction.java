/*
 * @(#)UngroupAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.CompositeFigure;
import org.jhotdraw.draw.figure.GroupFigure;
import org.jhotdraw.utils.util.ResourceBundleUtil;

/** UngroupAction. */
public class UngroupAction extends GroupAction {

  private static final long serialVersionUID = 1L;
  public static final String ID = "edit.ungroupSelection";

  private CompositeFigure prototype;

  public UngroupAction(DrawingEditor editor) {
    super(editor, new GroupFigure(), false);
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    labels.configureAction(this, ID);
    updateEnabledState();
  }

  public UngroupAction(DrawingEditor editor, CompositeFigure prototype) {
    super(editor, prototype, false);
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    labels.configureAction(this, ID);
    updateEnabledState();
  }
}
