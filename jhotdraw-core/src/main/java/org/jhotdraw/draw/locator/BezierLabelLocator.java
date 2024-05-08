/*
 * @(#)PolyLineDecorationLocator.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.locator;

import org.jhotdraw.draw.figure.BezierFigure;
import org.jhotdraw.draw.figure.Figure;

/**
 * A {@link Locator} which can be used to place a label on the path of a {@link BezierFigure}.
 *
 * <p>The point is located at a distance and an angle relative to the total length of the bezier
 * path.
 *
 * <p>XXX - The angle should be perpendicular to the path.
 */
public class BezierLabelLocator extends PathLabelLocator {
  /** For write/read. */
  public BezierLabelLocator() {}

  /**
   * Creates a new locator.
   *
   * @param relativePosition The relative position of the label on the polyline. 0.0 specifies the
   *     start of the bezier path, 1.0 the end of the polyline. Values between 0.0 and 1.0 are
   *     relative positions on the bezier path.
   * @param angle The angle of the distance vector.
   * @param distance The length of the distance vector.
   */
  public BezierLabelLocator(double relativePosition, double angle, double distance) {
    super(relativePosition, angle, distance);
  }

  @Override
  public Locator.Position locate(Figure owner, double scale) {
    return getRelativePoint(
        (double relative, double tolerance) -> ((BezierFigure) owner).getPointOnPath(relative, 3),
        scale);
  }

  @Override
  public Locator.Position locate(Figure owner, Figure label, double scale) {
    return getRelativeLabelPoint(
        (double relative, double tolerance) -> ((BezierFigure) owner).getPointOnPath(relative, 3),
        label,
        scale);
  }
}
