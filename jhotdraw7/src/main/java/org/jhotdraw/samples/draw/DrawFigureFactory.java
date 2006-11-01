/*
 * @(#)DrawFigureFactory.java  1.0  February 17, 2004
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.samples.draw;

import java.util.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.xml.DefaultDOMFactory;
/**
 * DrawFigureFactory.
 *
 * @author  Werner Randelshofer
 * @version 1.0 February 17, 2004 Created.
 */
public class DrawFigureFactory extends DefaultDOMFactory {
    private final static Object[][] classTagArray = {
        { DefaultDrawing.class, "drawing" },
        { QuadTreeDrawing.class, "drawing" },
        { DiamondFigure.class, "diamond" },
        { TriangleFigure.class, "triangle" },
        { BezierFigure.class, "bezier" },
        { RectangleFigure.class, "r" },
        { RoundRectangleFigure.class, "rr" },
        { LineFigure.class, "l" },
        { BezierFigure.class, "b" },
        { LineConnectionFigure.class, "lnk" },
        { EllipseFigure.class, "e" },
        { TextFigure.class, "t" },
        { TextAreaFigure.class, "ta" },
        { GroupFigure.class, "g" },
        
        { ArrowTip.class, "arrowTip" },
        { ChopBoxConnector.class, "rConnector" },
        { ChopEllipseConnector.class, "ellipseConnector" },
        { ChopRoundRectConnector.class, "rrConnector" },
        { ChopTriangleConnector.class, "triangleConnector" },
        { ChopDiamondConnector.class, "diamondConnector" },
        { ChopBezierConnector.class, "bezierConnector" },
        
        { ElbowLiner.class, "elbowLiner" },
    };
    private final static Object[][] enumTagArray = {
        { AttributeKeys.StrokePlacement.class, "strokePlacement" },
        { AttributeKeys.StrokeType.class, "strokeType" },
        { AttributeKeys.Underfill.class, "underfill" },
        { AttributeKeys.Orientation.class, "orientation" },
    };
    
    /** Creates a new instance. */
    public DrawFigureFactory() {
        for (Object[] o : classTagArray) {
            addStorableClass((String) o[1], (Class) o[0]);
        }
        for (Object[] o : enumTagArray) {
            addEnumClass((String) o[1], (Class) o[0]);
        }
    }
}
