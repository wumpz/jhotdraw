/*
 * @(#)ChopEllipseConnector.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.draw.connector;

import org.jhotdraw.draw.*;
import java.awt.*;
import java.awt.geom.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.geom.*;
/**
 * A {@link Connector} which locates a connection point at the bounds
 * of any figure which has an elliptic shape, such as {@link org.jhotdraw.draw.EllipseFigure}.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ChopEllipseConnector extends ChopRectangleConnector {
    private static final long serialVersionUID = 1L;
    /** 
     * Only used for DOMStorable input.
     */
    public ChopEllipseConnector() {
    }
    
    public ChopEllipseConnector(Figure owner) {
        super(owner);
    }
    
    private Color getStrokeColor(Figure f) {
        return f.get(STROKE_COLOR);
    }
    private float getStrokeWidth(Figure f) {
        Double w = f.get(STROKE_WIDTH);
        return (w == null) ? 1f : w.floatValue();
    }

    @Override
    protected Point2D.Double chop(Figure target, Point2D.Double from) {
        target =  getConnectorTarget(target);
        Rectangle2D.Double r = target.getBounds();
        if (getStrokeColor(target) != null) {
            double grow;
            switch (target.get(STROKE_PLACEMENT)) {
                case CENTER:
                 default :
                    grow = getStrokeTotalWidth(target) / 2d;
                    break;
                case OUTSIDE :
                    grow = getStrokeTotalWidth(target);
                    break;
                case INSIDE :
                    grow = 0f;
                    break;
            }
            Geom.grow(r, grow, grow);
        }
        double angle = Geom.pointToAngle(r, from);
        return Geom.ovalAngleToPoint(r, angle);
    }
}
