/*
 * @(#)AbstractLayouter.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.layouter;

import org.jhotdraw.draw.figure.CompositeFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.utils.geom.Insets2D;

/**
 * This abstract class can be extended to implement a {@link Layouter} which has its own attribute
 * set.
 */
public abstract class AbstractLayouter implements Layouter {

  public Insets2D.Double getInsets(Figure child) {
    Insets2D.Double value = child.attr().get(CompositeFigure.LAYOUT_INSETS);
    return (value == null) ? new Insets2D.Double() : (Insets2D.Double) value.clone();
  }
}
