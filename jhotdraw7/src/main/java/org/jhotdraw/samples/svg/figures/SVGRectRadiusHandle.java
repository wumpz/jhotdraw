/*
 * @(#)RoundRectRadiusHandle.java  1.0  2006-12-10
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

import org.jhotdraw.draw.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.util.*;
import org.jhotdraw.undo.*;
import java.awt.*;
import java.awt.geom.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

/**
 * A Handle to manipulate the radius of a round lead rectangle.
 *
 * @author  Werner Randelshofer
 * @version 1.0 2006-12-10 Adapted from RoundRectangleHandle.
 */
public class SVGRectRadiusHandle extends AbstractHandle {
    private final static boolean DEBUG = false;
    private static final int OFFSET = 6;
    private Point originalArc;
    CompositeEdit edit;
    
    /** Creates a new instance. */
    public SVGRectRadiusHandle(Figure owner) {
        super(owner);
    }
    
    /**
     * Draws this handle.
     */
    public void draw(Graphics2D g) {
        drawDiamond(g, Color.yellow, Color.black);
    }
    
    protected Rectangle basicGetBounds() {
        Rectangle r = new Rectangle(locate());
        r.grow(getHandlesize() / 2 + 1, getHandlesize() / 2 + 1);
        return r;
    }
    
    private Point locate() {
        SVGRectFigure owner = (SVGRectFigure) getOwner();
        Rectangle2D.Double r = owner.getBounds();
        Point2D.Double p = new Point2D.Double(
                r.x + owner.getArcWidth(), 
                r.y + owner.getArcHeight()
                );
        if (TRANSFORM.get(owner) != null) {
            TRANSFORM.get(owner).transform(p, p);
        }
        return view.drawingToView(p);
    }
    
    public void trackStart(Point anchor, int modifiersEx) {
        view.getDrawing().fireUndoableEditHappened(edit = new CompositeEdit("Mehrfachaenderung"));
        SVGRectFigure owner = (SVGRectFigure) getOwner();
        originalArc = view.drawingToView(new Point2D.Double(owner.getArcWidth(), owner.getArcHeight()));
    }
    
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        int dx = lead.x - anchor.x;
        int dy = lead.y - anchor.y;
        SVGRectFigure owner = (SVGRectFigure) getOwner();
        Point2D.Double p = view.viewToDrawing(lead);
        if (TRANSFORM.get(owner) != null) {
            try {
                TRANSFORM.get(owner).inverseTransform(p, p);
            } catch (NoninvertibleTransformException ex) {
                if (DEBUG) ex.printStackTrace();
            }
        }
        Rectangle2D.Double r = owner.getBounds();
        owner.setArc(p.x - r.x, p.y - r.y);
    }
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        view.getDrawing().fireUndoableEditHappened(edit);
    }
    public String getToolTipText(Point p) {
        return ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels").getString("roundRectangleRadiusHandle.tip");
        }
}
