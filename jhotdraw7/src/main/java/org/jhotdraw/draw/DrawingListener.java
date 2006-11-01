/*
 * @(#)DrawingListener.java  1.0  11. November 2003
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

import java.util.*;
/**
 * Listener interested in Drawing changes.
 *
 * @author Werner Randelshofer
 * @version 1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface DrawingListener extends EventListener {
    
    /**
     * Sent when an area of the drawing needs to be repainted.
     */
    public void areaInvalidated(DrawingEvent e);
    
    /**
     * Sent when a figure was added.
     */
    public void figureAdded(DrawingEvent e);
    
    /**
     * Sent when a figure was removed.
     */
    public void figureRemoved(DrawingEvent e);
}
