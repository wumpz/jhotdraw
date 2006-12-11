/*
 * @(#)LinearGradient.java  1.0  December 9, 2006
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
    
    public Paint getPaint(Figure f) {
        if (stopColors.length == 0) {
            return Color.black;
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
        }
        float[] fractions = new float[stopColors.length];
        for (int i=0; i < stopColors.length; i++) {
            fractions[i] = (float) stopOffsets[i];
        }
        LinearGradientPaint gp = 
                new LinearGradientPaint(p1, p2, fractions, stopColors);
        return gp;
    }
    
}
