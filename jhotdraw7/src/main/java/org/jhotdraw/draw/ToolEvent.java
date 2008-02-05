/*
 * @(#)ToolEvent.java  3.0  2006-02-13
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
import java.util.*;
/**
 * An event sent to ToolListener's.
 *
 * @author Werner Randelshofer
 * @version 3.0 2006-02-13 Changed to support multiple views.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class ToolEvent extends EventObject {
    private Rectangle invalidatedArea;
    private DrawingView view;
    
    /** Creates a new instance. */
    public ToolEvent(Tool src, DrawingView view, Rectangle invalidatedArea) {
        super(src);
        this.view = view;
        this.invalidatedArea = invalidatedArea;
    }
    
    /**
     * Gets the tool which is the source of the event.
     */
    public Tool getTool() {
        return (Tool) getSource();
    }
    /**
     * Gets the drawing view of the tool.
     */
    public DrawingView getView() {
        return view;
    }
    /**
     *  Gets the bounds of the invalidated area on the drawing view.
     */
    public Rectangle getInvalidatedArea() {
        return invalidatedArea;
    }
}
