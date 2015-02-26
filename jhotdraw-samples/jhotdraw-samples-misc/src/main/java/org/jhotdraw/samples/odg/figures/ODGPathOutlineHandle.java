/*
 * @(#)ODGPathOutlineHandle.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.samples.odg.figures;

import org.jhotdraw.draw.handle.AbstractHandle;
import java.awt.*;
import static org.jhotdraw.samples.odg.ODGAttributeKeys.*;

/**
 * A non-interactive {@link org.jhotdraw.draw.handle.Handle} which draws the outline of a
 * {@link ODGPathFigure} to make adjustments easier.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ODGPathOutlineHandle extends AbstractHandle {
    private static final Color HANDLE_FILL_COLOR = new Color(0x00a8ff);
    private static final Color HANDLE_STROKE_COLOR = Color.WHITE;
    
    /** Creates a new instance. */
    public ODGPathOutlineHandle(ODGPathFigure owner) {
        super(owner);
    }
    
    @Override
    public ODGPathFigure getOwner() {
        return (ODGPathFigure) super.getOwner();
    }
    
    @Override
    protected Rectangle basicGetBounds() {
        return view.drawingToView(getOwner().getDrawingArea());
    }
    @Override public boolean contains(Point p) {
        return false;
    }
    
    @Override
    public void trackStart(Point anchor, int modifiersEx) {
    }
    
    @Override
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
    }
    
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
    }
    
    @Override public void draw(Graphics2D g) {
        Shape bounds = getOwner().getPath();
        if (getOwner().get(TRANSFORM) != null) {
            bounds = getOwner().get(TRANSFORM).createTransformedShape(bounds);
        }
        bounds = view.getDrawingToViewTransform().createTransformedShape(bounds);
        g.setColor(HANDLE_FILL_COLOR);
        g.draw(bounds);
    }
}
