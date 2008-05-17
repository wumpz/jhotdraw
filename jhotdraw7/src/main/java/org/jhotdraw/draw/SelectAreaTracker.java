/*
 * @(#)SelectAreaTracker.java  4.0  2008-05-17
 *
 * Copyright (c) 1996-2008 by the original authors of JHotDraw
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

import java.awt.event.*;
import java.awt.*;
import java.util.*;

/**
 * <code>SelectAreaTracker</code> implements interactions with the background
 * area of a <code>Drawing</code>.
 * <p>
 * The <code>SelectAreaTracker</code> handles one of the three states of the 
 * <code>SelectionTool</code>. It comes into action, when the user presses
 * the mouse button over the background of a <code>Drawing</code>.
 *
 * @see SelectionTool
 *
 * @author Werner Randelshofer
 * @version 4.0 2008-05-17 Displays handles with index -1, if the mouse
 * hovers over a figure. 
 * <br>3.0 2006-02-15 Updated to handle multiple views.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class SelectAreaTracker extends AbstractTool {

    /**
     * The bounds of the rubberband. 
     */
    private Rectangle rubberband = new Rectangle();
    /**
     * Rubberband color. When this is null, the tracker does not
     * draw the rubberband.
     */
    private Color rubberbandColor = Color.BLACK;
    /**
     * Rubberband stroke.
     */
    private Stroke rubberbandStroke = new BasicStroke();
    /**
     * The hover handles, are the handles of the figure over which the
     * mouse pointer is currently hovering.
     */
    private LinkedList<Handle> hoverHandles = new LinkedList<Handle>();
    /**
     * The hover Figure is the figure, over which the mouse is currently
     * hovering.
     */
    private Figure hoverFigure = null;

    /** Creates a new instance. */
    public SelectAreaTracker() {
    }

    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        clearRubberBand();
    }

    public void mouseReleased(MouseEvent evt) {
        selectGroup(evt.isShiftDown());
        clearRubberBand();

    }

    public void mouseDragged(MouseEvent evt) {
        Rectangle invalidatedArea = (Rectangle) rubberband.clone();
        rubberband.setBounds(
                Math.min(anchor.x, evt.getX()),
                Math.min(anchor.y, evt.getY()),
                Math.abs(anchor.x - evt.getX()),
                Math.abs(anchor.y - evt.getY()));
        if (invalidatedArea.isEmpty()) {
            invalidatedArea = (Rectangle) rubberband.clone();
        } else {
            invalidatedArea = invalidatedArea.union(rubberband);
        }
        fireAreaInvalidated(invalidatedArea);
    }

    public void mouseMoved(MouseEvent evt) {
        clearRubberBand();
        DrawingView view = editor.findView((Container) evt.getSource());
        updateCursor(view, new Point(evt.getX(), evt.getY()));
        if (view == null || editor.getActiveView() != view) {
            clearHoverHandles();
        } else {
            Figure f = view.findFigure(evt.getPoint());
            updateHoverHandles(view, f);
        }
    }

    @Override
    public void mouseExited(MouseEvent evt) {
        DrawingView view = editor.findView((Container) evt.getSource());
        updateHoverHandles(view, null);
    }

    private void clearRubberBand() {
        if (!rubberband.isEmpty()) {
            fireAreaInvalidated(rubberband);
            rubberband.width = -1;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setStroke(rubberbandStroke);
        g.setColor(rubberbandColor);
        g.drawRect(rubberband.x, rubberband.y, rubberband.width - 1, rubberband.height - 1);
        if (hoverHandles.size() > 0 && !getView().isFigureSelected(hoverFigure)) {
            for (Handle h : hoverHandles) {
                h.draw(g);
            }
        }
    }

    private void selectGroup(boolean toggle) {
        getView().addToSelection(getView().findFiguresWithin(rubberband));
    }

    protected void clearHoverHandles() {
        hoverFigure = null;
        for (Handle h : hoverHandles) {
            h.setView(null);
            h.dispose();
        }
        hoverHandles.clear();
    }

    protected void updateHoverHandles(DrawingView view, Figure f) {
        if (f != hoverFigure) {
            Rectangle r = null;
            if (hoverFigure != null) {
                fireAreaInvalidated(view.drawingToView(hoverFigure.getDrawingArea()));
                for (Handle h : hoverHandles) {
                    if (r == null) {
                        r = h.getDrawingArea();
                    } else {
                        r.add(h.getDrawingArea());
                    }
                    h.setView(null);
                    h.dispose();
                }
                hoverHandles.clear();
            }
            hoverFigure = f;
            if (hoverFigure != null) {
                hoverHandles.addAll(hoverFigure.createHandles(-1));
                for (Handle h : hoverHandles) {
                    h.setView(view);
                    if (r == null) {
                        r = h.getDrawingArea();
                    } else {
                        r.add(h.getDrawingArea());
                    }
                }
            }
            if (r != null) {
                r.grow(1, 1);
                fireAreaInvalidated(r);
            }
        }
    }

    @Override
    public void deactivate(DrawingEditor editor) {
        super.deactivate(editor);
        clearHoverHandles();
    }
}
