/*
 * @(#)PertFactory.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.net;

import static org.jhotdraw.io.DOMDefaultDrawFigureFactory.readAttributes;
import static org.jhotdraw.io.DOMDefaultDrawFigureFactory.writeAttributes;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.figure.GroupFigure;
import org.jhotdraw.draw.figure.LineConnectionFigure;
import org.jhotdraw.draw.figure.TextAreaFigure;
import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.geom.Insets2D;
import org.jhotdraw.io.DOMDefaultDrawFigureFactory;
import org.jhotdraw.samples.net.figures.NodeFigure;
import org.jhotdraw.xml.*;

/** NetFactory. */
public class NetFactory extends DefaultDOMFactory {

  private static final Object[][] CLASS_TAGS = {
    {DefaultDrawing.class, "Net"},
    {NodeFigure.class, "node"},
    {LineConnectionFigure.class, "link"},
    {GroupFigure.class, "g"},
    {GroupFigure.class, "g"},
    {TextAreaFigure.class, "ta"},
    {LocatorConnector.class, "locConnect"},
    {ChopRectangleConnector.class, "rectConnect"},
    {ArrowTip.class, "arrowTip"},
    {Insets2D.Double.class, "insets"},
    {RelativeLocator.class, "relativeLoc"}
  };

  private static final Object[][] ENUM_TAGS = {{AttributeKeys.StrokeType.class, "strokeType"}};

  public NetFactory() {
    register("Net", DefaultDrawing.class, null, null); // do not allow processing
    register("node", NodeFigure.class, NetFactory::readNode, NetFactory::writeNode);
    register(
        "link",
        LineConnectionFigure.class,
        DOMDefaultDrawFigureFactory::readLineConnection,
        DOMDefaultDrawFigureFactory::writeLineConnection);
    register(
        "g",
        GroupFigure.class,
        DOMDefaultDrawFigureFactory::readGroup,
        DOMDefaultDrawFigureFactory::writeGroup);
    register(
        "ta",
        TextAreaFigure.class,
        DOMDefaultDrawFigureFactory::readBaseData,
        DOMDefaultDrawFigureFactory::writeBaseData);
    register(
        "locConnect",
        LocatorConnector.class,
        DOMDefaultDrawFigureFactory::readLocatorConnector,
        DOMDefaultDrawFigureFactory::writeLocatorConnector);
    register(
        "rectConnect",
        ChopRectangleConnector.class,
        DOMDefaultDrawFigureFactory::readConnector,
        DOMDefaultDrawFigureFactory::writeConnector);
    register(
        "arrowTip",
        ArrowTip.class,
        DOMDefaultDrawFigureFactory::readArrowTip,
        DOMDefaultDrawFigureFactory::writeArrowTip);

    register("insets", Insets2D.Double.class, (f, i) -> {}, (f, o) -> {}); // do nothing;
    register("relativeLoc", RelativeLocator.class, (f, i) -> {}, (f, o) -> {}); // do nothing;

    for (Object[] o : ENUM_TAGS) {
      addEnumClass((String) o[1], (Class) o[0]);
    }
  }

  public static void readNode(NodeFigure figure, DOMInput domInput) throws IOException {
    figure.setBounds(
        new Point2D.Double(domInput.getAttribute("x", 0d), domInput.getAttribute("y", 0d)),
        new Point2D.Double(0, 0));
    readAttributes(figure, domInput);
  }

  public static void writeNode(NodeFigure figure, DOMOutput domOutput) throws IOException {
    Rectangle2D.Double b = figure.getBounds();
    domOutput.addAttribute("x", b.x);
    domOutput.addAttribute("y", b.y);
    writeAttributes(figure, domOutput);
  }
}
