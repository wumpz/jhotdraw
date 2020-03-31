/*
 * @(#)PertFactory.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.pert;

import org.jhotdraw.draw.figure.GroupFigure;
import org.jhotdraw.draw.figure.TextAreaFigure;
import org.jhotdraw.draw.figure.TextFigure;
import org.jhotdraw.draw.figure.ListFigure;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.samples.pert.figures.DependencyFigure;
import org.jhotdraw.samples.pert.figures.SeparatorLineFigure;
import org.jhotdraw.samples.pert.figures.TaskFigure;
import org.jhotdraw.xml.*;

/**
 * PertFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PertFactory extends DefaultDOMFactory {

    private static final Object[][] CLASS_TAGS = {
        {DefaultDrawing.class, "PertDiagram"},
        {TaskFigure.class, "task"},
        {DependencyFigure.class, "dep"},
        {ListFigure.class, "list"},
        {TextFigure.class, "text"},
        {GroupFigure.class, "g"},
        {TextAreaFigure.class, "ta"},
        {SeparatorLineFigure.class, "separator"},
        {ChopRectangleConnector.class, "rectConnector"},
        {LocatorConnector.class, "locConnector"},
        {RelativeLocator.class, "relativeLocator"},
        {ArrowTip.class, "arrowTip"}
    };

    /**
     * Creates a new instance.
     */
    public PertFactory() {
        for (Object[] o : CLASS_TAGS) {
            addStorableClass((String) o[1], (Class) o[0]);
        }
    }
}
