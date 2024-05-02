/*
 * @(#)LineFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.handle.BezierNodeHandle;
import org.jhotdraw.draw.handle.BezierOutlineHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.geom.path.BezierPath;

/** A {@link Figure} which draws a continuous bezier path between two points. */
public class LineFigure extends BezierFigure {

  private static final long serialVersionUID = 1L;

  public LineFigure() {
    addNode(new BezierPath.Node(new Point2D.Double(0, 0)));
    addNode(new BezierPath.Node(new Point2D.Double(0, 0)));
    setConnectable(false);
  }

  // DRAWING
  // SHAPE AND BOUNDS
  // ATTRIBUTES
  // EDITING
  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    List<Handle> handles = new ArrayList<>();
    switch (detailLevel) {
      case -1: // Mouse hover handles
        handles.add(new BezierOutlineHandle(this, true));
        break;
      case 0:
        handles.add(new BezierOutlineHandle(this));
        for (int i = 0, n = path.size(); i < n; i++) {
          handles.add(new BezierNodeHandle(this, i));
        }
        break;
    }
    return handles;
  }

  // CONNECTING
  // COMPOSITE FIGURES
  // CLONING
  // EVENT HANDLING

  /** Handles a mouse click. */
  @Override
  public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
    if (evt.getClickCount() == 2 && view.getHandleDetailLevel() == 0) {
      willChange();
      final int index = splitSegment(p, (float) (5f / view.getScaleFactor()));
      if (index != -1) {
        final BezierPath.Node newNode = getNode(index);
        fireUndoableEditHappened(new AbstractUndoableEdit() {
          private static final long serialVersionUID = 1L;

          @Override
          public void redo() throws CannotRedoException {
            super.redo();
            willChange();
            addNode(index, newNode);
            changed();
          }

          @Override
          public void undo() throws CannotUndoException {
            super.undo();
            willChange();
            removeNode(index);
            changed();
          }
        });
        changed();
        return true;
      }
    }
    return false;
  }
}
