/*
 * @(#)PathTool.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.odg;

import java.util.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.BezierFigure;
import org.jhotdraw.draw.tool.BezierTool;
import org.jhotdraw.samples.svg.figures.SVGBezierFigure;
import org.jhotdraw.samples.svg.figures.SVGPathFigure;

/** Tool to scribble a ODGPath */
public class PathTool extends BezierTool {

  private static final long serialVersionUID = 1L;

  /** The path prototype for new figures. */
  private SVGPathFigure pathPrototype;

  public PathTool(SVGPathFigure pathPrototype, SVGBezierFigure bezierPrototype) {
    this(pathPrototype, bezierPrototype, null);
  }

  public PathTool(
      SVGPathFigure pathPrototype,
      SVGBezierFigure bezierPrototype,
      Map<AttributeKey<?>, Object> attributes) {
    super(bezierPrototype, attributes);
    this.pathPrototype = pathPrototype;
  }

  @SuppressWarnings("unchecked")
  protected SVGPathFigure createPath() {
    SVGPathFigure f = pathPrototype.clone();
    getEditor().applyDefaultAttributesTo(f);
    if (attributes != null) {
      for (Map.Entry<AttributeKey<?>, Object> entry : attributes.entrySet()) {
        f.attr().set((AttributeKey<Object>) entry.getKey(), entry.getValue());
      }
    }
    return f;
  }

  @Override
  protected void finishCreation(BezierFigure createdFigure, DrawingView creationView) {
    creationView.getDrawing().remove(createdFigure);
    SVGPathFigure createdPath = createPath();
    createdPath.removeAllChildren();
    createdPath.add(createdFigure);
    creationView.getDrawing().add(createdPath);
    creationView.addToSelection(createdPath);
    fireUndoEvent(createdPath, creationView);
  }
}
