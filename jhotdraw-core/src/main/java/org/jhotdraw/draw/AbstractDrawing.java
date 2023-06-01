/*
 * @(#)AbstractDrawing.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import org.jhotdraw.draw.event.DrawingEvent;
import org.jhotdraw.draw.event.DrawingListener;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.event.FigureListenerAdapter;
import org.jhotdraw.draw.figure.Attributes;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.draw.io.OutputFormat;

public abstract class AbstractDrawing implements Drawing {

  private static final Object LOCK = new JPanel().getTreeLock();

  /** Caches the bounds to improve the performance of method {@link #getBounds}. */
  protected transient Rectangle2D.Double cachedBounds;

  protected transient Rectangle2D.Double cachedDrawingArea;
  protected int changingDepth = 0;
  protected final List<Figure> CHILDREN = new ArrayList<>();
  protected final List<Figure> UNMODIFIABLE_CHILDREN = Collections.unmodifiableList(CHILDREN);

  protected EventHandler eventHandler = new EventHandler();
  protected EventListenerList listenerList = new EventListenerList();
  private Attributes attributes = new Attributes(this::fireDrawingAttributeChanged);
  private transient FontRenderContext fontRenderContext;
  private List<InputFormat> inputFormats = new ArrayList<>();
  private List<OutputFormat> outputFormats = new ArrayList<>();

  public AbstractDrawing() {
    eventHandler = createEventHandler();
  }

  @Override
  public boolean add(Figure figure) {
    add(getChildCount(), figure);
    return true;
  }

  @Override
  public void add(int index, Figure figure) {
    basicAdd(index, figure);
    figure.addNotify(this);
    fireFigureAdded(figure, index);
    invalidate();
  }

  @Override
  public void addAll(Collection<? extends Figure> figures) {
    addAll(getChildCount(), figures);
  }

  @Override
  public void addDrawingListener(DrawingListener listener) {
    listenerList.add(DrawingListener.class, listener);
  }

  @Override
  public void addInputFormat(InputFormat format) {
    inputFormats.add(format);
  }

  @Override
  public void addOutputFormat(OutputFormat format) {
    outputFormats.add(format);
  }

  @Override
  public void addUndoableEditListener(UndoableEditListener l) {
    listenerList.add(UndoableEditListener.class, l);
  }

  @Override
  public Attributes attr() {
    return attributes;
  }

  @Override
  public void basicAdd(int index, Figure figure) {
    CHILDREN.add(index, figure);
    figure.addFigureListener(eventHandler);
  }

  @Override
  public void basicAdd(Figure figure) {
    basicAdd(getChildCount(), figure);
  }

  @Override
  public void basicAddAll(int index, Collection<? extends Figure> figures) {
    for (Figure f : figures) {
      basicAdd(index++, f);
    }
  }

  @Override
  public void basicRemoveAll(Collection<? extends Figure> figures) {
    for (Figure f : new ArrayList<>(getChildren())) {
      basicRemove(f);
    }
  }

  @Override
  public void bringToFront(Figure figure) {
    if (basicRemove(figure) != -1) {
      basicAdd(figure);
      fireDrawingChanged(figure.getDrawingArea());
    }
  }

  @Override
  public void changed() {
    if (changingDepth == 1) {
      validate();
      fireDrawingChanged(getDrawingArea());
    } else if (changingDepth < 1) {
      throw new IllegalStateException(
          "changed was called without a prior call to willChange. " + changingDepth);
    }
    changingDepth--;
  }

  @Override
  @SuppressWarnings("unchecked")
  public AbstractDrawing clone() {
    AbstractDrawing that;
    try {
      that = (AbstractDrawing) super.clone();
    } catch (CloneNotSupportedException ex) {
      throw new InternalError("clone failed", ex);
    }
    that.attributes = Attributes.from(attributes, that::fireDrawingAttributeChanged);
    that.listenerList = new EventListenerList();

    that.inputFormats = (this.inputFormats == null) ? null : new ArrayList<>(this.inputFormats);
    that.outputFormats = (this.outputFormats == null) ? null : new ArrayList<>(this.outputFormats);
    return that;
  }

  /** Notify all listenerList that have registered interest for notification on this event type. */
  @Override
  public void fireUndoableEditHappened(UndoableEdit edit) {
    UndoableEditEvent event = null;
    if (listenerList.getListenerCount() > 0) {
      // Notify all listeners that have registered interest for
      // Guaranteed to return a non-null array
      Object[] listeners = listenerList.getListenerList();
      // Process the listeners last to first, notifying
      // those that are interested in this event
      for (int i = listeners.length - 2; i >= 0; i -= 2) {
        if (event == null) {
          event = new UndoableEditEvent(this, edit);
        }
        if (listeners[i] == UndoableEditListener.class) {
          ((UndoableEditListener) listeners[i + 1]).undoableEditHappened(event);
        }
      }
    }
  }

  @Override
  public Figure getChild(int index) {
    return CHILDREN.get(index);
  }

  @Override
  public int getChildCount() {
    return CHILDREN.size();
  }

  @Override
  public List<Figure> getChildren() {
    return UNMODIFIABLE_CHILDREN;
  }

  @Override
  public Rectangle2D.Double getDrawingArea() {
    return getDrawingArea(1.0);
  }

  @Override
  public Rectangle2D.Double getDrawingArea(double factor) {
    if (cachedDrawingArea == null) {
      if (getChildCount() == 0) {
        cachedDrawingArea = new Rectangle2D.Double();
      } else {
        for (Figure f : CHILDREN) {
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
  public FontRenderContext getFontRenderContext() {
    return fontRenderContext;
  }

  @Override
  public void setFontRenderContext(FontRenderContext frc) {
    fontRenderContext = frc;
  }

  @Override
  public List<InputFormat> getInputFormats() {
    return inputFormats;
  }

  @Override
  public void setInputFormats(List<InputFormat> formats) {
    this.inputFormats = new ArrayList<>(formats);
  }

  /** The drawing view synchronizes on the lock when drawing a drawing. */
  @Override
  public Object getLock() {
    return LOCK;
  }

  @Override
  public List<OutputFormat> getOutputFormats() {
    return outputFormats;
  }

  @Override
  public void setOutputFormats(List<OutputFormat> formats) {
    this.outputFormats = new ArrayList<>(formats);
  }

  @Override
  public boolean remove(Figure figure) {
    int index = CHILDREN.indexOf(figure);
    if (index == -1) {
      return false;
    } else {
      basicRemoveChild(index);
      figure.removeNotify(this);
      fireFigureRemoved(figure, index);
      return true;
    }
  }

  @Override
  public void removeAll(Collection<? extends Figure> figures) {
    willChange();
    for (Figure f : new ArrayList<Figure>(figures)) {
      remove(f);
    }
    changed();
  }

  @Override
  public void removeAllChildren() {
    for (Figure f : new ArrayList<>(getChildren())) {
      basicRemove(f);
    }
  }

  @Override
  public void removeDrawingListener(DrawingListener listener) {
    listenerList.remove(DrawingListener.class, listener);
  }

  @Override
  public void removeUndoableEditListener(UndoableEditListener l) {
    listenerList.remove(UndoableEditListener.class, l);
  }

  @Override
  public void sendToBack(Figure figure) {
    if (basicRemove(figure) != -1) {
      basicAdd(0, figure);
      fireDrawingChanged(figure.getDrawingArea());
    }
  }

  @Override
  public void willChange() {
    if (changingDepth == 0) {
      invalidate();
    }
    changingDepth++;
  }

  protected int basicRemove(Figure child) {
    int index = CHILDREN.indexOf(child);
    if (index != -1) {
      basicRemoveChild(index);
    }
    return index;
  }

  protected Figure basicRemoveChild(int index) {
    Figure figure = CHILDREN.remove(index);
    figure.removeFigureListener(eventHandler);
    invalidate();
    return figure;
  }

  protected EventHandler createEventHandler() {
    return new EventHandler();
  }

  protected <T> void fireDrawingAttributeChanged(
      AttributeKey<T> attribute, T oldValue, T newValue) {
    fireDrawingEvent(
        (listener, event) -> listener.drawingChanged(event),
        () -> new DrawingEvent(this, attribute, oldValue, newValue));
  }

  protected void fireDrawingChanged(Rectangle2D.Double changedArea) {
    fireDrawingEvent(
        (listener, event) -> listener.drawingChanged(event),
        () -> new DrawingEvent(this, changedArea));
  }

  protected void fireDrawingEvent(
      BiConsumer<DrawingListener, DrawingEvent> listenerConsumer,
      Supplier<DrawingEvent> eventSupplier) {
    DrawingEvent event = null;
    if (listenerList.getListenerCount() == 0) {
      return;
    }
    for (DrawingListener listener : listenerList.getListeners(DrawingListener.class)) {
      if (event == null) {
        event = eventSupplier.get();
      }
      listenerConsumer.accept(listener, event);
    }
  }

  protected void fireFigureAdded(Figure figure, int index) {
    fireDrawingEvent(
        (listener, event) -> listener.figureAdded(event),
        () -> new DrawingEvent(this, index, figure));
  }

  protected void fireFigureRemoved(Figure figure, int index) {
    fireDrawingEvent(
        (listener, event) -> listener.figureRemoved(event),
        () -> new DrawingEvent(this, index, figure));
  }

  protected int getChangingDepth() {
    return changingDepth;
  }

  protected void invalidate() {
    cachedBounds = null;
    cachedDrawingArea = null;
  }

  protected boolean isChanging() {
    return changingDepth != 0;
  }

  protected void validate() {}

  private final void addAll(int index, Collection<? extends Figure> figures) {
    for (Figure f : figures) {
      basicAdd(index++, f);
      f.addNotify(this);
      fireFigureAdded(f, index);
    }
    invalidate();
  }

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
        fireDrawingChanged(invalidatedArea);
      }
    }

    @Override
    public void areaInvalidated(FigureEvent e) {
      fireDrawingChanged(e.getInvalidatedArea());
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
}
