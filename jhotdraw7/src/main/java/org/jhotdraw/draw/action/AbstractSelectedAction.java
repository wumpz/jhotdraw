/*
 * @(#)AbstractSelectedAction.java  4.0  2008-06-08
 *
 * Copyright (c) 2003-2008 by the original authors of JHotDraw
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
import org.jhotdraw.draw.FigureSelectionEvent;
import org.jhotdraw.draw.FigureSelectionListener;
import javax.swing.*;
import java.beans.*;
import java.io.Serializable;
import javax.swing.undo.*;
import org.jhotdraw.util.*;
import java.util.*;
import org.jhotdraw.beans.Disposable;
import org.jhotdraw.beans.WeakPropertyChangeListener;

/**
 * Abstract super class for actions which act on the selected figures of a 
 * {@link DrawingEditor}. If no figures are selected, the action is disabled.
 * <b>
 * {@code AbstractDrawingEditorAction} listens using a
 * {@link WeakPropertyChangeListener} on the {@code DrawingEditor} and thus may
 * become garbage collected if it is not referenced by any other object.
 *
 * @author Werner Randelshofer
 *
 * @version 4.0 2009-06-02 Register with DrawingEditor using
 * WeakPropertyChangeListener.
 * <br>3.1.2 2008-06-08 Method setEditor did not register the EventHandler
 * to the active view of the editor.
 * <br>3.1.1. 2006-07-09 Fixed enabled state. 
 * <br>3.1 2006-03-15 Support for enabled state of view added.
 * <br>3.0 2006-02-24 Changed to support multiple views.
 * <br>2.0 2006-02-14 Updated to work with multiple views.
 * <br>1.0 2003-12-01 Created.
 */
public abstract class AbstractSelectedAction
        extends AbstractAction implements Disposable {

    private DrawingEditor editor;
    private DrawingView activeView;
    protected ResourceBundleUtil labels =
            ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels", Locale.getDefault());

    private class EventHandler implements PropertyChangeListener, FigureSelectionListener, Serializable {

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName() == DrawingEditor.ACTIVE_VIEW_PROPERTY) {
                if (activeView != null) {
                    activeView.removeFigureSelectionListener(this);
                    activeView.removePropertyChangeListener(this);
                }
                if (evt.getNewValue() != null) {
                    activeView = ((DrawingView) evt.getNewValue());
                    activeView.addFigureSelectionListener(this);
                    activeView.addPropertyChangeListener(this);
                }
                updateEnabledState();
            } else if (evt.getPropertyName().equals("enabled")) {
                updateEnabledState();
            }
        }

        public String toString() {
            return AbstractSelectedAction.this + " " + this.getClass();
        }

        public void selectionChanged(FigureSelectionEvent evt) {
            updateEnabledState();

        }
    };
    private EventHandler eventHandler = new EventHandler();

    /** Creates an action which acts on the selected figures on the current view
     * of the specified editor.
     */
    public AbstractSelectedAction(DrawingEditor editor) {
        setEditor(editor);
        updateEnabledState();
    }

    protected void updateEnabledState() {
        if (getView() != null) {
            setEnabled(getView().isEnabled() &&
                    getView().getSelectionCount() > 0);
        } else {
            setEnabled(false);
        }
    }

    public void dispose() {
        setEditor(null);
    }

    public void setEditor(DrawingEditor editor) {
        if (this.editor != null) {
            this.editor.removePropertyChangeListener(eventHandler);
            if (this.editor.getActiveView() != null) {
                this.editor.getActiveView().removeFigureSelectionListener(eventHandler);
            }
        }
        if (activeView != null) {
            activeView.removeFigureSelectionListener(eventHandler);
            activeView.removePropertyChangeListener(eventHandler);
        }
        this.editor = editor;
        if (this.editor != null) {
            this.editor.addPropertyChangeListener(new WeakPropertyChangeListener(eventHandler));
            activeView = editor.getActiveView();
            if (activeView != null) {
                activeView.addFigureSelectionListener(eventHandler);
                activeView.addPropertyChangeListener(eventHandler);
            }
            updateEnabledState();
        }
    }

    public DrawingEditor getEditor() {
        return editor;
    }

    protected DrawingView getView() {
        return (editor == null) ? null : editor.getActiveView();
    }

    protected Drawing getDrawing() {
        return (getView() == null) ? null : getView().getDrawing();
    }

    protected void fireUndoableEditHappened(UndoableEdit edit) {
        getDrawing().fireUndoableEditHappened(edit);
    }
}
