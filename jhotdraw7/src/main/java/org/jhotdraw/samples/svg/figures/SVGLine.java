/*
 * @(#)SVGLine.java  1.0  July 8, 2006
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

package org.jhotdraw.samples.svg.figures;

import java.awt.event.*;
import java.awt.geom.*;
import java.io.IOException;
import org.jhotdraw.samples.svg.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.xml.*;

/**
 * SVGLine.
 *
 * XXX - Get rid of this class and roll in the read() method into SVGPath.
 *
 * @author Werner Randelshofer
 * @version 1.0 July 8, 2006 Created.
 */
public class SVGLine extends LineFigure implements SVGFigure {
    
    /** Creates a new instance. */
    public SVGLine() {
        SVGUtil.setDefaults(this);
    }
    
    @Override public void write(DOMOutput out) throws IOException {
        out.addAttribute("x1", getPoint(0).x);
        out.addAttribute("y1", getPoint(0).y);
        out.addAttribute("x2", getPoint(1).x);
        out.addAttribute("y2", getPoint(1).y);
        writeAttributes(out);
    }
    protected void writeAttributes(DOMOutput out) throws IOException {
        SVGUtil.writeAttributes(this, out);
    }
    @Override public void read(DOMInput in) throws IOException {
        setBounds(
                new Point2D.Double(
                SVGUtil.getDimension(in, "x1"),
                SVGUtil.getDimension(in, "y1")
                ),
                new Point2D.Double(
                SVGUtil.getDimension(in, "x2"),
                SVGUtil.getDimension(in, "y2")
                )
                );
        readAttributes(in);
        AffineTransform tx = SVGUtil.getTransform(in, "transform");
        basicTransform(tx);
    }
    protected void readAttributes(DOMInput in) throws IOException {
        SVGUtil.readAttributes(this, in);
    }
    /**
     * Handles a mouse click.
     */
    @Override public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
            // do nothing
        return true;
    }
    public boolean isEmpty() {
        return false;
    }
}
