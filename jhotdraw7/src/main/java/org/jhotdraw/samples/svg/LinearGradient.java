/*
 * @(#)LinearGradient.java  1.0  December 9, 2006
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

package org.jhotdraw.samples.svg;

import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.draw.*;
import org.apache.batik.ext.awt.*;

/**
 * Represents an SVG LinearGradient.
 *
 * @author Werner Randelshofer
 * @version 1.0 December 9, 2006 Created.
 */
public class LinearGradient implements Gradient {
    private double x1;
    private double y1;
    private double x2;
    private double y2;
    private boolean isRelativeToFigureBounds = true;
    private double[] stopOffsets;
    private Color[] stopColors;
    
    /** Creates a new instance. */
    public LinearGradient() {
    }
    public void setGradientVector(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
    public void setStops(double[] offsets, Color[] colors) {
        this.stopOffsets = offsets;
        this.stopColors = colors;
    }
    public void setRelativeToFigureBounds(boolean b) {
        isRelativeToFigureBounds = b;
    }
    public boolean isRelativeToFigureBounds() {
        return isRelativeToFigureBounds;
    }
    
    public double getX1() {
        return x1;
    }
    public double getY1() {
        return y1;
    }
    public double getX2() {
        return x2;
    }
    public double getY2() {
        return y2;
    }
    public double[] getStopOffsets() {
        return stopOffsets.clone();
    }
    public Color[] getStopColors() {
        return stopColors.clone();
    }
    
    public Paint getPaint(Figure f, double opacity) {
        if (stopColors.length == 0) {
            return new Color(0xa0ff0000,true);
        }
        Point2D.Double p1;
        Point2D.Double p2;
        if (isRelativeToFigureBounds) {
            // XXX This does not work with transformed bounds!
            Rectangle2D.Double bounds = f.getBounds();
            p1 = new Point2D.Double(bounds.x + bounds.width * x1, bounds.y + bounds.height * y1);
            p2 = new Point2D.Double(bounds.x + bounds.width * x2, bounds.y + bounds.height * y2);
            /*
               AffineTransform tx = SVGAttributeKeys.TRANSFORM.get(f);
            if (tx != null) {
                   tx.transform(p1, p1);
                   tx.transform(p2, p2);
            }*/
            
        } else {
            p1 = new Point2D.Double(x1, y1);
            p2 = new Point2D.Double(x2, y2);
            /*
            Rectangle2D.Double bounds = f.getBounds();
            p1 = new Point2D.Double(bounds.x, bounds.y);
            p2 = new Point2D.Double(bounds.x + bounds.width, bounds.y + bounds.height);
       */
        }
        
        float[] fractions = new float[stopColors.length];
        for (int i=0; i < stopColors.length; i++) {
            fractions[i] = (float) stopOffsets[i];
        }
        org.apache.batik.ext.awt.LinearGradientPaint gp = 
               new org.apache.batik.ext.awt.LinearGradientPaint(p1, p2, fractions, stopColors);
        return gp;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("LinearGradient@");
        buf.append(hashCode());
        buf.append('(');
        for (int i=0; i < stopOffsets.length; i++) {
            if (i != 0) buf.append(',');
            buf.append(stopOffsets[i]);
            buf.append('=');
            buf.append(stopColors[i]);
        }
        buf.append(')');
        return buf.toString();
    }
    
}
