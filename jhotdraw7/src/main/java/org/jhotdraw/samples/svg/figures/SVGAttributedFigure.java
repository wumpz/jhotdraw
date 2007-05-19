/*
 * @(#)SVGAttributedFigure.java  1.0  December 10, 2006
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

import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import org.jhotdraw.draw.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.io.*;
import org.jhotdraw.samples.svg.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.util.*;
import org.jhotdraw.xml.*;
/**
 * SVGAttributedFigure.
 *
 * @author Werner Randelshofer
 * @version 1.0 December 10, 2006 Created.
 */
public abstract class SVGAttributedFigure extends AbstractAttributedFigure {
    
    /** Creates a new instance. */
    public SVGAttributedFigure() {
    }
    
    public void draw(Graphics2D g)  {
        double opacity = OPACITY.get(this);
        opacity = Math.min(Math.max(0d, opacity), 1d);
        if (opacity != 0d) {
            if (opacity != 1d) {
                Rectangle2D.Double drawingArea = getDrawingArea();
                
                Rectangle2D clipBounds = g.getClipBounds();
                if (clipBounds != null) {
                    Rectangle2D.intersect(drawingArea, clipBounds, drawingArea);
                }
                
                if (! drawingArea.isEmpty()) {
                    
                    BufferedImage buf = new BufferedImage(
                            (int) ((2 + drawingArea.width) * g.getTransform().getScaleX()),
                            (int) ((2 + drawingArea.height) * g.getTransform().getScaleY()),
                            BufferedImage.TYPE_INT_ARGB);
                    Graphics2D gr = buf.createGraphics();
                    gr.scale(g.getTransform().getScaleX(), g.getTransform().getScaleY());
                    gr.translate((int) -drawingArea.x, (int) -drawingArea.y);
                    gr.setRenderingHints(g.getRenderingHints());
                    drawFigure(gr);
                    gr.dispose();
                    Composite savedComposite = g.getComposite();
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity));
                    g.drawImage(buf, (int) drawingArea.x, (int) drawingArea.y,
                            2 + (int) drawingArea.width, 2 + (int) drawingArea.height, null);
                    g.setComposite(savedComposite);
                }
            } else {
                drawFigure(g);
            }
        }
    }
    
    /**
     * This method is invoked before the rendered image of the figure is
     * composited.
     */
    public void drawFigure(Graphics2D g) {
        AffineTransform savedTransform = null;
        if (TRANSFORM.get(this) != null) {
            savedTransform = g.getTransform();
            g.transform(TRANSFORM.get(this));
        }
        
        Paint paint = SVGAttributeKeys.getFillPaint(this);
        if (paint != null) {
            g.setPaint(paint);
            drawFill(g);
        }
        paint = SVGAttributeKeys.getStrokePaint(this);
        if (paint != null && STROKE_WIDTH.get(this) > 0) {
            g.setPaint(paint);
            g.setStroke(SVGAttributeKeys.getStroke(this));
            drawStroke(g);
        }
        if (TRANSFORM.get(this) != null) {
            g.setTransform(savedTransform);
        }
    }
    public void setAttribute(AttributeKey key, Object newValue) {
        if (key == TRANSFORM) {
            invalidate();
        }
        super.setAttribute(key, newValue);
    }
    @Override public Collection<Action> getActions(Point2D.Double p) {
        LinkedList<Action> actions = new LinkedList<Action>();
        if (TRANSFORM.get(this) != null) {
            ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.svg.Labels");
            actions.add(new AbstractAction(labels.getString("removeTransform")) {
                public void actionPerformed(ActionEvent evt) {
                    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.svg.Labels");
                    SVGAttributedFigure.this.willChange();
                    fireUndoableEditHappened(
                            TRANSFORM.setUndoable(SVGAttributedFigure.this, null, labels)
                            );
                    SVGAttributedFigure.this.changed();
                }
            });
        }
        return actions;
    }
    @Override final public void write(DOMOutput out) throws IOException {
        throw new UnsupportedOperationException("Use SVGStorableOutput to write this Figure.");
    }
    @Override final public void read(DOMInput in) throws IOException {
        throw new UnsupportedOperationException("Use SVGStorableInput to read this Figure.");
    }
}
