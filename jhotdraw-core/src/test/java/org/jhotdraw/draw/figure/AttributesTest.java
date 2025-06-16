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

import java.util.Map;
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

    assertEquals(1.5, attrRestored.get(AttributeKeys.STROKE_WIDTH));
    assertEquals(
        attr.get(AttributeKeys.STROKE_WIDTH).doubleValue(),
        attrRestored.get(AttributeKeys.STROKE_WIDTH).doubleValue());
  }

  @Test
  public void testGetAndInclude() {
    Attributes attr = new Attributes();
    assertThat(attr.getAttributes()).isEmpty();
    assertEquals(1.0, attr.getAndInclude(AttributeKeys.STROKE_WIDTH));
    assertThat(attr.getAttributes()).isNotEmpty();
    attr.getAttributes().containsKey(AttributeKeys.STROKE_WIDTH);
  }

  @Test
  void testCopyAttributesIncludingUserData() {
    Attributes attr = new Attributes();
    attr.set(AttributeKeys.STROKE_WIDTH, 1.5);
    attr.getAndInclude(AttributeKeys.USER_DATA).data().put("var", "value");

    Attributes attrNew = new Attributes();
    attrNew.setAttributes(attr.getAttributes());

    attr.set(AttributeKeys.STROKE_WIDTH, 2.0);
    attr.get(AttributeKeys.USER_DATA).data().put("var", "value2");

    // ensure that the values are valid and no instance was copy to the new Attribute container
    assertEquals(attrNew.get(AttributeKeys.STROKE_WIDTH).doubleValue(), 1.5f);
    assertEquals(attr.get(AttributeKeys.STROKE_WIDTH).doubleValue(), 2.0f);

    assertEquals(attrNew.get(AttributeKeys.USER_DATA).data().get("var"), "value");
    assertEquals(attr.get(AttributeKeys.USER_DATA).data().get("var"), "value2");
  }

  @Test
  void testEnsureCopyOfAttributesMapRestoreData() {
    Attributes attr = new Attributes();
    attr.set(AttributeKeys.STROKE_WIDTH, 1.5);
    attr.set(AttributeKeys.STROKE_COLOR, null); // null values can happen

    var data = attr.getAttributesRestoreData();

    attr.removeAttribute(AttributeKeys.STROKE_WIDTH);
    attr.removeAttribute(AttributeKeys.STROKE_COLOR);
    assertThat(data).isInstanceOf(Map.class);
    assertThat((Map) data).hasSize(2);

    attr.restoreAttributesTo(data);

    assertThat(attr.get(AttributeKeys.STROKE_WIDTH)).isEqualTo(1.5);
    assertThat(attr.get(AttributeKeys.STROKE_COLOR)).isNull();
  }
}
