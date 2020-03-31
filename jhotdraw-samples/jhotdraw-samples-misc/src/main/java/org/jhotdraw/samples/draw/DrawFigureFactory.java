/*
 * @(#)DrawFigureFactory.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.draw;

import org.jhotdraw.draw.figure.ImageFigure;
import org.jhotdraw.draw.figure.EllipseFigure;
import org.jhotdraw.draw.figure.BezierFigure;
import org.jhotdraw.draw.figure.TriangleFigure;
import org.jhotdraw.draw.figure.RectangleFigure;
import org.jhotdraw.draw.figure.GroupFigure;
import org.jhotdraw.draw.figure.DiamondFigure;
import org.jhotdraw.draw.figure.TextAreaFigure;
import org.jhotdraw.draw.figure.RoundRectangleFigure;
import org.jhotdraw.draw.figure.TextFigure;
import org.jhotdraw.draw.figure.LineFigure;
import org.jhotdraw.draw.figure.LineConnectionFigure;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connector.ChopBezierConnector;
import org.jhotdraw.draw.connector.ChopDiamondConnector;
import org.jhotdraw.draw.connector.ChopEllipseConnector;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.ChopRoundRectangleConnector;
import org.jhotdraw.draw.connector.ChopTriangleConnector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.liner.CurvedLiner;
import org.jhotdraw.draw.liner.ElbowLiner;
import org.jhotdraw.xml.DefaultDOMFactory;

/**
 * DrawFigureFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DrawFigureFactory extends DefaultDOMFactory {

    private static final Object[][] CLASS_TAGS = {
        {DefaultDrawing.class, "drawing"},
        {QuadTreeDrawing.class, "drawing"},
        {DiamondFigure.class, "diamond"},
        {TriangleFigure.class, "triangle"},
        {BezierFigure.class, "bezier"},
        {RectangleFigure.class, "r"},
        {RoundRectangleFigure.class, "rr"},
        {LineFigure.class, "l"},
        {BezierFigure.class, "b"},
        {LineConnectionFigure.class, "lnk"},
        {EllipseFigure.class, "e"},
        {TextFigure.class, "t"},
        {TextAreaFigure.class, "ta"},
        {ImageFigure.class, "image"},
        {GroupFigure.class, "g"},
        {ArrowTip.class, "arrowTip"},
        {ChopRectangleConnector.class, "rConnector"},
        {ChopEllipseConnector.class, "ellipseConnector"},
        {ChopRoundRectangleConnector.class, "rrConnector"},
        {ChopTriangleConnector.class, "triangleConnector"},
        {ChopDiamondConnector.class, "diamondConnector"},
        {ChopBezierConnector.class, "bezierConnector"},
        {ElbowLiner.class, "elbowLiner"},
        {CurvedLiner.class, "curvedLiner"}};
    
    private static final Object[][] ENUM_TAGS = {
        {AttributeKeys.StrokePlacement.class, "strokePlacement"},
        {AttributeKeys.StrokeType.class, "strokeType"},
        {AttributeKeys.Underfill.class, "underfill"},
        {AttributeKeys.Orientation.class, "orientation"}};

    /**
     * Creates a new instance.
     */
    public DrawFigureFactory() {
        for (Object[] o : CLASS_TAGS) {
            addStorableClass((String) o[1], (Class) o[0]);
        }
        for (Object[] o : ENUM_TAGS) {
            addEnumClass((String) o[1], (Class) o[0]);
        }
    }
}
