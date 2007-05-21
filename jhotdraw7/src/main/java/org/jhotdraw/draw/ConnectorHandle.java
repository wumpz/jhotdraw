/*
 * @(#)ConnectorHandle.java  2.0  2007-05-18
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

import java.util.*;
import javax.swing.undo.*;
import org.jhotdraw.undo.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.geom.*;

/**
 * A ConnectorHandle allows to create a ConnectionFigure by dragging the
 * connector handle to a connector.
 *
 * @author Werner Randelshofer.
 * @version 2.0 2007-05-15 Renamed from ConnectionHandle to ConnectorHandle.
 * Uses a Connector instead of a Locator now.
 * <br>1.0 20. Juni 2006 Created.
 */
public class ConnectorHandle extends AbstractHandle {
    private final static int ANCHOR_WIDTH = 6;
    /**
     * Holds the ConnectionFigure which is currently being created.
     */
    private ConnectionFigure createdConnection;
    
    /**
     * The prototype for the ConnectionFigure to be created
     */
    private ConnectionFigure prototype;
    
    /**
     * The Connector.
     */
    private Connector connector;
    
    /**
     * The current connectable Figure.
     */
    private Figure connectableFigure;
    /**
     * The current connectable Connector.
     */
    private Connector connectableConnector;
    
    /**
     * All connectors of the connectable Figure.
     */
    protected Collection<Connector> connectors = Collections.emptyList();
    
    /** Creates a new instance. */
    public ConnectorHandle(Connector connector, ConnectionFigure prototype) {
        super(connector.getOwner());
        this.connector = connector;
        this.prototype = prototype;
    }
    
    public Point2D.Double getLocationOnDrawing() {
        return connector.getAnchor();
    }
    
    public Point getLocation() {
        return view.drawingToView(connector.getAnchor());
    }
    
    public void draw(Graphics2D g) {
        Graphics2D gg = (Graphics2D) g.create();
        gg.transform(view.getDrawingToViewTransform());
        for (Connector c : connectors) {
            c.draw(gg);
        }
        if (createdConnection == null) {
            drawCircle(g, Color.blue, Color.black);
        } else {
            drawCircle(g, Color.GREEN, Color.BLACK);
            Point p = view.drawingToView(createdConnection.getEndPoint());
            g.setColor(Color.GREEN);
            g.fillOval(p.x - ANCHOR_WIDTH / 2, p.y - ANCHOR_WIDTH / 2, ANCHOR_WIDTH, ANCHOR_WIDTH);
            g.setColor(Color.BLACK);
            g.drawOval(p.x - ANCHOR_WIDTH / 2, p.y - ANCHOR_WIDTH / 2, ANCHOR_WIDTH, ANCHOR_WIDTH);
        }
    }
    
    
    public void trackStart(Point anchor, int modifiersEx) {
        setConnection(createConnection());
        
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        
        Point2D.Double p = getLocationOnDrawing();
        getConnection().setStartPoint(p);
        getConnection().setEndPoint(p);
        view.getDrawing().add(getConnection());
    }
    
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        //updateConnectors(lead);
        Point2D.Double p = view.viewToDrawing(lead);
        
        Rectangle r = new Rectangle(
                view.drawingToView(getConnection().getEndPoint())
                );
        r.grow(ANCHOR_WIDTH, ANCHOR_WIDTH);
        fireAreaInvalidated(r);
        Figure figure = findConnectableFigure(p, view.getDrawing());
        if (figure != connectableFigure) {
            connectableFigure = figure;
            repaintConnectors();
        }
        connectableConnector = findConnectableConnector(figure, p);
        if (connectableConnector != null) {
            p = connectableConnector.getAnchor();
        }
        getConnection().willChange();
        getConnection().setEndPoint(p);
        getConnection().changed();
        r = new Rectangle(view.drawingToView(p));
        r.grow(ANCHOR_WIDTH, ANCHOR_WIDTH);
        fireAreaInvalidated(r);
    }
    
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        Point2D.Double p = view.viewToDrawing(lead);
        view.getConstrainer().constrainPoint(p);
        Figure f = findConnectableFigure(p, view.getDrawing());
        connectableConnector = findConnectableConnector(f, p);
        if (connectableConnector != null) {
            final Drawing drawing = view.getDrawing();
            final ConnectionFigure createdConnection = getConnection();
            getConnection().setStartConnector(connector);
            getConnection().setEndConnector(connectableConnector);
            getConnection().updateConnection();
            view.clearSelection();
            view.addToSelection(createdConnection);
            view.getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
                @Override public String getPresentationName() {
                    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
                    return labels.getString("createConnectionFigure");
                }
                @Override public void undo() throws CannotUndoException {
                    super.undo();
                    drawing.remove(createdConnection);
                }
                @Override public void redo() throws CannotRedoException {
                    super.redo();
                    drawing.add(createdConnection);
                    view.clearSelection();
                    view.addToSelection(createdConnection);
                }
            });
        } else {
            view.getDrawing().remove(getConnection());
        }
        connectableConnector = null;
        connectors = Collections.emptyList();
        setConnection(null);
        setTargetFigure(null);
    }
    
    /**
     * Creates the ConnectionFigure. By default the figure prototype is
     * cloned.
     */
    protected ConnectionFigure createConnection() {
        return (ConnectionFigure) prototype.clone();
    }
    protected void setConnection(ConnectionFigure newConnection) {
        createdConnection = newConnection;
    }
    
    protected ConnectionFigure getConnection() {
        return createdConnection;
    }
    
    protected Figure getTargetFigure() {
        return connectableFigure;
    }
    
    protected void setTargetFigure(Figure newTargetFigure) {
        connectableFigure = newTargetFigure;
    }
    private Figure findConnectableFigure(Point2D.Double p, Drawing drawing) {
        for (Figure figure : drawing.getFiguresFrontToBack()) {
            if (!figure.includes(getConnection()) &&
                    figure.canConnect() &&
                    figure.contains(p)) {
                return figure;
            }
            
        }
        return null;
    }
    
    /**
     * Finds a connection end figure.
     */
    protected Connector findConnectableConnector(Figure connectableFigure, Point2D.Double p) {
        Connector target = (connectableFigure == null) ?
            null :
            connectableFigure.findConnector(p, getConnection());
        
        if ((connectableFigure != null) && connectableFigure.canConnect()
        && !connectableFigure.includes(getOwner())
        && getConnection().canConnect(connector, target)) {
            return target;
        }
        return null;
    }
    
    protected Rectangle basicGetBounds() {
        Rectangle r = new Rectangle(getLocation());
        r.grow(getHandlesize() / 2, getHandlesize() / 2);
        return r;
    }
    @Override public boolean isCombinableWith(Handle handle) {
        return false;
    }
    /**
     * Updates the list of connectors that we draw when the user
     * moves or drags the mouse over a figure to which can connect.
     */
    public void repaintConnectors() {
        Rectangle2D.Double invalidArea = null;
        for (Connector c : connectors) {
            if (invalidArea == null) {
                invalidArea = c.getDrawingArea();
            } else {
                invalidArea.add(c.getDrawingArea());
            }
        }
        connectors = (connectableFigure == null) ? 
            new java.util.LinkedList<Connector>() : 
            connectableFigure.getConnectors(prototype);
        for (Connector c : connectors) {
            if (invalidArea == null) {
                invalidArea = c.getDrawingArea();
            } else {
                invalidArea.add(c.getDrawingArea());
            }
        }
        if (invalidArea != null) {
            view.getComponent().repaint(
                    view.drawingToView(invalidArea)
                    );
        }
    }
}
