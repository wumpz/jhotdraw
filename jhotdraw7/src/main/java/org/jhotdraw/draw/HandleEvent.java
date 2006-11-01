/*
 * @(#)HandleEvent.java  1.0  18. November 2003
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
import java.util.*;
/**
 * HandleEvent.
 *
 * @author Werner Randelshofer
 * @version 1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class HandleEvent extends EventObject {
    private Rectangle invalidatedArea;
    
    /** Creates a new instance. */
    public HandleEvent(Handle src, Rectangle invalidatedArea) {
        super(src);
        this.invalidatedArea = invalidatedArea;
    }
    
    public Handle getHandle() {
        return (Handle) getSource();
    }
    /**
     *  Gets the bounds of the invalidated area on the drawing view.
     */
    public Rectangle getInvalidatedArea() {
        return invalidatedArea;
    }
}
