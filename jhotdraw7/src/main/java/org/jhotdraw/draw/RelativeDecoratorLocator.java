/*
 * @(#)RelativeLocator.java  1.0  2006-03-29
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

import java.awt.*;
import java.awt.geom.*;
/**
 * A locator that specfies a point that is relative to the bounds
 * of a figures decorator.
 *
 * @author Werner Randelshofer
 * @version 2006-03-29 Created.
 */
public class RelativeDecoratorLocator extends RelativeLocator {
    private boolean isQuadratic;
    
    public RelativeDecoratorLocator() {
    }
    
    /** Creates a new instance. */
    public RelativeDecoratorLocator(double relativeX, double relativeY) {
        super(relativeX, relativeY);
    }
    public RelativeDecoratorLocator(double relativeX, double relativeY, boolean isQuadratic) {
        super(relativeX, relativeY);
        this.isQuadratic = isQuadratic;
    }
    
    public java.awt.geom.Point2D.Double locate(Figure owner) {
        Rectangle2D.Double r;
        if (owner.getDecorator() != null) {
            r = owner.getDecorator().getBounds();
        } else {
            r = owner.getBounds();
        }
        if (isQuadratic) {
        double side = Math.max(r.width, r.height);
        r.x -= (side - r.width) / 2;
        r.y -= (side - r.height) / 2;
        r.width = r.height = side;
        }
        return new Point2D.Double(
                r.x + r.width * relativeX,
                r.y + r.height * relativeY
                );
    }
    
    static public Locator east() {
        return new RelativeDecoratorLocator(1.0, 0.5);
    }
    
    /**
     * North.
     */
    static public Locator north() {
        return new RelativeDecoratorLocator(0.5, 0.0);
    }
    
    /**
     * West.
     */
    static public Locator west() {
        return new RelativeDecoratorLocator(0.0, 0.5);
    }
    
    /**
     * North east.
     */
    static public Locator northEast() {
        return new RelativeDecoratorLocator(1.0, 0.0);
    }
    
    /**
     * North west.
     */
    static public Locator northWest() {
        return new RelativeDecoratorLocator(0.0, 0.0);
    }
    
    /**
     * South.
     */
    static public Locator south() {
        return new RelativeDecoratorLocator(0.5, 1.0);
    }
    
    /**
     * South east.
     */
    static public Locator southEast() {
        return new RelativeDecoratorLocator(1.0, 1.0);
    }
    
    /**
     * South west.
     */
    static public Locator southWest() {
        return new RelativeDecoratorLocator(0.0, 1.0);
    }
    
    /**
     * Center.
     */
    static public Locator center() {
        return new RelativeDecoratorLocator(0.5, 0.5);
    }
}
