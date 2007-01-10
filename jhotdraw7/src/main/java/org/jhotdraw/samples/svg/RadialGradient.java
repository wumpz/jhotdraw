/*
 * @(#)RadialGradient.java  1.0  December 9, 2006
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
 * Represents an SVG RadialGradient.
 *
 * @author Werner Randelshofer
 * @version 1.0 December 9, 2006 Created.
 */
public class RadialGradient implements Gradient {
    private double cx;
    private double cy;
    private double r;
    private boolean isRelativeToFigureBounds = true;
    private double[] stopOffsets;
    private Color[] stopColors;
    
    
    /** Creates a new instance. */
    public RadialGradient() {
    }
    
    public void setGradientCircle(double cx, double cy, double r) {
        this.cx = cx;
        this.cy = cy;
        this.r = r;
    }
    public void setStops(double[] offsets, Color[] colors) {
        this.stopOffsets = offsets;
        this.stopColors = colors;
    }
    public void setRelativeToFigureBounds(boolean b) {
        isRelativeToFigureBounds = b;
    }
    public void makeRelativeToFigureBounds(Figure f) {
        // XXX - Untested code
        if (! isRelativeToFigureBounds) {
            isRelativeToFigureBounds = true;
            Rectangle2D.Double bounds = f.getBounds();
            cx = (cx - bounds.x) / bounds.width;
            cy = (cy - bounds.y) / bounds.height;
            r = Math.sqrt(bounds.width * bounds.width + bounds.height * bounds.height) / r;
        }
    }
    
    
    public Paint getPaint(Figure f, double opacity) {
        if (stopColors.length == 0) {
            return new Color(0xa0a0a000,true);
        }
        Point2D.Double cp;
        double rr;
        if (isRelativeToFigureBounds) {
            // FIXME This does not work well with transformed bounds!
            Rectangle2D.Double bounds = f.getBounds();
            cp = new Point2D.Double(bounds.x + bounds.width * cx, bounds.y + bounds.height * cy);
            rr = r * Math.sqrt(bounds.width * bounds.width + bounds.height * bounds.height);
        } else {
            cp = new Point2D.Double(cx, cy);
            rr = r;
        }
        float[] fractions = new float[stopColors.length];
        for (int i=0; i < stopColors.length; i++) {
            fractions[i] = (float) stopOffsets[i];
        }
        if (rr <= 0) {
            System.out.println("RadialGradient: radius should be > 0");
            return new Color(0xa0a0aa00,true);
        }
        org.apache.batik.ext.awt.RadialGradientPaint gp =
                new org.apache.batik.ext.awt.RadialGradientPaint(cp, (float) rr, fractions, stopColors);
        return gp;
    }

    public double getCX() {
        return cx;
    }

    public double getCY() {
        return cy;
    }
    public double getR() {
        return r;
    }
    public double[] getStopOffsets() {
        return stopOffsets.clone();
    }
    public Color[] getStopColors() {
        return stopColors.clone();
    }
    public boolean isRelativeToFigureBounds() {
        return isRelativeToFigureBounds;
    }
    
}

