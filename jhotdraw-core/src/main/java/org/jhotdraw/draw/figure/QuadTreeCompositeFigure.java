/*
 * @(#)QuadTreeCompositeFigure.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.event.FigureListenerAdapter;
import org.jhotdraw.utils.geom.Dimension2DDouble;
import org.jhotdraw.utils.geom.QuadTree;
import org.jhotdraw.utils.util.ReversedList;

/** QuadTreeCompositeFigure. */
public abstract class QuadTreeCompositeFigure extends AbstractAttributedCompositeFigure {

  private static final long serialVersionUID = 1L;
  private final QuadTree<Figure> quadTree = new QuadTree<>();
  private boolean needsSorting = false;
  private final FigureHandler figureHandler = new FigureHandler();
  private Dimension2DDouble canvasSize;

  public QuadTreeCompositeFigure() {}

  @Override
  public int indexOf(Figure figure) {
    return children.indexOf(figure);
  }

  @Override
  public void basicAdd(int index, Figure figure) {
    children.add(index, figure);
    quadTree.add(figure, figure.getDrawingArea());
    figure.addFigureListener(figureHandler);
    needsSorting = true;
  }

  @Override
  public Figure basicRemoveChild(int index) {
    Figure figure = children.get(index);
    children.remove(index);
    quadTree.remove(figure);
    figure.removeFigureListener(figureHandler);
    needsSorting = true;
    return figure;
  }

  @Override
  public void draw(Graphics2D g) {
    Rectangle2D clipBounds = g.getClipBounds();
    if (clipBounds != null) {
      Collection<Figure> c = quadTree.findIntersects(clipBounds);
      Collection<Figure> toDraw = sort(c);
      draw(g, toDraw);
    } else {
      draw(g, children);
    }
  }

  /** Implementation note: Sorting can not be done for orphaned children. */
  public java.util.List<Figure> sort(Collection<Figure> c) {
    ensureSorted();
    ArrayList<Figure> sorted = new ArrayList<>(c.size());
    for (Figure f : children) {
      if (c.contains(f)) {
        sorted.add(f);
      }
    }
    return sorted;
  }

  public void draw(Graphics2D g, Collection<Figure> c) {
    for (Figure f : c) {
      f.draw(g);
    }
  }

  //  public Collection<Figure> getFigures(Rectangle2D.Double bounds) {
  //    return new ArrayList<>(quadTree.findInside(bounds));
  //  }

  @Override
  public java.util.List<Figure> getChildren() {
    return Collections.unmodifiableList(children);
  }

  @Override
  public Figure findFigureInside(Point2D.Double p) {
    Collection<Figure> c = quadTree.findContains(p);
    for (Figure f : getFiguresFrontToBack()) {
      if (c.contains(f) && f.contains(p)) {
        return f.findFigureInside(p);
      }
    }
    return null;
  }

  /** Returns an iterator to iterate in Z-order front to back over the children. */
  public java.util.List<Figure> getFiguresFrontToBack() {
    ensureSorted();
    return new ReversedList<>(children);
  }

  public Figure findFigure(Point2D.Double p) {
    Collection<Figure> c = quadTree.findContains(p);
    switch (c.size()) {
      case 0:
        return null;
      case 1:
        Figure f = c.iterator().next();
        return (f.contains(p)) ? f : null;

      default:
        for (Figure f2 : getFiguresFrontToBack()) {
          if (c.contains(f2) && f2.contains(p)) {
            return f2;
          }
        }
        return null;
    }
  }

  public Figure findFigureExcept(Point2D.Double p, Figure ignore) {
    Collection<Figure> c = quadTree.findContains(p);
    switch (c.size()) {
      case 0:
        return null;
      case 1:
        Figure f = c.iterator().next();
        return (f == ignore || !f.contains(p)) ? null : f;
      default:
        for (Figure f2 : getFiguresFrontToBack()) {
          if (f2 != ignore && f2.contains(p)) {
            return f2;
          }
        }
        return null;
    }
  }

  public Figure findFigureExcept(Point2D.Double p, Collection<Figure> ignore) {
    Collection<Figure> c = quadTree.findContains(p);
    switch (c.size()) {
      case 0:
        return null;
      case 1:
        Figure f = c.iterator().next();
        return (!ignore.contains(f) || !f.contains(p)) ? null : f;
      default:
        for (Figure f2 : getFiguresFrontToBack()) {
          if (!ignore.contains(f2) && f2.contains(p)) {
            return f2;
          }
        }
        return null;
    }
  }

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

  public Figure findFigureBehind(Point2D.Double p, Collection<Figure> figures) {
    int inFrontOf = figures.size();
    for (Figure f : getFiguresFrontToBack()) {
      if (inFrontOf == 0) {
        if (f.isVisible() && f.contains(p)) {
          return f;
        }
      } else {
        if (figures.contains(f)) {
          inFrontOf--;
        }
      }
    }
    return null;
  }

  public List<Figure> findFigures(Rectangle2D.Double r) {
    List<Figure> c = new ArrayList<>(quadTree.findIntersects(r));
    switch (c.size()) {
      case 0:
        // fall through
      case 1:
        return c;
      default:
        return sort(c);
    }
  }

  public List<Figure> findFiguresWithin(Rectangle2D.Double bounds) {
    List<Figure> contained = new ArrayList<>();
    for (Figure f : children) {
      Rectangle2D r = f.getBounds();
      if (f.attr().get(TRANSFORM) != null) {
        r = f.attr().get(TRANSFORM).createTransformedShape(r).getBounds2D();
      }
      if (f.isVisible() && bounds.contains(r)) {
        contained.add(f);
      }
    }
    return contained;
  }

  @Override
  public void bringToFront(Figure figure) {
    if (children.remove(figure)) {
      children.add(figure);
      needsSorting = true;
      fireAreaInvalidated(figure.getDrawingArea());
    }
  }

  @Override
  public void sendToBack(Figure figure) {
    if (children.remove(figure)) {
      children.add(0, figure);
      needsSorting = true;
      fireAreaInvalidated(figure.getDrawingArea());
    }
  }

  @Override
  public boolean contains(Figure f) {
    return children.contains(f);
  }

  /** Ensures that the children are sorted in z-order sequence. */
  private void ensureSorted() {
    if (needsSorting) {
      Collections.sort(children, Comparator.comparing(Figure::getLayer));
      needsSorting = false;
    }
  }

  public void setCanvasSize(Dimension2DDouble newValue) {
    Dimension2DDouble oldValue = canvasSize;
    canvasSize = newValue;
  }

  public Dimension2DDouble getCanvasSize() {
    return canvasSize;
  }

  /** Handles all figure events fired by Figures contained in the Drawing. */
  protected class FigureHandler extends FigureListenerAdapter implements UndoableEditListener {

    /**
     * We propagate all edit events from our children to undoable edit listeners, which have
     * registered with us.
     */
    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
      fireUndoableEditHappened(e.getEdit());
    }

    @Override
    public void areaInvalidated(FigureEvent e) {
      fireAreaInvalidated(e.getInvalidatedArea());
    }

    @Override
    public void figureChanged(FigureEvent e) {
      quadTree.remove(e.getFigure());
      quadTree.add(e.getFigure(), e.getFigure().getDrawingArea());
      needsSorting = true;
      if (!isChanging()) {
        fireAreaInvalidated(e.getInvalidatedArea());
      }
    }

    @Override
    public void figureRequestRemove(FigureEvent e) {
      remove(e.getFigure());
    }
  }
}
