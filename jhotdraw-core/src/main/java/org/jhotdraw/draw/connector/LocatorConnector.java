/*
 * @(#)LocatorConnector.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.connector;

import java.awt.geom.*;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figure.ConnectionFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.locator.Locator;

/**
 * A LocatorConnector locates connection points with the help of a Locator. It supports the
 * definition of connection points to semantic locations.
 *
 * @see Locator
 * @see Connector
 */
public class LocatorConnector extends AbstractConnector {

  private static final long serialVersionUID = 1L;

  /**
   * The standard size of the connector. The display box is centered around the located point.
   *
   * <p>FIXME - Why do we need a standard size?
   */
  public static final int SIZE = 2;

  private Locator locator;

  public LocatorConnector() {}

  public LocatorConnector(Figure owner, Locator l) {
    super(owner);
    locator = l;
  }

  public Locator getLocator() {
    return locator;
  }

  public void setLocator(Locator locator) {
    this.locator = locator;
  }

  protected Point2D.Double locate(ConnectionFigure connection) {
    return locator
        .locate(getOwner(), AttributeKeys.scaleFromContext(connection))
        .location();
  }

  /** Tests if a point is contained in the connector. */
  @Override
  public boolean contains(Point2D.Double p) {
    return getBounds().contains(p);
  }

  /** Gets the display box of the connector. */
  @Override
  public Rectangle2D.Double getBounds(double scale) {
    Point2D.Double p = locator.locate(getOwner(), scale).location();
    return new Rectangle2D.Double(p.x - SIZE / 2, p.y - SIZE / 2, SIZE, SIZE);
  }
}
