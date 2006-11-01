/*
 * @(#)ChopBoxConnector.java  2.1  2006-03-22
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
�
 */

package org.jhotdraw.draw;

import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.geom.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.geom.*;
/**
 * A ChopBoxConnector locates connection points by
 * choping the connection between the centers of the
 * two figures at the display box.
 * <p>
 * XXX - Replace all Chop...Connectors by a single ChopToCenterConnector and
 * move method chop(Point2D.Double) into Figure interface.
 *
 * @see Connector
 *
 * @author Werner Randelshofer
 * @version 2.1 2006-03-22 Support for total stroke width added.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class ChopBoxConnector extends AbstractConnector {
    
    
    /** Creates a new instance.
     * Only used for storable.
     */
    public ChopBoxConnector() {
    }
    
    public ChopBoxConnector(Figure owner) {
        super(owner);
    }
    
    public Point2D.Double findStart(ConnectionFigure connection) {
        Figure startFigure = connection.getStartConnector().getOwner();
        Point2D.Double from;
        if (connection.getPointCount() <= 2 || connection.getLiner() != null) {
            if (connection.getEndConnector() == null) {
                from = connection.getEndPoint();
            } else {
                Rectangle2D.Double r1 = getConnectorTarget(connection.getEndConnector().getOwner()).getBounds();
                from = new Point2D.Double(r1.x + r1.width/2, r1.y + r1.height/2);
            }
        } else {
            from = connection.getPoint(1);
        }
        return chop(startFigure, from);
    }
    
    public Point2D.Double findEnd(ConnectionFigure connection) {
        Figure endFigure = connection.getEndConnector().getOwner();
        Point2D.Double from;
        if (connection.getPointCount() <= 2 || connection.getLiner() != null) {
            if (connection.getStartConnector() == null) {
                from = connection.getStartPoint();
            } else {
                Rectangle2D.Double r1 = getConnectorTarget(connection.getStartConnector().getOwner()).getBounds();
                from = new Point2D.Double(r1.x + r1.width/2, r1.y + r1.height/2);
            }
        } else {
            from = connection.getPoint(connection.getPointCount() - 2);
        }
        
        return chop(endFigure, from);
    }
    
    protected Point2D.Double chop(Figure target, Point2D.Double from) {
        target = getConnectorTarget(target);
        Rectangle2D.Double r = target.getBounds();
        if (STROKE_COLOR.get(target) != null) {
            double grow;
            switch (STROKE_PLACEMENT.get(target)) {
                case CENTER:
                 default :
                    grow = AttributeKeys.getStrokeTotalWidth(target) / 2d;
                    break;
                case OUTSIDE :
                    grow = AttributeKeys.getStrokeTotalWidth(target);
                    break;
                case INSIDE :
                    grow = 0d;
                    break;
            }
            Geom.grow(r, grow, grow);
        }
        return Geom.angleToPoint(r, Geom.pointToAngle(r, from));
    }
}
