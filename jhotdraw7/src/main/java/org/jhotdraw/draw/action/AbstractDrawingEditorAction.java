/*
 * @(#)AbstractDrawingEditorAction.java  3.0  2009-06-02
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
import javax.swing.*;
import javax.swing.undo.*;
import java.beans.*;
import org.jhotdraw.beans.WeakPropertyChangeListener;

/**
 * Abstract super class for actions which act on a {@link DrawingEditor} and
 * which need to listen for property changes of the {@code DrawingEditor}.
 * <p>
 * By default the enabled state of this action reflects the enabled state of the
 * {@code DrawingEditor}. When many actions listen to the enabled state of the
 * drawing editor this can considerably slow down the editor. If updating the
 * enabled state is not necessary, you can prevent the action from doing so using
 * {@link #setUpdateEnabledState}.
 * <p>
 * {@code AbstractDrawingEditorAction} listens using a
 * {@link WeakPropertyChangeListener} on the {@code DrawingEditor} and thus may
 * become garbage collected if it is not referenced by any other object.
 *
 * @author Werner Randelshofer
 * @version 3.0 2009-06-02 Register with DrawingEditor using
 * WeakPropertyChangeListener.
 * <br>2.0 2009-02-15 Renamed from AbstractEditorAction to
 * AbstractDrawingEditorAction.
 * <br>1.1 2006-03-15 Support for enabled state of editor added.
 * <br>1.0 2003-12-01 Created.
 */
public abstract class AbstractDrawingEditorAction extends AbstractAction {

    protected DrawingEditor editor;

    private class EventHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("enabled")) {
                updateEnabledState();
            }
        }
    };
    private EventHandler eventHandler = new EventHandler();

    /** Creates a new instance. */
    public AbstractDrawingEditorAction(DrawingEditor editor) {
        setEditor(editor);
    }

    public void setEditor(DrawingEditor newValue) {
        if (eventHandler != null) {
            unregisterEventHandler();
        }
        editor = newValue;
        if (eventHandler != null) {
            registerEventHandler();
            updateEnabledState();
        }
    }

    protected void updateEnabledState() {
        setEnabled(editor != null && editor.isEnabled());
    }

    public DrawingEditor getEditor() {
        return editor;
    }

    protected DrawingView getView() {
        return editor.getActiveView();
    }

    protected Drawing getDrawing() {
        return getView().getDrawing();
    }

    /** Updates the enabled state of this action to reflect the enabled state
     * of the active {@code DrawingView}. If no drawing view is active, this
     * action is disabled.
     */
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
    public void setUpdatEnabledState(boolean newValue) {
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
    }

    /** Registers the event handler from the drawing editor and the
     * active drawing view.
     */
    private void registerEventHandler() {
        if (editor != null) {
            editor.addPropertyChangeListener(new WeakPropertyChangeListener(eventHandler));
        }
    }
}
