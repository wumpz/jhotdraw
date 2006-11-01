/*
 * @(#)BezierFigure.java 2.1.1  2006-06-08
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
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.undo.*;
import java.io.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
/**
 * A BezierFigure can be used to draw arbitrary shapes using a BezierPath.
 * It can be used to draw an open path or a closed shape.
 *
 * @version 2.1.1 2006-06-08 Fixed caps drawing.
 * <br>2.1 2006-04-21 Improved caps drawing.
 * <br>2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 March 14, 2004.
 * @author Werner Randelshofer
 */
public class BezierFigure extends AttributedFigure {
    public final static AttributeKey<Boolean> CLOSED = new AttributeKey<Boolean>("closed", false);
    /**
     * The BezierPath.
     */
    protected BezierPath path;
    /**
     * The cappedPath BezierPath is derived from variable path.
     * We cache it to increase the drawing speed of the figure.
     */
    private transient BezierPath cappedPath;
    
    
    /** Creates a new instance. */
    public BezierFigure() {
        this(false);
    }
    /** Creates a new instance. */
    public BezierFigure(boolean isClosed) {
        path = new BezierPath();
        CLOSED.set(this, isClosed);
        //path.setClosed(isClosed);
    }
    
    // DRAWING
    // SHAPE AND BOUNDS
    // ATTRIBUTES
    // EDITING
    // CONNECTING
    /**
     * Returns the Figures connector for the specified location.
     * By default a ChopDiamondConnector is returned.
     * @see ChopDiamondConnector
     */
    public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
        return new ChopBezierConnector(this);
    }
    
    public Connector findCompatibleConnector(Connector c, boolean isStart) {
        return new ChopBezierConnector(this);
    }
    // COMPOSITE FIGURES
    // CLONING
    // EVENT HANDLING
    protected void drawStroke(Graphics2D g) {
        if (isClosed()) {
            double grow = AttributeKeys.getPerpendicularDrawGrowth(this);
            if (grow == 0d) {
                g.draw(path);
            } else {
                GrowStroke gs = new GrowStroke((float) grow,
                        (float) (AttributeKeys.getStrokeTotalWidth(this) *
                        STROKE_MITER_LIMIT_FACTOR.get(this))
                        );
                g.draw(gs.createStrokedShape(path));
            }
        } else {
            g.draw(getCappedPath());
        }
        drawCaps(g);
    }
    
    protected void drawCaps(Graphics2D g) {
        if (getPointCount() > 1) {
            if (START_DECORATION.get(this) != null) {
                BezierPath cp = getCappedPath();
                Point2D.Double p1 = path.get(0,0);
                Point2D.Double p2 = cp.get(0,0);
                if (p2.equals(p1)) {
                    p2 = path.get(1,0);
                }
                START_DECORATION.get(this).draw(g, this, p1, p2);
            }
            if (END_DECORATION.get(this) != null) {
                BezierPath cp = getCappedPath();
                Point2D.Double p1 = path.get(path.size()-1,0);
                Point2D.Double p2 = cp.get(path.size()-1,0);
                if (p2.equals(p1)) {
                    p2 = path.get(path.size()-2,0);
                }
                END_DECORATION.get(this).draw(g, this, p1, p2);
            }
        }
    }
    
    protected void drawFill(Graphics2D g) {
        if (isClosed()) {
            double grow = AttributeKeys.getPerpendicularFillGrowth(this);
            if (grow == 0d) {
                g.fill(path);
            } else {
                GrowStroke gs = new GrowStroke((float) grow,
                        (float) (AttributeKeys.getStrokeTotalWidth(this) *
                        STROKE_MITER_LIMIT_FACTOR.get(this))
                        );
                g.fill(gs.createStrokedShape(path));
            }
        }
    }
    
    public boolean contains(Point2D.Double p) {
        if (isClosed()) {
            double grow = AttributeKeys.getPerpendicularHitGrowth(this);
            if (grow == 0d) {
                return path.contains(p);
            } else {
                GrowStroke gs = new GrowStroke((float) grow,
                        (float) (AttributeKeys.getStrokeTotalWidth(this) *
                        STROKE_MITER_LIMIT_FACTOR.get(this))
                        );
                return gs.createStrokedShape(path).contains(p);
            }
        } else {
        double tolerance = Math.max(2f, AttributeKeys.getStrokeTotalWidth(this) / 2);
            if (getCappedPath().outlineContains(p, tolerance)) {
                return true;
            }
            if (START_DECORATION.get(this) != null) {
                BezierPath cp = getCappedPath();
                Point2D.Double p1 = path.get(0,0);
                Point2D.Double p2 = cp.get(0,0);
                // FIXME - Check here, if caps path contains the point
                if (Geom.lineContainsPoint(p1.x,p1.y,p2.x,p2.y, p.x, p.y, tolerance)) {
                    return true;
                }
            }
            if (END_DECORATION.get(this) != null) {
                BezierPath cp = getCappedPath();
                Point2D.Double p1 = path.get(path.size()-1,0);
                Point2D.Double p2 = cp.get(path.size()-1,0);
                // FIXME - Check here, if caps path contains the point
                if (Geom.lineContainsPoint(p1.x,p1.y,p2.x,p2.y, p.x, p.y, tolerance)) {
                    return true;
                }
            }
            return false;
        }
    }
    /**
     * Checks if this figure can be connected. By default
     * filled BezierFigures can be connected.
     */
    public boolean canConnect() {
        return isClosed();
    }
    public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = new LinkedList<Handle>();
        switch (detailLevel) {
            case 0 :
                BoxHandleKit.addBoxHandles(this, handles);
                handles.add(new BezierScaleHandle(this));
                break;
            case 1 :
                for (int i=0, n = path.size(); i < n; i++) {
                    handles.add(new BezierNodeHandle(this, i));
                }
                break;
        }
        return handles;
    }
    
    public Rectangle2D.Double getBounds() {
        Rectangle2D.Double bounds =path.getBounds2DDouble();
        // Make sure, bounds are not empty
        bounds.width = Math.max(1, bounds.width);
        bounds.height = Math.max(1, bounds.height);
        return bounds;
    }
    public Rectangle2D.Double getFigureDrawBounds() {
        Rectangle2D.Double r = super.getFigureDrawBounds();
        
        if (getNodeCount() > 1) {
            if (START_DECORATION.get(this) != null) {
                Point2D.Double p1 = getPoint(0, 0);
                Point2D.Double p2 = getPoint(1, 0);
                r.add(START_DECORATION.get(this).getDrawBounds(this, p1, p2));
            }
            if (END_DECORATION.get(this) != null) {
                Point2D.Double p1 = getPoint(getNodeCount() - 1, 0);
                Point2D.Double p2 = getPoint(getNodeCount() - 2, 0);
                r.add(END_DECORATION.get(this).getDrawBounds(this, p1, p2));
            }
        }
        
        return r;
    }
    
    protected void validate() {
        super.validate();
        path.invalidatePath();
        invalidateCappedPath();
    }
    
    
    
    /**
     * Returns a clone of the bezier path of this figure.
     */
    public BezierPath getBezierPath() {
        return (BezierPath) path.clone();
    }
    
    public Point2D.Double getPointOnPath(float relative, double flatness) {
        return path.getPointOnPath(relative, flatness);
    }
    
    /**
     * Sets the bezier path, without cloning and without firing events.
     */
    public void basicSetBezierPath(BezierPath newValue) {
        this.path = newValue;
        this.setClosed(newValue.isClosed());
    }
    
    public boolean isClosed() {
        return (Boolean) getAttribute(CLOSED);
    }
    public void setClosed(boolean newValue) {
        CLOSED.set(this, newValue);
    }
    public void basicSetAttribute(AttributeKey key, Object newValue) {
        if (key == CLOSED) {
            path.setClosed((Boolean) newValue);
        }
        super.basicSetAttribute(key, newValue);
    }
    
    public void basicSetBounds(Point2D.Double anchor, Point2D.Double lead) {
        basicSetStartPoint(anchor);
        basicSetEndPoint(lead);
        invalidate();
    }
    public void basicTransform(AffineTransform tx) {
        path.transform(tx);
        invalidate();
    }
    public void invalidate() {
        super.invalidate();
        path.invalidatePath();
        invalidateCappedPath();
    }
    protected void invalidateCappedPath() {
        cappedPath = null;
    }
    
    /**
     * Returns a path which is cappedPath at the ends, to prevent
     * it from drawing under the end caps.
     */
    protected BezierPath getCappedPath() {
        if (cappedPath == null) {
            cappedPath = (BezierPath) path.clone();
            if (isClosed()) {
                cappedPath.setClosed(true);
            } else {
                if (cappedPath.size() > 1) {
                    if (START_DECORATION.get(this) != null) {
                        BezierPath.Node p0 = cappedPath.get(0);
                        BezierPath.Node p1 = cappedPath.get(1);
                        Point2D.Double pp;
                        if ((p0.getMask() & BezierPath.C2_MASK) != 0) {
                            pp = p0.getControlPoint(2);
                        } else if ((p1.getMask() & BezierPath.C1_MASK) != 0) {
                            pp = p1.getControlPoint(1);
                        } else {
                            pp = p1.getControlPoint(0);
                        }
                        double radius = START_DECORATION.get(this).getDecorationRadius(this);
                        double lineLength = Geom.length(p0.getControlPoint(0), pp);
                        cappedPath.set(0,0, Geom.cap(pp, p0.getControlPoint(0), - Math.min(radius, lineLength)));
                    }
                    if (END_DECORATION.get(this) != null) {
                        BezierPath.Node p0 = cappedPath.get(cappedPath.size() - 1);
                        BezierPath.Node p1 = cappedPath.get(cappedPath.size() - 2);
                        
                        Point2D.Double pp;
                        if ((p0.getMask() & BezierPath.C1_MASK) != 0) {
                            pp = p0.getControlPoint(1);
                        } else if ((p1.getMask() & BezierPath.C2_MASK) != 0) {
                            pp = p1.getControlPoint(2);
                        } else {
                            pp = p1.getControlPoint(0);
                        }
                        
                        
                        double radius = END_DECORATION.get(this).getDecorationRadius(this);
                        double lineLength = Geom.length(p0.getControlPoint(0), pp);
                        cappedPath.set(cappedPath.size() - 1, 0, Geom.cap(pp, p0.getControlPoint(0), -Math.min(radius, lineLength)));
                    }
                    cappedPath.invalidatePath();
                }
            }
        }
        return cappedPath;
    }
    public void layout() {
    }
    
    /**
     * Adds a control point.
     */
    public void addNode(BezierPath.Node p) {
        addNode(getNodeCount(), p);
    }
    /**
     * Adds a node to the list of points.
     */
    public void addNode(final int index, BezierPath.Node p) {
        final BezierPath.Node newPoint = new BezierPath.Node(p);
        
        willChange();
        basicAddNode(index, newPoint);
        layout();
        changed();
        fireUndoableEditHappened(new AbstractUndoableEdit() {
            public String getPresentationName() { return "Punkt einf\u00fcgen"; }
            public void undo()  throws CannotUndoException {
                super.undo();
                willChange();
                basicRemoveNode(index);
                changed();
            }
            public void redo()  throws CannotUndoException {
                super.redo();
                willChange();
                basicAddNode(index, newPoint);
                changed();
            }
        });
    }
    /**
     * Adds a control point.
     */
    public void basicAddNode(BezierPath.Node p) {
        path.add(p);
        invalidate();
    }
    /**
     * Adds a control point.
     */
    public void basicAddNode(int index, BezierPath.Node p) {
        path.add(index, p);
    }
    /**
     * Sets a control point.
     */
    public void setNode(int index, BezierPath.Node p) {
        willChange();
        BezierPath.Node oldValue = path.get(index);
        BezierPath.Node newValue = new BezierPath.Node(p);
        basicSetNode(index, newValue);
        changed();
        fireUndoableEditHappened(new BezierNodeEdit(this, index, oldValue, newValue));
    }
    /**
     * Sets a control point.
     */
    public void basicSetNode(int index, BezierPath.Node p) {
        path.set(index, p);
    }
    
    /**
     * Gets a control point.
     */
    public BezierPath.Node getNode(int index) {
        return (BezierPath.Node) path.get(index).clone();
    }
    /**
     * Convenience method for getting the point coordinate of
     * the first control point of the specified node.
     */
    public Point2D.Double getPoint(int index) {
        return path.get(index).getControlPoint(0);
    }
    /**
     * Gets the point coordinate of a control point.
     */
    public Point2D.Double getPoint(int index, int coord) {
        return path.get(index).getControlPoint(coord);
    }
    /**
     * Sets the point coordinate of a control point.
     */
    public void setPoint(int index, int coord, Point2D.Double p) {
        willChange();
        basicSetPoint(index, coord, p);
        changed();
        // XXX - Fire undoable Edit event
    }
    /**
     * Sets the point coordinate of control point 0 at the specified node.
     */
    public void basicSetPoint(int index, Point2D.Double p) {
        BezierPath.Node node = path.get(index);
        double dx = p.x - node.x[0];
        double dy = p.y - node.y[0];
        for (int i=0; i < node.x.length; i++) {
            node.x[i] += dx;
            node.y[i] += dy;
        }
        invalidate();
    }
    /**
     * Sets the point coordinate of a control point.
     */
    public void basicSetPoint(int index, int coord, Point2D.Double p) {
        BezierPath.Node cp = new BezierPath.Node(path.get(index));
        cp.setControlPoint(coord, p);
        basicSetNode(index, cp);
    }
    /**
     * Convenience method for setting the point coordinate of the start point.
     */
    public void basicSetStartPoint(Point2D.Double p) {
        basicSetPoint(0, p);
    }
    /**
     * Convenience method for setting the point coordinate of the end point.
     */
    public void basicSetEndPoint(Point2D.Double p) {
        basicSetPoint(getPointCount() - 1, p);
    }
    /**
     * Convenience method for getting the start point.
     */
    public Point2D.Double getStartPoint() {
        return getPoint(0, 0);
    }
    /**
     * Convenience method for getting the end point.
     */
    public Point2D.Double getEndPoint() {
        return getPoint(getNodeCount() - 1, 0);
    }
    /**
     * Finds a control point index.
     * Returns -1 if no control point could be found.
     * FIXME - Move this to BezierPath
     */
    public int findNode(Point2D.Double p) {
        BezierPath tp = path;
        for (int i=0; i < tp.size(); i++) {
            BezierPath.Node p2 = tp.get(i);
            if (p2.x[0] == p.x && p2.y[0] == p.y) {
                return i;
            }
        }
        return -1;
    }
    /**
     * Removes all points.
     */
    public final void removeAllNodes() {
        willChange();
        basicRemoveAllNodes();
        changed();
    }
    /**
     * Removes the Point2D.Double at the specified index.
     */
    public final void removeNode(final int index) {
        final BezierPath.Node oldPoint = new BezierPath.Node(path.get(index));
        willChange();
        basicRemoveNode(index);
        layout();
        changed();
        fireUndoableEditHappened(new AbstractUndoableEdit() {
            public String getPresentationName() { return "Punkt entfernen"; }
            public void undo()  throws CannotUndoException {
                super.undo();
                willChange();
                basicAddNode(index, oldPoint);
                changed();
            }
            public void redo()  throws CannotUndoException {
                super.redo();
                basicRemoveNode(index);
                changed();
            }
        });
    }
    /**
     * Gets the segment of the polyline that is hit by
     * the given Point2D.Double.
     * @return the index of the segment or -1 if no segment was hit.
     *
     * XXX - Move this to BezierPath
     */
    public int findSegment(Point2D.Double find) {
        // Fixme - use path iterator
        
        Point2D.Double p1, p2;
        for (int i = 0, n = getNodeCount() - 1; i < n; i++) {
            p1 = path.get(i, 0);
            p2 = path.get(i+1, 0);
            if (Geom.lineContainsPoint(p1.x, p1.y, p2.x, p2.y, find.x, find.y, 3d)) {
                return i;
            }
        }
        return -1;
    }
    /**
     * Joins two segments into one if the given Point2D.Double hits a node
     * of the polyline.
     * @return true if the two segments were joined.
     *
     * XXX - Move this to BezierPath
     */
    public boolean basicJoinSegments(Point2D.Double join) {
        int i = findSegment(join);
        if (i != -1 && i > 1) {
            removeNode(i);
            return true;
        }
        return false;
    }
    /**
     * Splits the segment at the given Point2D.Double if a segment was hit.
     * @return the index of the segment or -1 if no segment was hit.
     *
     * XXX - Move this to BezierPath
     */
    public int basicSplitSegment(Point2D.Double split) {
        int i = findSegment(split);
        if (i != -1) {
            addNode(i + 1, new BezierPath.Node(split));
        }
        return i+1;
    }
    /**
     * Removes the Point2D.Double at the specified index.
     */
    protected void basicRemoveNode(int index) {
        path.remove(index);
    }
    /**
     * Removes the Point2D.Double at the specified index.
     */
    protected void basicRemoveAllNodes() {
        path.clear();
    }
    /**
     * Gets the node count.
     */
    public int getNodeCount() {
        return path.size();
    }
    /**
     * Gets the point count.
     */
    public int getPointCount() {
        return path.size();
    }
    
    public BezierFigure clone() {
        BezierFigure that = (BezierFigure) super.clone();
        that.path = (BezierPath) this.path.clone();
        that.invalidate();
        return that;
    }
    
    public void restoreTo(Object geometry) {
        path.setTo((BezierPath) geometry);
    }
    
    public Object getRestoreData() {
        return path.clone();
    }
    
    public Point2D.Double chop(Point2D.Double p) {
        if (isClosed()) {
            double grow = AttributeKeys.getPerpendicularHitGrowth(this);
            if (grow == 0d) {
                return path.chop(p);
            } else {
                GrowStroke gs = new GrowStroke((float) grow,
                        (float) (AttributeKeys.getStrokeTotalWidth(this) *
                        STROKE_MITER_LIMIT_FACTOR.get(this))
                        );
                return Geom.chop(gs.createStrokedShape(path), p);
            }
        } else {
            return path.chop(p);
        }
    }
    
    public Point2D.Double getCenter() {
        return path.getCenter();
    }
    public Point2D.Double getOutermostPoint() {
        return path.get(path.indexOfOutermostNode()).getControlPoint(0);
    }
    /**
     * Joins two segments into one if the given Point2D.Double hits a node
     * of the polyline.
     * @return true if the two segments were joined.
     */
    public int basicJoinSegments(Point2D.Double join, float tolerance) {
        return path.joinSegments(join, tolerance);
    }
    /**
     * Splits the segment at the given Point2D.Double if a segment was hit.
     * @return the index of the segment or -1 if no segment was hit.
     */
    public int basicSplitSegment(Point2D.Double split, float tolerance) {
        return path.splitSegment(split, tolerance);
    }
    /**
     * Handles a mouse click.
     */
    public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
        if (evt.getClickCount() == 2 && view.getHandleDetailLevel() == 1) {
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
    
    public void write(DOMOutput out) throws IOException {
        writePoints(out);
        writeAttributes(out);
    }
    protected void writePoints(DOMOutput out) throws IOException {
        out.openElement("points");
        if (isClosed()) {
            out.addAttribute("closed", true);
        }
        for (int i=0, n = getNodeCount(); i < n; i++) {
            BezierPath.Node node = getNode(i);
            out.openElement("p");
            if (node.mask != 0) {
                out.addAttribute("mask", node.mask);
            }
            if (! node.keepColinear) {
                out.addAttribute("colinear", false);
            }
            out.addAttribute("x", node.x[0]);
            out.addAttribute("y", node.y[0]);
            if (node.x[1] != node.x[0] ||
                    node.y[1] != node.y[0]) {
                out.addAttribute("c1x", node.x[1]);
                out.addAttribute("c1y", node.y[1]);
            }
            if (node.x[2] != node.x[0] ||
                    node.y[2] != node.y[0]) {
                out.addAttribute("c2x", node.x[2]);
                out.addAttribute("c2y", node.y[2]);
            }
            out.closeElement();
        }
        out.closeElement();
    }
    
   @Override public void read(DOMInput in) throws IOException {
        readPoints(in);
        readAttributes(in);
    }
    protected void readPoints(DOMInput in) throws IOException {
        path.clear();
        in.openElement("points");
        setClosed(in.getAttribute("closed", false));
        
        for (int i=0, n = in.getElementCount("p"); i < n; i++) {
            in.openElement("p", i);
            BezierPath.Node node = new BezierPath.Node(
                    in.getAttribute("mask", 0),
                    in.getAttribute("x", 0d),
                    in.getAttribute("y", 0d),
                    in.getAttribute("c1x", in.getAttribute("x", 0d)),
                    in.getAttribute("c1y", in.getAttribute("y", 0d)),
                    in.getAttribute("c2x", in.getAttribute("x", 0d)),
                    in.getAttribute("c2y", in.getAttribute("y", 0d))
                    );
            node.keepColinear = in.getAttribute("colinear", true);
            path.add(node);
            path.invalidatePath();
            in.closeElement();
        }
        in.closeElement();
    }
}