/*
 * @(#)SeparatorLineFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.pert.figures;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.Graphics2D;
import java.awt.geom.*;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figure.RectangleFigure;
import org.jhotdraw.utils.geom.Dimension2DDouble;
import org.jhotdraw.utils.geom.Geom;

/** A horizontal line with a preferred size of 1,1. */
public class SeparatorLineFigure extends RectangleFigure {

  private static final long serialVersionUID = 1L;

  public SeparatorLineFigure() {}

  @Override
  public Dimension2DDouble getPreferredSize(double scale) {
    double width = Math.ceil(STROKE_WIDTH.get(this));
    return new Dimension2DDouble(width, width);
  }

  @Override
  protected void drawFill(Graphics2D g) {
    // no fill
  }

  @Override
  protected void drawStroke(Graphics2D g) {
    Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
    double grow =
        AttributeKeys.getPerpendicularDrawGrowth(this, AttributeKeys.getScaleFactorFromGraphics(g));
    Geom.grow(r, grow, grow);
    g.draw(new Line2D.Double(r.x, r.y, r.x + r.width - 1, r.y));
  }
}
