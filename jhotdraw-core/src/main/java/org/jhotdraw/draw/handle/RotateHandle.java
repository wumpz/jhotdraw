/*
 * @(#)RotateHandle.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import java.awt.Point;
import java.awt.geom.*;
import javax.swing.undo.AbstractUndoableEdit;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.Origin;
import org.jhotdraw.draw.figure.Rotation;

/**
 * A Handle to rotate a Figure. If the Figure implements Origin, this point is returned instead of the center.
 */
public class RotateHandle extends AbstractRotateHandle {

  public RotateHandle(Figure owner) {
    super(owner);
  }

  @Override
  protected Point2D.Double getCenter() {
    if (getOwner() instanceof Origin origin) {
      return origin.getOrigin();
    } else {
      Rectangle2D.Double bounds = getTransformedBounds();
      return new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
    }
  }

  @Override
  protected Point2D.Double getDrawingLocation() {
    // This handle is placed above the figure.
    // We move it up by a handlesizes, so that it won't overlap with
    // the handles from TransformHandleKit.
    Rectangle2D.Double bounds = getTransformedBounds();
    Point2D.Double origin = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
    return origin;
  }

  @Override
  public void trackEnd(Point anchor, Point lead, int modifiersEx) {
    if (getOwner() instanceof Rotation rotation) {
      double startAngle = this.getStartTheta();
      double actualAngle = rotation.getAngle();

      view.getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
        @Override
        public String getPresentationName() {
          return "Rotation";
        }

        @Override
        public void undo() {
          super.undo();
          rotation.setAngle(startAngle);
          fireAreaInvalidated(getDrawingArea());
        }

        @Override
        public void redo() {
          super.redo();
          rotation.setAngle(actualAngle);
          fireAreaInvalidated(getDrawingArea());
        }
      });
      fireAreaInvalidated(getDrawingArea());
      // location = null;
      invalidate();
      fireAreaInvalidated(getDrawingArea());
    } else super.trackEnd(anchor, lead, modifiersEx);
  }
}
