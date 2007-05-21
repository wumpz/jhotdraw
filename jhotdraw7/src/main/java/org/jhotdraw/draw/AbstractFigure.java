/*
 * @(#)AbstractFigure.java   4.0  2007-05-18
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

import org.jhotdraw.util.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.undo.*;
import java.io.*;
import org.jhotdraw.geom.*;
/**
 * AbstractFigure provides the functionality for managing listeners
 * for a Figure.
 *
 *
 * @author Werner Randelshofer
 * @version 4.0 2007-05-18 Removed addUndoableEditListener and
 * removeUndoableEditListener, isConnectorsVisible, setConnectorsVisible
 * methods due to changes in Figure interface.
 * <br>3.4 2007-02-09 Method fireFigureHandlesChanged added.
 * <br>3.3 Reworked.
 * <br>3.2 2006-01-05 Added method getChangingDepth().
 * <br>3.0 2006-01-20 Reworked for J2SE 1.5.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public abstract class AbstractFigure
        implements Figure {
    protected EventListenerList listenerList = new EventListenerList();
    private Drawing drawing;
    private boolean isInteractive;
    private boolean isVisible = true;
    /**
     * We increase this number on each invocation of willChange() and
     * decrease it on each invocation of changed().
     */
    protected int changingDepth = 0;
    
    /** Creates a new instance. */
    public AbstractFigure() {
    }
    
    // DRAWING
    // SHAPE AND BOUNDS
    // ATTRIBUTES
    // EDITING
    // CONNECTING
    // COMPOSITE FIGURES
    // CLONING
    // EVENT HANDLING
    public void addFigureListener(FigureListener l) {
        listenerList.add(FigureListener.class, l);
    }
    
    public void removeFigureListener(FigureListener l) {
        listenerList.remove(FigureListener.class, l);
    }
    
    public void addNotify(Drawing d) {
        this.drawing = d;
        fireFigureAdded();
    }
    public void removeNotify(Drawing d) {
        fireFigureRemoved();
        this.drawing = null;
    }
    
    protected Drawing getDrawing() {
        return drawing;
    }
    protected Object getLock() {
        return (getDrawing() == null) ? this : getDrawing().getLock();
    }
    
    /**
     *  Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    public void fireAreaInvalidated() {
        fireAreaInvalidated(getDrawingArea());
    }
    /**
     *  Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    protected void fireAreaInvalidated(Rectangle2D.Double invalidatedArea) {
        if (listenerList.getListenerCount() > 0) {
            FigureEvent event = null;
            // Notify all listeners that have registered interest for
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (listeners[i] == FigureListener.class) {
                    // Lazily create the event:
                    if (event == null)
                        event = new FigureEvent(this, invalidatedArea);
                    ((FigureListener)listeners[i+1]).figureAreaInvalidated(event);
                }
            }
        }
    }
    /**
     *  Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    protected void fireFigureRequestRemove() {
        if (listenerList.getListenerCount() > 0) {
            FigureEvent event = null;
            // Notify all listeners that have registered interest for
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (listeners[i] == FigureListener.class) {
                    // Lazily create the event:
                    if (event == null)
                        event = new FigureEvent(this, getBounds());
                    ((FigureListener)listeners[i+1]).figureRequestRemove(event);
                }
            }
        }
    }
    /**
     *  Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    protected void fireFigureAdded() {
        if (listenerList.getListenerCount() > 0) {
            FigureEvent event = null;
            // Notify all listeners that have registered interest for
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (listeners[i] == FigureListener.class) {
                    // Lazily create the event:
                    if (event == null)
                        event = new FigureEvent(this, getBounds());
                    ((FigureListener)listeners[i+1]).figureAdded(event);
                }
            }
        }
    }
    /**
     *  Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    protected void fireFigureRemoved() {
        if (listenerList.getListenerCount() > 0) {
            FigureEvent event = null;
            // Notify all listeners that have registered interest for
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (listeners[i] == FigureListener.class) {
                    // Lazily create the event:
                    if (event == null)
                        event = new FigureEvent(this, getBounds());
                    ((FigureListener)listeners[i+1]).figureRemoved(event);
                }
            }
        }
    }
    public void fireFigureChanged() {
        fireFigureChanged(getBounds());
    }
    /**
     *  Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    protected void fireFigureChanged(Rectangle2D.Double changedArea) {
        if (listenerList.getListenerCount() > 0) {
            FigureEvent event = null;
            // Notify all listeners that have registered interest for
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (listeners[i] == FigureListener.class) {
                    // Lazily create the event:
                    if (event == null)
                        event = new FigureEvent(this, changedArea);
                    ((FigureListener)listeners[i+1]).figureChanged(event);
                }
            }
        }
    }
    protected void fireFigureChanged(FigureEvent event) {
        if (listenerList.getListenerCount() > 0) {
            // Notify all listeners that have registered interest for
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (listeners[i] == FigureListener.class) {
                    // Lazily create the event:
                    ((FigureListener)listeners[i+1]).figureChanged(event);
                }
            }
        }
    }
    /**
     *  Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    protected void fireAttributeChanged(AttributeKey attribute, Object oldValue, Object newValue) {
        if (listenerList.getListenerCount() > 0) {
            FigureEvent event = null;
            // Notify all listeners that have registered interest for
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (listeners[i] == FigureListener.class) {
                    // Lazily create the event:
                    if (event == null)
                        event = new FigureEvent(this, attribute, oldValue, newValue);
                    ((FigureListener)listeners[i+1]).figureAttributeChanged(event);
                }
            }
        }
    }
    /**
     *  Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    protected void fireFigureHandlesChanged() {
        Rectangle2D.Double changedArea = getDrawingArea();
        if (listenerList.getListenerCount() > 0) {
            FigureEvent event = null;
            // Notify all listeners that have registered interest for
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (listeners[i] == FigureListener.class) {
                    // Lazily create the event:
                    if (event == null)
                        event = new FigureEvent(this, changedArea);
                    ((FigureListener)listeners[i+1]).figureHandlesChanged(event);
                }
            }
        }
    }
    /**
     * Notify all UndoableEditListener of the Drawing, to which this Figure has
     * been added to. If this Figure is not part of a Drawing, the event is
     * lost.
     */
    protected void fireUndoableEditHappened(UndoableEdit edit) {
        if (getDrawing() != null) {
            getDrawing().fireUndoableEditHappened(edit);
        }
    }
    /*
    public Set createHandles() {
        return new HashSet();
    }
     */
    
    public AbstractFigure clone() {
        try {
            AbstractFigure that = (AbstractFigure) super.clone();
            that.listenerList = new EventListenerList();
            that.drawing = null; // Clones need to be explictly added to a drawing
            return that;
        } catch (CloneNotSupportedException e) {
            InternalError error = new InternalError(e.getMessage());
            error.initCause(e);
            throw error;
        }
    }
    public final AbstractFigure basicClone(HashMap<Figure,Figure> oldToNew) {
        // XXX - Delete me
        return null;
    }
    public void remap(HashMap<Figure,Figure> oldToNew) {
    }
    
    public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = new LinkedList<Handle>();
        ResizeHandleKit.addResizeHandles(this, handles);
        return handles;
    }
    
    
    public Cursor getCursor(Point2D.Double p) {
        if (contains(p)) {
            return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        } else {
            return Cursor.getDefaultCursor();
        }
    }
    
    public final void setBounds(Rectangle2D.Double bounds) {
        setBounds(new Point2D.Double(bounds.x, bounds.y), new Point2D.Double(bounds.x + bounds.width, bounds.y + bounds.height));
    }
    public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
        Point2D.Double oldAnchor = getStartPoint();
        Point2D.Double oldLead = getEndPoint();
        if (! oldAnchor.equals(anchor)
        || ! oldLead.equals(lead)) {
            willChange();
            setBounds(anchor, lead);
            changed();
            fireUndoableEditHappened(new SetBoundsEdit(this, oldAnchor, oldLead, anchor, lead));
        }
    }
    
    /**
     * Checks if this figure can be connected. By default
     * AbstractFigures can be connected.
     */
    public boolean canConnect() {
        return true;
    }
    
    /**
     * Informs that a figure needs to be redrawn.
     */
    public void invalidate() {
        fireAreaInvalidated(getDrawingArea());
    }
    protected boolean isChanging() {
        return changingDepth != 0;
    }
    protected int getChangingDepth() {
        return changingDepth;
    }
    /**
     * Informs that a figure is about to change something that
     * affects the contents of its display box.
     */
    public void willChange() {
        changingDepth++;
        invalidate();
    }
    
    protected void validate() {
        
    }
    
    /**
     * Informs that a figure changed the area of its display box.
     */
    public void changed() {
        if (changingDepth <= 1) {
            validate();
            fireFigureChanged(getDrawingArea());
            changingDepth = 0;
        } else {
            changingDepth--;
        }
    }
    
    /**
     * Returns the Figures connector for the specified location.
     * By default a ChopBoxConnector is returned.
     *
     *
     * @see ChopRectangleConnector
     */
    public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
        return new ChopRectangleConnector(this);
    }
    
    public boolean includes(Figure figure) {
        return figure == this;
    }
    
    public Figure findFigureInside(Point2D.Double p) {
        return (contains(p)) ? this : null;
    }
    
    public Connector findCompatibleConnector(Connector c, boolean isStart) {
        return new ChopRectangleConnector(this);
    }
    
    /**
     * Returns a collection of actions which are presented to the user
     * in a popup menu.
     * <p>The collection may contain null entries. These entries are used
     * interpreted as separators in the popup menu.
     */
    public Collection<Action> getActions(Point2D.Double p) {
        return Collections.emptyList();
    }
    
    /**
     * Returns a specialized tool for the given coordinate.
     * <p>Returns null, if no specialized tool is available.
     */
    public Tool getTool(Point2D.Double p) {
        return null;
    }
    
    /**
     * Handles a mouse click.
     */
    public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
        return false;
    }
    
    public boolean handleDrop(Point2D.Double p, Collection<Figure> droppedFigures, DrawingView view) {
        return false;
    }
    
    public Point2D.Double getEndPoint() {
        Rectangle2D.Double r = getBounds();
        return new Point2D.Double(r.x + r.width, r.y + r.height);
    }
    
    public Point2D.Double getStartPoint() {
        Rectangle2D.Double r = getBounds();
        return new Point2D.Double(r.x, r.y);
    }
    /*
    public Rectangle2D.Double getHitBounds() {
        return getBounds();
    }
     */
    public Dimension2DDouble getPreferredSize() {
        Rectangle2D.Double r = getBounds();
        return new Dimension2DDouble(r.width, r.height);
    }
    
    public void remap(Map oldToNew) {
    }
    
    public boolean isInteractive() {
        return isInteractive;
    }
    public void setInteractive(boolean b) {
        isInteractive = b;
    }
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
    
    public Collection<Figure> getDecomposition() {
        LinkedList<Figure> list = new LinkedList<Figure>();
        list.add(this);
        return list;
    }
    
    protected FontRenderContext getFontRenderContext() {
        FontRenderContext frc = null;
        if (frc == null) {
            frc = new FontRenderContext(new AffineTransform(), Options.isTextAntialiased(), Options.isFractionalMetrics());
        }
        return frc;
    }
    
    public void requestRemove() {
        fireFigureRequestRemove();
    }
    
    public int getLayer() {
        return 0;
    }
    
    public String getToolTipText(Point2D.Double p) {
        return null;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getName().substring(getClass().getName().lastIndexOf('.')+1));
        buf.append('@');
        buf.append(hashCode());
        return buf.toString();
    }

    public Collection<Connector> getConnectors(ConnectionFigure prototype) {
        LinkedList connectors = new LinkedList<Connector>();
        connectors.add(new ChopRectangleConnector(this));
        return connectors;
    }
}
