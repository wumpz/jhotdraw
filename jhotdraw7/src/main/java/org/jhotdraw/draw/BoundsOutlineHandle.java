/*
 * @(#)BoundsOutlineHandle.java  1.2  2008-04-15
 *
 * Copyright (c) 2007-2008 by the original authors of JHotDraw
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

import java.awt.*;
import java.awt.geom.*;
import static org.jhotdraw.draw.AttributeKeys.*;
/**
 * Draws the outline of the Figure bounds to make adjustment easier.
 *
 * @author Werner Randelshofer
 * @version 1.2 2008-04-15 Distinguish between bounds handle for resizing
 * and for transforming. 
 * <br>1.1 2008-04-12 Improve visibility of the outline, by drawing it
 * using two differently colored strokes. 
 * <br>1.0 April 15, 2007 Created.
 */
public class BoundsOutlineHandle extends AbstractHandle {
    /* XXX - In a future version all these styles should be properties of
     * the DrawingEditor (much like properties in javax.swing.UIManager).
     * So that we can have visually styled (skinned) drawing editors.
     */
    private final static BasicStroke HANDLE_STROKE1 = new BasicStroke(
            1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[] { 5f, 5f }, 5f
            );
    private final static Color HANDLE_STROKE_COLOR1 = Color.WHITE;
    private final static BasicStroke HANDLE_STROKE2 = new BasicStroke(
            1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[] { 5f, 5f }, 0f
            );
    private final static Color HANDLE_STROKE_COLOR2 = Color.BLUE;
    private final static Color HANDLE_STROKE_TRANSFORM_COLOR1 = Color.WHITE;
    private final static Color HANDLE_STROKE_TRANSFORM_COLOR2 = Color.BLACK;
    
    private Color strokeColor1;
    private Color strokeColor2;
    
    /**
     * Creates a bounds outline handle for resizing a component.
     * 
     * @param owner
     */
    public BoundsOutlineHandle(Figure owner) {
        this(owner, false);
    }
    
    /**
     * Creates a bounds outline handle for resizing or transforming a component.
     * 
     * @param owner
     */
    public BoundsOutlineHandle(Figure owner, boolean isTransformHandle) {
        super(owner);
        if (isTransformHandle) {
            strokeColor1 = HANDLE_STROKE_TRANSFORM_COLOR1;
            strokeColor2 = HANDLE_STROKE_TRANSFORM_COLOR2;
        } else {
            strokeColor1 = HANDLE_STROKE_COLOR1;
            strokeColor2 = HANDLE_STROKE_COLOR2;
        }
    }
    
    @Override protected Rectangle basicGetBounds() {
        Shape bounds = getOwner().getBounds();
        if (TRANSFORM.get(getOwner()) != null) {
            bounds = TRANSFORM.get(getOwner()).createTransformedShape(bounds);
        }
        bounds = view.getDrawingToViewTransform().createTransformedShape(bounds);
        Rectangle2D r = bounds.getBounds2D();
        return view.drawingToView(new Rectangle2D.Double(r.getX(), r.getY(),
                r.getWidth(), r.getHeight()));
    }
    @Override public boolean contains(Point p) {
        return false;
    }
    
    public void trackStart(Point anchor, int modifiersEx) {
    }
    
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
    }
    
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
    }
    
    @Override public void draw(Graphics2D g) {
        Shape bounds = getOwner().getBounds();
        if (AttributeKeys.TRANSFORM.get(getOwner()) != null) {
            bounds = AttributeKeys.TRANSFORM.get(getOwner()).createTransformedShape(bounds);
        }
        bounds = view.getDrawingToViewTransform().createTransformedShape(bounds);
        g.setStroke(HANDLE_STROKE1);
        g.setColor(strokeColor1);
        g.draw(bounds);
        g.setStroke(HANDLE_STROKE2);
        g.setColor(strokeColor2);
        g.draw(bounds);
    }
}