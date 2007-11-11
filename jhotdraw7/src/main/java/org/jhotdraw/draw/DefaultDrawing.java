/*
 * @(#)DefaultDrawing.java  2.2  2007-04-09
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.draw;

import java.awt.event.MouseEvent;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.util.ReversedList;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import org.jhotdraw.util.*;
import java.util.*;
/**
 * DefaultDrawing to be used for drawings that contain only a few children.
 * For larger drawings, {@link QuadTreeDrawing} should be used.
 * <p>
 * FIXME - Maybe we should rename this class to SimpleDrawing or we should
 * get rid of this class altogether.
 *
 *
 * @author Werner Randelshofer
 * @version 2.2 2007-04-09 Methods setCanvasSize, getCanvasSize added.
 * <br>2.1 2007-02-09 Moved FigureListener and UndoableEditListener into
 * inner class.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class DefaultDrawing
        extends AbstractDrawing {
    private boolean needsSorting = false;
    private Rectangle2D.Double canvasBounds;
    private Rectangle2D.Double cachedBounds;
    private Rectangle2D.Double cachedDrawingArea;
    
    /** Creates a new instance. */
    public DefaultDrawing() {
    }
    
    public void draw(Graphics2D g) {
        synchronized (getLock()) {
            ensureSorted();
            ArrayList<Figure> toDraw = new ArrayList<Figure>(getChildren().size());
            Rectangle clipRect = g.getClipBounds();
            for (Figure f : getChildren()) {
                if (f.getDrawingArea().intersects(clipRect)) {
                    toDraw.add(f);
                }
            }
            draw(g, toDraw);
        }
    }
    
    public void draw(Graphics2D g, Collection<Figure> children) {
        Rectangle2D clipBounds = g.getClipBounds();
        if (clipBounds != null) {
            for (Figure f : children) {
                if (f.isVisible() && f.getDrawingArea().intersects(clipBounds)) {
                    f.draw(g);
                }
            }
        } else {
            for (Figure f : children) {
                if (f.isVisible()) {
                    f.draw(g);
                }
            }
        }
    }
    
    public java.util.List<Figure> sort(Collection<Figure> c) {
        HashSet<Figure> unsorted = new HashSet<Figure>();
        unsorted.addAll(c);
        ArrayList<Figure> sorted = new ArrayList<Figure>(c.size());
        for (Figure f : getChildren()) {
            if (unsorted.contains(f)) {
                sorted.add(f);
                unsorted.remove(f);
            }
        }
        for (Figure f : c) {
            if (unsorted.contains(f)) {
                sorted.add(f);
                unsorted.remove(f);
            }
        }
        return sorted;
    }
    
    
    public Figure findFigure(Point2D.Double p) {
        for (Figure f : getFiguresFrontToBack()) {
            if (f.isVisible() && f.contains(p)) {
                return f;
            }
        }
        return null;
    }
    public Figure findFigureExcept(Point2D.Double p, Figure ignore) {
        for (Figure f : getFiguresFrontToBack()) {
            if (f != ignore && f.isVisible() && f.contains(p)) {
                return f;
            }
        }
        return null;
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
    public Figure findFigureBehind(Point2D.Double p, Collection<Figure> children) {
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
    public Figure findFigureExcept(Point2D.Double p, Collection<Figure> ignore) {
        for (Figure f : getFiguresFrontToBack()) {
            if (! ignore.contains(f) && f.isVisible() && f.contains(p)) {
                return f;
            }
        }
        return null;
    }
    public java.util.List<Figure> findFigures(Rectangle2D.Double bounds) {
        LinkedList<Figure> intersection = new LinkedList<Figure>();
        for (Figure f : getChildren()) {
            if (f.isVisible() && f.getBounds().intersects(bounds)) {
                intersection.add(f);
            }
        }
        return intersection;
    }
    public java.util.List<Figure> findFiguresWithin(Rectangle2D.Double bounds) {
        LinkedList<Figure> contained = new LinkedList<Figure>();
        for (Figure f : getChildren()) {
            Rectangle2D r = f.getBounds();
            if (AttributeKeys.TRANSFORM.get(f) != null) {
                r = AttributeKeys.TRANSFORM.get(f).createTransformedShape(r).getBounds2D();
            }
            if (f.isVisible() && bounds.contains(r)) {
                contained.add(f);
            }
        }
        return contained;
    }
    
    public Figure findFigureInside(Point2D.Double p) {
        Figure f = findFigure(p);
        return (f == null) ? null : f.findFigureInside(p);
    }
    
    /**
     * Returns an iterator to iterate in
     * Z-order front to back over the children.
     */
    public java.util.List<Figure> getFiguresFrontToBack() {
        ensureSorted();
        return new ReversedList<Figure>(getChildren());
    }
    
    public void bringToFront(Figure figure) {
        if (basicRemove(figure) != -1) {
            basicAdd(figure);
            invalidateSortOrder();
            fireAreaInvalidated(figure.getDrawingArea());
        }
    }
    public void sendToBack(Figure figure) {
        if (basicRemove(figure) != -1) {
            basicAdd(0, figure);
            invalidateSortOrder();
            fireAreaInvalidated(figure.getDrawingArea());
        }
    }
    
    /**
     * Invalidates the sort order.
     */
    private void invalidateSortOrder() {
        needsSorting = true;
    }
    /**
     * Ensures that the children are sorted in z-order sequence from back to
     * front.
     */
    private void ensureSorted() {
        if (needsSorting) {
            Collections.sort(children, FigureLayerComparator.INSTANCE);
            needsSorting = false;
        }
    }
    
    public void setCanvasSize(Dimension2DDouble newValue) {
        Dimension2DDouble oldValue = new Dimension2DDouble(
                canvasBounds.width, canvasBounds.height);
        canvasBounds.width = newValue == null ? -1 : newValue.width;
        canvasBounds.height = newValue == null ? -1 : newValue.height;
        firePropertyChange("canvasSize", oldValue, newValue);
    }
    
    public Dimension2DDouble getCanvasSize() {
        return canvasBounds == null || canvasBounds.isEmpty() ? null : 
            new Dimension2DDouble(
                canvasBounds.width, canvasBounds.height
                );
    }
    
    protected void invalidateBounds() {
        cachedBounds = null;
        cachedDrawingArea = null;
    }
    
    public Rectangle2D.Double getBounds() {
        if (cachedBounds == null) {
            if (getCanvasSize() != null) {
                cachedBounds = new Rectangle2D.Double(0, 0, getCanvasSize().width, getCanvasSize().height);
            } else {
                if (children.size() == 0) {
                    cachedBounds = new Rectangle2D.Double();
                } else {
                    for (Figure f : children) {
                        if (cachedBounds == null) {
                            cachedBounds = f.getBounds();
                        } else {
                            cachedBounds.add(f.getBounds());
                        }
                    }
                }
            }
        }
        return (Rectangle2D.Double) cachedBounds.clone();
    }
    
    public Rectangle2D.Double getDrawingArea() {
        if (cachedDrawingArea == null) {
            if (children.size() == 0) {
                cachedDrawingArea = new Rectangle2D.Double();
            } else {
                for (Figure f : children) {
                    if (cachedDrawingArea == null) {
                        cachedDrawingArea = f.getDrawingArea();
                    } else {
                        cachedDrawingArea.add(f.getDrawingArea());
                    }
                }
            }
        }
        return (Rectangle2D.Double) cachedDrawingArea.clone();
    }
    
/*    public void basicAdd(int index, Figure figure) {
        children.add(index, figure);
    }
  */
    /*
    public Figure basicRemoveChild(int index) {
        return children.remove(index);
    }*/
    
    public int indexOf(Figure figure) {
        return children.indexOf(figure);
    }
    /*
    public int basicRemove(Figure figure) {
        int index = indexOf(figure);
        children.remove(figure);
        return index;
    }*/
    
    public DefaultDrawing clone() {
        DefaultDrawing that = (DefaultDrawing) super.clone();
        that.children = new ArrayList<Figure>();
        that.canvasBounds = (Rectangle2D.Double) this.canvasBounds.clone();
        for (Figure f : this.children) {
            that.children.add((Figure) f.clone());
        }
        return that;
    }
}
