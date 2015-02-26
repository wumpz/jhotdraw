/*
 * @(#)BezierPointLocator.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.draw.locator;

import org.jhotdraw.draw.*;
import java.awt.geom.*;
import org.jhotdraw.xml.*;

/**
 * A {@link Locator} which locates a node on the bezier path of a BezierFigure.
 * 
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BezierPointLocator extends AbstractLocator {
    private static final long serialVersionUID = 1L;
    private int index;
    private int coord;
    
    public BezierPointLocator(int index) {
        this.index = index;
        this.coord = 0;
    }
    public BezierPointLocator(int index, int coord) {
        this.index = index;
        this.coord = index;
    }
    
    @Override
    public Point2D.Double locate(Figure owner) {
        BezierFigure plf = (BezierFigure) owner;
        if (index < plf.getNodeCount()) {
            return plf.getPoint(index, coord);
        }
        return new Point2D.Double(0, 0);
    }

    @Override
    public void write(DOMOutput out) {
        out.addAttribute("index", index, 0);
        out.addAttribute("coord", coord, 0);
    }

    @Override
    public void read(DOMInput in) {
       index = in.getAttribute("index", 0);
       coord = in.getAttribute("coord", 0);
    }
    
    
}
