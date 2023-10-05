/*
 * @(#)AbstractLineDecoration.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 *
 */
package org.jhotdraw.draw.decoration;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.geom.Geom;

/** This abstract class can be extended to implement a {@link LineDecoration}. */
public abstract class AbstractLineDecoration implements LineDecoration {

  private static final long serialVersionUID = 1L;
  /** If this is true, the decoration is filled. */
  private boolean isFilled;
  /** If this is true, the decoration is stroked. */
  private boolean isStroked;
  /** If this is true, the stroke color is used to fill the decoration. */
  private boolean isSolid;

  /** Constructs an arrow tip with the given angle and radius. */
  public AbstractLineDecoration(boolean isFilled, boolean isStroked, boolean isSolid) {
    this.isFilled = isFilled;
    this.isStroked = isStroked;
    this.isSolid = isSolid;
  }

  public boolean isFilled() {
    return isFilled;
  }

  public boolean isStroked() {
    return isStroked;
  }

  public boolean isSolid() {
    return isSolid;
  }

  /** Draws the arrow tip in the direction specified by the given two Points. (template method) */
  @Override
  public void draw(Graphics2D g, Figure f, Point2D.Double p1, Point2D.Double p2) {
    Path2D.Double path =
        getTransformedDecoratorPath(
            f,
            p1,
            p2,
            AttributeKeys.getGlobalValueFactor(f, AttributeKeys.getScaleFactorFromGraphics(g)));
    Color color;
    if (isFilled) {
      if (isSolid) {
        color = f.attr().get(STROKE_COLOR);
      } else {
        color = f.attr().get(FILL_COLOR);
      }
      if (color != null) {
        g.setColor(color);
        g.fill(path);
      }
    }
    if (isStroked) {
      color = f.attr().get(STROKE_COLOR);
      if (color != null) {
        g.setColor(color);
        g.setStroke(AttributeKeys.getStroke(f, AttributeKeys.getScaleFactorFromGraphics(g)));
        g.draw(path);
      }
    }
  }

  /** Returns the drawing area of the decorator. */
  @Override
  public Rectangle2D.Double getDrawingArea(
      Figure f, Point2D.Double p1, Point2D.Double p2, double factor) {
    Path2D.Double path =
        getTransformedDecoratorPath(f, p1, p2, AttributeKeys.getGlobalValueFactor(f, factor));
    Rectangle2D b = path.getBounds2D();
    Rectangle2D.Double area =
        new Rectangle2D.Double(b.getX(), b.getY(), b.getWidth(), b.getHeight());
    if (isStroked) {
      double strokeWidth = f.attr().get(STROKE_WIDTH);
      int strokeJoin = f.attr().get(STROKE_JOIN);
      double miterLimit = (f.attr().get(STROKE_MITER_LIMIT) * strokeWidth);
      double grow;
      if (strokeJoin == BasicStroke.JOIN_MITER) {
        grow = (int) (1 + strokeWidth / 2 * miterLimit);
      } else {
        grow = (int) (1 + strokeWidth / 2);
      }
      Geom.grow(area, grow * factor, grow * factor);
    } else {
      Geom.grow(area, factor, factor); // grow due to antialiasing
    }
    return area;
  }

  @Override
  public double getDecorationRadius(Figure f, double factor) {
    double strokeWidth = f.attr().get(STROKE_WIDTH);
    double scaleFactor;
    if (strokeWidth > 1f) {
      scaleFactor = 1d + (strokeWidth - 1d) / 2d;
    } else {
      scaleFactor = 1d;
    }
    scaleFactor *= factor;
    return getDecoratorPathRadius(f) * scaleFactor;
  }

  private Path2D.Double getTransformedDecoratorPath(
      Figure f, Point2D.Double p1, Point2D.Double p2, double factor) {
    Path2D.Double path = getDecoratorPath(f);
    double strokeWidth = f.attr().get(STROKE_WIDTH);
    AffineTransform transform = new AffineTransform();
    transform.translate(p1.x, p1.y);
    transform.rotate(Math.atan2(p1.x - p2.x, p2.y - p1.y));
    // transform.rotate(Math.PI / 2);
    if (strokeWidth > 1f) {
      transform.scale(1d + (strokeWidth - 1d) / 2d, 1d + (strokeWidth - 1d) / 2d);
    }
    transform.scale(factor, factor);
    path.transform(transform);
    return path;
  }

  public void setFilled(boolean b) {
    isFilled = b;
  }

  public void setStroked(boolean b) {
    isStroked = b;
  }

  public void setSolid(boolean b) {
    isSolid = b;
  }

  /** Hook method to calculate the path of the decorator. */
  protected abstract Path2D.Double getDecoratorPath(Figure f);

  /** Hook method to calculate the radius of the decorator path. */
  protected abstract double getDecoratorPathRadius(Figure f);
}
