/*
 * @(#)AbstractLocator.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.locator;

import java.awt.geom.*;
import java.io.Serializable;
import org.jhotdraw.draw.figure.Figure;

/** This abstract class can be extended to implement a {@link Locator}. */
public abstract class AbstractLocator implements Locator, Serializable {

  private static final long serialVersionUID = 1L;

  public AbstractLocator() {}

  @Override
  public Point2D.Double locate(Figure owner, Figure dependent) {
    return locate(owner);
  }
}
