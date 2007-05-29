/*
 * @(#)JDoubleAttributeSlider.java  1.0  April 30, 2007
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.gui;

import javax.swing.JSlider;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.util.*;

/**
 * A JSlider that can be used to edit a double attribute of a Figure.
 *
 * @author Werner Randelshofer
 * @version 1.0 April 30, 2007 Created.
 */
public class JDoubleAttributeSlider extends JSlider {
    private double scaleFactor = 1d;
    private DrawingEditor editor;
    private AttributeKey<Double> attributeKey;
    private boolean isMultipleValues;
    protected ResourceBundleUtil labels =
            ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
    private int isUpdatingSlider = 0;
    private LinkedList<Object> attributeRestoreData = new LinkedList<Object>();
    
    private PropertyChangeListener viewEventHandler = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name == "enabled") {
                updateEnabledState();
            }
        }
    };
    
    private class EditorEventHandler implements PropertyChangeListener, FigureSelectionListener {
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name == DrawingEditor.PROP_ACTIVE_VIEW) {
                if (evt.getOldValue() != null) {
                    DrawingView view = ((DrawingView) evt.getOldValue());
                    view.removeFigureSelectionListener(this);
                    view.removePropertyChangeListener(viewEventHandler);
                }
                if (evt.getNewValue() != null) {
                    DrawingView view = ((DrawingView) evt.getNewValue());
                    view.addFigureSelectionListener(this);
                    view.addPropertyChangeListener(viewEventHandler);
                }
                updateEnabledState();
                updateSlider();
            } else if (name.equals(attributeKey.getKey())) {
                updateSlider();
            }
        }
        public void selectionChanged(FigureSelectionEvent evt) {
            updateEnabledState();
            updateSlider();
        }
    };
    
    private EditorEventHandler eventHandler = new EditorEventHandler();
    
    private class ChangeHandler implements ChangeListener {
        public void stateChanged(ChangeEvent evt) {
            // FIXME - Use isValueAdjusting for undo/redo
            updateFigures();
        }
    }
    
    private ChangeHandler changeHandler = new ChangeHandler();
    
    
    /** Creates new instance. */
    public JDoubleAttributeSlider() {
        this(null, null);
    }
    public JDoubleAttributeSlider(int orientation, int min, int max, int value) {
        super(orientation, min, max, value);
        getModel().addChangeListener(changeHandler);
    }
    public JDoubleAttributeSlider(DrawingEditor editor, AttributeKey<Double> attributeKey) {
        initComponents();
        this.attributeKey = attributeKey;
        setEditor(editor);
        this.setModel(new DefaultBoundedRangeModel());
    }
    
    public void setAttributeKey(AttributeKey<Double> newValue) {
        this.attributeKey = newValue;
    }
    public void setEditor(DrawingEditor editor) {
        if (this.editor != null) {
            this.editor.removePropertyChangeListener(eventHandler);
            if (getView() != null) {
                getView().removeFigureSelectionListener(eventHandler);
            }
        }
        this.editor = editor;
        if (this.editor != null) {
            this.editor.addPropertyChangeListener(eventHandler);
            if (getView() != null) {
                getView().addFigureSelectionListener(eventHandler);
            }
            updateEnabledState();
            updateSlider();
        }
    }
    public DrawingEditor getEditor() {
        return editor;
    }
    protected DrawingView getView() {
        return (editor == null) ? null : editor.getActiveView();
    }
    
    public void setModel(BoundedRangeModel brm) {
        BoundedRangeModel model = getModel();
        if (model != null) {
            model.removeChangeListener(changeHandler);
        }
        super.setModel(brm);
        if (brm != null) {
            brm.addChangeListener(changeHandler);
        }
    }
    
    
    protected void updateEnabledState() {
        if (getView() != null) {
            setEnabled(getView().isEnabled() &&
                    getView().getSelectionCount() > 0
                    );
        } else {
            setEnabled(false);
        }
    }
    
    public void setScaleFactor(double newValue) {
        this.scaleFactor = newValue;
    }
    
    public double getScaleFactor() {
        return scaleFactor;
    }
    
    protected void updateSlider() {
        if (isUpdatingSlider++ == 0) {
            if (getView() == null || attributeKey == null) {
                setValue(0);
            } else {
                Double sliderValue = null;
                boolean isFirst = true;
                isMultipleValues = false;
                for (Figure f : getView().getSelectedFigures()) {
                    if (isFirst) {
                        isFirst = false;
                        sliderValue = attributeKey.get(f);
                    } else {
                        Double figureValue = attributeKey.get(f);
                        if (figureValue == sliderValue ||
                                figureValue != null && sliderValue != null &&
                                figureValue.equals(sliderValue)) {
                        } else {
                            sliderValue = null;
                            isMultipleValues = true;
                        }
                    }
                }
                if (sliderValue != null) {
                    setValue((int) (sliderValue * scaleFactor));
                }
            }
            repaint();
        }
        isUpdatingSlider--;
    }
    
    private void updateFigures() {
        if (isUpdatingSlider++ == 0) {
            double value = getValue() / scaleFactor;
            if (getView() != null && attributeKey != null) {
                if (attributeRestoreData.isEmpty()) {
                    for (Figure f : getView().getSelectedFigures()) {
                        attributeRestoreData.add(f.getAttributesRestoreData());
                        attributeKey.set(f, value);
                    }
                } else {
                    for (Figure f : getView().getSelectedFigures()) {
                        attributeKey.set(f, value);
                    }
                }
            }
            if (editor != null) {
                editor.setDefaultAttribute(attributeKey, value);
            }
            if (! getModel().getValueIsAdjusting()) {
                final LinkedList<Figure> editedFigures =
                        new LinkedList<Figure>(getView().getSelectedFigures());
                final LinkedList<Object> editUndoData = new LinkedList<Object>(attributeRestoreData);
                final double editRedoValue = value;
                UndoableEdit edit = new AbstractUndoableEdit() {
                    public String getPresentationName() {
                        return labels.getString(attributeKey.getKey());
                    }
                    public void undo() throws CannotRedoException {
                        super.undo();
                        Iterator<Object> di = editUndoData.iterator();
                        for (Figure f : editedFigures) {
                            f.willChange();
                            f.restoreAttributesTo(di.next());
                            f.changed();
                        }
                    }
                    public void redo() throws CannotRedoException {
                        super.redo();
                        for (Figure f : editedFigures) {
                            f.willChange();
                            attributeKey.basicSet(f, editRedoValue);
                            f.changed();
                        }
                    }
                };
                getView().getDrawing().fireUndoableEditHappened(edit);
            }
        }
        isUpdatingSlider--;
    }
    
    public void dispose() {
        if (this.editor != null) {
            this.editor.removePropertyChangeListener(eventHandler);
            if (this.editor.getActiveView() != null) {
                this.editor.getActiveView().removeFigureSelectionListener(eventHandler);
            }
        }
        this.editor = null;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
