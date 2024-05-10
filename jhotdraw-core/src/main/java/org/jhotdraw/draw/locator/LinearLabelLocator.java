/*
 * Copyright (C) 2024 JHotDraw.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.jhotdraw.draw.locator;

import java.awt.geom.Point2D;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.Origin;
import org.jhotdraw.draw.figure.Rotation;
import org.jhotdraw.geom.Dimension2DDouble;

/**
 * A {@link Locator} which can be used to place a label on the path of a path like structure. {@link LinearLocatorBase}.
 *
 * <p>The point is located at a distance and an angle relative to the total length of the bezier path.
 *
 * <p>The angle should is perpendicular to the path.
 */
public class LinearLabelLocator implements Locator {

  private double relativePosition;
  private double angle;
  private double distance;

  public LinearLabelLocator() {}

  /**
   * Creates a new locator.
   *
   * @param relativePosition The relative position of the label on the polyline. 0.0 specifies the
   *     start of the bezier path, 1.0 the end of the polyline. Values between 0.0 and 1.0 are
   *     relative positions on the bezier path.
   * @param angle The angle of the distance vector.
   * @param distance The length of the distance vector.
   */
  public LinearLabelLocator(double relativePosition, double angle, double distance) {
    this.relativePosition = relativePosition;
    this.angle = angle;
    this.distance = distance;
  }

  @Override
  public Locator.Position locate(Figure owner, double scale) {
    if (owner instanceof LinearLocatorBase path) return getRelativePoint(path, scale);
    else return returnBoundsCenter(owner);
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
    if (owner instanceof LinearLocatorBase path) return getRelativeLabelPoint(path, label, scale);
    else return returnBoundsCenter(owner);
  }

  /** Returns the coordinates of the relative point on the path of the specified path. */
  protected Locator.Position getRelativePoint(LinearLocatorBase owner, double scale) {
    Point2D.Double point = owner.getPointOnPath(relativePosition, 0.1);
    Point2D.Double nextPoint = owner.getPointOnPath(
        (relativePosition < 0.5) ? relativePosition + 0.1d : relativePosition - 0.1d, 0.1);
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
    return new Locator.Position(p, dir);
  }

  /**
   * Returns a Point2D.Double on the polyline that is at the provided
   */
  protected Locator.Position getRelativeLabelPoint(
      LinearLocatorBase owner, Figure label, double scale) {
    // Get a point on the path an the next point on the path
    Point2D.Double point = owner.getPointOnPath(relativePosition, 0.1);
    Locator.Position position = getRelativePoint(owner, scale);

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
        return new Locator.Position(
            new Point2D.Double(p.x - labelDim.width / 2, p.y), position.angle());
      } else {
        // North East
        return new Locator.Position(
            new Point2D.Double(p.x - labelDim.width / 2, p.y - labelDim.height), position.angle());
      }
    } else {
      if (p.x >= point.x) {
        if (p.y >= point.y) {
          // South East
          return new Locator.Position(new Point2D.Double(p.x, p.y), position.angle());
        } else {
          // North East
          return new Locator.Position(
              new Point2D.Double(p.x, p.y - labelDim.height), position.angle());
        }
      } else {
        if (p.y >= point.y) {
          // South West
          return new Locator.Position(
              new Point2D.Double(p.x - labelDim.width, p.y), position.angle());
        } else {
          // North West
          return new Locator.Position(
              new Point2D.Double(p.x - labelDim.width, p.y - labelDim.height), position.angle());
        }
      }
    }
  }

  public static final Position returnBoundsCenter(Figure owner) {
    var bounds = owner.getBounds();
    return new Locator.Position(new Point2D.Double(bounds.getCenterX(), bounds.getCenterY()), 0);
  }
}
