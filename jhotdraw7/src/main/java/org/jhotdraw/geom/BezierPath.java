/*
 * @(#)BezierPath.java  1.1  2006-03-22
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

package org.jhotdraw.geom;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
/**
 * BezierPath allows the construction of paths consisting of straight lines,
 * quadratic curves and cubic curves.
 * <p>
 * A BezierPath represents a geometric path constructed by vertices.
 * Each Node has three control points: C0, C1, C2.
 * A mask defines which control points are in use. The path passes through
 * C0. C1 controls the curve going towards C0. C2 controls the curve going
 * away from C0.
 *
 * @author Werner Randelshofer
 * @version 1.1 2006-03-22 Methods moveTo, lineTo and quadTo  added.
 * <br>1.0 January 20, 2006 Created.
 */
public class BezierPath extends ArrayList<BezierPath.Node>
        implements Shape {
    /** Constant for control point C1.
     * */
    public final static int C1_MASK = 1;
    /** Constant for control point C2. */
    public final static int C2_MASK = 2;
    /** Convenience constant for control point C1 and C2. */
    public final static int C1C2_MASK = C1_MASK | C2_MASK;
    
    /**
     * We cache a GeneralPath instance to speed up Shape operations.
     */
    private transient GeneralPath generalPath;
    
    /**
     * We cache the index of the outermost node to speed up method indexOfOutermostNode(); 
     */
    private int outer = -1;
    
    /**
     * If this value is set to true, closes the bezier path.
     */
    private boolean isClosed;
    
    /**
     * Defines a vertex (node) of the bezier path.
     * <p>
     * A vertex consists of three control points: C0, C1 and C2.
     * <ul>
     * <li>The bezier path always passes through C0.</li>
     * <li>C1 is used to control the curve towards C0.
     * </li>
     * <li>C2 is used to control the curve going away from C0.</li>
     * </ul>
     */
    public static class Node implements Cloneable {
        /**
         * This mask is used to describe which control points in addition to
         * C0 are in effect.
         */
        public int mask = 0;
        /** Control point x coordinates. */
        public double[] x = new double[3];
        /** Control point y coordinates. */
        public double[] y = new double[3];
        
        /** This is a hint for editing tools. If this is set to true,
         * the editing tools shall keep all control points on the same
         * line.
         */
        public boolean keepColinear = true;
        
        public Node() {
            mask = 1;
        }
        public Node(Node that) {
            setTo(that);
        }
        public void setTo(Node that) {
            this.mask = that.mask;
            this.keepColinear = that.keepColinear;
            System.arraycopy(that.x, 0, this.x, 0, 3);
            System.arraycopy(that.y, 0, this.y, 0, 3);
        }
        
        public Node(Point2D.Double c0) {
            this.mask = 0;
            x[0] = c0.x;
            y[0] = c0.y;
            x[1] = c0.x;
            y[1] = c0.y;
            x[2] = c0.x;
            y[2] = c0.y;
        }
        public Node(int mask, Point2D.Double c0, Point2D.Double c1, Point2D.Double c2) {
            this.mask = mask;
            x[0] = c0.x;
            y[0] = c0.y;
            x[1] = c1.x;
            y[1] = c1.y;
            x[2] = c2.x;
            y[2] = c2.y;
        }
        public Node(double x0, double y0) {
            this.mask = 0;
            x[0] = x0;
            y[0] = y0;
            x[1] = x0;
            y[1] = y0;
            x[2] = x0;
            y[2] = y0;
        }
        public Node(int mask, double x0, double y0, double x1, double y1, double x2, double y2) {
            this.mask = mask;
            x[0] = x0;
            y[0] = y0;
            x[1] = x1;
            y[1] = y1;
            x[2] = x2;
            y[2] = y2;
        }
        
        public int getMask() {
            return mask;
        }
        public void setMask(int newValue) {
            mask = newValue;
        }
        public void setControlPoint(int index, Point2D.Double p) {
            x[index] = p.x;
            y[index] = p.y;
        }
        public Point2D.Double getControlPoint(int index) {
            return new Point2D.Double(x[index], y[index]);
        }
        public void moveTo(Point2D.Double p) {
            moveBy(p.x - x[0], p.y - y[0]);
        }
        public void moveTo(double x, double y) {
            moveBy(x - this.x[0], y - this.y[0]);
        }
        public void moveBy(double dx, double dy) {
            for (int i=0; i < 3; i++) {
                x[i] += dx;
                y[i] += dy;
            }
        }
        
        public Object clone() {
            try {
                Node that = (Node) super.clone();
                that.x = this.x.clone();
                that.y = this.y.clone();
                return that;
            } catch (CloneNotSupportedException e) {
                InternalError error = new InternalError();
                error.initCause(e);
                throw error;
            }
        }
        
        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append('[');
            for (int i=0; i < 3; i++) {
                if (i != 0) {
                    if ((mask & i) == i) {
                        buf.append(',');
                    } else {
                        continue;
                    }
                }
                
                buf.append('x');
                buf.append(i);
                buf.append('=');
                buf.append(x[i]);
                buf.append(",y");
                buf.append(i);
                buf.append('=');
                buf.append(y[i]);
            }
            buf.append(']');
            return buf.toString();
        }
    }
    
    /** Creates a new instance. */
    public BezierPath() {
    }
    
    /**
     * Convenience method for adding a control point with a single
     * coordinate C0.
     */
    public void add(Point2D.Double c0) {
        add(new Node(0, c0, c0, c0));
    }
    public void addPoint(double x, double y) {
        add(new Node(0, x, y, x, y, x, y));
    }
    /**
     * Convenience method for adding a control point with three
     * coordinates C0, C1 and C2 with a mask.
     */
    public void add(int mask, Point2D.Double c0, Point2D.Double c1, Point2D.Double c2) {
        add(new Node(mask, c0, c1, c2));
    }
    /**
     * Convenience method for changing a single coordinate of a control point.
     */
    public void set(int index, int coord, Point2D.Double p) {
        Node c = get(index);
        c.x[coord] = p.x;
        c.y[coord] = p.y;
    }
    /**
     * Convenience method for getting a single coordinate of a control point.
     */
    public Point2D.Double get(int index, int coord) {
        Node c = get(index);
        return new Point2D.Double(
                c.x[coord],
                c.y[coord]
                );
    }
    
    /**
     * This must be called after the BezierPath has been changed.
     */
    public void invalidatePath() {
        generalPath = null;
        outer = -1;
    }
    
    /**
     * Recomputes the BezierPath, if it is invalid.
     */
    public void validatePath() {
        if (generalPath == null) {
            generalPath = toGeneralPath();
        }
    }
    
    /** Converts the BezierPath into a GeneralPath. */
    public GeneralPath toGeneralPath() {
        GeneralPath gp = new GeneralPath();
        gp.setWindingRule(GeneralPath.WIND_EVEN_ODD);
        if (size() == 0) {
            gp.moveTo(0,0);
            gp.lineTo(0,0 + 1);
        } else if (size() == 1) {
            Node current = get(0);
            gp.moveTo((float) current.x[0], (float) current.y[0]);
            gp.lineTo((float) current.x[0], (float) current.y[0] + 1);
        } else {
            Node previous;
            Node current;
            
            previous = current = get(0);
            gp.moveTo((float) current.x[0], (float) current.y[0]);
            for (int i=1, n = size(); i < n; i++) {
                previous = current;
                current = get(i);
                
                if ((previous.mask & C2_MASK) == 0) {
                    if ((current.mask & C1_MASK) == 0) {
                        gp.lineTo(
                                (float) current.x[0], (float) current.y[0]
                                );
                    } else {
                        gp.quadTo(
                                (float) current.x[1], (float) current.y[1],
                                (float) current.x[0], (float) current.y[0]
                                );
                    }
                } else {
                    if ((current.mask & C1_MASK) == 0) {
                        gp.quadTo(
                                (float) previous.x[2], (float) previous.y[2],
                                (float) current.x[0], (float) current.y[0]
                                );
                    } else {
                        gp.curveTo(
                                (float) previous.x[2], (float) previous.y[2],
                                (float) current.x[1], (float) current.y[1],
                                (float) current.x[0], (float) current.y[0]
                                );
                    }
                }
            }
            if (isClosed) {
                if (size() > 1) {
                    previous = get(size() - 1);
                    current = get(0);
                    
                    if ((previous.mask & C2_MASK) == 0) {
                        if ((current.mask & C1_MASK) == 0) {
                            gp.lineTo(
                                    (float) current.x[0], (float) current.y[0]
                                    );
                        } else {
                            gp.quadTo(
                                    (float) current.x[1], (float) current.y[1],
                                    (float) current.x[0], (float) current.y[0]
                                    );
                        }
                    } else {
                        if ((current.mask & C1_MASK) == 0) {
                            gp.quadTo(
                                    (float) previous.x[2], (float) previous.y[2],
                                    (float) current.x[0], (float) current.y[0]
                                    );
                        } else {
                            gp.curveTo(
                                    (float) previous.x[2], (float) previous.y[2],
                                    (float) current.x[1], (float) current.y[1],
                                    (float) current.x[0], (float) current.y[0]
                                    );
                        }
                    }
                }
                gp.closePath();
            }
        }
        return gp;
    }
    
    public boolean contains(Point2D p) {
        validatePath();
        return generalPath.contains(p);
    };
    
    /**
     * Returns true, if the outline of this bezier path contains the specified
     * point.
     *
     * @param p The point to be tested.
     * @param tolerance The tolerance for the test.
     */
    public boolean outlineContains(Point2D.Double p, double tolerance) {
        validatePath();
        
        PathIterator i = generalPath.getPathIterator(new AffineTransform(), tolerance);
        
        double[] coords = new double[6];
        int type = i.currentSegment(coords);
        double prevX = coords[0];
        double prevY = coords[1];
        i.next();
        while (! i.isDone()) {
            i.currentSegment(coords);
            if (Geom.lineContainsPoint(
                    prevX, prevY, coords[0], coords[1],
                    p.x, p.y, tolerance)
                    ) {
                return true;
            }
            prevX = coords[0];
            prevY = coords[1];
            i.next();
        }
        return false;
    }
    
    public boolean intersects(Rectangle2D r) {
        validatePath();
        return generalPath.intersects(r);
    }
    
    public PathIterator getPathIterator(AffineTransform at) {
        validatePath();
        return generalPath.getPathIterator(at);
    }
    
    public boolean contains(Rectangle2D r) {
        validatePath();
        return generalPath.contains(r);
    }
    
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        validatePath();
        return generalPath.getPathIterator(at, flatness);
    }
    
    public boolean intersects(double x, double y, double w, double h) {
        validatePath();
        return generalPath.intersects(x, y, w, h);
    }
    
    public Rectangle2D getBounds2D() {
        validatePath();
        return generalPath.getBounds2D();
    }
    public Rectangle2D.Double getBounds2DDouble() {
        validatePath();
        Rectangle2D r = generalPath.getBounds2D();
        if (r instanceof Rectangle2D.Double) {
            return (Rectangle2D.Double) r;
        } else {
            return new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        }
    }
    
    public Rectangle getBounds() {
        validatePath();
        return generalPath.getBounds();
    }
    
    public boolean contains(double x, double y, double w, double h) {
        validatePath();
        return generalPath.contains(x, y, w, h);
    }
    
    public boolean contains(double x, double y) {
        validatePath();
        return generalPath.contains(x, y);
    }
    
    public void setClosed(boolean newValue) {
        if (isClosed != newValue) {
            isClosed = newValue;
            invalidatePath();
        }
    }
    public boolean isClosed() {
        return isClosed;
    }
    
    /** Creates a deep copy of the BezierPath. */
    public Object clone() {
        BezierPath that = (BezierPath) super.clone();
        for (int i=0, n = this.size(); i < n; i++) {
            that.set(i, (Node) this.get(i).clone());
        }
        return that;
    }
    
    /**
     * Transforms the BezierPath.
     * @param tx the transformation.
     */
    public void transform(AffineTransform tx) {
        Point2D.Double p = new Point2D.Double();
        for (Node cp : this) {
            for (int i=0; i < 3; i++) {
                p.x = cp.x[i];
                p.y = cp.y[i];
                tx.transform(p, p);
                cp.x[i] = p.x;
                cp.y[i] = p.y;
            }
        }
        invalidatePath();
    }
    
    public void setTo(BezierPath that) {
        while (that.size() < size()) {
            remove(size() - 1);
        }
        for (int i=0, n = size(); i < n; i++) {
            get(i).setTo(that.get(i));
        }
        while (size() < that.size()) {
            add((Node) that.get(size()).clone());
        }
    }
    
    /**
     * Returns the point at the center of the bezier path.
     */
    public Point2D.Double getCenter() {
        double sx = 0;
        double sy = 0;
        for (Node p : this) {
            sx += p.x[0];
            sy += p.y[0];
        }
        
        int n = size();
        return new Point2D.Double(sx / n, sy / n);
    }
    
    /**
     * Returns a point on the edge of the bezier path which crosses the line
     * from the center of the bezier path to the specified point.
     * If no edge crosses the line, the nearest C0 control point is returned.
     */
    public Point2D.Double chop(Point2D.Double p) {
        validatePath();
        return Geom.chop(generalPath, p);
        /*
        Point2D.Double ctr = getCenter();
        
        // Chopped point
        double cx = -1;
        double cy = -1;
        double len = Double.MAX_VALUE;
        
        // Try for points along edge
        validatePath();
        PathIterator i = generalPath.getPathIterator(new AffineTransform(), 1);
        double[] coords = new double[6];
        int type = i.currentSegment(coords);
        double prevX = coords[0];
        double prevY = coords[1];
        i.next();
        for (; ! i.isDone(); i.next()) {
            i.currentSegment(coords);
            Point2D.Double chop = Geom.intersect(
                    prevX, prevY,
                    coords[0], coords[1],
                    p.x, p.y,
                    ctr.x, ctr.y
                    );
            
            if (chop != null) {
                double cl = Geom.length2(chop.x, chop.y, p.x, p.y);
                if (cl < len) {
                    len = cl;
                    cx = chop.x;
                    cy = chop.y;
                }
            }
            
            prevX = coords[0];
            prevY = coords[1];
        }
        
        //
        if (isClosed() && size() > 1) {
            Node first = get(0);
            Node last = get(size() - 1);
            Point2D.Double chop = Geom.intersect(
                    first.x[0], first.y[0],
                    last.x[0], last.y[0],
                    p.x, p.y,
                    ctr.x, ctr.y
                    );
            if (chop != null) {
                double cl = Geom.length2(chop.x, chop.y, p.x, p.y);
                if (cl < len) {
                    len = cl;
                    cx = chop.x;
                    cy = chop.y;
                }
            }
        }
        
        
        // if none found, pick closest vertex
        if (len == Double.MAX_VALUE) {
            for (int j = 0, n = size(); j < n; j++) {
                Node cp = get(j);
                double l = Geom.length2(cp.x[0], cp.y[0], p.x, p.y);
                if (l < len) {
                    len = l;
                    cx = cp.x[0];
                    cy = cp.y[0];
                }
            }
        }
        return new Point2D.Double(cx, cy);
         */
    }
    /**
     * Return the index of the control point that is furthest from the center
     **/
    public int indexOfOutermostNode() {
        if (outer == -1) {
            Point2D.Double ctr = getCenter();
            outer = 0;
            double dist = 0;
            
            for (int i = 0, n = size(); i < n; i++) {
                Node cp = get(i);
                double d = Geom.length2(ctr.x, ctr.y,
                        cp.x[0],
                        cp.y[0]);
                if (d > dist) {
                    dist = d;
                    outer = i;
                }
            }
        }
        return outer;
    }
    
    /**
     * Returns a relative point on the path.
     * Where 0 is the start point of the path and 1 is the end point of the
     * path.
     *
     * @param relative a value between 0 and 1.
     */
    public Point2D.Double getPointOnPath(double relative, double flatness) {
        if (size() == 0) {
            return null;
        } else if (size() == 1) {
            return get(0).getControlPoint(0);
        }
        if (relative <= 0) {
            return get(0).getControlPoint(0);
        } else if (relative >= 1) {
            return get(size() - 1).getControlPoint(0);
        }
        validatePath();
        // Determine the length of the path
        double len = 0;
        PathIterator i = generalPath.getPathIterator(new AffineTransform(), flatness);
        double[] coords = new double[6];
        int type = i.currentSegment(coords);
        double prevX = coords[0];
        double prevY = coords[1];
        i.next();
        for (; ! i.isDone(); i.next()) {
            i.currentSegment(coords);
            len += Geom.length(prevX, prevY, coords[0], coords[1]);
            prevX = coords[0];
            prevY = coords[1];
        }
        
        // Compute the relative point on the path
        double relativeLen = len * relative;
        double pos = 0;
        i = generalPath.getPathIterator(new AffineTransform(), flatness);
        type = i.currentSegment(coords);
        prevX = coords[0];
        prevY = coords[1];
        i.next();
        for (; ! i.isDone(); i.next()) {
            i.currentSegment(coords);
            double segLen = Geom.length(prevX, prevY, coords[0], coords[1]);
            if (pos + segLen >= relativeLen) {
                //if (true) return new Point2D.Double(coords[0], coords[1]);
                // Compute the relative Point2D.Double on the line
                /*
                return new Point2D.Double(
                        prevX * pos / len + coords[0] * (pos + segLen) / len,
                        prevY * pos / len + coords[1] * (pos + segLen) / len
                        );*/
                double factor = (relativeLen - pos) / segLen;
                
                return new Point2D.Double(
                        prevX * (1 - factor) + coords[0] * factor,
                        prevY * (1 - factor) + coords[1] * factor
                        );
            }
            pos += segLen;
            prevX = coords[0];
            prevY = coords[1];
        }
        throw new InternalError("We should never get here");
    }
    /**
     * Gets the segment of the polyline that is hit by
     * the given Point2D.Double.
     * @return the index of the segment or -1 if no segment was hit.
     */
    public int findSegment(Point2D.Double find, float tolerance) {
        // XXX - This works only for straight lines!
        Node v1, v2;
        BezierPath tempPath = new BezierPath();
        Node t1, t2;
        tempPath.add(t1 = new Node());
        tempPath.add(t2 = new Node());
        
        for (int i = 0, n = size()-1; i < n; i++) {
            v1 = get(i);
            v2 = get(i+1);
            if (v1.mask == 0 && v2.mask == 0) {
                if (Geom.lineContainsPoint(v1.x[0], v1.y[0], v2.x[0], v2.y[0], find.x, find.y, tolerance)) {
                    return i;
                }
            } else {
                t1.setTo(v1);
                t2.setTo(v2);
                tempPath.invalidatePath();
                if (tempPath.outlineContains(find, tolerance)) {
                    return i;
                }
            }
        }
        if (isClosed && size() > 1) {
            v1 = get(size() - 1);
            v2 = get(0);
            if (v1.mask == 0 && v2.mask == 0) {
                if (Geom.lineContainsPoint(v1.x[0], v1.y[0], v2.x[0], v2.y[0], find.x, find.y, tolerance)) {
                    return size() - 1;
                }
            } else {
                t1.setTo(v1);
                t2.setTo(v2);
                tempPath.invalidatePath();
                if (tempPath.outlineContains(find, tolerance)) {
                    return size() - 1;
                }
            }
        }
        return -1;
    }
    /**
     * Joins two segments into one if the given Point2D.Double hits a node
     * of the bezier path.
     * @return the index of the joined segment or -1 if no segment was joined.
     */
    public int joinSegments(Point2D.Double join, float tolerance) {
        for (int i=0; i < size(); i++) {
            Node p = get(i);
            if (Geom.length(p.x[0], p.y[0], join.x, join.y) < tolerance) {
                remove(i);
                return i;
            }
        }
        return -1;
    }
    /**
     * Splits the segment at the given Point2D.Double if a segment was hit.
     * @return the index of the segment or -1 if no segment was hit.
     */
    public int splitSegment(Point2D.Double split, float tolerance) {
        int i = findSegment(split, tolerance);
        int nextI = (i + 1) % size();
        if (i != -1) {
            if ((get(i).mask & C2_MASK) == C2_MASK &&
                    (get(nextI).mask & C1_MASK) == 0) {
                // quadto
                add(i + 1, new Node(C2_MASK, split, split, split));
            } else if ((get(i).mask & C2_MASK) == 0 &&
                    (get(nextI).mask & C1_MASK) == C1_MASK) {
                // quadto
                add(i + 1, new Node(C2_MASK, split, split, split));
            } else if ((get(i).mask & C2_MASK) == C2_MASK &&
                    (get(nextI).mask & C1_MASK) == C2_MASK) {
                // cubicto
                add(i + 1, new Node(C1_MASK | C2_MASK, split, split, split));
            } else {
                // lineto
                add(i + 1, new Node(split));
            }
        }
        return i+1;
    }
    public void moveTo(double x1, double y1) {
        if (size() != 0) {
            throw new IllegalPathStateException("moveTo only allowed when empty");
        }
        add(new Node(x1, y1));
    }
    public void lineTo(double x1, double y1) {
        if (size() == 0) {
            throw new IllegalPathStateException("lineTo only allowed when not empty");
        }
        add(new Node(x1, y1));
    }
    public void quadTo(double x1, double y1,
            double x2, double y2) {
        if (size() == 0) {
            throw new IllegalPathStateException("quadTo only allowed when not empty");
        }
        
        add(new Node(C1_MASK, x2, y2, x1, y1, x2, y2));
    }
    public void curveTo(double x1, double y1,
            double x2, double y2,
            double x3, double y3) {
        if (size() == 0) {
            throw new IllegalPathStateException("curveTo only allowed when not empty");
        }
        Node lastPoint = get(size() - 1);
        lastPoint.mask |= C2_MASK;
        lastPoint.x[2] = x1;
        lastPoint.y[2] = y1;
        
        add(new Node(C1_MASK, x3, y3, x2, y2, x3, y3));
    }
    
    public Point2D.Double[] toPolygonArray() {
        Point2D.Double[] points = new Point2D.Double[size()];
        for (int i=0, n = size(); i < n; i++) {
            points[i] = new Point2D.Double(get(i).x[0], get(i).y[0]);
        }
        return points;
    }
    
}