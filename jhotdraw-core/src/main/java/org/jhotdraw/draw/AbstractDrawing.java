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
import java.util.LinkedList;
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

/**
 * This abstract class can be extended to implement a {@link Drawing}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractDrawing implements Drawing {

  private static final long serialVersionUID = 1L;
  private static final Object LOCK = new JPanel().getTreeLock();
  private transient FontRenderContext fontRenderContext;
  private LinkedList<InputFormat> inputFormats = new LinkedList<>();
  private LinkedList<OutputFormat> outputFormats = new LinkedList<>();
  private static boolean debugMode = false;
  protected EventListenerList listenerList = new EventListenerList();
  protected ArrayList<Figure> children = new ArrayList<>();
  protected transient Rectangle2D.Double cachedDrawingArea;
  /** Caches the bounds to improve the performance of method {@link #getBounds}. */
  protected transient Rectangle2D.Double cachedBounds;

  private Attributes attributes = new Attributes(this::fireDrawingAttributeChanged);
  protected EventHandler eventHandler = new EventHandler();

  protected EventHandler createEventHandler() {
    return new EventHandler();
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

  //  protected void fireFigureEvent(
  //      BiConsumer<FigureListener, FigureEvent> listenerConsumer,
  //      Supplier<FigureEvent> eventSupplier) {
  //    FigureEvent event = null;
  //    if (listenerList.getListenerCount() == 0) {
  //      return;
  //    }
  //    for (FigureListener listener : listenerList.getListeners(FigureListener.class)) {
  //      if (event == null) {
  //        event = eventSupplier.get();
  //      }
  //      listenerConsumer.accept(listener, event);
  //    }
  //  }

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

  protected void fireDrawingChanged(Rectangle2D.Double changedArea) {
    fireDrawingEvent(
        (listener, event) -> listener.drawingChanged(event),
        () -> new DrawingEvent(this, changedArea));
  }

  protected <T> void fireDrawingAttributeChanged(
      AttributeKey<T> attribute, T oldValue, T newValue) {
    fireDrawingEvent(
        (listener, event) -> listener.drawingChanged(event),
        () -> new DrawingEvent(this, attribute, oldValue, newValue));
  }

  protected void fireFigureRemoved(Figure figure, int index) {
    fireDrawingEvent(
        (listener, event) -> listener.figureRemoved(event),
        () -> new DrawingEvent(this, index, figure));
  }

  protected void fireFigureAdded(Figure figure, int index) {
    fireDrawingEvent(
        (listener, event) -> listener.figureAdded(event),
        () -> new DrawingEvent(this, index, figure));
  }

  protected void invalidate() {
    cachedBounds = null;
    cachedDrawingArea = null;
  }

  protected int changingDepth = 0;

  protected boolean isChanging() {
    return changingDepth != 0;
  }

  protected int getChangingDepth() {
    return changingDepth;
  }

  protected void validate() {}

  public Attributes attr() {
    return attributes;
  }

  /** Creates a new instance. */
  public AbstractDrawing() {
    eventHandler = createEventHandler();
  }

  @Override
  public void basicAdd(int index, Figure figure) {
    children.add(index, figure);
    figure.addFigureListener(eventHandler);
  }

  @Override
  public void addUndoableEditListener(UndoableEditListener l) {
    listenerList.add(UndoableEditListener.class, l);
  }

  @Override
  public void removeUndoableEditListener(UndoableEditListener l) {
    listenerList.remove(UndoableEditListener.class, l);
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
  public java.util.List<Figure> getChildren() {
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
  public FontRenderContext getFontRenderContext() {
    return fontRenderContext;
  }

  @Override
  public void setFontRenderContext(FontRenderContext frc) {
    fontRenderContext = frc;
  }

  /** The drawing view synchronizes on the lock when drawing a drawing. */
  @Override
  public Object getLock() {
    return LOCK;
  }

  @Override
  public void addInputFormat(InputFormat format) {
    inputFormats.add(format);
  }

  @Override
  public void addOutputFormat(OutputFormat format) {
    outputFormats.add(format);
    if (debugMode) {
      System.out.println(this + ".addOutputFormat(" + format + ")");
    }
  }

  @Override
  public void setOutputFormats(java.util.List<OutputFormat> formats) {
    this.outputFormats = new LinkedList<>(formats);
  }

  @Override
  public void setInputFormats(java.util.List<InputFormat> formats) {
    this.inputFormats = new LinkedList<>(formats);
  }

  @Override
  public java.util.List<InputFormat> getInputFormats() {
    return inputFormats;
  }

  @Override
  public java.util.List<OutputFormat> getOutputFormats() {
    if (debugMode) {
      System.out.println(this + ".getOutputFormats size:" + outputFormats.size());
    }
    return outputFormats;
  }

  //  @Override
  //  public Drawing getDrawing() {
  //    return this;
  //  }

  /*@Override
  public Rectangle2D.Double getDrawingArea() {
      Rectangle2D.Double drawingArea;
      Dimension2DDouble canvasSize = getCanvasSize();
      if (canvasSize != null) {
          drawingArea = new Rectangle2D.Double(
                  0d, 0d,
                  canvasSize.width, canvasSize.height);
      } else {
          drawingArea = super.getDrawingArea();
          drawingArea.add(0d, 0d);
          /*drawingArea = new Rectangle2D.Double(
                  0d, 0d,
                  canvasSize.width, canvasSize.height);* /
      }
      return drawingArea;
  }*/
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

    that.inputFormats =
        (this.inputFormats == null) ? null : (LinkedList<InputFormat>) this.inputFormats.clone();
    that.outputFormats =
        (this.outputFormats == null) ? null : (LinkedList<OutputFormat>) this.outputFormats.clone();
    return that;
  }

  public static boolean isDebugMode() {
    return debugMode;
  }

  public static void setDebugMode(boolean debugMode) {
    AbstractDrawing.debugMode = debugMode;
  }

  @Override
  public void willChange() {
    if (changingDepth == 0) {
      invalidate();
    }
    changingDepth++;
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
  public Rectangle2D.Double getDrawingArea() {
    return getDrawingArea(1.0);
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
    return (Rectangle2D.Double) cachedDrawingArea.clone();
  }

  @Override
  public boolean remove(Figure figure) {
    int index = children.indexOf(figure);
    if (index == -1) {
      return false;
    } else {
      basicRemoveChild(index);
      figure.removeNotify(this);
      fireFigureRemoved(figure, index);
      return true;
    }
  }

  protected Figure basicRemoveChild(int index) {
    Figure figure = children.remove(index);
    figure.removeFigureListener(eventHandler);
    invalidate();
    return figure;
  }

  @Override
  public void removeAll(Collection<? extends Figure> figures) {
    willChange();
    for (Figure f : new ArrayList<Figure>(figures)) {
      remove(f);
    }
    changed();
  }

  protected int basicRemove(Figure child) {
    int index = children.indexOf(child);
    if (index != -1) {
      basicRemoveChild(index);
    }
    return index;
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
  public void sendToBack(Figure figure) {
    if (basicRemove(figure) != -1) {
      basicAdd(0, figure);
      fireDrawingChanged(figure.getDrawingArea());
    }
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

  private final void addAll(int index, Collection<? extends Figure> figures) {
    for (Figure f : figures) {
      basicAdd(index++, f);
      f.addNotify(this);
      fireFigureAdded(f, index);
    }
    invalidate();
  }

  @Override
  public void addDrawingListener(DrawingListener listener) {
    listenerList.add(DrawingListener.class, listener);
  }
}
