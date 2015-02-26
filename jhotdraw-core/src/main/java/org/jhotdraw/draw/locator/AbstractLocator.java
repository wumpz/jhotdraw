/*
 * @(#)AbstractLocator.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.draw.locator;

import org.jhotdraw.draw.*;
import java.awt.geom.*;
import java.io.Serializable;
import org.jhotdraw.xml.*;
/**
 * This abstract class can be extended to implement a {@link Locator}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractLocator implements Locator, DOMStorable, Serializable {
    private static final long serialVersionUID = 1L;
    
    /** Creates a new instance. */
    public AbstractLocator() {
    }
    
    @Override
    public Point2D.Double locate(Figure owner, Figure dependent) {
        return locate(owner);
    }
    
    
}
