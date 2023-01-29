/*
 * @(#)ChopTriangleConnector.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.connector;

import java.awt.geom.*;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.TriangleFigure;

/**
 * A {@link Connector} which locates a connection point at the bounds of a {@link TriangleFigure}.
 *
 * <p>
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class ChopTriangleConnector extends ChopRectangleConnector {

  private static final long serialVersionUID = 1L;

  public ChopTriangleConnector() {}

  public ChopTriangleConnector(TriangleFigure owner) {
    super(owner);
  }

  @Override
  protected Point2D.Double chop(Figure target, Point2D.Double from) {
    TriangleFigure bf = (TriangleFigure) getConnectorTarget(target);
    return bf.chop(from);
  }
}
