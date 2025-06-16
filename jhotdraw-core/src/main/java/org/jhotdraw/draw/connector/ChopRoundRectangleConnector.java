/*
 * @(#)ChopRoundRectangleConnector.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.connector;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.geom.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.RoundRectangleFigure;
import org.jhotdraw.utils.geom.Geom;

/**
 * A {@link Connector} which locates a connection point at the bounds of a {@link
 * RoundRectangleFigure}.
 *
 * <p>
 */
public class ChopRoundRectangleConnector extends ChopRectangleConnector {

  private static final long serialVersionUID = 1L;

  public ChopRoundRectangleConnector() {}

  public ChopRoundRectangleConnector(Figure owner) {
    super(owner);
  }

  @Override
  protected Point2D.Double chop(Figure target, Point2D.Double from) {
    target = getConnectorTarget(target);
    RoundRectangleFigure rrf = (RoundRectangleFigure) target;
    Rectangle2D.Double outer = rrf.getBounds();
    double grow;
    switch (target.attr().get(STROKE_PLACEMENT)) {
      case CENTER:
      default:
        grow =
            AttributeKeys.getStrokeTotalWidth(target, AttributeKeys.scaleFromContext(target)) / 2d;
        break;
      case OUTSIDE:
        grow = AttributeKeys.getStrokeTotalWidth(target, AttributeKeys.scaleFromContext(target));
        break;
      case INSIDE:
        grow = 0;
        break;
    }
    Geom.grow(outer, grow, grow);
    Rectangle2D.Double inner = (Rectangle2D.Double) outer.clone();
    double gw = -(rrf.getArcWidth() + grow * 2) / 2;
    double gh = -(rrf.getArcHeight() + grow * 2) / 2;
    inner.x -= gw;
    inner.y -= gh;
    inner.width += gw * 2;
    inner.height += gh * 2;
    Point2D.Double p = Geom.angleToPoint(outer, Geom.pointToAngle(outer, from));
    if (p.x == outer.x || p.x == outer.x + outer.width) {
      p.y = Math.min(Math.max(p.y, inner.y), inner.y + inner.height);
    } else {
      p.x = Math.min(Math.max(p.x, inner.x), inner.x + inner.width);
    }
    return p;
  }
}
