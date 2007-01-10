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
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.draw.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.samples.svg.*;
import org.jhotdraw.samples.svg.SVGConstants;
import org.jhotdraw.xml.*;
import org.jhotdraw.util.*;
/**
 * SVGEllipse represents a SVG ellipse and a SVG circle element.
 *
 * @author Werner Randelshofer
 * @version 1.0 July 8, 2006 Created.
 */
public class SVGEllipseFigure extends SVGAttributedFigure implements SVGFigure {
    private Ellipse2D.Double ellipse;
    /**
     * This is used to perform faster drawing and hit testing.
     */
    private Shape cachedTransformedShape;
    
    /** Creates a new instance. */
    public SVGEllipseFigure() {
        this(0, 0, 0, 0);
    }
    
    public SVGEllipseFigure(double x, double y, double width, double height) {
        ellipse = new Ellipse2D.Double(x, y, width, height);
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
        return ellipse.x;
    }
    public double getY() {
        return ellipse.y;
    }
    public double getWidth() {
        return ellipse.getWidth();
    }
    public double getHeight() {
        return ellipse.getHeight();
    }
    
    public Rectangle2D.Double getBounds() {
        Rectangle2D rx = getTransformedShape().getBounds2D();
        Rectangle2D.Double r = (rx instanceof Rectangle2D.Double) ? (Rectangle2D.Double) rx : new Rectangle2D.Double(rx.getX(), rx.getY(), rx.getWidth(), rx.getHeight());
        return r;
    }
    public Rectangle2D.Double getFigureDrawBounds() {
        Rectangle2D rx = getTransformedShape().getBounds2D();
        Rectangle2D.Double r = (rx instanceof Rectangle2D.Double) ? (Rectangle2D.Double) rx : new Rectangle2D.Double(rx.getX(), rx.getY(), rx.getWidth(), rx.getHeight());
        double g = AttributeKeys.getPerpendicularHitGrowth(this);
        Geom.grow(r, g, g);
        return r;
    }
    /**
     * Checks if a Point2D.Double is inside the figure.
     */
    public boolean contains(Point2D.Double p) {
        return getTransformedShape().contains(p);
    }
    private void invalidateTransformedShape() {
        cachedTransformedShape = null;
    }
    private Shape getTransformedShape() {
        if (cachedTransformedShape == null) {
            if (TRANSFORM.get(this) == null) {
                cachedTransformedShape = ellipse;
            } else {
                cachedTransformedShape = TRANSFORM.get(this).createTransformedShape(ellipse);
            }
        }
        return cachedTransformedShape;
    }
    public void basicSetBounds(Point2D.Double anchor, Point2D.Double lead) {
        ellipse.x = Math.min(anchor.x, lead.x);
        ellipse.y = Math.min(anchor.y , lead.y);
        ellipse.width = Math.max(0.1, Math.abs(lead.x - anchor.x));
        ellipse.height = Math.max(0.1, Math.abs(lead.y - anchor.y));
    }
    /**
     * Transforms the figure.
     *
     * @param tx the transformation.
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
    public void restoreTransformTo(Object geometry) {
            invalidateTransformedShape();
            Object[] o = (Object[]) geometry;
            ellipse = (Ellipse2D.Double) ((Ellipse2D.Double) o[0]).clone();
            if (o[1] == null) {
                TRANSFORM.set(this, null);
            } else {
            TRANSFORM.set(this, (AffineTransform) ((AffineTransform) o[1]).clone());
            }
    }
    
    public Object getTransformRestoreData() {
        return new Object[] {
            ellipse.clone(),
            TRANSFORM.get(this)
        };
    }
    
    
    // ATTRIBUTES
    public void basicSetAttribute(AttributeKey key, Object newValue) {
        if (key == SVGAttributeKeys.TRANSFORM) {
            invalidateTransformedShape();
        }
        super.basicSetAttribute(key, newValue);
    }
    // EDITING
    public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = (LinkedList<Handle>) super.createHandles(detailLevel);
        handles.add(new RotateHandle(this));
        return handles;
    }
    @Override public Collection<Action> getActions(Point2D.Double p) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.svg.Labels");
        LinkedList<Action> actions = new LinkedList<Action>();
        if (TRANSFORM.get(this) != null) {
            actions.add(new AbstractAction(labels.getString("removeTransform")) {
                public void actionPerformed(ActionEvent evt) {
                    TRANSFORM.set(SVGEllipseFigure.this, null);
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
    public SVGEllipseFigure clone() {
        SVGEllipseFigure that = (SVGEllipseFigure) super.clone();
        that.ellipse = (Ellipse2D.Double) this.ellipse.clone();
        return that;
    }
    
    // EVENT HANDLING
    public boolean isEmpty() {
        Rectangle2D.Double b = getBounds();
        return b.width <= 0 || b.height <= 0;
    }
    @Override public void invalidate() {
        super.invalidate();
        invalidateTransformedShape();
    }
}
