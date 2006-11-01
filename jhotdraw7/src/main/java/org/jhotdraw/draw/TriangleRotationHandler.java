/*
 * @(#)TriangleRotationHandler.java  1.0  June 11, 2006
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

import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.geom.*;

/**
 * A Handle to rotate a TriangleFigure
 * Based on RadiusHandle
 *
 * @author Werner Randelshofer.
 *         Original code by Doug Lea  (dl at gee, Sun Mar 2 19:15:28 1997)
 * @version 1.0 June 11, 2006 Created.
 */
public class TriangleRotationHandler extends AbstractHandle {
    private Rectangle centerBox;
    private AttributeKeys.Orientation oldValue;
    private AttributeKeys.Orientation newValue;
    
    /** Creates a new instance. */
    public TriangleRotationHandler(TriangleFigure owner) {
        super(owner);
    }
    
    public boolean isCombinableWith(Handle h) {
        return false;
    }
    
    private Point2D.Double getLocation () {
        Rectangle2D.Double r = getOwner().getBounds();
        Point2D.Double p;
        double offset = getHandlesize();
        switch (ORIENTATION.get(getOwner())) {
            case NORTH :
            default :
                p = new Point2D.Double(r.x + r.width / 2d, r.y + offset);
                break;
            case NORTH_EAST :
                p = new Point2D.Double(r.x + r.width - offset, r.y + offset);
                break;
            case EAST :
                p = new Point2D.Double(r.x + r.width - offset, r.y + r.height / 2d);
                break;
            case SOUTH_EAST :
                p = new Point2D.Double(r.x + r.width - offset, r.y + r.height - offset);
                break;
            case SOUTH :
                p = new Point2D.Double(r.x + r.width / 2d, r.y + r.height - offset);
                break;
            case SOUTH_WEST :
                p = new Point2D.Double(r.x + offset, r.y + r.height - offset);
                break;
            case WEST :
                p = new Point2D.Double(r.x  + offset, r.y + r.height / 2d);
                break;
            case NORTH_WEST :
                p = new Point2D.Double(r.x + offset, r.y + offset);
                break;
        }
        return p;
    }
    
    protected Rectangle basicGetBounds() {
        Point p = view.drawingToView(getLocation());
        Rectangle r = new Rectangle(p);
        r.grow(getHandlesize() / 2, getHandlesize() / 2);
        return r;
    }
    
    public void trackStart(Point anchor, int modifiersEx) {
        oldValue = ORIENTATION.get(getOwner());
        
        centerBox = view.drawingToView(getOwner().getBounds());
        centerBox.grow(centerBox.width / -3, centerBox.height / -3);
    }
    
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        Rectangle leadRect = new Rectangle(lead);
      
        switch (Geom.outcode(centerBox, leadRect)) {
            case Geom.OUT_TOP :
            default :
                newValue = AttributeKeys.Orientation.NORTH;
                break;
            case Geom.OUT_TOP | Geom.OUT_RIGHT :
                newValue = AttributeKeys.Orientation.NORTH_EAST;
                break;
            case Geom.OUT_RIGHT :
                newValue = AttributeKeys.Orientation.EAST;
                break;
            case Geom.OUT_BOTTOM | Geom.OUT_RIGHT :
                newValue = AttributeKeys.Orientation.SOUTH_EAST;
                break;
            case Geom.OUT_BOTTOM :
                newValue = AttributeKeys.Orientation.SOUTH;
                break;
            case Geom.OUT_BOTTOM | Geom.OUT_LEFT :
                newValue = AttributeKeys.Orientation.SOUTH_WEST;
                break;
            case Geom.OUT_LEFT :
                newValue = AttributeKeys.Orientation.WEST;
                break;
            case Geom.OUT_TOP | Geom.OUT_LEFT :
                newValue = AttributeKeys.Orientation.NORTH_WEST;
                break;
        }
        // FIXME - Add undo redo support
                getOwner().willChange();
                ORIENTATION.basicSet(getOwner(), newValue);
                getOwner().changed();
        updateBounds();
    }
    public void draw(Graphics2D g) {
        drawDiamond(g, Color.yellow, Color.black);
    }
    
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        if (newValue != oldValue) {
            fireUndoableEditHappened(
        new AttributeChangeEdit(getOwner(), ORIENTATION, oldValue, newValue)
        );
        }
    }
    
}
