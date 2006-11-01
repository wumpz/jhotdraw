/*
 * @(#)Connector.java  2.0  2006-01-14
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

import org.jhotdraw.xml.DOMStorable;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
/**
 * Connectors know how to locate a connection point on a figure.
 * A Connector knows its owning figure and can determine either
 * the start or the endpoint of a given connection figure. A connector
 * has a display box that describes the area of a figure it is
 * responsible for. A connector can be visible but it doesn't have
 * to be.<br>
 *
 * @author Werner Randelshofer
 * @version 1.0 2003-12-01 Derived from JHotDraw 5.4b1.
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
     * Gets the display box of the connector.
     */
    public Rectangle2D.Double getBounds();

    /**
     * Gets the anchor of the connector.
     * This is a point at the center or at the bounds of the figure, where
     * the start or the end point will most likely be attached.
     * The purpose of this method is to give the user a hint, where the 
     * connector will most likely be attached to the owner of the connector.
     */
    public Point2D.Double getAnchor();

    /**
     * Updates the anchor of the connector.
     * This method is called when the user manually changes the end point of
     * the ConnectionFigure. The Connector can use this as a hint, where
     * the user wants to place the start or end point of the ConnectionFigure.
     */
    public void updateAnchor(Point2D.Double p);
    
    /**
     * Tests if a point is contained in the connector.
     */
    public boolean contains(Point2D.Double p);
    
    /**
     * Draws this connector. Connectors don't have to be visible
     * and it is OK leave this method empty.
     */
    public void draw(Graphics2D g);
    /**
     * Returns a clone of the Connection.
     */
    public Object clone();
}
