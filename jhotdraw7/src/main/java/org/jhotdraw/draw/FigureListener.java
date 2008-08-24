/*
 * @(#)FigureListener.java  2.0  2007-07-17
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
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

import java.util.*;
/**
 * Listener interested in Figure changes.
 * <p>
 * Design pattern:<br>
 * Name: Observer.<br>
 * Role: Observer.<br>
 * Partners: {@link Figure} as Subject.
 *
 * @author Werner Randelshofer
 * @version 2.0 2007-07-17 Shortened method names. 
 * <br>1.1 2007-02-09 Method figureHandlesChanged added.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
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
