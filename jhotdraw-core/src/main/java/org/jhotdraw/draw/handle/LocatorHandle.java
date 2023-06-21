/*
 * @(#)LocatorHandle.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.locator.Locator;

/**
 * A LocatorHandle implements a Handle by delegating the location requests to a Locator object.
 *
 * @see Locator
 */
public abstract class LocatorHandle extends AbstractHandle {

  private Locator locator;

  /** Initializes the LocatorHandle with the given Locator. */
  public LocatorHandle(Figure owner, Locator l) {
    super(owner);
    locator = l;
  }

  @Override
  public Point2D.Double getDrawingLocation() {
    return locator.locate(getOwner());
  }
}
