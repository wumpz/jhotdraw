/**
 * @(#)AbstractRotateHandle.java  3.0.1  2007-12-22
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
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
import org.jhotdraw.geom.*;
import org.jhotdraw.util.*;

/**
 * AbstractRotateHandle.
 *
 * @author Werner Randelshofer
 * @version 3.0.1 2007-12-22 Werner Randelshofer: Fixed computation of current
 * theta. 
 * <br>3.0 2007-11-28 Huw Jones: Split up into an AbstractRotateHandle class
 * and a concrete default RotateHandle class.
 * <br>2.0 2007-04-14 Werner Randelshofer: Added support for AttributeKeys.TRANSFORM.
 * <br>1.0 2006-06-12 Werner Randelshofer: Created.
 */
public abstract class AbstractRotateHandle extends AbstractHandle {
    private Point location;
    private Object restoreData;
    private AffineTransform transform;
    private Point2D.Double center;
    private double startTheta;
    private double startLength;
    
    /** Creates a new instance. */
    public AbstractRotateHandle(Figure owner) {
        super(owner);
    }
    
    @Override
    public boolean isCombinableWith(Handle h) {
        return false;
    }
    
    @Override
    public String getToolTipText(Point p) {
    	ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
    	return labels.getString("rotateHandle.tip");
    }
    
    /**
     * Draws this handle.
     */
    @Override
    public void draw(Graphics2D g) {
        drawDiamond(g, Color.green, Color.black);
    }
    
    @Override
    protected Rectangle basicGetBounds() {
        Rectangle r = new Rectangle(getLocation());
        r.grow(getHandlesize() / 2, getHandlesize() / 2);
        return r;
    }

    public Point getLocation() {
        if (location == null) {
            return view.drawingToView(getOrigin());
        }
        return location;
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

    protected Object getRestoreData() {
    	return restoreData;
    }
    
    protected double getStartTheta() {
    	return startTheta;
    }
    
    protected abstract Point2D.Double getOrigin();
    
    protected abstract Point2D.Double getCenter();

    public void trackStart(Point anchor, int modifiersEx) {
        location = new Point(anchor.x, anchor.y);
        restoreData = getOwner().getTransformRestoreData();
        transform = new AffineTransform();
        center = getCenter();
        Point2D.Double anchorPoint = view.viewToDrawing(anchor);
        startTheta = Geom.angle(center.x, center.y, anchorPoint.x, anchorPoint.y);
        startLength = Geom.length(center.x, center.y, anchorPoint.x, anchorPoint.y);
    }
    
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        location = new Point(lead.x, lead.y);
        Point2D.Double leadPoint = view.viewToDrawing(lead);
        double stepTheta = Geom.angle(center.x, center.y, leadPoint.x, leadPoint.y);
        double stepLength = Geom.length(center.x, center.y, leadPoint.x, leadPoint.y);
        
        double currentTheta = view.getConstrainer().constrainAngle(stepTheta - startTheta);
        
        transform.setToIdentity();
        transform.translate(center.x, center.y);
        transform.rotate(currentTheta);
        transform.translate(-center.x, -center.y);
        
        getOwner().willChange();
        getOwner().restoreTransformTo(restoreData);
        getOwner().transform(transform);
        getOwner().changed();
    }
    
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        view.getDrawing().fireUndoableEditHappened(
                new RestoreDataEdit(getOwner(), restoreData));
        fireAreaInvalidated(getDrawingArea());
        location = null;
        invalidate();
        fireAreaInvalidated(getDrawingArea());
    }
}
