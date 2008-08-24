/*
 * @(#)Layouter.java  2.0  2006-01-14
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
 * <p>
 * Design pattern:<br>
 * Name: Strategy.<br>
 * Role: Strategy.<br>
 * Partners: {@link ConnectionFigure} as Context.
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