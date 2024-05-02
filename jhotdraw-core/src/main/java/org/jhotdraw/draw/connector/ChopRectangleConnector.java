/*
 * @(#)ChopRectangleConnector.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.connector;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.geom.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.ConnectionFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.geom.Geom;

/**
 * A {@link Connector} which locates a connection point at the bounds of any figure which has a
 * rectangular shape, such as {@link org.jhotdraw.draw.RectangleFigure}.
 *
 * @see Connector
 */
public class ChopRectangleConnector extends AbstractConnector {

  private static final long serialVersionUID = 1L;

  public ChopRectangleConnector() {}

  public ChopRectangleConnector(Figure owner) {
    super(owner);
  }

  @Override
  public Point2D.Double findStart(ConnectionFigure connection) {
    Figure startFigure = connection.getStartConnector().getOwner();
    Point2D.Double from;
    if (connection.getNodeCount() <= 2 || connection.getLiner() != null) {
      if (connection.getEndConnector() == null) {
        from = connection.getEndPoint();
      } else {
        Rectangle2D.Double r1 =
            getConnectorTarget(connection.getEndConnector().getOwner()).getBounds();
        from = new Point2D.Double(r1.x + r1.width / 2, r1.y + r1.height / 2);
      }
    } else {
      from = connection.getPoint(1);
    }
    return chop(startFigure, from);
  }

  @Override
  public Point2D.Double findEnd(ConnectionFigure connection) {
    Figure endFigure = connection.getEndConnector().getOwner();
    Point2D.Double from;
    if (connection.getNodeCount() <= 3 && connection.getStartFigure() == connection.getEndFigure()
        || connection.getNodeCount() <= 2
        || connection.getLiner() != null) {
      if (connection.getStartConnector() == null) {
        from = connection.getStartPoint();
      } else if (connection.getStartFigure() == connection.getEndFigure()) {
        Rectangle2D.Double r1 =
            getConnectorTarget(connection.getStartConnector().getOwner()).getBounds();
        from = new Point2D.Double(r1.x + r1.width / 2, r1.y);
      } else {
        Rectangle2D.Double r1 =
            getConnectorTarget(connection.getStartConnector().getOwner()).getBounds();
        from = new Point2D.Double(r1.x + r1.width / 2, r1.y + r1.height / 2);
      }
    } else {
      from = connection.getPoint(connection.getNodeCount() - 2);
    }
    return chop(endFigure, from);
  }

  protected Point2D.Double chop(Figure target, Point2D.Double from) {
    target = getConnectorTarget(target);
    Rectangle2D.Double r = target.getBounds();
    if (target.attr().get(STROKE_COLOR) != null) {
      double grow;
      switch (target.attr().get(STROKE_PLACEMENT)) {
        case CENTER:
        default:
          grow = AttributeKeys.getStrokeTotalWidth(target, AttributeKeys.scaleFromContext(target))
              / 2d;
          break;
        case OUTSIDE:
          grow = AttributeKeys.getStrokeTotalWidth(target, AttributeKeys.scaleFromContext(target));
          break;
        case INSIDE:
          grow = 0d;
          break;
      }
      Geom.grow(r, grow, grow);
    }
    return Geom.angleToPoint(r, Geom.pointToAngle(r, from));
  }
}
