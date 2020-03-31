/*
 * @(#)FigureChangeEvent.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.event;

import org.jhotdraw.draw.figure.Figure;
import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.draw.*;

/**
 * An {@code EventObject} sent to {@link FigureListener}s.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p>
 * <em>Observer</em><br>
 * State changes of figures can be observed by other objects. Specifically
 * {@code CompositeFigure} observes area invalidations and remove requests
 * of its child figures. {@link DrawingView} also observes area invalidations
 * of its drawing object.
 * Subject: {@link Figure}; Observer:
 * {@link FigureListener}; Event: {@link FigureEvent}; Concrete Observer:
 * {@link org.jhotdraw.draw.CompositeFigure}, {@link DrawingView}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FigureEvent extends EventObject {

    private static final long serialVersionUID = 1L;
    private Rectangle2D.Double invalidatedArea;
    private AttributeKey<?> attribute;
    private Object oldValue;
    private Object newValue;

    /**
     * Constructs an event for the given source Figure.
     *
     * @param invalidatedArea The bounds of the invalidated area on the drawing.
     */
    public FigureEvent(Figure source, Rectangle2D.Double invalidatedArea) {
        super(source);
        this.invalidatedArea = invalidatedArea;
    }

    /**
     * Constructs an event for the given source Figure.
     */
    public FigureEvent(Figure source, AttributeKey<?> attribute, Object oldValue, Object newValue) {
        super(source);
        this.attribute = attribute;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * Gets the changed figure
     */
    public Figure getFigure() {
        return (Figure) getSource();
    }

    /**
     * Gets the bounds of the invalidated area on the drawing.
     */
    public Rectangle2D.Double getInvalidatedArea() {
        return invalidatedArea;
    }

    public AttributeKey<?> getAttribute() {
        return attribute;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }
}
