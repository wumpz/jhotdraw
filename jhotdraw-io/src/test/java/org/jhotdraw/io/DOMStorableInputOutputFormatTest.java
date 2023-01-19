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

package org.jhotdraw.io;

import java.awt.Color;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.RectangleFigure;
import org.jhotdraw.draw.io.InputFormat;
import org.junit.jupiter.api.Test;

/**
 * @author tw
 */
public class DOMStorableInputOutputFormatTest {

  @Test
  public void testRectangle() throws IOException {
    InputFormat format = new DOMStorableInputOutputFormat(new DefaultDOM2DrawFigureFactory());
    Drawing drawing = new DefaultDrawing();
    format.read(
        DOMStorableInputOutputFormatTest.class.getResourceAsStream("green_rectangle.xml"),
        drawing,
        true);

    assertThat(drawing.getChildren()).hasSize(1);
    Figure rect = drawing.getChild(0);
    assertThat(rect).isInstanceOf(RectangleFigure.class);
    assertThat(rect.get(AttributeKeys.STROKE_COLOR)).isEqualTo(new Color(255, 0, 0));
    assertThat(rect.get(AttributeKeys.STROKE_WIDTH)).isEqualTo(3.0);
    assertThat(rect.get(AttributeKeys.FILL_COLOR)).isEqualTo(new Color(0, 128, 0));
  }
  
  @Test
  public void testSomeFigures() throws IOException {
    InputFormat format = new DOMStorableInputOutputFormat(new DefaultDOM2DrawFigureFactory());
    Drawing drawing = new DefaultDrawing();
    format.read(
        DOMStorableInputOutputFormatTest.class.getResourceAsStream("figures.xml"),
        drawing,
        true);

    assertThat(drawing.getChildren()).hasSize(11);
    Figure rect = drawing.getChild(0);
    assertThat(rect).isInstanceOf(RectangleFigure.class);
    assertThat(rect.get(AttributeKeys.STROKE_COLOR)).isEqualTo(Color.BLACK);
    assertThat(rect.get(AttributeKeys.STROKE_WIDTH)).isEqualTo(1.0);
    assertThat(rect.get(AttributeKeys.FILL_COLOR)).isEqualTo(new Color(255, 255, 102));
  }
}