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
import org.jhotdraw.util.*;
import javax.swing.*;
import javax.swing.undo.*;
import java.util.*;
import java.beans.*;
import org.jhotdraw.beans.WeakPropertyChangeListener;
/**
 * Abstract super class for actions which act on a {@link DrawingEditor} and
 * which need to listen for property changes of the {@code DrawingEditor}.
 * <b>
 * Altough {@code AbstractDrawingEditorAction} has its own enabled state, it
 * is automatically disabled, if the associated {@code DrawingEditor}
 * is disabled.
 * <b>
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
    private PropertyChangeListener propertyChangeHandler = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("enabled")) {
                updateEnabledState();
            } else if (evt.getPropertyName().equals(DrawingEditor.ACTIVE_VIEW_PROPERTY)) {
                updateViewState();
            }
        }
    };
    
    /** Creates a new instance. */
    public AbstractDrawingEditorAction(DrawingEditor editor) {
        setEditor(editor);
    }
    
    public void setEditor(DrawingEditor newValue) {
        if (editor != null) {
            editor.removePropertyChangeListener(new WeakPropertyChangeListener(propertyChangeHandler));
        }
        editor = newValue;
        if (editor != null) {
            editor.addPropertyChangeListener(propertyChangeHandler);
                updateEnabledState();
        }
    }
    
    protected void updateEnabledState() {
            setEnabled(editor != null && editor.isEnabled());
    }
    
    protected void updateViewState() {
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
    protected void fireUndoableEditHappened(UndoableEdit edit) {
        getDrawing().fireUndoableEditHappened(edit);
    }

}
