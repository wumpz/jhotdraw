/*
 * @(#)HandleMulticaster.java  1.0  2003-12-01
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
�
 */


package org.jhotdraw.draw;

import org.jhotdraw.util.*;
import org.jhotdraw.undo.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Forwards events to one or many handles.
 *
 * @author Werner Randelshofer
 * @version 1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class HandleMulticaster {
    LinkedList<Handle> handles;
    CompositeEdit edit;
    
    /** Creates a new instance. */
    public HandleMulticaster(Handle handle) {
        this.handles = new LinkedList<Handle>();
        this.handles.add(handle);
    }
    /** Creates a new instance. */
    public HandleMulticaster(Collection<Handle> handles) {
        this.handles = new LinkedList<Handle>(handles);
    }
    
    public void draw(java.awt.Graphics2D g) {
        for (Handle h : handles) {
            h.draw(g);
        }
    }
    
    public void keyPressed(java.awt.event.KeyEvent e) {
        for (Handle h : handles) {
            h.keyPressed(e);
        }
    }
    
    public void keyReleased(java.awt.event.KeyEvent e) {
        for (Handle h : handles) {
            h.keyReleased(e);
        }
    }
    
    public void keyTyped(java.awt.event.KeyEvent e) {
        for (Handle h : handles) {
            h.keyTyped(e);
        }
    }
    
    public void trackEnd(Point current, Point anchor, int modifiersEx, DrawingView view) {
        for (Handle h : new ReversedList<Handle>(handles)) {
            h.trackEnd(current, anchor, modifiersEx);
        }
        view.getDrawing().fireUndoableEditHappened(edit);
    }
    
    public void trackStart(Point anchor, int modifiersEx, DrawingView view) {
        view.getDrawing().fireUndoableEditHappened(edit = new CompositeEdit());
        
        for (Handle h : handles) {
            h.trackStart(anchor, modifiersEx);
        }
    }
    public void trackDoubleClick(Point p, int modifiersEx, DrawingView view) {
        for (Handle h : handles) {
            h.trackDoubleClick(p, modifiersEx);
        }
    }
    
    public void trackStep(Point anchor, Point lead, int modifiersEx, DrawingView view) {
        for (Handle h : handles) {
            h.trackStep(anchor, lead, modifiersEx);
        }
    }
    
}
