/*
 * @(#)AttributeFieldEventHandler.java  1.0  15. Mai 2007
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.gui;

import java.beans.*;
import java.util.*;
import org.jhotdraw.draw.*;

/**
 * Helper class for {@code AttributeField}. Listens for the current view of the
 * DrawingEditor.
 *
 * @author Werner Randelshofer
 * @version 1.0 15. Mai 2007 Created.
 */
public class AttributeFieldEventHandler
        implements PropertyChangeListener, FigureSelectionListener, FigureListener {
    private AttributeField field;
    private DrawingEditor editor;
    private DrawingView view;
    
    public AttributeFieldEventHandler(AttributeField field) {
        this.field = field;
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        Object src = evt.getSource();

        // handle property change events on the DrawingEditor
        if (src == editor) {
            if (name == DrawingEditor.ACTIVE_VIEW_PROPERTY && getView() != null) {
                if (evt.getOldValue() != null) {
                    DrawingView view = ((DrawingView) evt.getOldValue());
                    for (Figure f : view.getSelectedFigures()) {
                        f.removeFigureListener(this);
                    }
                    view.removeFigureSelectionListener(this);
                    view.removePropertyChangeListener(this);
                }
                if (evt.getNewValue() != null) {
                    DrawingView view = ((DrawingView) evt.getNewValue());
                    for (Figure f : view.getSelectedFigures()) {
                        f.addFigureListener(this);
                    }
                    view.addFigureSelectionListener(this);
                    view.addPropertyChangeListener(this);
                }
                updateFieldEnabledState();
                updateField();
            }
            
            // handle property change events on the current DrawingView
        } else if (src == getCurrentView()) {
            if (name == "enabled") {
                updateFieldEnabledState();
            }
        }
    }
    
    
    public void selectionChanged(FigureSelectionEvent evt) {
        for (Figure f : evt.getOldSelection()) {
            f.removeFigureListener(this);
        }
        for (Figure f : evt.getNewSelection()) {
            f.addFigureListener(this);
        }
        updateFieldEnabledState();
        updateField();
    }
    
    
    protected void updateFieldEnabledState() {
        if (getCurrentView() != null) {
            field.getComponent().setEnabled(getCurrentView().isEnabled() &&
                    getCurrentView().getSelectionCount() > 0
                    );
        } else {
            field.getComponent().setEnabled(false);
        }
    }
    protected void updateField() {
        field.updateField(getCurrentView().getSelectedFigures());
    }
    protected Set<Figure> getCurrentSelection() {
        if (getCurrentView() != null) {
            return getCurrentView().getSelectedFigures();
        } else {
            return Collections.emptySet();
        }
    }
    
    
    public void areaInvalidated(FigureEvent e) {
        // empty
    }
    
    public void attributeChanged(FigureEvent e) {
        updateField();
    }
    
    
    public void figureHandlesChanged(FigureEvent e) {
        // empty
    }
    
    public void figureChanged(FigureEvent e) {
        // empty
    }
    
    public void figureAdded(FigureEvent e) {
        // empty
    }
    
    public void figureRemoved(FigureEvent e) {
        // empty
    }
    
    public void figureRequestRemove(FigureEvent e) {
        // empty
    }
    /**
     * Sets the drawing editor. This must be set to a non-null value to make
     *  the attribute field functional.
     */
    public void setEditor(DrawingEditor newValue) {
        if (editor != null) {
            editor.removePropertyChangeListener(this);
            if (getCurrentView() != null) {
                getCurrentView().removeFigureSelectionListener(this);
            }
        }
        editor = newValue;
        if (editor != null) {
            editor.addPropertyChangeListener(this);
            if (getCurrentView() != null) {
                getCurrentView().addFigureSelectionListener(this);
            } 
        }
    }
    public DrawingEditor getEditor() {
        return editor;
    }
    /**
     * Sets the drawing view. Setting this to a non-null value, makes the
     * attribute field local to the drawing view. Setting this to null, makes
     * the attribute field global to all drawing views in the drawing editor.
     */
    public void setView(DrawingView newValue) {
        if (getCurrentView() != null) {
            getCurrentView().removeFigureSelectionListener(this);
            for (Figure f : getCurrentView().getSelectedFigures()) {
                f.removeFigureListener(this);
            }
        }
        view = newValue;
        if (getCurrentView() != null) {
            getCurrentView().addFigureSelectionListener(this);
            for (Figure f : getCurrentView().getSelectedFigures()) {
                f.addFigureListener(this);
            }
        }
    }
    
    public DrawingView getView() {
        return view;
    }
    
    public DrawingView getCurrentView() {
        return (view != null) ? view :
            ((editor == null) ? null : editor.getActiveView());
    }
    
    public void dispose() {
        setEditor(null);
    }
}