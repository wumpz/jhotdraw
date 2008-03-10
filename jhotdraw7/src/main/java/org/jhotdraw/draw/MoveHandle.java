/*
 * @(#)MoveHandle.java  2.1  2008-02-28
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

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
/**
 * A handle that changes the location of the owning figure, if the figure is
 * transformable. 
 *
 * @author Werner Randelshofer
 * @version 2.1 2008-02-28 Only move a figure, if it is transformable. 
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class MoveHandle extends LocatorHandle {
    /**
     * The previously handled x and y coordinates.
     */
    private Point2D.Double oldPoint;
    
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
     * <p>
     * If the figure is transformable, the handle is drawn as a filled rectangle.
     * If the figure is not transformable, the handle is drawn as an unfilled
     * rectangle.
     */
    public void draw(Graphics2D g) {
        drawRectangle(g, getOwner().isTransformable() ? Color.white : null, Color.black);
    }
    /**
     * Returns a cursor for the handle. 
     * 
     * @return Returns a move cursor, if the figure
     * is transformable. Returns a default cursor otherwise. 
     */
    public Cursor getCursor() {
        return Cursor.getPredefinedCursor(
                getOwner().isTransformable() ? Cursor.MOVE_CURSOR : Cursor.DEFAULT_CURSOR
                );
    }
    
    public void trackStart(Point anchor, int modifiersEx) {
        oldPoint = view.getConstrainer().constrainPoint(view.viewToDrawing(anchor));
    }
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        Figure f = getOwner();
        if (f.isTransformable()) {
        Point2D.Double newPoint = view.getConstrainer().constrainPoint(view.viewToDrawing(lead));
        AffineTransform tx = new AffineTransform();
        tx.translate(newPoint.x - oldPoint.x, newPoint.y - oldPoint.y);
        f.willChange();
        f.transform(tx);
        f.changed();
        
        oldPoint = newPoint;
        }
    }
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        if (getOwner().isTransformable()) {
        AffineTransform tx = new AffineTransform();
        tx.translate(lead.x - anchor.x, lead.y - anchor.y);
        fireUndoableEditHappened(
                new TransformEdit(getOwner(),tx)
                );
        }
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
