/*
 * @(#)Connector.java  2.0  2007-05-19
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

import org.jhotdraw.xml.DOMStorable;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
/**
 * A {@code Connector} knows how to locate a connection point on a figure.
 * A Connector knows its owning figure and can determine either
 * the start point or the end point of a given ConnectionFigure. A connector
 * has bounds which describe the area of a figure it is
 * responsible for. A connector can be drawn, but it doesn't have
 * to be.<br>
 *
 * @author Werner Randelshofer
 * @version 2.0 2007-05-19 Connectors don't have the ability to draw themselves
 * anymore. Its the responsibility of the tools and handles to draw the
 * connectors they can connect with.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface Connector extends Cloneable, Serializable, DOMStorable {
    
    /**
     * Finds the start point for the connection.
     */
    public Point2D.Double findStart(ConnectionFigure connection);
    
    /**
     * Finds the end point for the connection.
     */
    public Point2D.Double findEnd(ConnectionFigure connection);
    
    /**
     * Gets the connector's owner.
     */
    public Figure getOwner();
    
    /**
     * Gets the anchor of the connector.
     * This is a point at the center or at the bounds of the figure, where
     * the start or the end point will most likely be attached.
     * The purpose of this method is to give the user a hint, where the 
     * connector will most likely be attached to the owner of the connector.
     */
    public Point2D.Double getAnchor();
    /**
     * Gets the bounds of the connector.
     * This usually are the bounds of the Figure which owns the Connector.
     * The bounds can differ from the Figure bounds, if the Connector 
     * connects to a specific region of the Figure.
     */
    public Rectangle2D.Double getBounds();

    /**
     * Updates the anchor of the connector.
     * This method is called when the user manually changes the end point of
     * the ConnectionFigure. The Connector uses this as a hint for choosing
     * a new anchor position.
     */
    public void updateAnchor(Point2D.Double p);
    
    /**
     * Tests if a point is contained in the connector.
     */
    public boolean contains(Point2D.Double p);
    
    /**
     * Returns a clone of the Connection.
     */
    public Object clone();
    
    /**
     * Gets the drawing area of the connector.
     */
    public Rectangle2D.Double getDrawingArea();
    /** Draws the connector.
     */
    public void draw(Graphics2D g);
}
