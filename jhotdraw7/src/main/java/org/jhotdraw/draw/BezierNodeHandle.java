/*
 * @(#)BezierNodeHandle.java  1.0.1  2006-04-21
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

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.jhotdraw.util.*;
import org.jhotdraw.undo.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.geom.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;
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
    private final static Color HANDLE_FILL_COLOR = new Color(0x00a8ff); 
    private final static Color HANDLE_STROKE_COLOR = Color.WHITE;
    protected int index;
    private CompositeEdit edit;
    private BezierPath.Node oldNode;
    private Figure transformOwner;
    
    /** Creates a new instance. */
    public BezierNodeHandle(BezierFigure owner, int index) {
        this(owner, index, owner);
    }
    public BezierNodeHandle(BezierFigure owner, int index, Figure transformOwner) {
        super(owner);
        this.index = index;
        this.transformOwner = transformOwner;
        transformOwner.addFigureListener(this);
    }
    public void dispose() {
        super.dispose();
        transformOwner.removeFigureListener(this);
        transformOwner = null;
    }
    
    /**
     * Draws this handle.
     */
    public void draw(Graphics2D g) {
        BezierFigure f = getOwner();
        int size = f.getNodeCount();
        boolean isClosed = f.isClosed();
        if (size > index) {
            BezierPath.Node v = f.getNode(index);
            if (v.mask == 0 ||
                    index == 0 && v.mask == BezierPath.C1_MASK && ! isClosed ||
                    index == size - 1 && v.mask == BezierPath.C2_MASK && ! isClosed
                    ) {
                drawRectangle(g, HANDLE_FILL_COLOR, HANDLE_STROKE_COLOR);
            } else if (v.mask == BezierPath.C1_MASK ||
                    v.mask == BezierPath.C2_MASK ||
                    index == 0 && ! isClosed ||
                    index == size - 1 && ! isClosed) {
                drawDiamond(g, HANDLE_FILL_COLOR, HANDLE_STROKE_COLOR);
            } else {
                drawCircle(g, HANDLE_FILL_COLOR, HANDLE_STROKE_COLOR);
            }
        }
    }
    
    public BezierFigure getOwner() {
        return (BezierFigure) super.getOwner();
    }
    
    protected Point getLocation() {
        if (getOwner().getNodeCount() > index) {
            Point2D.Double p = getOwner().getPoint(index, 0);
            if (TRANSFORM.get(getTransformOwner()) != null) {
                TRANSFORM.get(getTransformOwner()).transform(p, p);
            }
            return view.drawingToView(p);
        } else {
            return new Point(10,10);
        }
    }
    protected BezierPath.Node getBezierNode() {
        return getOwner().getNodeCount() > index ?
            getOwner().getNode(index) :
            null;
    }
    
    protected Rectangle basicGetBounds() {
        Rectangle r = new Rectangle(getLocation());
        r.grow(getHandlesize() / 2, getHandlesize() / 2);
        return r;
    }
    
    protected Figure getTransformOwner() {
        return transformOwner;
    }
    
    public void trackStart(Point anchor, int modifiersEx) {
        BezierFigure figure = getOwner();
        view.getDrawing().fireUndoableEditHappened(edit = new CompositeEdit("Punkt verschieben"));
        Point2D.Double location = view.getConstrainer().constrainPoint(view.viewToDrawing(getLocation()));
        Point2D.Double p = view.getConstrainer().constrainPoint(view.viewToDrawing(anchor));
        oldNode = figure.getNode(index);
        fireHandleRequestSecondaryHandles();
    }
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        BezierFigure figure = getOwner();
        figure.willChange();
        Point2D.Double p = view.getConstrainer().constrainPoint(view.viewToDrawing(lead));
        
        if (TRANSFORM.get(getTransformOwner()) != null) {
            try {
                TRANSFORM.get(getTransformOwner()).inverseTransform(p, p);
            } catch (NoninvertibleTransformException ex) {
                ex.printStackTrace();
            }
        }
        
        BezierPath.Node n = figure.getNode(index);
        //fireAreaInvalidated(n);
        n.moveTo(p);
        //fireAreaInvalidated(n);
        figure.setNode(index, n);
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
        BezierFigure f = getOwner();
        
        // Change node type
        if ((modifiersEx & (InputEvent.META_DOWN_MASK | InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK  | InputEvent.SHIFT_DOWN_MASK)) != 0 &&
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
            f.setNode(index, v);
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
                    that.getOwner().getNodeCount() ==
                    this.getOwner().getNodeCount();
        }*/
        return false;
    }
    
    public void trackDoubleClick(Point p, int modifiersEx) {
        final BezierFigure f = getOwner();
        if (f.getNodeCount() > 2 &&
                (modifiersEx &
                (InputEvent.META_DOWN_MASK | InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK)) == 0
                
                ) {
            Rectangle invalidatedArea = getDrawingArea();
            f.willChange();
            final BezierPath.Node removedNode = f.removeNode(index);
            f.changed();
            fireHandleRequestRemove(invalidatedArea);
            fireUndoableEditHappened(new AbstractUndoableEdit() {
                public String getPresentationName() {
                    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
                    return labels.getString("bezierPath.joinSegment");
                }
                public void redo() throws CannotRedoException {
                    super.redo();
                    view.removeFromSelection(f);
                    f.willChange();
                    f.removeNode(index);
                    f.changed();
                    view.addToSelection(f);
                }
                
                public void undo() throws CannotUndoException {
                    super.undo();
                    view.removeFromSelection(f);
                    f.willChange();
                    f.addNode(index, removedNode);
                    f.changed();
                    view.addToSelection(f);
                }
                
            });
        }
    }
    
    public Collection<Handle> createSecondaryHandles() {
        BezierFigure f = getOwner();
        LinkedList<Handle> list = new LinkedList<Handle>();
        BezierPath.Node v = f.getNode(index);
        if ((v.mask & BezierPath.C1_MASK) != 0 &&
                (index != 0 || f.isClosed())) {
            list.add(new BezierControlPointHandle(f, index, 1, getTransformOwner()));
        }
        if ((v.mask & BezierPath.C2_MASK) != 0 &&
                (index < f.getNodeCount() - 1 ||
                f.isClosed())) {
            list.add(new BezierControlPointHandle(f, index, 2, getTransformOwner()));
        }
        if (index > 0 || f.isClosed()) {
            int i = (index == 0) ? f.getNodeCount() - 1 : index - 1;
            v = f.getNode(i);
            if ((v.mask & BezierPath.C2_MASK) != 0) {
                list.add(new BezierControlPointHandle(f, i, 2, getTransformOwner()));
            }
        }
        if (index < f.getNodeCount() - 1 || f.isClosed()) {
            int i = (index == f.getNodeCount() - 1) ? 0 : index + 1;
            v = f.getNode(i);
            if ((v.mask & BezierPath.C1_MASK) != 0) {
                list.add(new BezierControlPointHandle(f, i, 1, getTransformOwner()));
            }
        }
        return list;
    }
    public String getToolTipText(Point p) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        BezierPath.Node node = getBezierNode();
        return (node == null) ? null : labels.getFormatted("bezierNodeHandle.tip",
                labels.getFormatted(
                (node.getMask() == 0) ?
                    "bezierNode.linearNode" :
                    ((node.getMask() == BezierPath.C1C2_MASK) ?
                        "bezierNode.cubicNode" : "bezierNode.quadraticNode")
                        )
                        );
    }
}
