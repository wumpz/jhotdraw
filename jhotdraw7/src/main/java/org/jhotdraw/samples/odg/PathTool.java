/*
 * @(#)PathTool.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.samples.odg;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.jhotdraw.draw.tool.BezierTool;
import org.jhotdraw.samples.svg.figures.*;
import org.jhotdraw.draw.*;
import java.util.*;
/**
 * Tool to scribble a ODGPath
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class PathTool extends BezierTool {
    private static final long serialVersionUID = 1L;
    /**
     * Set this to true to turn on debugging output on System.out.
     */
    private static final boolean DEBUG = false;
    /**
     * The path prototype for new figures.
     */
    private SVGPathFigure pathPrototype;
    
    /** Creates a new instance. */
    public PathTool(SVGPathFigure pathPrototype, SVGBezierFigure bezierPrototype) {
        this(pathPrototype, bezierPrototype, null);
    }
    /** Creates a new instance. */
    public PathTool(SVGPathFigure pathPrototype, SVGBezierFigure bezierPrototype, @Nullable Map<AttributeKey<?>,Object> attributes) {
        super(bezierPrototype, attributes);
        this.pathPrototype = pathPrototype;
    }
    @SuppressWarnings("unchecked")
    protected SVGPathFigure createPath() {
        SVGPathFigure f = pathPrototype.clone();
        getEditor().applyDefaultAttributesTo(f);
        if (attributes != null) {
            for (Map.Entry<AttributeKey<?>, Object> entry : attributes.entrySet()) {
                f.set((AttributeKey<Object>)entry.getKey(), entry.getValue());
            }
        }
        return f;
    }
    @Override protected void finishCreation(BezierFigure createdFigure, DrawingView creationView) {
        if (DEBUG) System.out.println("PathTool.finishCreation "+createdFigure);
        creationView.getDrawing().remove(createdFigure);
        SVGPathFigure createdPath = createPath();
        createdPath.removeAllChildren();
        createdPath.add(createdFigure);
        creationView.getDrawing().add(createdPath);
        creationView.addToSelection(createdPath);
        fireUndoEvent(createdPath, creationView);
    }
}
