/*
 * @(#)HandleTracker.java  1.0  2003-12-01
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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
/**
 * HandleTracker implements interactions with the handles of a Figure.
 *
 * @see SelectionTool
 *
 * @author Werner Randelshofer
 * @version 1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class HandleTracker extends AbstractTool {
    private Handle masterHandle;
    private HandleMulticaster multicaster;
    private Point anchor;
    
    /** Creates a new instance. */
    public HandleTracker(Handle handle) {
        masterHandle = handle;
        multicaster = new HandleMulticaster(handle);
    }
    public HandleTracker(Handle master, Collection<Handle> handles) {
        masterHandle = master;
        multicaster = new HandleMulticaster(handles);
    }

    public void activate(DrawingEditor editor) {
        super.activate(editor);
        
        getView().setCursor(masterHandle.getCursor());
    }
    
    public void deactivate(DrawingEditor editor) {
        super.deactivate(editor);
        getView().setCursor(Cursor.getDefaultCursor());
    }
    
    public void keyPressed(KeyEvent evt) {
        multicaster.keyPressed(evt);
    }
    
    public void keyReleased(KeyEvent evt) {
        multicaster.keyReleased(evt);
    }
    
    public void keyTyped(KeyEvent evt) {
        multicaster.keyTyped(evt);
    }
    
    public void mouseClicked(MouseEvent evt) {
        if (evt.getClickCount() == 2) {
        multicaster.trackDoubleClick(new Point(evt.getX(), evt.getY()),
                evt.getModifiersEx(), getView());
        }
    }
    
    public void mouseDragged(MouseEvent evt) {
        multicaster.trackStep(anchor, new Point(evt.getX(), evt.getY()), 
                evt.getModifiersEx(), getView());
    }
    
    public void mouseEntered(MouseEvent evt) {
    }
    
    public void mouseExited(MouseEvent evt) {
    }
    
    public void mouseMoved(MouseEvent evt) {
        updateCursor(editor.findView((Container) evt.getSource()),new Point(evt.getX(), evt.getY()));
    }
    
    public void mousePressed(MouseEvent evt) {
        //handle.mousePressed(evt);
        anchor = new Point(evt.getX(), evt.getY());
        multicaster.trackStart(anchor, evt.getModifiersEx(), getView());
    }
    
    public void mouseReleased(MouseEvent evt) {
        multicaster.trackEnd(anchor, new Point(evt.getX(), evt.getY()),
                evt.getModifiersEx(), getView());
        fireToolDone();
    }    
}
