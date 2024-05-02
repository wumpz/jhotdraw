/*
 * @(#)BezierFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import static org.jhotdraw.draw.AttributeKeys.END_DECORATION;
import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.PATH_CLOSED;
import static org.jhotdraw.draw.AttributeKeys.START_DECORATION;
import static org.jhotdraw.draw.AttributeKeys.STROKE_MITER_LIMIT;
import static org.jhotdraw.draw.AttributeKeys.UNCLOSED_PATH_FILLED;
import static org.jhotdraw.draw.AttributeKeys.WINDING_RULE;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.connector.ChopBezierConnector;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.handle.BezierNodeHandle;
import org.jhotdraw.draw.handle.BezierOutlineHandle;
import org.jhotdraw.draw.handle.BezierScaleHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.TransformHandleKit;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.geom.GrowStroke;
import org.jhotdraw.geom.path.BezierPath;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * A {@link Figure} which draws an opened or a closed bezier path.
 *
 * <p>A bezier figure can be used to draw arbitrary shapes using a {@link BezierPath}. It can be
 * used to draw an open path or a closed shape.
 *
 * <p>A BezierFigure can have straight path segments and curved segments. A straight path segment
 * can be added by clicking on the drawing area. Curved segments can be added by dragging the mouse
 * pointer over the drawing area.
 *
 * <p><hr> <b>Design Patterns</b>
 *
 * <p><em>Decorator</em><br>
 * The start and end point of a {@code BezierFigure} can be decorated with a line decoration.<br>
 * Component: {@link BezierFigure}; Decorator: {@link org.jhotdraw.draw.decoration.LineDecoration}.
 * <hr>
 *
 * @see org.jhotdraw.geom.path.BezierPath
 */
public class BezierFigure extends AbstractAttributedFigure {

  private static final long serialVersionUID = 1L;

  protected BezierPath path;

  /**
   * The cappedPath BezierPath is derived from variable path. We cache it to increase the drawing
   * speed of the figure. The factor could influence the cappedPath due to Arrow sizes.
   */
  private transient BezierPath cappedPath;

  private transient double cappedPathFactor;

  /**
   * Creates an empty <code>BezierFigure</code>, for example without any <code>BezierPath.Node
   * </code>s. The BezierFigure will not draw anything, if at least two nodes are added to it. The
   * <code>BezierPath</code> created by this constructor is not closed.
   */
  public BezierFigure() {
    this(false);
  }

  /**
   * Creates an empty BezierFigure, for example without any <code>BezierPath.Node</code>s. The
   * BezierFigure will not draw anything, unless at least two nodes are added to it.
   *
   * @param isClosed Specifies whether the <code>BezierPath</code> shall be closed.
   */
  public BezierFigure(boolean isClosed) {
    path = new BezierPath();
    attr().set(PATH_CLOSED, isClosed);
  }

  /**
   * Returns the Figures connector for the specified location. By default a {@link
   * ChopBezierConnector} is returned.
   */
  @Override
  public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
    return new ChopBezierConnector(this);
  }

  @Override
  public Connector findCompatibleConnector(Connector c, boolean isStart) {
    return new ChopBezierConnector(this);
  }

  // COMPOSITE FIGURES
  // CLONING
  // EVENT HANDLING
  @Override
  protected void drawStroke(Graphics2D g) {
    if (isClosed()) {
      double grow = AttributeKeys.getPerpendicularDrawGrowth(
          this, AttributeKeys.getScaleFactorFromGraphics(g));
      if (grow == 0d) {
        g.draw(path);
      } else {
        GrowStroke gs = new GrowStroke(
            grow,
            AttributeKeys.getStrokeTotalWidth(this, AttributeKeys.getScaleFactorFromGraphics(g))
                * attr().get(STROKE_MITER_LIMIT));
        g.draw(gs.createStrokedShape(path));
      }
    } else {
      g.draw(getCappedPath(AttributeKeys.getScaleFactorFromGraphics(g)));
    }
    drawCaps(g);
  }

  protected void drawCaps(Graphics2D g) {
    if (getNodeCount() > 1) {
      if (attr().get(START_DECORATION) != null) {
        BezierPath cp = getCappedPath(AttributeKeys.getScaleFactorFromGraphics(g));
        Point2D.Double p1 = path.get(0, 0);
        Point2D.Double p2 = cp.get(0, 0);
        if (p2.equals(p1)) {
          p2 = path.get(1, 0);
        }
        attr().get(START_DECORATION).draw(g, this, p1, p2);
      }
      if (attr().get(END_DECORATION) != null) {
        BezierPath cp = getCappedPath(AttributeKeys.getScaleFactorFromGraphics(g));
        Point2D.Double p1 = path.get(path.size() - 1, 0);
        Point2D.Double p2 = cp.get(path.size() - 1, 0);
        if (p2.equals(p1)) {
          p2 = path.get(path.size() - 2, 0);
        }
        attr().get(END_DECORATION).draw(g, this, p1, p2);
      }
    }
  }

  @Override
  protected void drawFill(Graphics2D g) {
    if (isClosed() || attr().get(UNCLOSED_PATH_FILLED)) {
      double grow = AttributeKeys.getPerpendicularFillGrowth(
          this, AttributeKeys.getScaleFactorFromGraphics(g));
      if (grow == 0d) {
        g.fill(path);
      } else {
        GrowStroke gs = new GrowStroke(
            grow,
            AttributeKeys.getStrokeTotalWidth(this, AttributeKeys.getScaleFactorFromGraphics(g))
                * attr().get(STROKE_MITER_LIMIT));
        g.fill(gs.createStrokedShape(path));
      }
    }
  }

  @Override
  public boolean contains(Point2D.Double p, double scaleDenominator) {
    double tolerance =
        Math.max(1f, 2 * AttributeKeys.getPerpendicularHitGrowth(this, scaleDenominator));
    if (isClosed() || attr().get(FILL_COLOR) != null && attr().get(UNCLOSED_PATH_FILLED)) {
      if (path.contains(p)) {
        return true;
      }
      double grow = tolerance;
      GrowStroke gs = new GrowStroke(
          grow,
          AttributeKeys.getStrokeTotalWidth(this, scaleDenominator)
              * attr().get(STROKE_MITER_LIMIT));
      if (gs.createStrokedShape(path).contains(p)) {
        return true;
      } else {
        if (isClosed()) {
          return false;
        }
      }
    }
    if (!isClosed()) {
      if (getCappedPath(scaleDenominator).outlineContains(p, tolerance)) {
        return true;
      }
      if (attr().get(START_DECORATION) != null) {
        BezierPath cp = getCappedPath(scaleDenominator);
        Point2D.Double p1 = path.get(0, 0);
        Point2D.Double p2 = cp.get(0, 0);
        // FIXME - Check here, if caps path contains the point
        if (Geom.lineContainsPoint(p1.x, p1.y, p2.x, p2.y, p.x, p.y, tolerance)) {
          return true;
        }
      }
      if (attr().get(END_DECORATION) != null) {
        BezierPath cp = getCappedPath(scaleDenominator);
        Point2D.Double p1 = path.get(path.size() - 1, 0);
        Point2D.Double p2 = cp.get(path.size() - 1, 0);
        // FIXME - Check here, if caps path contains the point
        if (Geom.lineContainsPoint(p1.x, p1.y, p2.x, p2.y, p.x, p.y, tolerance)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    List<Handle> handles = new ArrayList<>();
    switch (detailLevel % 2) {
      case -1: // Mouse hover handles
        handles.add(new BezierOutlineHandle(this, true));
        break;
      case 0:
        handles.add(new BezierOutlineHandle(this));
        for (int i = 0, n = path.size(); i < n; i++) {
          handles.add(new BezierNodeHandle(this, i));
        }
        break;
      case 1:
        TransformHandleKit.addTransformHandles(this, handles);
        handles.add(new BezierScaleHandle(this));
        break;
    }
    return handles;
  }

  @Override
  public Rectangle2D.Double getBounds(double scale) {
    Rectangle2D.Double bounds = path.getBounds2D();
    return bounds;
  }

  @Override
  public Rectangle2D.Double getDrawingArea(double factor) {
    Rectangle2D.Double r = super.getDrawingArea(factor);
    if (getNodeCount() > 1) {
      if (attr().get(START_DECORATION) != null) {
        Point2D.Double p1 = getPoint(0, 0);
        Point2D.Double p2 = getPoint(1, 0);
        r.add(attr().get(START_DECORATION).getDrawingArea(this, p1, p2, factor));
      }
      if (attr().get(END_DECORATION) != null) {
        Point2D.Double p1 = getPoint(getNodeCount() - 1, 0);
        Point2D.Double p2 = getPoint(getNodeCount() - 2, 0);
        r.add(attr().get(END_DECORATION).getDrawingArea(this, p1, p2, factor));
      }
    }
    return r;
  }

  @Override
  protected void validate() {
    super.validate();
    path.invalidatePath();
    cappedPath = null;
  }

  /** Returns a clone of the bezier path of this figure. */
  public BezierPath getBezierPath() {
    return path.clone();
  }

  public void setBezierPath(BezierPath newValue) {
    path = newValue.clone();
    this.setClosed(newValue.isClosed());
  }

  public Point2D.Double getPointOnPath(double relative, double flatness) {
    return path.getPointOnPath(relative, flatness);
  }

  public boolean isClosed() {
    return attr().get(PATH_CLOSED);
  }

  public void setClosed(boolean newValue) {
    attr().set(PATH_CLOSED, newValue);
    setConnectable(newValue);
  }

  @Override
  protected <T> void fireAttributeChanged(AttributeKey<T> attribute, T oldValue, T newValue) {
    if (attribute == PATH_CLOSED) {
      path.setClosed((Boolean) newValue);
    } else if (attribute == WINDING_RULE) {
      path.setWindingRule(
          newValue == AttributeKeys.WindingRule.EVEN_ODD
              ? Path2D.Double.WIND_EVEN_ODD
              : Path2D.Double.WIND_NON_ZERO);
    }
    invalidate();
    super.fireAttributeChanged(attribute, oldValue, newValue);
  }

  /**
   * Sets the location of the first and the last <code>BezierPath.Node</code> of the BezierFigure.
   * If the BezierFigure has not at least two nodes, nodes are added to the figure until the
   * BezierFigure has at least two nodes.
   */
  @Override
  public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
    setStartPoint(anchor);
    setEndPoint(lead);
    invalidate();
  }

  @Override
  public void transform(AffineTransform tx) {
    path.transform(tx);
    invalidate();
  }

  @Override
  public void invalidate() {
    super.invalidate();
    path.invalidatePath();
    cappedPath = null;
  }

  /**
   * Returns a path which is cappedPath at the ends, to prevent it from drawing under the end caps.
   */
  protected BezierPath getCappedPath(double factor) {
    if (cappedPath == null || factor != cappedPathFactor) {
      cappedPath = path.clone();
      cappedPathFactor = factor;
      if (isClosed()) {
        cappedPath.setClosed(true);
      } else {
        if (cappedPath.size() > 1) {
          if (attr().get(START_DECORATION) != null) {
            BezierPath.Node p0 = cappedPath.nodes().get(0);
            BezierPath.Node p1 = cappedPath.nodes().get(1);
            Point2D.Double pp;
            if ((p0.getMask() & BezierPath.C2_MASK) != 0) {
              pp = p0.getControlPoint(2);
            } else if ((p1.getMask() & BezierPath.C1_MASK) != 0) {
              pp = p1.getControlPoint(1);
            } else {
              pp = p1.getControlPoint(0);
            }
            double radius = attr().get(START_DECORATION).getDecorationRadius(this, factor);
            double lineLength = Geom.length(p0.getControlPoint(0), pp);
            cappedPath.set(
                0, 0, Geom.cap(pp, p0.getControlPoint(0), -Math.min(radius, lineLength)));
          }
          if (attr().get(END_DECORATION) != null) {
            BezierPath.Node p0 = cappedPath.nodes().get(cappedPath.size() - 1);
            BezierPath.Node p1 = cappedPath.nodes().get(cappedPath.size() - 2);
            Point2D.Double pp;
            if ((p0.getMask() & BezierPath.C1_MASK) != 0) {
              pp = p0.getControlPoint(1);
            } else if ((p1.getMask() & BezierPath.C2_MASK) != 0) {
              pp = p1.getControlPoint(2);
            } else {
              pp = p1.getControlPoint(0);
            }
            double radius = attr().get(END_DECORATION).getDecorationRadius(this, factor);
            double lineLength = Geom.length(p0.getControlPoint(0), pp);
            cappedPath.set(
                cappedPath.size() - 1,
                0,
                Geom.cap(pp, p0.getControlPoint(0), -Math.min(radius, lineLength)));
          }
          cappedPath.invalidatePath();
        }
      }
    }
    return cappedPath;
  }

  public void layout() {}

  /** Adds a control point. */
  public void addNode(BezierPath.Node p) {
    addNode(getNodeCount(), p);
  }

  /** Adds a node to the list of points. */
  public void addNode(final int index, BezierPath.Node p) {
    path.add(index, p);
    invalidate();
  }

  /** Sets a control point. */
  public void setNode(int index, BezierPath.Node p) {
    path.set(index, p);
    invalidate();
  }

  /** Gets a control point. */
  public BezierPath.Node getNode(int index) {
    return (BezierPath.Node) path.nodes().get(index).clone();
  }

  /**
   * Convenience method for getting the point coordinate of the first control point of the specified
   * node.
   */
  public Point2D.Double getPoint(int index) {
    return path.nodes().get(index).getControlPoint(0);
  }

  /** Gets the point coordinate of a control point. */
  public Point2D.Double getPoint(int index, int coord) {
    return path.nodes().get(index).getControlPoint(coord);
  }

  /** Sets the point coordinate of control point 0 at the specified node. */
  public void setPoint(int index, Point2D.Double p) {
    BezierPath.Node node = path.nodes().get(index);
    double dx = p.x - node.x[0];
    double dy = p.y - node.y[0];
    for (int i = 0; i < node.x.length; i++) {
      node.x[i] += dx;
      node.y[i] += dy;
    }
    invalidate();
  }

  /** Sets the point coordinate of a control point. */
  public void setPoint(int index, int ctrlPntIndex, Point2D.Double p) {
    BezierPath.Node cp = new BezierPath.Node(path.nodes().get(index));
    cp.setControlPoint(ctrlPntIndex, p);
    setNode(index, cp);
  }

  /**
   * Convenience method for setting the point coordinate of the start point. If the BezierFigure has
   * not at least two nodes, nodes are added to the figure until the BezierFigure has at least two
   * nodes.
   */
  public void setStartPoint(Point2D.Double p) {
    // Add two nodes if we haven't at least two nodes
    for (int i = getNodeCount(); i < 2; i++) {
      addNode(0, new BezierPath.Node(p.x, p.y));
    }
    setPoint(0, p);
  }

  /**
   * Convenience method for setting the point coordinate of the end point. If the BezierFigure has
   * not at least two nodes, nodes are added to the figure until the BezierFigure has at least two
   * nodes.
   */
  public void setEndPoint(Point2D.Double p) {
    // Add two nodes if we haven't at least two nodes
    for (int i = getNodeCount(); i < 2; i++) {
      addNode(0, new BezierPath.Node(p.x, p.y));
    }
    setPoint(getNodeCount() - 1, p);
  }

  /** Convenience method for getting the start point. */
  @Override
  public Point2D.Double getStartPoint() {
    return getPoint(0, 0);
  }

  /** Convenience method for getting the end point. */
  @Override
  public Point2D.Double getEndPoint() {
    return getPoint(getNodeCount() - 1, 0);
  }

  /**
   * Finds a control point index. Returns -1 if no control point could be found. FIXME - Move this
   * to BezierPath
   */
  public int findNode(Point2D.Double p) {
    BezierPath tp = path;
    for (int i = 0; i < tp.size(); i++) {
      BezierPath.Node p2 = tp.nodes().get(i);
      if (p2.x[0] == p.x && p2.y[0] == p.y) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Gets the segment of the polyline that is hit by the given Point2D.Double.
   *
   * @param find a Point on the bezier path
   * @param tolerance a tolerance, tolerance should take into account the line width, plus 2 divided
   *     by the zoom factor.
   * @return the index of the segment or -1 if no segment was hit.
   */
  public int findSegment(Point2D.Double find, double tolerance) {
    return path.findSegment(find, tolerance);
  }

  /**
   * Joins two segments into one if the given Point2D.Double hits a node of the polyline.
   *
   * @return true if the two segments were joined.
   * @param join a Point at a node on the bezier path
   * @param tolerance a tolerance, tolerance should take into account the line width, plus 2 divided
   *     by the zoom factor.
   */
  public boolean joinSegments(Point2D.Double join, double tolerance) {
    int i = findSegment(join, tolerance);
    if (i != -1 && i > 1) {
      removeNode(i);
      return true;
    }
    return false;
  }

  /**
   * Splits the segment at the given Point2D.Double if a segment was hit.
   *
   * @return the index of the segment or -1 if no segment was hit.
   * @param split a Point on (or near) a line segment on the bezier path
   * @param tolerance a tolerance, tolerance should take into account the line width, plus 2 divided
   *     by the zoom factor.
   */
  public int splitSegment(Point2D.Double split, double tolerance) {
    int i = findSegment(split, tolerance);
    if (i != -1) {
      addNode(i + 1, new BezierPath.Node(split));
    }
    return i + 1;
  }

  /** Removes the Node at the specified index. */
  public BezierPath.Node removeNode(int index) {
    return path.remove(index);
  }

  /** Removes the Point2D.Double at the specified index. */
  protected void removeAllNodes() {
    path.clear();
  }

  /** Gets the node count. */
  public int getNodeCount() {
    return path.size();
  }

  @Override
  public BezierFigure clone() {
    BezierFigure that = (BezierFigure) super.clone();
    that.path = this.path.clone();
    that.invalidate();
    return that;
  }

  @Override
  public void restoreTransformTo(Object geometry) {
    path.setTo((BezierPath) geometry);
  }

  @Override
  public Object getTransformRestoreData() {
    return path.clone();
  }

  public Point2D.Double chop(Point2D.Double p) {
    if (isClosed()) {
      double grow =
          AttributeKeys.getPerpendicularHitGrowth(this, AttributeKeys.scaleFromContext(this));
      if (grow == 0d) {
        return path.chop(p);
      } else {
        GrowStroke gs = new GrowStroke(
            grow,
            AttributeKeys.getStrokeTotalWidth(this, AttributeKeys.scaleFromContext(this))
                * attr().get(STROKE_MITER_LIMIT));
        return Geom.chop(gs.createStrokedShape(path), p);
      }
    } else {
      return path.chop(p);
    }
  }

  public Point2D.Double getCenter() {
    return path.getCenter();
  }

  public Point2D.Double getOutermostPoint() {
    return path.nodes().get(path.indexOfOutermostNode()).getControlPoint(0);
  }

  /**
   * Joins two segments into one if the given Point2D.Double hits a node of the polyline.
   *
   * @return true if the two segments were joined.
   */
  public int joinSegments(Point2D.Double join, float tolerance) {
    return path.joinSegments(join, tolerance);
  }

  /**
   * Splits the segment at the given Point2D.Double if a segment was hit.
   *
   * @return the index of the segment or -1 if no segment was hit.
   */
  public int splitSegment(Point2D.Double split, float tolerance) {
    return path.splitSegment(split, tolerance);
  }

  /** Handles a mouse click. */
  @Override
  public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
    if (evt.getClickCount() == 2 && view.getHandleDetailLevel() % 2 == 0) {
      willChange();
      final int index = splitSegment(p, 5f / view.getScaleFactor());
      if (index != -1) {
        final BezierPath.Node newNode = getNode(index);
        fireUndoableEditHappened(new AbstractUndoableEdit() {
          private static final long serialVersionUID = 1L;

          @Override
          public String getPresentationName() {
            ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
            return labels.getString("edit.bezierPath.splitSegment.text");
          }

          @Override
          public void redo() throws CannotRedoException {
            super.redo();
            willChange();
            addNode(index, newNode);
            changed();
          }

          @Override
          public void undo() throws CannotUndoException {
            super.undo();
            willChange();
            removeNode(index);
            changed();
          }
        });
        changed();
        evt.consume();
        return true;
      }
    }
    return false;
  }
}
