/*
 * @(#)ChopRectangleConnector.java  2.2.2  2007-05-14
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.draw;

import java.awt.geom.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.geom.*;
/**
 * A ChopRectangleConnector locates connection points by
 * choping the connection between the centers of the
 * two figures at the display box.
 * <p>
 * XXX - Replace all Chop...Connectors by a single ChopToCenterConnector and
 * move method chop(Point2D.Double) into Figure interface.
 *
 * @see Connector
 *
 * @author Werner Randelshofer
 * @version 2.2.2 2007-05-14 Fixed strange layout behavior while manipulating
 * a connection. 
 * <br>2.2.1 2007-02-01 Added support for self-connecting connections. 
 * <br>2.2 2006-12-23 Renamed from ChopBoxConnector to ChopRectangleConnector.
 * <br>2.1 2006-03-22 Support for total stroke width added.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class ChopRectangleConnector extends AbstractConnector {
    
    
    /** Creates a new instance.
     * Only used for storable.
     */
    public ChopRectangleConnector() {
    }
    
    public ChopRectangleConnector(Figure owner) {
        super(owner);
    }
    
    @Override
    public Point2D.Double findStart(ConnectionFigure connection) {
        Figure startFigure = connection.getStartConnector().getOwner();
        Point2D.Double from;
        if (connection.getNodeCount() <= 2 || connection.getLiner() != null) {
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
    
    @Override
    public Point2D.Double findEnd(ConnectionFigure connection) {
        Figure endFigure = connection.getEndConnector().getOwner();
        Point2D.Double from;
        if (connection.getNodeCount() <= 3 && connection.getStartFigure() == connection.getEndFigure() ||
                connection.getNodeCount() <= 2 ||
                connection.getLiner() != null) {
            if (connection.getStartConnector() == null) {
                from = connection.getStartPoint();
            } else if (connection.getStartFigure() == connection.getEndFigure()) {
                Rectangle2D.Double r1 = getConnectorTarget(connection.getStartConnector().getOwner()).getBounds();
                from = new Point2D.Double(r1.x + r1.width/2, r1.y);
            } else {
                Rectangle2D.Double r1 = getConnectorTarget(connection.getStartConnector().getOwner()).getBounds();
                from = new Point2D.Double(r1.x + r1.width/2, r1.y + r1.height/2);
            }
        } else {
            from = connection.getPoint(connection.getNodeCount() - 2);
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
