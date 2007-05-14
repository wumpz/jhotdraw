/*
 * @(#)RadialGradient.java  1.0.1  2007-04-10
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
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

/**
 * Represents an SVG RadialGradient.
 *
 * @author Werner Randelshofer
 * @version 1.0.1 2007-04-10 Radius for gradients which are relative to figure
 * boudns is computed better.
 * <br>1.0 December 9, 2006 Created.
 */
public class RadialGradient implements Gradient {
    private double cx;
    private double cy;
    private double r;
    private boolean isRelativeToFigureBounds = true;
    private double[] stopOffsets;
    private Color[] stopColors;
    private AffineTransform transform;
    private double[] stopOpacities;
    
    /** Creates a new instance. */
    public RadialGradient() {
    }
    
    public void setGradientCircle(double cx, double cy, double r) {
        this.cx = cx;
        this.cy = cy;
        this.r = r;
    }
    public void setStops(double[] offsets, Color[] colors, double[] stopOpacities) {
        this.stopOffsets = offsets;
        this.stopColors = colors;
        this.stopOpacities = stopOpacities;
    }
    public void setRelativeToFigureBounds(boolean b) {
        isRelativeToFigureBounds = b;
    }
    public void makeRelativeToFigureBounds(Figure f) {
        if (! isRelativeToFigureBounds) {
            isRelativeToFigureBounds = true;
            Rectangle2D.Double bounds = f.getBounds();
            cx = (cx - bounds.x) / bounds.width;
            cy = (cy - bounds.y) / bounds.height;
            r = r / Math.sqrt(bounds.width * bounds.width / 2d + bounds.height * bounds.height / 2d);
        }
    }
    
    
    public Paint getPaint(Figure f, double opacity) {
        if (stopColors.length == 0) {
            return new Color(0xa0a0a000,true);
        }
        Point2D.Double cp;
        double rr;
        if (isRelativeToFigureBounds) {
            Rectangle2D.Double bounds = f.getBounds();
            cp = new Point2D.Double(bounds.x + bounds.width * cx, bounds.y + bounds.height * cy);
            rr = r * Math.sqrt(bounds.width * bounds.width / 2d + bounds.height * bounds.height / 2d);
        } else {
            cp = new Point2D.Double(cx, cy);
            rr = r;
        }
        Color[] colors = new Color[stopColors.length];
        float[] fractions = new float[stopColors.length];
        for (int i=0; i < stopColors.length; i++) {
            fractions[i] = (float) stopOffsets[i];
            colors[i] = new Color(
                    (stopColors[i].getRGB() & 0xffffff) |
                    ((int) (opacity * stopOpacities[i] * 255) << 24),
                    true
                    );
        }
        if (rr <= 0) {
            System.err.println("RadialGradient: radius should be > 0");
            return new Color(0xa0a0aa00,true);
        }
        
        Point2D.Double focus = cp;
        
        org.apache.batik.ext.awt.RadialGradientPaint gp;
        if (transform != null) {
            gp = new org.apache.batik.ext.awt.RadialGradientPaint(
                    cp,
                    (float) rr,
                    focus,
                    fractions,
                    colors,
                    RadialGradientPaint.NO_CYCLE,
                    RadialGradientPaint.SRGB,
                    transform
                    );
        } else {
            gp = new org.apache.batik.ext.awt.RadialGradientPaint(
                    cp,
                    (float) rr,
                    focus,
                    fractions,
                    colors
                    );
        }
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
    public double[] getStopOpacities() {
        return stopOpacities.clone();
    }
    public boolean isRelativeToFigureBounds() {
        return isRelativeToFigureBounds;
    }
    
    public void transform(AffineTransform tx) {
        if (tx != null) {
            if (transform == null) {
                transform = (AffineTransform) tx.clone();
            } else {
                transform.preConcatenate(tx);
            }
        }
    }
    
    public Object clone() {
        try {
            RadialGradient that = (RadialGradient) super.clone();
            that.stopOffsets = this.stopOffsets.clone();
            that.stopColors = this.stopColors.clone();
            that.stopOpacities = this.stopOpacities.clone();
            return that;
        } catch (CloneNotSupportedException ex) {
            InternalError e = new InternalError();
            e.initCause(ex);
            throw e;
        }
    }
}

