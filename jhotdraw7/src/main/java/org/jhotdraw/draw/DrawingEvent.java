/*
 * @(#)DrawingChangeEvent.java  2.0  2006-01-14
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
 */

package org.jhotdraw.draw;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
/**
 * Change event passed to DrawingChangeListeners.
 *
 * @author Werner Randelshofer
 * @version 1.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class DrawingEvent extends EventObject {
    private Rectangle2D.Double invalidatedArea;
    private Figure figure;
    
    /**
     * Constructs an event for the provided Drawing.
     * @param figure The changed figure.
     * @param invalidatedArea The bounds of the invalidated area on the drawing.
     */
    public DrawingEvent(Drawing source, Figure figure, Rectangle2D.Double invalidatedArea) {
        super(source);
        this.figure = figure;
        this.invalidatedArea = invalidatedArea;
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
}
