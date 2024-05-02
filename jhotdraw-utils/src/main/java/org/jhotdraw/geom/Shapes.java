/*
 * @(#)Shapes.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.geom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

/** Shapes. */
public class Shapes {

  private Shapes() {}

  /**
   * Returns true, if the outline of this bezier path contains the specified point.
   *
   * @param p The point to be tested.
   * @param tolerance The tolerance for the test.
   */
  public static boolean outlineContains(Shape shape, Point2D.Double p, double tolerance) {
    double[] coords = new double[6];
    double prevX = 0, prevY = 0;
    double moveX = 0, moveY = 0;
    for (PathIterator i = new FlatteningPathIterator(
            shape.getPathIterator(new AffineTransform(), tolerance), tolerance);
        !i.isDone();
        i.next()) {
      switch (i.currentSegment(coords)) {
        case PathIterator.SEG_CLOSE:
          if (Geom.lineContainsPoint(prevX, prevY, moveX, moveY, p.x, p.y, tolerance)) {
            return true;
          }
          break;
        case PathIterator.SEG_CUBICTO:
          break;
        case PathIterator.SEG_LINETO:
          if (Geom.lineContainsPoint(prevX, prevY, coords[0], coords[1], p.x, p.y, tolerance)) {
            return true;
          }
          break;
        case PathIterator.SEG_MOVETO:
          moveX = coords[0];
          moveY = coords[1];
          break;
        case PathIterator.SEG_QUADTO:
          break;
        default:
          break;
      }
      prevX = coords[0];
      prevY = coords[1];
    }
    return false;
  }
}
