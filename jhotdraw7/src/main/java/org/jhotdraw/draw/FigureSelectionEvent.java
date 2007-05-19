/*
 * @(#)FigureSelectionEvent.java  2.0  2007-05-14
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
 * FigureSelectionEvent.
 *
 * @author Werner Randelshofer
 * @version 2.0 2007-05-14 Added getters for newValue and oldValue. 
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class FigureSelectionEvent extends java.util.EventObject {
private Set<Figure> oldValue;
private Set<Figure> newValue;

    /** Creates a new instance. */
    public FigureSelectionEvent(DrawingView source, Set<Figure> oldValue, Set<Figure> newValue) {
        super(source);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    public DrawingView getView() {
        return (DrawingView) source;
    }
    
    public Set<Figure> getOldSelection() {
        return oldValue;
    }
    public Set<Figure> getNewSelection() {
        return newValue;
    }
}
