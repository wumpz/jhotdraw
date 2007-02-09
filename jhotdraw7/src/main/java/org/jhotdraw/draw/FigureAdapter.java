/*
 * @(#)FigureAdapter.java  2.0  2007-02-09
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

/**
 * FigureAdapter implements the FigureListener interface with empty methods.
 * 
 * @author Werner Randelshofer
 * @version 2.0 2007-02-09 Renamed from AbstractFigureListener to FigureAdapter.
 * <br>1.0 2. Februar 2004  Created.
 */
public class FigureAdapter implements FigureListener {
    
    public void figureAreaInvalidated(FigureEvent e) {
    }
    
    public void figureAttributeChanged(FigureEvent e) {
    }
    
    public void figureAdded(FigureEvent e) {
    }
    
    public void figureChanged(FigureEvent e) {
    }
    
    public void figureRemoved(FigureEvent e) {
    }
    
    public void figureRequestRemove(FigureEvent e) {
    }

    public void figureHandlesChanged(FigureEvent e) {
    }
    
}
