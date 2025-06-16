/*
 * @(#)EllipseFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Collection;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.connector.ChopEllipseConnector;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.ResizeHandleKit;
import org.jhotdraw.utils.geom.Geom;

/** A {@link Figure} with an elliptic shape. */
public class EllipseFigure extends AbstractAttributedFigure {

  private static final long serialVersionUID = 1L;
  protected Ellipse2D.Double ellipse;

  /**
   * Constructs a new {@code EllipseFigure}, initialized to location (0,&nbsp;0) and size
   * (0,&nbsp;0).
   */
  public EllipseFigure() {
    this(0, 0, 0, 0);
  }

  /**
   * Constructs and initializes an {@code EllipseFigure} from the specified coordinates.
   *
   * @param x the x coordinate of the bounding rectangle
   * @param y the y coordinate of the bounding rectangle
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   */
  public EllipseFigure(double x, double y, double width, double height) {
    ellipse = new Ellipse2D.Double(x, y, width, height);
  }

  @Override
  public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
    return new ChopEllipseConnector(this);
  }

  @Override
  public Connector findCompatibleConnector(Connector c, boolean isStartConnector) {
    return new ChopEllipseConnector(this);
  }

  @Override
  public Rectangle2D.Double getBounds(double scale) {
    return (Rectangle2D.Double) ellipse.getBounds2D();
  }

  @Override
  public Rectangle2D.Double getDrawingArea(double scale) {
    Rectangle2D.Double r = (Rectangle2D.Double) ellipse.getBounds2D();
    double grow = AttributeKeys.getPerpendicularHitGrowth(this, scale);
    Geom.grow(r, grow + 1, grow + 1);
    return r;
  }

  @Override
  protected void drawFill(Graphics2D g) {
    Ellipse2D.Double r = (Ellipse2D.Double) ellipse.clone();
    double grow =
        AttributeKeys.getPerpendicularFillGrowth(this, AttributeKeys.getScaleFactorFromGraphics(g));
    r.x -= grow;
    r.y -= grow;
    r.width += grow * 2;
    r.height += grow * 2;
    if (r.width > 0 && r.height > 0) {
      g.fill(r);
    }
  }

  @Override
  protected void drawStroke(Graphics2D g) {
    Ellipse2D.Double r = (Ellipse2D.Double) ellipse.clone();
    double grow =
        AttributeKeys.getPerpendicularDrawGrowth(this, AttributeKeys.getScaleFactorFromGraphics(g));
    r.x -= grow;
    r.y -= grow;
    r.width += grow * 2;
    r.height += grow * 2;
    if (r.width > 0 && r.height > 0) {
      g.draw(r);
    }
  }

  /** Checks if a Point2D.Double is inside the figure. */
  @Override
  public boolean contains(Point2D.Double p, double scaleDenominator) {
    Ellipse2D.Double r = (Ellipse2D.Double) ellipse.clone();
    double grow = AttributeKeys.getPerpendicularHitGrowth(this, scaleDenominator);
    r.x -= grow;
    r.y -= grow;
    r.width += grow * 2;
    r.height += grow * 2;
    return r.contains(p);
  }

  @Override
  public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
    ellipse.x = Math.min(anchor.x, lead.x);
    ellipse.y = Math.min(anchor.y, lead.y);
    ellipse.width = Math.max(0.1, Math.abs(lead.x - anchor.x));
    ellipse.height = Math.max(0.1, Math.abs(lead.y - anchor.y));
  }

  /**
   * Transforms the figure.
   *
   * @param tx the transformation.
   */
  @Override
  public void transform(AffineTransform tx) {
    Point2D.Double anchor = getStartPoint();
    Point2D.Double lead = getEndPoint();
    setBounds(
        (Point2D.Double) tx.transform(anchor, anchor), (Point2D.Double) tx.transform(lead, lead));
  }

  @Override
  public EllipseFigure clone() {
    EllipseFigure that = (EllipseFigure) super.clone();
    that.ellipse = (Ellipse2D.Double) this.ellipse.clone();
    return that;
  }

  @Override
  public void restoreTransformTo(Object geometry) {
    Ellipse2D.Double e = (Ellipse2D.Double) geometry;
    ellipse.x = e.x;
    ellipse.y = e.y;
    ellipse.width = e.width;
    ellipse.height = e.height;
  }

  @Override
  public Object getTransformRestoreData() {
    return ellipse.clone();
  }

  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    java.util.List<Handle> handles = new ArrayList<>();
    switch (detailLevel) {
      case -1:
        handles.add(new BoundsOutlineHandle(this, false, true));
        break;
      case 0:
        ResizeHandleKit.addResizeHandles(this, handles, true);
        break;
    }
    return handles;
  }
}
