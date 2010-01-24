/*
 * @(#)PertFactory.java
 *
 * Copyright (c) 1996-2010 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.samples.net;

import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.geom.*;
import org.jhotdraw.samples.net.figures.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.xml.*;
/**
 * NetFactory.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NetFactory extends DefaultDOMFactory {
    private final static Object[][] classTagArray = {
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
    private final static Object[][] enumTagArray = {
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
