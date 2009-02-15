/*
 * @(#)AbstractDrawingEditorAction.java  2.0  2009-02-15
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
/**
 * Abstract super class for actions which act on a DrawingEditor.
 *
 * @author Werner Randelshofer
 * @version 2.0 2009-02-15 Renamed from AbstractEditorAction to
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
        this.editor = editor;
        if (editor != null) {
            editor.addPropertyChangeListener(propertyChangeHandler);
                updateEnabledState();
        }
    }
    
    public void setEditor(DrawingEditor newValue) {
        if (editor != null) {
            editor.removePropertyChangeListener(propertyChangeHandler);
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
