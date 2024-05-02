/*
 * @(#)ODGRect.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.odg.figures;

import static org.jhotdraw.draw.AttributeKeys.STROKE_CAP;
import static org.jhotdraw.draw.AttributeKeys.STROKE_JOIN;
import static org.jhotdraw.draw.AttributeKeys.STROKE_MITER_LIMIT;
import static org.jhotdraw.draw.AttributeKeys.TRANSFORM;
import static org.jhotdraw.samples.odg.ODGAttributeKeys.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.figure.ConnectionFigure;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.ResizeHandleKit;
import org.jhotdraw.draw.handle.TransformHandleKit;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.geom.GrowStroke;
import org.jhotdraw.samples.odg.Gradient;
import org.jhotdraw.samples.odg.ODGAttributeKeys;

/** ODGRect. */
public class ODGRectFigure extends ODGAttributedFigure implements ODGFigure {

  private static final long serialVersionUID = 1L;
  private RoundRectangle2D.Double roundrect;

  /** This is used to perform faster drawing. */
  private transient Shape cachedTransformedShape;

  /** This is used to perform faster hit testing. */
  private transient Shape cachedHitShape;

  public ODGRectFigure() {
    this(0, 0, 0, 0);
  }

  public ODGRectFigure(double x, double y, double width, double height) {
    this(x, y, width, height, 0, 0);
  }

  public ODGRectFigure(double x, double y, double width, double height, double rx, double ry) {
    roundrect = new RoundRectangle2D.Double(x, y, width, height, rx, ry);
    ODGAttributeKeys.setDefaults(this);
  }

  // DRAWING
  @Override
  protected void drawFill(Graphics2D g) {
    if (getArcHeight() == 0d && getArcWidth() == 0d) {
      g.fill(roundrect.getBounds2D());
    } else {
      g.fill(roundrect);
    }
  }

  @Override
  protected void drawStroke(Graphics2D g) {
    if (getArcHeight() == 0d && getArcWidth() == 0d) {
      g.draw(roundrect.getBounds2D());
    } else {
      g.draw(roundrect);
    }
  }

  // SHAPE AND BOUNDS
  public double getX() {
    return roundrect.x;
  }

  public double getY() {
    return roundrect.y;
  }

  public double getWidth() {
    return roundrect.width;
  }

  public double getHeight() {
    return roundrect.height;
  }

  public double getArcWidth() {
    return roundrect.arcwidth / 2d;
  }

  public double getArcHeight() {
    return roundrect.archeight / 2d;
  }

  @Override
  public Rectangle2D.Double getBounds(double scale) {
    return (Rectangle2D.Double) roundrect.getBounds2D();
  }

  @Override
  public Rectangle2D.Double getDrawingArea(double scale) {
    Rectangle2D rx = getTransformedShape().getBounds2D();
    Rectangle2D.Double r = (rx instanceof Rectangle2D.Double)
        ? (Rectangle2D.Double) rx
        : new Rectangle2D.Double(rx.getX(), rx.getY(), rx.getWidth(), rx.getHeight());
    if (attr().get(TRANSFORM) == null) {
      double g = ODGAttributeKeys.getPerpendicularHitGrowth(this, 1.0) * 2;
      Geom.grow(r, g, g);
    } else {
      double strokeTotalWidth = AttributeKeys.getStrokeTotalWidth(this, 1.0);
      double width = strokeTotalWidth / 2d;
      if (attr().get(STROKE_JOIN) == BasicStroke.JOIN_MITER) {
        width *= attr().get(STROKE_MITER_LIMIT);
      }
      if (attr().get(STROKE_CAP) != BasicStroke.CAP_BUTT) {
        width += strokeTotalWidth * 2;
      }
      width++;
      Geom.grow(r, width, width);
    }
    return r;
  }

  /** Checks if a Point2D.Double is inside the figure. */
  @Override
  public boolean contains(Point2D.Double p, double scaleDenominator) {
    return getHitShape().contains(p);
  }

  @Override
  public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
    invalidateTransformedShape();
    roundrect.x = Math.min(anchor.x, lead.x);
    roundrect.y = Math.min(anchor.y, lead.y);
    roundrect.width = Math.max(0.1, Math.abs(lead.x - anchor.x));
    roundrect.height = Math.max(0.1, Math.abs(lead.y - anchor.y));
  }

  private void invalidateTransformedShape() {
    cachedTransformedShape = null;
    cachedHitShape = null;
  }

  private Shape getTransformedShape() {
    if (cachedTransformedShape == null) {
      if (getArcHeight() == 0 || getArcWidth() == 0) {
        cachedTransformedShape = roundrect.getBounds2D();
      } else {
        cachedTransformedShape = (Shape) roundrect.clone();
      }
      if (attr().get(TRANSFORM) != null) {
        cachedTransformedShape =
            attr().get(TRANSFORM).createTransformedShape(cachedTransformedShape);
      }
    }
    return cachedTransformedShape;
  }

  private Shape getHitShape() {
    if (cachedHitShape == null) {
      cachedHitShape = new GrowStroke(
              (float) ODGAttributeKeys.getStrokeTotalWidth(this, 1.0) / 2f,
              (float) ODGAttributeKeys.getStrokeTotalMiterLimit(this, 1.0))
          .createStrokedShape(getTransformedShape());
    }
    return cachedHitShape;
  }

  /**
   * Transforms the figure.
   *
   * @param tx The transformation.
   */
  @Override
  public void transform(AffineTransform tx) {
    invalidateTransformedShape();
    if (attr().get(TRANSFORM) != null
        || //              (tx.getType() & (AffineTransform.TYPE_TRANSLATION |
        // AffineTransform.TYPE_MASK_SCALE)) != tx.getType()) {
        (tx.getType() & (AffineTransform.TYPE_TRANSLATION)) != tx.getType()) {
      if (attr().get(TRANSFORM) == null) {
        attr().set(TRANSFORM, (AffineTransform) tx.clone());
      } else {
        AffineTransform t = TRANSFORM.getClone(this);
        t.preConcatenate(tx);
        attr().set(TRANSFORM, t);
      }
    } else {
      Point2D.Double anchor = getStartPoint();
      Point2D.Double lead = getEndPoint();
      setBounds(
          (Point2D.Double) tx.transform(anchor, anchor), (Point2D.Double) tx.transform(lead, lead));
      if (attr().get(FILL_GRADIENT) != null
          && !attr().get(FILL_GRADIENT).isRelativeToFigureBounds()) {
        Gradient g = FILL_GRADIENT.getClone(this);
        g.transform(tx);
        attr().set(FILL_GRADIENT, g);
      }
      if (attr().get(STROKE_GRADIENT) != null
          && !attr().get(STROKE_GRADIENT).isRelativeToFigureBounds()) {
        Gradient g = STROKE_GRADIENT.getClone(this);
        g.transform(tx);
        attr().set(STROKE_GRADIENT, g);
      }
    }
  }

  // ATTRIBUTES
  public void setArc(double w, double h) {
    roundrect.arcwidth = Math.max(0d, Math.min(roundrect.width, w * 2d));
    roundrect.archeight = Math.max(0d, Math.min(roundrect.height, h * 2d));
  }

  public void setArc(Dimension2DDouble arc) {
    roundrect.arcwidth = Math.max(0d, Math.min(roundrect.width, arc.width * 2d));
    roundrect.archeight = Math.max(0d, Math.min(roundrect.height, arc.height * 2d));
  }

  public Dimension2DDouble getArc() {
    return new Dimension2DDouble(roundrect.arcwidth / 2d, roundrect.archeight / 2d);
  }

  @Override
  public void restoreTransformTo(Object geometry) {
    invalidateTransformedShape();
    Object[] restoreData = (Object[]) geometry;
    roundrect = (RoundRectangle2D.Double) ((RoundRectangle2D.Double) restoreData[0]).clone();
    TRANSFORM.setClone(this, (AffineTransform) restoreData[1]);
    FILL_GRADIENT.setClone(this, (Gradient) restoreData[2]);
    STROKE_GRADIENT.setClone(this, (Gradient) restoreData[3]);
  }

  @Override
  public Object getTransformRestoreData() {
    return new Object[] {
      roundrect.clone(),
      TRANSFORM.getClone(this),
      FILL_GRADIENT.getClone(this),
      STROKE_GRADIENT.getClone(this)
    };
  }

  // EDITING
  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    LinkedList<Handle> handles = new LinkedList<Handle>();
    switch (detailLevel % 2) {
      case 0:
        ResizeHandleKit.addResizeHandles(this, handles);
        handles.add(new ODGRectRadiusHandle(this));
        break;
      case 1:
        TransformHandleKit.addTransformHandles(this, handles);
        break;
      default:
        break;
    }
    return handles;
  }

  // CONNECTING
  @Override
  public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
    return null; // ODG does not support connectors
  }

  @Override
  public Connector findCompatibleConnector(Connector c, boolean isStartConnector) {
    return null; // ODG does not support connectors
  }

  // COMPOSITE FIGURES
  // CLONING
  @Override
  public ODGRectFigure clone() {
    ODGRectFigure that = (ODGRectFigure) super.clone();
    that.roundrect = (RoundRectangle2D.Double) this.roundrect.clone();
    that.cachedTransformedShape = null;
    that.cachedHitShape = null;
    return that;
  }

  @Override
  public boolean isEmpty() {
    Rectangle2D.Double b = getBounds();
    return b.width <= 0 || b.height <= 0;
  }

  @Override
  public void invalidate() {
    super.invalidate();
    invalidateTransformedShape();
  }
}
