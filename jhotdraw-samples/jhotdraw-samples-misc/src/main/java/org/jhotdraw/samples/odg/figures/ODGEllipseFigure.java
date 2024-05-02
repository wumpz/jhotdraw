/*
 * @(#)ODGEllipse.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.odg.figures;

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
import org.jhotdraw.geom.Geom;
import org.jhotdraw.samples.odg.Gradient;
import org.jhotdraw.samples.odg.ODGAttributeKeys;

/** ODGEllipse represents a ODG ellipse and a ODG circle element. */
public class ODGEllipseFigure extends ODGAttributedFigure implements ODGFigure {

  private static final long serialVersionUID = 1L;
  private Ellipse2D.Double ellipse;

  /** This is used to perform faster drawing and hit testing. */
  private transient Shape cachedTransformedShape;

  public ODGEllipseFigure() {
    this(0, 0, 0, 0);
  }

  public ODGEllipseFigure(double x, double y, double width, double height) {
    ellipse = new Ellipse2D.Double(x, y, width, height);
    ODGAttributeKeys.setDefaults(this);
  }

  // DRAWING
  @Override
  protected void drawFill(Graphics2D g) {
    g.fill(ellipse);
    // g.fill(getTransformedShape());
  }

  @Override
  protected void drawStroke(Graphics2D g) {
    g.draw(ellipse);
    /*
    if (TRANSFORM.get(this) == null) {
    g.draw(ellipse);
    } else {
    AffineTransform savedTransform = g.getTransform();
    g.transform(TRANSFORM.get(this));
    g.draw(ellipse);
    g.setTransform(savedTransform);
    }*/
  }

  // SHAPE AND BOUNDS
  public double getX() {
    return ellipse.x;
  }

  public double getY() {
    return ellipse.y;
  }

  public double getWidth() {
    return ellipse.getWidth();
  }

  public double getHeight() {
    return ellipse.getHeight();
  }

  @Override
  public Rectangle2D.Double getBounds(double scale) {
    return (Rectangle2D.Double) ellipse.getBounds2D();
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
      width *= Math.max(attr().get(TRANSFORM).getScaleX(), attr().get(TRANSFORM).getScaleY());
      Geom.grow(r, width, width);
    }
    return r;
  }

  /** Checks if a Point2D.Double is inside the figure. */
  @Override
  public boolean contains(Point2D.Double p, double scaleDenominator) {
    // XXX - This does not take the stroke width into account!
    return getTransformedShape().contains(p);
  }

  private Shape getTransformedShape() {
    if (cachedTransformedShape == null) {
      if (attr().get(TRANSFORM) == null) {
        cachedTransformedShape = ellipse;
      } else {
        cachedTransformedShape = attr().get(TRANSFORM).createTransformedShape(ellipse);
      }
    }
    return cachedTransformedShape;
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
    if (attr().get(TRANSFORM) != null
        || (tx.getType() & (AffineTransform.TYPE_TRANSLATION)) != tx.getType()) {
      if (attr().get(TRANSFORM) == null) {
        TRANSFORM.setClone(this, tx);
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
    invalidate();
  }

  @Override
  public void restoreTransformTo(Object geometry) {
    Object[] restoreData = (Object[]) geometry;
    ellipse = (Ellipse2D.Double) ((Ellipse2D.Double) restoreData[0]).clone();
    TRANSFORM.setClone(this, (AffineTransform) restoreData[1]);
    FILL_GRADIENT.setClone(this, (Gradient) restoreData[2]);
    STROKE_GRADIENT.setClone(this, (Gradient) restoreData[3]);
    invalidate();
  }

  @Override
  public Object getTransformRestoreData() {
    return new Object[] {
      ellipse.clone(),
      TRANSFORM.getClone(this),
      FILL_GRADIENT.getClone(this),
      STROKE_GRADIENT.getClone(this)
    };
  }

  // ATTRIBUTES
  // EDITING
  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    LinkedList<Handle> handles = new LinkedList<Handle>();
    switch (detailLevel % 2) {
      case 0:
        ResizeHandleKit.addResizeHandles(this, handles);
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
  public ODGEllipseFigure clone() {
    ODGEllipseFigure that = (ODGEllipseFigure) super.clone();
    that.ellipse = (Ellipse2D.Double) this.ellipse.clone();
    that.cachedTransformedShape = null;
    return that;
  }

  // EVENT HANDLING
  @Override
  public boolean isEmpty() {
    Rectangle2D.Double b = getBounds();
    return b.width <= 0 || b.height <= 0;
  }

  @Override
  public void invalidate() {
    super.invalidate();
    cachedTransformedShape = null;
  }
}
