/*
 * @(#)LabelFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.event.FigureListener;
import org.jhotdraw.draw.event.FigureListenerAdapter;
import org.jhotdraw.draw.tool.TextEditingTool;
import org.jhotdraw.draw.tool.Tool;

/** A LabelFigure can be used to provide more double clickable area for a TextHolderFigure. */
public class LabelFigure extends TextFigure {

  private static final long serialVersionUID = 1L;
  private TextHolderFigure target;

  public LabelFigure() {
    this("Label");
  }

  public LabelFigure(String text) {
    setText(text);
    setEditable(false);
  }

  public void setLabelFor(TextHolderFigure target) {
    if (this.target != null) {
      this.target.removeFigureListener(FIGURE_LISTENER);
    }
    this.target = target;
    if (this.target != null) {
      this.target.addFigureListener(FIGURE_LISTENER);
    }
  }

  @Override
  public TextHolderFigure getLabelFor() {
    return (target == null) ? this : target;
  }

  /**
   * Returns a specialized tool for the given coordinate.
   *
   * <p>
   *
   * <p>Returns null, if no specialized tool is available.
   */
  @Override
  public Tool getTool(Point2D.Double p) {
    return (target != null && contains(p)) ? new TextEditingTool(target) : null;
  }

  private final FigureListener FIGURE_LISTENER =
      new FigureListenerAdapter() {

        @Override
        public void figureRemoved(FigureEvent e) {
          if (e.getFigure() == target) {
            target.removeFigureListener(this);
            target = null;
          }
        }

        @Override
        public void figureRequestRemove(FigureEvent e) {}
      };

  @Override
  public void remap(Map<Figure, Figure> oldToNew, boolean disconnectIfNotInMap) {
    super.remap(oldToNew, disconnectIfNotInMap);
    if (target != null) {
      Figure newTarget = oldToNew.get(target);
      if (newTarget != null) {
        target.removeFigureListener(FIGURE_LISTENER);
        target = (TextHolderFigure) newTarget;
        newTarget.addFigureListener(FIGURE_LISTENER);
      }
    }
  }
}
