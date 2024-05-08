/*
 * @(#)Locator.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.locator;

import java.awt.geom.*;
import org.jhotdraw.draw.figure.Figure;

/**
 * A <em>locator</em> encapsulates a strategy for locating a point and an angle on a {@link Figure}.
 * The angle would be a tangent on the element found, at least the locator supports this.
 *
 * <p><hr> <b>Design Patterns</b>
 *
 * <p><em>Strategy</em><br>
 * {@code Locator} encapsulates a strategy for locating a point on a {@code Figure}.<br>
 * Strategy: {@link Locator}; Context: {@link Figure}. <hr>
 */
public interface Locator {
  public record Position(Point2D.Double location, double angle) {
    public Position(Point2D.Double location) {
      this(location, 0);
    }
  }

  /**
   * Locates a position on the provided figure.
   *
   * @param owner locate a point relative to this owner
   * @param scale location should consider this scale denominator (e.g. pixel sized texts in scaled
   *     environments)
   * @return a point on the figure.
   */
  public Position locate(Figure owner, double scale);

  /**
   * Locates a position on the provided figure relative to the dependent figure.
   *
   * @param owner locate a point relative to this owner
   * @param dependent
   * @param scale location should consider this scale denominator (e.g. pixel sized texts in scaled
   *     environments)
   * @return a point on the figure.
   */
  public Position locate(Figure owner, Figure dependent, double scale);
}
