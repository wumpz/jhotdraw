/*
 * @(#)CompositeFigureEvent.java  3.0  2007-07-17
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
import java.util.*;
/**
 * Change event passed to CompositeFigureListeners.
 *
 * @author Werner Randelshofer
 * @version 3.0 2007-07-17 Renamed from DrawingEvent to CompositeFigureEvent. 
 * <br>2.1 2007-05-21 Added z-index property. 
 * <br>1.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class CompositeFigureEvent extends EventObject {
    private Rectangle2D.Double invalidatedArea;
    private Figure child;
    private int index;
    
    /**
     * Constructs an event for the provided CompositeFigure.
     * @param source The composite figure.
     * @param child The changed figure.
     * @param invalidatedArea The bounds of the invalidated area on the drawing.
     */
    public CompositeFigureEvent(CompositeFigure source, Figure child, Rectangle2D.Double invalidatedArea, int zIndex) {
        super(source);
        this.child = child;
        this.invalidatedArea = invalidatedArea;
        this.index = 0;
    }
    
    
    /**
     *  Gets the changed drawing.
     */
    public CompositeFigure getCompositeFigure() {
        return (CompositeFigure) getSource();
    }
    /**
     *  Gets the changed child figure.
     */
    public Figure getChildFigure() {
        return child;
    }
    
    /**
     *  Gets the bounds of the invalidated area on the drawing.
     */
    public Rectangle2D.Double getInvalidatedArea() {
        return invalidatedArea;
    }
    
    /**
     * Returns the z-index of the child figure.
     */
    public int getIndex() {
        return index;
    }
}
