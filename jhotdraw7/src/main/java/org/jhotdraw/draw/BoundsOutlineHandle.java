/*
 * @(#)BoundsOutlineHandle.java  1.0  April 15, 2007
 *
 * Copyright (c) 2007 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.draw;

import java.awt.*;
import java.awt.geom.*;
import static org.jhotdraw.draw.AttributeKeys.*;
/**
 * Draws the outline of the Figure bounds to make adjustment easier.
 *
 * @author Werner Randelshofer
 * @version 1.0 April 15, 2007 Created.
 */
public class BoundsOutlineHandle extends AbstractHandle {
    private final static Color HANDLE_STROKE_COLOR = new Color(0x00a8ff); //Color.WHITE;
    
    public BoundsOutlineHandle(Figure owner) {
        super(owner);
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
        g.setColor(HANDLE_STROKE_COLOR);
        g.draw(bounds);
    }
}