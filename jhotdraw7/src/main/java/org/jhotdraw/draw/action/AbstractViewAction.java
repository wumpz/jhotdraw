/*
 * @(#)AbstractViewAction.java  1.2  2006-04-21
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
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
import java.beans.*;
import javax.swing.*;
import javax.swing.undo.*;
import java.util.*;
/**
 * Abstract super class for actions which act on a DrawingView.
 *
 * @author Werner Randelshofer
 * @version 1.2 2006-04-21 Method setEditor added.
 * <br>1.1 2006-03-15 Support for enabled state of view added.
 * <br>1.0 2003-12-01 Created.
 */
public abstract class AbstractViewAction extends AbstractAction {
    private DrawingEditor editor;
    private DrawingView view;
    protected final static ResourceBundleUtil labels =
            ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
    
    private PropertyChangeListener propertyChangeHandler = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("enabled")) {
                updateEnabledState();
            }
        }
    };
    /**
     * Creates a view action which acts on the current view of the editor.
     */
    public AbstractViewAction(DrawingEditor editor) {
        this.editor = editor;
        editor.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("view")) {
                    if (evt.getOldValue() != null) {
                        DrawingView view = ((DrawingView) evt.getOldValue());
                        view.removePropertyChangeListener(propertyChangeHandler);
                    }
                    if (evt.getNewValue() != null) {
                        DrawingView view = ((DrawingView) evt.getOldValue());
                        view.addPropertyChangeListener(propertyChangeHandler);
                        updateEnabledState();
                    }
                }
            }
        });
    }
    /**
     * Creates a view action which acts on the specified view.
     */
    public AbstractViewAction(DrawingView view) {
        this.view = view;
    }
    
    protected void setEditor(DrawingEditor newValue) {
        editor = newValue;
    }
    protected DrawingView getView() {
        return (view != null) ? view : editor.getActiveView();
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
            setEnabled(getView().isEnabled() && 
                    getView().getSelectionCount() > 0
                    );
        } else {
            setEnabled(false);
        }
    }
}
