/*
 * Copyright (C) 2026 JHotDraw.
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

package org.jhotdraw.draw.tool;

import java.awt.geom.Point2D;
import org.jhotdraw.draw.figure.Figure;

/**
 * Allow further filtering of which figures may be recognized from the SelectionTool.
 */
@FunctionalInterface
public interface SelectionFigureFilter {
  /**
   * @param drawingPoint actual checking pointg
   * @param figure actual checked Figure
   * @param scale actual drawing to screen scale
   * @param selected is a selected figure checked
   */
  boolean allowFigure(Point2D.Double drawingPoint, Figure figure, double scale, boolean selected);
}
