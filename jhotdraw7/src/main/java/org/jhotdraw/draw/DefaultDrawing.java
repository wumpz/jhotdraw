/*
 * @(#)DefaultDrawing.java  2.0  2006-01-14
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
�
 */

package org.jhotdraw.draw;

import org.jhotdraw.util.ReversedList;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import org.jhotdraw.util.*;
import java.util.*;
/**
 * DefaultDrawing.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class DefaultDrawing
extends AbstractDrawing
implements FigureListener, UndoableEditListener {
    private ArrayList<Figure> figures = new ArrayList<Figure>();
    private boolean needsSorting = false;
    
    /** Creates a new instance. */
    public DefaultDrawing() {
    }
    
    protected int indexOf(Figure figure) {
        return figures.indexOf(figure);
    }
    public void basicAdd(int index, Figure figure) {
        figures.add(index, figure);
        figure.addFigureListener(this);
        figure.addUndoableEditListener(this);
        invalidateSortOrder();
    }
    public void basicRemove(Figure figure) {
        figures.remove(figure);
        figure.removeFigureListener(this);
        figure.removeUndoableEditListener(this);
        invalidateSortOrder();
    }
    
    
    public void draw(Graphics2D g) {
        synchronized (getLock()) {
        ensureSorted();
        ArrayList<Figure> toDraw = new ArrayList<Figure>(figures.size());
        Rectangle clipRect = g.getClipBounds();
        for (Figure f : figures) {
            if (f.getDrawBounds().intersects(clipRect)) {
                toDraw.add(f);
            }
        }
        draw(g, toDraw);
        }
    }
    
    public void draw(Graphics2D g, Collection<Figure> figures) {
        for (Figure f : figures) {
            if (f.isVisible()) {
                f.draw(g);
            }
        }
    }
    
    public Collection<Figure> sort(Collection<Figure> c) {
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
    
    public void figureAreaInvalidated(FigureEvent e) {
        fireAreaInvalidated(e.getInvalidatedArea());
    }
    public void figureChanged(FigureEvent e) {
        invalidateSortOrder();
        fireAreaInvalidated(e.getInvalidatedArea());
    }
    
    public void figureAdded(FigureEvent e) {
    }
    public void figureRemoved(FigureEvent e) {
    }
    public void figureRequestRemove(FigureEvent e) {
        remove(e.getFigure());
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
    public Figure findFigureExcept(Point2D.Double p, Collection<Figure> ignore) {
        for (Figure f : getFiguresFrontToBack()) {
            if (! ignore.contains(f) && f.isVisible() && f.contains(p)) {
                return f;
            }
        }
        return null;
    }
    public Collection<Figure> findFigures(Rectangle2D.Double bounds) {
        ArrayList<Figure> intersection = new ArrayList<Figure>();
        for (Figure f : figures) {
            if (f.isVisible() && f.getBounds().intersects(bounds)) {
                intersection.add(f);
            }
        }
        return intersection;
    }
    public Collection<Figure> findFiguresWithin(Rectangle2D.Double bounds) {
        ArrayList<Figure> contained = new ArrayList<Figure>();
        for (Figure f : figures) {
            if (f.isVisible() && bounds.contains(f.getBounds())) {
                contained.add(f);
            }
        }
        return contained;
    }
    
    public Collection<Figure> getFigures() {
        return Collections.unmodifiableCollection(figures);
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
            fireAreaInvalidated(figure.getDrawBounds());
        }
    }
    public void sendToBack(Figure figure) {
        if (figures.remove(figure)) {
            figures.add(0, figure);
            invalidateSortOrder();
            fireAreaInvalidated(figure.getDrawBounds());
        }
    }
    
    /**
     * We propagate all edit events from our figures to 
     * undoable edit listeners, which have registered with us.
     */
    public void undoableEditHappened(UndoableEditEvent e) {
        fireUndoableEditHappened(e.getEdit());
    }
    
    public void figureAttributeChanged(FigureEvent e) {
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
}
