/*
 * @(#)ConnectionHandle.java  1.0  20. Juni 2006
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

import org.jhotdraw.undo.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.geom.*;

/**
 * A handle to connect figures.
 * The connection object to be created is specified by a prototype.
 *
 * @author Werner Randelshofer.
 * @version 1.0 20. Juni 2006 Created.
 */
public class ConnectionHandle extends LocatorHandle {
    private CompositeEdit edit;
    /**
     * the currently created connection
     */
    private ConnectionFigure currentConnection;
    
    /**
     * the prototype of the connection to be created
     */
    private ConnectionFigure prototype;
    
    /**
     * the current target
     */
    private Figure targetFigure;
    
    /** Creates a new instance. */
    public ConnectionHandle(Figure owner, Locator locator, ConnectionFigure prototype) {
        super(owner, locator);
        this.prototype = prototype;
    }
    
    public void draw(Graphics2D g) {
        drawCircle(g, Color.blue, Color.blue.darker());
    }
    
    
    public void trackStart(Point anchor, int modifiersEx) {
        setConnection(createConnection());
        
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        
        edit = new CompositeEdit(labels.getString("createConnection"));
        fireUndoableEditHappened(edit);
        
        Point2D.Double p = getLocationOnDrawing();
        getConnection().setStartPoint(p);
        getConnection().setEndPoint(p);
        view.getDrawing().add(getConnection());
    }
    
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        Point2D.Double p = view.viewToDrawing(lead);
        Figure f = findConnectableFigure(p, view.getDrawing());
        // track the figure containing the mouse
        if (f != getTargetFigure()) {
            if (getTargetFigure() != null) {
                getTargetFigure().setConnectorsVisible(false, null);
            }
            setTargetFigure(f);
            if (getTargetFigure() != null) {
                getTargetFigure().setConnectorsVisible(true, getConnection());
            }
        }
        
        Connector target = findConnectionTarget(p, view.getDrawing());
        if (target != null) {
            p = Geom.center(target.getBounds());
        }
        getConnection().setEndPoint(p);
    }
    
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        Point2D.Double p = view.viewToDrawing(lead);
		Connector target = findConnectionTarget(p, view.getDrawing());
		if (target != null) {
			getConnection().setStartConnector(getStartConnector());
			getConnection().setEndConnector(target);
			getConnection().updateConnection();
		} else {
			view.getDrawing().remove(getConnection());
			//setUndoActivity(null);
                        edit.setSignificant(false);
		}
		setConnection(null);
		if (getTargetFigure() != null) {
			getTargetFigure().setConnectorsVisible(false, null);
			setTargetFigure(null);
		}
                
        fireUndoableEditHappened(edit);
    }
    
    /**
     * Creates the ConnectionFigure. By default the figure prototype is
     * cloned.
     */
    protected ConnectionFigure createConnection() {
        return (ConnectionFigure) prototype.clone();
    }
    protected void setConnection(ConnectionFigure newConnection) {
        currentConnection = newConnection;
    }
    
    protected ConnectionFigure getConnection() {
        return currentConnection;
    }
    
    protected Figure getTargetFigure() {
        return targetFigure;
    }
    
    protected void setTargetFigure(Figure newTargetFigure) {
        targetFigure = newTargetFigure;
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
    /*
    protected Connector findConnector(Point2D.Double p, Figure f) {
        return f.findConnector(p, getConnection());
    }*/
    
	/**
	 * Finds a connection end figure.
	 */
	protected Connector findConnectionTarget(Point2D.Double p, Drawing drawing) {
		Figure target = findConnectableFigure(p, drawing);
		if ((target != null) && target.canConnect()
			 && !target.includes(getOwner())
			 && getConnection().canConnect(getOwner(), target)) {
				return target.findConnector(p, getConnection());
		}
		return null;
	}

	private Connector getStartConnector() {
		Point2D.Double p = getLocationOnDrawing();
		return getOwner().findConnector(p, getConnection());
	}
}
