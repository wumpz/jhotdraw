/*
 * @(#)AbstractCompositeFigure.java
 *
 * Copyright (c) 2007-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.event.CompositeFigureEvent;
import org.jhotdraw.draw.event.CompositeFigureListener;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.event.FigureListenerAdapter;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.TransformHandleKit;
import org.jhotdraw.draw.layouter.Layouter;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.util.ReversedList;

/**
 * This abstract class can be extended to implement a {@link CompositeFigure}.
 * AbstractCompositeFigure.
 */
public abstract class AbstractAttributedCompositeFigure extends AbstractAttributedFigure
    implements CompositeFigure {

  private static final long serialVersionUID = 1L;
  /** A Layouter determines how the children of the CompositeFigure are laid out graphically. */
  protected Layouter layouter;

  protected List<Figure> children = new ArrayList<>();
  /** Caches the drawing area to improve the performance of method {@link #getDrawingArea}. */
  protected transient Rectangle2D.Double cachedDrawingArea;
  /** Caches the bounds to improve the performance of method {@link #getBounds}. */
  protected transient Rectangle2D.Double cachedBounds;
  /** Handles figure changes in the children. */
  protected EventHandler eventHandler;

  protected class EventHandler extends FigureListenerAdapter
      implements UndoableEditListener, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public void figureRequestRemove(FigureEvent e) {
      remove(e.getFigure());
    }

    @Override
    public void figureChanged(FigureEvent e) {
      if (!isChanging()) {
        Rectangle2D.Double invalidatedArea = getDrawingArea();
        invalidatedArea.add(e.getInvalidatedArea());
        // We call invalidate/validate here, because we must layout
        // the figure again.
        invalidate();
        validate();
        // Forward the figureChanged event to listeners on AbstractCompositeFigure.
        invalidatedArea.add(getDrawingArea());
        fireFigureChanged(invalidatedArea);
      }
    }

    @Override
    public void areaInvalidated(FigureEvent e) {
      fireAreaInvalidated(e);
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
      fireUndoableEditHappened(e.getEdit());
    }

    @Override
    public void attributeChanged(FigureEvent e) {
      invalidate();
    }

    @Override
    public void figureAdded(FigureEvent e) {
      invalidate();
    }

    @Override
    public void figureRemoved(FigureEvent e) {
      invalidate();
    }
  }

  public AbstractAttributedCompositeFigure() {
    eventHandler = createEventHandler();
  }

  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    List<Handle> handles = new ArrayList<>();
    if (detailLevel == 0) {
      handles.add(new BoundsOutlineHandle(this, true, false));
      TransformHandleKit.addScaleMoveTransformHandles(this, handles);
    }
    return handles;
  }

  protected EventHandler createEventHandler() {
    return new EventHandler();
  }

  @Override
  public boolean add(Figure figure) {
    add(getChildCount(), figure);
    return true;
  }

  @Override
  public void add(int index, Figure figure) {
    basicAdd(index, figure);
    if (getDrawing() != null) {
      figure.addNotify(getDrawing());
    }
    fireFigureAdded(figure, index);
    invalidate();
  }

  public void addAll(Collection<? extends Figure> figures) {
    addAll(getChildCount(), figures);
  }

  public final void addAll(int index, Collection<? extends Figure> figures) {
    for (Figure f : figures) {
      basicAdd(index++, f);
      if (getDrawing() != null) {
        f.addNotify(getDrawing());
      }
      fireFigureAdded(f, index);
    }
    invalidate();
  }

  @Override
  public void basicAdd(Figure figure) {
    basicAdd(getChildCount(), figure);
  }

  public void basicAddAll(int index, Collection<? extends Figure> newFigures) {
    for (Figure f : newFigures) {
      basicAdd(index++, f);
    }
  }

  @Override
  public void addNotify(Drawing drawing) {
    super.addNotify(drawing);
    for (Figure child : getChildren()) {
      child.addNotify(drawing);
    }
  }

  @Override
  public void removeNotify(Drawing drawing) {
    super.removeNotify(drawing);
    // Copy children collection to avoid concurrent modification exception
    for (Figure child : new ArrayList<>(getChildren())) {
      child.removeNotify(drawing);
    }
  }

  @Override
  public boolean remove(final Figure figure) {
    int index = children.indexOf(figure);
    if (index == -1) {
      return false;
    } else {
      basicRemoveChild(index);
      if (getDrawing() != null) {
        figure.removeNotify(getDrawing());
      }
      fireFigureRemoved(figure, index);
      return true;
    }
  }

  @Override
  public Figure removeChild(int index) {
    Figure removed = basicRemoveChild(index);
    if (getDrawing() != null) {
      removed.removeNotify(getDrawing());
    }
    return removed;
  }

  /**
   * Removes all specified children.
   *
   * @see #add
   */
  public void removeAll(Collection<? extends Figure> figures) {
    willChange();
    for (Figure f : new ArrayList<Figure>(figures)) {
      remove(f);
    }
    changed();
  }

  /**
   * Removes all children.
   *
   * @see #add
   */
  @Override
  public void removeAllChildren() {
    removeAll(getChildren());
  }

  /**
   * Removes all children.
   *
   * @see #add
   */
  @Override
  public void basicRemoveAllChildren() {
    for (Figure f : new ArrayList<>(getChildren())) {
      basicRemove(f);
    }
  }

  /**
   * Removes all children.
   *
   * @see #add
   */
  public void basicRemoveAll(Collection<? extends Figure> figures) {
    for (Figure f : figures) {
      basicRemove(f);
    }
  }

  /**
   * Sends a figure to the back of the composite figure.
   *
   * @param figure that is part of this composite figure
   */
  public void sendToBack(Figure figure) {
    if (basicRemove(figure) != -1) {
      basicAdd(0, figure);
      fireAreaInvalidated(figure.getDrawingArea());
    }
  }

  /**
   * Brings a figure to the front of the drawing.
   *
   * @param figure that is part of the drawing
   */
  public void bringToFront(Figure figure) {
    if (basicRemove(figure) != -1) {
      basicAdd(figure);
      fireAreaInvalidated(figure.getDrawingArea());
    }
  }

  /** Transforms the figure. */
  @Override
  public void transform(AffineTransform tx) {
    for (Figure f : getChildren()) {
      f.transform(tx);
    }
    invalidate();
    // invalidate();
  }

  @Override
  public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
    Rectangle2D.Double oldBounds = getBounds();
    Rectangle2D.Double newBounds =
        new Rectangle2D.Double(
            Math.min(anchor.x, lead.x),
            Math.min(anchor.y, lead.y),
            Math.abs(anchor.x - lead.x),
            Math.abs(anchor.y - lead.y));
    double sx = newBounds.width / oldBounds.width;
    double sy = newBounds.height / oldBounds.height;
    AffineTransform tx = new AffineTransform();
    tx.translate(-oldBounds.x, -oldBounds.y);
    if (!Double.isNaN(sx)
        && !Double.isNaN(sy)
        && !Double.isInfinite(sx)
        && !Double.isInfinite(sy)
        && (sx != 1d || sy != 1d)
        && !(sx < 0.0001)
        && !(sy < 0.0001)) {
      transform(tx);
      tx.setToIdentity();
      tx.scale(sx, sy);
      transform(tx);
      tx.setToIdentity();
    }
    tx.translate(newBounds.x, newBounds.y);
    transform(tx);
  }

  /** Returns an iterator to iterate in Z-order front to back over the children. */
  public java.util.List<Figure> getChildrenFrontToBack() {
    return children.size() == 0 ? new ArrayList<>() : new ReversedList<>(getChildren());
  }

  @Override
  public boolean contains(Figure f) {
    return children.contains(f);
  }

  @Override
  public boolean contains(Point2D.Double p, double scaleDenominator) {
    if (getDrawingArea().contains(p)) {
      if (attr().get(TRANSFORM) != null) {
        try {
          p = (Point2D.Double) attr().get(TRANSFORM).inverseTransform(p, new Point2D.Double());
        } catch (NoninvertibleTransformException ex) {
          InternalError error = new InternalError(ex.getMessage());
          error.initCause(ex);
          throw error;
        }
      }
      for (Figure child : getChildren()) {
        if (child.isVisible() && child.contains(p, scaleDenominator)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public Figure findFigureInside(Point2D.Double p) {
    if (getDrawingArea().contains(p)) {
      Figure found = null;
      for (Figure child : getChildrenFrontToBack()) {
        if (child.isVisible()) {
          found = child.findFigureInside(p);
          if (found != null) {
            return found;
          }
        }
      }
    }
    return null;
  }

  public Figure findChild(Point2D.Double p) {
    if (getBounds().contains(p)) {
      Figure found = null;
      for (Figure child : getChildrenFrontToBack()) {
        if (child.isVisible() && child.contains(p)) {
          return child;
        }
      }
    }
    return null;
  }

  public int findChildIndex(Point2D.Double p) {
    Figure child = findChild(p);
    return (child == null) ? -1 : children.indexOf(child);
  }

  /**
   * Get a Layouter object which encapsulated a layout algorithm for this figure. Typically, a
   * Layouter accesses the child components of this figure and arranges their graphical
   * presentation.
   *
   * @return layout strategy used by this figure
   */
  @Override
  public Layouter getLayouter() {
    return layouter;
  }

  /**
   * A layout algorithm is used to define how the child components should be laid out in relation to
   * each other. The task for layouting the child components for presentation is delegated to a
   * Layouter which can be plugged in at runtime.
   */
  @Override
  public void layout() {
    // Note: We increase and below decrease the changing depth here,
    //       because we want to ignore change events from our children
    //       why we lay them out.
    changingDepth++;
    for (Figure child : getChildren()) {
      if (child instanceof CompositeFigure) {
        CompositeFigure cf = (CompositeFigure) child;
        cf.layout();
      }
    }
    changingDepth--;
    if (getLayouter() != null) {
      Rectangle2D.Double bounds = getBounds();
      Point2D.Double p = new Point2D.Double(bounds.x, bounds.y);
      Rectangle2D.Double r = getLayouter().layout(this, p, p);
      setBounds(new Point2D.Double(r.x, r.y), new Point2D.Double(r.x + r.width, r.y + r.height));
      invalidate();
    }
  }

  /**
   * Set a Layouter object which encapsulated a layout algorithm for this figure. Typically, a
   * Layouter accesses the child components of this figure and arranges their graphical
   * presentation. It is a good idea to set the Layouter in the protected initialize() method so it
   * can be recreated if a GraphicalCompositeFigure is read and restored from a StorableInput
   * stream.
   *
   * @param newLayouter encapsulation of a layout algorithm.
   */
  @Override
  public void setLayouter(Layouter newLayouter) {
    this.layouter = newLayouter;
  }

  @Override
  public Dimension2DDouble getPreferredSize() {
    if (this.layouter != null) {
      Rectangle2D.Double r = layouter.calculateLayout(this, getStartPoint(), getEndPoint());
      return new Dimension2DDouble(r.width, r.height);
    } else {
      return super.getPreferredSize();
    }
  }

  @Override
  public void draw(Graphics2D g) {
    Rectangle2D clipBounds = g.getClipBounds();
    if (clipBounds != null) {
      for (Figure child : getChildren()) {
        if (child.isVisible() && child.getDrawingArea().intersects(clipBounds)) {
          child.draw(g);
        }
      }
    } else {
      for (Figure child : getChildren()) {
        if (child.isVisible()) {
          child.draw(g);
        }
      }
    }
  }

  @Override
  public void restoreTransformTo(Object geometry) {
    @SuppressWarnings("unchecked")
    List list = (ArrayList) geometry;
    Iterator<Object> i = list.iterator();
    for (Figure child : getChildren()) {
      child.restoreTransformTo(i.next());
    }
    invalidate();
  }

  @Override
  public Object getTransformRestoreData() {
    List<Object> list = new ArrayList<>();
    for (Figure child : getChildren()) {
      list.add(child.getTransformRestoreData());
    }
    return list;
  }

  @Override
  public void basicAdd(int index, Figure figure) {
    children.add(index, figure);
    figure.addFigureListener(eventHandler);
  }

  @Override
  public Figure basicRemoveChild(int index) {
    Figure figure = children.remove(index);
    figure.removeFigureListener(eventHandler);
    invalidate();
    return figure;
  }

  @Override
  public List<Figure> getChildren() {
    return Collections.unmodifiableList(children);
  }

  @Override
  public int getChildCount() {
    return children.size();
  }

  @Override
  public Figure getChild(int index) {
    return children.get(index);
  }

  @Override
  public AbstractAttributedCompositeFigure clone() {
    AbstractAttributedCompositeFigure that = (AbstractAttributedCompositeFigure) super.clone();
    that.attributes =
        Attributes.from(
            attributes,
            that::fireAttributeChanged,
            Attributes.attrSupplier(() -> that.getChildren()));
    that.children = new ArrayList<>();
    that.eventHandler = that.createEventHandler();
    for (Figure thisChild : this.children) {
      Figure thatChild = thisChild.clone();
      that.children.add(thatChild);
      thatChild.addFigureListener(that.eventHandler);
    }
    return that;
  }

  @Override
  protected void validate() {
    super.validate();
    layout();
  }

  @Override
  protected void invalidate() {
    cachedBounds = null;
    cachedDrawingArea = null;
  }

  @Override
  public void willChange() {
    super.willChange();
    for (Figure child : children) {
      child.willChange();
    }
  }

  @Override
  public void changed() {
    for (Figure child : children) {
      child.changed();
    }
    super.changed();
  }

  @Override
  public int basicRemove(Figure child) {
    int index = children.indexOf(child);
    if (index != -1) {
      basicRemoveChild(index);
    }
    return index;
  }

  @Override
  public int indexOf(Figure child) {
    return children.indexOf(child);
  }

  @Override
  public Rectangle2D.Double getDrawingArea(double factor) {
    if (cachedDrawingArea == null) {
      if (getChildCount() == 0) {
        cachedDrawingArea = new Rectangle2D.Double();
      } else {
        for (Figure f : children) {
          if (cachedDrawingArea == null) {
            cachedDrawingArea = f.getDrawingArea(factor);
          } else {
            cachedDrawingArea.add(f.getDrawingArea(factor));
          }
        }
      }
    }
    return new Rectangle2D.Double(
        cachedDrawingArea.x,
        cachedDrawingArea.y,
        cachedDrawingArea.width,
        cachedDrawingArea.height);
  }

  @Override
  public Rectangle2D.Double getBounds() {
    if (cachedBounds == null) {
      if (getChildCount() == 0) {
        cachedBounds = new Rectangle2D.Double();
      } else {
        for (Figure f : children) {
          if (cachedBounds == null) {
            cachedBounds = f.getBounds();
          } else {
            cachedBounds.add(f.getBounds());
          }
        }
      }
    }
    return (Rectangle2D.Double) cachedBounds.clone();
  }

  /** tool method to process a listener and create its event object lazily. */
  protected void fireCompositeFigureEvent(
      BiConsumer<CompositeFigureListener, CompositeFigureEvent> listenerConsumer,
      Supplier<CompositeFigureEvent> eventSupplier) {
    CompositeFigureEvent event = null;
    if (listenerList.getListenerCount() == 0) {
      return;
    }
    for (CompositeFigureListener listener :
        listenerList.getListeners(CompositeFigureListener.class)) {
      if (event == null) {
        event = eventSupplier.get();
      }
      listenerConsumer.accept(listener, event);
    }
  }

  /** Notify all listenerList that have registered interest for notification on this event type. */
  protected void fireFigureAdded(Figure f, int zIndex) {
    fireCompositeFigureEvent(
        (listener, event) -> listener.figureAdded(event),
        () -> new CompositeFigureEvent(this, f, f.getDrawingArea(), zIndex));
  }

  /** Notify all listenerList that have registered interest for notification on this event type. */
  protected void fireFigureRemoved(Figure f, int zIndex) {
    fireCompositeFigureEvent(
        (listener, event) -> listener.figureRemoved(event),
        () -> new CompositeFigureEvent(this, f, f.getDrawingArea(), zIndex));
  }

  @Override
  public void removeCompositeFigureListener(CompositeFigureListener listener) {
    listenerList.remove(CompositeFigureListener.class, listener);
  }

  @Override
  public void addCompositeFigureListener(CompositeFigureListener listener) {
    listenerList.add(CompositeFigureListener.class, listener);
  }

  private Attributes attributes =
      new Attributes(
          this::fireAttributeChanged,
          Attributes.attrSupplier(() -> AbstractAttributedCompositeFigure.this.getChildren()));

  @Override
  public Attributes attr() {
    return attributes;
  }

  @Override
  protected <T> void fireAttributeChanged(AttributeKey<T> attribute, T oldValue, T newValue) {
    super.fireAttributeChanged(
        attribute, oldValue,
        newValue); // To change body of generated methods, choose Tools | Templates.
  }

  public void drawFigure(Graphics2D g) {
    drawChildren(g);
    if (attr().get(FILL_COLOR) != null) {
      g.setColor(attr().get(FILL_COLOR));
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

  protected void drawChildren(Graphics2D g) {
    for (Figure child : getChildren()) {
      child.draw(g);
    }
  }

  public Stroke getStroke() {
    return AttributeKeys.getStroke(this, 1.0);
  }

  @Override
  public double getStrokeMiterLimitFactor() {
    Number value = (Number) attr().get(AttributeKeys.STROKE_MITER_LIMIT);
    return (value != null) ? value.doubleValue() : 10f;
  }

  public Rectangle2D.Double getFigureDrawBounds() {
    double width = AttributeKeys.getStrokeTotalWidth(this, 1.0) / 2d;
    if (attr().get(STROKE_JOIN) == BasicStroke.JOIN_MITER) {
      width *= attr().get(STROKE_MITER_LIMIT);
    }
    width++;
    Rectangle2D.Double r = getBounds();
    Geom.grow(r, width, width);
    return r;
  }
}
