/*
 * @(#)DrawingChangeEvent.java  2.1  2007-05-21
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
 * Change event passed to DrawingChangeListeners.
 *
 * @author Werner Randelshofer
 * @version 2.1 2007-05-21 Added z-index property. 
 * <br>1.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class DrawingEvent extends EventObject {
    private Rectangle2D.Double invalidatedArea;
    private Figure figure;
    private int index;
    
    /**
     * Constructs an event for the provided Drawing.
     * @param figure The changed figure.
     * @param invalidatedArea The bounds of the invalidated area on the drawing.
     */
    public DrawingEvent(Drawing source, Figure figure, Rectangle2D.Double invalidatedArea) {
        this(source, figure, invalidatedArea, -1);
    }
    /**
     * Constructs an event for the provided Drawing.
     * @param figure The changed figure.
     * @param invalidatedArea The bounds of the invalidated area on the drawing.
     */
    public DrawingEvent(Drawing source, Figure figure, Rectangle2D.Double invalidatedArea, int zIndex) {
        super(source);
        this.figure = figure;
        this.invalidatedArea = invalidatedArea;
        this.index = 0;
    }
    
    
    /**
     *  Gets the changed drawing.
     */
    public Drawing getDrawing() {
        return (Drawing) getSource();
    }
    /**
     *  Gets the changed figure.
     */
    public Figure getFigure() {
        return figure;
    }
    
    /**
     *  Gets the bounds of the invalidated area on the drawing.
     */
    public Rectangle2D.Double getInvalidatedArea() {
        return invalidatedArea;
    }
    
    /**
     * Returns the z-index of the figure.
     */
    public int getIndex() {
        return index;
    }
}
