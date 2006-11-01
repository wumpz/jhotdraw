/*
 * @(#)GrowStroke.java  1.0  June 9, 2006
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

package org.jhotdraw.geom;

import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;

/**
 * GrowStroke can be used to grow/shrink a figure by a specified line width.
 * This only works with closed convex paths having edges in clockwise direction.
 * <p>
 * Note: Although this is a Stroke object, it does not actually create a stroked
 * shape, but one that can be used for filling. 
 * 
 * @author Werner Randelshofer.
 * @version 1.0 June 9, 2006 Created.
 */
public class GrowStroke extends DoubleStroke {
    private float grow;
    
    public GrowStroke(float grow, float miterLimit) {
        super(grow * 2, 1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, miterLimit, null, 0f);
   this.grow = grow;
    }
    
    public Shape createStrokedShape(Shape s) {
    BezierPath bp = new BezierPath();
        GeneralPath left = new GeneralPath();
        GeneralPath right = new GeneralPath();
        
        double[] coords = new double[6];
        // FIXME - We only do a flattened path
        for (PathIterator i = s.getPathIterator(null, 0.1d); ! i.isDone(); i.next()) {
            int type = i.currentSegment(coords);
            
            switch (type) {
                case PathIterator.SEG_MOVETO :
                    if (bp.size() != 0) {
                        traceStroke(bp, left, right);
                    }
                    bp.clear();
                    bp.moveTo(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_LINETO :
                    if (coords[0] != bp.get(bp.size() - 1).x[0] ||
                            coords[1] != bp.get(bp.size() - 1).y[0]) {
                        bp.lineTo(coords[0], coords[1]);
                    }
                    break;
                case PathIterator.SEG_QUADTO :
                    bp.quadTo(coords[0], coords[1], coords[2], coords[3]);
                    break;
                case PathIterator.SEG_CUBICTO :
                    bp.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    break;
                case PathIterator.SEG_CLOSE:
                    bp.setClosed(true);
                    break;
            }
        }
        if (bp.size() > 1) {
            traceStroke(bp, left, right);
        }
        
        
        if (left.getBounds2D().contains(right.getBounds2D())) {
            return (grow > 0) ? left : right;
        } else {
            return (grow > 0) ? right : left;
        }
    }
    
}