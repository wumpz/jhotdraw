/*
 * @(#)ODGRectRadiusHandle.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.samples.odg.figures;

import org.jhotdraw.undo.CompositeEdit;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.draw.handle.AbstractHandle;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.geom.*;
import static org.jhotdraw.samples.odg.ODGAttributeKeys.*;

/**
 * A Handle to manipulate the radius of a round lead rectangle.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class ODGRectRadiusHandle extends AbstractHandle {

    private static final boolean DEBUG = false;
    private static final int OFFSET = 6;
    private Dimension2DDouble originalArc2D;
    CompositeEdit edit;

    /** Creates a new instance. */
    public ODGRectRadiusHandle(Figure owner) {
        super(owner);
    }

    /**
     * Draws this handle.
     */
    @Override
    public void draw(Graphics2D g) {
        drawDiamond(g, Color.yellow, Color.black);
    }

    @Override
    protected Rectangle basicGetBounds() {
        Rectangle r = new Rectangle(locate());
        r.grow(getHandlesize() / 2 + 1, getHandlesize() / 2 + 1);
        return r;
    }

    private Point locate() {
        ODGRectFigure owner = (ODGRectFigure) getOwner();
        Rectangle2D.Double r = owner.getBounds();
        Point2D.Double p = new Point2D.Double(
                r.x + owner.getArcWidth(),
                r.y + owner.getArcHeight());
        if (owner.get(TRANSFORM) != null) {
            owner.get(TRANSFORM).transform(p, p);
        }
        return view.drawingToView(p);
    }

    @Override
    public void trackStart(Point anchor, int modifiersEx) {
        ODGRectFigure odgRect = (ODGRectFigure) getOwner();
        originalArc2D = odgRect.getArc();
    }

    @Override
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        ODGRectFigure odgRect = (ODGRectFigure) getOwner();
        odgRect.willChange();
        Point2D.Double p = view.viewToDrawing(lead);
        if (odgRect.get(TRANSFORM) != null) {
            try {
                odgRect.get(TRANSFORM).inverseTransform(p, p);
            } catch (NoninvertibleTransformException ex) {
                if (DEBUG) {
                    ex.printStackTrace();
                }
            }
        }
        Rectangle2D.Double r = odgRect.getBounds();
        odgRect.setArc(p.x - r.x, p.y - r.y);
        odgRect.changed();
    }

    @Override
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        final ODGRectFigure odgRect = (ODGRectFigure) getOwner();
        final Dimension2DDouble oldValue = originalArc2D;
        final Dimension2DDouble newValue = odgRect.getArc();
        view.getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
    private static final long serialVersionUID = 1L;

            @Override
            public String getPresentationName() {
                ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.odg.Labels");
                return labels.getString("arc");
            }

            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                odgRect.willChange();
                odgRect.setArc(oldValue);
                odgRect.changed();
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                odgRect.willChange();
                odgRect.setArc(newValue);
                odgRect.changed();
            }
        });
    }

    @Override
    public String getToolTipText(Point p) {
        return ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels").//
                getString("handle.roundRectangleRadius.toolTipText");
    }
}
