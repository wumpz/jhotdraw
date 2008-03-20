/*
 * @(#)SVGBezierFigure.java  1.0.1  2008-03-20
 *
 * Copyright (c) 2007-2008 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.samples.svg.figures;

import java.awt.BasicStroke;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.geom.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

/**
 * SVGBezierFigure is not an actual SVG element, it is used by SVGPathFigure to
 * represent a single BezierPath segment within an SVG path.
 *
 * @author Werner Randelshofer
 * @version 1.0.1 2008-03-20 Fixed computation of clip bounds. 
 * <br>1.0 April 14, 2007 Created.
 */
public class SVGBezierFigure extends BezierFigure {
    private Rectangle2D.Double cachedDrawingArea;
    
    /** Creates a new instance. */
    public SVGBezierFigure() {
        this(false);
    }
    public SVGBezierFigure(boolean isClosed) {
        super(isClosed);
        FILL_OPEN_PATH.basicSet(this, true);
    }
    
    public Collection<Handle> createHandles(SVGPathFigure pathFigure, int detailLevel) {
        LinkedList<Handle> handles = new LinkedList<Handle>();
        switch (detailLevel % 2) {
            case 0 :
                for (int i=0, n = path.size(); i < n; i++) {
                    handles.add(new BezierNodeHandle(this, i, pathFigure));
                }
                break;
            case 1 :
                TransformHandleKit.addTransformHandles(this, handles);
                break;
            default:
                break;
        }
        return handles;
    }
    @Override public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
        if (evt.getClickCount() == 2/* && view.getHandleDetailLevel() == 0*/) {
            willChange();
            final int index = splitSegment(p, (float) (5f / view.getScaleFactor()));
            if (index != -1) {
                final BezierPath.Node newNode = getNode(index);
                fireUndoableEditHappened(new AbstractUndoableEdit() {
                    public void redo() throws CannotRedoException {
                        super.redo();
                        willChange();
                        addNode(index, newNode);
                        changed();
                    }
                    
                    public void undo() throws CannotUndoException {
                        super.undo();
                        willChange();
                        removeNode(index);
                        changed();
                    }
                    
                });
                changed();
                evt.consume();
                return true;
            }
        }
        return false;
    }
    public void transform(AffineTransform tx) {
        if (TRANSFORM.get(this) != null ||
                (tx.getType() & (AffineTransform.TYPE_TRANSLATION)) != tx.getType()) {
            if (TRANSFORM.get(this) == null) {
                TRANSFORM.basicSetClone(this, tx);
            } else {
                AffineTransform t = TRANSFORM.getClone(this);
                t.preConcatenate(tx);
                TRANSFORM.basicSet(this, t);
            }
        } else {
            super.transform(tx);
        }
    }
    
    public Rectangle2D.Double getDrawingArea() {
        if (cachedDrawingArea == null) {
            if (TRANSFORM.get(this) == null) {
                cachedDrawingArea = path.getBounds2D();
            } else {
                BezierPath p2 = (BezierPath) path.clone();
                p2.transform(TRANSFORM.get(this));
                cachedDrawingArea = p2.getBounds2D();
            }
            double strokeTotalWidth = AttributeKeys.getStrokeTotalWidth(this);
            double width = strokeTotalWidth / 2d;
            if (STROKE_JOIN.get(this) == BasicStroke.JOIN_MITER) {
                width *= STROKE_MITER_LIMIT.get(this);
            } else if (STROKE_CAP.get(this) != BasicStroke.CAP_BUTT) {
                width += strokeTotalWidth * 2;
            }
            Geom.grow(cachedDrawingArea, width, width);
        }
        return (Rectangle2D.Double) cachedDrawingArea.clone();
    }
    
    /**
     * Transforms all coords of the figure by the current TRANSFORM attribute
     * and then sets the TRANSFORM attribute to null.
     */
    public void flattenTransform() {
        if (TRANSFORM.get(this) != null) {
            path.transform(TRANSFORM.get(this));
            TRANSFORM.basicSet(this, null);
        }
        invalidate();
    }
    
    public void invalidate() {
        super.invalidate();
        cachedDrawingArea = null;
    }
}
