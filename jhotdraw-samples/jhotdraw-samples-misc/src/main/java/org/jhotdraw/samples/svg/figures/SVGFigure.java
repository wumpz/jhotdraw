/*
 * @(#)SVGFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg.figures;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.Figure;

/** SVGFigure. */
public interface SVGFigure extends Figure {

  /**
   * Returns true, if this figure is empty for one of the following reasons:
   *
   * <ul>
   *   <li>A group has no children
   *   <li>A path has less than two points
   *   <li>An ellipse or a rectangle has a width or a height of 0
   *   <li>A text has no characters
   * </ul>
   */
  public boolean isEmpty();
}
