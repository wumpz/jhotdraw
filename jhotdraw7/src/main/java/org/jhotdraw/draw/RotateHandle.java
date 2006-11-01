/*
 * @(#)RotateHandle.java  1.0  12. July 2006
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
 * A Handle to rotate a Figure.
 *
 * @author Werner Randelshofer.
 * @version 1.0 12. July 2006 Created.
 */
public class RotateHandle extends AbstractHandle {
    private Point location;
    private Object restoreData;
    private AffineTransform transform;
    private Point2D.Double center;
    private double startTheta;
    private double startLength;
    
    /** Creates a new instance. */
    public RotateHandle(Figure owner) {
        super(owner);
    }
    
    public boolean isCombinableWith(Handle h) {
        return false;
    }
    
    
    /**
     * Draws this handle.
     */
    public void draw(Graphics2D g) {
        drawDiamond(g, Color.green, Color.black);
    }
    
    protected Rectangle basicGetBounds() {
        Rectangle r = new Rectangle(getLocation());
        r.grow(getHandlesize() / 2, getHandlesize() / 2);
        return r;
    }
    
    public Point getLocation() {
        if (location == null) {
            return  /*location =*/ view.drawingToView(getOrigin());
        }
        return location;
    }
    
    private Point2D.Double getOrigin() {
        // This handle is placed above the figure.
        // We move it up by a handlesizes, so that it won't overlap with
        // the handles from BoxHandleKit.
        Rectangle2D.Double bounds = getOwner().getBounds();
        return new Point2D.Double(bounds.getCenterX(),
                bounds.y - getHandlesize());
    }
    public void trackStart(Point anchor, int modifiersEx) {
        location = new Point(anchor.x, anchor.y);
        restoreData = getOwner().getRestoreData();
        Rectangle2D.Double bounds = getOwner().getBounds();
        transform = new AffineTransform();
        center = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
        Point2D.Double anchorPoint = view.viewToDrawing(anchor);
        startTheta = Geom.angle(center.x, center.y, anchorPoint.x, anchorPoint.y);
        startLength = Geom.length(center.x, center.y, anchorPoint.x, anchorPoint.y);
    }
    
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        location = new Point(lead.x, lead.y);
        Point2D.Double leadPoint = view.viewToDrawing(lead);
        double stepTheta = Geom.angle(center.x, center.y, leadPoint.x, leadPoint.y);
        double stepLength = Geom.length(center.x, center.y, leadPoint.x, leadPoint.y);
        transform.setToIdentity();
        transform.translate(center.x, center.y);
        transform.rotate(stepTheta - startTheta);
        transform.translate(-center.x, -center.y);
        getOwner().willChange();
        getOwner().restoreTo(restoreData);
        getOwner().basicTransform(transform);
        getOwner().changed();
    }
    
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        view.getDrawing().fireUndoableEditHappened(
                new RestoreDataEdit(getOwner(), restoreData));
        location = null;
    }
    
}
