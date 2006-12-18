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

package org.jhotdraw.samples.svg;

import java.util.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.samples.svg.figures.*;
import org.jhotdraw.xml.*;
/**
 * DrawFigureFactory.
 * 
 * @deprecated  This class will be removed soon.
 *
 * @author  Werner Randelshofer
 * @version 1.0 February 17, 2004 Created.
 */
public class SVGFigureFactoryOld extends DefaultDOMFactory {
    private final static Object[][] classTagArray = {
        { SVGDrawing.class, "svg" },
        { SVGGroupFigure.class, "defs" }, 
        { SVGGroupFigure.class, "g" },
        { SVGTextFigure.class, "text" },
        { SVGRectFigure.class, "rect" },
       // { SVGLine.class, "line" },
        { SVGImageFigure.class, "image" },
        
        // SVC circle element is presented by an SVGEllipse figure
        { SVGEllipseFigure.class, "circle" },
        { SVGEllipseFigure.class, "ellipse" },
        
        // SVC polyline and polygon elements are presented by an 
        // SVGPath figure
        { SVGPathFigure.class, "polyline" },
        { SVGPathFigure.class, "polygon" },
        { SVGPathFigure.class, "path" },
    };
    private final static Object[][] enumTagArray = {
    };
    
    /** Creates a new instance. */
    public SVGFigureFactoryOld() {
        for (Object[] o : classTagArray) {
            addStorableClass((String) o[1], (Class) o[0]);
        }
        for (Object[] o : enumTagArray) {
            addEnumClass((String) o[1], (Class) o[0]);
        }
    }
}
