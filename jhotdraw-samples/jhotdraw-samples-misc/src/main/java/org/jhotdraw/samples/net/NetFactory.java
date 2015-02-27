/*
 * @(#)PertFactory.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.samples.net;

import org.jhotdraw.geom.Insets2D;
import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.*;
import org.jhotdraw.samples.net.figures.NodeFigure;
import org.jhotdraw.xml.*;
/**
 * NetFactory.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NetFactory extends DefaultDOMFactory {
    private static final Object[][] classTagArray = {
        { DefaultDrawing.class, "Net" },
        { NodeFigure.class, "node" },
        { LineConnectionFigure.class, "link" },
        { GroupFigure.class, "g" },
        { GroupFigure.class, "g" },
        { TextAreaFigure.class, "ta" },
        
        { LocatorConnector.class, "locConnect" },
        { ChopRectangleConnector.class, "rectConnect" },
        { ArrowTip.class, "arrowTip" },
        { Insets2D.Double.class, "insets" },
        { RelativeLocator.class, "relativeLoc" },
        };
    private static final Object[][] enumTagArray = {
        { AttributeKeys.StrokeType.class, "strokeType" },
    };
    
    /** Creates a new instance. */
    public NetFactory() {
        for (Object[] o : classTagArray) {
            addStorableClass((String) o[1], (Class) o[0]);
        }
        for (Object[] o : enumTagArray) {
            addEnumClass((String) o[1], (Class) o[0]);
        }
    }
}
