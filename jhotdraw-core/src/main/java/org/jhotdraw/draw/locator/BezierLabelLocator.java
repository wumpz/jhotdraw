/*
 * @(#)PolyLineDecorationLocator.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.locator;

import java.awt.geom.*;
import org.jhotdraw.draw.figure.BezierFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.Origin;
import org.jhotdraw.draw.figure.Rotation;
import org.jhotdraw.geom.Dimension2DDouble;

/**
 * A {@link Locator} which can be used to place a label on the path of a {@link BezierFigure}.
 *
 * <p>The point is located at a distance and an angle relative to the total length of the bezier
 * path.
 *
 * <p>XXX - The angle should be perpendicular to the path.
 */
public class BezierLabelLocator implements Locator {

  private double relativePosition;
  private double angle;
  private double distance;

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
    this.relativePosition = relativePosition;
    this.angle = angle;
    this.distance = distance;
  }

  @Override
  public Locator.Position locate(Figure owner, double scale) {
    return getRelativePoint((BezierFigure) owner, scale);
  }

  public double getRelativePosition() {
    return relativePosition;
  }

  public double getAngle() {
    return angle;
  }

  public double getDistance() {
    return distance;
  }

  public void setRelativePosition(double relativePosition) {
    this.relativePosition = relativePosition;
  }

  public void setAngle(double angle) {
    this.angle = angle;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  @Override
  public Locator.Position locate(Figure owner, Figure label, double scale) {
    return getRelativeLabelPoint((BezierFigure) owner, label, scale);
  }

  /** Returns the coordinates of the relative point on the path of the specified bezier figure. */
  public Locator.Position getRelativePoint(BezierFigure owner, double scale) {
    Point2D.Double point = owner.getPointOnPath(relativePosition, 3);
    Point2D.Double nextPoint = owner.getPointOnPath(
        (relativePosition < 0.5) ? relativePosition + 0.1d : relativePosition - 0.1d, 3);
    double dir = Math.atan2(nextPoint.y - point.y, nextPoint.x - point.x);
    if (relativePosition >= 0.5) {
      dir += Math.PI;
    }
    double alpha = dir + angle;
    Point2D.Double p = new Point2D.Double(
        point.x + distance / scale * Math.cos(alpha), point.y + distance / scale * Math.sin(alpha));
    if (Double.isNaN(p.x)) {
      p = point;
    }
    return new Position(p, dir);
  }

  /**
   * Returns a Point2D.Double on the polyline that is at the provided relative position. XXX -
   * Implement this and move it to BezierPath
   */
  public Locator.Position getRelativeLabelPoint(BezierFigure owner, Figure label, double scale) {
    // Get a point on the path an the next point on the path
    Point2D.Double point = owner.getPointOnPath(relativePosition, 3);
    Position position = getRelativePoint(owner, scale);

    // If there is a fixed origin, this locator should move the origin the the boundary midth.
    // This should then do the label component.
    if ((label instanceof Origin) && (label instanceof Rotation)) {
      return position;
    }

    Point2D.Double p = position.location();

    Dimension2DDouble labelDim = label.getPreferredSize(scale);
    if (relativePosition == 0.5 && p.x >= point.x - distance / 2 && p.x <= point.x + distance / 2) {
      if (p.y >= point.y) {
        // South East
        return new Position(new Point2D.Double(p.x - labelDim.width / 2, p.y), position.angle());
      } else {
        // North East
        return new Position(
            new Point2D.Double(p.x - labelDim.width / 2, p.y - labelDim.height), position.angle());
      }
    } else {
      if (p.x >= point.x) {
        if (p.y >= point.y) {
          // South East
          return new Position(new Point2D.Double(p.x, p.y), position.angle());
        } else {
          // North East
          return new Position(new Point2D.Double(p.x, p.y - labelDim.height), position.angle());
        }
      } else {
        if (p.y >= point.y) {
          // South West
          return new Position(new Point2D.Double(p.x - labelDim.width, p.y), position.angle());
        } else {
          // North West
          return new Position(
              new Point2D.Double(p.x - labelDim.width, p.y - labelDim.height), position.angle());
        }
      }
    }
  }
}
