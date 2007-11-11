/*
 * @(#)GridConstrainer.java  3.1  2007-09-15
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
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
import org.jhotdraw.beans.AbstractBean;
/**
 * Constrains a point such that it falls on a grid.
 *
 * @author  Werner Randelshofer
 * @version 3.1 2007-09-15 Added constructor which allows to control
 * the visiblity of the grid. 
 * <br>3.0 2007-08-01 Reworked.
 * <br>2.1.1 2006-07-05 Fixed drawing bug.
 * <br>2.1 2006-07-03 Method isVisible added.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2004-03-17  Created.
 */
public class GridConstrainer extends AbstractConstrainer {
    private double width, height;
    private boolean isVisible;
    private static Color minorColor = new Color(0xebebeb);
    private static Color majorColor = new Color(0xcacaca);
    private int majorGridSpacing = 5;
    /**
     * Creates a new instance with a grid of 1x1.
     */
    public GridConstrainer() {
        this(1d, 1d, false);
    }
    /**
     * Creates a new instance with the specified grid size.
     * The grid is visible.
     *
     * @param width The width of a grid cell.
     * @param height The height of a grid cell.
     */
    public GridConstrainer(double width, double height) {
        this(width, height, true);
    }
    /**
     * Creates a new instance with the specified grid size.
     * The grid is visible.
     *
     * @param width The width of a grid cell.
     * @param height The height of a grid cell.
     * @param visible Wether the grid is visible or not.
     */
    public GridConstrainer(double width, double height, boolean visible) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("Width or height is <= 0");
        this.width = width;
        this.height = height;
        this.isVisible = visible;
    }
    
    public double getWidth() {
        return width;
    }
    public double getHeight() {
        return height;
    }
    public void setWidth(double newValue) {
        double oldValue = width;
        width = newValue;
        firePropertyChange("width", oldValue, newValue);
        fireStateChanged();
    }
    public void setHeight(double newValue) {
        double oldValue = height;
        height = newValue;
        firePropertyChange("height", oldValue, newValue);
        fireStateChanged();
    }
    
    /**
     * Constrains a point to the closest grid point in any direction.
     */
    public Point2D.Double constrainPoint(Point2D.Double p) {
        // FIXME - This works only for integer widths!
        p.x = Math.round(p.x / width) * width;
        p.y = Math.round(p.y / height) * height;
        return p;
    }
    
    /**
     * Constrains the placement of a point towards a direction.
     * <p>
     * This method changes the point which is passed as a parameter.
     *
     * @param p A point on the drawing.
     * @param dir A direction.
     * @return Returns the constrained point.
     */
    protected Point2D.Double constrainPoint(Point2D.Double p, Direction dir) {
        Point2D.Double p0 = constrainPoint((Point2D.Double) p.clone());
        
        switch (dir) {
            case NORTH :
            case NORTH_WEST :
            case NORTH_EAST :
                if (p0.y < p.y) {
                    p.y = p0.y;
                } else if (p0.y > p.y) {
                    p.y = p0.y - height;
                }
                break;
            case SOUTH :
            case SOUTH_WEST :
            case SOUTH_EAST :
                if (p0.y < p.y) {
                    p.y = p0.y + height;
                } else if (p0.y > p.y) {
                    p.y = p0.y;
                }
                break;
        }
        switch (dir) {
            case WEST :
            case NORTH_WEST :
            case SOUTH_WEST :
                if (p0.x < p.x) {
                    p.x = p0.x;
                } else if (p0.x > p.x) {
                    p.x = p0.x - width;
                }
                break;
            case EAST :
            case NORTH_EAST :
            case SOUTH_EAST :
                if (p0.x < p.x) {
                    p.x = p0.x + width;
                } else if (p0.x > p.x) {
                    p.x = p0.x;
                }
                break;
        }
        
        return p;
    }
    /**
     * Moves a point to the closest grid point in a direction.
     */
    public Point2D.Double movePoint(Point2D.Double p, Direction dir) {
        Point2D.Double p0 = constrainPoint((Point2D.Double) p.clone());
        
        switch (dir) {
            case NORTH :
            case NORTH_WEST :
            case NORTH_EAST :
                p.y = p0.y - height;
                break;
            case SOUTH :
            case SOUTH_WEST :
            case SOUTH_EAST :
                p.y = p0.y + height;
                break;
        }
        switch (dir) {
            case WEST :
            case NORTH_WEST :
            case SOUTH_WEST :
                p.x = p0.x - width;
                break;
            case EAST :
            case NORTH_EAST :
            case SOUTH_EAST :
                p.x = p0.x + width;
                break;
        }
        
        return p;
    }
    
    public Rectangle2D.Double constrainRectangle(Rectangle2D.Double r) {
        Point2D.Double p0 = constrainPoint(new Point2D.Double(r.x, r.y));
        Point2D.Double p1 = constrainPoint(new Point2D.Double(r.x+r.width, r.y+r.height));
        
        if (Math.abs(p0.x - r.x) < Math.abs(p1.x - r.x - r.width)) {
            r.x = p0.x;
        } else {
            r.x = p1.x - r.width;
        }
        if (Math.abs(p0.y - r.y) < Math.abs(p1.y - r.y - r.height)) {
            r.y = p0.y;
        } else {
            r.y = p1.y - r.height;
        }
        
        return r;
    }
    /**
     * Constrains the placement of a rectangle towards a direction.
     * <p>
     * This method changes the location of the rectangle which is passed as a
     * parameter. This method does not change the size of the rectangle.
     *
     * @param r A rectangle on the drawing.
     * @param dir A direction.
     * @return Returns the constrained rectangle.
     */
    protected Rectangle2D.Double constrainRectangle(Rectangle2D.Double r, Direction dir) {
        Point2D.Double p0 = new Point2D.Double(r.x, r.y);
        
        switch (dir) {
            case NORTH :
            case NORTH_WEST :
            case WEST :
                constrainPoint(p0, dir);
                break;
            case EAST :
            case NORTH_EAST :
                p0.x += r.width;
                constrainPoint(p0, dir);
                p0.x -= r.width;
                break;
            case SOUTH :
            case SOUTH_WEST :
                p0.y += r.height;
                constrainPoint(p0, dir);
                p0.y -= r.height;
                break;
            case SOUTH_EAST :
                p0.y += r.height;
                p0.x += r.width;
                constrainPoint(p0, dir);
                p0.y -= r.height;
                p0.x -= r.width;
                break;
        }
        
        r.x = p0.x;
        r.y = p0.y;
        
        return r;
    }
    public Rectangle2D.Double moveRectangle(Rectangle2D.Double r, Direction dir) {
        double x = r.x;
        double y = r.y;
        
        constrainRectangle(r, dir);
        
        switch (dir) {
            case NORTH :
            case NORTH_WEST :
            case NORTH_EAST :
                if (y == r.y) { r.y -= height; }
                break;
            case SOUTH :
            case SOUTH_WEST :
            case SOUTH_EAST :
                if (y == r.y) { r.y += height; }
                break;
        }
        switch (dir) {
            case WEST :
            case NORTH_WEST :
            case SOUTH_WEST :
                if (x == r.x) { r.x -= width; }
                break;
            case EAST :
            case NORTH_EAST :
            case SOUTH_EAST :
                if (x == r.x) { r.x += width; }
                break;
        }
        
        return r;
    }
    public String toString() {
        return super.toString()+"["+width+","+height+"]";
    }
    
    public boolean isVisible() {
        return isVisible;
    }
    public void setVisible(boolean newValue) {
        boolean oldValue = isVisible;
        isVisible = newValue;
        firePropertyChange("visible", oldValue, newValue);
        fireStateChanged();
    }
    
    /**
     * Spacing between major grid lines.
     */
    public int getMajorGridSpacing() {
        return majorGridSpacing;
    }
    /**
     * Spacing between major grid lines.
     */
    public void setMajorGridSpacing(int newValue) {
        int oldValue = majorGridSpacing;
        majorGridSpacing = newValue;
        firePropertyChange("majorGridSpacing", oldValue, newValue);
        fireStateChanged();
    }
    
    public void draw(Graphics2D g, DrawingView view) {
        if (isVisible()) {
            AffineTransform t = view.getDrawingToViewTransform();
            Rectangle viewBounds = g.getClipBounds();
            Rectangle2D.Double bounds = view.viewToDrawing(viewBounds);
            
            Point2D.Double origin = constrainPoint(new Point2D.Double(bounds.x, bounds.y));
            Point2D.Double point = new Point2D.Double();
            Point2D.Double viewPoint = new Point2D.Double();
            
            if (width * view.getScaleFactor() > 2) {
                g.setColor(minorColor);
                for (int i=(int) (origin.x / width), m = (int) ((origin.x + bounds.width) / width) + 1; i <= m; i++) {
                    g.setColor((i % majorGridSpacing == 0) ? majorColor : minorColor);
                    
                    point.x = width * i;
                    t.transform(point, viewPoint);
                    g.drawLine((int) viewPoint.x, (int) viewBounds.y,
                            (int) viewPoint.x, (int) (viewBounds.y + viewBounds.height));
                }
            } else if (width * majorGridSpacing * view.getScaleFactor() > 2) {
                g.setColor(majorColor);
                for (int i=(int) (origin.x / width), m = (int) ((origin.x + bounds.width) / width) + 1; i <= m; i++) {
                    if (i % majorGridSpacing == 0) {
                        point.x = width * i;
                        t.transform(point, viewPoint);
                        g.drawLine((int) viewPoint.x, (int) viewBounds.y,
                                (int) viewPoint.x, (int) (viewBounds.y + viewBounds.height));
                    }
                }
            }
            
            if (height * view.getScaleFactor() > 2) {
                g.setColor(minorColor);
                for (int i=(int) (origin.y / height), m = (int) ((origin.y + bounds.height) / height) + 1; i <= m; i++) {
                    g.setColor((i % majorGridSpacing == 0) ? majorColor : minorColor);
                    
                    point.y = height * i;
                    t.transform(point, viewPoint);
                    g.drawLine((int) viewBounds.x, (int) viewPoint.y,
                            (int) (viewBounds.x + viewBounds.width), (int) viewPoint.y);
                }
            } else if (height * majorGridSpacing * view.getScaleFactor() > 2) {
                g.setColor(majorColor);
                for (int i=(int) (origin.y / height), m = (int) ((origin.y + bounds.height) / height) + 1; i <= m; i++) {
                    if (i % majorGridSpacing == 0) {
                        point.y = height * i;
                        t.transform(point, viewPoint);
                        g.drawLine((int) viewBounds.x, (int) viewPoint.y,
                                (int) (viewBounds.x + viewBounds.width), (int) viewPoint.y);
                    }
                }
            }
        }
    }
}

