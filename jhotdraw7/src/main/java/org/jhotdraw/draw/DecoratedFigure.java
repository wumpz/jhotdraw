/*
 * @(#)DecoratedFigure.java  1.0  January 5, 2007
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

/**
 * This interface is implemented by Figures that can be Decorated with another
 * Figure.
 *
 * @author Werner Randelshofer
 * @version 1.0 January 5, 2007 Created.
 */
public interface DecoratedFigure {
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
