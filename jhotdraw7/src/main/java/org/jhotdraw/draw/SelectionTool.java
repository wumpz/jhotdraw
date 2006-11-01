/*
 * @(#)SelectionTool.java  1.0  2003-12-01
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
/**
 * Tool to select and manipulate figures.
 * A selection tool is in one of three states, e.g., background
 * selection, figure selection, handle manipulation. The different
 * states are handled by different tracker objects.
 *
 * @author Werner Randelshofer
 * @version 1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class SelectionTool extends AbstractTool
        implements ToolListener {
    /**
     * The tracker encapsulates the current state of the SelectionTool.
     */
    private Tool tracker;
    
    /** Creates a new instance. */
    public SelectionTool() {
        tracker = createAreaTracker();
        tracker.addToolListener(this);
    }
    
    
    
    public void activate(DrawingEditor editor) {
        super.activate(editor);
        tracker.activate(editor);
    }
    public void deactivate(DrawingEditor editor) {
        super.deactivate(editor);
        tracker.deactivate(editor);
    }
    
    public void keyPressed(KeyEvent e) {
        tracker.keyPressed(e);
    }
    
    public void keyReleased(KeyEvent evt) {
        tracker.keyReleased(evt);
    }
    
    public void keyTyped(KeyEvent evt) {
        tracker.keyTyped(evt);
    }
    
    public void mouseClicked(MouseEvent evt) {
        tracker.mouseClicked(evt);
    }
    
    public void mouseDragged(MouseEvent evt) {
        tracker.mouseDragged(evt);
    }
    
    public void mouseEntered(MouseEvent evt) {
        super.mouseEntered(evt);
        tracker.mouseEntered(evt);
    }
    
    public void mouseExited(MouseEvent evt) {
        super.mouseExited(evt);
        tracker.mouseExited(evt);
    }
    
    public void mouseMoved(MouseEvent evt) {
        tracker.mouseMoved(evt);
    }
    
    public void mouseReleased(MouseEvent evt) {
        tracker.mouseReleased(evt);
    }
    public void draw(Graphics2D g) {
        tracker.draw(g);
    }
    
    
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        DrawingView view = getView();
        Handle handle = view.findHandle(anchor);
        Tool newTracker = null;
        if (handle != null) {
            newTracker = createHandleTracker(handle);
        } else {
            Figure figure = view.findFigure(anchor);
            if (figure != null) {
                newTracker = createDragTracker(figure);
            } else {
                if (! evt.isShiftDown()) {
                    view.clearSelection();
                    view.setHandleDetailLevel(0);
                }
                newTracker = createAreaTracker();
            }
        }
        
        if (newTracker != null) {
            setTracker(newTracker);
        }
        tracker.mousePressed(evt);
    }
    
    protected void setTracker(Tool newTracker) {
        if (tracker != null) {
            tracker.deactivate(getEditor());
            tracker.removeToolListener(this);
        }
        tracker = newTracker;
        if (tracker != null) {
            tracker.activate(getEditor());
            tracker.addToolListener(this);
        }
    }
    
    /**
     * Factory method to create a Handle tracker. It is used to track a handle.
     */
    protected Tool createHandleTracker(Handle handle) {
        return new HandleTracker(handle, getView().getCompatibleHandles(handle));
    }
    
    /**
     * Factory method to create a Drag tracker. It is used to drag a figure.
     */
    protected Tool createDragTracker(Figure f) {
        return new DragTracker(f);
    }
    
    /**
     * Factory method to create an area tracker. It is used to select an
     * area.
     */
    protected Tool createAreaTracker() {
        return new SelectAreaTracker();
    }
    
    public void toolStarted(ToolEvent event) {
        
    }
    public void toolDone(ToolEvent event) {
        // Empty
        Tool newTracker = createAreaTracker();
        
        if (newTracker != null) {
            if (tracker != null) {
                tracker.deactivate(getEditor());
                tracker.removeToolListener(this);
            }
            tracker = newTracker;
            tracker.activate(getEditor());
            tracker.addToolListener(this);
        }
        fireToolDone();
    }
    /**
     * Sent when an area of the drawing view needs to be repainted.
     */
    public void areaInvalidated(ToolEvent e) {
        fireAreaInvalidated(e.getInvalidatedArea());
    }
}