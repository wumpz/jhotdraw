/*
 * @(#)BezierBezierLineConnection.java  1.0.1  2006-02-06
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
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
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.undo.*;
import java.io.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
/**
 * A LineConnection is a standard implementation of the
 * ConnectionFigure interface. The interface is implemented with BezierFigure.
 *
 *
 *
 * @author Werner Randelshofer
 * @version 1.0.1 2006-02-06 Fixed redo bug.
 * <br>1.0 23. Januar 2006 Created.
 */
public class LineConnectionFigure extends LineFigure
        implements ConnectionFigure {
    private Connector    startConnector;
    private Connector    endConnector;
    private Liner liner;
    
    /**
     * Handles figure changes in the start and the
     * end figure.
     */
    private ConnectionHandler connectionHandler = new ConnectionHandler(this);
    private static class ConnectionHandler implements FigureListener {
        private LineConnectionFigure owner;
        private ConnectionHandler(LineConnectionFigure owner) {
            this.owner = owner;
        }
        public void figureRequestRemove(FigureEvent e) {
        }
        
        public void figureRemoved(FigureEvent evt) {
            // The commented lines below must stay commented out.
            // This is because, we must not set our connectors to null,
            // in order to support reconnection using redo.
            /*
            if (evt.getFigure() == owner.getStartFigure()
            || evt.getFigure() == owner.getEndFigure()) {
                owner.setStartConnector(null);
                owner.setEndConnector(null);
            }*/
            owner.fireFigureRequestRemove();
        }
        
        public void figureChanged(FigureEvent e) {
            if (e.getSource() == owner.getStartFigure() ||
                    e.getSource() == owner.getEndFigure()) {
                owner.willChange();
                owner.updateConnection();
                owner.changed();
            }
        }
        
        public void figureAdded(FigureEvent e) {
        }
        
        public void figureAttributeChanged(FigureEvent e) {
        }
        
        public void figureAreaInvalidated(FigureEvent e) {
        }
        
    };
    
    /** Creates a new instance. */
    public LineConnectionFigure() {
    }
    // DRAWING
    // SHAPE AND BOUNDS
    /**
     * Ensures that a connection is updated if the connection
     * was moved.
     */
    public void basicTransform(AffineTransform tx) {
        super.basicTransform(tx);
        updateConnection(); // make sure that we are still connected
    }
    // ATTRIBUTES
    // EDITING
    /**
     * Gets the handles of the figure. It returns the normal
     * PolylineHandles but adds ChangeConnectionHandles at the
     * start and end.
     */
    public Collection<Handle> createHandles(int detailLevel) {
        ArrayList<Handle> handles = new ArrayList<Handle>(getNodeCount());
        
        switch (detailLevel) {
            case 0 :
                if (getLiner() == null) {
                    for (int i = 1, n = getNodeCount() - 1; i < n; i++) {
                        handles.add(new BezierNodeHandle(this, i));
                    }
                }
                handles.add(new ChangeConnectionStartHandle(this));
                handles.add(new ChangeConnectionEndHandle(this));
                break;
        }
        return handles;
    }
    
// CONNECTING
    /**
     * Tests whether a figure can be a connection target.
     * ConnectionFigures cannot be connected and return false.
     */
    public boolean canConnect() {
        return false;
    }
    public void updateConnection() {
        willChange();
        if (getStartConnector() != null) {
            Point2D.Double start = getStartConnector().findStart(this);
            if(start != null) {
                basicSetStartPoint(start);
            }
        }
        if (getEndConnector() != null) {
            Point2D.Double end = getEndConnector().findEnd(this);
            
            if(end != null) {
                basicSetEndPoint(end);
            }
        }
        
        changed();
    }
    public void validate() {
        super.validate();
        lineout();
    }
    
    public boolean canConnect(Figure start, Figure end) {
        return start.canConnect() && end.canConnect();
    }
    
    public boolean connectsSame(ConnectionFigure other) {
        return other.getStartConnector() == getStartConnector()
        && other.getEndConnector() == getEndConnector();
    }
    
    public Connector getEndConnector() {
        return endConnector;
    }
    
    public Figure getEndFigure() {
        return (endConnector == null) ? null : endConnector.getOwner();
    }
    
    public Connector getStartConnector() {
        return startConnector;
    }
    
    public Figure getStartFigure() {
        return (startConnector == null) ? null : startConnector.getOwner();
    }
    /**
     * Note: this method is only final for testing purposes. You can
     * remove the final keywoard at any time.
     */
    public final void setEndConnector(final Connector newEnd) {
        final Connector oldEnd = endConnector;
        if (newEnd != oldEnd) {
            willChange();
            basicSetEndConnector(newEnd);
            fireUndoableEditHappened(new AbstractUndoableEdit() {
                public String getPresentationName() { return "End-Verbindung setzen"; }
                public void undo() throws CannotUndoException {
                    super.undo();
                    willChange();
                    basicSetEndConnector(oldEnd);
                    changed();
                }
                public void redo()  throws CannotUndoException {
                    super.redo();
                    willChange();
                    basicSetEndConnector(newEnd);
                    changed();
                }
            });
            changed();
        }
    }
    protected void basicSetEndConnector(Connector newEnd) {
        if (newEnd != endConnector) {
            if (endConnector != null) {
                getEndFigure().removeFigureListener(connectionHandler);
                if (getStartFigure() != null) {
                    handleDisconnect(getStartFigure(), getEndFigure());
                }
            }
            endConnector = newEnd;
            if (endConnector != null) {
                getEndFigure().addFigureListener(connectionHandler);
                if (getStartFigure() != null && getEndFigure() != null) {
                    handleConnect(getStartFigure(), getEndFigure());
                    updateConnection();
                }
            }
        }
    }
    /**
     * Note: this method is only final for testing purposes. You can
     * remove the final keywoard at any time.
     */
    public final void setStartConnector(final Connector newStart) {
        final Connector oldStart = startConnector;
        if (newStart != oldStart) {
            willChange();
            basicSetStartConnector(newStart);
            fireUndoableEditHappened(new AbstractUndoableEdit() {
                public String getPresentationName() { return "Start-Verbindung setzen"; }
                public void undo()  throws CannotUndoException {
                    super.undo();
                    willChange();
                    basicSetStartConnector(oldStart);
                    changed();
                }
                public void redo()  throws CannotUndoException {
                    super.redo();
                    willChange();
                    basicSetStartConnector(newStart);
                    changed();
                }
            });
            changed();
        }
    }
    
    public void basicSetStartConnector(Connector newStart) {
        if (newStart != startConnector) {
            if (startConnector != null) {
                getStartFigure().removeFigureListener(connectionHandler);
                if (getEndFigure() != null) {
                    handleDisconnect(getStartFigure(), getEndFigure());
                }
            }
            startConnector = newStart;
            if (startConnector != null) {
                getStartFigure().addFigureListener(connectionHandler);
                if (getStartFigure() != null && getEndFigure() != null) {
                    handleConnect(getStartFigure(), getEndFigure());
                    updateConnection();
                }
            }
        }
    }
    
    
    
    // COMPOSITE FIGURES
    // LAYOUT
    /*
    public Liner getBezierPathLayouter() {
        return (Liner) getAttribute(BEZIER_PATH_LAYOUTER);
    }
    public void setBezierPathLayouter(Liner newValue) {
        setAttribute(BEZIER_PATH_LAYOUTER, newValue);
    }
    /**
     * Lays out the connection. This is called when the connection
     * itself changes. By default the connection is recalculated
     * /
    public void layoutConnection() {
        if (getStartConnector() != null && getEndConnector() != null) {
            willChange();
            Liner bpl = getBezierPathLayouter();
            if (bpl != null) {
                bpl.lineout(this);
            } else {
                if (getStartConnector() != null) {
                    Point2D.Double start = getStartConnector().findStart(this);
                    if(start != null) {
                        basicSetStartPoint(start);
                    }
                }
                if (getEndConnector() != null) {
                    Point2D.Double end = getEndConnector().findEnd(this);
     
                    if(end != null) {
                        basicSetEndPoint(end);
                    }
                }
            }
            changed();
        }
    }
     */
    // CLONING
    // EVENT HANDLING
    public void addNotify(Drawing drawing) {
        super.addNotify(drawing);
        /*
        if (getStartConnector() != null && getEndConnector() != null) {
            handleConnect(getStartFigure(), getEndFigure());
        }*/
    }
    public void removeNotify(Drawing drawing) {
        /*
        setStartConnector(null);
        setEndConnector(null);
        /*
        if (getStartConnector() != null && getEndConnector() != null) {
            handleDisconnect(getStartFigure(), getEndFigure());
        }*/
        super.removeNotify(drawing);
    }
    
    /**
     * Handles the disconnection of a connection.
     * Override this method to handle this event.
     */
    protected void handleDisconnect(Figure start, Figure end) {
    }
    
    /**
     * Handles the connection of a connection.
     * Override this method to handle this event.
     */
    protected void handleConnect(Figure start, Figure end) {
    }
    
    
    public LineConnectionFigure clone() {
        LineConnectionFigure that = (LineConnectionFigure) super.clone();
        that.connectionHandler = new ConnectionHandler(that);
        if (this.liner != null) {
            that.liner = (Liner) this.liner.clone();
        }
        // That shares the same connectors that this object has.
        // To work properly, that must be registered as a figure listener
        // to the connected figures.
        if (this.startConnector != null) {
            that.startConnector = (Connector) this.startConnector.clone();
            that.getStartFigure().addFigureListener(that.connectionHandler);
        }
        if (this.endConnector != null) {
            that.endConnector = (Connector) this.endConnector.clone();
            that.getEndFigure().addFigureListener(that.connectionHandler);
        }
        if (that.startConnector != null && that.endConnector != null) {
            that.handleConnect(that.getStartFigure(), that.getEndFigure());
            that.updateConnection();
        }
        return that;
    }
    
    public void remap(Map oldToNew) {
        willChange();
        super.remap(oldToNew);
        Figure newStartFigure = null;
        Figure newEndFigure = null;
        if (getStartFigure() != null) {
            newStartFigure = (Figure) oldToNew.get(getStartFigure());
            if (newStartFigure == null) newStartFigure = getStartFigure();
        }
        if (getEndFigure() != null) {
            newEndFigure = (Figure) oldToNew.get(getEndFigure());
            if (newEndFigure == null) newEndFigure = getEndFigure();
        }
        
        if (newStartFigure != null) {
            setStartConnector(newStartFigure.findCompatibleConnector(getStartConnector(),  true));
        }
        if (newEndFigure != null) {
            setEndConnector(newEndFigure.findCompatibleConnector(getEndConnector(),  false));
        }
        
        updateConnection();
        changed();
    }
    
    
    public boolean canConnect(Figure start) {
        return start.canConnect();
    }
    
    /**
     * Handles a mouse click.
     */
    public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
        if (getLiner() == null &&
                evt.getClickCount() == 2) {
            willChange();
            final int index = basicSplitSegment(p, (float) (5f / view.getScaleFactor()));
            if (index != -1) {
                final BezierPath.Node newNode = getNode(index);
                fireUndoableEditHappened(new AbstractUndoableEdit() {
                    public void redo() throws CannotRedoException {
                        super.redo();
                        willChange();
                        basicAddNode(index, newNode);
                        changed();
                    }
                    
                    public void undo() throws CannotUndoException {
                        super.undo();
                        willChange();
                        basicRemoveNode(index);
                        changed();
                    }
                    
                });
                changed();
                return true;
            }
        }
        return false;
    }
    // PERSISTENCE
    protected void readPoints(DOMInput in) throws IOException {
        super.readPoints(in);
        in.openElement("startConnector");
        setStartConnector((Connector) in.readObject());
        in.closeElement();
        in.openElement("endConnector");
        setEndConnector((Connector) in.readObject());
        in.closeElement();
    }
    public void read(DOMInput in) throws IOException {
        readPoints(in);
        readAttributes(in);
        readLiner(in);
    }
    protected void readLiner(DOMInput in) throws IOException {
        if (in.getElementCount("liner") > 0) {
            in.openElement("liner");
            liner = (Liner) in.readObject();
            in.closeElement();
        }
    }
    public void write(DOMOutput out) throws IOException {
        writePoints(out);
        writeAttributes(out);
        writeLiner(out);
    }
    protected void writeLiner(DOMOutput out) throws IOException {
        if (liner != null) {
            out.openElement("liner");
            out.writeObject(liner);
            out.closeElement();
        }
    }
    protected void writePoints(DOMOutput out) throws IOException {
        super.writePoints(out);
        out.openElement("startConnector");
        out.writeObject(getStartConnector());
        out.closeElement();
        out.openElement("endConnector");
        out.writeObject(getEndConnector());
        out.closeElement();
    }
    
    public void setLiner(Liner newValue) {
        willChange();
        this.liner = newValue;
        changed();
    }
    
    public void basicSetNode(int index, BezierPath.Node p) {
        if (index != 0 && index != getPointCount() - 1) {
            if (getStartConnector() != null) {
                Point2D.Double start = getStartConnector().findStart(this);
                if(start != null) {
                    basicSetStartPoint(start);
                }
            }
            if (getEndConnector() != null) {
                Point2D.Double end = getEndConnector().findEnd(this);
                
                if(end != null) {
                    basicSetEndPoint(end);
                }
            }
        }
        super.basicSetNode(index, p);
    }
    /*
    public void basicSetPoint(int index, Point2D.Double p) {
        if (index != 0 && index != getPointCount() - 1) {
            if (getStartConnector() != null) {
                Point2D.Double start = getStartConnector().findStart(this);
                if(start != null) {
                    basicSetStartPoint(start);
                }
            }
            if (getEndConnector() != null) {
                Point2D.Double end = getEndConnector().findEnd(this);
     
                if(end != null) {
                    basicSetEndPoint(end);
                }
            }
        }
        super.basicSetPoint(index, p);
    }
     */
    public void lineout() {
        if (liner != null) {
            liner.lineout(this);
        }
    }
    /**
     * FIXME - Liner must work with API of LineConnection!
     */
    public BezierPath getBezierPath() {
        return path;
    }
    
    
    public Liner getLiner() {
        return liner;
    }
    
    public void setStartPoint(Point2D.Double p) {
        setPoint(0, p);
    }
    
    public void setPoint(int index, Point2D.Double p) {
        setPoint(index, 0, p);
    }
    
    public void setEndPoint(Point2D.Double p) {
        setPoint(getPointCount() - 1, p);
    }
    
    public void reverseConnection() {
        if (startConnector != null && endConnector != null) {
            handleDisconnect(startConnector.getOwner(), endConnector.getOwner());
            Connector tmpC = startConnector;
            startConnector = endConnector;
            endConnector = tmpC;
            Point2D.Double tmpP = getStartPoint();
            setStartPoint(getEndPoint());
            setEndPoint(tmpP);
            handleConnect(startConnector.getOwner(), endConnector.getOwner());
            updateConnection();
        }
    }
}