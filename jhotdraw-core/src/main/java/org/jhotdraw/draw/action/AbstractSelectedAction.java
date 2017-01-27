/*
 * @(#)AbstractSelectedAction.java
 *
 * Copyright (c) 2003-2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import javax.annotation.Nullable;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.event.FigureSelectionEvent;
import org.jhotdraw.draw.event.FigureSelectionListener;
import javax.swing.*;
import java.beans.*;
import java.io.Serializable;
import javax.swing.undo.*;
import org.jhotdraw.app.Disposable;
import org.jhotdraw.beans.WeakPropertyChangeListener;

/**
 * This abstract class can be extended to implement an {@code Action} that acts
 * on behalf of the selected figures of a {@link org.jhotdraw.draw.DrawingView}.
 * <p>
 * By default the enabled state of this action reflects the enabled state of the
 * active {@code DrawingView}. If no drawing view is active, this action is
 * disabled. When many actions listen to the enabled state of the active drawing
 * views this can considerably slow down the editor. If updating the enabled
 * state is not necessary, you can prevent the action from doing so using
 * {@link #setUpdateEnabledState}.
 * <p>
 * {@code AbstractDrawingEditorAction} listens using a
 * {@link WeakPropertyChangeListener} on the {@code DrawingEditor} and thus may
 * become garbage collected if it is not referenced by any other object.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractSelectedAction
        extends AbstractAction implements Disposable {
    private static final long serialVersionUID = 1L;

    @Nullable
    private DrawingEditor editor;
    @Nullable transient private DrawingView activeView;

    private class EventHandler implements PropertyChangeListener, FigureSelectionListener, Serializable {
    private static final long serialVersionUID = 1L;

        @Override
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
            } else if ("enabled".equals(evt.getPropertyName())) {
                updateEnabledState();
            }
        }

        @Override
        public String toString() {
            return AbstractSelectedAction.this + " " + this.getClass() + "@" + hashCode();
        }

        @Override
        public void selectionChanged(FigureSelectionEvent evt) {
            updateEnabledState();

        }
    };
    @Nullable private EventHandler eventHandler = new EventHandler();

    /** Creates an action which acts on the selected figures on the current view
     * of the specified editor.
     */
    public AbstractSelectedAction(DrawingEditor editor) {
        setEditor(editor);
        //updateEnabledState();
    }

    /** Updates the enabled state of this action to reflect the enabled state
     * of the active {@code DrawingView}. If no drawing view is active, this
     * action is disabled.
     */
    protected void updateEnabledState() {
        if (getView() != null) {
            setEnabled(getView().isEnabled()
                    && getView().getSelectionCount() > 0);
        } else {
            setEnabled(false);
        }
    }

    @Override
    public void dispose() {
        setEditor(null);
    }

    public void setEditor(@Nullable DrawingEditor editor) {
        if (eventHandler != null) {
            unregisterEventHandler();
        }
        this.editor = editor;
        if (editor != null && eventHandler != null) {
            registerEventHandler();
            updateEnabledState();
        }
    }

    @Nullable
    public DrawingEditor getEditor() {
        return editor;
    }

    @Nullable
    protected DrawingView getView() {
        return (editor == null) ? null : editor.getActiveView();
    }

    @Nullable
    protected Drawing getDrawing() {
        return (getView() == null) ? null : getView().getDrawing();
    }

    protected void fireUndoableEditHappened(UndoableEdit edit) {
        getDrawing().fireUndoableEditHappened(edit);
    }

    /** By default, the enabled state of this action is updated to reflect
     * the enabled state of the active {@code DrawingView}.
     * Since this is not always necessary, and since many listening actions
     * may considerably slow down the drawing editor, you can switch this
     * behavior off here.
     *
     * @param newValue Specify false to prevent automatic updating of the
     * enabled state.
     */
    public void setUpdateEnabledState(boolean newValue) {
        // Note: eventHandler != null yields true, if we are currently updating
        // the enabled state.
        if (eventHandler != null != newValue) {
            if (newValue) {
                eventHandler = new EventHandler();
                registerEventHandler();
            } else {
                unregisterEventHandler();
                eventHandler = null;
            }
        }
        if (newValue) {
            updateEnabledState();
        }
    }

    /** Returns true, if this action automatically updates its enabled
     * state to reflect the enabled state of the active {@code DrawingView}.
     */
    public boolean isUpdatEnabledState() {
        return eventHandler != null;
    }

    /** Unregisters the event handler from the drawing editor and the
     * active drawing view.
     */
    private void unregisterEventHandler() {
        if (editor != null) {
            editor.removePropertyChangeListener(eventHandler);
        }
        if (activeView != null) {
            activeView.removeFigureSelectionListener(eventHandler);
            activeView.removePropertyChangeListener(eventHandler);
            activeView = null;
        }
    }

    /** Registers the event handler from the drawing editor and the 
     * active drawing view.
     */
    private void registerEventHandler() {
        if (editor != null) {
            editor.addPropertyChangeListener(new WeakPropertyChangeListener(eventHandler));
            if (activeView != null) {
                activeView.removeFigureSelectionListener(eventHandler);
                activeView.removePropertyChangeListener(eventHandler);
            }
            activeView = editor.getActiveView();
            if (activeView != null) {
                activeView.addFigureSelectionListener(eventHandler);
                activeView.addPropertyChangeListener(eventHandler);
            }
        }
    }
}
