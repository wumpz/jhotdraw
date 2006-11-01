/*
 * @(#)BezierNodeHandle.java  1.0.1  2006-04-21
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
 */

package org.jhotdraw.draw;

import org.jhotdraw.util.*;
import org.jhotdraw.undo.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.geom.*;
/**
 * BezierNodeHandle.
 *
 *
 * @author Werner Randelshofer
 * @version 1.0.1 2006-04-21 Don't change node type when right mouse button
 * is down.
 * <br>1.0 January 20, 2006 Created.
 */
public class BezierNodeHandle extends AbstractHandle {
    protected int index;
    private CompositeEdit edit;
    private BezierPath.Node oldNode;
    
    /** Creates a new instance. */
    public BezierNodeHandle(BezierFigure owner, int index) {
        super(owner);
        this.index = index;
    }
    /**
     * Draws this handle.
     */
    public void draw(Graphics2D g) {
        BezierFigure f = getBezierFigure();
        if (f.getPointCount() > index) {
            BezierPath.Node v = f.getNode(index);
            if (v.mask == 0) {
                drawRectangle(g, Color.black, Color.white);
            } else if (v.mask == BezierPath.C1_MASK ||
                    v.mask == BezierPath.C2_MASK ||
                    !f.isClosed() &&
                    v.mask == (BezierPath.C1_MASK | BezierPath.C2_MASK) &&
                    index == 0 || index == f.getNodeCount() - 1) {
                drawDiamond(g, Color.black, Color.white);
            } else {
                drawCircle(g, Color.black, Color.white);
            }
        }
    }
    
    protected BezierFigure getBezierFigure() {
        return (BezierFigure) getOwner();
    }
    
    protected Point getLocation() {
        return getBezierFigure().getPointCount() > index ?
            view.drawingToView(getBezierFigure().getPoint(index, 0)) :
            new Point(10,10);
    }
    
    protected Rectangle basicGetBounds() {
        Rectangle r = new Rectangle(getLocation());
        r.grow(getHandlesize() / 2, getHandlesize() / 2);
        return r;
    }
    
    
    
    public void trackStart(Point anchor, int modifiersEx) {
        BezierFigure figure = getBezierFigure();
        view.getDrawing().fireUndoableEditHappened(edit = new CompositeEdit("Punkt verschieben"));
        Point2D.Double location = view.getConstrainer().constrainPoint(view.viewToDrawing(getLocation()));
        Point2D.Double p = view.getConstrainer().constrainPoint(view.viewToDrawing(anchor));
oldNode = figure.getNode(index);
        fireHandleRequestSecondaryHandles();
    }
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        BezierFigure figure = getBezierFigure();
        figure.willChange();
        Point2D.Double p = view.getConstrainer().constrainPoint(view.viewToDrawing(lead));
        BezierPath.Node n = figure.getNode(index);
        fireAreaInvalidated(n);
        n.moveTo(p);
        fireAreaInvalidated(n);
        figure.basicSetNode(index, n);
        figure.changed();
    }
    
    private void fireAreaInvalidated(BezierPath.Node v) {
        Rectangle2D.Double dr = new Rectangle2D.Double(v.x[0], v.y[0], 0, 0);
        for (int i=1; i < 3; i++) {
            dr.add(v.x[i], v.y[i]);
        }
        Rectangle vr = view.drawingToView(dr);
        vr.grow(getHandlesize(), getHandlesize());
        fireAreaInvalidated(vr);
    }
    
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        BezierFigure f = getBezierFigure();
        
        // Change node type
        if ((modifiersEx & (InputEvent.META_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)) != 0 &&
                (modifiersEx & InputEvent.BUTTON2_DOWN_MASK) == 0) {
            f.willChange();
            BezierPath.Node v = f.getNode(index);
            if (index > 0 && index < f.getNodeCount() || f.isClosed()) {
                v.mask = (v.mask + 3) % 4;
            } else if (index == 0) {
                v.mask = ((v.mask & BezierPath.C2_MASK) == 0) ? BezierPath.C2_MASK : 0;
            } else {
                v.mask = ((v.mask & BezierPath.C1_MASK) == 0) ? BezierPath.C1_MASK : 0;
            }
            f.basicSetNode(index, v);
            f.changed();
            fireHandleRequestSecondaryHandles();
        }
        view.getDrawing().fireUndoableEditHappened(new BezierNodeEdit(f,index,oldNode,f.getNode(index)));
        view.getDrawing().fireUndoableEditHappened(edit);
    }
   @Override public boolean isCombinableWith(Handle h) {
       /*
        if (super.isCombinableWith(h)) {
            BezierNodeHandle that = (BezierNodeHandle) h;
            return that.index == this.index &&
                    that.getBezierFigure().getNodeCount() ==
                    this.getBezierFigure().getNodeCount();
        }*/
        return false;
    }
    
    public void trackDoubleClick(Point p, int modifiersEx) {
        BezierFigure f = getBezierFigure();
        if (f.getNodeCount() > 2 &&
                (modifiersEx &
                (InputEvent.META_DOWN_MASK | InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK)) == 0
                
                ) {
            Rectangle invalidatedArea = getDrawBounds();
            f.willChange();
            f.basicRemoveNode(index);
            f.changed();
            fireHandleRequestRemove(invalidatedArea);
        }
    }
    
    public Collection<Handle> createSecondaryHandles() {
        BezierFigure f = getBezierFigure();
        LinkedList<Handle> list = new LinkedList<Handle>();
        BezierPath.Node v = f.getNode(index);
        if ((v.mask & BezierPath.C1_MASK) != 0 &&
                (index != 0 || f.isClosed())) {
            list.add(new BezierControlPointHandle(f, index, 1));
        }
        if ((v.mask & BezierPath.C2_MASK) != 0 &&
                (index < f.getNodeCount() - 1 ||
                f.isClosed())) {
            list.add(new BezierControlPointHandle(f, index, 2));
        }
        if (index > 0 || f.isClosed()) {
            int i = (index == 0) ? f.getNodeCount() - 1 : index - 1;
            v = f.getNode(i);
            if ((v.mask & BezierPath.C2_MASK) != 0) {
                list.add(new BezierControlPointHandle(f, i, 2));
            }
        }
        if (index < f.getNodeCount() - 2 || f.isClosed()) {
            int i = (index == f.getNodeCount() - 1) ? 0 : index + 1;
            v = f.getNode(i);
            if ((v.mask & BezierPath.C1_MASK) != 0) {
                list.add(new BezierControlPointHandle(f, i, 1));
            }
        }
        return list;
    }
}
