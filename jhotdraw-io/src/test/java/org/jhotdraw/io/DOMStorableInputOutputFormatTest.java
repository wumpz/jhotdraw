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

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.figure.BezierFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.RectangleFigure;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.draw.io.OutputFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.xmlunit.assertj.XmlAssert;

/**
 * @author tw
 */
public class DOMStorableInputOutputFormatTest {

  @Test
  public void testRectangle() throws IOException {
    InputFormat format = new DOMStorableInputFormat(new DOMDefaultDrawFigureFactory());
    Drawing drawing = new DefaultDrawing();
    format.read(
        DOMStorableInputOutputFormatTest.class.getResourceAsStream("green_rectangle.xml"),
        drawing,
        true);

    assertThat(drawing.getChildren()).hasSize(1);
    Figure rect = drawing.getChild(0);
    assertThat(rect).isInstanceOf(RectangleFigure.class);
    assertThat(rect.attr().get(AttributeKeys.STROKE_COLOR)).isEqualTo(new Color(255, 0, 0));
    assertThat(rect.attr().get(AttributeKeys.STROKE_WIDTH)).isEqualTo(3.0);
    assertThat(rect.attr().get(AttributeKeys.FILL_COLOR)).isEqualTo(new Color(0, 128, 0));
  }

  @Test
  public void testSomeFigures() throws IOException {
    InputFormat format = new DOMStorableInputFormat(new DOMDefaultDrawFigureFactory());
    Drawing drawing = new DefaultDrawing();
    format.read(
        DOMStorableInputOutputFormatTest.class.getResourceAsStream("figures.xml"), drawing, true);

    assertThat(drawing.getChildren()).hasSize(11);
    Figure rect = drawing.getChild(0);
    assertThat(rect).isInstanceOf(RectangleFigure.class);
    assertThat(rect.attr().get(AttributeKeys.STROKE_COLOR)).isEqualTo(Color.BLACK);
    assertThat(rect.attr().get(AttributeKeys.STROKE_WIDTH)).isEqualTo(1.0);
    assertThat(rect.attr().get(AttributeKeys.FILL_COLOR)).isEqualTo(new Color(255, 255, 102));
  }

  @ParameterizedTest(name = "{index} {0}")
  @CsvSource({"figures", "arrowtip", "green_rectangle", "bezier", "group", "image"})
  public void testSomeFiguresInOut(String filename) throws IOException, URISyntaxException {
    LOG.info("testing " + filename + ".xml");
    InputFormat format = new DOMStorableInputFormat(new DOMDefaultDrawFigureFactory());
    Drawing drawing = new DefaultDrawing();
    format.read(
        DOMStorableInputOutputFormatTest.class.getResourceAsStream(filename + ".xml"),
        drawing,
        true);

    OutputFormat outFormat = new DOMStorableOutputFormat(new DOMDefaultDrawFigureFactory());
    File outputFile = new File("target/test-output/" + filename + "_roundtrip.xml");
    outputFile.getParentFile().mkdirs();
    outFormat.write(
        new FileOutputStream("target/test-output/" + filename + "_roundtrip.xml"), drawing);

    XmlAssert.assertThat(
            DOMStorableInputOutputFormatTest.class.getResourceAsStream(filename + ".xml"))
        .and(outputFile)
        .ignoreWhitespace()
        .areIdentical();
  }

  private static final Logger LOG =
      Logger.getLogger(DOMStorableInputOutputFormatTest.class.getName());

  /**
   * This new implementation skipped color entries like <color id="14" rgba="#ff000000"/>.
   *
   * <p>Indeed this is correct since this is the standard value for this data item and skipping it
   * would result in the same output.
   */
  @Test
  public void testProblemAttributeRectangle() throws IOException {
    InputFormat format = new DOMStorableInputFormat(new DOMDefaultDrawFigureFactory());
    Drawing drawing = new DefaultDrawing();
    format.read(
        DOMStorableInputOutputFormatTest.class.getResourceAsStream("problem_figure_attributes.xml"),
        drawing,
        true);

    assertThat(drawing.getChildren()).hasSize(1);
    Figure rect = drawing.getChild(0);
    assertThat(rect).isInstanceOf(RectangleFigure.class);
    assertThat(rect.attr().get(AttributeKeys.FILL_COLOR)).isEqualTo(new Color(102, 102, 255));
    assertThat(rect.attr().get(AttributeKeys.TEXT_COLOR)).isEqualTo(new Color(0, 0, 0));
  }

  @Test
  public void testBezierFigure() throws IOException {
    InputFormat format = new DOMStorableInputFormat(new DOMDefaultDrawFigureFactory());
    Drawing drawing = new DefaultDrawing();
    format.read(
        DOMStorableInputOutputFormatTest.class.getResourceAsStream("bezier.xml"), drawing, true);

    assertThat(drawing.getChildren()).hasSize(1);
    Figure rect = drawing.getChild(0);
    assertThat(rect).isInstanceOf(BezierFigure.class);
    assertThat(((BezierFigure) rect).getNodeCount()).isEqualTo(10);
    assertThat(rect.attr().get(AttributeKeys.TEXT_COLOR)).isEqualTo(new Color(0, 0, 0));
  }
}
