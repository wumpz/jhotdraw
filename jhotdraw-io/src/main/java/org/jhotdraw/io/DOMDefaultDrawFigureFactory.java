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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connector.AbstractConnector;
import org.jhotdraw.draw.connector.ChopBezierConnector;
import org.jhotdraw.draw.connector.ChopDiamondConnector;
import org.jhotdraw.draw.connector.ChopEllipseConnector;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.ChopRoundRectangleConnector;
import org.jhotdraw.draw.connector.ChopTriangleConnector;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.figure.BezierFigure;
import org.jhotdraw.draw.figure.DecoratedFigure;
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
import org.jhotdraw.draw.liner.Liner;
import org.jhotdraw.draw.locator.Locator;
import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.geom.path.BezierPath;
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

  public DOMDefaultDrawFigureFactory() {
    register("drawing", DefaultDrawing.class, null, null); // do not allow processing
    register("drawing", QuadTreeDrawing.class, null, null); // do not allow processing
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
    register(
        "bezier",
        BezierFigure.class,
        DOMDefaultDrawFigureFactory::readBezier,
        DOMDefaultDrawFigureFactory::writeBezier);
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
    register(
        "l",
        LineFigure.class,
        DOMDefaultDrawFigureFactory::readBezier,
        DOMDefaultDrawFigureFactory::writeBezier);
    register(
        "b",
        BezierFigure.class,
        DOMDefaultDrawFigureFactory::readBezier,
        DOMDefaultDrawFigureFactory::writeBezier);
    register(
        "lnk",
        LineConnectionFigure.class,
        DOMDefaultDrawFigureFactory::readLineConnection,
        DOMDefaultDrawFigureFactory::writeLineConnection);
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
    register(
        "ta",
        TextAreaFigure.class,
        DOMDefaultDrawFigureFactory::readBaseData,
        DOMDefaultDrawFigureFactory::writeBaseData);
    register(
        "image",
        ImageFigure.class,
        DOMDefaultDrawFigureFactory::readImage,
        DOMDefaultDrawFigureFactory::writeImage);
    register(
        "g",
        GroupFigure.class,
        DOMDefaultDrawFigureFactory::readGroup,
        DOMDefaultDrawFigureFactory::writeGroup);
    register(
        "arrowTip",
        ArrowTip.class,
        DOMDefaultDrawFigureFactory::readArrowTip,
        DOMDefaultDrawFigureFactory::writeArrowTip);
    register(
        "rConnector",
        ChopRectangleConnector.class,
        DOMDefaultDrawFigureFactory::readConnector,
        DOMDefaultDrawFigureFactory::writeConnector);
    register(
        "ellipseConnector",
        ChopEllipseConnector.class,
        DOMDefaultDrawFigureFactory::readConnector,
        DOMDefaultDrawFigureFactory::writeConnector);
    register(
        "rrConnector",
        ChopRoundRectangleConnector.class,
        DOMDefaultDrawFigureFactory::readConnector,
        DOMDefaultDrawFigureFactory::writeConnector);
    register(
        "triangleConnector",
        ChopTriangleConnector.class,
        DOMDefaultDrawFigureFactory::readConnector,
        DOMDefaultDrawFigureFactory::writeConnector);
    register(
        "diamondConnector",
        ChopDiamondConnector.class,
        DOMDefaultDrawFigureFactory::readConnector,
        DOMDefaultDrawFigureFactory::writeConnector);
    register(
        "bezierConnector",
        ChopBezierConnector.class,
        DOMDefaultDrawFigureFactory::readConnector,
        DOMDefaultDrawFigureFactory::writeConnector);
    register(
        "locatorConnector",
        LocatorConnector.class,
        DOMDefaultDrawFigureFactory::readLocatorConnector,
        DOMDefaultDrawFigureFactory::writeLocatorConnector);

    register("relativeLoc", RelativeLocator.class, (f, i) -> {}, (f, o) -> {}); // do nothing;
    register("elbowLiner", ElbowLiner.class, (f, i) -> {}, (f, o) -> {}); // do nothing
    register("curvedLiner", CurvedLiner.class, (f, i) -> {}, (f, o) -> {}); // do nothing

    for (Object[] o : ENUM_TAGS) {
      addEnumClass((String) o[1], (Class) o[0]);
    }
  }

  public static void readImage(ImageFigure figure, DOMInput domInput) throws IOException {
    readBaseData(figure, domInput);
    readDecorator(figure, domInput);
    if (domInput.getElementCount("imageData") > 0) {
      domInput.openElement("imageData");
      String base64Data = domInput.getText();
      if (base64Data != null) {
        figure.setImageData(Base64.decode(base64Data));
      }
      domInput.closeElement();
    }
  }

  public static void writeImage(ImageFigure figure, DOMOutput domOutput) throws IOException {
    writeBaseData(figure, domOutput);
    writeDecorator(figure, domOutput);
    if (figure.getImageData() != null) {
      domOutput.openElement("imageData");
      domOutput.addText(Base64.encodeBytes(figure.getImageData()));
      domOutput.closeElement();
    }
  }

  public static void readGroup(GroupFigure figure, DOMInput domInput) throws IOException {
    domInput.openElement("children");
    for (int i = 0; i < domInput.getElementCount(); i++) {
      figure.basicAdd((Figure) domInput.readObject(i));
    }
    domInput.closeElement();
  }

  public static void writeGroup(GroupFigure figure, DOMOutput domOutput) throws IOException {
    domOutput.openElement("children");
    for (Figure child : figure.getChildren()) {
      domOutput.writeObject(child);
    }
    domOutput.closeElement();
  }

  public static void readArrowTip(ArrowTip figure, DOMInput domInput) throws IOException {
    figure.setAngle(domInput.getAttribute("angle", 0.35f));
    figure.setInnerRadius(domInput.getAttribute("innerRadius", 12f));
    figure.setOuterRadius(domInput.getAttribute("outerRadius", 12f));
    figure.setFilled(domInput.getAttribute("isFilled", false));
    figure.setStroked(domInput.getAttribute("isStroked", false));
    figure.setSolid(domInput.getAttribute("isSolid", false));
  }

  public static void writeArrowTip(ArrowTip figure, DOMOutput domOutput) throws IOException {
    domOutput.addAttribute("angle", figure.getAngle());
    domOutput.addAttribute("innerRadius", figure.getInnerRadius());
    domOutput.addAttribute("outerRadius", figure.getOuterRadius());
    domOutput.addAttribute("isFilled", figure.isFilled());
    domOutput.addAttribute("isStroked", figure.isStroked());
    domOutput.addAttribute("isSolid", figure.isSolid());
  }

  public static void readLineConnection(LineConnectionFigure figure, DOMInput domInput)
      throws IOException {
    figure.removeNode(0);
    readAttributes(figure, domInput);
    readLiner(figure, domInput);
    readPoints(figure, domInput);
    readPointsForLineConnection(figure, domInput);
  }

  public static void readLiner(LineConnectionFigure figure, DOMInput domInput) throws IOException {
    if (domInput.getElementCount("liner") > 0) {
      domInput.openElement("liner");
      figure.setLiner((Liner) domInput.readObject());
      domInput.closeElement();
    } else {
      figure.setLiner(null);
    }
  }

  public static void readPointsForLineConnection(LineConnectionFigure figure, DOMInput domInput)
      throws IOException {
    domInput.openElement("startConnector");
    figure.setStartConnector((Connector) domInput.readObject());
    domInput.closeElement();
    domInput.openElement("endConnector");
    figure.setEndConnector((Connector) domInput.readObject());
    domInput.closeElement();
  }

  public static void writeLineConnection(LineConnectionFigure figure, DOMOutput domOutput)
      throws IOException {
    writePoints(figure, domOutput);
    writePointsForLineConnection(figure, domOutput);
    writeAttributes(figure, domOutput);
    writeLiner(figure, domOutput);
  }

  public static void writeLiner(LineConnectionFigure figure, DOMOutput domOutput)
      throws IOException {
    if (figure.getLiner() != null) {
      domOutput.openElement("liner");
      domOutput.writeObject(figure.getLiner());
      domOutput.closeElement();
    }
  }

  public static void writePointsForLineConnection(LineConnectionFigure figure, DOMOutput domOutput)
      throws IOException {
    domOutput.openElement("startConnector");
    domOutput.writeObject(figure.getStartConnector());
    domOutput.closeElement();
    domOutput.openElement("endConnector");
    domOutput.writeObject(figure.getEndConnector());
    domOutput.closeElement();
  }

  public static void readLocatorConnector(LocatorConnector connector, DOMInput domInput)
      throws IOException {
    readConnector(connector, domInput);

    domInput.openElement("locator");
    connector.setLocator((Locator) domInput.readObject(0));
    domInput.closeElement();
  }

  public static void writeLocatorConnector(LocatorConnector connector, DOMOutput domOutput)
      throws IOException {
    writeConnector(connector, domOutput);
    domOutput.openElement("locator");
    domOutput.writeObject(connector.getLocator());
    domOutput.closeElement();
  }

  public static void readConnector(AbstractConnector connector, DOMInput domInput)
      throws IOException {
    // statePersistent is never set
    //    if (connector.isStatePersistent) {
    //      isConnectToDecorator = in.getAttribute("connectToDecorator", false);
    //    }
    domInput.openElement("Owner");
    connector.setOwner((Figure) domInput.readObject(0));
    domInput.closeElement();
  }

  public static void writeConnector(Connector connector, DOMOutput domOutput) throws IOException {
    // statePersistent is never set
    //    if (isStatePersistent) {
    //      if (isConnectToDecorator) {
    //        out.addAttribute("connectToDecorator", true);
    //      }
    //    }
    domOutput.openElement("Owner");
    domOutput.writeObject(connector.getOwner());
    domOutput.closeElement();
  }

  public static void readBezier(BezierFigure figure, DOMInput domInput) throws IOException {
    readPoints(figure, domInput);
    readAttributes(figure, domInput);
  }

  public static void readPoints(BezierFigure figure, DOMInput domInput) throws IOException {
    while (figure.getNodeCount() > 0) {
      figure.removeNode(0);
    }

    domInput.openElement("points");
    figure.setClosed(domInput.getAttribute("closed", false));
    List<BezierPath.Node> nodes = new ArrayList<>();
    for (int i = 0, n = domInput.getElementCount("p"); i < n; i++) {
      domInput.openElement("p", i);
      BezierPath.Node node =
          new BezierPath.Node(
              domInput.getAttribute("mask", 0),
              domInput.getAttribute("x", 0d),
              domInput.getAttribute("y", 0d),
              domInput.getAttribute("c1x", domInput.getAttribute("x", 0d)),
              domInput.getAttribute("c1y", domInput.getAttribute("y", 0d)),
              domInput.getAttribute("c2x", domInput.getAttribute("x", 0d)),
              domInput.getAttribute("c2y", domInput.getAttribute("y", 0d)));
      node.keepColinear = domInput.getAttribute("colinear", true);
      figure.addNode(node);
      domInput.closeElement();
    }
    domInput.closeElement();
  }

  public static void writeBezier(BezierFigure figure, DOMOutput domOutput) throws IOException {
    writePoints(figure, domOutput);
    writeAttributes(figure, domOutput);
  }

  public static void writePoints(BezierFigure figure, DOMOutput domOutput) throws IOException {
    domOutput.openElement("points");
    if (figure.isClosed()) {
      domOutput.addAttribute("closed", true);
    }
    for (int i = 0, n = figure.getNodeCount(); i < n; i++) {
      BezierPath.Node node = figure.getNode(i);
      domOutput.openElement("p");
      domOutput.addAttribute("mask", node.mask, 0);
      domOutput.addAttribute("colinear", true);
      domOutput.addAttribute("x", node.x[0]);
      domOutput.addAttribute("y", node.y[0]);
      domOutput.addAttribute("c1x", node.x[1], node.x[0]);
      domOutput.addAttribute("c1y", node.y[1], node.y[0]);
      domOutput.addAttribute("c2x", node.x[2], node.x[0]);
      domOutput.addAttribute("c2y", node.y[2], node.y[0]);
      domOutput.closeElement();
    }
    domOutput.closeElement();
  }

  public static void readText(TextFigure figure, DOMInput domInput) throws IOException {
    figure.setBounds(
        new Point2D.Double(domInput.getAttribute("x", 0d), domInput.getAttribute("y", 0d)),
        new Point2D.Double(0, 0));
    readAttributes(figure, domInput);
    readDecorator(figure, domInput);
  }

  public static void readDecorator(DecoratedFigure figure, DOMInput domInput) throws IOException {
    if (domInput.getElementCount("decorator") > 0) {
      domInput.openElement("decorator");
      figure.setDecorator((Figure) domInput.readObject());
      domInput.closeElement();
    } else {
      figure.setDecorator(null);
    }
  }

  public static void writeText(TextFigure figure, DOMOutput domOutput) throws IOException {
    Rectangle2D.Double b = figure.getBounds();
    domOutput.addAttribute("x", b.x);
    domOutput.addAttribute("y", b.y);
    writeAttributes(figure, domOutput);
    writeDecorator(figure, domOutput);
  }

  public static void writeDecorator(DecoratedFigure figure, DOMOutput domOutput)
      throws IOException {
    if (figure.getDecorator() != null) {
      domOutput.openElement("decorator");
      domOutput.writeObject(figure.getDecorator());
      domOutput.closeElement();
    }
  }

  public static void readRoundRectangle(RoundRectangleFigure figure, DOMInput domInput)
      throws IOException {
    readBaseData(figure, domInput);
    figure.setArc(
        domInput.getAttribute("arcWidth", RoundRectangleFigure.DEFAULT_ARC),
        domInput.getAttribute("arcHeight", RoundRectangleFigure.DEFAULT_ARC));
  }

  public static void writeRoundRectangle(RoundRectangleFigure figure, DOMOutput domOutput)
      throws IOException {
    writeBaseData(figure, domOutput);
    domOutput.addAttribute("arcWidth", figure.getArcWidth());
    domOutput.addAttribute("arcHeight", figure.getArcHeight());
  }

  public static void readBaseData(Figure figure, DOMInput domInput) throws IOException {
    readBounds(figure, domInput);
    readAttributes(figure, domInput);
  }

  public static void readBounds(Figure figure, DOMInput domInput) {
    double x = domInput.getAttribute("x", 0d);
    double y = domInput.getAttribute("y", 0d);
    double w = domInput.getAttribute("w", 0d);
    double h = domInput.getAttribute("h", 0d);
    figure.setBounds(new Point2D.Double(x, y), new Point2D.Double(x + w, y + h));
  }

  public static void readAttributes(Figure figure, DOMInput domInput) throws IOException {
    if (domInput.getElementCount("a") > 0) {
      domInput.openElement("a");
      for (int i = 0, n = domInput.getElementCount(); i < n; i++) {
        domInput.openElement(i);
        Object value = domInput.readObject();
        AttributeKey<?> key = AttributeKeys.SUPPORTED_ATTRIBUTES_MAP.get(domInput.getTagName());
        if (key != null && key.isAssignable(value)) {
          figure.attr().set((AttributeKey<Object>) key, value);
        }
        domInput.closeElement();
      }
      domInput.closeElement();
    }
  }

  public static void writeBaseData(Figure figure, DOMOutput domOutput) throws IOException {
    writeBounds(figure, domOutput);
    writeAttributes(figure, domOutput);
  }

  public static void writeAttributes(Figure figure, DOMOutput domOutput) throws IOException {
    Figure prototype = (Figure) domOutput.getPrototype();
    boolean isElementOpen = false;
    for (Map.Entry<AttributeKey<?>, Object> entry : figure.attr().getAttributes().entrySet()) {
      AttributeKey<?> key = entry.getKey();
      if (figure.attr().isAttributeEnabled(key)) {
        Object prototypeValue = prototype.attr().get(key);
        Object attributeValue = figure.attr().get(key);
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

  public static void writeBounds(Figure figure, DOMOutput domOutput) {
    Rectangle2D.Double r = figure.getBounds();
    domOutput.addAttribute("x", r.x);
    domOutput.addAttribute("y", r.y);
    domOutput.addAttribute("w", r.width);
    domOutput.addAttribute("h", r.height);
  }
}
