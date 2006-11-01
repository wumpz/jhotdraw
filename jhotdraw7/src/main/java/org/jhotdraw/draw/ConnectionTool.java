/*
 * @(#)ConnectionTool.java  3.1  2006-07-15
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
�
 */

package org.jhotdraw.draw;

import org.jhotdraw.undo.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import java.awt.dnd.*;

/**
 * A tool that can be used to connect figures, to split
 * connections, and to join two segments of a connection.
 * ConnectionTools turns the visibility of the Connectors
 * on when it enters a figure.
 * The connection object to be created is specified by a prototype.
 * <p>
 * FIXME: Use a Tracker instance for each state of this tool.
 *
 * @author Werner Randelshofer
 * @version 3.1 2006-07-15 Added support for prototype class name. 
 * <br>3.0 2006-06-07 Reworked.
 * <br>2.1 2006-03-15 When user is not pressing the mouse button, we use
 * the mouse over view as the current view.
 * <br>2.0.1 2006-02-14 Fixed drawing code.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class ConnectionTool extends AbstractTool implements FigureListener {
    private Map<AttributeKey, Object> attributes;
    /**
     * the anchor point of the interaction
     */
    private Connector   startConnector;
    private Connector   endConnector;
    private Connector   targetConnector;
    
    private Figure target;
    /**
     * the currently created figure
     */
    private ConnectionFigure  connection;
    
    /**
     * the currently manipulated connection point
     */
    private int  splitPoint;
    /**
     * the currently edited connection
     */
    private ConnectionFigure  editedConnection;
    
    /**
     * the figure that was actually added
     * Note, this can be a different figure from the one which has been created.
     */
    private Figure createdFigure;
    
    /**
     * the prototypical figure that is used to create new
     * connections.
     */
    protected ConnectionFigure  prototype;
    
    protected boolean isPressed;
    
    /** Creates a new instance. */
    public ConnectionTool(ConnectionFigure prototype) {
        this.prototype = prototype;
    }
    public ConnectionTool(ConnectionFigure prototype, Map attributes) {
        this.prototype = prototype;
        this.attributes = attributes;
    }
    public ConnectionTool(String prototypeClassName) {
        this(prototypeClassName, null);
    }
    public ConnectionTool(String prototypeClassName, Map<AttributeKey, Object> attributes) {
        try {
        this.prototype = (ConnectionFigure) Class.forName(prototypeClassName).newInstance();
        } catch (Exception e) {
            InternalError error = new InternalError("Unable to create ConnectionFigure from "+prototypeClassName);
            error.initCause(e);
            throw error;
        }
        this.attributes = attributes;
    }
    public ConnectionFigure getPrototype() {
        return prototype;
    }
    
    public void mouseMoved(MouseEvent evt) {
        trackConnectors(evt);
    }
    /**
     * Manipulates connections in a context dependent way. If the
     * mouse down hits a figure start a new connection. If the mousedown
     * hits a connection split a segment or join two segments.
     */
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        isPressed = true;
        getView().clearSelection();
        Point2D.Double ap = viewToDrawing(anchor);
        if (getTargetFigure() != null) {
            getTargetFigure().setConnectorsVisible(false, null);
        }
        
        setTargetFigure(findConnectionStart(ap, getDrawing()));
        
        if (getTargetFigure() != null) {
            setStartConnector(findConnector(ap, target, prototype));
            if (getStartConnector() != null && canConnect(getTargetFigure())) {
                Point2D.Double p = getStartConnector().getAnchor();
                setConnection(createFigure());
                getConnection().basicSetBounds(p, p);
                getConnection().addFigureListener(this);
                setCreatedFigure(getConnection());
            }
        }
    }
    
    /**
     * Adjust the created connection or split segment.
     */
    public void mouseDragged(java.awt.event.MouseEvent e) {
        Point2D.Double p = viewToDrawing(new Point(e.getX(), e.getY()));
        if (getConnection() != null) {
            trackConnectors(e);
            
            if (getTargetConnector() != null) {
                p = getTargetConnector().getAnchor();
            }
            ConnectionFigure f = getConnection();
            fireAreaInvalidated(f.getDrawBounds());
            f.willChange();
            f.basicSetBounds(f.getStartPoint(), p);
            f.changed();
            fireAreaInvalidated(f.getDrawBounds());//new Rectangle2D.Double(viewToDrawing(anchor), p));
        } else if (editedConnection != null) {
            editedConnection.willChange();
            editedConnection.setPoint(splitPoint, p);
            editedConnection.changed();
        }
    }
    
    protected boolean canConnect(Figure start) {
        return prototype.canConnect(start);
    }
    protected boolean canConnect(Figure start, Figure end) {
        return prototype.canConnect(start, end);
    }
    
    /**
     * Connects the figures if the mouse is released over another
     * figure.
     */
    public void mouseReleased(MouseEvent e) {
        isPressed = false;
        isWorking = false;
        Figure c = null;
        Point2D.Double p = viewToDrawing(new Point(e.getX(), e.getY()));
        if (getStartConnector() != null) {
            c = findTarget(p, getDrawing());
        }
        
        if (c != null) {
            setEndConnector(findConnector(p, c, prototype));
            if (getEndConnector() != null) {
                CompositeEdit creationEdit = new CompositeEdit("Verbindung erstellen");
                getDrawing().fireUndoableEditHappened(creationEdit);
                ConnectionFigure f = getConnection();
                f.willChange();
                f.setStartConnector(getStartConnector());
                f.setEndConnector(getEndConnector());
                f.basicSetBounds(f.getStartPoint(), p);
                f.updateConnection();
                f.changed();
                f.removeFigureListener(this);
                getDrawing().add(f);
                getDrawing().fireUndoableEditHappened(creationEdit);
            }
        } else if (getConnection() != null) {
            getDrawing().remove(getConnection());
        }
        
        setConnection(null);
        setStartConnector(null);
        setEndConnector(null);
        setCreatedFigure(null);
        fireToolDone();
    }
    public void activate(DrawingEditor editor) {
        super.activate(editor);
       // getView().clearSelection();
    }
    public void deactivate(DrawingEditor editor) {
        super.deactivate(editor);
        if (getTargetFigure() != null) {
            getTargetFigure().setConnectorsVisible(false, null);
        }
    }
    //--
    /**
     * Creates the ConnectionFigure. By default the figure prototype is
     * cloned.
     */
    protected ConnectionFigure createFigure() {
        ConnectionFigure f = (ConnectionFigure) prototype.clone();
        getEditor().applyDefaultAttributesTo(f);
        if (attributes != null) {
            for (Map.Entry<AttributeKey, Object> entry : attributes.entrySet()) {
                f.setAttribute((AttributeKey) entry.getKey(), entry.getValue());
            }
        }
        return f;
    }
    
    /**
     * Finds a connectable figure target.
     */
    protected Figure findSource(Point2D.Double p, Drawing drawing) {
        return findConnectableFigure(p, drawing);
    }
    
    /**
     * Finds a connectable figure target.
     */
    protected Figure findTarget(Point2D.Double p, Drawing drawing) {
        Figure target = findConnectableFigure(p, drawing);
        Figure start = getStartConnector().getOwner();
        
        if (target != null
                && getConnection() != null
                && canConnect(target)
                //&& ! target.includes(start)
                && canConnect(start, target)) {
            return target;
        }
        return null;
    }
    
    /**
     * Finds an existing connection figure.
     */
    protected ConnectionFigure findConnection(Point2D.Double p, Drawing drawing) {
        for (Figure f : drawing.getFiguresFrontToBack()) {
            if (f != null && (f instanceof ConnectionFigure)) {
                return (ConnectionFigure) f;
            }
        }
        return null;
    }
    
    protected void setConnection(ConnectionFigure newConnection) {
        connection = newConnection;
    }
    
    /**
     * Gets the connection which is created by this tool
     */
    protected ConnectionFigure getConnection() {
        return connection;
    }
    
    protected void trackConnectors(MouseEvent e) {
        Point2D.Double p = viewToDrawing(new Point(e.getX(), e.getY()));
        Figure c = null;
        
        if (getStartConnector() == null) {
            c = findSource(p, getDrawing());
        } else {
            c = findTarget(p, getDrawing());
        }
        
        // track the figure containing the mouse
        if (c != getTargetFigure()) {
            if (getTargetFigure() != null) {
                getTargetFigure().setConnectorsVisible(false, null);
            }
            setTargetFigure(c);
            if (getStartConnector() != null) {
                if (getTargetFigure() != null
                        && canConnect(getStartConnector().getOwner(), getTargetFigure())) {
                    getTargetFigure().setConnectorsVisible(true, getConnection());
                }
            } else {
                
                if (getTargetFigure() != null
                        && canConnect(getTargetFigure())) {
                    getTargetFigure().setConnectorsVisible(true, getConnection());
                }
            }
        }
        
        Connector cc = null;
        if (c != null) {
            cc = findConnector(p, c, prototype);
        }
        if (cc != getTargetConnector()) {
            setTargetConnector(cc);
        }
        
        //view().checkDamage();
    }
    public void draw(Graphics2D g) {
        if (createdFigure != null) {
            Graphics2D gg = (Graphics2D) g.create();
            gg.transform(getView().getDrawingToViewTransform());
            createdFigure.draw(gg);
            gg.dispose();
        }
    }
    
    protected Connector findConnector(Point2D.Double p, Figure target, ConnectionFigure f) {
        return target.findConnector(p, f);
    }
    
    /**
     * Finds a connection start figure.
     */
    protected Figure findConnectionStart(Point2D.Double p, Drawing drawing) {
        Figure target = findConnectableFigure(p, drawing);
        if ((target != null) && target.canConnect()) {
            return target;
        }
        return null;
    }
    
    protected Figure findConnectableFigure(Point2D.Double p, Drawing drawing) {
        return drawing.findFigureExcept(p, createdFigure);
    }
    
    protected void setStartConnector(Connector newStartConnector) {
        startConnector = newStartConnector;
    }
    
    protected Connector getStartConnector() {
        return startConnector;
    }
    
    protected void setEndConnector(Connector newEndConnector) {
        endConnector = newEndConnector;
    }
    
    protected Connector getEndConnector() {
        return endConnector;
    }
    
    private void setTargetConnector(Connector newTargetConnector) {
        targetConnector = newTargetConnector;
    }
    
    protected Connector getTargetConnector() {
        return targetConnector;
    }
    
    private void setTargetFigure(Figure newTarget) {
        target = newTarget;
    }
    
    protected Figure getTargetFigure() {
        return target;
    }
    
    /**
     * Gets the figure that was actually added
     * Note, this can be a different figure from the one which has been created.
     */
    protected Figure getCreatedFigure() {
        return createdFigure;
    }
    
    protected void setCreatedFigure(Figure newCreatedFigure) {
        createdFigure = newCreatedFigure;
    }
    
    public void figureAreaInvalidated(FigureEvent evt) {
        fireAreaInvalidated(evt.getInvalidatedArea());
    }
    
    public void figureAdded(FigureEvent e) {
    }
    
    public void figureChanged(FigureEvent e) {
    }
    
    public void figureRemoved(FigureEvent e) {
    }
    
    public void figureRequestRemove(FigureEvent e) {
    }
    
    public void figureAttributeChanged(FigureEvent e) {
    }
}
