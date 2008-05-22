/*
 * @(#)SVGPathOutlineHandle.java  2.0  2008-05-22
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
package org.jhotdraw.samples.svg.figures;

import org.jhotdraw.draw.*;
import java.awt.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

/**
 * SVGPathOutlineHandle.
 *
 * @author Werner Randelshofer
 * @version 2.0 2008-05-22 Handle can be used to mark figure over which mouse is
 * hovering.
 * <br>1.1 2008-04-12 Improve visibility of the outline, by drawing it
 * using two differently colored strokes. 
 * <br>1.0 13. Mai 2007 Created.
 */
public class SVGPathOutlineHandle extends AbstractHandle {

    /**
     * Set this to true, if the handle is used for marking a figure over
     * which the mouse pointer is hovering.
     */
    private boolean isHoverHandle = false;

    /** Creates a new instance. */
    public SVGPathOutlineHandle(SVGPathFigure owner) {
        this(owner, false);
    }

    /** Creates a new instance. */
    public SVGPathOutlineHandle(SVGPathFigure owner, boolean isHoverHandle) {
        super(owner);
        this.isHoverHandle = isHoverHandle;
    }

    public SVGPathFigure getOwner() {
        return (SVGPathFigure) super.getOwner();
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
        Shape bounds = getOwner().getPath();
        if (TRANSFORM.get(getOwner()) != null) {
            bounds = TRANSFORM.get(getOwner()).createTransformedShape(bounds);
        }
        bounds = view.getDrawingToViewTransform().createTransformedShape(bounds);
        Stroke stroke;
        Color strokeColor;
        if (isHoverHandle) {
            stroke = (Stroke) getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_PATH_HOVER_STROKE_1);
            strokeColor = (Color) getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_PATH_HOVER_COLOR_1);
        } else {
            stroke = (Stroke) getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_PATH_STROKE_1);
            strokeColor = (Color) getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_PATH_COLOR_1);
        }
        if (stroke != null && strokeColor != null) {
            g.setStroke(stroke);
            g.setColor(strokeColor);
            g.draw(bounds);
        }
        if (isHoverHandle) {
        stroke = (Stroke) getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_PATH_HOVER_STROKE_2);
        strokeColor = (Color) getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_PATH_HOVER_COLOR_2);
        } else {
        stroke = (Stroke) getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_PATH_STROKE_2);
        strokeColor = (Color) getEditor().getHandleAttribute(HandleAttributeKeys.BEZIER_PATH_COLOR_2);
        }
        if (stroke != null && strokeColor != null) {
            g.setStroke(stroke);
            g.setColor(strokeColor);
            g.draw(bounds);
        }
    }
}
