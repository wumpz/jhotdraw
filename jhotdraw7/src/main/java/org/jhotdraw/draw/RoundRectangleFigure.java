/*
 * @(#)RoundRectangleFigure.java  2.2  2006-06-17
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


package org.jhotdraw.draw;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import javax.swing.undo.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
/**
 * A Rectangle2D.Double with round corners.
 *
 * @author Werner Randelshofer
 * @version 2.2 2006-06-17 Method chop added.
 * 2.1 2006-05-29 Method basicSetBounds did not work for bounds smaller
 * than 1 pixel.
 * <br>2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 2004-03-02 Derived from JHotDraw 6.0b1.
 */
public class RoundRectangleFigure extends AttributedFigure {
    private RoundRectangle2D.Double roundrect;
    private static final double DEFAULT_ARC = 20;
    
    /** Creates a new instance. */
    public RoundRectangleFigure() {
        this(0, 0, 0, 0);
    }
    
    public RoundRectangleFigure(double x, double y, double width, double height) {
        roundrect = new RoundRectangle2D.Double(x, y, width, height, DEFAULT_ARC, DEFAULT_ARC);
        /*
        FILL_COLOR.set(this, Color.white);
        STROKE_COLOR.set(this, Color.black);
         */
    }
    
    public Rectangle2D.Double getBounds() {
        return (Rectangle2D.Double) roundrect.getBounds2D();
    }
    public Rectangle2D.Double getFigureDrawBounds() {
        Rectangle2D.Double r = (Rectangle2D.Double) roundrect.getBounds2D();
            double grow = AttributeKeys.getPerpendicularHitGrowth(this);
            Geom.grow(r, grow, grow);
            
        return r;
    }
    
    public double getArcWidth() {
        return roundrect.arcwidth;
    }
    public double getArcHeight() {
        return roundrect.archeight;
    }
    public void setArc(final double w, final double h) {
        final double oldWidth = roundrect.getArcWidth();
        final double oldHeight = roundrect.getArcHeight();
        roundrect.arcwidth = w;
        roundrect.archeight = h;
        fireFigureChanged(getDrawBounds());
        fireUndoableEditHappened(new AbstractUndoableEdit() {
            public String getPresentationName() {
                return "Rundung";
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
                roundrect.arcwidth = w;
                roundrect.archeight = h;
                changed();
            }
        });
    }
    
    protected void drawFill(Graphics2D g) {
        RoundRectangle2D.Double r = (RoundRectangle2D.Double) roundrect.clone();
            double grow = AttributeKeys.getPerpendicularFillGrowth(this);
            r.x -= grow;
            r.y -= grow;
            r.width += grow * 2;
            r.height += grow * 2;
        r.arcwidth += grow * 2;
        r.archeight += grow * 2;
        if (r.width > 0 && r.height > 0) {
        g.fill(r);
        }
    }
    
    protected void drawStroke(Graphics2D g) {
        RoundRectangle2D.Double r = (RoundRectangle2D.Double) roundrect.clone();
            double grow = AttributeKeys.getPerpendicularDrawGrowth(this);
            r.x -= grow;
            r.y -= grow;
            r.width += grow * 2;
            r.height += grow * 2;
        r.arcwidth += grow * 2;
        r.archeight += grow * 2;
        if (r.width > 0 && r.height > 0) {
        g.draw(r);
        }
    }
    
    /**
     * Checks if a Point2D.Double is inside the figure.
     */
    public boolean contains(Point2D.Double p) {
        RoundRectangle2D.Double r = (RoundRectangle2D.Double) roundrect.clone();
            double grow = AttributeKeys.getPerpendicularHitGrowth(this);
            r.x -= grow;
            r.y -= grow;
            r.width += grow * 2;
            r.height += grow * 2;
        r.arcwidth += grow * 2;
        r.archeight += grow * 2;
        return r.contains(p);
    }
    public void basicSetBounds(Point2D.Double anchor, Point2D.Double lead) {
        roundrect.x = Math.min(anchor.x, lead.x);
        roundrect.y = Math.min(anchor.y , lead.y);
        roundrect.width = Math.max(0.1, Math.abs(lead.x - anchor.x));
        roundrect.height = Math.max(0.1, Math.abs(lead.y - anchor.y));
    }
    /**
     * Transforms the figure.
     * @param tx The transformation.
     */
    public void basicTransform(AffineTransform tx) {
        Point2D.Double anchor = getStartPoint();
        Point2D.Double lead = getEndPoint();
        basicSetBounds(
                (Point2D.Double) tx.transform(anchor, anchor),
                (Point2D.Double) tx.transform(lead, lead)
                );
    }
    
    public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = (LinkedList<Handle>) super.createHandles(detailLevel);
        handles.add(new RoundRectRadiusHandle(this));
        
        return handles;
    }
    
    public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
        return new ChopRoundRectConnector(this);
    }
    public Connector findCompatibleConnector(Connector c, boolean isStartConnector) {
        return new ChopRoundRectConnector(this);
    }

    public RoundRectangleFigure clone() {
        RoundRectangleFigure that = (RoundRectangleFigure) super.clone();
        that.roundrect = (RoundRectangle2D.Double) this.roundrect.clone();
        return that;
    }
    
   @Override public void read(DOMInput in) throws IOException {
        super.read(in);
        roundrect.arcwidth = in.getAttribute("arcWidth", DEFAULT_ARC);
        roundrect.archeight = in.getAttribute("arcHeight", DEFAULT_ARC);
    }
    
    @Override public void write(DOMOutput out) throws IOException {
        super.write(out);
        out.addAttribute("arcWidth", roundrect.arcwidth);
        out.addAttribute("arcHeight", roundrect.archeight);
    }
    public void restoreTo(Object geometry) {
        RoundRectangle2D.Double r = (RoundRectangle2D.Double) geometry;
        roundrect.x = r.x;
        roundrect.y = r.y;
        roundrect.width = r.width;
        roundrect.height = r.height;
    }
    
    public Object getRestoreData() {
        return roundrect.clone();
    }
    public Point2D.Double chop(Point2D.Double from) {
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
}
