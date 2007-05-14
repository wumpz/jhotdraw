/*
 * @(#)Shapes.java  1.0  8. Mai 2007
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
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

/**
 * Shapes.
 *
 * @author Werner Randelshofer
 * @version 1.0 8. Mai 2007 Created.
 */
public class Shapes {
    
    /** Creates a new instance. */
    private Shapes() {
    }
    
    /**
     * Returns true, if the outline of this bezier path contains the specified
     * point.
     *
     * @param p The point to be tested.
     * @param tolerance The tolerance for the test.
     */
    public static boolean outlineContains(Shape shape, Point2D.Double p, double tolerance) {
        PathIterator i = shape.getPathIterator(new AffineTransform(), tolerance);
        if (! i.isDone()) {
            double[] coords = new double[6];
            int type = i.currentSegment(coords);
            double prevX = coords[0];
            double prevY = coords[1];
            i.next();
            while (! i.isDone()) {
                i.currentSegment(coords);
                if (Geom.lineContainsPoint(
                        prevX, prevY, coords[0], coords[1],
                        p.x, p.y, tolerance)
                        ) {
                    return true;
                }
                prevX = coords[0];
                prevY = coords[1];
                i.next();
            }
        }
        return false;
    }
    
}
