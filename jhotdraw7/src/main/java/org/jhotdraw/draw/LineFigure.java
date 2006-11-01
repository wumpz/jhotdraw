/*
 * @(#)LineFigure.java  2.0  2006-02-27
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

import javax.swing.undo.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.geom.*;
/**
 * LineFigure.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-02-27 Support point editing at handle level 0. 
 * <br>2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class LineFigure extends BezierFigure {
    
    /** Creates a new instance. */
    public LineFigure() {
        basicAddNode(new BezierPath.Node(new Point2D.Double(0,0)));
        basicAddNode(new BezierPath.Node(new Point2D.Double(0,0)));
    }
    
    // DRAWING
    // SHAPE AND BOUNDS
    // ATTRIBUTES
    // EDITING
    public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = new LinkedList<Handle>();
        switch (detailLevel) {
            case 0 :
                for (int i=0, n = path.size(); i < n; i++) {
                    handles.add(new BezierNodeHandle(this, i));
                }
                break;
        }
        return handles;
    }
// CONNECTING
    // COMPOSITE FIGURES
    // CLONING
    // EVENT HANDLING
    public boolean canConnect() {
        return false;
    }
    /**
     * Handles a mouse click.
     */
    public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
        if (evt.getClickCount() == 2 && view.getHandleDetailLevel() == 0) {
            willChange();
            final int index = basicSplitSegment(p, (float) (5f / view.getScaleFactor()));
            if (index != -1) {
                final BezierPath.Node newNode = getNode(index);
                fireUndoableEditHappened(new AbstractUndoableEdit() {
                    public void redo() throws CannotRedoException {
                        super.redo();
                        willChange();
                        basicAddNode(index, newNode);
                        changed();
                    }

                    public void undo() throws CannotUndoException {
                        super.undo();
                        willChange();
                        basicRemoveNode(index);
                        changed();
                    }
                    
                });
                changed();
                return true;
            }
        }
        return false;
    }
}
