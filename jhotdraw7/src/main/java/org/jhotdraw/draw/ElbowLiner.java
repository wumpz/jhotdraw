/*
 * @(#)ElbowLiner.java  1.0  2006-03-28
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

package org.jhotdraw.draw;

import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
import org.jhotdraw.xml.DOMStorable;

/**
 * A Liner that constrains a connection to orthogonal lines.
 *
 * @author Werner Randelshofer
 * @version 1.0 2006-03-28 Created.
 */
public class ElbowLiner
        implements Liner, DOMStorable {
    
    /** Creates a new instance. */
    public ElbowLiner() {
    }
    
    public Collection<Handle> createHandles(BezierPath path) {
        return null;
    }
    
    public void lineout(ConnectionFigure figure) {
        BezierPath path = ((LineConnectionFigure) figure).getBezierPath();
        Connector start = figure.getStartConnector();
        Connector end = figure.getEndConnector();
        if (start == null || end == null || path == null) {
            return;
        }
        
        Point2D.Double sp = start.findStart(figure);
        Point2D.Double ep = end.findEnd(figure);
        
        path.clear();
        path.add(new BezierPath.Node(sp.x,sp.y));
        
        if (sp.x == ep.x || sp.y == ep.y) {
            path.add(new BezierPath.Node(ep.x,ep.y));
        } else {
            Rectangle2D.Double sb = start.getBounds();
            sb.x += 5d;
            sb.y += 5d;
            sb.width -= 10d;
            sb.height -= 10d;
            Rectangle2D.Double eb = end.getBounds();
            eb.x += 5d;
            eb.y += 5d;
            eb.width -= 10d;
            eb.height -= 10d;
            
            int soutcode = sb.outcode(sp);
            if (soutcode == 0) {
                soutcode = Geom.outcode(sb, eb);
            }
            int eoutcode = eb.outcode(ep);
            if (eoutcode == 0) {
                eoutcode = Geom.outcode(eb, sb);
            }
            
            if ((soutcode & (Geom.OUT_TOP | Geom.OUT_BOTTOM)) != 0 &&
                    (eoutcode & (Geom.OUT_TOP | Geom.OUT_BOTTOM)) != 0) {
                path.add(new BezierPath.Node(sp.x, (sp.y + ep.y)/2));
                path.add(new BezierPath.Node(ep.x, (sp.y + ep.y)/2));
            } else if ((soutcode & (Geom.OUT_LEFT | Geom.OUT_RIGHT)) != 0 &&
                    (eoutcode & (Geom.OUT_LEFT | Geom.OUT_RIGHT)) != 0) {
                path.add(new BezierPath.Node((sp.x + ep.x)/2, sp.y));
                path.add(new BezierPath.Node((sp.x + ep.x)/2, ep.y));
            }
            
            path.add(new BezierPath.Node(ep.x,ep.y));
        }

        path.invalidatePath();
    }
    
    public void read(DOMInput in) {
    }
    
    public void write(DOMOutput out) {
    }
    public Liner clone() {
        try {
            return (Liner) super.clone();
        } catch (CloneNotSupportedException ex) {
            InternalError error = new InternalError(ex.getMessage());
            error.initCause(ex);
            throw error;
        }
    }
}
