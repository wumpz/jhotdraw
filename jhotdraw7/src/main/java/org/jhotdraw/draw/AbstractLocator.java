/*
 * @(#)AbstractLocator.java  2.1  2006-07-08
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
public abstract class AbstractLocator implements Locator, DOMStorable {
    
    /** Creates a new instance. */
    public AbstractLocator() {
    }
    
    public Point2D.Double locate(Figure owner, Figure dependent) {
        return locate(owner);
    }
    
    
}
