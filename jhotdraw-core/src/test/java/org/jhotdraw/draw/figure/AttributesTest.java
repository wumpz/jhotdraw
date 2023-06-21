/*
 * Copyright (C) 2023 JHotDraw.
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

package org.jhotdraw.draw.figure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jhotdraw.draw.AttributeKeys;
import org.junit.jupiter.api.Test;

/**
 * @author tw
 */
public class AttributesTest {

  @Test
  public void testBackupRestore() {
    Attributes attr = new Attributes();
    attr.set(AttributeKeys.STROKE_WIDTH, 1.5);

    Object backup = attr.getAttributesRestoreData();

    Attributes attrRestored = new Attributes();
    assertThat(attrRestored.getAttributes()).isEmpty();

    attrRestored.restoreAttributesTo(backup);

    assertEquals(1.5, attrRestored.get(AttributeKeys.STROKE_WIDTH).doubleValue());
    assertEquals(
        attr.get(AttributeKeys.STROKE_WIDTH).doubleValue(),
        attrRestored.get(AttributeKeys.STROKE_WIDTH).doubleValue());
  }
}
