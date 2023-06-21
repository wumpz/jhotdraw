/*
 * @(#)AbstractHandle.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import javax.swing.event.EventListenerList;
import javax.swing.undo.UndoableEdit;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.event.FigureListener;
import org.jhotdraw.draw.event.FigureListenerAdapter;
import org.jhotdraw.draw.event.HandleEvent;
import org.jhotdraw.draw.event.HandleListener;
import org.jhotdraw.draw.figure.Figure;

/** This abstract class can be extended to implement a {@link Handle}. */
public abstract class AbstractHandle implements Handle {

  protected final FigureListener FIGURE_LISTENER =
      new FigureListenerAdapter() {
        /**
         * Sent when a region used by the figure needs to be repainted. The implementation of this
         * method assumes that the handle is located on the bounds of the figure or inside the
         * figure. If the handle is located elsewhere this method must be reimpleted by the
         * subclass.
         */
        @Override
        public void areaInvalidated(FigureEvent evt) {
          updateBounds();
        }

        @Override
        public void figureChanged(FigureEvent evt) {
          updateBounds();
        }
      };

  protected EventListenerList listenerList = new EventListenerList();
  protected DrawingView view;
  private Rectangle bounds;
  private final Figure owner;
  private String toolTipText;

  public AbstractHandle(Figure owner) {
    if (owner == null) {
      throw new IllegalArgumentException("owner must not be null");
    }
    this.owner = owner;
    owner.addFigureListener(FIGURE_LISTENER);
  }

  @Override
  public void addHandleListener(HandleListener l) {
    listenerList.add(HandleListener.class, l);
  }

  @Override
  public boolean contains(Point p) {
    return getBounds().contains(p);
  }

  @Override
  public Collection<Handle> createSecondaryHandles() {
    return Collections.emptyList();
  }

  @Override
  public void dispose() {
    owner.removeFigureListener(FIGURE_LISTENER);
  }

  @Override
  public void draw(Graphics2D g) {
    drawCircle(
        g,
        getEditor().getHandleAttribute(HandleAttributeKeys.HANDLE_FILL_COLOR),
        getEditor().getHandleAttribute(HandleAttributeKeys.HANDLE_STROKE_COLOR));
  }

  /** Gets and caches actual handles bounds. The computation itself is done in basicGetBounds. */
  @Override
  public final Rectangle getBounds() {
    if (bounds == null) {
      bounds = basicGetBounds();
    }
    return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
  }

  @Override
  public Cursor getCursor() {
    return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
  }

  @Override
  public Rectangle getDrawingArea() {
    Rectangle r = getBounds();
    r.grow(2, 2); // grow by two pixels to take antialiasing into account
    return r;
  }

  public DrawingEditor getEditor() {
    return view.getEditor();
  }

  @Override
  public Figure getOwner() {
    return owner;
  }

  @Override
  public String getToolTipText(Point p) {
    return toolTipText;
  }

  public void setToolTipText(String newValue) {
    toolTipText = newValue;
  }

  public DrawingView getView() {
    return view;
  }

  @Override
  public void setView(DrawingView view) {
    this.view = view;
  }

  @Override
  public void invalidate() {
    bounds = null;
  }

  /**
   * Returns true, if the given handle is an instance of the same class or of a subclass of this
   * handle,.
   */
  @Override
  public boolean isCombinableWith(Handle handle) {
    return getClass().isAssignableFrom(handle.getClass());
  }

  @Override
  public void keyPressed(KeyEvent evt) {}

  @Override
  public void keyReleased(KeyEvent evt) {}

  @Override
  public void keyTyped(KeyEvent evt) {}

  @Override
  public void removeHandleListener(HandleListener l) {
    listenerList.remove(HandleListener.class, l);
  }

  @Override
  public void trackDoubleClick(Point p, int modifiersEx) {}

  @Override
  public void trackStart(Point anchor, int modifiersEx) {}

  @Override
  public void trackStep(Point anchor, Point lead, int modifiersEx) {}

  @Override
  public void trackEnd(Point anchor, Point lead, int modifiersEx) {}

  @Override
  public void viewTransformChanged() {
    invalidate();
  }

  protected Rectangle basicGetBounds() {
    Rectangle r = new Rectangle(getScreenLocation());
    int h = getHandlesize();
    r.x -= h / 2;
    r.y -= h / 2;
    r.width = r.height = h;
    return r;
  }

  protected Point2D.Double getDrawingLocation() {
    return null;
  }

  protected Point getScreenLocation() {
    return Optional.ofNullable(getDrawingLocation())
        .map(pnt -> view.drawingToView(pnt))
        .orElseGet(() -> new Point(10, 10));
  }

  protected void drawCircle(Graphics2D g, Color fill, Color stroke) {
    Rectangle r = getBounds();
    if (fill != null) {
      g.setColor(fill);
      g.fillOval(r.x + 1, r.y + 1, r.width - 2, r.height - 2);
    }
    if (stroke != null) {
      g.setStroke(new BasicStroke());
      g.setColor(stroke);
      g.drawOval(r.x, r.y, r.width - 1, r.height - 1);
      if (getView().getActiveHandle() == this) {
        g.fillOval(r.x + 2, r.y + 2, r.width - 4, r.height - 4);
      }
    }
  }

  protected void drawDiamond(Graphics2D g, Color fill, Color stroke) {
    if (stroke != null) {
      Rectangle r = getBounds();
      r.grow(1, 1);
      Path2D.Double p = new Path2D.Double();
      p.moveTo(r.x + r.width / 2f, r.y);
      p.lineTo(r.x + r.width, r.y + r.height / 2f);
      p.lineTo(r.x + r.width / 2f, r.y + r.height);
      p.lineTo(r.x, r.y + r.height / 2f);
      p.closePath();
      g.setColor(stroke);
      g.fill(p);
    }
    if (fill != null) {
      Rectangle r = getBounds();
      Path2D.Double p = new Path2D.Double();
      p.moveTo(r.x + r.width / 2f, r.y);
      p.lineTo(r.x + r.width, r.y + r.height / 2f);
      p.lineTo(r.x + r.width / 2f, r.y + r.height);
      p.lineTo(r.x, r.y + r.height / 2f);
      p.closePath();
      g.setColor(fill);
      g.fill(p);
    }
    if (stroke != null && getView().getActiveHandle() == this) {
      Rectangle r = getBounds();
      r.grow(-1, -1);
      Path2D.Double p = new Path2D.Double();
      p.moveTo(r.x + r.width / 2f, r.y);
      p.lineTo(r.x + r.width, r.y + r.height / 2f);
      p.lineTo(r.x + r.width / 2f, r.y + r.height);
      p.lineTo(r.x, r.y + r.height / 2f);
      p.closePath();
      g.setColor(stroke);
      g.fill(p);
    }
  }

  protected void drawRectangle(Graphics2D g, Color fill, Color stroke) {
    if (fill != null) {
      Rectangle r = getBounds();
      g.setColor(fill);
      r.x += 1;
      r.y += 1;
      r.width -= 2;
      r.height -= 2;
      g.fill(r);
    }
    g.setStroke(new BasicStroke());
    if (stroke != null) {
      Rectangle r = getBounds();
      r.width -= 1;
      r.height -= 1;
      g.setColor(stroke);
      g.draw(r);
      if (getView().getActiveHandle() == this) {
        r.x += 2;
        r.y += 2;
        r.width -= 3;
        r.height -= 3;
        g.fill(r);
      }
    }
  }

  protected void fireAreaInvalidated(Rectangle invalidatedArea) {
    fireHandleEvent(
        (listener, event) -> listener.areaInvalidated(event),
        () -> new HandleEvent(this, invalidatedArea));
  }
  /**
   * Wrapper around multiple types of event firing. This is used to define all kinds of event
   * methods below.
   *
   * @param listenerConsumer lambda to call the right listener method with the right event
   * @param eventSupplier creates if needed an event instance for the listener
   */
  protected void fireHandleEvent(
      BiConsumer<HandleListener, HandleEvent> listenerConsumer,
      Supplier<HandleEvent> eventSupplier) {
    HandleEvent event = null;
    if (listenerList.getListenerCount() == 0) {
      return;
    }
    for (HandleListener listener : listenerList.getListeners(HandleListener.class)) {
      if (event == null) {
        event = eventSupplier.get();
      }
      listenerConsumer.accept(listener, event);
    }
  }

  protected void fireHandleRequestRemove(Rectangle invalidatedArea) {
    fireHandleEvent(
        (listener, event) -> listener.handleRequestRemove(event),
        () -> new HandleEvent(this, invalidatedArea));
  }

  protected void fireHandleRequestSecondaryHandles() {
    fireHandleEvent(
        (listener, event) -> listener.handleRequestSecondaryHandles(event),
        () -> new HandleEvent(this, null));
  }

  protected void fireUndoableEditHappened(UndoableEdit edit) {
    view.getDrawing().fireUndoableEditHappened(edit);
  }

  protected int getHandlesize() {
    return getEditor().getHandleAttribute(HandleAttributeKeys.HANDLE_SIZE);
  }

  protected void updateBounds() {
    Rectangle newBounds = basicGetBounds();
    if (bounds == null || !newBounds.equals(bounds)) {
      if (bounds != null) {
        fireAreaInvalidated(getDrawingArea());
      }
      bounds = newBounds;
      fireAreaInvalidated(getDrawingArea());
    }
  }
}
