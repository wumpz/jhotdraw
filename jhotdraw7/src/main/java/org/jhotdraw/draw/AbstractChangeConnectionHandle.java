/*
 * @(#)AbstractChangeConnectionHandle.java  2.1  2006-02-16
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

import java.awt.event.InputEvent;
import org.jhotdraw.geom.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
/**
 * AbstractChangeConnectionHandle factors the common code for handles
 * that can be used to reconnect connections.
 *
 * @author Werner Randelshofer
 * @version 2.1 2006-02-16 Remove liner from connection while tracking.
 * <br>2.0 2006-01-14 Changed to support double coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 * @see ChangeConnectionEndHandle
 * @see ChangeConnectionStartHandle
 */
public abstract class AbstractChangeConnectionHandle extends AbstractHandle {
    private Connector         originalTarget;
    private Figure            targetFigure;
    private ConnectionFigure  connection;
    private Point             start;
    /**
     * We temporarily remove the liner from the connection figure, while the
     * handle is being moved.
     * We store the liner here, and add it back when the user has finished
     * the interaction.
     */
    private Liner   liner;
    
    /**
     * Initializes the change connection handle.
     */
    protected AbstractChangeConnectionHandle(Figure owner) {
        super(owner);
        setConnection((ConnectionFigure) owner);
        setTargetFigure(null);
    }
    
    public boolean isCombinableWith(Handle handle) {
        return false;
    }
    /**
     * Returns the target connector of the change.
     */
    protected abstract Connector getTarget();
    
    /**
     * Disconnects the connection.
     */
    protected abstract void disconnect();
    
    /**
     * Connect the connection with the given figure.
     */
    protected abstract void connect(Connector c);
    
    /**
     * Sets the location of the target point.
     */
    protected abstract void setLocation(Point2D.Double p);
    /**
     * Returns the start point of the connection.
     */
    protected abstract Point2D.Double getLocation();
    
    /**
     * Gets the side of the connection that is unaffected by
     * the change.
     */
    protected Connector getSource() {
        if (getTarget() == getConnection().getStartConnector()) {
            return getConnection().getEndConnector();
        }
        return getConnection().getStartConnector();
    }
    
    
    /**
     * Disconnects the connection.
     */
    public void trackStart(Point anchor, int modifiersEx) {
        originalTarget = getTarget();
        start = anchor;
        liner = connection.getLiner();
        connection.setLiner(null);
        //disconnect();
    }
    
    /**
     * Finds a new target of the connection.
     */
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        Point2D.Double p = view.viewToDrawing(lead);
        view.getConstrainer().constrainPoint(p);
        Figure f = findConnectableFigure(p, view.getDrawing());
        
        // track the figure containing the mouse
        if (f != getTargetFigure()) {
            if (getTargetFigure() != null) {
                getTargetFigure().setConnectorsVisible(false, null);
            }
            setTargetFigure(f);
            if (getSource() == null) {
                if (getTargetFigure() != null) {
                    getTargetFigure().setConnectorsVisible(true, getConnection());
                }
                
            } else {
                if (getTargetFigure() != null
                        && canConnect(getSource().getOwner(), getTargetFigure())) {
                    getTargetFigure().setConnectorsVisible(true, getConnection());
                }
            }
        }
        
        Connector target = findConnectionTarget(p, view.getDrawing());
        if (target != null) {
            p = target.getAnchor();
        }
        
        setLocation(p);
    }
    
    /**
     * Connects the figure to the new target. If there is no
     * new target the connection reverts to its original one.
     */
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        // Change node type
        ConnectionFigure cf = (ConnectionFigure) getOwner();
        if (cf.getLiner() == null && 
                (modifiersEx & (InputEvent.META_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)) != 0 &&
                (modifiersEx & InputEvent.BUTTON2_DOWN_MASK) == 0) {
            int index = getBezierNodeIndex();
            BezierFigure f = getBezierFigure();
            f.willChange();
            BezierPath.Node v = f.getNode(index);
            if (index > 0 && index < f.getNodeCount() || f.isClosed()) {
                v.mask = (v.mask + 3) % 4;
            } else if (index == 0) {
                v.mask = ((v.mask & BezierPath.C2_MASK) == 0) ? BezierPath.C2_MASK : 0;
            } else {
                v.mask = ((v.mask & BezierPath.C1_MASK) == 0) ? BezierPath.C1_MASK : 0;
            }
            f.basicSetNode(index, v);
            f.changed();
            fireHandleRequestSecondaryHandles();
        }
        
        
        Point2D.Double p = view.viewToDrawing(lead);
        view.getConstrainer().constrainPoint(p);
        Connector target = findConnectionTarget(p, view.getDrawing());
        if (target == null) {
            target = originalTarget;
        }
        
        setLocation(p);
        if (target != originalTarget) {
            disconnect();
            connect(target);
        }
        connection.setLiner(liner);
        getConnection().updateConnection();
        
        
        if (getTargetFigure() != null) {
            getTargetFigure().setConnectorsVisible(false, null);
            setTargetFigure(null);
        }
    }
    
    private Connector findConnectionTarget(Point2D.Double p, Drawing drawing) {
        Figure targetFigure = findConnectableFigure(p, drawing);
        
        if (getSource() == null && targetFigure != null) {
            return findConnector(p, targetFigure, getConnection());
        } else {
            if ((targetFigure != null) && targetFigure.canConnect()
            && targetFigure != originalTarget
                    && !targetFigure.includes(getOwner())
                    //&& getConnection().canConnect(getSource().getOwner(), targetFigure)) {
                    && canConnect(getSource().getOwner(), targetFigure)) {
                return findConnector(p, targetFigure, getConnection());
            }
        }
        return null;
    }
    
    protected abstract boolean canConnect(Figure existingEnd, Figure targetEnd);
    
    protected Connector findConnector(Point2D.Double p, Figure f, ConnectionFigure prototype) {
        return f.findConnector(p, prototype);
    }
    
    /**
     * Draws this handle.
     */
    public void draw(Graphics2D g) {
        drawCircle(g,
                (getTarget() == null) ? Color.red : Color.green,
                Color.black
                );
    }
    
    private Figure findConnectableFigure(Point2D.Double p, Drawing drawing) {
        for (Figure f : drawing.getFiguresFrontToBack()) {
            if (! f.includes(getConnection()) && f.canConnect() && f.contains(p)) {
                return f;
            }
        }
        return null;
    }
    
    protected void setConnection(ConnectionFigure newConnection) {
        connection = newConnection;
    }
    
    protected ConnectionFigure getConnection() {
        return connection;
    }
    
    protected void setTargetFigure(Figure newTarget) {
        targetFigure = newTarget;
    }
    
    protected Figure getTargetFigure() {
        return targetFigure;
    }
    protected Rectangle basicGetBounds() {
        //if (connection.getPointCount() == 0) return new Rectangle(0, 0, getHandlesize(), getHandlesize());
        Point center = view.drawingToView(getLocation());
        return new Rectangle(center.x - getHandlesize() / 2, center.y - getHandlesize() / 2, getHandlesize(), getHandlesize());
    }
    
    protected BezierFigure getBezierFigure() {
        return (BezierFigure) getOwner();
    }
    
    abstract protected int getBezierNodeIndex();
    
   @Override final public Collection<Handle> createSecondaryHandles() {
        LinkedList<Handle> list = new LinkedList<Handle>();
        if (((ConnectionFigure) getOwner()).getLiner() == null && liner == null) {
            int index = getBezierNodeIndex();
            BezierFigure f = getBezierFigure();
            BezierPath.Node v = f.getNode(index);
            if ((v.mask & BezierPath.C1_MASK) != 0 &&
                    (index != 0 || f.isClosed())) {
                list.add(new BezierControlPointHandle(f, index, 1));
            }
            if ((v.mask & BezierPath.C2_MASK) != 0 &&
                    (index < f.getNodeCount() - 1 ||
                    f.isClosed())) {
                list.add(new BezierControlPointHandle(f, index, 2));
            }
            if (index > 0 || f.isClosed()) {
                int i = (index == 0) ? f.getNodeCount() - 1 : index - 1;
                v = f.getNode(i);
                if ((v.mask & BezierPath.C2_MASK) != 0) {
                    list.add(new BezierControlPointHandle(f, i, 2));
                }
            }
            if (index < f.getNodeCount() - 2 || f.isClosed()) {
                int i = (index == f.getNodeCount() - 1) ? 0 : index + 1;
                v = f.getNode(i);
                if ((v.mask & BezierPath.C1_MASK) != 0) {
                    list.add(new BezierControlPointHandle(f, i, 1));
                }
            }
        }
        return list;
    }
    protected BezierPath.Node getBezierNode() {
        int index = getBezierNodeIndex();
        return getBezierFigure().getPointCount() > index ?
            getBezierFigure().getNode(index) :
            null;
    }
    
    public String getToolTipText(Point p) {
        ConnectionFigure f = (ConnectionFigure) getOwner();
        if (f.getLiner() == null && liner == null) {
            ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
            BezierPath.Node node = getBezierNode();
            return (node == null) ? null : labels.getFormatted("bezierNodeHandle.tip",
                    labels.getFormatted(
                    (node.getMask() == 0) ?
                        "bezierNode.linearNode" :
                        ((node.getMask() == BezierPath.C1C2_MASK) ?
                            "bezierNode.cubicNode" : "bezierNode.quadraticNode")
                            )
                            );
        } else {
            return null;
        }
    }
}
