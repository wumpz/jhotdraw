/*
 * Copyright (C) 2015 JHotDraw.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.jhotdraw.draw.figure.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.jhotdraw.draw.figure.AbstractAttributedFigure;
import org.jhotdraw.draw.figure.Attributes;
import org.junit.jupiter.api.Test;

/**
 * @author tw
 */
public class AbstractFigureTest {

  @Test
  public void testChangedWithoutWillChange() {
    assertThrows(IllegalStateException.class, () -> new AbstractFigureImpl().changed());
  }

  @Test
  public void testWillChangeChangedEvents() {
    // AbstractAttributedFigure figure = new AbstractFigureImpl();
    AbstractFigureImpl figure = new AbstractFigureImpl();
    assertEquals(figure.changingDepth(), 0);
    figure.willChange();
    assertEquals(figure.changingDepth(), 1);
    figure.willChange();
    assertEquals(figure.changingDepth(), 2);
    figure.changed();
    assertEquals(figure.changingDepth(), 1);
    figure.changed();
    assertEquals(figure.changingDepth(), 0);
  }

  public static class AbstractFigureImpl extends AbstractAttributedFigure {

    @Override
    public void draw(Graphics2D g) {}

    @Override
    public Rectangle2D.Double getBounds(double scale) {
      return null;
    }

    @Override
    public boolean contains(Point2D.Double p) {
      return true;
    }

    @Override
    public Object getTransformRestoreData() {
      return null;
    }

    @Override
    public void restoreTransformTo(Object restoreData) {}

    @Override
    public void transform(AffineTransform tx) {}

    @Override
    public Rectangle2D.Double getDrawingArea(double factor) {
      return null;
    }

    @Override
    public Attributes attr() {
      return null;
    }

    @Override
    protected void drawFill(Graphics2D g) {}

    @Override
    protected void drawStroke(Graphics2D g) {}

    @Override
    public boolean contains(Point2D.Double p, double scaleDenominator) {
      return false;
    }

    public int changingDepth() {
      return getChangingDepth();
    }
  }
}
