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
 * DefaultDrawing to be used for drawings that contain only a few figures.
 * For larger drawings, {@see QuadTreeDrawing} should be used.
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
    private ArrayList<Figure> figures = new ArrayList<Figure>();
    private boolean needsSorting = false;
    private FigureHandler figureHandler;
    private Dimension2DDouble canvasSize;
    
    /** Creates a new instance. */
    public DefaultDrawing() {
        figureHandler = createFigureHandler();
    }
    
    protected FigureHandler createFigureHandler() {
        return new FigureHandler();
    }
    
    public int indexOf(Figure figure) {
        return figures.indexOf(figure);
    }
    public void basicAdd(int index, Figure figure) {
        figures.add(index, figure);
        figure.addFigureListener(figureHandler);
        invalidateSortOrder();
    }
    public void basicRemove(Figure figure) {
        figures.remove(figure);
        figure.removeFigureListener(figureHandler);
        invalidateSortOrder();
    }
    
    
    public void draw(Graphics2D g) {
        synchronized (getLock()) {
            ensureSorted();
            ArrayList<Figure> toDraw = new ArrayList<Figure>(figures.size());
            Rectangle clipRect = g.getClipBounds();
            for (Figure f : figures) {
                if (f.getDrawingArea().intersects(clipRect)) {
                    toDraw.add(f);
                }
            }
            draw(g, toDraw);
        }
    }
    
    public void draw(Graphics2D g, Collection<Figure> figures) {
        Rectangle2D clipBounds = g.getClipBounds();
        if (clipBounds != null) {
            for (Figure f : figures) {
                if (f.isVisible() && f.getDrawingArea().intersects(clipBounds)) {
                    f.draw(g);
                }
            }
        } else {
            for (Figure f : figures) {
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
        for (Figure f : figures) {
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
        for (Figure f : figures) {
            if (f.isVisible() && f.getBounds().intersects(bounds)) {
                intersection.add(f);
            }
        }
        return intersection;
    }
    public java.util.List<Figure> findFiguresWithin(Rectangle2D.Double bounds) {
        LinkedList<Figure> contained = new LinkedList<Figure>();
        for (Figure f : figures) {
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
    
    public java.util.List<Figure> getFigures() {
        return Collections.unmodifiableList(figures);
    }
    
    public Figure findFigureInside(Point2D.Double p) {
        Figure f = findFigure(p);
        return (f == null) ? null : f.findFigureInside(p);
    }
    
    /**
     * Returns an iterator to iterate in
     * Z-order front to back over the figures.
     */
    public java.util.List<Figure> getFiguresFrontToBack() {
        ensureSorted();
        return new ReversedList<Figure>(figures);
    }
    
    public void bringToFront(Figure figure) {
        if (figures.remove(figure)) {
            figures.add(figure);
            invalidateSortOrder();
            fireAreaInvalidated(figure.getDrawingArea());
        }
    }
    public void sendToBack(Figure figure) {
        if (figures.remove(figure)) {
            figures.add(0, figure);
            invalidateSortOrder();
            fireAreaInvalidated(figure.getDrawingArea());
        }
    }
    
    public boolean contains(Figure f) {
        return figures.contains(f);
    }
    /**
     * Invalidates the sort order.
     */
    private void invalidateSortOrder() {
        needsSorting = true;
    }
    /**
     * Ensures that the figures are sorted in z-order sequence from back to
     * front.
     */
    private void ensureSorted() {
        if (needsSorting) {
            Collections.sort(figures, FigureLayerComparator.INSTANCE);
            needsSorting = false;
        }
    }
    
    public void setCanvasSize(Dimension2DDouble newValue) {
        Dimension2DDouble oldValue = canvasSize;
        canvasSize = newValue;
        firePropertyChange("canvasSize", oldValue, newValue);
    }
    
    public Dimension2DDouble getCanvasSize() {
        return canvasSize;
    }

    /**
     * Handles all figure events fired by Figures contained in the Drawing.
     */
    protected class FigureHandler extends FigureAdapter implements UndoableEditListener {
        /**
         * We propagate all edit events from our figures to
         * undoable edit listeners, which have registered with us.
         */
        public void undoableEditHappened(UndoableEditEvent e) {
            fireUndoableEditHappened(e.getEdit());
        }
        
        @Override public void figureAreaInvalidated(FigureEvent e) {
            fireAreaInvalidated(e.getInvalidatedArea());
        }
        @Override public void figureChanged(FigureEvent e) {
            invalidateSortOrder();
            fireAreaInvalidated(e.getInvalidatedArea());
        }
        
        @Override public void figureRequestRemove(FigureEvent e) {
            remove(e.getFigure());
        }
    }
}
