/*
 * @(#)SplitPathsAction.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.odg.action;

import java.util.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.draw.figure.CompositeFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.samples.odg.figures.ODGPathFigure;
import org.jhotdraw.util.*;

/** SplitPathsAction. */
public class SplitAction extends UngroupAction {

  private static final long serialVersionUID = 1L;
  public static final String ID = "edit.splitPath";
  private ResourceBundleUtil labels =
      ResourceBundleUtil.getBundle("org.jhotdraw.samples.odg.Labels");

  public SplitAction(DrawingEditor editor) {
    super(editor, new ODGPathFigure());
    labels.configureAction(this, ID);
  }

  @Override
  protected boolean canUngroup() {
    if (super.canUngroup()) {
      return ((CompositeFigure) getView().getSelectedFigures().iterator().next()).getChildCount()
          > 1;
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<Figure> ungroupFigures(DrawingView view, CompositeFigure group) {
    LinkedList<Figure> figures = new LinkedList<Figure>(group.getChildren());
    view.clearSelection();
    group.basicRemoveAllChildren();
    LinkedList<Figure> paths = new LinkedList<Figure>();
    for (Figure f : figures) {
      ODGPathFigure path = new ODGPathFigure();
      path.removeAllChildren();
      for (Map.Entry<AttributeKey<?>, Object> entry :
          group.attr().getAttributes().entrySet()) {
        path.attr().set((AttributeKey<Object>) entry.getKey(), entry.getValue());
      }
      path.add(f);
      view.getDrawing().basicAdd(path);
      paths.add(path);
    }
    view.getDrawing().remove(group);
    view.addToSelection(paths);
    return figures;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void groupFigures(DrawingView view, CompositeFigure group, Collection<Figure> figures) {
    Collection<Figure> sorted = view.getDrawing().sort(figures);
    view.getDrawing().basicRemoveAll(figures);
    view.clearSelection();
    view.getDrawing().add(group);
    group.willChange();
    ((ODGPathFigure) group).removeAllChildren();
    for (Map.Entry<AttributeKey<?>, Object> entry :
        figures.iterator().next().attr().getAttributes().entrySet()) {
      group.attr().set((AttributeKey<Object>) entry.getKey(), entry.getValue());
    }
    for (Figure f : sorted) {
      ODGPathFigure path = (ODGPathFigure) f;
      for (Figure child : path.getChildren()) {
        group.basicAdd(child);
      }
    }
    group.changed();
    view.addToSelection(group);
  }
}
