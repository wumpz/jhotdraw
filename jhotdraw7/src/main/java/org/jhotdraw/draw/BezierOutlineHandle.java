/*
 * @(#)BezierOutlineHandle.java  1.0  April 14, 2007
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
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

/**
 * Draws the outlines of a BezierFigure to make adjustment easier.
 *
 * @author Werner Randelshofer
 * @version 1.0 April 14, 2007 Created.
 */
public class BezierOutlineHandle extends AbstractHandle {
    private final static Color HANDLE_FILL_COLOR = new Color(0x00a8ff);
    private final static Color HANDLE_STROKE_COLOR = Color.WHITE;
    
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
        Shape bounds = getOwner().getBezierPath();
        if (AttributeKeys.TRANSFORM.get(getOwner()) != null) {
            bounds = AttributeKeys.TRANSFORM.get(getOwner()).createTransformedShape(bounds);
        }
        bounds = view.getDrawingToViewTransform().createTransformedShape(bounds);
        g.setColor(HANDLE_FILL_COLOR);
        g.draw(bounds);
    }
}
