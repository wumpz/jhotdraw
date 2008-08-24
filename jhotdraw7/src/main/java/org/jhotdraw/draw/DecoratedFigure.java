/*
 * @(#)DecoratedFigure.java  2.0  2008-01-10
 *
 * Copyright (c) 1996-2008 by the original authors of JHotDraw
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
 * This interface is implemented by Figures that can be Decorated with another
 * Figure.
 * <p>
 * Design pattern:<br>
 * Name: Decorator.<br>
 * Role: Component.<br>
 * Partners: {@link Figure} as Decorator. 
 *
 * @author Werner Randelshofer
 * @version 2.0 2008-01-10 This interface extends now the Figure interface. 
 * <br>1.0 January 5, 2007 Created.
 */
public interface DecoratedFigure extends Figure {
    /**
     * Sets a decorator Figure, for example a visual adornment to this Figure.
     * Set this to null, if no decorator is desired.
     * The decorator uses the same logical bounds as this Figure plus 
     * AttributeKeys.DECORATOR_INSETS. The decorator does not handle events.
     * The decorator is drawn when the figure is drawn.
     */
    public void setDecorator(Figure newValue);
    /**
     * Gets the decorator for this figure.
     */
    public Figure getDecorator();    
}
