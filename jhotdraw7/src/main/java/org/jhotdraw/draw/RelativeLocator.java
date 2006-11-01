/*
 * @(#)RelativeLocator.java  2.2  2006-07-08
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
�
 */

package org.jhotdraw.draw;

import java.awt.*;
import java.awt.geom.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
/**
 * A locator that specfies a point that is relative to the bounds
 * of a figure.
 *
 * @author Werner Randelshofer
 * @version 2006-07-08 Added DOMStorable support. 
 * <br>2006-07-05 Added support for DECORATOR_INSETS.
 * <br>2006-02-14 Fixed computed coordinate values.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class RelativeLocator extends AbstractLocator {
    double  relativeX;
    double  relativeY;
    
    public RelativeLocator() {
        relativeX = 0.0;
        relativeY = 0.0;
    }
    
    /** Creates a new instance. */
    public RelativeLocator(double relativeX, double relativeY) {
        this.relativeX = relativeX;
        this.relativeY = relativeY;
    }
    
    public java.awt.geom.Point2D.Double locate(Figure owner) {
        Rectangle2D.Double bounds = owner.getBounds();
        if (owner.getDecorator() != null) {
            Insets2DDouble insets = DECORATOR_INSETS.get(owner);
            if (insets != null) {
                bounds.x -= insets.left;
                bounds.y -= insets.top;
                bounds.width += insets.left + insets.right;
                bounds.height += insets.top + insets.bottom;
            }
        }
        return new Point2D.Double(
        bounds.x + bounds.width * relativeX,
        bounds.y + bounds.height * relativeY
        );
    }
    
    static public Locator east() {
        return new RelativeLocator(1.0, 0.5);
    }
    
    /**
     * North.
     */
    static public Locator north() {
        return new RelativeLocator(0.5, 0.0);
    }
    
    /**
     * West.
     */
    static public Locator west() {
        return new RelativeLocator(0.0, 0.5);
    }
    
    /**
     * North east.
     */
    static public Locator northEast() {
        return new RelativeLocator(1.0, 0.0);
    }
    
    /**
     * North west.
     */
    static public Locator northWest() {
        return new RelativeLocator(0.0, 0.0);
    }
    
    /**
     * South.
     */
    static public Locator south() {
        return new RelativeLocator(0.5, 1.0);
    }
    
    /**
     * South east.
     */
    static public Locator southEast() {
        return new RelativeLocator(1.0, 1.0);
    }
    
    /**
     * South west.
     */
    static public Locator southWest() {
        return new RelativeLocator(0.0, 1.0);
    }
    
    /**
     * Center.
     */
    static public Locator center() {
        return new RelativeLocator(0.5, 0.5);
    }

    public void write(DOMOutput out) {
        out.addAttribute("relativeX", relativeX, 0.5);
        out.addAttribute("relativeY", relativeY, 0.5);
    }

    public void read(DOMInput in) {
      relativeX = in.getAttribute("relativeX", 0.5);
      relativeY = in.getAttribute("relativeY", 0.5);
    }
}
