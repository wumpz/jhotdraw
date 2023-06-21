/*
 * @(#)PertFactory.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.pert;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.figure.GroupFigure;
import org.jhotdraw.draw.figure.ListFigure;
import org.jhotdraw.draw.figure.TextAreaFigure;
import org.jhotdraw.draw.figure.TextFigure;
import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.io.DOMDefaultDrawFigureFactory;
import org.jhotdraw.samples.pert.figures.DependencyFigure;
import org.jhotdraw.samples.pert.figures.SeparatorLineFigure;
import org.jhotdraw.samples.pert.figures.TaskFigure;
import org.jhotdraw.xml.*;

/** PertFactory. */
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

  public PertFactory() {
    register("Net", DefaultDrawing.class, null, null); // do not allow processing
    register(
        "t",
        TextFigure.class,
        DOMDefaultDrawFigureFactory::readText,
        DOMDefaultDrawFigureFactory::writeText);
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
        "rectConnector",
        ChopRectangleConnector.class,
        DOMDefaultDrawFigureFactory::readConnector,
        DOMDefaultDrawFigureFactory::writeConnector);
    register(
        "locConnector",
        LocatorConnector.class,
        DOMDefaultDrawFigureFactory::readLocatorConnector,
        DOMDefaultDrawFigureFactory::writeLocatorConnector);
    register(
        "arrowTip",
        ArrowTip.class,
        DOMDefaultDrawFigureFactory::readArrowTip,
        DOMDefaultDrawFigureFactory::writeArrowTip);

    register("relativeLoc", RelativeLocator.class, (f, i) -> {}, (f, o) -> {}); // do nothing;

    //    for (Object[] o : CLASS_TAGS) {
    //      register((String) o[1], (Class) o[0], null, null);
    //    }
  }
}
