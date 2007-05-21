/*
 * @(#)AbstractDrawing.java  3.0  2007-05-18
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */


package org.jhotdraw.draw;

import org.jhotdraw.beans.*;
import org.jhotdraw.undo.*;
import org.jhotdraw.xml.*;
import org.jhotdraw.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import org.jhotdraw.util.*;
import java.util.*;
import java.io.*;

/**
 * AbstractDrawing.
 *
 * @author Werner Randelshofer
 * @version 3.0 2007-05-18 Don't fire UndoableEdit events when Figures
 * are added/removed from a Drawing. The
 * <br>2.2 2006-12-26 Support for InputFormat's and OutputFormat's added.
 * <br>2.1 2006-07-08 Extend AbstractBean.
 * <br>2.0.1 2006-02-06 Did ugly dirty fix for IndexOutOfBoundsException when
 * undoing removal of Figures.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public abstract class AbstractDrawing extends AbstractBean implements Drawing {
    private final static Object lock = new JPanel().getTreeLock();
    protected EventListenerList listenerList = new EventListenerList();
    private FontRenderContext fontRenderContext;
    private java.util.List<InputFormat> inputFormats = new java.util.LinkedList<InputFormat>();
    private java.util.List<OutputFormat> outputFormats = new java.util.LinkedList<OutputFormat>();
    
    /** Creates a new instance. */
    public AbstractDrawing() {
    }
    
    public void addDrawingListener(DrawingListener listener) {
        listenerList.add(DrawingListener.class, listener);
    }
    public void removeDrawingListener(DrawingListener listener) {
        listenerList.remove(DrawingListener.class, listener);
    }
    public void addUndoableEditListener(UndoableEditListener l) {
        listenerList.add(UndoableEditListener.class, l);
    }
    
    public void removeUndoableEditListener(UndoableEditListener l) {
        listenerList.remove(UndoableEditListener.class, l);
    }
    public final void addAll(Collection<Figure> figures) {
        addAll(getFigureCount(), figures);
    }
    public final void addAll(int index, Collection<Figure> figures) {
        for (Figure f : figures) {
            add(index++, f);
        }
    }
    
    
    /***
     * Removes all figures.
     */
    public void clear() {
        removeAll(getFigures());
    }
    
    /**
     * Gets the number of figures.
     */
    public int getFigureCount() {
        return getFigures().size();
    }
    
    public void removeAll(Collection<Figure> toBeRemoved) {
        CompositeEdit edit = new CompositeEdit("Figuren entfernen");
        for (Figure f : new ArrayList<Figure>(toBeRemoved)) {
            remove(f);
        }
    }
    public void basicAddAll(int index, Collection<Figure> figures) {
        for (Figure f : figures) {
            basicAdd(index++, f);
        }
    }
    public void basicRemoveAll(Collection<Figure> toBeOrphaned) {
        // Implementation note: We create a new collection to
        // avoid problems that may be caused, if the collection
        // is somehow connected to our figures connection.
        for (Figure f : new ArrayList<Figure>(toBeOrphaned)) {
            basicRemove(f);
        }
    }
    
    /**
     * Calls basicAdd and then calls figure.addNotify and firesFigureAdded.
     */
    public final void add(final Figure figure) {
        add(getFigureCount(), figure);
    }
    public final void add(int index, Figure figure) {
        basicAdd(index, figure);
        figure.addNotify(this);
        fireFigureAdded(figure, index);
        fireAreaInvalidated(figure.getDrawingArea());
    }
    
    
    /**
     * Thi
     */
    public void basicAdd(Figure figure) {
        basicAdd(getFigureCount(), figure);
    }
    
    /**
     * Calls basicRemove and then calls figure.addNotify and firesFigureAdded.
     */
    public final void remove(final Figure figure) {
        if (contains(figure)) {
            final int index = indexOf(figure);
            basicRemove(figure);
            figure.removeNotify(this);
            fireFigureRemoved(figure, index);
        } else {
            fireAreaInvalidated(figure.getDrawingArea());
        }
    }
    /**
     *  Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    protected void fireAreaInvalidated(Rectangle2D.Double dirtyRegion) {
        DrawingEvent event = null;
        // Notify all listeners that have registered interest for
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == DrawingListener.class) {
                // Lazily create the event:
                if (event == null)
                    event = new DrawingEvent(this, null, dirtyRegion);
                ((DrawingListener)listeners[i+1]).areaInvalidated(event);
            }
        }
    }
    /**
     *  Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    public void fireUndoableEditHappened(UndoableEdit edit) {
        UndoableEditEvent event = null;
        if (listenerList.getListenerCount() > 0) {
            // Notify all listeners that have registered interest for
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (event == null)
                    event = new UndoableEditEvent(this, edit);
                if (listeners[i] == UndoableEditListener.class) {
                    ((UndoableEditListener)listeners[i+1]).undoableEditHappened(event);
                }
            }
        }
    }
    
    /**
     *  Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    protected void fireFigureAdded(Figure f, int zIndex) {
        DrawingEvent event = null;
        // Notify all listeners that have registered interest for
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == DrawingListener.class) {
                // Lazily create the event:
                if (event == null)
                    event = new DrawingEvent(this, f, f.getDrawingArea(), zIndex);
                ((DrawingListener)listeners[i+1]).figureAdded(event);
            }
        }
    }
    
    /**
     *  Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    protected void fireFigureRemoved(Figure f, int zIndex) {
        DrawingEvent event = null;
        // Notify all listeners that have registered interest for
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == DrawingListener.class) {
                // Lazily create the event:
                if (event == null)
                    event = new DrawingEvent(this, f, f.getDrawingArea(), zIndex);
                ((DrawingListener)listeners[i+1]).figureRemoved(event);
            }
        }
    }
    
    
    
    public FontRenderContext getFontRenderContext() {
        return fontRenderContext;
    }
    
    public void setFontRenderContext(FontRenderContext frc) {
        fontRenderContext = frc;
    }
    
    public void read(DOMInput in) throws IOException {
        in.openElement("figures");
        for (int i=0; i < in.getElementCount(); i++) {
            Figure f;
            add(f = (Figure) in.readObject(i));
        }
        in.closeElement();
    }
    
    public void write(DOMOutput out) throws IOException {
        out.openElement("figures");
        for (Figure f : getFigures()) {
            out.writeObject(f);
        }
        out.closeElement();
    }
    /**
     * The drawing view synchronizes on the lock when drawing a drawing.
     */
    public Object getLock() {
        return lock;
    }
    
    public void setOutputFormats(java.util.List<OutputFormat> formats) {
        this.outputFormats = formats;
    }
    
    public void setInputFormats(java.util.List<InputFormat> formats) {
        this.inputFormats = formats;
    }
    
    public java.util.List<InputFormat> getInputFormats() {
        return inputFormats;
    }
    
    public java.util.List<OutputFormat> getOutputFormats() {
        return outputFormats;
    }
}
