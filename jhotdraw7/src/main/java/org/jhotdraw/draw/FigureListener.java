/*
 * @(#)FigureListener.java  1.1  2007-02-09
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

import java.util.*;
/**
 * Listener interested in Figure changes.
 *
 * @author Werner Randelshofer
 * @version 1.1 2007-02-09 Method figureHandlesChanged added.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface FigureListener extends EventListener {
    
    /**
     * Sent when the drawing area used by the figure needs to be repainted.
     */
    public void figureAreaInvalidated(FigureEvent e);
    /**
     * Sent when an attribute of the figure has changed.
     */
    public void figureAttributeChanged(FigureEvent e);
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
