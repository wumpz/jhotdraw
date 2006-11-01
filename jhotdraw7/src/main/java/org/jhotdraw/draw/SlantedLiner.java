/*
 * @(#)SlantedLiner.java  1.0  24. Januar 2006
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

import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
import org.jhotdraw.xml.DOMStorable;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.geom.*;

/**
 * SlantedLiner.
 * 
 * 
 * @author Werner Randelshofer
 * @version 1.0 24. Januar 2006 Created.
 */
public class SlantedLiner
        implements Liner, DOMStorable {
    private double slantSize;
    
    /** Creates a new instance. */
    public SlantedLiner() {
        this(20);
    }
    public SlantedLiner(double slantSize) {
        this.slantSize = slantSize;
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
        // Ensure path has exactly four nodes
        while (path.size() < 4) {
            path.add(1, new BezierPath.Node(0,0));
        }
        while (path.size() < 4) {
            path.remove(1);
        }
        
        Point2D.Double sp = start.findStart(figure);
        Point2D.Double ep = end.findEnd(figure);
        sp = figure.getStartPoint();
        ep = figure.getEndPoint();
        
        Rectangle2D.Double sb = start.getBounds();
        Rectangle2D.Double eb = end.getBounds();
        path.get(0).moveTo(sp);
        path.get(path.size() - 1).moveTo(ep);
        
        int outcode = sb.outcode(sp);
        if (outcode == 0) {
            outcode = Geom.outcode(sb, eb);
        }
        
        if ((outcode & Geom.OUT_RIGHT) != 0) {
            path.get(1).moveTo(sp.x + slantSize, sp.y);
        } else if ((outcode & Geom.OUT_LEFT) != 0) {
            path.get(1).moveTo(sp.x - slantSize, sp.y);
        } else if ((outcode & Geom.OUT_BOTTOM) != 0) {
            path.get(1).moveTo(sp.x, sp.y + slantSize);
        } else {
            path.get(1).moveTo(sp.x, sp.y - slantSize);
        }
        outcode = eb.outcode(ep);
        if (outcode == 0) {
            outcode = Geom.outcode(eb, sb);
        }
        if ((outcode & Geom.OUT_RIGHT) != 0) {
            path.get(2).moveTo(ep.x + slantSize, ep.y);
        } else if ((outcode & Geom.OUT_LEFT) != 0) {
            path.get(2).moveTo(ep.x - slantSize, ep.y);
        } else if ((outcode & Geom.OUT_BOTTOM) != 0) {
            path.get(2).moveTo(ep.x, ep.y + slantSize);
        } else {
            path.get(2).moveTo(ep.x, ep.y - slantSize);
        }
        path.invalidatePath();
        
    }

    public void read(DOMInput in) {
        slantSize = in.getAttribute("slant", 20d);
    }

    public void write(DOMOutput out) {
        out.addAttribute("slant", slantSize);
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
