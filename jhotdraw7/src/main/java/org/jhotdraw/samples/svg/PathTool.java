/*
 * @(#)PathTool.java  1.0  2006-07-12
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

import org.jhotdraw.samples.svg.figures.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.util.*;
import org.jhotdraw.undo.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import org.jhotdraw.geom.*;
/**
 * Tool to scribble a SVGPath
 *
 * @author  Werner Randelshofer
 * @version 1.0 2006-07-12 Created.
 */
public class PathTool extends BezierTool {
    /**
     * The path prototype for new figures.
     */
    private SVGPath pathPrototype;
    
    /** Creates a new instance. */
    public PathTool(SVGPath pathPrototype, BezierFigure bezierPrototype) {
        this(pathPrototype, bezierPrototype, null);
    }
    /** Creates a new instance. */
    public PathTool(SVGPath pathPrototype, BezierFigure bezierPrototype, Map attributes) {
        super(bezierPrototype, attributes);
        this.pathPrototype = pathPrototype;
    }
    protected SVGPath createPath() {
        SVGPath f = (SVGPath) pathPrototype.clone();
        getEditor().applyDefaultAttributesTo(f);
        if (attributes != null) {
            for (Map.Entry<AttributeKey, Object> entry : attributes.entrySet()) {
                f.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        return f;
    }
    protected void finishCreation(BezierFigure createdFigure) {
        getDrawing().remove(createdFigure);
        SVGPath createdPath = createPath();
        createdPath.removeAllChildren();
        createdPath.add(createdFigure);
        getDrawing().add(createdPath);
        getView().addToSelection(createdPath);
    }
}
