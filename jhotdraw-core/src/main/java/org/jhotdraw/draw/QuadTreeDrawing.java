/*
 * @(#)QuadTreeDrawing.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.geom.QuadTree;
import org.jhotdraw.util.*;

/**
 * An implementation of {@link Drawing} which uses a {@link org.jhotdraw.geom.QuadTree} to provide a
 * good responsiveness for drawings which contain many figures.
 */
public class QuadTreeDrawing extends AbstractDrawing {

  private static final long serialVersionUID = 1L;
  private QuadTree<Figure> quadTree = new QuadTree<>();
  private boolean needsSorting = false;

  @Override
  public int indexOf(Figure figure) {
    return CHILDREN.indexOf(figure);
  }

  @Override
  public void basicAdd(int index, Figure figure) {
    super.basicAdd(index, figure);
    quadTree.add(figure, figure.getDrawingArea());
    needsSorting = true;
  }

  @Override
  public Figure basicRemoveChild(int index) {
    Figure figure = getChild(index);
    quadTree.remove(figure);
    needsSorting = true;
    super.basicRemoveChild(index);
    return figure;
  }

  @Override
  public void draw(Graphics2D g) {
    Rectangle2D clipBounds = g.getClipBounds();
    if (clipBounds != null) {
      draw(g, sort(quadTree.findIntersects(clipBounds)));
    } else {
      draw(g, CHILDREN);
    }
  }

  /** Implementation note: Sorting can not be done for orphaned children. */
  @Override
  public List<Figure> sort(Collection<? extends Figure> c) {
    List<Figure> sorted = new ArrayList<>(c);
    Collections.sort(sorted, Comparator.comparing(Figure::getLayer));
    return sorted;
  }

  public void draw(Graphics2D g, Collection<Figure> c) {
    for (Figure f : c) {
      if (f.isVisible()) {
        f.draw(g);
      }
    }
  }

  public List<Figure> getChildren(Rectangle2D.Double bounds) {
    return new ArrayList<>(quadTree.findInside(bounds));
  }

  @Override
  public List<Figure> getChildren() {
    return UNMODIFIABLE_CHILDREN;
  }

  @Override
  public Figure findFigureInside(Point2D.Double p) {
    Collection<Figure> c = quadTree.findContains(p);
    for (Figure f : getFiguresFrontToBack(c)) {
      if (c.contains(f) && f.contains(p)) {
        return f.findFigureInside(p);
      }
    }
    return null;
  }

  /** Returns an iterator to iterate in Z-order front to back over the children. */
  @Override
  public List<Figure> getFiguresFrontToBack() {
    ensureSorted();
    return new ReversedList<>(CHILDREN);
  }

  protected List<Figure> getFiguresFrontToBack(Collection<Figure> smallCollection) {
    List<Figure> list = new ArrayList<>(smallCollection);
    Collections.sort(list, Comparator.comparing(Figure::getLayer).reversed());
    return list;
  }

  @Override
  public Figure findFigure(Point2D.Double p) {
    Collection<Figure> c = quadTree.findContains(p);
    switch (c.size()) {
      case 0:
        return null;
      case 1:
        Figure f = c.iterator().next();
        return (f.contains(p)) ? f : null;
      default:
        for (Figure f2 : getFiguresFrontToBack(c)) {
          if (f2.contains(p)) {
            return f2;
          }
        }
        return null;
    }
  }

  @Override
  public Figure findFigureExcept(Point2D.Double p, Figure ignore) {
    Collection<Figure> c = quadTree.findContains(p);
    switch (c.size()) {
      case 0:
        return null;
      case 1:
        Figure f = c.iterator().next();
        return (f == ignore || !f.contains(p)) ? null : f;
      default:
        for (Figure f2 : getFiguresFrontToBack(c)) {
          if (f2 != ignore && f2.contains(p)) {
            return f2;
          }
        }
        return null;
    }
  }

  @Override
  public Figure findFigureExcept(Point2D.Double p, Collection<? extends Figure> ignore) {
    Collection<Figure> c = quadTree.findContains(p);
    switch (c.size()) {
      case 0:
        return null;
      case 1:
        Figure f = c.iterator().next();
        return (!ignore.contains(f) || !f.contains(p)) ? null : f;
      default:
        for (Figure f2 : getFiguresFrontToBack(c)) {
          if (!ignore.contains(f2) && f2.contains(p)) {
            return f2;
          }
        }
        return null;
    }
  }

  @Override
  public Figure findFigureBehind(Point2D.Double p, Figure figure) {
    boolean isBehind = false;
    for (Figure f : getFiguresFrontToBack()) {
      if (isBehind) {
        if (f.isVisible() && f.contains(p)) {
          return f;
        }
      } else {
        isBehind = figure == f;
      }
    }
    return null;
  }

  @Override
  public Figure findFigureBehind(Point2D.Double p, Collection<? extends Figure> children) {
    int inFrontOf = children.size();
    for (Figure f : getFiguresFrontToBack()) {
      if (inFrontOf == 0) {
        if (f.isVisible() && f.contains(p)) {
          return f;
        }
      } else {
        if (children.contains(f)) {
          inFrontOf--;
        }
      }
    }
    return null;
  }

  @Override
  public List<Figure> findFigures(Rectangle2D.Double r) {
    List<Figure> c = new ArrayList<>(quadTree.findIntersects(r));
    switch (c.size()) {
      case 0:
        // fall through
      case 1:
        return c;
      default:
        return getFiguresFrontToBack(c);
    }
  }

  @Override
  public List<Figure> findFiguresWithin(Rectangle2D.Double bounds) {
    List<Figure> contained = new ArrayList<>();
    for (Figure f : CHILDREN) {
      Rectangle2D.Double r = f.getBounds();
      if (f.attr().get(TRANSFORM) != null) {
        Rectangle2D rt = f.attr().get(TRANSFORM).createTransformedShape(r).getBounds2D();
        r =
            (rt instanceof Rectangle2D.Double)
                ? (Rectangle2D.Double) rt
                : new Rectangle2D.Double(rt.getX(), rt.getY(), rt.getWidth(), rt.getHeight());
      }
      if (f.isVisible() && Geom.contains(bounds, r)) {
        contained.add(f);
      }
    }
    return contained;
  }

  @Override
  public void bringToFront(Figure figure) {
    if (CHILDREN.remove(figure)) {
      CHILDREN.add(figure);
      needsSorting = true;
      fireDrawingChanged(figure.getDrawingArea());
    }
  }

  @Override
  public void sendToBack(Figure figure) {
    if (CHILDREN.remove(figure)) {
      CHILDREN.add(0, figure);
      needsSorting = true;
      fireDrawingChanged(figure.getDrawingArea());
    }
  }

  /** Ensures that the children are sorted in z-order sequence. */
  private void ensureSorted() {
    if (needsSorting) {
      Collections.sort(CHILDREN, Comparator.comparing(Figure::getLayer));
      needsSorting = false;
    }
  }

  @Override
  public QuadTreeDrawing clone() {
    QuadTreeDrawing that = (QuadTreeDrawing) super.clone();
    that.quadTree = new QuadTree<>();
    for (Figure f : getChildren()) {
      quadTree.add(f, f.getDrawingArea());
    }
    return that;
  }

  @Override
  protected EventHandler createEventHandler() {
    return new QuadTreeEventHandler();
  }

  @Override
  public Figure findFigure(Point2D.Double p, double scaleDenominator) {
    double tolerance = 10 / 2 / scaleDenominator;
    Rectangle2D.Double rect =
        new Rectangle2D.Double(p.x - tolerance, p.y - tolerance, 2 * tolerance, 2 * tolerance);
    for (Figure figure : findFigures(rect)) {
      if (figure.isVisible() && figure.contains(p, scaleDenominator)) {
        return figure;
      }
    }
    return null;
  }

  @Override
  public Figure findFigureBehind(Point2D.Double p, double scaleDenominator, Figure behindFigure) {
    double tolerance = 10 / 2 / scaleDenominator;
    Rectangle2D.Double rect =
        new Rectangle2D.Double(p.x - tolerance, p.y - tolerance, 2 * tolerance, 2 * tolerance);
    boolean check = false;
    for (Figure figure : findFigures(rect)) {
      if (check && figure.isVisible() && figure.contains(p, scaleDenominator)) {
        return figure;
      } else if (figure == behindFigure) {
        check = true;
      }
    }
    return null;
  }

  /** Handles all figure events fired by Figures contained in the Drawing. */
  protected class QuadTreeEventHandler extends AbstractDrawing.EventHandler {

    private static final long serialVersionUID = 1L;

    @Override
    public void figureChanged(FigureEvent e) {
      if (!isChanging()) {
        quadTree.remove(e.getFigure());
        quadTree.add(e.getFigure(), e.getFigure().getDrawingArea());
        needsSorting = true;
        invalidate();
        fireDrawingChanged(e.getInvalidatedArea());
      }
    }
  }

  @Override
  public void drawCanvas(Graphics2D g) {
    if (attr().get(CANVAS_WIDTH) != null && attr().get(CANVAS_HEIGHT) != null) {
      // Determine canvas color and opacity
      Color canvasColor = attr().get(CANVAS_FILL_COLOR);
      Double fillOpacity = attr().get(CANVAS_FILL_OPACITY);
      if (canvasColor != null && fillOpacity > 0) {
        canvasColor =
            new Color((canvasColor.getRGB() & 0xffffff) | ((int) (fillOpacity * 255) << 24), true);
        // Fill the canvas
        Rectangle2D.Double r =
            new Rectangle2D.Double(0, 0, attr().get(CANVAS_WIDTH), attr().get(CANVAS_HEIGHT));
        g.setColor(canvasColor);
        g.fill(r);
      }
    }
  }
}
