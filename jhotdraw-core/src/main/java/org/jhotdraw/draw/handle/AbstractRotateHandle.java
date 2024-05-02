/**
 * @(#)AbstractRotateHandle.java
 *
 * <p>Copyright (c) 1996-2010 The authors and contributors of JHotDraw. You may not use, copy or
 * modify this file, except in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.jhotdraw.draw.event.TransformEdit;
import org.jhotdraw.draw.event.TransformRestoreEdit;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.Rotation;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * This abstract class can be extended to implement a {@link Handle} which can rotate a {@link
 * Figure}.
 */
public abstract class AbstractRotateHandle extends AbstractHandle {

  private Point location;
  private Object restoreData;
  private AffineTransform transform;
  private Point2D.Double center;
  private double startTheta;
  private double startLength;

  public AbstractRotateHandle(Figure owner) {
    super(owner);
  }

  @Override
  public boolean isCombinableWith(Handle h) {
    return false;
  }

  @Override
  public String getToolTipText(Point p) {
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    return labels.getString("handle.rotate.toolTipText");
  }

  /** Draws this handle. */
  @Override
  public void draw(Graphics2D g) {
    if (getEditor().getTool().supportsHandleInteraction()) {
      drawCircle(
          g,
          getEditor().getHandleAttribute(HandleAttributeKeys.ROTATE_HANDLE_FILL_COLOR),
          getEditor().getHandleAttribute(HandleAttributeKeys.ROTATE_HANDLE_STROKE_COLOR));
    } else {
      drawCircle(
          g,
          getEditor().getHandleAttribute(HandleAttributeKeys.ROTATE_HANDLE_FILL_COLOR_DISABLED),
          getEditor().getHandleAttribute(HandleAttributeKeys.ROTATE_HANDLE_STROKE_COLOR_DISABLED));
    }
  }

  protected Rectangle2D.Double getTransformedBounds() {
    Figure owner = getOwner();
    Rectangle2D.Double bounds = owner.getBounds();
    if (owner.attr().get(TRANSFORM) != null) {
      Rectangle2D r = owner.attr().get(TRANSFORM).createTransformedShape(bounds).getBounds2D();
      bounds.x = r.getX();
      bounds.y = r.getY();
      bounds.width = r.getWidth();
      bounds.height = r.getHeight();
    }
    return bounds;
  }

  protected Object getRestoreData() {
    return restoreData;
  }

  protected double getStartTheta() {
    return startTheta;
  }

  protected abstract Point2D.Double getCenter();

  @Override
  public void trackStart(Point anchor, int modifiersEx) {
    location = new Point(anchor.x, anchor.y);
    restoreData = getOwner().getTransformRestoreData();
    transform = new AffineTransform();
    center = getCenter();
    Point2D.Double anchorPoint = view.viewToDrawing(anchor);
    startTheta = Geom.angle(center.x, center.y, anchorPoint.x, anchorPoint.y);
    startLength = Geom.length(center.x, center.y, anchorPoint.x, anchorPoint.y);
  }

  @Override
  public void trackStep(Point anchor, Point lead, int modifiersEx) {
    location = new Point(lead.x, lead.y);
    Point2D.Double leadPoint = view.viewToDrawing(lead);
    double stepTheta = Geom.angle(center.x, center.y, leadPoint.x, leadPoint.y);
    double currentTheta = view.getConstrainer() == null
        ? (stepTheta - startTheta)
        : view.getConstrainer().constrainAngle(stepTheta - startTheta, getOwner());
    transform.setToIdentity();
    transform.translate(center.x, center.y);
    transform.rotate(currentTheta);
    transform.translate(-center.x, -center.y);
    getOwner().willChange();
    getOwner().restoreTransformTo(restoreData);
    if (getOwner() instanceof Rotation rotateOwner) {
      rotateOwner.setRotation(currentTheta);
    } else {
      getOwner().transform(transform);
    }
    getOwner().changed();
  }

  @Override
  public void trackEnd(Point anchor, Point lead, int modifiersEx) {
    view.getDrawing()
        .fireUndoableEditHappened(new TransformRestoreEdit(
            getOwner(), restoreData, getOwner().getTransformRestoreData()));
    fireAreaInvalidated(getDrawingArea());
    location = null;
    invalidate();
    fireAreaInvalidated(getDrawingArea());
  }

  @Override
  public void keyPressed(KeyEvent evt) {
    Figure f = getOwner();
    center = getCenter();
    if (f.isTransformable()) {
      AffineTransform tx = new AffineTransform();
      switch (evt.getKeyCode()) {
        case KeyEvent.VK_UP:
        case KeyEvent.VK_LEFT:
          tx.rotate(-1d / 180d * Math.PI, center.x, center.y);
          evt.consume();
          break;
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_RIGHT:
          tx.rotate(1d / 180d * Math.PI, center.x, center.y);
          evt.consume();
          break;
      }
      f.willChange();
      f.transform(tx);
      f.changed();
      fireUndoableEditHappened(new TransformEdit(f, tx));
    }
  }
}
