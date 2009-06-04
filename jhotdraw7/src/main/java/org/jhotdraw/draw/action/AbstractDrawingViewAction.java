/*
 * @(#)AbstractDrawingViewAction.java  3.0  2009-06-02
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
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import java.beans.*;
import javax.swing.*;
import javax.swing.undo.*;
import org.jhotdraw.beans.Disposable;
import org.jhotdraw.beans.WeakPropertyChangeListener;

/**
 * Abstract super class for actions which act on a {@link DrawingView}.
 * {@code AbstractDrawingEditorAction} can either act on a specific
 * {@code DrawingView} or on the currently active {@link DrawingView} of
 * a {@link DrawingEditor}.
 * <b>
 * Altough {@code AbstractDrawingViewAction} has its own enabled state, it
 * is automatically disabled, if the associated {@code DrawingView} is disabled.
 * <b>
 * If the {@code AbstractDrawingEditorAction} acts on the currently active
 * {@code DrawingView} it listens for property changes in the
 * {@code DrawingEditor}. It listens using a {@link WeakPropertyChangeListener}
 * on the {@code DrawingEditor} and thus may become garbage collected if it is
 * not referenced by any other object.
 *
 *
 * @author Werner Randelshofer
 * @version 3.0 2009-06-02 Register with DrawingEditor using
 * WeakPropertyChangeListener.
 * <br>2.0.1 2009-04-04 PropertyChangeEvent was checked against the wrong
 * property name and the view listener was attached to the old value.
 * <br>2.0 2009-02-15 Renamed from AbstractViewAction to
 * AbstractDrawingViewAction.
 * <br>1.2 2006-04-21 Method setEditor added.
 * <br>1.1 2006-03-15 Support for enabled state of view added.
 * <br>1.0 2003-12-01 Created.
 */
public abstract class AbstractDrawingViewAction extends AbstractAction implements Disposable {

    private DrawingEditor editor;
    private DrawingView specificView;
    private DrawingView activeView;
    private PropertyChangeListener propertyChangeHandler = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("enabled")) {
                updateEnabledState();
            } else if (evt.getPropertyName() == DrawingEditor.ACTIVE_VIEW_PROPERTY) {
                if (evt.getOldValue() != null) {
                    activeView = ((DrawingView) evt.getOldValue());
                    activeView.removePropertyChangeListener(propertyChangeHandler);
                }
                if (evt.getNewValue() != null) {
                    activeView = ((DrawingView) evt.getNewValue());
                    activeView.addPropertyChangeListener(propertyChangeHandler);
                    updateEnabledState();
                }
            }
        }
    };

    /**
     * Creates a view action which acts on the current view of the editor.
     */
    public AbstractDrawingViewAction(DrawingEditor editor) {
        setEditor(editor);
    }

    /**
     * Creates a view action which acts on the specified view.
     */
    public AbstractDrawingViewAction(DrawingView view) {
        this.specificView = view;
        specificView.addPropertyChangeListener(propertyChangeHandler);
    }

    protected void setEditor(DrawingEditor newValue) {
        if (editor != null) {
            editor.removePropertyChangeListener(propertyChangeHandler);
        }
        if (activeView != null) {
            activeView.removePropertyChangeListener(propertyChangeHandler);
        }
        editor = newValue;
        if (editor != null) {
            editor.addPropertyChangeListener(new WeakPropertyChangeListener(propertyChangeHandler));
            activeView = editor.getActiveView();
            if (activeView != null) {
                activeView.addPropertyChangeListener(propertyChangeHandler);
            }
        }
    }

    protected DrawingEditor getEditor() {
        return editor;
    }

    protected DrawingView getView() {
        return (specificView != null) ? specificView : activeView;
    }

    protected Drawing getDrawing() {
        return getView().getDrawing();
    }

    protected void fireUndoableEditHappened(UndoableEdit edit) {
        getDrawing().fireUndoableEditHappened(edit);
    }

    protected void viewChanged() {
    }

    public void updateEnabledState() {
        if (getView() != null) {
            setEnabled(getView().isEnabled());
        } else {
            setEnabled(false);
        }
    }

    public void dispose() {
        setEditor(null);
    }
}
