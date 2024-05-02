/*
 * @(#)SVGPathFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg.figures;

import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.PATH_CLOSED;
import static org.jhotdraw.draw.AttributeKeys.STROKE_CAP;
import static org.jhotdraw.draw.AttributeKeys.STROKE_JOIN;
import static org.jhotdraw.draw.AttributeKeys.STROKE_MITER_LIMIT;
import static org.jhotdraw.draw.AttributeKeys.TRANSFORM;
import static org.jhotdraw.draw.AttributeKeys.WINDING_RULE;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.*;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.AttributeKeys.WindingRule;
import org.jhotdraw.draw.figure.AbstractAttributedCompositeFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.TransformHandleKit;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.geom.GrowStroke;
import org.jhotdraw.geom.Shapes;
import org.jhotdraw.samples.svg.Gradient;
import org.jhotdraw.samples.svg.SVGAttributeKeys;
import org.jhotdraw.util.*;

/** SVGPath is a composite Figure which contains one or more SVGBezierFigures as its children. */
public class SVGPathFigure extends AbstractAttributedCompositeFigure implements SVGFigure {

  private static final long serialVersionUID = 1L;

  /** This cached path is used for drawing. */
  private transient Path2D.Double cachedPath;

  // private transient Rectangle2D.Double cachedDrawingArea;
  /** This is used to perform faster hit testing. */
  private transient Shape cachedHitShape;

  public SVGPathFigure() {
    add(new SVGBezierFigure());
    SVGAttributeKeys.setDefaults(this);
  }

  public SVGPathFigure(boolean isEmpty) {
    if (!isEmpty) {
      add(new SVGBezierFigure());
    }
    SVGAttributeKeys.setDefaults(this);
    setConnectable(false);
  }

  @Override
  public void draw(Graphics2D g) {
    double opacity = attr().get(OPACITY);
    opacity = Math.min(Math.max(0d, opacity), 1d);
    if (opacity != 0d) {
      if (opacity != 1d) {
        Rectangle2D.Double drawingArea = getDrawingArea();
        Rectangle2D clipBounds = g.getClipBounds();
        if (clipBounds != null) {
          Rectangle2D.intersect(drawingArea, clipBounds, drawingArea);
        }
        if (!drawingArea.isEmpty()) {
          BufferedImage buf = new BufferedImage(
              Math.max(1, (int) ((2 + drawingArea.width) * g.getTransform().getScaleX())),
              Math.max(1, (int) ((2 + drawingArea.height) * g.getTransform().getScaleY())),
              BufferedImage.TYPE_INT_ARGB);
          Graphics2D gr = buf.createGraphics();
          gr.scale(g.getTransform().getScaleX(), g.getTransform().getScaleY());
          gr.translate((int) -drawingArea.x, (int) -drawingArea.y);
          gr.setRenderingHints(g.getRenderingHints());
          drawFigure(gr);
          gr.dispose();
          Composite savedComposite = g.getComposite();
          g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity));
          g.drawImage(
              buf,
              (int) drawingArea.x,
              (int) drawingArea.y,
              2 + (int) drawingArea.width,
              2 + (int) drawingArea.height,
              null);
          g.setComposite(savedComposite);
        }
      } else {
        drawFigure(g);
      }
    }
  }

  @Override
  public void drawFigure(Graphics2D g) {
    AffineTransform savedTransform = null;
    if (attr().get(TRANSFORM) != null) {
      savedTransform = g.getTransform();
      g.transform(attr().get(TRANSFORM));
    }
    Paint paint = SVGAttributeKeys.getFillPaint(this);
    if (paint != null) {
      g.setPaint(paint);
      drawFill(g);
    }
    paint = SVGAttributeKeys.getStrokePaint(this);
    if (paint != null) {
      g.setPaint(paint);
      g.setStroke(SVGAttributeKeys.getStroke(this, AttributeKeys.getScaleFactorFromGraphics(g)));
      drawStroke(g);
    }
    if (attr().get(TRANSFORM) != null) {
      g.setTransform(savedTransform);
    }
  }

  @Override
  protected void drawChildren(Graphics2D g) {
    // empty
  }

  @Override
  public void drawFill(Graphics2D g) {
    g.fill(getPath());
  }

  @Override
  public void drawStroke(Graphics2D g) {
    g.draw(getPath());
  }

  @Override
  protected void invalidate() {
    super.invalidate();
    cachedPath = null;
    cachedDrawingArea = null;
    cachedHitShape = null;
  }

  protected Path2D.Double getPath() {
    if (cachedPath == null) {
      cachedPath = new Path2D.Double();
      cachedPath.setWindingRule(
          attr().get(WINDING_RULE) == WindingRule.EVEN_ODD
              ? Path2D.Double.WIND_EVEN_ODD
              : Path2D.Double.WIND_NON_ZERO);
      for (Figure child : getChildren()) {
        SVGBezierFigure b = (SVGBezierFigure) child;
        cachedPath.append(b.getBezierPath(), false);
      }
    }
    return cachedPath;
  }

  protected Shape getHitShape() {
    if (cachedHitShape == null) {
      cachedHitShape = getPath();
      if (attr().get(FILL_COLOR) == null && attr().get(FILL_GRADIENT) == null) {
        cachedHitShape =
            SVGAttributeKeys.getHitStroke(this, 1.0).createStrokedShape(cachedHitShape);
      }
    }
    return cachedHitShape;
  }

  // int count;
  @Override
  public Rectangle2D.Double getDrawingArea(double scale) {
    if (cachedDrawingArea == null) {
      double strokeTotalWidth = Math.max(1d, AttributeKeys.getStrokeTotalWidth(this, 1.0));
      double width = strokeTotalWidth / 2d;
      if (attr().get(STROKE_JOIN) == BasicStroke.JOIN_MITER) {
        width *= attr().get(STROKE_MITER_LIMIT);
      } else if (attr().get(STROKE_CAP) != BasicStroke.CAP_BUTT) {
        width += strokeTotalWidth * 2;
      }
      Shape gp = getPath();
      Rectangle2D strokeRect = new Rectangle2D.Double(0, 0, width, width);
      AffineTransform tx = attr().get(TRANSFORM);
      if (tx != null) {
        // We have to use the (rectangular) bounds of the path here,
        // because we draw a rectangular handle over the shape of the figure
        gp = tx.createTransformedShape(gp.getBounds2D());
        strokeRect = tx.createTransformedShape(strokeRect).getBounds2D();
      }
      Rectangle2D rx = gp.getBounds2D();
      Rectangle2D.Double r = (rx instanceof Rectangle2D.Double)
          ? (Rectangle2D.Double) rx
          : new Rectangle2D.Double(rx.getX(), rx.getY(), rx.getWidth(), rx.getHeight());
      Geom.grow(r, strokeRect.getWidth(), strokeRect.getHeight());
      cachedDrawingArea = r;
    }
    return (Rectangle2D.Double) cachedDrawingArea.clone();
  }

  @Override
  public boolean contains(Point2D.Double p) {
    getPath();
    if (attr().get(TRANSFORM) != null) {
      try {
        p = (Point2D.Double) attr().get(TRANSFORM).inverseTransform(p, new Point2D.Double());
      } catch (NoninvertibleTransformException ex) {
        ex.printStackTrace();
      }
    }
    boolean isClosed = getChild(0).attr().get(PATH_CLOSED);
    if (isClosed && attr().get(FILL_COLOR) == null && attr().get(FILL_GRADIENT) == null) {
      return getHitShape().contains(p);
    }
    /*
    return cachedPath.contains(p2);
    */
    double tolerance = Math.max(2f, AttributeKeys.getStrokeTotalWidth(this, 1.0) / 2d);
    if (isClosed || attr().get(FILL_COLOR) != null || attr().get(FILL_GRADIENT) != null) {
      if (getPath().contains(p)) {
        return true;
      }
      double grow = AttributeKeys.getPerpendicularHitGrowth(this, 1.0)
          /** 2d */
          ;
      GrowStroke gs = new GrowStroke(
          grow, (AttributeKeys.getStrokeTotalWidth(this, 1.0) * attr().get(STROKE_MITER_LIMIT)));
      if (gs.createStrokedShape(getPath()).contains(p)) {
        return true;
      } else {
        if (isClosed) {
          return false;
        }
      }
    }
    if (!isClosed) {
      if (Shapes.outlineContains(getPath(), p, tolerance)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
    if (getChildCount() == 1 && getChild(0).getNodeCount() <= 2) {
      SVGBezierFigure b = getChild(0);
      b.setBounds(anchor, lead);
      invalidate();
    } else {
      super.setBounds(anchor, lead);
    }
  }

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
      for (Figure f : getChildren()) {
        f.transform(tx);
      }
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

  @SuppressWarnings("unchecked")
  @Override
  public void restoreTransformTo(Object geometry) {
    invalidate();
    Object[] restoreData = (Object[]) geometry;
    ArrayList<Object> paths = (ArrayList<Object>) restoreData[0];
    for (int i = 0, n = getChildCount(); i < n; i++) {
      getChild(i).restoreTransformTo(paths.get(i));
    }
    TRANSFORM.setClone(this, (AffineTransform) restoreData[1]);
    FILL_GRADIENT.setClone(this, (Gradient) restoreData[2]);
    STROKE_GRADIENT.setClone(this, (Gradient) restoreData[3]);
  }

  @Override
  public Object getTransformRestoreData() {
    ArrayList<Object> paths = new ArrayList<Object>(getChildCount());
    for (int i = 0, n = getChildCount(); i < n; i++) {
      paths.add(getChild(i).getTransformRestoreData());
    }
    return new Object[] {
      paths, TRANSFORM.getClone(this), FILL_GRADIENT.getClone(this), STROKE_GRADIENT.getClone(this)
    };
  }

  @Override
  public boolean isEmpty() {
    for (Figure child : getChildren()) {
      SVGBezierFigure b = (SVGBezierFigure) child;
      if (b.getNodeCount() > 0) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    LinkedList<Handle> handles = new LinkedList<Handle>();
    switch (detailLevel % 2) {
      case -1: // Mouse hover handles
        handles.add(new SVGPathOutlineHandle(this, true));
        break;
      case 0:
        handles.add(new SVGPathOutlineHandle(this));
        for (Figure child : getChildren()) {
          handles.addAll(((SVGBezierFigure) child).createHandles(this, detailLevel));
        }
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

  @Override
  public Collection<Action> getActions(Point2D.Double p) {
    final ResourceBundleUtil labels =
        ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
    LinkedList<Action> actions = new LinkedList<Action>();
    if (attr().get(TRANSFORM) != null) {
      actions.add(new AbstractAction(labels.getString("edit.removeTransform.text")) {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent evt) {
          willChange();
          fireUndoableEditHappened(TRANSFORM.setUndoable(SVGPathFigure.this, null));
          changed();
        }
      });
      actions.add(new AbstractAction(labels.getString("edit.flattenTransform.text")) {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent evt) {
          // CompositeEdit edit = new CompositeEdit(labels.getString("flattenTransform"));
          // TransformEdit edit = new TransformEdit(SVGPathFigure.this, )
          final Object restoreData = getTransformRestoreData();
          UndoableEdit edit = new AbstractUndoableEdit() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getPresentationName() {
              return labels.getString("edit.flattenTransform.text");
            }

            @Override
            public void undo() throws CannotUndoException {
              super.undo();
              willChange();
              restoreTransformTo(restoreData);
              changed();
            }

            @Override
            public void redo() throws CannotRedoException {
              super.redo();
              willChange();
              restoreTransformTo(restoreData);
              flattenTransform();
              changed();
            }
          };
          willChange();
          flattenTransform();
          changed();
          fireUndoableEditHappened(edit);
        }
      });
    }
    if (getChild(getChildCount() - 1).attr().get(PATH_CLOSED)) {
      actions.add(new AbstractAction(labels.getString("attribute.openPath.text")) {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent evt) {
          willChange();
          for (Figure child : getChildren()) {
            getDrawing().fireUndoableEditHappened(PATH_CLOSED.setUndoable(child, false));
          }
          changed();
        }
      });
    } else {
      actions.add(new AbstractAction(labels.getString("attribute.closePath.text")) {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent evt) {
          willChange();
          for (Figure child : getChildren()) {
            getDrawing().fireUndoableEditHappened(PATH_CLOSED.setUndoable(child, true));
          }
          changed();
        }
      });
    }
    if (attr().get(WINDING_RULE) != WindingRule.EVEN_ODD) {
      actions.add(new AbstractAction(labels.getString("attribute.windingRule.evenOdd.text")) {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent evt) {
          willChange();
          getDrawing()
              .fireUndoableEditHappened(
                  WINDING_RULE.setUndoable(SVGPathFigure.this, WindingRule.EVEN_ODD));
          changed();
        }
      });
    } else {
      actions.add(new AbstractAction(labels.getString("attribute.windingRule.nonZero.text")) {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent evt) {
          willChange();
          attr().set(WINDING_RULE, WindingRule.NON_ZERO);
          changed();
          getDrawing()
              .fireUndoableEditHappened(
                  WINDING_RULE.setUndoable(SVGPathFigure.this, WindingRule.NON_ZERO));
        }
      });
    }
    return actions;
  }

  // CONNECTING
  // EDITING
  /** Handles a mouse click. */
  @Override
  public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
    if (evt.getClickCount() == 2 && view.getHandleDetailLevel() % 2 == 0) {
      for (Figure child : getChildren()) {
        SVGBezierFigure bf = (SVGBezierFigure) child;
        int index = bf.findSegment(p, 5f / view.getScaleFactor());
        if (index != -1) {
          bf.handleMouseClick(p, evt, view);
          evt.consume();
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void add(final int index, final Figure figure) {
    super.add(index, (SVGBezierFigure) figure);
  }

  @Override
  public SVGBezierFigure getChild(int index) {
    return (SVGBezierFigure) super.getChild(index);
  }

  @Override
  public SVGPathFigure clone() {
    SVGPathFigure that = (SVGPathFigure) super.clone();
    return that;
  }

  public void flattenTransform() {
    willChange();
    AffineTransform tx = attr().get(TRANSFORM);
    if (tx != null) {
      for (Figure child : getChildren()) {
        // ((SVGBezierFigure) child).transform(tx);
        ((SVGBezierFigure) child).flattenTransform();
      }
    }
    if (attr().get(FILL_GRADIENT) != null) {
      attr().get(FILL_GRADIENT).transform(tx);
    }
    if (attr().get(STROKE_GRADIENT) != null) {
      attr().get(STROKE_GRADIENT).transform(tx);
    }
    attr().set(TRANSFORM, null);
    changed();
  }
}
