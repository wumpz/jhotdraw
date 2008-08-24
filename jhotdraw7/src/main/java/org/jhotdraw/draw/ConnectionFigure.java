/*
 * @(#)ConnectionFigure.java  3.0  2007-05-18
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.draw;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.geom.*;

/**
 * A {@code ConnectionFigure} draws a connection between two {@link Connector}s.
 * A ConnectionFigure knows its start and end Connector.
 * A ConnectionFigure can be laid out using a {@link Liner}.
 * <p>
 * Design pattern:<br>
 * Name: Strategy.<br>
 * Role: Context.<br>
 * Partners: {@link Liner} as Strategy.
 *
 * @see Connector
 * @see Liner
 *
 * @author Werner Randelshofer
 * @version 3.0 2007-05-18 Methods canConnect use now Connector objects as
 * parameters instead of Figure objects. Removed method connectsSame. Added 
 * support for BezierPath.Node's.
 * <br>2.1 2007-02-09 Method setLiner renamed.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface ConnectionFigure
        extends Figure {
    // DRAWING
    // SHAPE AND BOUNDS
    // ATTRIBUTES
    // EDITING
    // CONNECTING
    /**
     * Sets the start {@code Connector} of the connection.
     * Set this to null to disconnect the start connection.
     * @param start the start Connector of the connection
     */
    public void setStartConnector(Connector start);
    /**
     * Gets the start {@code Connector}.
     * Returns null, if there is no start connection.
     */
    public Connector getStartConnector();
 
    /**
     * Sets the end Connector of the connection.
     * Set this to null to disconnect the end connection.
     * @param end the end Connector of the connection
     */
    public void setEndConnector(Connector end);
    /**
     * Gets the end Connector.
     * Returns null, if there is no end connection.
     */
    public Connector getEndConnector();
    
    /**
     * Updates the start and end point of the figure and fires figureChanged
     * events.
     */
    public void updateConnection();
    
    /**
     * Returns true, if this ConnectionFigure can connect the specified
     * {@code Connector}s.
     * Implement this method to constrain the allowed connections between figures.
     */
    public boolean canConnect(Connector start, Connector end);
    /**
     * Checks if this {@code ConnectionFigure} can be connect to the specified
     * {@code Connector}.
     * This is used to provide an early feedback to the user, when he/she
     * creates a new connection.
     */
    public boolean canConnect(Connector start);
    
    /**
     * Sets the start point.
     */
    public void setStartPoint(Point2D.Double p);
    
    /**
     * Sets the end point.
     */
    public void setEndPoint(Point2D.Double p);
    /**
     * Sets the specified point.
     */
    public void setPoint(int index, Point2D.Double p);
    /**
     * Gets the node count.
     */
    public int getNodeCount();
    
    /**
     * Returns the specified point.
     */
    public Point2D.Double getPoint(int index);
    /**
     * Returns the specified node.
     */
    public BezierPath.Node getNode(int index);
    /**
     * Sets the specified node.
     */
    public void setNode(int index, BezierPath.Node node);
    
    /**
     * Gets the start point.
     */
    public Point2D.Double getStartPoint();
    
    /**
     * Gets the end point.
     */
    public Point2D.Double getEndPoint();
    
    /**
     * Gets the start figure of the connection.
     * This is a convenience method for doing getStartConnector().getOwner()
     * and handling null cases.
     */
    public Figure getStartFigure();
    
    /**
     * Gets the end figure of the connection.
     * This is a convenience method for doing getEndConnector().getOwner()
     * and handling null cases.
     */
    public Figure getEndFigure();
// COMPOSITE FIGURES
    /**
     * Get a Liner object which encapsulated a lineout
     * algorithm for this figure. Typically, a Liner
     * accesses the child components of this figure and arranges
     * their graphical presentation.
     *
     * @return lineout strategy used by this figure
     */
    public Liner getLiner();
    /**
     * Set a Liner object which encapsulated a lineout
     * algorithm for this figure. Typically, a Liner
     * accesses the child components of this figure and arranges
     * their graphical presentation.
     *
     * @param newValue	encapsulation of a lineout algorithm.
     */
    public void setLiner(Liner newValue);
    /**
     * A lineout algorithm is used to define how the child components
     * should be laid out in relation to each other. The task for
     * lineouting the child components for presentation is delegated
     * to a Liner which can be plugged in at runtime.
     */
    public void lineout();
    // CLONING
    // EVENT HANDLING
}
