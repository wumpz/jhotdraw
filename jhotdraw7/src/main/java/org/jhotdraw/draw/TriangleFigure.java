/*
 * @(#)TriangleFigure.java  1.0  2006-03-27
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

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.geom.*;
/**
 * A triangle with same dimensions as its enclosing rectangle,
 * and apex at any of 8 places
 *
 *
 * @author Werner Randelshofer
 * @version 1.0 2006-03-27 Created.
 */

public class TriangleFigure extends AttributedFigure {
    
    /**
     * The bounds of the triangle figure.
     */
    private Rectangle2D.Double rectangle;
    
    /** Creates a new instance. */
    public TriangleFigure() {
        this(0, 0, 0, 0);
    }
    public TriangleFigure(Orientation direction) {
        this(0, 0, 0, 0, direction);
    }
    
    public TriangleFigure(double x, double y, double width, double height) {
        this(x, y, width, height, Orientation.NORTH);
    }
    public TriangleFigure(double x, double y, double width, double height, Orientation direction) {
        rectangle = new Rectangle2D.Double(x, y, width, height);
        /*
        setFillColor(Color.white);
        setStrokeColor(Color.black);
         */
        ORIENTATION.set(this, direction);
    }
    
    // DRAWING
    // SHAPE AND BOUNDS
    // ATTRIBUTES
    // EDITING
    // CONNECTING
    public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
        return new ChopTriangleConnector(this);
    }
    public Connector findCompatibleConnector(Connector c, boolean isStartConnector) {
        return new ChopTriangleConnector(this);
    }
    /**
     * Returns the Figures connector for the specified location.
     * By default a ChopDiamondConnector is returned.
     * @see ChopDiamondConnector
     *//*
    public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
        return new ChopTriangleConnector(this);
    }
        
    public Connector findCompatibleConnector(Connector c, boolean isStart) {
        return new ChopTriangleConnector(this);
    }*/
    // COMPOSITE FIGURES
    // CLONING
    // EVENT HANDLING
    public Rectangle2D.Double getBounds() {
        Rectangle2D.Double bounds = (Rectangle2D.Double) rectangle.clone();
        return bounds;
    }
    
    protected void drawFill(Graphics2D g) {
        Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
        
        Shape triangle = getBezierPath();
        double grow = AttributeKeys.getPerpendicularFillGrowth(this);
        if (grow != 0d) {
            GrowStroke gs = new GrowStroke((float) grow,
                    (float) (AttributeKeys.getStrokeTotalWidth(this) *
                    STROKE_MITER_LIMIT_FACTOR.get(this))
                    );
            triangle = gs.createStrokedShape(triangle);
        }
        
        g.fill(triangle);
    }
    
    protected void drawStroke(Graphics2D g) {
        Shape triangle = getBezierPath();
        
        double grow = AttributeKeys.getPerpendicularDrawGrowth(this);
        if (grow != 0d) {
            GrowStroke gs = new GrowStroke((float) grow,
                    (float) (AttributeKeys.getStrokeTotalWidth(this) *
                    STROKE_MITER_LIMIT_FACTOR.get(this))
                    );
            triangle = gs.createStrokedShape(triangle);
        }
        
        g.draw(triangle);
    }
    public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = (LinkedList<Handle>) super.createHandles(detailLevel);
        if (detailLevel == 0) {
            handles.add(new TriangleRotationHandler(this));
        }
        return handles;
    }
    
    public BezierPath getBezierPath() {
        Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
        
        BezierPath triangle = new BezierPath();
        switch (ORIENTATION.get(this)) {
            case NORTH :
            default :
                triangle.moveTo((float) (r.x + r.width / 2), (float) r.y);
                triangle.lineTo((float) (r.x + r.width), (float) (r.y + r.height));
                triangle.lineTo((float) r.x, (float) (r.y + r.height));
                break;
            case NORTH_EAST :
                triangle.moveTo((float) (r.x), (float) r.y);
                triangle.lineTo((float) (r.x + r.width), (float) (r.y));
                triangle.lineTo((float) (r.x + r.width), (float) (r.y + r.height));
                break;
            case EAST :
                triangle.moveTo((float) (r.x), (float) (r.y));
                triangle.lineTo((float) (r.x  + r.width), (float) (r.y + r.height / 2d));
                triangle.lineTo((float) r.x, (float) (r.y + r.height));
                break;
            case SOUTH_EAST :
                triangle.moveTo((float) (r.x + r.width), (float) (r.y));
                triangle.lineTo((float) (r.x + r.width), (float) (r.y + r.height));
                triangle.lineTo((float) (r.x), (float) (r.y + r.height));
                break;
            case SOUTH :
                triangle.moveTo((float) (r.x + r.width / 2), (float) (r.y + r.height));
                triangle.lineTo((float) r.x, (float) r.y);
                triangle.lineTo((float) (r.x + r.width), (float) r.y);
                break;
            case SOUTH_WEST :
                triangle.moveTo((float) (r.x + r.width), (float) (r.y + r.height));
                triangle.lineTo((float) (r.x), (float) (r.y + r.height));
                triangle.lineTo((float) (r.x), (float) (r.y));
                break;
            case WEST :
                triangle.moveTo((float) (r.x), (float) (r.y + r.height / 2));
                triangle.lineTo((float) (r.x + r.width), (float) (r.y ));
                triangle.lineTo((float) (r.x + r.width), (float) (r.y + r.height));
                break;
            case NORTH_WEST :
                triangle.moveTo((float) (r.x), (float) (r.y + r.height));
                triangle.lineTo((float) (r.x), (float) (r.y));
                triangle.lineTo((float) (r.x + r.width), (float) (r.y));
                break;
        }
        triangle.setClosed(true);
        return triangle;
    }
    /**
     * Checks if a Point2D.Double is inside the figure.
     */
    public boolean contains(Point2D.Double p) {
        Shape triangle = getBezierPath();
        
        double grow = AttributeKeys.getPerpendicularHitGrowth(this);
        if (grow != 0d) {
            GrowStroke gs = new GrowStroke((float) grow,
                    (float) (AttributeKeys.getStrokeTotalWidth(this) *
                    STROKE_MITER_LIMIT_FACTOR.get(this))
                    );
            triangle =gs.createStrokedShape(triangle);
        }
        return triangle.contains(p);
    }
    public void basicSetBounds(Point2D.Double anchor, Point2D.Double lead) {
        rectangle.x = Math.min(anchor.x, lead.x);
        rectangle.y = Math.min(anchor.y , lead.y);
        rectangle.width = Math.max(0.1, Math.abs(lead.x - anchor.x));
        rectangle.height = Math.max(0.1, Math.abs(lead.y - anchor.y));
    }
    public Rectangle2D.Double getFigureDrawBounds() {
        double totalStrokeWidth = AttributeKeys.getStrokeTotalWidth(this);
        double width = 0d;
        if (STROKE_COLOR.get(this) != null) {
            switch (STROKE_PLACEMENT.get(this)) {
                case INSIDE :
                    width = 0d;
                    break;
                case OUTSIDE :
                    if (STROKE_JOIN.get(this) == BasicStroke.JOIN_MITER) {
                        width = totalStrokeWidth * STROKE_MITER_LIMIT_FACTOR.get(this);
                    } else {
                        width = totalStrokeWidth;
                    }
                    break;
                case CENTER :
                    if (STROKE_JOIN.get(this) == BasicStroke.JOIN_MITER) {
                        width = totalStrokeWidth / 2d * STROKE_MITER_LIMIT_FACTOR.get(this);
                    } else {
                        width = totalStrokeWidth / 2d;
                    }
                    break;
            }
        }
        width++;
        Rectangle2D.Double r = getBounds();
        
        Geom.grow(r, width, width);
        return r;
    }
    public Point2D.Double chop(Point2D.Double p) {
        Shape triangle = getBezierPath();
        
        double grow = AttributeKeys.getPerpendicularHitGrowth(this);
        if (grow != 0d) {
            GrowStroke gs = new GrowStroke((float) grow,
                    (float) (AttributeKeys.getStrokeTotalWidth(this) *
                    STROKE_MITER_LIMIT_FACTOR.get(this))
                    );
            triangle =gs.createStrokedShape(triangle);
        }
        return Geom.chop(triangle, p);
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
    
    public TriangleFigure clone() {
        TriangleFigure that = (TriangleFigure) super.clone();
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
