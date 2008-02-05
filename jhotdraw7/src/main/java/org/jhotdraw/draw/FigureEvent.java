/*
 * @(#)FigureChangeEvent.java  3.0  2006-06-07
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
�
 */

package org.jhotdraw.draw;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
/**
 * Change event passed to FigureListeners.
 *
 * @author Werner Randelshofer
 * @version 3.0 2006-06-07 Reworked.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class FigureEvent extends EventObject {
    private Rectangle2D.Double invalidatedArea;
    private AttributeKey attribute;
    private Object oldValue;
    private Object newValue;
    
    /**
     * Constructs an event for the given source Figure.
     * @param invalidatedArea The bounds of the invalidated area on the drawing.
     */
    public FigureEvent(Figure source, Rectangle2D.Double invalidatedArea) {
        super(source);
        this.invalidatedArea = invalidatedArea;
    }
    
    /**
     * Constructs an event for the given source Figure.
     */
    public FigureEvent(Figure source, AttributeKey attribute, Object oldValue, Object newValue) {
        super(source);
        this.attribute = attribute;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    /**
     *  Gets the changed figure
     */
    public Figure getFigure() {
        return (Figure) getSource();
    }
    
    /**
     *  Gets the bounds of the invalidated area on the drawing.
     */
    public Rectangle2D.Double getInvalidatedArea() {
        return invalidatedArea;
    }
    
    public AttributeKey getAttribute() {
        return attribute;
    }
    public Object getOldValue() {
        return oldValue;
    }
    public Object getNewValue() {
        return newValue;
    }
}
