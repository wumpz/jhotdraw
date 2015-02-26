/*
 * @(#)FigureListener.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */


package org.jhotdraw.draw.event;

import java.util.*;

/**
 * Interface implemented by observers of {@link org.jhotdraw.draw.Figure} objects.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Observer</em><br>
 * State changes of figures can be observed by other objects. Specifically
 * {@code CompositeFigure} observes area invalidations and remove requests
 * of its child figures. {@code DrawingView} also observes area invalidations
 * of its drawing object.
 * Subject: {@link org.jhotdraw.draw.Figure};
 * Observer: {@link FigureListener};
 * Event: {@link FigureEvent};
 * Concrete Observer: {@link org.jhotdraw.draw.CompositeFigure},
 * {@link org.jhotdraw.draw.DrawingView}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface FigureListener extends EventListener {
    
    /**
     * Sent when the drawing area used by the figure needs to be repainted.
     */
    public void areaInvalidated(FigureEvent e);
    /**
     * Sent when an attribute of the figure has changed.
     */
    public void attributeChanged(FigureEvent e);
    /**
     * Sent when handles of a Figure have been added, removed or replaced.
     * <p>
     * DrawingViews listen to this event to repopulate the Handles.
     * <p>
     * A Figure should not fire this event, if just the state or the location
     * of Handle has changed.
     */
    public void figureHandlesChanged(FigureEvent e);
    /**
     * Sent when the geometry (for example the bounds) of the figure has changed.
     */
    public void figureChanged(FigureEvent e);
    
    /**
     * Sent when a figure was added to a drawing.
     */
    public void figureAdded(FigureEvent e);
    /**
     * Sent when a figure was removed from a drawing.
     */
    public void figureRemoved(FigureEvent e);
    /**
     * Sent when the figure requests to be removed from a drawing.
     */
    public void figureRequestRemove(FigureEvent e);
    
}
