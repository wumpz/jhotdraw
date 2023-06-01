/*
 * @(#)OrientationHandle.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.event.AttributeChangeEdit;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.TriangleFigure;
import org.jhotdraw.geom.Geom;

/**
 * A {@link Handle} to change the value of the figure attribute {@link
 * org.jhotdraw.draw.AttributeKeys#ORIENTATION}.
 */
public class OrientationHandle extends AbstractHandle {

  private Rectangle centerBox;
  private AttributeKeys.Orientation oldValue;
  private AttributeKeys.Orientation newValue;

  public OrientationHandle(TriangleFigure owner) {
    super(owner);
  }

  @Override
  public boolean isCombinableWith(Handle h) {
    return false;
  }

  @Override
  protected Point2D.Double getDrawingLocation() {
    Figure owner = getOwner();
    Rectangle2D.Double r = owner.getBounds();
    Point2D.Double p;
    double offset = getHandlesize();
    switch (owner.attr().get(ORIENTATION)) {
      case NORTH:
      default:
        p = new Point2D.Double(r.x + r.width / 2d, r.y + offset);
        break;
      case NORTH_EAST:
        p = new Point2D.Double(r.x + r.width - offset, r.y + offset);
        break;
      case EAST:
        p = new Point2D.Double(r.x + r.width - offset, r.y + r.height / 2d);
        break;
      case SOUTH_EAST:
        p = new Point2D.Double(r.x + r.width - offset, r.y + r.height - offset);
        break;
      case SOUTH:
        p = new Point2D.Double(r.x + r.width / 2d, r.y + r.height - offset);
        break;
      case SOUTH_WEST:
        p = new Point2D.Double(r.x + offset, r.y + r.height - offset);
        break;
      case WEST:
        p = new Point2D.Double(r.x + offset, r.y + r.height / 2d);
        break;
      case NORTH_WEST:
        p = new Point2D.Double(r.x + offset, r.y + offset);
        break;
    }
    return p;
  }

  @Override
  public void trackStart(Point anchor, int modifiersEx) {
    oldValue = getOwner().attr().get(ORIENTATION);
    centerBox = view.drawingToView(getOwner().getBounds());
    centerBox.grow(centerBox.width / -3, centerBox.height / -3);
  }

  @Override
  public void trackStep(Point anchor, Point lead, int modifiersEx) {
    Rectangle leadRect = new Rectangle(lead);
    switch (Geom.outcode(centerBox, leadRect)) {
      case Geom.OUT_TOP:
      default:
        newValue = AttributeKeys.Orientation.NORTH;
        break;
      case Geom.OUT_TOP | Geom.OUT_RIGHT:
        newValue = AttributeKeys.Orientation.NORTH_EAST;
        break;
      case Geom.OUT_RIGHT:
        newValue = AttributeKeys.Orientation.EAST;
        break;
      case Geom.OUT_BOTTOM | Geom.OUT_RIGHT:
        newValue = AttributeKeys.Orientation.SOUTH_EAST;
        break;
      case Geom.OUT_BOTTOM:
        newValue = AttributeKeys.Orientation.SOUTH;
        break;
      case Geom.OUT_BOTTOM | Geom.OUT_LEFT:
        newValue = AttributeKeys.Orientation.SOUTH_WEST;
        break;
      case Geom.OUT_LEFT:
        newValue = AttributeKeys.Orientation.WEST;
        break;
      case Geom.OUT_TOP | Geom.OUT_LEFT:
        newValue = AttributeKeys.Orientation.NORTH_WEST;
        break;
    }
    getOwner().willChange();
    getOwner().attr().set(ORIENTATION, newValue);
    getOwner().changed();
    updateBounds();
  }

  @Override
  public void draw(Graphics2D g) {
    drawDiamond(
        g,
        getEditor().getHandleAttribute(HandleAttributeKeys.ATTRIBUTE_HANDLE_FILL_COLOR),
        getEditor().getHandleAttribute(HandleAttributeKeys.ATTRIBUTE_HANDLE_STROKE_COLOR));
  }

  @Override
  public void trackEnd(Point anchor, Point lead, int modifiersEx) {
    if (newValue != oldValue) {
      fireUndoableEditHappened(
          new AttributeChangeEdit<>(getOwner(), ORIENTATION, oldValue, newValue));
    }
  }
}
