/*
 * @(#)DrawFigureFactory.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.io;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connector.ChopBezierConnector;
import org.jhotdraw.draw.connector.ChopDiamondConnector;
import org.jhotdraw.draw.connector.ChopEllipseConnector;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.ChopRoundRectangleConnector;
import org.jhotdraw.draw.connector.ChopTriangleConnector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.figure.BezierFigure;
import org.jhotdraw.draw.figure.DiamondFigure;
import org.jhotdraw.draw.figure.EllipseFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.GroupFigure;
import org.jhotdraw.draw.figure.ImageFigure;
import org.jhotdraw.draw.figure.LineConnectionFigure;
import org.jhotdraw.draw.figure.LineFigure;
import org.jhotdraw.draw.figure.RectangleFigure;
import org.jhotdraw.draw.figure.RoundRectangleFigure;
import org.jhotdraw.draw.figure.TextAreaFigure;
import org.jhotdraw.draw.figure.TextFigure;
import org.jhotdraw.draw.figure.TriangleFigure;
import org.jhotdraw.draw.liner.CurvedLiner;
import org.jhotdraw.draw.liner.ElbowLiner;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
import org.jhotdraw.xml.DefaultDOMFactory;

/** DOM based figure factory */
public class DOMDefaultDrawFigureFactory extends DefaultDOMFactory {

  private static final Object[][] ENUM_TAGS = {
    {AttributeKeys.StrokePlacement.class, "strokePlacement"},
    {AttributeKeys.StrokeType.class, "strokeType"},
    {AttributeKeys.Underfill.class, "underfill"},
    {AttributeKeys.Orientation.class, "orientation"}
  };

  /** Creates a new instance. */
  public DOMDefaultDrawFigureFactory() {
    register("drawing", DefaultDrawing.class, null, null);
    register("drawing", QuadTreeDrawing.class, null, null);
    register(
        "diamond",
        DiamondFigure.class,
        DOMDefaultDrawFigureFactory::readBaseData,
        DOMDefaultDrawFigureFactory::writeBaseData);
    register(
        "triangle",
        TriangleFigure.class,
        DOMDefaultDrawFigureFactory::readBaseData,
        DOMDefaultDrawFigureFactory::writeBaseData);
    register("bezier", BezierFigure.class);
    register(
        "r",
        RectangleFigure.class,
        DOMDefaultDrawFigureFactory::readBaseData,
        DOMDefaultDrawFigureFactory::writeBaseData);
    register(
        "rr",
        RoundRectangleFigure.class,
        DOMDefaultDrawFigureFactory::readRoundRectangle,
        DOMDefaultDrawFigureFactory::writeRoundRectangle);
    register("l", LineFigure.class);
    register("b", BezierFigure.class);
    register("lnk", LineConnectionFigure.class);
    register(
        "e",
        EllipseFigure.class,
        DOMDefaultDrawFigureFactory::readBaseData,
        DOMDefaultDrawFigureFactory::writeBaseData);
    register(
        "t",
        TextFigure.class,
        DOMDefaultDrawFigureFactory::readText,
        DOMDefaultDrawFigureFactory::writeText);
    register("ta", TextAreaFigure.class);
    register("image", ImageFigure.class);
    register("g", GroupFigure.class);
    register("arrowTip", ArrowTip.class);
    register("rConnector", ChopRectangleConnector.class);
    register("ellipseConnector", ChopEllipseConnector.class);
    register("rrConnector", ChopRoundRectangleConnector.class);
    register("triangleConnector", ChopTriangleConnector.class);
    register("diamondConnector", ChopDiamondConnector.class);
    register("bezierConnector", ChopBezierConnector.class);
    register("elbowLiner", ElbowLiner.class);
    register("curvedLiner", CurvedLiner.class);

    for (Object[] o : ENUM_TAGS) {
      addEnumClass((String) o[1], (Class) o[0]);
    }
  }

  private static void readText(TextFigure figure, DOMInput domInput) throws IOException {
    figure.setBounds(
        new Point2D.Double(domInput.getAttribute("x", 0d), domInput.getAttribute("y", 0d)),
        new Point2D.Double(0, 0));
    readAttributes(figure, domInput);
    readDecorator(figure, domInput);
  }

  private static void readDecorator(TextFigure figure, DOMInput domInput) throws IOException {
    if (domInput.getElementCount("decorator") > 0) {
      domInput.openElement("decorator");
      figure.setDecorator((Figure) domInput.readObject());
      domInput.closeElement();
    } else {
      figure.setDecorator(null);
    }
  }

  private static void writeText(TextFigure figure, DOMOutput domOutput) throws IOException {
    Rectangle2D.Double b = figure.getBounds();
    domOutput.addAttribute("x", b.x);
    domOutput.addAttribute("y", b.y);
    writeAttributes(figure, domOutput);
    writeDecorator(figure, domOutput);
  }

  private static void writeDecorator(TextFigure figure, DOMOutput domOutput) throws IOException {
    if (figure.getDecorator() != null) {
      domOutput.openElement("decorator");
      domOutput.writeObject(figure.getDecorator());
      domOutput.closeElement();
    }
  }

  private static void readRoundRectangle(RoundRectangleFigure figure, DOMInput domInput)
      throws IOException {
    readBaseData(figure, domInput);
    figure.setArc(
        domInput.getAttribute("arcWidth", RoundRectangleFigure.DEFAULT_ARC),
        domInput.getAttribute("arcHeight", RoundRectangleFigure.DEFAULT_ARC));
  }

  private static void writeRoundRectangle(RoundRectangleFigure figure, DOMOutput domOutput)
      throws IOException {
    writeBaseData(figure, domOutput);
    domOutput.addAttribute("arcWidth", figure.getArcWidth());
    domOutput.addAttribute("arcHeight", figure.getArcHeight());
  }

  private static void readBaseData(Figure figure, DOMInput domInput) throws IOException {
    readBounds(figure, domInput);
    readAttributes(figure, domInput);
  }

  private static void readBounds(Figure figure, DOMInput domInput) {
    double x = domInput.getAttribute("x", 0d);
    double y = domInput.getAttribute("y", 0d);
    double w = domInput.getAttribute("w", 0d);
    double h = domInput.getAttribute("h", 0d);
    figure.setBounds(new Point2D.Double(x, y), new Point2D.Double(x + w, y + h));
  }

  private static void readAttributes(Figure figure, DOMInput domInput) throws IOException {
    if (domInput.getElementCount("a") > 0) {
      domInput.openElement("a");
      for (int i = 0, n = domInput.getElementCount(); i < n; i++) {
        domInput.openElement(i);
        Object value = domInput.readObject();
        AttributeKey<?> key = AttributeKeys.SUPPORTED_ATTRIBUTES_MAP.get(domInput.getTagName());
        if (key != null && key.isAssignable(value)) {
          figure.set((AttributeKey<Object>) key, value);
        }
        domInput.closeElement();
      }
      domInput.closeElement();
    }
  }

  private static void writeBaseData(Figure figure, DOMOutput domOutput) throws IOException {
    writeBounds(figure, domOutput);
    writeAttributes(figure, domOutput);
  }

  private static void writeAttributes(Figure figure, DOMOutput domOutput) throws IOException {
    Figure prototype = (Figure) domOutput.getPrototype();
    boolean isElementOpen = false;
    for (Map.Entry<AttributeKey<?>, Object> entry : figure.getAttributes().entrySet()) {
      AttributeKey<?> key = entry.getKey();
      if (figure.isAttributeEnabled(key)) {
        Object prototypeValue = prototype.get(key);
        Object attributeValue = figure.get(key);
        if (!Objects.equals(prototypeValue, attributeValue)) {
          if (!isElementOpen) {
            domOutput.openElement("a");
            isElementOpen = true;
          }
          domOutput.openElement(key.getKey());
          domOutput.writeObject(entry.getValue());
          domOutput.closeElement();
        }
      }
    }
    if (isElementOpen) {
      domOutput.closeElement();
    }
  }

  private static void writeBounds(Figure figure, DOMOutput domOutput) {
    Rectangle2D.Double r = figure.getBounds();
    domOutput.addAttribute("x", r.x);
    domOutput.addAttribute("y", r.y);
    domOutput.addAttribute("w", r.width);
    domOutput.addAttribute("h", r.height);
  }
}
