/*
 * @(#)CompositeFigureEvent.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.draw.event;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.CompositeFigure;
import java.awt.geom.*;
import java.util.*;
/**
 * An {@code EventObject} sent to {@link CompositeFigureListener}s.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Observer</em><br>
 * Changes in the composition of a composite figure can be observed.<br>
 * Subject: {@link CompositeFigure}; Observer:
 * {@link CompositeFigureListener}; Event: {@link CompositeFigureEvent}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CompositeFigureEvent extends EventObject {
    private static final long serialVersionUID=1L;
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
