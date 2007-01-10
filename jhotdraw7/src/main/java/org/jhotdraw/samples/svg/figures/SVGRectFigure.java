/*
 * @(#)SVGRect.java  1.0  July 8, 2006
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
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;
import org.jhotdraw.samples.svg.*;
import org.jhotdraw.samples.svg.SVGConstants;
import org.jhotdraw.util.*;
import org.jhotdraw.xml.*;
import org.jhotdraw.geom.*;

/**
 * SVGRect.
 *
 * @author Werner Randelshofer
 * @version 1.0 July 8, 2006 Created.
 */
public class SVGRectFigure extends SVGAttributedFigure implements SVGFigure {
    private RoundRectangle2D.Double roundrect;
    /**
     * This is used to perform faster drawing.
     */
    private Shape cachedTransformedShape;
    /**
     * This is used to perform faster hit testing.
     */
    private Shape cachedHitShape;
    
    /** Creates a new instance. */
    public SVGRectFigure() {
        this(0,0,0,0);
    }
    public SVGRectFigure(double x, double y, double width, double height) {
        this(x, y, width, height, 0, 0);
    }
    public SVGRectFigure(double x, double y, double width, double height, double rx, double ry) {
        roundrect = new RoundRectangle2D.Double(x, y, width, height, rx, ry);
       SVGAttributeKeys.setDefaults(this);
    }
    
    // DRAWING
    protected void drawFill(Graphics2D g) {
        g.fill(getTransformedShape());
    }
    
    protected void drawStroke(Graphics2D g) {
        g.draw(getTransformedShape());
    }
    
    // SHAPE AND BOUNDS
    public double getX() {
        return roundrect.x;
    }
    public double getY() {
        return roundrect.y;
    }
    public double getWidth() {
        return roundrect.width;
    }
    public double getHeight() {
        return roundrect.height;
    }
    public double getArcWidth() {
        return roundrect.arcwidth / 2d;
    }
    public double getArcHeight() {
        return roundrect.archeight / 2d;
    }
    public Rectangle2D.Double getBounds() {
        Rectangle2D rx = getTransformedShape().getBounds2D();
        Rectangle2D.Double r = (rx instanceof Rectangle2D.Double) ? (Rectangle2D.Double) rx : new Rectangle2D.Double(rx.getX(), rx.getY(), rx.getWidth(), rx.getHeight());
        return r;
    }
    public Rectangle2D.Double getFigureDrawBounds() {
        Rectangle2D rx = getTransformedShape().getBounds2D();
        Rectangle2D.Double r = (rx instanceof Rectangle2D.Double) ? (Rectangle2D.Double) rx : new Rectangle2D.Double(rx.getX(), rx.getY(), rx.getWidth(), rx.getHeight());
        double g = AttributeKeys.getPerpendicularHitGrowth(this) * 2;
        Geom.grow(r, g, g);
        return r;
    }
    /**
     * Checks if a Point2D.Double is inside the figure.
     */
    public boolean contains(Point2D.Double p) {
        return getHitShape().contains(p);
    }
    
    public void basicSetBounds(Point2D.Double anchor, Point2D.Double lead) {
        invalidateTransformedShape();
        roundrect.x = Math.min(anchor.x, lead.x);
        roundrect.y = Math.min(anchor.y , lead.y);
        roundrect.width = Math.max(0.1, Math.abs(lead.x - anchor.x));
        roundrect.height = Math.max(0.1, Math.abs(lead.y - anchor.y));
    }
    private void invalidateTransformedShape() {
        cachedTransformedShape = null;
        cachedHitShape = null;
    }
    private Shape getTransformedShape() {
        if (cachedTransformedShape == null) {
                if (getArcHeight() == 0 || getArcWidth() == 0) {
                    cachedTransformedShape = roundrect.getBounds2D();
                } else {
                    cachedTransformedShape = (Shape) roundrect.clone();
                }
            if (TRANSFORM.get(this) != null) {
                cachedTransformedShape = TRANSFORM.get(this).createTransformedShape(cachedTransformedShape);
            }
        }
        return cachedTransformedShape;
    }
    private Shape getHitShape() {
        if (cachedHitShape == null) {
            cachedHitShape = new GrowStroke(
                    (float) SVGAttributeKeys.getStrokeTotalWidth(this) / 2f,
                    (float) SVGAttributeKeys.getStrokeTotalMiterLimit(this)
                    ).createStrokedShape(getTransformedShape());
        }
        return cachedHitShape;
    }
    /**
     * Transforms the figure.
     * @param tx The transformation.
     */
    public void basicTransform(AffineTransform tx) {
        invalidateTransformedShape();
        if (TRANSFORM.get(this) != null ||
                (tx.getType() & (AffineTransform.TYPE_TRANSLATION | AffineTransform.TYPE_MASK_SCALE)) != tx.getType()) {
            if (TRANSFORM.get(this) == null) {
                TRANSFORM.basicSet(this, (AffineTransform) tx.clone());
            } else {
                TRANSFORM.get(this).preConcatenate(tx);
            }
        } else {
            Point2D.Double anchor = getStartPoint();
            Point2D.Double lead = getEndPoint();
            basicSetBounds(
                    (Point2D.Double) tx.transform(anchor, anchor),
                    (Point2D.Double) tx.transform(lead, lead)
                    );
        }
    }
    // ATTRIBUTES
    public void setArc(final double w, final double h) {
        willChange();
        final double oldWidth = roundrect.getArcWidth();
        final double oldHeight = roundrect.getArcHeight();
        roundrect.arcwidth = Math.max(0d, Math.min(roundrect.width, w * 2d));
        roundrect.archeight = Math.max(0d, Math.min(roundrect.height, h * 2d));
      //  fireFigureChanged(getDrawBounds());
        fireUndoableEditHappened(new AbstractUndoableEdit() {
            public String getPresentationName() {
                return "Arc";
            }
            public void undo() throws CannotUndoException {
                super.undo();
                willChange();
                roundrect.arcwidth = oldWidth;
                roundrect.archeight = oldHeight;
                changed();
            }
            public void redo() throws CannotRedoException {
                super.redo();
                willChange();
                roundrect.arcwidth = w * 2d;
                roundrect.archeight = h * 2d;
                changed();
            }
        });
        changed();
    }
    public void restoreTransformTo(Object geometry) {
            invalidateTransformedShape();
            Object[] o = (Object[]) geometry;
            roundrect = (RoundRectangle2D.Double) ((RoundRectangle2D.Double) o[0]).clone();
            if (o[1] == null) {
                TRANSFORM.set(this, null);
            } else {
            TRANSFORM.set(this, (AffineTransform) ((AffineTransform) o[1]).clone());
            }
    }
    
    public Object getTransformRestoreData() {
        return new Object[] {
            roundrect.clone(),
            TRANSFORM.get(this)
        };
    }
    
    // EDITING
    public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = (LinkedList<Handle>) super.createHandles(detailLevel);
         handles.add(new SVGRectRadiusHandle(this));
                    handles.add(new RotateHandle(this));

        return handles;
    }
    @Override public Collection<Action> getActions(Point2D.Double p) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.svg.Labels");
        LinkedList<Action> actions = new LinkedList<Action>();
        if (TRANSFORM.get(this) != null) {
            actions.add(new AbstractAction(labels.getString("removeTransform")) {
                public void actionPerformed(ActionEvent evt) {
                    TRANSFORM.set(SVGRectFigure.this, null);
                }
            });
        }
        return actions;
    }
    // CONNECTING
    public boolean canConnect() {
        return false; // SVG does not support connecting
    }
    public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
        return null; // SVG does not support connectors
    }
    public Connector findCompatibleConnector(Connector c, boolean isStartConnector) {
        return null; // SVG does not support connectors
    }
    
    // COMPOSITE FIGURES
    // CLONING
    public SVGRectFigure clone() {
        SVGRectFigure that = (SVGRectFigure) super.clone();
        that.roundrect = (RoundRectangle2D.Double) this.roundrect.clone();
        that.cachedTransformedShape = null;
        that.cachedHitShape = null;
        return that;
    }
    
    public boolean isEmpty() {
        Rectangle2D.Double b = getBounds();
        return b.width <= 0 || b.height <= 0;
    }
    
    @Override public void invalidate() {
        super.invalidate();
        invalidateTransformedShape();
    }
}
