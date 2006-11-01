/*
 * @(#)EllipseFigure.java  2.3  2006-06-17
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

import org.jhotdraw.geom.Geom;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.geom.*;
import static org.jhotdraw.draw.AttributeKeys.*;
/**
 * EllipseFigure.
 *
 * @author Werner Randelshofer
 * @version 2.3 2006-06-17 Added method chop(Point2D.Double).
 * <br>2.2 2006-05-19 Support for stroke placement added.
 * <br>2.1 2006-03-22 Method getFigureDrawBounds added.
 * <br>2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class EllipseFigure extends AttributedFigure {
    private Ellipse2D.Double ellipse;
    
    /** Creates a new instance. */
    public EllipseFigure() {
        this(0, 0, 0, 0);
    }
    
    public EllipseFigure(double x, double y, double width, double height) {
        ellipse = new Ellipse2D.Double(x, y, width, height);
        /*
        setFillColor(Color.white);
        setStrokeColor(Color.black);
         */
        setAttributeEnabled(TEXT_COLOR, false);
    }
    
    // DRAWING
    // SHAPE AND BOUNDS
    // ATTRIBUTES
    // EDITING
    // CONNECTING
    public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
        return new ChopEllipseConnector(this);
    }
    public Connector findCompatibleConnector(Connector c, boolean isStartConnector) {
        return new ChopEllipseConnector(this);
    }
    // COMPOSITE FIGURES
    // CLONING
    // EVENT HANDLING
    public Rectangle2D.Double getBounds() {
        return (Rectangle2D.Double) ellipse.getBounds2D();
    }
    public Rectangle2D.Double getFigureDrawBounds() {
        Rectangle2D.Double r = (Rectangle2D.Double) ellipse.getBounds2D();
        double grow = AttributeKeys.getPerpendicularHitGrowth(this);
        Geom.grow(r, grow, grow);
        return r;
    }
    
    protected void drawFill(Graphics2D g) {
        Ellipse2D.Double r = (Ellipse2D.Double) ellipse.clone();
        double grow = AttributeKeys.getPerpendicularFillGrowth(this);
        r.x -= grow;
        r.y -= grow;
        r.width += grow * 2;
        r.height += grow * 2;
        if (r.width > 0 && r.height > 0) {
            g.fill(r);
        }
    }
    
    protected void drawStroke(Graphics2D g) {
        Ellipse2D.Double r = (Ellipse2D.Double) ellipse.clone();
        double grow = AttributeKeys.getPerpendicularDrawGrowth(this);
        r.x -= grow;
        r.y -= grow;
        r.width += grow * 2;
        r.height += grow * 2;
        
        if (r.width > 0 && r.height > 0) {
            g.draw(r);
        }
    }
    
    /**
     * Checks if a Point2D.Double is inside the figure.
     */
    public boolean contains(Point2D.Double p) {
        Ellipse2D.Double r = (Ellipse2D.Double) ellipse.clone();
        double grow = AttributeKeys.getPerpendicularHitGrowth(this);
        r.x -= grow;
        r.y -= grow;
        r.width += grow * 2;
        r.height += grow * 2;
        
        return r.contains(p);
    }
    
    public void basicSetBounds(Point2D.Double anchor, Point2D.Double lead) {
        ellipse.x = Math.min(anchor.x, lead.x);
        ellipse.y = Math.min(anchor.y , lead.y);
        ellipse.width = Math.max(0.1, Math.abs(lead.x - anchor.x));
        ellipse.height = Math.max(0.1, Math.abs(lead.y - anchor.y));
    }
    /**
     * Transforms the figure.
     *
     * @param tx the transformation.
     */
    public void basicTransform(AffineTransform tx) {
        Point2D.Double anchor = getStartPoint();
        Point2D.Double lead = getEndPoint();
        basicSetBounds(
                (Point2D.Double) tx.transform(anchor, anchor),
                (Point2D.Double) tx.transform(lead, lead)
                );
    }
    
    public EllipseFigure clone() {
        EllipseFigure that = (EllipseFigure) super.clone();
        that.ellipse = (Ellipse2D.Double) this.ellipse.clone();
        return that;
    }
    
    public void restoreTo(Object geometry) {
        Ellipse2D.Double r = (Ellipse2D.Double) geometry;
        ellipse.x = r.x;
        ellipse.y = r.y;
        ellipse.width = r.width;
        ellipse.height = r.height;
    }
    
    public Object getRestoreData() {
        return ellipse.clone();
    }
    
}