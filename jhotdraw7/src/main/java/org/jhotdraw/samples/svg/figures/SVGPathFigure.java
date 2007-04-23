/*
 * @(#)SVGPath.java  1.0  July 8, 2006
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
import org.jhotdraw.draw.action.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.samples.svg.*;
import org.jhotdraw.samples.svg.SVGConstants;
import org.jhotdraw.util.*;
import org.jhotdraw.xml.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

/**
 * SVGPath is a composite Figure which contains one or more
 * SVGBezierFigures as its children.
 * <p>
 * XXX - Roll in the read() method of SVGLine.
 *
 * @author Werner Randelshofer
 * @version 1.0 July 8, 2006 Created.
 */
public class SVGPathFigure extends AbstractAttributedCompositeFigure implements SVGFigure {
    /**
     * This path is used for drawing.
     */
    private GeneralPath path;
    
    private final static boolean DEBUG = false;
    
    /** Creates a new instance. */
    public SVGPathFigure() {
        add(new SVGBezierFigure());
        SVGAttributeKeys.setDefaults(this);
    }
    
    
    public void draw(Graphics2D g) {
        validatePath();
        Paint paint = SVGAttributeKeys.getFillPaint(this);
        if (paint != null) {
            g.setPaint(paint);
            drawFill(g);
        }
        paint = SVGAttributeKeys.getStrokePaint(this);
        if (paint != null) {
            g.setPaint(paint);
            g.setStroke(SVGAttributeKeys.getStroke(this));
            drawStroke(g);
        }
        if (isConnectorsVisible()) {
            drawConnectors(g);
        }
        
        /*
        // DEBUGGING STUFF
        g.setStroke(new BasicStroke());
        g.setColor(Color.blue);
 double grow = AttributeKeys.getPerpendicularHitGrowth(this);
 GrowStroke gs = new GrowStroke((float) grow,
                (float) (AttributeKeys.getStrokeTotalWidth(this) *
                STROKE_MITER_LIMIT.get(this))
                );
        for (Figure child : getChildren()) {
            SVGBezierFigure b = (SVGBezierFigure) child;
            g.draw(gs.createStrokedShape(b.getBezierPath()));
        }*/
    }
    
    public void drawFill(Graphics2D g) {
        g.fill(path);
    }
    public void drawStroke(Graphics2D g) {
        g.draw(path);
    }
    
    public void invalidate() {
        super.invalidate();
        invalidatePath();
    }
    
    protected void validate() {
        validatePath();
        super.validate();
    }
    
    protected void validatePath() {
        if (path == null) {
            path = new GeneralPath();
            path.setWindingRule(WINDING_RULE.get(this) == WindingRule.EVEN_ODD ?
                GeneralPath.WIND_EVEN_ODD :
                GeneralPath.WIND_NON_ZERO
                    );
            for (Figure child : getChildren()) {
                SVGBezierFigure b = (SVGBezierFigure) child;
                path.append(b.getBezierPath(), false);
            }
        }
    }
    protected void invalidatePath() {
        path = null;
    }
    
    @Override final public void write(DOMOutput out) throws IOException {
        throw new UnsupportedOperationException("Use SVGStorableOutput to write this Figure.");
    }
    @Override final public void read(DOMInput in) throws IOException {
        throw new UnsupportedOperationException("Use SVGStorableInput to read this Figure.");
    }
    
    public void basicSetBounds(Point2D.Double anchor, Point2D.Double lead) {
        if (getChildCount() == 1 && ((SVGBezierFigure) getChild(0)).getNodeCount() <= 2) {
            SVGBezierFigure b = (SVGBezierFigure) getChild(0);
            b.basicSetBounds(anchor, lead);
            invalidate();
        } else {
            super.basicSetBounds(anchor, lead);
        }
    }
    public void basicTransform(AffineTransform tx) {
        super.basicTransform(tx);
        invalidatePath();
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
    public void restoreTransformTo(Object geometry) {
        Object[] restoreData = (Object[]) geometry;
        super.restoreTransformTo(restoreData[0]);
        FILL_GRADIENT.basicSetClone(this, (Gradient) restoreData[1]);
        STROKE_GRADIENT.basicSetClone(this, (Gradient) restoreData[2]);
    }
    
    public Object getTransformRestoreData() {
        return new Object[] {
            super.getTransformRestoreData(),
            FILL_GRADIENT.getClone(this),
            STROKE_GRADIENT.getClone(this),
        };
    }
    
    public boolean isEmpty() {
        for (Figure child : getChildren()) {
            SVGBezierFigure b = (SVGBezierFigure) child;
            if (b.getPointCount() > 0) {
                return false;
            }
        }
        return true;
    }
    
    @Override public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = new LinkedList<Handle>();
        switch (detailLevel % 2) {
            case 0 :
                for (Figure child : getChildren()) {
                    handles.addAll(child.createHandles(detailLevel));
                }
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
        actions.add(new AbstractAction(labels.getString("closePath")) {
            public void actionPerformed(ActionEvent evt) {
                for (Figure child : getChildren()) {
                    SVGBezierFigure b = (SVGBezierFigure) child;
                    b.setClosed(true);
                }
            }
        });
        actions.add(new AbstractAction(labels.getString("openPath")) {
            public void actionPerformed(ActionEvent evt) {
                for (Figure child : getChildren()) {
                    SVGBezierFigure b = (SVGBezierFigure) child;
                    b.setClosed(false);
                }
            }
        });
        actions.add(new AbstractAction(labels.getString("windingEvenOdd")) {
            public void actionPerformed(ActionEvent evt) {
                WINDING_RULE.set(SVGPathFigure.this, WindingRule.EVEN_ODD);
            }
        });
        actions.add(new AbstractAction(labels.getString("windingNonZero")) {
            public void actionPerformed(ActionEvent evt) {
                WINDING_RULE.set(SVGPathFigure.this, WindingRule.NON_ZERO);
            }
        });
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
    /**
     * Handles a mouse click.
     */
    @Override public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
        if (evt.getClickCount() == 2 && view.getHandleDetailLevel() % 2 == 0) {
            for (Figure child : getChildren()) {
                SVGBezierFigure bf = (SVGBezierFigure) child;
                int index = bf.getBezierPath().findSegment(p, (float) (5f / view.getScaleFactor()));
                if (index != -1) {
                    bf.handleMouseClick(p, evt, view);
                    evt.consume();
                    return true;
                }
            }
        }
        return false;
    }
    
    public void basicSetAttribute(AttributeKey key, Object newValue) {
        if (key == SVGAttributeKeys.TRANSFORM) {
            basicTransform((AffineTransform) newValue);
        } else {
            super.basicSetAttribute(key, newValue);
        }
        // invalidatePath();
    }
    
    @Override public void add(final int index, final Figure figure) {
        super.add(index, (SVGBezierFigure) figure);
    }
    
    @Override public SVGBezierFigure getChild(int index) {
        return (SVGBezierFigure) super.getChild(index);
    }
    
}
