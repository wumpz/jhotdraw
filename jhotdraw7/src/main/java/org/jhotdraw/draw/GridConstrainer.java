/*
 * @(#)GridConstrainer.java  2.1.1  2006-07-05
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
/**
 * Constrains a point such that it falls on a grid.
 *
 * @author  Werner Randelshofer
 * @version 2.1.1 2006-07-05 Fixed drawing bug.
 * <br>2.1 2006-07-03 Method isVisible added.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2004-03-17  Created.
 */
public class GridConstrainer implements Constrainer {
    private double width, height;
    private static Color minorColor = new Color(0xebebeb);
    private static Color majorColor = new Color(0xcacaca);
    /**
     * Creates a new instance.
     * @param width The width of a grid cell.
     * @param height The height of a grid cell.
     */
    public GridConstrainer(double width, double height) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("Width or height is <= 0");
        this.width = width;
        this.height = height;
    }
    
    public double getWidth() {
        return width;
    }
    public double getHeight() {
        return height;
    }
    
    public Point2D.Double constrainPoint(Point2D.Double p) {
        // FIXME - This works only for integer widths!
        p.x = Math.round(p.x / width) * width;
        p.y = Math.round(p.y / height) * height;
        return p;
    }
    public String toString() {
        return super.toString()+"["+width+","+height+"]";
    }
    
    public boolean isVisible() {
        return (width > 1 && height > 1);
    }
    
    public void draw(Graphics2D g, DrawingView view) {
        if (isVisible()) {
            AffineTransform t = view.getDrawingToViewTransform();
            Rectangle viewBounds = g.getClipBounds();
            Rectangle2D.Double bounds = view.viewToDrawing(viewBounds);
            
            Point2D.Double origin = constrainPoint(new Point2D.Double(bounds.x, bounds.y));
            Point2D.Double point = new Point2D.Double();
            Point2D.Double viewPoint = new Point2D.Double();
            
            if (width * view.getScaleFactor() > 1) {
                
                g.setColor(minorColor);
                for (int i=(int) (origin.x / width), m = (int) ((origin.x + bounds.width) / width) + 1; i <= m; i++) {
                    g.setColor((i % 5 == 0) ? majorColor : minorColor);
                    
                    point.x = width * i;
                    t.transform(point, viewPoint);
                    g.drawLine((int) viewPoint.x, (int) viewBounds.y,
                            (int) viewPoint.x, (int) (viewBounds.y + viewBounds.height));
                }
            }
            if (height * view.getScaleFactor() > 1) {
                g.setColor(minorColor);
                for (int i=(int) (origin.y / height), m = (int) ((origin.y + bounds.height) / height) + 1; i <= m; i++) {
                    g.setColor((i % 5 == 0) ? majorColor : minorColor);
                    
                    point.y = height * i;
                    t.transform(point, viewPoint);
                    g.drawLine((int) viewBounds.x, (int) viewPoint.y,
                            (int) (viewBounds.x + viewBounds.width), (int) viewPoint.y);
                }
            }
        }
    }
}
