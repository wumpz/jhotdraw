/*
 * @(#)SVGEllipse.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg.figures;

import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.TRANSFORM;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.ResizeHandleKit;
import org.jhotdraw.draw.handle.TransformHandleKit;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.geom.GrowStroke;
import org.jhotdraw.samples.svg.Gradient;
import org.jhotdraw.samples.svg.SVGAttributeKeys;

/** SVGEllipse represents a SVG ellipse and a SVG circle element. */
public class SVGEllipseFigure extends SVGAttributedFigure implements SVGFigure {

  private static final long serialVersionUID = 1L;
  private Ellipse2D.Double ellipse;

  /** This is used to perform faster drawing and hit testing. */
  private transient Shape cachedTransformedShape;

  /** This is used to perform faster hit testing. */
  private transient Shape cachedHitShape;

  public SVGEllipseFigure() {
    this(0, 0, 0, 0);
  }

  public SVGEllipseFigure(double x, double y, double width, double height) {
    ellipse = new Ellipse2D.Double(x, y, width, height);
    SVGAttributeKeys.setDefaults(this);
    setConnectable(false);
  }

  // DRAWING
  @Override
  protected void drawFill(Graphics2D g) {
    if (ellipse.width > 0 && ellipse.height > 0) {
      g.fill(ellipse);
    }
  }

  @Override
  protected void drawStroke(Graphics2D g) {
    if (ellipse.width > 0 && ellipse.height > 0) {
      g.draw(ellipse);
    }
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
      double g = SVGAttributeKeys.getPerpendicularHitGrowth(this, 1.0) * 2d + 1;
      Geom.grow(r, g, g);
    } else {
      double strokeTotalWidth = AttributeKeys.getStrokeTotalWidth(this, 1.0);
      double width = strokeTotalWidth / 2d;
      width *= Math.max(attr().get(TRANSFORM).getScaleX(), attr().get(TRANSFORM).getScaleY()) + 1;
      Geom.grow(r, width, width);
    }
    return r;
  }

  /** Checks if a Point2D.Double is inside the figure. */
  @Override
  public boolean contains(Point2D.Double p, double scaleDenominator) {
    return getHitShape().contains(p);
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

  private Shape getHitShape() {
    if (cachedHitShape == null) {
      if (attr().get(FILL_COLOR) != null || attr().get(FILL_GRADIENT) != null) {
        cachedHitShape = new GrowStroke(
                (float) SVGAttributeKeys.getStrokeTotalWidth(this, 1.0) / 2f,
                (float) SVGAttributeKeys.getStrokeTotalMiterLimit(this, 1.0))
            .createStrokedShape(getTransformedShape());
      } else {
        cachedHitShape =
            SVGAttributeKeys.getHitStroke(this, 1.0).createStrokedShape(getTransformedShape());
      }
    }
    return cachedHitShape;
  }

  @Override
  public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
    ellipse.x = Math.min(anchor.x, lead.x);
    ellipse.y = Math.min(anchor.y, lead.y);
    ellipse.width = Math.max(0.1, Math.abs(lead.x - anchor.x));
    ellipse.height = Math.max(0.1, Math.abs(lead.y - anchor.y));
    invalidate();
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
      case -1: // Mouse hover handles
        handles.add(new BoundsOutlineHandle(this, false, true));
        break;
      case 0:
        ResizeHandleKit.addResizeHandles(this, handles);
        handles.add(new LinkHandle(this));
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
  // COMPOSITE FIGURES
  // CLONING
  @Override
  public SVGEllipseFigure clone() {
    SVGEllipseFigure that = (SVGEllipseFigure) super.clone();
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
    cachedHitShape = null;
  }
}
