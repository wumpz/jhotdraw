/*
 * @(#)RoundRectRadiusHandle.java  3.0  2008-05-11
 *
 * Copyright (c) 1996-2008 by the original authors of JHotDraw
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

import org.jhotdraw.geom.Geom;
import org.jhotdraw.util.*;
import org.jhotdraw.undo.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Locale;
import javax.swing.undo.*;

/**
 * A Handle to manipulate the radius of a round lead rectangle.
 *
 * @author  Werner Randelshofer
 * @version 3.0 2008-05-11 Implemented undo/redo support.
 * <br>2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 2004-03-02 Derived from JHotDraw 6.0b1.
 */
public class RoundRectangleRadiusHandle extends AbstractHandle {

    private static final int OFFSET = 6;
    private Point originalArc;
    CompositeEdit edit;

    /** Creates a new instance. */
    public RoundRectangleRadiusHandle(Figure owner) {
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
        int h = getHandlesize();
        r.x -= h / 2;
        r.y -= h / 2;
        r.width = r.height = h;
        return r;
    }

    private Point locate() {
        RoundRectangleFigure owner = (RoundRectangleFigure) getOwner();
        Rectangle r = view.drawingToView(owner.getBounds());
        Point arc = view.drawingToView(new Point2D.Double(owner.getArcWidth(), owner.getArcHeight()));
        return new Point(r.x + arc.x / 2 + OFFSET, r.y + arc.y / 2 + OFFSET);
    }

    public void trackStart(Point anchor, int modifiersEx) {
        RoundRectangleFigure owner = (RoundRectangleFigure) getOwner();
        originalArc = view.drawingToView(new Point2D.Double(owner.getArcWidth(), owner.getArcHeight()));
    }

    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        int dx = lead.x - anchor.x;
        int dy = lead.y - anchor.y;
        RoundRectangleFigure owner = (RoundRectangleFigure) getOwner();
        Rectangle r = view.drawingToView(owner.getBounds());
        Point viewArc = new Point(
                Geom.range(0, r.width, 2 * (originalArc.x / 2 + dx)),
                Geom.range(0, r.height, 2 * (originalArc.y / 2 + dy)));
        Point2D.Double arc = view.viewToDrawing(viewArc);
        owner.willChange();
        owner.setArc(arc.x, arc.y);
        owner.changed();
    }

    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        int dx = lead.x - anchor.x;
        int dy = lead.y - anchor.y;
        final RoundRectangleFigure owner = (RoundRectangleFigure) getOwner();
        Rectangle r = view.drawingToView(owner.getBounds());
        Point viewArc = new Point(
                Geom.range(0, r.width, 2 * (originalArc.x / 2 + dx)),
                Geom.range(0, r.height, 2 * (originalArc.y / 2 + dy)));
        final Point2D.Double oldArc = view.viewToDrawing(originalArc);
        final Point2D.Double newArc = view.viewToDrawing(viewArc);
        fireUndoableEditHappened(new AbstractUndoableEdit() {
            @Override
            public String getPresentationName() {
                ResourceBundleUtil labels =
                        ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
                return labels.getString("attribute.roundRectRadius");
            }


            @Override
            public void redo() throws CannotRedoException {
                owner.willChange();
                owner.setArc(newArc.x, newArc.y);
                owner.changed();
                super.redo();
            }

            @Override
            public void undo() throws CannotUndoException {
                owner.willChange();
                owner.setArc(oldArc.x, oldArc.y);
                owner.changed();
                super.undo();
            }
        });
    }

    public String getToolTipText(Point p) {
        return ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels").getString("roundRectangleRadiusHandle.tip");
    }
}
