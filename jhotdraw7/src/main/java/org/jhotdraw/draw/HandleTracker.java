/*
 * @(#)HandleTracker.java  2.0  2008-05-17
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

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * HandleTracker implements interactions with the handles of a Figure.
 * <p>
 * The <code>HandleTracker</code> handles one of the three states of the 
 * <code>SelectionTool</code>. Iz comes into action, when the user presses
 * the mouse button over a <code>Figure</code>.
 *
 * @see SelectionTool
 *
 * @author Werner Randelshofer
 * @version 2.0 2008-05-17 Added support for hover handles. 
 * <br>1.1 2008-05-15 Don't hide anchor field from super class. 
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class HandleTracker extends AbstractTool {

    private Handle masterHandle;
    private HandleMulticaster multicaster;
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
    public HandleTracker(Handle handle) {
        masterHandle = handle;
        multicaster = new HandleMulticaster(handle);
    }

    public HandleTracker(Handle master, Collection<Handle> handles) {
        masterHandle = master;
        multicaster = new HandleMulticaster(handles);
    }

    @Override
    public void draw(Graphics2D g) {
        if (hoverHandles.size() > 0 && !getView().isFigureSelected(hoverFigure)) {
            for (Handle h : hoverHandles) {
                h.draw(g);
            }
        }
    }

    /* FIXME - The handle should draw itself in selected mode
    public void draw(Graphics2D g) {
    g.setColor(Color.RED);
    g.draw(
    masterHandle.getBounds()
    );
    }*/
    public void activate(DrawingEditor editor) {
        super.activate(editor);
        getView().setCursor(masterHandle.getCursor());
        getView().setActiveHandle(masterHandle);
        clearHoverHandles();
    }

    public void deactivate(DrawingEditor editor) {
        super.deactivate(editor);
        getView().setCursor(Cursor.getDefaultCursor());
        getView().setActiveHandle(null);
        clearHoverHandles();
    }

    public void keyPressed(KeyEvent evt) {
        multicaster.keyPressed(evt);
        if (!evt.isConsumed()) {
            super.keyPressed(evt);
        }
    }

    public void keyReleased(KeyEvent evt) {
        multicaster.keyReleased(evt);
    }

    public void keyTyped(KeyEvent evt) {
        multicaster.keyTyped(evt);
    }

    public void mouseClicked(MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            multicaster.trackDoubleClick(new Point(evt.getX(), evt.getY()),
                    evt.getModifiersEx(), getView());
        }
        evt.consume();
    }

    public void mouseDragged(MouseEvent evt) {
        multicaster.trackStep(anchor, new Point(evt.getX(), evt.getY()),
                evt.getModifiersEx(), getView());
        clearHoverHandles();
    }

    public void mouseEntered(MouseEvent evt) {
    }

    public void mouseExited(MouseEvent evt) {
        DrawingView view = editor.findView((Container) evt.getSource());
        updateHoverHandles(view, null);
    }

    @Override
    public void mouseMoved(MouseEvent evt) {
        Point point = evt.getPoint();
        updateCursor(editor.findView((Container) evt.getSource()), point);
        DrawingView view = editor.findView((Container) evt.getSource());
        updateCursor(view, point);
        if (view == null || editor.getActiveView() != view) {
            clearHoverHandles();
        } else {
            // Search first, if one of the selected figures contains
            // the current mouse location. Only then search for other
            // figures. This search sequence is consistent with the
            // search sequence of the SelectionTool.
            Figure hf = null;
            Point2D.Double p = view.viewToDrawing(point);
            for (Figure f : view.getSelectedFigures()) {
                if (f.contains(p)) {
                    hf = f;
                }
            }
            if (hf == null) {
                hf = view.findFigure(point);
            }

            updateHoverHandles(view, hf);
        }
    }

    public void mousePressed(MouseEvent evt) {
        //handle.mousePressed(evt);
        anchor = new Point(evt.getX(), evt.getY());
        multicaster.trackStart(anchor, evt.getModifiersEx(), getView());
        clearHoverHandles();
    }

    public void mouseReleased(MouseEvent evt) {
        multicaster.trackEnd(anchor, new Point(evt.getX(), evt.getY()),
                evt.getModifiersEx(), getView());
    }

    protected void clearHoverHandles() {
        updateHoverHandles(null, null);
    }

    protected void updateHoverHandles(DrawingView view, Figure f) {
        if (f != hoverFigure) {
            Rectangle r = null;
            if (hoverFigure != null) {
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
}
