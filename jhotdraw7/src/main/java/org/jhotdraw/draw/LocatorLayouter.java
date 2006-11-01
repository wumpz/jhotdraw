/*
 * @(#)LocatorLayouter.java  2.0  2006-01-14
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
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

import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
/**
 * A layouter which lays out all children of a CompositeFigure according to their
 * LayoutLocator property..
 * 
 * 
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 3. Februar 2004  Created.
 */
public class LocatorLayouter implements Layouter {
    /**
     * LayoutLocator property used by the children to specify their location
     * relative to the compositeFigure.
     */
    public final static AttributeKey<Locator> LAYOUT_LOCATOR = new AttributeKey<Locator>("layoutLocator",null);
    
    /** Creates a new instance. */
    public LocatorLayouter() {
    }
    
    public Rectangle2D.Double calculateLayout(CompositeFigure compositeFigure, Point2D.Double anchor, Point2D.Double lead) {
        Rectangle2D.Double bounds = null;
        
        for (Figure child : compositeFigure.getChildren()){
            Locator locator = getLocator(child);
            Rectangle2D.Double r;
            if (locator == null) {
                r = child.getBounds();
            } else {
                Point2D.Double p = locator.locate(compositeFigure);
                Dimension2DDouble d = child.getPreferredSize();
                r = new Rectangle2D.Double(p.x, p.y, d.width, d.height);
            }
            if (! r.isEmpty()) {
                if (bounds == null) {
                    bounds = r;
                } else {
                    bounds.add(r);
                }
            }
        }
        
        return (bounds == null) ? new Rectangle2D.Double() : bounds;
    }
    
    public Rectangle2D.Double layout(CompositeFigure compositeFigure, Point2D.Double anchor, Point2D.Double lead) {
        Rectangle2D.Double bounds = null;
        
        for (Figure child : compositeFigure.getChildren()) {
            Locator locator = getLocator(child);

            Rectangle2D.Double r;
            if (locator == null) {
                r = child.getBounds();
            } else {
                Point2D.Double p = locator.locate(compositeFigure, child);
                Dimension2DDouble d = child.getPreferredSize();
                r = new Rectangle2D.Double(p.x, p.y, d.width, d.height);
            }
            child.willChange();
            child.basicSetBounds(
            new Point2D.Double(r.getMinX(), r.getMinY()),
            new Point2D.Double(r.getMaxX(), r.getMaxY())
            );
            child.changed();
            if (! r.isEmpty()) {
                if (bounds == null) {
                    bounds = r;
                } else {
                    bounds.add(r);
                }
            }
        }
        
        return (bounds == null) ? new Rectangle2D.Double() : bounds;
    }
    
    private Locator getLocator(Figure f) {
        return (Locator) f.getAttribute(LAYOUT_LOCATOR);
    }
}
