/*
 * @(#)DefaultDrawing.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.draw;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jhotdraw.geom.Geom;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.util.ReversedList;

/**
 * A default implementation of {@link Drawing} useful for drawings which contain
 * only a few figures. <p> For larger drawings, {@link QuadTreeDrawing} is
 * recommended. <p> FIXME - Maybe we should rename this class to SimpleDrawing
 * or we should get rid of this class altogether.
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DefaultDrawing
        extends AbstractDrawing {

    private static final long serialVersionUID = 1L;
    
    private boolean needsSorting = false;

    /**
     * Creates a new instance.
     */
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
            List<Figure> toDraw = new ArrayList<>(getChildren().size());
            Rectangle clipRect = g.getClipBounds();
            double scale = AttributeKeys.getScaleFactorFromGraphics(g);
            for (Figure f : getChildren()) {
                if (f.getDrawingArea(scale).intersects(clipRect)) {
                    toDraw.add(f);
                }
            }
            draw(g, toDraw);
        }
    }

    public void draw(Graphics2D g, Collection<Figure> children) {
        Rectangle2D clipBounds = g.getClipBounds();
        double scale = AttributeKeys.getScaleFactorFromGraphics(g);
        if (clipBounds != null) {
            for (Figure f : children) {
                if (f.isVisible() && f.getDrawingArea(scale).intersects(clipBounds)) {
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

    @Override
    public List<Figure> sort(Collection<? extends Figure> c) {
        Set<Figure> unsorted = new HashSet<>();
        unsorted.addAll(c);
        List<Figure> sorted = new ArrayList<>(c.size());
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

    @Override
    public Figure findFigure(Point2D.Double p) {
        for (Figure f : getFiguresFrontToBack()) {
            if (f.isVisible() && f.contains(p)) {
                return f;
            }
        }
        return null;
    }

    @Override
    public Figure findFigureExcept(Point2D.Double p, Figure ignore) {
        for (Figure f : getFiguresFrontToBack()) {
            if (f != ignore && f.isVisible() && f.contains(p)) {
                return f;
            }
        }
        return null;
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
    public Figure findFigureExcept(Point2D.Double p, Collection<? extends Figure> ignore) {
        for (Figure f : getFiguresFrontToBack()) {
            if (!ignore.contains(f) && f.isVisible() && f.contains(p)) {
                return f;
            }
        }
        return null;
    }

    @Override
    public List<Figure> findFigures(Rectangle2D.Double bounds) {
        List<Figure> intersection = new LinkedList<>();
        for (Figure f : getChildren()) {
            if (f.isVisible() && f.getBounds().intersects(bounds)) {
                intersection.add(f);
            }
        }
        return intersection;
    }

    @Override
    public List<Figure> findFiguresWithin(Rectangle2D.Double bounds) {
        List<Figure> contained = new LinkedList<>();
        for (Figure f : getChildren()) {
            Rectangle2D.Double r = f.getBounds();
            if (f.get(TRANSFORM) != null) {
                Rectangle2D rt = f.get(TRANSFORM).createTransformedShape(r).getBounds2D();
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
     * Returns an iterator to iterate in Z-order front to back over the
     * children.
     */
    @Override
    public List<Figure> getFiguresFrontToBack() {
        ensureSorted();
        return new ReversedList<>(getChildren());
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
    protected <T> void setAttributeOnChildren(AttributeKey<T> key, T newValue) {
        // empty
    }

    @Override
    public int indexOf(Figure figure) {
        return children.indexOf(figure);
    }

    @Override
    protected void drawFill(Graphics2D g) {
    }

    @Override
    protected void drawStroke(Graphics2D g) {      
    }

    @Override
    public void drawCanvas(Graphics2D g) {
        if (get(CANVAS_WIDTH) != null && get(CANVAS_HEIGHT) != null) {
            // Determine canvas color and opacity
            Color canvasColor = get(CANVAS_FILL_COLOR);
            Double fillOpacity = get(CANVAS_FILL_OPACITY);
            if (canvasColor != null && fillOpacity > 0) {
                canvasColor = new Color(
                        (canvasColor.getRGB() & 0xffffff)
                        | ((int) (fillOpacity * 255) << 24), true);

                // Fill the canvas
                Rectangle2D.Double r = new Rectangle2D.Double(
                        0, 0, get(CANVAS_WIDTH), get(CANVAS_HEIGHT));

                g.setColor(canvasColor);
                g.fill(r);
            }
        }
    }
}
