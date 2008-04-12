/*
 * @(#)SVGPathOutlineHandle.java  1.1  2008-04-12
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
 * @version 1.1 2008-04-12 Improve visibility of the outline, by drawing it
 * using two differently colored strokes. 
 * <br>1.0 13. Mai 2007 Created.
 */
public class SVGPathOutlineHandle extends AbstractHandle {
    private final static BasicStroke HANDLE_STROKE1 = new BasicStroke(
            2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL
            );
    private final static Color HANDLE_STROKE_COLOR1 = Color.WHITE;
    private final static BasicStroke HANDLE_STROKE2 = new BasicStroke(
            2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[] { 6f, 6f }, 0f
            );
    private final static Color HANDLE_STROKE_COLOR2 = new Color(0x00a8ff);
     
    /** Creates a new instance. */
    public SVGPathOutlineHandle(SVGPathFigure owner) {
        super(owner);
    }
    
    public SVGPathFigure getOwner() {
        return (SVGPathFigure) super.getOwner();
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
        Shape bounds = getOwner().getPath();
        if (TRANSFORM.get(getOwner()) != null) {
            bounds = TRANSFORM.get(getOwner()).createTransformedShape(bounds);
        }
        bounds = view.getDrawingToViewTransform().createTransformedShape(bounds);
        g.setStroke(HANDLE_STROKE1);
        g.setColor(HANDLE_STROKE_COLOR1);
        g.draw(bounds);
        g.setStroke(HANDLE_STROKE2);
        g.setColor(HANDLE_STROKE_COLOR2);
        g.draw(bounds);
    }
}
