/*
 * @(#)AbstractEditorAction.java  1.1 2006-03-15
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
 * @version 1.1 2006-03-15 Support for enabled state of editor added.
 * <br>1.0 2003-12-01 Created.
 */
public abstract class AbstractEditorAction extends AbstractAction {
    protected DrawingEditor editor;
    protected final static ResourceBundleUtil labels = 
            ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
    private PropertyChangeListener propertyChangeHandler = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("enabled")) {
                updateEnabledState();
            }
        }
    };
    
    /** Creates a new instance. */
    public AbstractEditorAction(DrawingEditor editor) {
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
    
    public void updateEnabledState() {
            setEnabled(editor != null && editor.isEnabled());
    }
    
    public DrawingEditor getEditor() {
        return editor;
    }
    protected DrawingView getView() {
        return editor.getView();
    }
    protected Drawing getDrawing() {
        return getView().getDrawing();
    }
    protected void fireUndoableEditHappened(UndoableEdit edit) {
        getDrawing().fireUndoableEditHappened(edit);
    }

}
