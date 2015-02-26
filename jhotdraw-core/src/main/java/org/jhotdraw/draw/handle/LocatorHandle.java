/*
 * @(#)LocatorHandle.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */


package org.jhotdraw.draw.handle;

import org.jhotdraw.draw.locator.Locator;
import org.jhotdraw.draw.*;
import java.awt.*;
import java.awt.geom.*;
/**
 * A LocatorHandle implements a Handle by delegating the location requests to
 * a Locator object.
 *
 * @see Locator
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class LocatorHandle extends AbstractHandle {
    private Locator locator;
    
    /**
     * Initializes the LocatorHandle with the given Locator.
     */
    public LocatorHandle(Figure owner, Locator l) {
        super(owner);
        locator = l;
    }
    
    public Point2D.Double getLocationOnDrawing() {
        return locator.locate(getOwner());
    }
    
    public Point getLocation() {
        return view.drawingToView(locator.locate(getOwner()));
    }
    
    @Override
    protected Rectangle basicGetBounds() {
        Rectangle r = new Rectangle(getLocation());
        int h = getHandlesize();
        r.x -= h / 2;
        r.y -= h / 2;
        r.width = r.height = h;
        return r;
    }
}
