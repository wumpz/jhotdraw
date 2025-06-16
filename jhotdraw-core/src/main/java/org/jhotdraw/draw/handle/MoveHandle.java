/*
 * @(#)MoveHandle.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.draw.event.TransformEdit;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.locator.Locator;
import org.jhotdraw.draw.locator.RelativeLocator;

/** A handle that changes the location of the owning figure, if the figure is transformable. */
public class MoveHandle extends LocatorHandle {

  /** The previously handled x and y coordinates. */
  private Point2D.Double oldPoint;

  public MoveHandle(Figure owner, Locator locator) {
    super(owner, locator);
  }

  /** Creates handles for each corner of a figure and adds them to the provided collection. */
  public static void addMoveHandles(Figure f, Collection<Handle> handles) {
    handles.add(southEast(f));
    handles.add(southWest(f));
    handles.add(northEast(f));
    handles.add(northWest(f));
  }

  /**
   * Draws this handle.
   *
   * <p>If the figure is transformable, the handle is drawn as a filled rectangle. If the figure is
   * not transformable, the handle is drawn as an unfilled rectangle.
   */
  @Override
  public void draw(Graphics2D g) {
    if (getOwner().isTransformable()) {
      drawRectangle(
          g,
          getEditor().getHandleAttribute(HandleAttributeKeys.MOVE_HANDLE_FILL_COLOR),
          getEditor().getHandleAttribute(HandleAttributeKeys.MOVE_HANDLE_STROKE_COLOR));
    } else {
      drawRectangle(
          g,
          getEditor().getHandleAttribute(HandleAttributeKeys.NULL_HANDLE_FILL_COLOR),
          getEditor().getHandleAttribute(HandleAttributeKeys.NULL_HANDLE_STROKE_COLOR));
    }
  }

  /**
   * Returns a cursor for the handle.
   *
   * @return Returns a move cursor, if the figure is transformable. Returns a default cursor
   *     otherwise.
   */
  @Override
  public Cursor getCursor() {
    return Cursor.getPredefinedCursor(
        getOwner().isTransformable() ? Cursor.MOVE_CURSOR : Cursor.DEFAULT_CURSOR);
  }

  @Override
  public void trackStart(Point anchor, int modifiersEx) {
    oldPoint = view.getConstrainer() == null
        ? view.viewToDrawing(anchor)
        : view.getConstrainer().constrainPoint(view.viewToDrawing(anchor));
  }

  @Override
  public void trackStep(Point anchor, Point lead, int modifiersEx) {
    Figure f = getOwner();
    if (f.isTransformable()) {
      Point2D.Double newPoint = view.getConstrainer() == null
          ? view.viewToDrawing(lead)
          : view.getConstrainer().constrainPoint(view.viewToDrawing(lead));
      AffineTransform tx = new AffineTransform();
      tx.translate(newPoint.x - oldPoint.x, newPoint.y - oldPoint.y);
      f.willChange();
      f.transform(tx);
      f.changed();
      oldPoint = newPoint;
    }
  }

  @Override
  public void trackEnd(Point anchor, Point lead, int modifiersEx) {
    if (getOwner().isTransformable()) {
      AffineTransform tx = new AffineTransform();
      tx.translate(lead.x - anchor.x, lead.y - anchor.y);
      fireUndoableEditHappened(new TransformEdit(getOwner(), tx));
    }
  }

  @Override
  public void keyPressed(KeyEvent evt) {
    Figure f = getOwner();
    if (f.isTransformable()) {
      AffineTransform tx = new AffineTransform();
      switch (evt.getKeyCode()) {
        case KeyEvent.VK_UP:
          tx.translate(0, -1);
          evt.consume();
          break;
        case KeyEvent.VK_DOWN:
          tx.translate(0, +1);
          evt.consume();
          break;
        case KeyEvent.VK_LEFT:
          tx.translate(-1, 0);
          evt.consume();
          break;
        case KeyEvent.VK_RIGHT:
          tx.translate(+1, 0);
          evt.consume();
          break;
      }
      f.willChange();
      f.transform(tx);
      f.changed();
      fireUndoableEditHappened(new TransformEdit(f, tx));
    }
  }

  public static Handle south(Figure owner) {
    return new MoveHandle(owner, RelativeLocator.south());
  }

  public static Handle southEast(Figure owner) {
    return new MoveHandle(owner, RelativeLocator.southEast());
  }

  public static Handle southWest(Figure owner) {
    return new MoveHandle(owner, RelativeLocator.southWest());
  }

  public static Handle north(Figure owner) {
    return new MoveHandle(owner, RelativeLocator.north());
  }

  public static Handle northEast(Figure owner) {
    return new MoveHandle(owner, RelativeLocator.northEast());
  }

  public static Handle northWest(Figure owner) {
    return new MoveHandle(owner, RelativeLocator.northWest());
  }

  public static Handle east(Figure owner) {
    return new MoveHandle(owner, RelativeLocator.east());
  }

  public static Handle west(Figure owner) {
    return new MoveHandle(owner, RelativeLocator.west());
  }
}
