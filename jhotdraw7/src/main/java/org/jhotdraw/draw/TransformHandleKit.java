/*
 * @(#)TransformHandleKit.java  3.0  2007-04-14
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
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

import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import org.jhotdraw.util.ResourceBundleUtil;
/**
 * A set of utility methods to create Handles which transform a Figure by using
 * its <code>transform</code> method.
 * 
 * 
 * @author Werntransformr
 * @version 3.0 2007-04-14 Renamed to TransformHandleKit to differentiate
 * it from the BoundsHandleKit. 
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 * @see Figure#basicTransform
 */
public class TransformHandleKit {
    
    /** Creates a new instance. */
    public TransformHandleKit() {
    }
    
    /**
     * Creates handles for each corner of a
     * figure and adds them to the provided collection.
     */
    static public void addCornerTransformHandles(Figure f, Collection<Handle> handles) {
        handles.add(southEast(f));
        handles.add(southWest(f));
        handles.add(northEast(f));
        handles.add(northWest(f));
    }
    /**
     * Fills the given Vector with handles at each
     * the north, south, east, and west of the figure.
     */
    static public void addEdgeTransformHandles(Figure f, Collection<Handle> handles) {
        handles.add(south(f));
        handles.add(north(f));
        handles.add(east(f));
        handles.add(west(f));
    }
    /**
     * Adds handles for scaling and moving a Figure.
     */
    static public void addScaleMoveTransformHandles(Figure f, Collection<Handle> handles) {
        addCornerTransformHandles(f, handles);
        addEdgeTransformHandles(f, handles);
    }
    /**
     * Adds handles for scaling, moving, rotating and shearing a Figure.
     */
    static public void addTransformHandles(Figure f, Collection<Handle> handles) {
        addCornerTransformHandles(f, handles);
        addEdgeTransformHandles(f, handles);
        handles.add(new RotateHandle(f));
    }
    
    static public Handle south(Figure owner) {
        return new SouthHandle(owner);
    }
    
    static public Handle southEast(Figure owner) {
        return new SouthEastHandle(owner);
    }
    
    static public Handle southWest(Figure owner) {
        return new SouthWestHandle(owner);
    }
    
    static public Handle north(Figure owner) {
        return new NorthHandle(owner);
    }
    
    static public Handle northEast(Figure owner) {
        return new NorthEastHandle(owner);
    }
    
    static public Handle northWest(Figure owner) {
        return new NorthWestHandle(owner);
    }
    
    static public Handle east(Figure owner) {
        return new EastHandle(owner);
    }
    
    static public Handle west(Figure owner) {
        return new WestHandle(owner);
    }
    
    private static class TransformHandle extends LocatorHandle {
        private int dx, dy;
        Object geometry;
        
        TransformHandle(Figure owner, Locator loc) {
            super(owner, loc);
        }
        public String getToolTipText(Point p) {
            ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
            return labels.getString("transformHandle.tip");
        }
        
        protected Rectangle2D.Double getTransformedBounds() {
            Figure owner = getOwner();
            Rectangle2D.Double bounds = owner.getBounds();
            if (AttributeKeys.TRANSFORM.get(owner) != null) {
                Rectangle2D r = AttributeKeys.TRANSFORM.get(owner).
                        createTransformedShape(bounds).getBounds2D();
                bounds.x = r.getX();
                bounds.y = r.getY();
                bounds.width = r.getWidth();
                bounds.height = r.getHeight();
            }
            return bounds;
        }
        
        public void trackStart(Point anchor, int modifiersEx) {
            geometry = getOwner().getTransformRestoreData();
            Point location = getLocation();
            dx = -anchor.x + location.x;
            dy = -anchor.y + location.y;
        }
        public void trackStep(Point anchor, Point lead, int modifiersEx) {
            Point2D.Double p = view.viewToDrawing(new Point(lead.x + dx, lead.y + dy));
            view.getConstrainer().constrainPoint(p);
            
            trackStepNormalized(p);
        }
        public void trackEnd(Point anchor, Point lead, int modifiersEx) {
            fireUndoableEditHappened(
                    new GeometryEdit(getOwner(), geometry, getOwner().getTransformRestoreData())
                    );
            
        }
        protected void trackStepNormalized(Point2D.Double p) {
        }
        protected void transform(Point2D.Double anchor, Point2D.Double lead) {
            Figure f = getOwner();
            f.willChange();
        Rectangle2D.Double oldBounds = getTransformedBounds();
        Rectangle2D.Double newBounds = new Rectangle2D.Double(
                Math.min(anchor.x, lead.x),
                Math.min(anchor.y, lead.y),
                Math.abs(anchor.x - lead.x),
                Math.abs(anchor.y - lead.y)
                );
        double sx = newBounds.width / oldBounds.width;
        double sy = newBounds.height / oldBounds.height;
        
        AffineTransform tx = new AffineTransform();
        tx.translate(-oldBounds.x, -oldBounds.y);
        if (! Double.isNaN(sx) && ! Double.isNaN(sy) &&
                (sx != 1d || sy != 1d) && 
                ! (sx < 0.0001) && ! (sy < 0.0001)) {
            f.transform(tx);
            tx.setToIdentity();
            tx.scale(sx, sy);
            f.transform(tx);
            tx.setToIdentity();
        }
        tx.translate(newBounds.x, newBounds.y);
        f.transform(tx);
            f.changed();
        }
    }
    
    private static class NorthEastHandle extends TransformHandle {
        NorthEastHandle(Figure owner) {
            super(owner, RelativeLocator.northEast());
        }
        protected void trackStepNormalized(Point2D.Double p) {
            Rectangle2D.Double r = getTransformedBounds();
            transform(
                    new Point2D.Double(r.x, Math.min(r.y + r.height - 1, p.y)),
                    new Point2D.Double(Math.max(r.x, p.x), r.y + r.height)
                    );
        }
        public Cursor getCursor() {
            return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
        }
    }
    
    private static class EastHandle extends TransformHandle {
        EastHandle(Figure owner) {
            super(owner, RelativeLocator.east());
        }
        protected void trackStepNormalized(Point2D.Double p) {
            Rectangle2D.Double r = getTransformedBounds();
            transform(
                    new Point2D.Double(r.x, r.y),
                    new Point2D.Double(Math.max(r.x + 1, p.x), r.y + r.height)
                    );
        }
        public Cursor getCursor() {
            return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
        }
    }
    
    private static class NorthHandle extends TransformHandle {
        NorthHandle(Figure owner) {
            super(owner, RelativeLocator.north());
        }
        protected void trackStepNormalized(Point2D.Double p) {
            Rectangle2D.Double r = getTransformedBounds();
            transform(
                    new Point2D.Double(r.x, Math.min(r.y + r.height - 1, p.y)),
                    new Point2D.Double(r.x + r.width, r.y + r.height)
                    );
        }
        public Cursor getCursor() {
            return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
        }
    }
    
    private static class NorthWestHandle extends TransformHandle {
        NorthWestHandle(Figure owner) {
            super(owner, RelativeLocator.northWest());
        }
        protected void trackStepNormalized(Point2D.Double p) {
            Rectangle2D.Double r = getTransformedBounds();
            transform(
                    new Point2D.Double(Math.min(r.x + r.width - 1, p.x), Math.min(r.y + r.height - 1, p.y)),
                    new Point2D.Double(r.x + r.width, r.y + r.height)
                    );
        }
        public Cursor getCursor() {
            return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
        }
    }
    
    private static class SouthEastHandle extends TransformHandle {
        SouthEastHandle(Figure owner) {
            super(owner, RelativeLocator.southEast());
        }
        
        
        protected void trackStepNormalized(Point2D.Double p) {
            Rectangle2D.Double r = getTransformedBounds();
            transform(
                    new Point2D.Double(r.x, r.y),
                    new Point2D.Double(Math.max(r.x + 1, p.x), Math.max(r.y + 1, p.y))
                    );
        }
        
        public Cursor getCursor() {
            return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
        }
    }
    
    private static class SouthHandle extends TransformHandle {
        SouthHandle(Figure owner) {
            super(owner, RelativeLocator.south());
        }
        protected void trackStepNormalized(Point2D.Double p) {
            Rectangle2D.Double r = getTransformedBounds();
            transform(
                    new Point2D.Double(r.x, r.y),
                    new Point2D.Double(r.x + r.width, Math.max(r.y + 1, p.y))
                    );
        }
        public Cursor getCursor() {
            return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
        }
    }
    
    private static class SouthWestHandle extends TransformHandle {
        SouthWestHandle(Figure owner) {
            super(owner, RelativeLocator.southWest());
        }
        protected void trackStepNormalized(Point2D.Double p) {
            Rectangle2D.Double r = getTransformedBounds();
            transform(
                    new Point2D.Double(Math.min(r.x + r.width - 1, p.x), r.y),
                    new Point2D.Double(r.x + r.width, Math.max(r.y + 1, p.y))
                    );
        }
        
        public Cursor getCursor() {
            return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
        }
    }
    
    private static class WestHandle extends TransformHandle {
        WestHandle(Figure owner) {
            super(owner, RelativeLocator.west());
        }
        protected void trackStepNormalized(Point2D.Double p) {
            Rectangle2D.Double r = getTransformedBounds();
            transform(
                    new Point2D.Double(Math.min(r.x + r.width - 1, p.x), r.y),
                    new Point2D.Double(r.x + r.width, r.y + r.height)
                    );
        }
        public Cursor getCursor() {
            return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
        }
    }
}