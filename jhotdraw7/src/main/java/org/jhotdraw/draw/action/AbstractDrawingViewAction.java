/*
 * @(#)AbstractDrawingViewAction.java  2.0.1  2009-04-04
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

/**
 * Abstract super class for actions which act on a DrawingView.
 *
 * @author Werner Randelshofer
 * @version 2.0.1 2009-04-04 PropertyChangeEvent was checked against the wrong
 * property name and the view listener was attached to the old value.
 * <br>2.0 2009-02-15 Renamed from AbstractViewAction to
 * AbstractDrawingViewAction.
 * <br>1.2 2006-04-21 Method setEditor added.
 * <br>1.1 2006-03-15 Support for enabled state of view added.
 * <br>1.0 2003-12-01 Created.
 */
public abstract class AbstractDrawingViewAction extends AbstractAction {

    private DrawingEditor editor;
    private DrawingView view;
    private PropertyChangeListener propertyChangeHandler = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("enabled")) {
                updateEnabledState();
            } else if (evt.getPropertyName() == DrawingEditor.ACTIVE_VIEW_PROPERTY) {
                if (evt.getOldValue() != null) {
                    DrawingView view = ((DrawingView) evt.getOldValue());
                    view.removePropertyChangeListener(propertyChangeHandler);
                }
                if (evt.getNewValue() != null) {
                    DrawingView view = ((DrawingView) evt.getNewValue());
                    view.addPropertyChangeListener(propertyChangeHandler);
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
        this.view = view;
    }

    protected void setEditor(DrawingEditor newValue) {
        if (editor != null) {
            editor.removePropertyChangeListener(propertyChangeHandler);
        }
        editor = newValue;
        if (editor != null) {
            editor.addPropertyChangeListener(propertyChangeHandler);
        }
    }

    protected DrawingEditor getEditor() {
        return editor;
    }

    protected DrawingView getView() {
        return (view != null) ? view : ((editor != null) ? editor.getActiveView():null);
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
}
