/*
 * @(#)FigureAdapter.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.draw.event;

/**
 * An abstract adapter class for receiving {@link FigureEvent}s. This class
 * exists as a convenience for creating {@link FigureListener} objects.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FigureAdapter implements FigureListener {
    @Override
    public void areaInvalidated(FigureEvent e) {
    }
    
    @Override
    public void attributeChanged(FigureEvent e) {
    }
    
    @Override
    public void figureAdded(FigureEvent e) {
    }
    
    @Override
    public void figureChanged(FigureEvent e) {
    }
    
    @Override
    public void figureRemoved(FigureEvent e) {
    }
    
    @Override
    public void figureRequestRemove(FigureEvent e) {
    }

    @Override
    public void figureHandlesChanged(FigureEvent e) {
    }
    
}
