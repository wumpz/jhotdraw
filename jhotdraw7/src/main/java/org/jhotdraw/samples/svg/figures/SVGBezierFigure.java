/*
 * @(#)SVGBezierFigure.java  1.0  April 14, 2007
 *
 * Copyright (c) 2007 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.samples.svg.figures;

import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.geom.*;

/**
 * SVGBezierFigure is not an actual SVG element, it is used by SVGPathFigure to
 * represent a single BezierPath segment within an SVG path.
 *
 * @author Werner Randelshofer
 * @version 1.0 April 14, 2007 Created.
 */
public class SVGBezierFigure extends BezierFigure {
    
    /** Creates a new instance. */
    public SVGBezierFigure() {
        this(false);
    }
    public SVGBezierFigure(boolean isClosed) {
        super(isClosed);
        FILL_OPEN_PATH.set(this, true);
    }
    
    @Override public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = new LinkedList<Handle>();
        switch (detailLevel % 2) {
            case 0 :
                handles.add(new BezierOutlineHandle(this));
                for (int i=0, n = path.size(); i < n; i++) {
                    handles.add(new BezierNodeHandle(this, i));
                }
                break;
            case 1 :
                TransformHandleKit.addTransformHandles(this, handles);
                break;
            default:
                break;
        }
        return handles;
    }
    @Override public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
        if (evt.getClickCount() == 2/* && view.getHandleDetailLevel() == 0*/) {
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
                evt.consume();
                return true;
            }
        }
        return false;
    }
}
