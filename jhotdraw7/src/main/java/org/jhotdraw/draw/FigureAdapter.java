/*
 * @(#)FigureAdapter.java  2.0  2007-02-09
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

/**
 * FigureAdapter implements the FigureListener interface with empty methods.
 * 
 * @author Werner Randelshofer
 * @version 2.0 2007-02-09 Renamed from AbstractFigureListener to FigureAdapter.
 * <br>1.0 2. Februar 2004  Created.
 */
public class FigureAdapter implements FigureListener {
    
    public void areaInvalidated(FigureEvent e) {
    }
    
    public void attributeChanged(FigureEvent e) {
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
