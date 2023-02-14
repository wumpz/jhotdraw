/*
 * @(#)LineFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.undo.*;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.handle.BezierNodeHandle;
import org.jhotdraw.draw.handle.BezierOutlineHandle;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.geom.BezierPath;

public class Path2DFigure extends AbstractAttributedFigure {

  private static final long serialVersionUID = 1L;
  private final Path2D.Double path = new Path2D.Double();

  public Path2DFigure() {
    setConnectable(false);
  }

  // DRAWING
  // SHAPE AND BOUNDS
  // ATTRIBUTES
  // EDITING
  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    if (detailLevel == -1)
      return super.createHandles(detailLevel);
    else {
    LinkedList<Handle> handles = new LinkedList<>();
    switch (detailLevel) {
      case 0:
        handles.add(new BoundsOutlineHandle(this, false, true));
        
      PathIterator pathIterator = path.getPathIterator(null);
      while (!pathIterator.isDone()) {
        handles.add(new )
      }
        
        break;

    }
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
        fireUndoableEditHappened(
            new AbstractUndoableEdit() {
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
