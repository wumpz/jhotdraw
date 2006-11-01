/*
 * @(#)AbstractTool.java  3.0  2006-02-15
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

import org.jhotdraw.undo.CompositeEdit;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;

/**
 * AbstractTool.
 *
 * @author Werner Randelshofer
 * @version 3.0 2006-02-15 Updated to handle multiple views.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public abstract class AbstractTool implements Tool {
    /**
     * This is set to true, if this is the active tool of the editor.
     */
    private boolean isActive;
    
    /**
     * This is set to true, while the tool is doing some work.
     * This prevents the currentView from being changed when a mouseEnter
     * event is received.
     */
    protected boolean isWorking;
    
    protected DrawingEditor editor;
    protected Point anchor = new Point();
    protected EventListenerList listenerList = new EventListenerList();
    
    
    /** Creates a new instance. */
    public AbstractTool() {
    }
    
    public void addUndoableEditListener(UndoableEditListener l) {
        listenerList.add(UndoableEditListener.class, l);
    }
    
    public void removeUndoableEditListener(UndoableEditListener l) {
        listenerList.remove(UndoableEditListener.class, l);
    }
    
    public void activate(DrawingEditor editor) {
        this.editor = editor;
        isActive = true;
        //editor.getView().requestFocus();
    }
    
    public void deactivate(DrawingEditor editor) {
        this.editor = editor;
        isActive = false;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    protected DrawingView getView() {
        return editor.getView();
    }
    protected DrawingEditor getEditor() {
        return editor;
    }
    protected Drawing getDrawing() {
        return getView().getDrawing();
    }
    protected Point2D.Double viewToDrawing(Point p) {
        return constrainPoint(getView().viewToDrawing(p));
    }
    protected Point2D.Double constrainPoint(Point p) {
        return constrainPoint(getView().viewToDrawing(p));
    }
    protected Point2D.Double constrainPoint(Point2D.Double p) {
        return getView().getConstrainer().constrainPoint(p);
    }
    
    /**
     * Deletes the selection.
     * Depending on the tool, this could be selected figures, selected points
     * or selected text.
     */
    public void editDelete() {
        getView().getDrawing().removeAll(getView().getSelectedFigures());
    }
    /**
     * Cuts the selection into the clipboard.
     * Depending on the tool, this could be selected figures, selected points
     * or selected text.
     */
    public void editCut() {
    }
    /**
     * Copies the selection into the clipboard.
     * Depending on the tool, this could be selected figures, selected points
     * or selected text.
     */
    public void editCopy() {
    }
    /**
     * Duplicates the selection.
     * Depending on the tool, this could be selected figures, selected points
     * or selected text.
     */
    public void editDuplicate() {
    }
    /**
     * Pastes the contents of the clipboard.
     * Depending on the tool, this could be selected figures, selected points
     * or selected text.
     */
    public void editPaste() {
    }
    
    public void keyReleased(KeyEvent evt) {
        fireToolDone();
    }
    
    
    public void keyTyped(KeyEvent evt) {
    }
    
    public void keyPressed(KeyEvent evt) {
        if (evt.getSource() instanceof Container) {
        editor.setView(editor.findView((Container) evt.getSource()));
        }
        //fireToolStarted(evt.get)
        CompositeEdit edit;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_DELETE :
            case KeyEvent.VK_BACK_SPACE : {
                editDelete();
                break;
            }
            case KeyEvent.VK_A : {
                if ((evt.getModifiers() &
                        (KeyEvent.CTRL_MASK | KeyEvent.META_MASK)) != 0) {
                    getView().addToSelection(getView().getDrawing().getFigures());
                }
                break;
            }
            case KeyEvent.VK_LEFT : {
                Collection<Figure> figures = getView().getSelectedFigures();
                AffineTransform tx = new AffineTransform();
                tx.translate(-1,0);
                for (Figure f : figures) {
                    f.willChange();
                    f.basicTransform(tx);
                    f.changed();
                }
                getDrawing().fireUndoableEditHappened(new TransformEdit(figures, tx));
                break;
            }
            case KeyEvent.VK_RIGHT : {
                Collection<Figure> figures = getView().getSelectedFigures();
                AffineTransform tx = new AffineTransform();
                tx.translate(1,0);
                for (Figure f : figures) {
                    f.willChange();
                    f.basicTransform(tx);
                    f.changed();
                }
                getDrawing().fireUndoableEditHappened(new TransformEdit(figures, tx));
                break;
            }
            case KeyEvent.VK_UP : {
                Collection<Figure> figures = getView().getSelectedFigures();
                AffineTransform tx = new AffineTransform();
                tx.translate(0,-1);
                for (Figure f : figures) {
                    f.willChange();
                    f.basicTransform(tx);
                    f.changed();
                }
                getDrawing().fireUndoableEditHappened(new TransformEdit(figures, tx));
                break;
            }
            case KeyEvent.VK_DOWN : {
                Collection<Figure> figures = getView().getSelectedFigures();
                getDrawing().fireUndoableEditHappened(edit = new CompositeEdit("Figur(en) verschieben"));
                AffineTransform tx = new AffineTransform();
                tx.translate(0,1);
                for (Figure f : figures) {
                    f.willChange();
                    f.basicTransform(tx);
                    f.changed();
                }
                getDrawing().fireUndoableEditHappened(new TransformEdit(figures, tx));
                break;
            }
        }
    }
    
    public void mouseClicked(MouseEvent evt) {
    }
    
    
    public void mouseEntered(MouseEvent evt) {
        if (! isWorking) {
            editor.setView(editor.findView((Container) evt.getSource()));
        }
    }
    
    public void mouseExited(MouseEvent evt) {
    }
    
    public void mouseMoved(MouseEvent evt) {
    }
    
    public void mousePressed(MouseEvent evt) {
        DrawingView view = editor.findView((Container) evt.getSource());
        view.requestFocus();
        anchor = new Point(evt.getX(), evt.getY());
        isWorking = true;
        fireToolStarted(view);
    }
    
    public void mouseReleased(MouseEvent evt) {
        isWorking = false;
    }
    
    public void addToolListener(ToolListener l) {
        listenerList.add(ToolListener.class, l);
    }
    
    public void removeToolListener(ToolListener l) {
        listenerList.remove(ToolListener.class, l);
    }
    
    /**
     *  Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    protected void fireToolStarted(DrawingView view) {
        ToolEvent event = null;
        // Notify all listeners that have registered interest for
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == ToolListener.class) {
                // Lazily create the event:
                if (event == null)
                    event = new ToolEvent(this, view, new Rectangle(0,0,-1,-1));
                ((ToolListener)listeners[i+1]).toolStarted(event);
            }
        }
    }
    
    /**
     *  Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    protected void fireToolDone() {
        ToolEvent event = null;
        // Notify all listeners that have registered interest for
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == ToolListener.class) {
                // Lazily create the event:
                if (event == null)
                    event = new ToolEvent(this, getView(), new Rectangle(0,0,-1,-1));
                ((ToolListener)listeners[i+1]).toolDone(event);
            }
        }
    }
    
    /**
     * Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    protected void fireAreaInvalidated(Rectangle2D.Double r) {
        Point p1 = getView().drawingToView(new Point2D.Double(r.x, r.y));
        Point p2 = getView().drawingToView(new Point2D.Double(r.x+r.width, r.y+r.height));
        fireAreaInvalidated(
                new Rectangle(p1.x,p1.y,p2.x-p1.x,p2.y-p1.y)
                );
    }
    /**
     * Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    protected void fireAreaInvalidated(Rectangle invalidatedArea) {
        ToolEvent event = null;
        // Notify all listeners that have registered interest for
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == ToolListener.class) {
                // Lazily create the event:
                if (event == null)
                    event = new ToolEvent(this, getView(), invalidatedArea);
                ((ToolListener)listeners[i+1]).areaInvalidated(event);
            }
        }
    }
    public void draw(Graphics2D g) {
    }
    
    public void updateCursor(DrawingView view, Point p) {
        Handle handle = view.findHandle(p);
        if (handle != null) {
            view.setCursor(handle.getCursor());
        } else {
            Figure figure = view.findFigure(p);
            if (figure != null) {
                view.setCursor(figure.getCursor(view.viewToDrawing(p)));
            } else {
                view.setCursor(Cursor.getDefaultCursor());
            }
        }
    }
}
