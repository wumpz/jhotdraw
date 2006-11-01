/*
 * @(#)LocatorConnector.java  2.0.1  2006-07-05
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
import java.io.IOException;
import org.jhotdraw.xml.*;
/**
 * A LocatorConnector locates connection points with
 * the help of a Locator. It supports the definition
 * of connection points to semantic locations.
 *
 * @see Locator
 * @see Connector
 *
 * @author Werner Randelshofer
 * @version 2.0.1 2006-07-05 Fixed override bugs.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class LocatorConnector extends AbstractConnector {
    /**
     * The standard size of the connector. The display box
     * is centered around the located point.
     * <p>
     * FIXME - Why do we need a standard size?
     */
    public static final int SIZE = 2;
    
    private Locator  locator;
    
    /**
     * Creates a new instance.
     * Only used for DOMStorable.
     */
    public LocatorConnector() {
    }
    
    public LocatorConnector(Figure owner, Locator l) {
        super(owner);
        locator = l;
    }
    
    protected Point2D.Double locate(ConnectionFigure connection) {
        return locator.locate(getOwner());
    }
    
    /**
     * Tests if a point is contained in the connector.
     */
    @Override public boolean contains(Point2D.Double p) {
        return getBounds().contains(p);
    }
    
    /**
     * Gets the display box of the connector.
     */
   @Override public Rectangle2D.Double getBounds() {
        Point2D.Double p = locator.locate(getOwner());
        return new Rectangle2D.Double(
        p.x - SIZE / 2,
        p.y - SIZE / 2,
        SIZE,
        SIZE);
    }
    
    @Override public void read(DOMInput in) throws IOException {
        super.read(in);
        in.openElement("locator");
        this.locator = (Locator) in.readObject(0);
        in.closeElement();
    }
    
   @Override public void write(DOMOutput out) throws IOException {
        super.write(out);
        out.openElement("locator");
        out.writeObject(locator);
        out.closeElement();
    }
}
