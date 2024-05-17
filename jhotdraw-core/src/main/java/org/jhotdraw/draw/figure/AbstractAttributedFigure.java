/*
 * @(#)AbstractAttributedFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.swing.Action;
import javax.swing.event.EventListenerList;
import javax.swing.undo.UndoableEdit;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.event.FigureListener;
import org.jhotdraw.draw.event.SetBoundsEdit;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.ResizeHandleKit;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.geom.Geom;

/**
 * This abstract class can be extended to implement a {@link Figure} which has its own attribute
 * set.
 *
 * @author Werner Randelshofer
 * @version $Id: AbstractAttributedFigure.java 778 2012-04-13 15:37:19Z rawcoder $
 */
public abstract class AbstractAttributedFigure implements Figure, Cloneable {

  private static final long serialVersionUID = 1L;
  protected EventListenerList listenerList = new EventListenerList();
  private Drawing drawing;
  private boolean isSelectable = true;
  private boolean isRemovable = true;
  private boolean isVisible = true;
  private boolean isTransformable = true;
  private boolean isConnectable = true;

  private Attributes attributes = new Attributes(this::fireAttributeChanged);

  @Override
  public Attributes attr() {
    return attributes;
  }

  /**
   * This variable is used to prevent endless change loops. We increase its value on each invocation
   * of willChange() and decrease it on each invocation of changed().
   */
  protected int changingDepth = 0;

  @Override
  public void draw(Graphics2D g) {
    if (attr().get(FILL_COLOR) != null) {
      var fillColor = attr().get(FILL_COLOR);
      Float opacity = attr().get(OPACITY);
      if (opacity < 1) {
        fillColor = new Color(fillColor.getRGB() & 0xffffff | ((int) (opacity * 256) << 24), true);
      }
      g.setColor(fillColor);
      drawFill(g);
    }
    if (attr().get(STROKE_COLOR) != null && attr().get(STROKE_WIDTH) >= 0d) {
      g.setStroke(AttributeKeys.getStroke(this, AttributeKeys.getScaleFactorFromGraphics(g)));
      g.setColor(attr().get(STROKE_COLOR));
      drawStroke(g);
    }
    if (attr().get(TEXT_COLOR) != null) {
      if (attr().get(TEXT_SHADOW_COLOR) != null && attr().get(TEXT_SHADOW_OFFSET) != null) {
        Dimension2DDouble d = attr().get(TEXT_SHADOW_OFFSET);
        g.translate(d.width, d.height);
        g.setColor(attr().get(TEXT_SHADOW_COLOR));
        drawText(g);
        g.translate(-d.width, -d.height);
      }
      g.setColor(attr().get(TEXT_COLOR));
      drawText(g);
    }
  }

  public double getStrokeMiterLimitFactor() {
    Number value = (Number) attr().get(AttributeKeys.STROKE_MITER_LIMIT);
    return (value != null) ? value.doubleValue() : 10f;
  }

  @Override
  public final Rectangle2D.Double getDrawingArea() {
    return getDrawingArea(AttributeKeys.scaleFromContext(this));
  }

  @Override
  public final Rectangle2D.Double getBounds() {
    return getBounds(AttributeKeys.scaleFromContext(this));
  }

  @Override
  public Rectangle2D.Double getDrawingArea(double scale) {
    //    double strokeTotalWidth = AttributeKeys.getStrokeTotalWidth(this, factor);
    //    double width = strokeTotalWidth / 2d;
    //    if (attr().get(STROKE_JOIN) == BasicStroke.JOIN_MITER) {
    //      width *= attr().get(STROKE_MITER_LIMIT);
    //    } else if (attr().get(STROKE_CAP) != BasicStroke.CAP_BUTT) {
    //      width += strokeTotalWidth * 2;
    //    }
    //    width++;
    Rectangle2D.Double r = getBounds(scale);
    double grow = AttributeKeys.getPerpendicularHitGrowth(this, scale) * 1.1 + 1;
    Geom.grow(r, grow, grow);
    return r;
  }

  /**
   * This method is called by method draw() to draw the fill area of the figure.
   * AbstractAttributedFigure configures the Graphics2D object with the FILL_COLOR attribute before
   * calling this method. If the FILL_COLOR attribute is null, this method is not called.
   */
  protected void drawFill(java.awt.Graphics2D g) {}

  /**
   * This method is called by method draw() to draw the lines of the figure . AttributedFigure
   * configures the Graphics2D object with the STROKE_COLOR attribute before calling this method. If
   * the STROKE_COLOR attribute is null, this method is not called.
   */
  protected void drawStroke(java.awt.Graphics2D g) {}

  /**
   * This method is called by method draw() to draw the text of the figure .
   * AbstractAttributedFigure configures the Graphics2D object with the TEXT_COLOR attribute before
   * calling this method. If the TEXT_COLOR attribute is null, this method is not called.
   */
  protected void drawText(java.awt.Graphics2D g) {}

  @Override
  public AbstractAttributedFigure clone() {
    AbstractAttributedFigure that;
    try {
      that = (AbstractAttributedFigure) super.clone();
    } catch (CloneNotSupportedException ex) {
      throw new InternalError("clone failed", ex);
    }
    that.attributes = Attributes.from(attributes, that::fireAttributeChanged);
    that.listenerList = new EventListenerList();
    that.drawing = null; // Clones need to be explictly added to a drawing
    return that;
  }

  @Override
  public void addFigureListener(FigureListener l) {
    if (Stream.of(listenerList.getListeners(FigureListener.class))
        .noneMatch(listener -> listener.equals(l))) {
      listenerList.add(FigureListener.class, l);
    }
  }

  @Override
  public void removeFigureListener(FigureListener l) {
    listenerList.remove(FigureListener.class, l);
  }

  @Override
  public void addNotify(Drawing d) {
    this.drawing = d;
    fireFigureAdded();
  }

  @Override
  public void removeNotify(Drawing d) {
    fireFigureRemoved();
    this.drawing = null;
  }

  public Drawing getDrawing() {
    return drawing;
  }

  private boolean modified = false;

  public final boolean isModified() {
    return modified;
  }

  public void setModified() {
    modified = true;
  }

  public void resetModified() {
    modified = false;
  }

  //  protected Object getLock() {
  //    return (getDrawing() == null) ? this : getDrawing().getLock();
  //  }

  /** tool method to process a listener and create its event object lazily. */
  protected void fireFigureEvent(
      BiConsumer<FigureListener, FigureEvent> listenerConsumer,
      Supplier<FigureEvent> eventSupplier) {
    FigureEvent event = null;
    if (listenerList.getListenerCount() == 0) {
      return;
    }
    for (FigureListener listener : listenerList.getListeners(FigureListener.class)) {
      if (event == null) {
        event = eventSupplier.get();
      }
      listenerConsumer.accept(listener, event);
    }
  }

  /** Notify all listenerList that have registered interest for notification on this event type. */
  public void fireAreaInvalidated() {
    fireAreaInvalidated(getDrawingArea());
  }

  /** Notify all listenerList that have registered interest for notification on this event type. */
  protected void fireAreaInvalidated(Rectangle2D.Double invalidatedArea) {
    fireFigureEvent(
        (listener, event) -> listener.areaInvalidated(event),
        () -> new FigureEvent(this, invalidatedArea));
  }

  /** Notify all listenerList that have registered interest for notification on this event type. */
  protected void fireAreaInvalidated(FigureEvent event) {
    for (FigureListener listener : listenerList.getListeners(FigureListener.class)) {
      listener.areaInvalidated(event);
    }
  }

  /** Notify all listenerList that have registered interest for notification on this event type. */
  protected void fireFigureRequestRemove() {
    fireFigureEvent(
        (listener, event) -> listener.figureRequestRemove(event),
        () -> new FigureEvent(this, getBounds(AttributeKeys.scaleFromContext(this))));
  }

  /** Notify all listenerList that have registered interest for notification on this event type. */
  protected void fireFigureAdded() {
    fireFigureEvent(
        (listener, event) -> listener.figureAdded(event),
        () -> new FigureEvent(this, getBounds(AttributeKeys.scaleFromContext(this))));
  }

  /** Notify all listenerList that have registered interest for notification on this event type. */
  protected void fireFigureRemoved() {
    fireFigureEvent(
        (listener, event) -> listener.figureRemoved(event),
        () -> new FigureEvent(this, getBounds(AttributeKeys.scaleFromContext(this))));
  }

  public void fireFigureChanged() {
    fireFigureChanged(getDrawingArea());
  }

  /** Notify all listenerList that have registered interest for notification on this event type. */
  protected void fireFigureChanged(Rectangle2D.Double changedArea) {
    fireFigureEvent(
        (listener, event) -> listener.figureChanged(event),
        () -> new FigureEvent(this, changedArea));
  }

  protected void fireFigureChanged(FigureEvent event) {
    fireFigureEvent((listener, evt) -> listener.figureChanged(evt), () -> event);
  }

  /** Notify all listenerList that have registered interest for notification on this event type. */
  protected <T> void fireAttributeChanged(AttributeKey<T> attribute, T oldValue, T newValue) {
    fireFigureEvent(
        (listener, event) -> listener.attributeChanged(event),
        () -> new FigureEvent(this, attribute, oldValue, newValue));
  }

  /** Notify all listenerList that have registered interest for notification on this event type. */
  protected void fireFigureHandlesChanged() {
    fireFigureEvent(
        (listener, event) -> listener.figureHandlesChanged(event),
        () -> new FigureEvent(this, getDrawingArea()));
  }

  /**
   * Notify all UndoableEditListener of the Drawing, to which this Figure has been added to. If this
   * Figure is not part of a Drawing, the event is lost.
   */
  protected void fireUndoableEditHappened(UndoableEdit edit) {
    if (getDrawing() != null) {
      getDrawing().fireUndoableEditHappened(edit);
    }
  }

  @Override
  public void remap(Map<Figure, Figure> oldToNew, boolean disconnectIfNotInMap) {}

  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    List<Handle> handles = new ArrayList<>();
    switch (detailLevel) {
      case -1:
        handles.add(new BoundsOutlineHandle(this, false, true));
        break;
      case 0:
        ResizeHandleKit.addResizeHandles(this, handles);
        break;
    }
    return handles;
  }

  @Override
  public Cursor getCursor(Point2D.Double p, double scaleDenominator) {
    if (contains(p, scaleDenominator)) {
      return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    } else {
      return Cursor.getDefaultCursor();
    }
  }

  public final void setBounds(Rectangle2D.Double bounds) {
    setBounds(
        new Point2D.Double(bounds.x, bounds.y),
        new Point2D.Double(bounds.x + bounds.width, bounds.y + bounds.height));
  }

  @Override
  public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
    Point2D.Double oldAnchor = getStartPoint();
    Point2D.Double oldLead = getEndPoint();
    if (!oldAnchor.equals(anchor) || !oldLead.equals(lead)) {
      willChange();
      setBounds(anchor, lead);
      changed();
      fireUndoableEditHappened(new SetBoundsEdit(this, oldAnchor, oldLead, anchor, lead));
    }
  }

  /**
   * Invalidates cached data of the Figure. This method must execute fast, because it can be called
   * very often.
   */
  protected void invalidate() {}

  protected boolean isChanging() {
    return changingDepth != 0;
  }

  protected int getChangingDepth() {
    return changingDepth;
  }

  @Override
  public boolean contains(Point2D.Double p) {
    return contains(p, AttributeKeys.scaleFromContext(this));
  }

  /**
   * Informs that a figure is about to change something that affects the contents of its display
   * box.
   */
  @Override
  public void willChange() {
    if (changingDepth == 0) {
      fireAreaInvalidated();
      invalidate();
    }
    changingDepth++;
  }

  protected void validate() {}

  /** Informs that a figure changed the area of its display box. */
  @Override
  public void changed() {
    if (changingDepth == 1) {
      validate();
      fireFigureChanged(getDrawingArea());
    } else if (changingDepth < 1) {
      throw new IllegalStateException(
          "changed was called without a prior call to willChange. " + changingDepth);
    }
    modified = true;
    changingDepth--;
  }

  /**
   * Returns the Figures connector for the specified location. By default a ChopBoxConnector is
   * returned.
   *
   * @see ChopRectangleConnector
   */
  @Override
  public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
    return new ChopRectangleConnector(this);
  }

  @Override
  public boolean includes(Figure figure) {
    return figure == this;
  }

  @Override
  public Figure findFigureInside(Point2D.Double p) {
    return (contains(p)) ? this : null;
  }

  @Override
  public Connector findCompatibleConnector(Connector c, boolean isStart) {
    return new ChopRectangleConnector(this);
  }

  /**
   * Returns a collection of actions which are presented to the user in a popup menu.
   *
   * <p>The collection may contain null entries. These entries are used interpreted as separators in
   * the popup menu.
   */
  @Override
  public Collection<Action> getActions(Point2D.Double p) {
    return Collections.emptyList();
  }

  /**
   * Returns a specialized tool for the given coordinate.
   *
   * <p>Returns null, if no specialized tool is available.
   */
  @Override
  public Tool getTool(Point2D.Double p) {
    return null;
  }

  /** Handles a mouse click. */
  @Override
  public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
    return false;
  }

  @Override
  public boolean handleDrop(Point2D.Double p, Collection<Figure> droppedFigures, DrawingView view) {
    return false;
  }

  @Override
  public Point2D.Double getEndPoint() {
    Rectangle2D.Double r = getBounds(AttributeKeys.scaleFromContext(this));
    return new Point2D.Double(r.x + r.width, r.y + r.height);
  }

  @Override
  public Point2D.Double getStartPoint() {
    Rectangle2D.Double r = getBounds(AttributeKeys.scaleFromContext(this));
    return new Point2D.Double(r.x, r.y);
  }

  /*
  public Rectangle2D.Double getHitBounds() {
  return getBounds();
  }
     */
  @Override
  public Dimension2DDouble getPreferredSize(double scale) {
    Rectangle2D.Double r = getBounds(scale);
    return new Dimension2DDouble(r.width, r.height);
  }

  /**
   * Checks whether this figure is connectable. By default {@code AbstractFigure} can be connected.
   */
  @Override
  public boolean isConnectable() {
    return isConnectable;
  }

  public void setConnectable(boolean newValue) {
    boolean oldValue = isConnectable;
    isConnectable = newValue;
  }

  /**
   * Checks whether this figure is selectable. By default {@code AbstractFigure} can be selected.
   */
  @Override
  public boolean isSelectable() {
    return isSelectable;
  }

  public void setSelectable(boolean newValue) {
    boolean oldValue = isSelectable;
    isSelectable = newValue;
  }

  /** Checks whether this figure is removable. By default {@code AbstractFigure} can be removed. */
  @Override
  public boolean isRemovable() {
    return isRemovable;
  }

  public void setRemovable(boolean newValue) {
    boolean oldValue = isRemovable;
    isRemovable = newValue;
  }

  /**
   * Checks whether this figure is transformable. By default {@code AbstractFigure} can be
   * transformed.
   */
  @Override
  public boolean isTransformable() {
    return isTransformable;
  }

  public void setTransformable(boolean newValue) {
    boolean oldValue = isTransformable;
    isTransformable = newValue;
  }

  /** Checks whether this figure is visible. By default {@code AbstractFigure} is visible. */
  @Override
  public boolean isVisible() {
    return isVisible;
  }

  public void setVisible(boolean newValue) {
    if (newValue != isVisible) {
      willChange();
      isVisible = newValue;
      changed();
    }
  }

  protected FontRenderContext getFontRenderContext() {
    FontRenderContext frc = null;
    if (frc == null) {
      frc = new FontRenderContext(new AffineTransform(), true, true);
    }
    return frc;
  }

  @Override
  public void requestRemove() {
    fireFigureRequestRemove();
  }

  /**
   * AbstractFigure always returns 0. Override this method if your figure needs to be on a different
   * layer.
   */
  @Override
  public int getLayer() {
    return 0;
  }

  @Override
  public String getToolTipText(Point2D.Double p) {
    return null;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append(getClass().getName().substring(getClass().getName().lastIndexOf('.') + 1));
    buf.append('@');
    buf.append(hashCode());
    return buf.toString();
  }

  @Override
  public Collection<Connector> getConnectors(ConnectionFigure prototype) {
    List<Connector> connectors = new ArrayList<>();
    connectors.add(new ChopRectangleConnector(this));
    return connectors;
  }
}
