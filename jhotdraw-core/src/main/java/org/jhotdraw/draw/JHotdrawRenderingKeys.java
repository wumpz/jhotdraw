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

package org.jhotdraw.draw;

import java.awt.RenderingHints;

/**
 * Additional settings to somehow change drawing.
 */
public final class JHotdrawRenderingKeys {
  /**
   * For drawing a figure, the scaling can be adjusted using this global precision value. Maybe you want to print
   * with double precisison, then your pixel sized stuff needs to be adjusted.
   */
  public static final RenderingHints.Key GLOBAL_PRECISION = new RenderingHints.Key(99) {
    @Override
    public boolean isCompatibleValue(Object val) {
      return val instanceof Double;
    }
  };

  private JHotdrawRenderingKeys() {}
}
