/*
 * @(#)RectangleFigure.java  2.2  2006-03-23
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
import java.io.*;
import java.util.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.geom.Geom;
/**
 * RectangleFigure.
 *
 * @author Werner Randelshofer
 * @version 2.2 2006-03-23 Take stroke size into account in method contains.
 * <br>2.1 2006-03-22 Method getFigureDrawBounds added.
 * <br>2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class RectangleFigure extends AttributedFigure {
    private Rectangle2D.Double rectangle;
    
    /** Creates a new instance. */
    public RectangleFigure() {
        this(0, 0, 0, 0);
    }
    
    public RectangleFigure(double x, double y, double width, double height) {
        rectangle = new Rectangle2D.Double(x, y, width, height);
        /*
        FILL_COLOR.set(this, Color.white);
        STROKE_COLOR.set(this, Color.black);
         */
    }
    
    // DRAWING
    // SHAPE AND BOUNDS
    // ATTRIBUTES
    // EDITING
// CONNECTING
    // COMPOSITE FIGURES
    // CLONING
    // EVENT HANDLING
    public Rectangle2D.Double getBounds() {
        Rectangle2D.Double bounds = (Rectangle2D.Double) rectangle.clone();
        return bounds;
    }
    
    protected void drawFill(Graphics2D g) {
        Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
            double grow = AttributeKeys.getPerpendicularFillGrowth(this);
            Geom.grow(r, grow, grow);
        g.fill(r);
    }
    
    protected void drawStroke(Graphics2D g) {
        Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
        double grow = AttributeKeys.getPerpendicularDrawGrowth(this);
       Geom.grow(r, grow, grow);
       
        g.draw(r);
    }
    
    public Rectangle2D.Double getFigureDrawBounds() {
        Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
        double grow = AttributeKeys.getPerpendicularHitGrowth(this) + 1d;
        Geom.grow(r, grow, grow);
        return r;
    }
    /**
     * Checks if a Point2D.Double is inside the figure.
     */
    public boolean contains(Point2D.Double p) {
        Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
        double grow = AttributeKeys.getPerpendicularHitGrowth(this) + 1d;
        Geom.grow(r, grow, grow);
        return r.contains(p);
    }
    
    public void basicSetBounds(Point2D.Double anchor, Point2D.Double lead) {
        rectangle.x = Math.min(anchor.x, lead.x);
        rectangle.y = Math.min(anchor.y , lead.y);
        rectangle.width = Math.max(0.1, Math.abs(lead.x - anchor.x));
        rectangle.height = Math.max(0.1, Math.abs(lead.y - anchor.y));
    }
    /**
     * Moves the Figure to a new location.
     * @param tx the transformation matrix.
     */
    public void basicTransform(AffineTransform tx) {
        Point2D.Double anchor = getStartPoint();
        Point2D.Double lead = getEndPoint();
        basicSetBounds(
                (Point2D.Double) tx.transform(anchor, anchor),
                (Point2D.Double) tx.transform(lead, lead)
                );
    }
    
    public RectangleFigure clone() {
        RectangleFigure that = (RectangleFigure) super.clone();
        that.rectangle = (Rectangle2D.Double) this.rectangle.clone();
        return that;
    }
    public void restoreTo(Object geometry) {
        Rectangle2D.Double r = (Rectangle2D.Double) geometry;
        rectangle.x = r.x;
        rectangle.y = r.y;
        rectangle.width = r.width;
        rectangle.height = r.height;
    }
    
    public Object getRestoreData() {
        return rectangle.clone();
    }
    
}
