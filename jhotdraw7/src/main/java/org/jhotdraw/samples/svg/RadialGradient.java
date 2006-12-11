/*
 * @(#)RadialGradient.java  1.0  December 9, 2006
 *
 * Copyright (c) 2006 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
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
    
    public Paint getPaint(Figure f) {
        if (stopColors.length == 0) {
            return Color.black;
        }
        Point2D.Double cp;
        double rr;
        if (isRelativeToFigureBounds) {
            // XXX This does not work with transformed bounds!
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
        RadialGradientPaint gp = new RadialGradientPaint(cp, (float) rr, fractions, stopColors);
        return gp;
    }
    
}

