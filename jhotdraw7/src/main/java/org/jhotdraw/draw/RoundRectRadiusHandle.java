/*
 * @(#)RoundRectRadiusHandle.java  2.0  2006-01-14
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

import org.jhotdraw.geom.Geom;
import org.jhotdraw.util.*;
import org.jhotdraw.undo.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * A Handle to manipulate the radius of a round lead rectangle.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 2004-03-02 Derived from JHotDraw 6.0b1.
 */
public class RoundRectRadiusHandle extends AbstractHandle {
    private static final int OFFSET = 6;
    private Point originalArc;
    CompositeEdit edit;
    
    /** Creates a new instance. */
    public RoundRectRadiusHandle(Figure owner) {
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
        RoundRectangleFigure owner = (RoundRectangleFigure) getOwner();
        Rectangle r = view.drawingToView(owner.getBounds());
        Point arc = view.drawingToView(new Point2D.Double(owner.getArcWidth(), owner.getArcHeight()));
        return new Point(r.x+arc.x/2+OFFSET, r.y+arc.y/2+OFFSET);
    }
    
    public void trackStart(Point anchor, int modifiersEx) {
        view.getDrawing().fireUndoableEditHappened(edit = new CompositeEdit("Mehrfachaenderung"));
        RoundRectangleFigure owner = (RoundRectangleFigure) getOwner();
        originalArc = view.drawingToView(new Point2D.Double(owner.getArcWidth(), owner.getArcHeight()));
    }
    
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        int dx = lead.x - anchor.x;
        int dy = lead.y - anchor.y;
        RoundRectangleFigure owner = (RoundRectangleFigure) getOwner();
        Rectangle r = view.drawingToView(owner.getBounds());
        Point viewArc = new Point(
        Geom.range(0, r.width, 2*(originalArc.x/2 + dx)),
        Geom.range(0, r.height, 2*(originalArc.y/2 + dy))
        );
        Point2D.Double arc = view.viewToDrawing(viewArc);
        owner.setArc(arc.x, arc.y);
    }
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        view.getDrawing().fireUndoableEditHappened(edit);
    }
}
