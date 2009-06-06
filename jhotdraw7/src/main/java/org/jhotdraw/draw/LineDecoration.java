/*
 * @(#)LineDecoration.java  2.1  2007-05-20
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
import java.io.*;
/**
 * A <em>line decoration</em> can be used to draw a decoration at the start or
 * end of a line.
 * <p>
 * Specifically, {@code LineDecoration} is used by {@link BezierFigure} to draw
 * decorations at the ends of a bezier path. A line decoration} can
 * be set as an attribute value to a bezier figure using the attribute keys
 * {@code AttributeKeys.START_DECORATION} and
 * {@code AttributeKeys.END_DECORATION}.
 * <p>
 * {@code LineDecoration} is not limited to this use. Any {@link Figure} can use
 * it to draw line decorations.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Strategy</em><br>
 * {@code LineDecoration} encapsulates a strategy for drawing line decorations
 * of a {@code BezierFigure}.<br>
 * Strategy: {@link LineDecoration}; Context: {@link BezierFigure}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version 2.1 2007-05-20 Renamed getDrawBounds to getDrawingArea.
 * <br>2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface LineDecoration
extends Cloneable, Serializable {
    
    /**
     * Draws the decoration in the direction specified by the two Points.
     */
    public void draw(Graphics2D g, Figure f, Point2D.Double p1, Point2D.Double p2);
    
    /**
     * Returns the radius of the decorator.
     * This is used to crop the end of the line, to prevent it from being
     * drawn it over the decorator.
     */
    public abstract double getDecorationRadius(Figure f);
    
    /**
     * Returns the drawing bounds of the decorator.
     */
    public Rectangle2D.Double getDrawingArea(Figure f, Point2D.Double p1, Point2D.Double p2);
}
