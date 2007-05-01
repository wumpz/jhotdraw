/*
 * @(#)SVGText.java  2.0  2007-04-14
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
import java.awt.font.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.samples.svg.*;
import org.jhotdraw.samples.svg.SVGConstants;
import org.jhotdraw.util.*;
import org.jhotdraw.xml.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;
/**
 * SVGText.
 * <p>
 * FIXME - Add support for transforms.
 * XXX At least on Mac OS X - Always draw text using TextLayout.getOutline(),
 * because outline layout does not match with TextLayout.draw() output.
 * Cache outline to improve performance.
 *
 * @author Werner Randelshofer
 * @version 2.0 2007-04-14 Adapted for new AttributeKeys.TRANSFORM support.
 * <br>1.0 July 8, 2006 Created.
 */
public class SVGTextFigure
        extends SVGAttributedFigure
        implements TextHolderFigure, SVGFigure {
    
    protected Point2D.Double[] coordinates = new Point2D.Double[] { new Point2D.Double() };
    protected double[] rotates = new double[] { 0 };
    private boolean editable = true;
    
    /**
     * This is used to perform faster drawing and hit testing.
     */
    private Shape cachedTransformedShape;
    private Shape cachedTransformedBounds;
    private Rectangle2D.Double cachedBounds;
    
    /** Creates a new instance. */
    public SVGTextFigure() {
        this("Text");
    }
    public SVGTextFigure(String text) {
        setText(text);
        SVGAttributeKeys.setDefaults(this);
    }
    
    // DRAWING
    protected void drawText(java.awt.Graphics2D g) {
    }
    protected void drawFill(Graphics2D g) {
        g.fill(getTransformedShape());
    }
    
    protected void drawStroke(Graphics2D g) {
        g.draw(getTransformedShape());
    }
    
    // SHAPE AND BOUNDS
    public void basicSetCoordinates(Point2D.Double[] coordinates) {
        this.coordinates = coordinates;
        // invalidate();
    }
    
    public Point2D.Double[] getCoordinates() {
        Point2D.Double[] c = new Point2D.Double[coordinates.length];
        for (int i=0; i < c.length; i++) {
            c[i] = (Point2D.Double) coordinates[i].clone();
        }
        return c;
    }
    
    public void basicSetRotates(double[] rotates) {
        this.rotates = rotates;
        // invalidate();
    }
    public double[] getRotates() {
        return (double[]) rotates.clone();
    }
    
    public Rectangle2D.Double getBounds() {
        if (cachedBounds == null) {
            String text = getText();
            if (text == null || text.length() == 0) {
                text = " ";
            }
            
            FontRenderContext frc = getFontRenderContext();
            HashMap<TextAttribute,Object> textAttributes = new HashMap<TextAttribute,Object>();
            textAttributes.put(TextAttribute.FONT, getFont());
            if (FONT_UNDERLINED.get(this)) {
                textAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
            }
            TextLayout textLayout = new TextLayout(text, textAttributes, frc);
            
            Rectangle2D r = textLayout.getBounds();
            cachedBounds = new Rectangle2D.Double(
                    coordinates[0].x + r.getX(), coordinates[0].y + r.getY(),
                    r.getWidth(), r.getHeight());
            switch (TEXT_ANCHOR.get(this)) {
                case END :
                    cachedBounds.x -=textLayout.getAdvance();
                    break;
                case MIDDLE :
                    cachedBounds.x -=textLayout.getAdvance() / 2d;
                    break;
                case START :
                    break;
            }
        }
        return (Rectangle2D.Double) cachedBounds.clone();
    }
    @Override public Rectangle2D.Double getDrawingArea() {
        Rectangle2D rx = getTransformedShape().getBounds2D();
        Rectangle2D.Double r = (rx instanceof Rectangle2D.Double) ? (Rectangle2D.Double) rx : new Rectangle2D.Double(rx.getX(), rx.getY(), rx.getWidth(), rx.getHeight());
        double g = SVGAttributeKeys.getPerpendicularHitGrowth(this);
        Geom.grow(r, g, g);
        return r;
    }
    /**
     * Checks if a Point2D.Double is inside the figure.
     */
    public boolean contains(Point2D.Double p) {
        return getTransformedShape().getBounds2D().contains(p);
    }
    private void invalidateTransformedShape() {
        cachedTransformedShape = null;
        cachedBounds = null;
    }
    
    private Shape getTransformedShape() {
        if (cachedTransformedShape == null) {
            String text = getText();
            if (text == null || text.length() == 0) {
                text = " ";
            }
            
            FontRenderContext frc = getFontRenderContext();
            HashMap<TextAttribute,Object> textAttributes = new HashMap<TextAttribute,Object>();
            textAttributes.put(TextAttribute.FONT, getFont());
            if (FONT_UNDERLINED.get(this)) {
                textAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
            }
            TextLayout textLayout = new TextLayout(text, textAttributes, frc);
            
            AffineTransform tx = new AffineTransform();
            tx.translate(coordinates[0].x, coordinates[0].y);
            switch (TEXT_ANCHOR.get(this)) {
                case END :
                    tx.translate(-textLayout.getAdvance(), 0);
                    break;
                case MIDDLE :
                    tx.translate(-textLayout.getAdvance() / 2d, 0);
                    break;
                case START :
                    break;
            }
            tx.rotate(rotates[0]);
            
            if (TRANSFORM.get(this) != null) {
                tx.preConcatenate(TRANSFORM.get(this));
            }
            
            cachedTransformedShape = tx.createTransformedShape(textLayout.getOutline(tx));
            cachedTransformedShape = textLayout.getOutline(tx);
        }
        return cachedTransformedShape;
    }
    
    public void basicSetBounds(Point2D.Double anchor, Point2D.Double lead) {
        coordinates = new Point2D.Double[] {
            new Point2D.Double(anchor.x, anchor.y)
        };
        rotates = new double[] { 0d };
    }
    /**
     * Transforms the figure.
     *
     * @param tx the transformation.
     */
    public void basicTransform(AffineTransform tx) {
        invalidateTransformedShape();
        if (TRANSFORM.get(this) != null ||
                (tx.getType() &
                (AffineTransform.TYPE_TRANSLATION /*| AffineTransform.TYPE_MASK_SCALE*/)) !=
                tx.getType()) {
            if (TRANSFORM.get(this) == null) {
                TRANSFORM.basicSet(this, (AffineTransform) tx.clone());
            } else {
                AffineTransform t = TRANSFORM.getClone(this);
                t.preConcatenate(tx);
                TRANSFORM.basicSet(this, t);
            }
        } else {
            for (int i=0; i < coordinates.length; i++) {
                tx.transform(coordinates[i], coordinates[i]);
            }
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
    public void restoreTransformTo(Object geometry) {
        Object[] restoreData = (Object[]) geometry;
        TRANSFORM.basicSetClone(this, (AffineTransform) restoreData[0]);
        FILL_GRADIENT.basicSetClone(this, (Gradient) restoreData[1]);
        STROKE_GRADIENT.basicSetClone(this, (Gradient) restoreData[2]);
    }
    
    public Object getTransformRestoreData() {
        return new Object[] {
            TRANSFORM.getClone(this),
            FILL_GRADIENT.getClone(this),
            STROKE_GRADIENT.getClone(this),
        };
    }
    
    // ATTRIBUTES
    /**
     * Gets the text shown by the text figure.
     */
    public String getText() {
        return (String) getAttribute(TEXT);
    }
    public void basicSetAttribute(AttributeKey key, Object newValue) {
        if (key == SVGAttributeKeys.TRANSFORM) {
            invalidateTransformedShape();
        }
        super.basicSetAttribute(key, newValue);
    }
    
    /**
     * Sets the text shown by the text figure.
     */
    public void setText(String newText) {
        setAttribute(TEXT, newText);
    }
    /**
     * Sets the text shown by the text figure without firing events.
     */
    public void basicSetText(String newText) {
        basicSetAttribute(TEXT, newText);
    }
    public boolean isEditable() {
        return editable;
    }
    public void setEditable(boolean b) {
        this.editable = b;
    }
    
    public int getTextColumns() {
        return (getText() == null) ? 4 : Math.max(getText().length(), 4);
    }
    
    public Font getFont() {
        return SVGAttributeKeys.getFont(this);
    }
    
    public Color getTextColor() {
        return FILL_COLOR.get(this);
        //   return TEXT_COLOR.get(this);
    }
    
    public Color getFillColor() {
        return FILL_COLOR.get(this).equals(Color.white) ? Color.black : Color.WHITE;
        //  return FILL_COLOR.get(this);
    }
    
    public void setFontSize(float size) {
        // FONT_SIZE.set(this, new Double(size));
        Point2D.Double p = new Point2D.Double(0, size);
        AffineTransform tx =  TRANSFORM.get(this);
        if (tx != null) {
            try {
                tx.inverseTransform(p, p);
                Point2D.Double p0 = new Point2D.Double(0, 0);
                tx.inverseTransform(p0, p0);
                p.y -= p0.y;
            } catch (NoninvertibleTransformException ex) {
                ex.printStackTrace();
            }
        }
        FONT_SIZE.set(this, Math.abs(p.y));
    }
    
    public float getFontSize() {
        //   return FONT_SIZE.get(this).floatValue();
        Point2D.Double p = new Point2D.Double(0, FONT_SIZE.get(this));
        AffineTransform tx =  TRANSFORM.get(this);
        if (tx != null) {
            tx.transform(p, p);
            Point2D.Double p0 = new Point2D.Double(0, 0);
            tx.transform(p0, p0);
            p.y -= p0.y;
                /*
            try {
                tx.inverseTransform(p, p);
            } catch (NoninvertibleTransformException ex) {
                ex.printStackTrace();
            }*/
        }
        return (float) Math.abs(p.y);
    }
    
    // EDITING
    // CONNECTING
    
    
    @Override public void invalidate() {
        super.invalidate();
        invalidateTransformedShape();
    }
    public Dimension2DDouble getPreferredSize() {
        Rectangle2D.Double b = getBounds();
        return new Dimension2DDouble(b.width, b.height);
    }
    
    public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = new LinkedList<Handle>();
        switch (detailLevel % 2) {
            case 0 :
                handles.add(new BoundsOutlineHandle(this));
                handles.add(new MoveHandle(this, RelativeLocator.northWest()));
                handles.add(new MoveHandle(this, RelativeLocator.northEast()));
                handles.add(new MoveHandle(this, RelativeLocator.southWest()));
                handles.add(new MoveHandle(this, RelativeLocator.southEast()));
                handles.add(new FontSizeHandle(this));
                break;
            case 1 :
                TransformHandleKit.addTransformHandles(this, handles);
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
                    TRANSFORM.set(SVGTextFigure.this, null);
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
    
    /**
     * Returns a specialized tool for the given coordinate.
     * <p>Returns null, if no specialized tool is available.
     */
    public Tool getTool(Point2D.Double p) {
        return (isEditable() && contains(p)) ? new TextTool(this) : null;
    }
    
    /**
     * Gets the number of characters used to expand tabs.
     */
    public int getTabSize() {
        return 8;
    }
    
    public TextHolderFigure getLabelFor() {
        return this;
    }
    
    public Insets2D.Double getInsets() {
        return new Insets2D.Double();
    }
    
    public SVGTextFigure clone() {
        SVGTextFigure that = (SVGTextFigure) super.clone();
        that.coordinates = (Point2D.Double[]) this.coordinates.clone();
        for (int i=0; i < this.coordinates.length; i++) {
            that.coordinates[i] = (Point2D.Double) this.coordinates[i].clone();
        }
        that.rotates = (double[]) this.rotates.clone();
        that.cachedBounds = null;
        that.cachedTransformedBounds = null;
        that.cachedTransformedShape = null;
        return that;
    }
    
    public boolean isEmpty() {
        return getText() == null || getText().length() == 0;
    }
}
