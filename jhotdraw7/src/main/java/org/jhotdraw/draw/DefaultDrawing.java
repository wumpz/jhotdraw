/*
 * @(#)DefaultDrawing.java  2.2.2  2009-04-04
 *
 * Copyright (c) 1996-2009 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.draw;

import org.jhotdraw.geom.Dimension2DDouble;
import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.util.*;
import java.util.*;
import org.jhotdraw.geom.Geom;

/**
 * DefaultDrawing to be used for drawings that contain only a few children.
 * For larger drawings, {@link QuadTreeDrawing} should be used.
 * <p>
 * FIXME - Maybe we should rename this class to SimpleDrawing or we should
 * get rid of this class altogether.
 *
 *
 * @author Werner Randelshofer
 * @version 2.2.2 2009-04-04 Adding figures did not invalidate sort order.
 * <br>2.2.1 Fixed NullPointerException when setting canvas size.
 * <br>2.2 2007-04-09 Methods setCanvasSize, getCanvasSize added.
 * <br>2.1 2007-02-09 Moved FigureListener and UndoableEditListener into
 * inner class.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class DefaultDrawing
        extends AbstractDrawing {

    private boolean needsSorting = false;
    private Dimension2DDouble canvasSize;

    /** Creates a new instance. */
    public DefaultDrawing() {
    }

    @Override
    public void basicAdd(int index, Figure figure) {
        super.basicAdd(index, figure);
        invalidateSortOrder();
    }

    @Override
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

    public java.util.List<Figure> sort(Collection<? extends Figure> c) {
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

    public Figure findFigureExcept(Point2D.Double p, Collection<? extends Figure> ignore) {
        for (Figure f : getFiguresFrontToBack()) {
            if (!ignore.contains(f) && f.isVisible() && f.contains(p)) {
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
            Rectangle2D.Double r = f.getBounds();
            if (AttributeKeys.TRANSFORM.get(f) != null) {
                Rectangle2D rt = AttributeKeys.TRANSFORM.get(f).createTransformedShape(r).getBounds2D();
                r = (rt instanceof Rectangle2D.Double) ? (Rectangle2D.Double) rt : new Rectangle2D.Double(rt.getX(), rt.getY(), rt.getWidth(), rt.getHeight());
            }
            if (f.isVisible() && Geom.contains(bounds, r)) {
                contained.add(f);
            }
        }
        return contained;
    }

    @Override
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

    @Override
    protected void setAttributeOnChildren(AttributeKey key, Object newValue) {
        // empty
    }

    public void setCanvasSize(Dimension2DDouble newValue) {
        Dimension2DDouble oldValue = canvasSize;
        canvasSize = (newValue == null) ? null : (Dimension2DDouble) newValue.clone();
        firePropertyChange(CANVAS_SIZE_PROPERTY, oldValue, newValue);
    }

    public Dimension2DDouble getCanvasSize() {
        return (canvasSize == null) ? null : (Dimension2DDouble) canvasSize.clone();
    }

    @Override
    public int indexOf(Figure figure) {
        return children.indexOf(figure);
    }

    @Override
    public DefaultDrawing clone() {
        DefaultDrawing that = (DefaultDrawing) super.clone();
        that.canvasSize = (this.canvasSize == null) ? null : (Dimension2DDouble) this.canvasSize.clone();
        return that;
    }

    @Override
    protected void drawFill(Graphics2D g) {
        //  throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void drawStroke(Graphics2D g) {
        //  throw new UnsupportedOperationException("Not supported yet.");
    }
}
