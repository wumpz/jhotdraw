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
import org.jhotdraw.util.*;
import org.jhotdraw.xml.*;
import org.jhotdraw.geom.*;

/**
 * SVGRect.
 * <p>
 * FIXME - Add support for transforms.
 *
 * @author Werner Randelshofer
 * @version 1.0 July 8, 2006 Created.
 */
public class SVGRect extends SVGAttributedFigure implements SVGFigure {
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
    public SVGRect() {
        this(0,0,0,0);
    }
    public SVGRect(double x, double y, double width, double height) {
        this(x, y, width, height, 0, 0);
    }
    public SVGRect(double x, double y, double width, double height, double rx, double ry) {
        roundrect = new RoundRectangle2D.Double(x, y, width, height, rx, ry);
    }
    
    // DRAWING
    protected void drawFill(Graphics2D g) {
        g.fill(getTransformedShape());
    }
    
    protected void drawStroke(Graphics2D g) {
        g.draw(getTransformedShape());
    }
    
    // SHAPE AND BOUNDS
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
                TRANSFORM.set(this, (AffineTransform) tx.clone());
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
    public double getArcWidth() {
        return roundrect.arcwidth / 2d;
    }
    public double getArcHeight() {
        return roundrect.archeight / 2d;
    }
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
    public void restoreTo(Object geometry) {
        TRANSFORM.set(this, (geometry == null) ? null : (AffineTransform) ((AffineTransform) geometry).clone());
    }
    
    public Object getRestoreData() {
        return TRANSFORM.get(this) == null ? new AffineTransform() : TRANSFORM.get(this).clone();
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
                    TRANSFORM.set(SVGRect.this, null);
                }
            });
        }
        return actions;
    }
    // CONNECTING
    public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
        // XXX - This doesn't work with a transformed rect
        return new ChopRoundRectConnector(this);
    }
    public Connector findCompatibleConnector(Connector c, boolean isStartConnector) {
        // XXX - This doesn't work with a transformed rect
        return new ChopRoundRectConnector(this);
    }
    public Point2D.Double chop(Point2D.Double from) {
        // XXX - This doesn't work with a transformed rect
        Rectangle2D.Double outer = getBounds();
        
        double grow;
        switch (STROKE_PLACEMENT.get(this)) {
            case CENTER :
            default :
                grow = AttributeKeys.getStrokeTotalWidth(this) / 2;
                break;
            case OUTSIDE :
                grow = AttributeKeys.getStrokeTotalWidth(this);
                break;
            case INSIDE :
                grow = 0;
                break;
        }
        outer.x -= grow;
        outer.y -= grow;
        outer.width += grow * 2;
        outer.height += grow * 2;
        
        Rectangle2D.Double inner = (Rectangle2D.Double) outer.clone();
        double gw = -(getArcWidth() + grow * 2) / 2;
        double gh = -(getArcHeight() + grow *2) / 2;
        inner.x -= gw;
        inner.y -= gh;
        inner.width += gw * 2;
        inner.height += gh * 2;
        
        double angle = Geom.pointToAngle(outer, from);
        Point2D.Double p = Geom.angleToPoint(outer, Geom.pointToAngle(outer, from));
        
        if (p.x == outer.x
                || p.x == outer.x + outer.width) {
            p.y = Math.min(Math.max(p.y, inner.y), inner.y + inner.height);
        } else {
            p.x = Math.min(Math.max(p.x, inner.x), inner.x + inner.width);
        }
        return p;
    }
    
    // COMPOSITE FIGURES
    // CLONING
    public SVGRect clone() {
        SVGRect that = (SVGRect) super.clone();
        that.roundrect = (RoundRectangle2D.Double) this.roundrect.clone();
        that.cachedTransformedShape = null;
        that.cachedHitShape = null;
        return that;
    }
    
    
    
    public void read(DOMInput in) throws IOException {
        double x = SVGUtil.getDimension(in, "x");
        double y = SVGUtil.getDimension(in, "y");
        double w = SVGUtil.getDimension(in, "width");
        double h = SVGUtil.getDimension(in, "height");
        setBounds(new Point2D.Double(x,y), new Point2D.Double(x+w,y+h));
        setArc(
                SVGUtil.getDimension(in, "rx"),
                SVGUtil.getDimension(in, "ry")
                );
        readAttributes(in);
        AffineTransform tx = SVGUtil.getTransform(in, "transform");
        basicTransform(tx);
    }
    protected void readAttributes(DOMInput in) throws IOException {
        SVGUtil.readAttributes(this, in);
    }
    
    public void write(DOMOutput out) throws IOException {
        Rectangle2D.Double r = getBounds();
        out.addAttribute("x", r.x);
        out.addAttribute("y", r.y);
        out.addAttribute("width", r.width);
        out.addAttribute("height", r.height);
        out.addAttribute("rx", getArcWidth());
        out.addAttribute("ry", getArcHeight());
        writeAttributes(out);
    }
    protected void writeAttributes(DOMOutput out) throws IOException {
        SVGUtil.writeAttributes(this, out);
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
