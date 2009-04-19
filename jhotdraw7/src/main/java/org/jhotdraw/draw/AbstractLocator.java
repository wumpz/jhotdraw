/*
 * @(#)AbstractLocator.java  2.1  2006-07-08
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.draw;

import java.awt.geom.*;
import java.io.Serializable;
import org.jhotdraw.xml.*;
/**
 * AbstractLocator provides default implementations for
 * the Locator interface.
 *
 * @author Werner Randelshofer
 * @version 2,1 2006-07-08 Added support for DOMStorable. 
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public abstract class AbstractLocator implements Locator, DOMStorable, Serializable {
    
    /** Creates a new instance. */
    public AbstractLocator() {
    }
    
    public Point2D.Double locate(Figure owner, Figure dependent) {
        return locate(owner);
    }
    
    
}
