/*
 * @(#)TextOverflowHandle.java  1.0  19. Mai 2007
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
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * The TextOverflowHandle indicates when the text does not fit into the
 * bounds of a TextAreaFigure.
 *
 * @author Werner Randelshofer
 * @version 1.0 19. Mai 2007 Created.
 */
public class TextOverflowHandle extends AbstractHandle {
    
    /** Creates a new instance. */
    public TextOverflowHandle(TextHolderFigure owner) {
        super(owner);
    }
    
    public TextHolderFigure getOwner() {
        return (TextHolderFigure) super.getOwner();
    }
    
    /**
     * Draws this handle.
     */
    @Override public void draw(Graphics2D g) {
        if (getOwner().isTextOverflow()) {
            g.setColor(Color.RED);
            Rectangle r = basicGetBounds();
            g.draw(r);
            g.drawLine(r.x, r.y, r.x+r.width, r.y+r.height);
            g.drawLine(r.x+r.width, r.y, r.x, r.y+r.height);
        }
    }
    
    @Override protected Rectangle basicGetBounds() {
        Rectangle2D.Double b = getOwner().getBounds();
        Point2D.Double p = new Point2D.Double(b.x + b.width, b.y  + b.height);
        if (TRANSFORM.get(getOwner()) != null) {
            TRANSFORM.get(getOwner()).transform(p, p);
        }
        Rectangle r = new Rectangle(view.drawingToView(p));
        r.x -= 6;
        r.y -= 6;
        r.width = r.height = 6;
        return r;
    }
    
    public void trackStart(Point anchor, int modifiersEx) {
    }
    
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
    }
    
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
    }
    
    @Override public String getToolTipText(Point p) {
        
        return (getOwner().isTextOverflow()) ?
            ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels").getString("textOverflowHandle.tip") :
            null;
    }
    
}
