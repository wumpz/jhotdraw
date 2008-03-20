/*
 * @(#)NodeFigure.java  1.0  July 4, 2006
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.samples.net.figures;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.geom.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.util.*;
import org.jhotdraw.xml.*;

/**
 * NodeFigure.
 *
 * @author Werner Randelshofer
 * @version 1.0 July 4, 2006 Created.
 */
public class NodeFigure extends TextFigure {
    private LinkedList<AbstractConnector> connectors;
    private static LocatorConnector north;
    private static LocatorConnector south;
    private static LocatorConnector east;
    private static LocatorConnector west;
    
    /** Creates a new instance. */
    public NodeFigure() {
        RectangleFigure rf = new RectangleFigure();
        setDecorator(rf);
        createConnectors();
        DECORATOR_INSETS.basicSet(this, new Insets2D.Double(6,10,6,10));
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.net.Labels");
        setText(labels.getString("nodeDefaultName"));
        setAttributeEnabled(DECORATOR_INSETS, false);
    }
    
    private void createConnectors() {
        connectors = new LinkedList<AbstractConnector>();
        connectors.add(new LocatorConnector(this, RelativeLocator.north()));
        connectors.add(new LocatorConnector(this, RelativeLocator.east()));
        connectors.add(new LocatorConnector(this, RelativeLocator.west()));
        connectors.add(new LocatorConnector(this, RelativeLocator.south()));
    }
    
    @Override public Collection getConnectors(ConnectionFigure prototype) {
        return Collections.unmodifiableList(connectors);
    }
    
    @Override public Collection<Handle> createHandles(int detailLevel) {
        java.util.List<Handle> handles = new LinkedList<Handle>();
        if (detailLevel == 0) {
            handles.add(new MoveHandle(this, RelativeLocator.northWest()));
            handles.add(new MoveHandle(this, RelativeLocator.northEast()));
            handles.add(new MoveHandle(this, RelativeLocator.southWest()));
            handles.add(new MoveHandle(this, RelativeLocator.southEast()));
            for (Connector c : connectors) {
                handles.add(new ConnectorHandle(c, new LineConnectionFigure()));
            }
        }
        return handles;
    }
    
    @Override public Rectangle2D.Double getFigureDrawingArea() {
        Rectangle2D.Double b = super.getFigureDrawingArea();
        // Grow for connectors
        Geom.grow(b, 10d, 10d);
        return b;
    }
    
    @Override public Connector findConnector(Point2D.Double p, ConnectionFigure figure) {
        // return closest connector
        double min = Double.MAX_VALUE;
        Connector closest = null;
        for (Connector c : connectors) {
            Point2D.Double p2 = Geom.center(c.getBounds());
            double d = Geom.length2(p.x, p.y, p2.x, p2.y);
            if (d < min) {
                min = d;
                closest = c;
            }
        }
        return closest;
    }
    
    @Override public Connector findCompatibleConnector(Connector c, boolean isStart) {
        return connectors.getFirst();
    }
    
    public NodeFigure clone() {
        NodeFigure that = (NodeFigure) super.clone();
        that.createConnectors();
        return that;
    }
    
    @Override public int getLayer() {
        return -1; // stay below ConnectionFigures
    }
    
    @Override protected void writeDecorator(DOMOutput out) throws IOException {
        // do nothing
    }
    @Override protected void readDecorator(DOMInput in) throws IOException {
        // do nothing
    }
    
    public void setAttribute(AttributeKey key, Object newValue) {
        super.setAttribute(key, newValue);
        if (getDecorator() != null) {
            key.basicSet(getDecorator(), newValue);
        }
    }
}

