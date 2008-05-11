/*
 * @(#)BezierOutlineHandle.java  2.0  2008-05-11
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

/**
 * Draws the outlines of a BezierFigure to make adjustment easier.
 *
 * @author Werner Randelshofer
 * @version 2008-05-11 Handle attributes are now retrieved from
 * DrawingEditor. 
 * <br>1.1 2008-04-12 Improve visibility of the outline, by drawing it
 * using two differently colored strokes. 
 * <br>1.0 April 14, 2007 Created.
 */
public class BezierOutlineHandle extends AbstractHandle {

    /** Creates a new instance. */
    public BezierOutlineHandle(BezierFigure owner) {
        super(owner);
    }

    public BezierFigure getOwner() {
        return (BezierFigure) super.getOwner();
    }

    protected Rectangle basicGetBounds() {
        return view.drawingToView(getOwner().getDrawingArea());
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
        Shape bounds = getOwner().getBezierPath();
        if (AttributeKeys.TRANSFORM.get(getOwner()) != null) {
            bounds = AttributeKeys.TRANSFORM.get(getOwner()).createTransformedShape(bounds);
        }
        bounds = view.getDrawingToViewTransform().createTransformedShape(bounds);
        Stroke stroke = (Stroke) getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_OUTLINE_HANDLE_STROKE_1);
        Color strokeColor = (Color) getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_OUTLINE_HANDLE_STROKE_COLOR_1);
        if (stroke != null && strokeColor != null) {
            g.setStroke(stroke);
            g.setColor(strokeColor);
            g.draw(bounds);
        }
         stroke = (Stroke) getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_OUTLINE_HANDLE_STROKE_2);
         strokeColor = (Color) getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_OUTLINE_HANDLE_STROKE_COLOR_2);
        if (stroke != null && strokeColor != null) {
            g.setStroke(stroke);
            g.setColor(strokeColor);
            g.draw(bounds);
        }
    }
}
