/*
 * @(#)RoundRectRadiusHandle.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.*;
import org.jhotdraw.draw.event.CompositeFigureEdit;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.RoundRectangleFigure;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.undo.PropertyChangeEdit;
import org.jhotdraw.util.*;

/** A {@link Handle} to manipulate the corner radius of a {@link RoundRectangleFigure}. */
public class RoundRectangleRadiusHandle extends AbstractHandle {

  private static final int OFFSET = 6;
  private Point originalArc;

  public RoundRectangleRadiusHandle(Figure owner) {
    super(owner);
  }

  /** Draws this handle. */
  @Override
  public void draw(Graphics2D g) {
    if (getEditor().getTool().supportsHandleInteraction()) {
      drawDiamond(
          g,
          getEditor().getHandleAttribute(HandleAttributeKeys.ATTRIBUTE_HANDLE_FILL_COLOR),
          getEditor().getHandleAttribute(HandleAttributeKeys.ATTRIBUTE_HANDLE_STROKE_COLOR));
    } else {
      drawDiamond(
          g,
          getEditor().getHandleAttribute(HandleAttributeKeys.ATTRIBUTE_HANDLE_FILL_COLOR_DISABLED),
          getEditor()
              .getHandleAttribute(HandleAttributeKeys.ATTRIBUTE_HANDLE_STROKE_COLOR_DISABLED));
    }
  }

  private Point locate() {
    RoundRectangleFigure owner = (RoundRectangleFigure) getOwner();
    Rectangle r = view.drawingToView(owner.getBounds());
    Point arc = view.drawingToView(new Point2D.Double(owner.getArcWidth(), owner.getArcHeight()));
    return new Point(r.x + arc.x / 2 + OFFSET, r.y + arc.y / 2 + OFFSET);
  }

  @Override
  public void trackStart(Point anchor, int modifiersEx) {
    RoundRectangleFigure owner = (RoundRectangleFigure) getOwner();
    originalArc = view.drawingToView(new Point2D.Double(owner.getArcWidth(), owner.getArcHeight()));
  }

  @Override
  public void trackStep(Point anchor, Point lead, int modifiersEx) {
    int dx = lead.x - anchor.x;
    int dy = lead.y - anchor.y;
    RoundRectangleFigure owner = (RoundRectangleFigure) getOwner();
    Rectangle r = view.drawingToView(owner.getBounds());
    Point viewArc = new Point(
        Geom.range(0, r.width, 2 * (originalArc.x / 2 + dx)),
        Geom.range(0, r.height, 2 * (originalArc.y / 2 + dy)));
    Point2D.Double arc = view.viewToDrawing(viewArc);
    owner.willChange();
    owner.setArc(arc.x, arc.y);
    owner.changed();
  }

  @Override
  public void trackEnd(Point anchor, Point lead, int modifiersEx) {
    int dx = lead.x - anchor.x;
    int dy = lead.y - anchor.y;
    RoundRectangleFigure owner = (RoundRectangleFigure) getOwner();
    Rectangle r = view.drawingToView(owner.getBounds());
    Point viewArc = new Point(
        Geom.range(0, r.width, 2 * (originalArc.x / 2 + dx)),
        Geom.range(0, r.height, 2 * (originalArc.y / 2 + dy)));
    Point2D.Double oldArc = view.viewToDrawing(originalArc);
    Point2D.Double newArc = view.viewToDrawing(viewArc);
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    CompositeFigureEdit edit =
        new CompositeFigureEdit(owner, labels.getString("attribute.roundRectRadius"));
    fireUndoableEditHappened(edit);
    fireUndoableEditHappened(
        new PropertyChangeEdit(owner, RoundRectangleFigure.ARC_WIDTH_PROPERTY, oldArc.x, newArc.x));
    fireUndoableEditHappened(new PropertyChangeEdit(
        owner, RoundRectangleFigure.ARC_HEIGHT_PROPERTY, oldArc.y, newArc.y));
    fireUndoableEditHappened(edit);
  }

  @Override
  public void keyPressed(KeyEvent evt) {
    RoundRectangleFigure owner = (RoundRectangleFigure) getOwner();
    Point2D.Double oldArc = new Point2D.Double(owner.getArcWidth(), owner.getArcHeight());
    Point2D.Double newArc = new Point2D.Double(owner.getArcWidth(), owner.getArcHeight());
    switch (evt.getKeyCode()) {
      case KeyEvent.VK_UP:
        if (newArc.y > 0) {
          newArc.y = Math.max(0, newArc.y - 1);
        }
        evt.consume();
        break;
      case KeyEvent.VK_DOWN:
        newArc.y += 1;
        evt.consume();
        break;
      case KeyEvent.VK_LEFT:
        if (newArc.x > 0) {
          newArc.x = Math.max(0, newArc.x - 1);
        }
        evt.consume();
        break;
      case KeyEvent.VK_RIGHT:
        newArc.x += 1;
        evt.consume();
        break;
    }
    if (!newArc.equals(oldArc)) {
      owner.willChange();
      owner.setArcWidth(newArc.x);
      owner.setArcHeight(newArc.y);
      owner.changed();
      ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
      CompositeFigureEdit edit =
          new CompositeFigureEdit(owner, labels.getString("attribute.roundRectRadius"));
      fireUndoableEditHappened(edit);
      fireUndoableEditHappened(new PropertyChangeEdit(
          owner, RoundRectangleFigure.ARC_WIDTH_PROPERTY, oldArc.x, newArc.x));
      fireUndoableEditHappened(new PropertyChangeEdit(
          owner, RoundRectangleFigure.ARC_HEIGHT_PROPERTY, oldArc.y, newArc.y));
      fireUndoableEditHappened(edit);
    }
  }

  @Override
  public String getToolTipText(Point p) {
    return ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels")
        .getString("handle.roundRectangleRadius.toolTipText");
  }
}
