/*
 * @(#)SVGEllipse.java  1.0  July 8, 2006
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

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.samples.svg.*;
import org.jhotdraw.xml.*;
/**
 * SVGEllipse presents a SVG ellipse or a SVG circle element.
 * It is always written as an SVG ellipse element.
 * <p>
 * FIXME - Add support for transforms.
 *
 * @author Werner Randelshofer
 * @version 1.0 July 8, 2006 Created.
 */
public class SVGEllipse extends EllipseFigure implements SVGFigure {
    
    /** Creates a new instance. */
    public SVGEllipse() {
        SVGUtil.setDefaults(this);
    }
    
    @Override public void write(DOMOutput out) throws IOException {
        Rectangle2D.Double r = getBounds();
        out.addAttribute("cx", r.x + r.width / 2d);
        out.addAttribute("cy", r.y + r.height / 2d);
        out.addAttribute("rx", r.width / 2);
        out.addAttribute("ry", r.height / 2);
        writeAttributes(out);
    }
    protected void writeAttributes(DOMOutput out) throws IOException {
        SVGUtil.writeAttributes(this, out);
    }
    
    @Override public void read(DOMInput in) throws IOException {
        double rx, ry;
        if (in.getTagName().equals("circle")) {
            rx = ry = SVGUtil.getDimension(in, "r");
        } else {
            rx = SVGUtil.getDimension(in, "rx");
            ry = SVGUtil.getDimension(in, "ry");
        }
        double x = SVGUtil.getDimension(in, "cx") - rx;
        double y = SVGUtil.getDimension(in, "cy") - ry;
        double w = rx * 2d;
        double h = ry * 2d;
        setBounds(new Point2D.Double(x,y), new Point2D.Double(x+w,y+h));
        readAttributes(in);
        
        AffineTransform tx = SVGUtil.getTransform(in, "transform");
        basicTransform(tx);
    }
    protected void readAttributes(DOMInput in) throws IOException {
        SVGUtil.readAttributes(this, in);
    }

    public boolean isEmpty() {
        Rectangle2D.Double b = getBounds();
        return b.width <= 0 || b.height <= 0;
    }
}
