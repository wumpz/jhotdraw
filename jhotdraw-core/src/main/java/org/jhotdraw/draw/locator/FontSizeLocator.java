/*
 * @(#)FontSizeLocator.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.locator;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.geom.*;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.TextHolderFigure;

/**
 * {@code FontSizeLocator} is used by {@link org.jhotdraw.draw.handle.FontSizeHandle} to locate its
 * position on the drawing.
 */
public class FontSizeLocator implements Locator {

  public FontSizeLocator() {}

  /**
   * Locates a position on the provided figure.
   *
   * @return a Point2D.Double on the figure.
   */
  @Override
  public Point2D.Double locate(Figure owner) {
    Point2D.Double p = (Point2D.Double) owner.getStartPoint().clone();
    if (owner instanceof TextHolderFigure) {
      p.y += ((TextHolderFigure) owner).getFontSize();
      p.y += ((TextHolderFigure) owner).getInsets().top;
    } else {
      p.y += owner.attr().get(FONT_SIZE);
    }
    if (owner.attr().get(TRANSFORM) != null) {
      owner.attr().get(TRANSFORM).transform(p, p);
    }
    return p;
  }

  @Override
  public Point2D.Double locate(Figure owner, Figure dependent) {
    return locate(owner);
  }
}
