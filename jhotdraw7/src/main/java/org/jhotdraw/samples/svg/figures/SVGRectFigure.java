/*
 * @(#)SVGRect.java  2.0  2007-04-14
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
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
 * @version 2.0 2007-04-14 Adapted for new AttributeKeys.TRANSFORM support. 
 * <br>1.0 July 8, 2006 Created.
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
        return (Rectangle2D.Double) roundrect.getBounds2D();
    }
    @Override public Rectangle2D.Double getDrawingArea() {
        Rectangle2D rx = getTransformedShape().getBounds2D();
        Rectangle2D.Double r = (rx instanceof Rectangle2D.Double) ? (Rectangle2D.Double) rx : new Rectangle2D.Double(rx.getX(), rx.getY(), rx.getWidth(), rx.getHeight());
        double g = SVGAttributeKeys.getPerpendicularHitGrowth(this) * 2;
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
                AffineTransform t = TRANSFORM.getClone(this);
                t.preConcatenate(tx);
                TRANSFORM.basicSet(this, t);
            }
        } else {
            Point2D.Double anchor = getStartPoint();
            Point2D.Double lead = getEndPoint();
            basicSetBounds(
                    (Point2D.Double) tx.transform(anchor, anchor),
                    (Point2D.Double) tx.transform(lead, lead)
                    );
        }
        // FIXME - This is experimental code
        if (FILL_GRADIENT.get(this) != null &&
                ! FILL_GRADIENT.get(this).isRelativeToFigureBounds()) {
            FILL_GRADIENT.get(this).transform(tx);
        }
        if (STROKE_GRADIENT.get(this) != null &&
                ! STROKE_GRADIENT.get(this).isRelativeToFigureBounds()) {
            STROKE_GRADIENT.get(this).transform(tx);
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
        Object[] restoreData = (Object[]) geometry;
        roundrect = (RoundRectangle2D.Double) ((RoundRectangle2D.Double) restoreData[0]).clone();
        TRANSFORM.basicSetClone(this, (AffineTransform) restoreData[1]);
        FILL_GRADIENT.basicSetClone(this, (Gradient) restoreData[2]);
        STROKE_GRADIENT.basicSetClone(this, (Gradient) restoreData[3]);
    }
    
    public Object getTransformRestoreData() {
        return new Object[] {
            roundrect.clone(),
            TRANSFORM.get(this),
            FILL_GRADIENT.getClone(this),
            STROKE_GRADIENT.getClone(this),
        };
    }
    
    // EDITING
    @Override public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = new LinkedList<Handle>();
        switch (detailLevel % 2) {
            case 0 :
                ResizeHandleKit.addResizeHandles(this, handles);
                handles.add(new SVGRectRadiusHandle(this));
                break;
            case 1 :
                TransformHandleKit.addTransformHandles(this, handles);
                break;
            default:
                break;
        }
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
