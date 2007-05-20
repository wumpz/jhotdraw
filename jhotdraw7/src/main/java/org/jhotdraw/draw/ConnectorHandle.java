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
     * The current target Figure.
     */
    private Figure targetFigure;
    /**
     * The current target Connector.
     */
    private Connector targetConnector;
    
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
        if (createdConnection == null) {
            drawCircle(g, Color.blue, Color.blue.darker());
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
        getCreatedConnection().setStartPoint(p);
        getCreatedConnection().setEndPoint(p);
        view.getDrawing().add(getCreatedConnection());
    }
    
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        Point2D.Double p = view.viewToDrawing(lead);
        view.getConstrainer().constrainPoint(p);
        Figure f = findConnectableFigure(p, view.getDrawing());
        // track the figure containing the mouse
        if (f != getTargetFigure()) {
            setTargetFigure(f);
        }
        
        Rectangle r = new Rectangle(
                view.drawingToView(getCreatedConnection().getEndPoint())
                );
        r.grow(ANCHOR_WIDTH, ANCHOR_WIDTH);
        fireAreaInvalidated(r);
        targetConnector = findConnectionTarget(p, view.getDrawing());
        if (targetConnector != null) {
            p = targetConnector.getAnchor();
        }
        getCreatedConnection().willChange();
        getCreatedConnection().setEndPoint(p);
        getCreatedConnection().changed();
        r = new Rectangle(view.drawingToView(p));
        r.grow(ANCHOR_WIDTH, ANCHOR_WIDTH);
        fireAreaInvalidated(r);
    }
    
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        Point2D.Double p = view.viewToDrawing(lead);
        view.getConstrainer().constrainPoint(p);
        targetConnector = findConnectionTarget(p, view.getDrawing());
        if (targetConnector != null) {
            final Drawing drawing = view.getDrawing();
            final ConnectionFigure createdConnection = getCreatedConnection();
            getCreatedConnection().setStartConnector(connector);
            getCreatedConnection().setEndConnector(targetConnector);
            getCreatedConnection().updateConnection();
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
            view.getDrawing().remove(getCreatedConnection());
        }
        targetConnector = null;
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
    
    protected ConnectionFigure getCreatedConnection() {
        return createdConnection;
    }
    
    protected Figure getTargetFigure() {
        return targetFigure;
    }
    
    protected void setTargetFigure(Figure newTargetFigure) {
        targetFigure = newTargetFigure;
    }
    private Figure findConnectableFigure(Point2D.Double p, Drawing drawing) {
        for (Figure figure : drawing.getFiguresFrontToBack()) {
            if (!figure.includes(getCreatedConnection()) &&
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
    protected Connector findConnectionTarget(Point2D.Double p, Drawing drawing) {
        Figure targetFigure = findConnectableFigure(p, drawing);
        Connector target = (targetFigure == null) ?
            null :
            targetFigure.findConnector(p, getCreatedConnection());
        
        if ((targetFigure != null) && targetFigure.canConnect()
        && !targetFigure.includes(getOwner())
        && getCreatedConnection().canConnect(connector, target)) {
            return target;
        }
        return null;
    }
    
    protected Rectangle basicGetBounds() {
        Rectangle r = new Rectangle(getLocation());
        r.grow(getHandlesize() / 2, getHandlesize() / 2);
        return r;
    }
}
