/*
 * @(#)Layouter.java  2.0  2006-01-14
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
import java.awt.*;
import java.awt.geom.*;
/**
 * A Layouter encapsulates a algorithm to layout
 * a CompositeFigure. It is passed on to a figure which delegates the
 * layout task to the Layouter's layout method.
 * The Layouter might need access to some information
 * specific to a certain figure in order to layout it out properly.
 * 
 * Note: Currently, only the GraphicalCompositeFigure uses
 *       such a Layouter to layout its child components.
 * 
 * 
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface Layouter /*extends Serializable, Storable*/ {
    
    /**
     * Calculate the layout for the figure and all its subelements. The
     * layout is not actually performed but just its dimensions are calculated.
     *
     * @param anchor start point for the layout
     * @param lead minimum lead point for the layout
     */
    public Rectangle2D.Double calculateLayout(CompositeFigure compositeFigure, Point2D.Double anchor, Point2D.Double lead);
    
    /**
     * Method which lays out a figure. It is called by the figure
     * if a layout task is to be performed. Implementing classes
     * specify a certain layout algorithm in this method.
     *
     * @param anchor start point for the layout
     * @param lead minimum lead point for the layout
     */
    public Rectangle2D.Double layout(CompositeFigure compositeFigure, Point2D.Double anchor, Point2D.Double lead);
}