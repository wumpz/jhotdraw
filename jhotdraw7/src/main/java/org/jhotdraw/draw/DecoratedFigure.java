/*
 * @(#)DecoratedFigure.java
 *
 * Copyright (c) 1996-2010 by the original authors of JHotDraw
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

import org.jhotdraw.annotations.NotNull;
import org.jhotdraw.annotations.Nullable;

/**
 * A <em>decorated figure</em> can be decorated with another {@link Figure},
 * for example to draw a border around the decorated figure.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Decorator</em><br>
 * Decorated figures can be adorned with another figure.<br>
 * Component: {@link DecoratedFigure}; Decorator: {@link Figure}.
 *
 * <p><em>Strategy</em><br>
 * {@code RelativeDecoratorLocator} encapsulates a strategy for locating a
 * point on a decorator.<br>
 * Component: {@link DecoratedFigure}; Strategy: {@link org.jhotdraw.draw.locator.RelativeDecoratorLocator}.
 * 
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
@NotNull
public interface DecoratedFigure extends Figure {
    /**
     * Sets a decorator Figure, for example a visual adornment to this Figure.
     * Set this to null, if no decorator is desired.
     * The decorator uses the same logical bounds as this Figure plus 
     * AttributeKeys.DECORATOR_INSETS. The decorator does not handle events.
     * The decorator is drawn when the figure is drawn.
     */
    public void setDecorator(@Nullable Figure newValue);
    /**
     * Gets the decorator for this figure.
     */
    @Nullable public Figure getDecorator();
}
