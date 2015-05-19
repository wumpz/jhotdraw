/*
 * @(#)ChopDiamondConnector.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.draw.connector;

import org.jhotdraw.geom.Geom;
import org.jhotdraw.draw.*;
import java.awt.geom.*;
import static org.jhotdraw.draw.AttributeKeys.*;

/**
 * A {@link Connector} which locates a connection point at the bounds of any figure which has a
 * diamond shape, such as {@link DiamondFigure}.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ChopDiamondConnector extends ChopRectangleConnector {

    private static final long serialVersionUID = 1L;

    public ChopDiamondConnector() {
        // only used for Storable implementation
    }

    public ChopDiamondConnector(Figure owner) {
        super(owner);
    }

    /**
     * Return an appropriate connection point on the edge of a diamond figure
     */
    @Override
    protected Point2D.Double chop(Figure target, Point2D.Double from) {
        target = getConnectorTarget(target);
        Rectangle2D.Double r = target.getBounds();

        if (target.get(DiamondFigure.IS_QUADRATIC)) {
            double side = Math.max(r.width, r.height);
            r.x -= (side - r.width) / 2;
            r.y -= (side - r.height) / 2;
            r.width = r.height = side;
        }
        double growx;
        double growy;
        // FIXME - This code is wrong. Copy correct code from DiamondFigure.
        switch (target.get(STROKE_PLACEMENT)) {
            case INSIDE: {
                growx = growy = 0f;
                break;
            }
            case OUTSIDE: {
                double lineLength = Math.sqrt(r.width * r.width + r.height * r.height);
                double scale = getStrokeTotalWidth(target, 1.0) * 2d / lineLength;
                growx = scale * r.height;
                growy = scale * r.width;
                //growy = getStrokeTotalWidth() * SQRT2;
                break;
            }
            case CENTER:
            default:
                double lineLength = Math.sqrt(r.width * r.width + r.height * r.height);
                double scale = getStrokeTotalWidth(target, 1.0) / lineLength;
                growx = scale * r.height;
                growy = scale * r.width;
                //growx = growy = getStrokeTotalWidth() / 2d * SQRT2;
                break;
        }
        Geom.grow(r, growx, growy);

        // Center point
        Point2D.Double c1 = new Point2D.Double(r.x + r.width / 2, r.y + (r.height / 2));
        Point2D.Double p2 = new Point2D.Double(r.x + r.width / 2, r.y + r.height);
        Point2D.Double p4 = new Point2D.Double(r.x + r.width / 2, r.y);

        // If overlapping, just return the opposite corners
        if (r.contains(from)) {
            if (from.y > r.y && from.y < (r.y + r.height / 2)) {
                return p2;
            } else {
                return p4;
            }
        }

        // Calculate angle to determine quadrant
        double ang = Geom.pointToAngle(r, from);

        // Dermine line points
        Point2D.Double p1 = new Point2D.Double(r.x + r.width, r.y + (r.height / 2));
        Point2D.Double p3 = new Point2D.Double(r.x, r.y + (r.height / 2));
        Point2D.Double rp = null; // This will be returned

        // Get the intersection with edges
        if (ang > 0 && ang < 1.57) {
            rp = Geom.intersect(p1.x, p1.y, p2.x, p2.y, c1.x, c1.y, from.x, from.y);
        } else if (ang > 1.575 && ang < 3.14) {
            rp = Geom.intersect(p2.x, p2.y, p3.x, p3.y, c1.x, c1.y, from.x, from.y);
        } else if (ang > -3.14 && ang < -1.575) {
            rp = Geom.intersect(p3.x, p3.y, p4.x, p4.y, c1.x, c1.y, from.x, from.y);
        } else if (ang > -1.57 && ang < 0) {
            rp = Geom.intersect(p4.x, p4.y, p1.x, p1.y, c1.x, c1.y, from.x, from.y);
        }

        // No proper edge found, we should send one of four corners
        if (rp == null) {
            rp = Geom.angleToPoint(r, ang);
        }

        return rp;
    }
}
