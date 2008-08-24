/*
 * @(#)FigureSelectionListener.java  1.0  25. November 2003
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
 * FigureSelectionListener.
 * <p>
 * Design pattern:<br>
 * Name: Observer.<br>
 * Role: Observer.<br>
 * Partners: {@link DrawingView} as Subject.
 *
 * @author Werner Randelshofer
 * @version 1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface FigureSelectionListener extends java.util.EventListener {
    public void selectionChanged(FigureSelectionEvent evt);
}
