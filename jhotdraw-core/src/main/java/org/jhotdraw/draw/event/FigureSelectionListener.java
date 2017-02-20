/*
 * @(#)FigureSelectionListener.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */


package org.jhotdraw.draw.event;


/**
 * Interface implemented by observers of selection changes in 
 * {@link org.jhotdraw.draw.DrawingView} objects.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Observer</em><br>
 * Selection changes of {@code DrawingView} are observed by user interface
 * components which act on selected figures.<br>
 * Subject: {@link org.jhotdraw.draw.DrawingView}; Observer:
 * {@link FigureSelectionListener}; Event: {@link FigureSelectionEvent}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface FigureSelectionListener extends java.util.EventListener {
    public void selectionChanged(FigureSelectionEvent evt);
}
