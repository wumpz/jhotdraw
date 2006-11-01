/*
 * @(#)AbstractLineDecoration.java  2.0  2006-01-14
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
 *
� */

package org.jhotdraw.draw;

import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.util.*;
import static org.jhotdraw.draw.AttributeKeys.*;

/**
 * An standard implementation of a line decoration. It draws a shape which
 * is rotated and moved to the end of the line. The shape is scaled by the
 * stroke width.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public abstract class AbstractLineDecoration implements LineDecoration {
    /**
     * If this is true, the decoration is filled.
     */
    private boolean isFilled;
    /**
     * If this is true, the decoration is stroked.
     */
    private boolean isStroked;
    /**
     * If this is true, the stroke color is used to fill the decoration.
     */
    private boolean isSolid;
    /**
     * Constructs an arrow tip with the given angle and radius.
     */
    public AbstractLineDecoration(boolean isFilled, boolean isStroked, boolean isSolid) {
        this.isFilled = isFilled;
        this.isStroked = isStroked;
        this.isSolid = isSolid;
    }
    
    protected boolean isFilled() {
        return isFilled;
    }
    protected boolean isStroked() {
        return isStroked;
    }
    protected boolean isSolid() {
        return isSolid;
    }
    
    /**
     * Draws the arrow tip in the direction specified by the given two
     * Points.. (template method)
     */
    public void draw(Graphics2D g, Figure f, Point2D.Double p1, Point2D.Double p2) {
        GeneralPath path = getTransformedDecoratorPath(f, p1, p2);
        Color color;
        if (isFilled) {
            if (isSolid) {
                color = STROKE_COLOR.get(f);
            } else {
                color = FILL_COLOR.get(f);
            }
            if (color != null) {
                g.setColor(color);
                g.fill(path);
            }
        }
        if (isStroked) {
            color = STROKE_COLOR.get(f);
            if (color != null) {
                g.setColor(color);
                g.setStroke(AttributeKeys.getStroke(f));
                g.draw(path);
            }
        }
    }
    
    /**
     * Returns the drawing bounds of the decorator.
     */
    public Rectangle2D.Double getDrawBounds(Figure f, Point2D.Double p1, Point2D.Double p2) {
        GeneralPath path = getTransformedDecoratorPath(f, p1, p2);
        Rectangle2D b = path.getBounds2D();
        Rectangle2D.Double bounds = new Rectangle2D.Double(b.getX(), b.getY(), b.getWidth(), b.getHeight());
        
        if (isStroked) {
            double strokeWidth = STROKE_WIDTH.get(f);
            int strokeJoin = STROKE_JOIN.get(f);
            float miterLimit = (float) (STROKE_MITER_LIMIT_FACTOR.get(f) * strokeWidth);
            
            int grow;
            if (strokeJoin == BasicStroke.JOIN_MITER) {
                grow  = (int) (1 + strokeWidth / 2 * miterLimit);
            } else {
                grow  = (int) (1 + strokeWidth / 2);
            }
            Geom.grow(bounds, grow, grow);
        }
        
        return bounds;
    }
    
    public double getDecorationRadius(Figure f) {
        double strokeWidth = STROKE_WIDTH.get(f);
        double scaleFactor;
        if (strokeWidth > 1f) {
            scaleFactor = 1d + (strokeWidth - 1d) / 2d;
        } else {
            scaleFactor = 1d;
        }
        return getDecoratorPathRadius(f) * scaleFactor;
    }
    
    private GeneralPath getTransformedDecoratorPath(Figure f, Point2D.Double p1, Point2D.Double p2) {
        GeneralPath path = getDecoratorPath(f);
        double strokeWidth = STROKE_WIDTH.get(f);
        
        AffineTransform transform = new AffineTransform();
        transform.translate(p1.x, p1.y);
        transform.rotate(Math.atan2(p1.x - p2.x, p2.y - p1.y));
       // transform.rotate(Math.PI / 2);
        if (strokeWidth > 1f) {
            transform.scale(1d + (strokeWidth - 1d) / 2d, 1d + (strokeWidth - 1d) / 2d);
        }
        path.transform(transform);
        
        return path;
    }
    
    protected void setFilled(boolean b) {
        isFilled = b;
    }
    protected void setStroked(boolean b) {
        isStroked = b;
    }
    protected void setSolid(boolean b) {
        isSolid = b;
    }
    
    /**
     * Hook method to calculates the path of the decorator.
     */
    protected abstract GeneralPath getDecoratorPath(Figure f);
    
    /**
     * Hook method to calculates the radius of the decorator path.
     */
    protected abstract double getDecoratorPathRadius(Figure f);
}
