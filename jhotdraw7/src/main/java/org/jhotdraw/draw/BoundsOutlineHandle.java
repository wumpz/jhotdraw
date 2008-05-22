/*
 * @(#)BoundsOutlineHandle.java  3.0  2008-05-22
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
 * @version 3.0 2008-05-22 Added support for hover handle. 
 * <br>2.0 2008-05-11 Handle attributes are now retrieved from
 * DrawingEditor.
 * <br>1.2 2008-04-15 Distinguish between bounds handle for resizing
 * and for transforming. 
 * <br>1.1 2008-04-12 Improve visibility of the outline, by drawing it
 * using two differently colored strokes. 
 * <br>1.0 April 15, 2007 Created.
 */
public class BoundsOutlineHandle extends AbstractHandle {

    private boolean isTransformHandle;
    private boolean isHoverHandle;

    /**
     * Creates a bounds outline handle for resizing a component.
     * 
     * @param owner
     */
    public BoundsOutlineHandle(Figure owner) {
        this(owner, false, false);
    }

    /**
     * Creates a bounds outline handle for resizing or transforming a component.
     * 
     * @param owner
     */
    public BoundsOutlineHandle(Figure owner, boolean isTransformHandle, boolean isHoverHandle) {
        super(owner);
        this.isTransformHandle = isTransformHandle;
        this.isHoverHandle = isHoverHandle;
    }

    @Override
    protected Rectangle basicGetBounds() {
        Shape bounds = getOwner().getBounds();
        if (AttributeKeys.TRANSFORM.get(getOwner()) != null) {
            bounds = AttributeKeys.TRANSFORM.get(getOwner()).createTransformedShape(bounds);
        }
        bounds = view.getDrawingToViewTransform().createTransformedShape(bounds);

        Rectangle r = bounds.getBounds();
        r.grow(2,2);
        return r;
    }

    @Override
    public boolean contains(Point p) {
        return false;
    }

    public void trackStart(Point anchor, int modifiersEx) {
    }

    public void trackStep(Point anchor, Point lead, int modifiersEx) {
    }

    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
    }

    @Override
    public void draw(Graphics2D g) {
        Shape bounds = getOwner().getBounds();
        if (AttributeKeys.TRANSFORM.get(getOwner()) != null) {
            bounds = AttributeKeys.TRANSFORM.get(getOwner()).createTransformedShape(bounds);
        }
        bounds = view.getDrawingToViewTransform().createTransformedShape(bounds);
        Stroke stroke;
        Color strokeColor;
        if (isTransformHandle) {
            if (isHoverHandle) {
            stroke = (Stroke) getEditor().getHandleAttribute(HandleAttributeKeys.TRANSFORM_BOUNDS_HOVER_STROKE_1);
            strokeColor = (Color) getEditor().getHandleAttribute(HandleAttributeKeys.TRANSFORM_BOUNDS_HOVER_COLOR_1);
            } else {
            stroke = (Stroke) getEditor().getHandleAttribute(HandleAttributeKeys.TRANSFORM_BOUNDS_STROKE_1);
            strokeColor = (Color) getEditor().getHandleAttribute(HandleAttributeKeys.TRANSFORM_BOUNDS_COLOR_1);
            }
        } else {
            if (isHoverHandle) {
            stroke = (Stroke) getEditor().getHandleAttribute(HandleAttributeKeys.RESIZE_BOUNDS_HOVER_STROKE_1);
            strokeColor = (Color) getEditor().getHandleAttribute(HandleAttributeKeys.RESIZE_BOUNDS_HOVER_COLOR_1);
            } else {
            stroke = (Stroke) getEditor().getHandleAttribute(HandleAttributeKeys.RESIZE_BOUNDS_STROKE_1);
            strokeColor = (Color) getEditor().getHandleAttribute(HandleAttributeKeys.RESIZE_BOUNDS_COLOR_1);
            }
        }
        if (stroke != null && strokeColor != null) {
            g.setStroke(stroke);
            g.setColor(strokeColor);
            g.draw(bounds);
        }
        if (isTransformHandle) {
            if (isHoverHandle) {
            stroke = (Stroke) getEditor().getHandleAttribute(HandleAttributeKeys.TRANSFORM_BOUNDS_HOVER_STROKE_2);
            strokeColor = (Color) getEditor().getHandleAttribute(HandleAttributeKeys.TRANSFORM_BOUNDS_HOVER_COLOR_2);
            } else {
            stroke = (Stroke) getEditor().getHandleAttribute(HandleAttributeKeys.TRANSFORM_BOUNDS_STROKE_2);
            strokeColor = (Color) getEditor().getHandleAttribute(HandleAttributeKeys.TRANSFORM_BOUNDS_COLOR_2);
            }
        } else {
            if (isHoverHandle) {
            stroke = (Stroke) getEditor().getHandleAttribute(HandleAttributeKeys.RESIZE_BOUNDS_HOVER_STROKE_2);
            strokeColor = (Color) getEditor().getHandleAttribute(HandleAttributeKeys.RESIZE_BOUNDS_HOVER_COLOR_2);
            } else {
            stroke = (Stroke) getEditor().getHandleAttribute(HandleAttributeKeys.RESIZE_BOUNDS_STROKE_2);
            strokeColor = (Color) getEditor().getHandleAttribute(HandleAttributeKeys.RESIZE_BOUNDS_COLOR_2);
            }
        }
        if (stroke != null && strokeColor != null) {
            g.setStroke(stroke);
            g.setColor(strokeColor);
            g.draw(bounds);
        }
    }
}