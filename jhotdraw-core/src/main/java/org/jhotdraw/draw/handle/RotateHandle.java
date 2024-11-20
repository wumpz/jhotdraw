/*
 * @(#)RotateHandle.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import java.awt.geom.*;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.Origin;

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
}
