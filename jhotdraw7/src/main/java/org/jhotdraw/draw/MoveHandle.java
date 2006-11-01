/*
 * @(#)MoveHandle.java  2.0  2006-01-14
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
import java.awt.geom.*;
import java.util.*;
/**
 * A handle that changes the location of the owning figure. Its only purpose is
 * to show feedback that a figure is selected.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class MoveHandle extends LocatorHandle {
    /**
     * The previously handled x and y coordinates.
     */
    private Point2D.Double oldPoint;
    
    private Object geometry;
    
    /** Creates a new instance. */
    public MoveHandle(Figure owner, Locator locator) {
        super(owner, locator);
    }
    
    /**
     * Creates handles for each corner of a
     * figure and adds them to the provided collection.
     */
    static public void addMoveHandles(Figure f, Collection<Handle> handles) {
        handles.add(southEast(f));
        handles.add(southWest(f));
        handles.add(northEast(f));
        handles.add(northWest(f));
    }
    /**
     * Draws this handle.
     * Null Handles are drawn as unfilled rectangles.
     */
    public void draw(Graphics2D g) {
        drawRectangle(g, Color.white, Color.black);
    }
    public void trackStart(Point anchor, int modifiersEx) {
        // geometry = owner.getGeometry();
        oldPoint = view.getConstrainer().constrainPoint(view.viewToDrawing(anchor));
    }
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        Figure f = getOwner();
        Point2D.Double newPoint = view.getConstrainer().constrainPoint(view.viewToDrawing(lead));
        AffineTransform tx = new AffineTransform();
        tx.translate(newPoint.x - oldPoint.x, newPoint.y - oldPoint.y);
        f.willChange();
        f.basicTransform(tx);
        f.changed();
        
        oldPoint = newPoint;
    }
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        AffineTransform tx = new AffineTransform();
        tx.translate(lead.x - anchor.x, lead.y - anchor.y);
        fireUndoableEditHappened(
                new TransformEdit(getOwner(),tx)
                );
    }
    
    static public Handle south(Figure owner) {
        return new MoveHandle(owner, RelativeLocator.south());
    }
    
    static public Handle southEast(Figure owner) {
        return new MoveHandle(owner, RelativeLocator.southEast());
    }
    
    static public Handle southWest(Figure owner) {
        return new MoveHandle(owner, RelativeLocator.southWest());
    }
    
    static public Handle north(Figure owner) {
        return new MoveHandle(owner, RelativeLocator.north());
    }
    
    static public Handle northEast(Figure owner) {
        return new MoveHandle(owner, RelativeLocator.northEast());
    }
    
    static public Handle northWest(Figure owner) {
        return new MoveHandle(owner, RelativeLocator.northWest());
    }
    
    static public Handle east(Figure owner) {
        return new MoveHandle(owner, RelativeLocator.east());
    }
    
    static public Handle west(Figure owner) {
        return new MoveHandle(owner, RelativeLocator.west());
    }
}
