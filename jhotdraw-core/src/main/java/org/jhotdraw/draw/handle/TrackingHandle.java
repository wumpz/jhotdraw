/*
 * @(#)BezierNodeHandle.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.jhotdraw.draw.event.TrackingEdit;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.undo.CompositeEdit;

/**
 * Simple tracking handle that changes in some way the owner Figure. This change is injected using
 * some lambdas.
 */
public class TrackingHandle extends AbstractHandle {
  private final Supplier<Point2D.Double> readLocation;
  private final Consumer<Point2D.Double> writeLocation;
  private final Runnable deleteLocation;
  private final Runnable insertLocation;
  private CompositeEdit edit;

  public TrackingHandle(
      Figure owner, Supplier<Point2D.Double> readLocation, Consumer<Point2D.Double> writeLocation) {
    this(owner, readLocation, writeLocation, null, null);
  }

  public TrackingHandle(
      Figure owner,
      Supplier<Point2D.Double> readLocation,
      Consumer<Point2D.Double> writeLocation,
      Runnable deleteLocation,
      Runnable insertLocation) {
    super(owner);
    this.readLocation = Objects.requireNonNull(readLocation);
    this.writeLocation = Objects.requireNonNull(writeLocation);
    this.deleteLocation = deleteLocation;
    this.insertLocation = insertLocation;
  }

  @Override
  public void draw(Graphics2D g) {
    drawRectangle(
        g,
        getEditor().getHandleAttribute(HandleAttributeKeys.HANDLE_FILL_COLOR),
        getEditor().getHandleAttribute(HandleAttributeKeys.HANDLE_STROKE_COLOR));
  }

  @Override
  protected Point2D.Double getDrawingLocation() {
    Point2D.Double location = readLocation.get();
    if (location != null) {
      if (getOwner().attr().get(TRANSFORM) != null) {
        getOwner().attr().get(TRANSFORM).transform(location, location);
      }
      return location;
    } else {
      return null;
    }
  }

  private Point2D.Double oldPoint = null;

  @Override
  public void trackStart(Point anchor, int modifiersEx) {
    Figure figure = getOwner();
    view.getDrawing().fireUndoableEditHappened(edit = new CompositeEdit("Punkt verschieben"));
    oldPoint = readLocation.get();
  }

  @Override
  public void trackStep(Point anchor, Point lead, int modifiersEx) {
    Figure figure = getOwner();
    figure.willChange();
    Point2D.Double p =
        view.getConstrainer() == null
            ? view.viewToDrawing(lead)
            : view.getConstrainer().constrainPoint(view.viewToDrawing(lead));
    if (getOwner().attr().get(TRANSFORM) != null) {
      try {
        getOwner().attr().get(TRANSFORM).inverseTransform(p, p);
      } catch (NoninvertibleTransformException ex) {
        ex.printStackTrace();
      }
    }
    writeLocation.accept(p);
    figure.changed();
  }

  private void fireAreaInvalidated(Point2D.Double p) {
    Rectangle2D.Double dr = new Rectangle2D.Double(p.x, p.y, 0, 0);
    Rectangle vr = view.drawingToView(dr);
    vr.grow(getHandlesize(), getHandlesize());
    fireAreaInvalidated(vr);
  }

  @Override
  public void trackEnd(Point anchor, Point lead, int modifiersEx) {
    final Figure f = getOwner();
    Point2D.Double newPoint = readLocation.get();
    view.getDrawing()
        .fireUndoableEditHappened(new TrackingEdit(f, writeLocation, oldPoint, newPoint));
    view.getDrawing().fireUndoableEditHappened(edit);
  }

  @Override
  public void keyPressed(KeyEvent evt) {
    Figure f = getOwner();
    oldPoint = readLocation.get();

    if (evt.getKeyCode() == KeyEvent.VK_DELETE || evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
      Rectangle invalidatedArea = getDrawingArea();
      f.willChange();
      deleteLocation.run();
      f.changed();
      fireHandleRequestRemove(invalidatedArea);
      fireUndoableEditHappened(
          new AbstractUndoableEdit() {
            @Override
            public void redo() throws CannotRedoException {
              super.redo();
              f.willChange();
              deleteLocation.run();
              f.changed();
            }

            @Override
            public void undo() throws CannotUndoException {
              super.undo();
              f.willChange();
              insertLocation.run();
              f.changed();
            }
          });
      evt.consume();
      // At this point, the handle is no longer valid, and
      // handles at higher node indices have become invalid too.
      fireHandleRequestRemove(invalidatedArea);
    } else {
      Point2D.Double newPoint = null;
      switch (evt.getKeyCode()) {
        case KeyEvent.VK_UP:
          newPoint = new Point2D.Double(oldPoint.x, oldPoint.y - 1d);
          break;
        case KeyEvent.VK_DOWN:
          newPoint = new Point2D.Double(oldPoint.x, oldPoint.y + 1d);
          break;
        case KeyEvent.VK_LEFT:
          newPoint = new Point2D.Double(oldPoint.x - 1d, oldPoint.y);
          break;
        case KeyEvent.VK_RIGHT:
          newPoint = new Point2D.Double(oldPoint.x + 1d, oldPoint.y);
          break;
      }
      if (newPoint != null) {
        f.willChange();
        writeLocation.accept(newPoint);
        f.changed();
        view.getDrawing()
            .fireUndoableEditHappened(new TrackingEdit(f, writeLocation, oldPoint, newPoint));
        evt.consume();
      }
    }
  }
}
