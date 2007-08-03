/*
 * @(#)CompositeFigureListener.java  2.0  2007-07-17
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
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

import java.util.*;
/**
 * Listener interested in changes in a CompositeFigure.
 *
 * @author Werner Randelshofer
 * @version 2.0 2007-07-17 Renamed from DrawingListener to CompositeFigureListener. 
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface CompositeFigureListener extends EventListener {
    /**
     * Sent when a figure was added.
     */
    public void figureAdded(CompositeFigureEvent e);
    
    /**
     * Sent when a figure was removed.
     */
    public void figureRemoved(CompositeFigureEvent e);
}
