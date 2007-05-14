/*
 * @(#)StraightLineFigure.java  1.0  January 4, 2007
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

package org.jhotdraw.samples.mini;

import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.geom.*;

/**
 * StraightLineFigure.
 *
 * @author Werner Randelshofer
 * @version 1.0 January 4, 2007 Created.
 */
public class StraightLineFigure extends AbstractAttributedFigure {
    private Line2D.Double line;
    
    /** Creates a new instance. */
    public StraightLineFigure() {
        line = new Line2D.Double();
    }

    protected void drawFill(Graphics2D g) {
    }

    protected void drawStroke(Graphics2D g) {
        g.draw(line);
    }

    public void transform(AffineTransform ty) {
        Point2D.Double p1 = (Point2D.Double) line.getP1();
        Point2D.Double p2 = (Point2D.Double) line.getP2();
        line.setLine(
                ty.transform(p1, p1),
                ty.transform(p2, p2)
                );
    }

    public void setBounds(Point2D.Double start, Point2D.Double end) {
        line.setLine(start, end);
    }

    public Rectangle2D.Double getBounds() {
        return (Rectangle2D.Double) line.getBounds2D();
    }

    public Object getTransformRestoreData() {
        return line.clone();
    }

    public void restoreTransformTo(Object restoreData) {
        line = (Line2D.Double) ((Line2D.Double) restoreData).clone();
    }

    public boolean contains(Point2D.Double p) {
        return Geom.lineContainsPoint(
                line.x1,line.y1,
                line.x2, line.y2,
                p.x, p.y, 
                AttributeKeys.getStrokeTotalWidth(this));
    }
    
    public StraightLineFigure clone() {
        StraightLineFigure that = (StraightLineFigure) super.clone();
        that.line = (Line2D.Double) this.line.clone();
        return that;
    }
}
