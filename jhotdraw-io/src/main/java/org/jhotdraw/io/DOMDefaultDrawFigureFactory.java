/*
 * @(#)DrawFigureFactory.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.io;

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
import org.jhotdraw.xml.DefaultDOMFactory;

/**
 * DrawFigureFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
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
    register("diamond", DiamondFigure.class);
    register("triangle", TriangleFigure.class);
    register("bezier", BezierFigure.class);
    register("r", RectangleFigure.class);
    register("rr", RoundRectangleFigure.class);
    register("l", LineFigure.class);
    register("b", BezierFigure.class);
    register("lnk", LineConnectionFigure.class);
    register("e", EllipseFigure.class);
    register("t", TextFigure.class);
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
}
