/*
 * @(#)PertFactory.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.net;

import org.jhotdraw.draw.figure.GroupFigure;
import org.jhotdraw.draw.figure.TextAreaFigure;
import org.jhotdraw.draw.figure.LineConnectionFigure;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.geom.Insets2D;
import org.jhotdraw.samples.net.figures.NodeFigure;
import org.jhotdraw.xml.*;

/**
 * NetFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
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
        {RelativeLocator.class, "relativeLoc"}};
    
    private static final Object[][] ENUM_TAGS = {
        {AttributeKeys.StrokeType.class, "strokeType"}};

    /**
     * Creates a new instance.
     */
    public NetFactory() {
        for (Object[] o : CLASS_TAGS) {
            addStorableClass((String) o[1], (Class) o[0]);
        }
        for (Object[] o : ENUM_TAGS) {
            addEnumClass((String) o[1], (Class) o[0]);
        }
    }
}
