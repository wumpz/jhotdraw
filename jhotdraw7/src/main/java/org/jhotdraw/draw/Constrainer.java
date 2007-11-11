/*
 * @(#)Constrainer.java  4.0  2007-07-31
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

import java.awt.*;
import java.awt.geom.*;
import javax.swing.event.ChangeListener;
/**
 * Interface to constrain points and figures on a Drawing.
 * This can be used to implement different kinds of grids.
 *
 * @author  Werner Randelshofer
 * @version 4.0 2007-07-31 Redesigned to support the constrainement of rectangles.
 * <br>3.0 2007-04-29 Method constrainPoint(Point2D.Double, Direction) added.
 * <br>2.1 2006-07-03 Method isVisible() added.
 * <br>2.0 2006-01-17 Changed to support double precision coordinates.
 * <br>1.0 2004-03-14  Created.
 */
public interface Constrainer {
    /**
     * Constrains the placement of a point towards the closest constrainment
     * in any direction.
     * <p>
     * This method changes the point which is passed as a parameter.
     *
     * @param p A point on the drawing.
     * @return Returns the constrained point.
     */
    public Point2D.Double constrainPoint(Point2D.Double p);
    /**
     * Moves a point in the specified direction.
     * <p>
     * This method changes the point which is passed as a parameter.
     *
     * @param p A point on the drawing.
     * @param dir A direction.
     * @return Returns the constrained point.
     */
    public Point2D.Double movePoint(Point2D.Double p, Direction dir);
    /**
     * Constrains the placement of a rectangle towards the closest constrainment
     * in any direction.
     * <p>
     * This method changes the location of the rectangle which is passed as a
     * parameter. This method does not change the size of the rectangle.
     *
     * @param r A rectangle on the drawing.
     * @return Returns the constrained rectangle.
     */
    public Rectangle2D.Double constrainRectangle(Rectangle2D.Double r);
    /**
     * Moves a rectangle in a direction.
     * <p>
     * This method changes the location of the rectangle which is passed as a
     * parameter. This method does not change the size of the rectangle.
     *
     * @param r A rectangle on the drawing.
     * @param dir A direction.
     * @return Returns the constrained rectangle.
     */
    public Rectangle2D.Double moveRectangle(Rectangle2D.Double r, Direction dir);
    
    /**
     * Returns true if the Constrainer grid is visible.
     */
    public boolean isVisible();
    
    /**
     * Draws the constrainer grid for the specified drawing view.
     */
    public void draw(Graphics2D g, DrawingView view);
    
    /**
     * Adds a change listener.
     */
    public void addChangeListener(ChangeListener listener);
    /**
     * Removes a change listener.
     */
    public void removeChangeListener(ChangeListener listener);
}
