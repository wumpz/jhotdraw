/*
 * @(#)JDoubleDrawingAttributeSlider.java  1.0  2008-05-18
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
 * A JSlider that can be used to edit a double attribute of a Drawing.
 *
 * @author Werner Randelshofer
 * @version 1.0 2008-05-18 Created.
 */
public class JDoubleDrawingAttributeSlider extends JSlider {

    private double scaleFactor = 1d;
    private DrawingEditor editor;
    private AttributeKey<Double> attributeKey;
    protected ResourceBundleUtil labels =
            ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
    private int isUpdatingSlider = 0;
    private LinkedList<Object> attributeRestoreData = new LinkedList<Object>();
    public final static String ENABLED_WITHOUT_SELECTION_PROPERTY = "enabledWithoutSelection";
    private PropertyChangeListener viewEventHandler = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name == "enabled") {
                updateEnabledState();
            }
        }
    };

    private class EditorEventHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name == DrawingEditor.ACTIVE_VIEW_PROPERTY) {
                if (evt.getOldValue() != null) {
                    DrawingView view = ((DrawingView) evt.getOldValue());
                    view.removePropertyChangeListener(viewEventHandler);
                }
                if (evt.getNewValue() != null) {
                    DrawingView view = ((DrawingView) evt.getNewValue());
                    view.addPropertyChangeListener(viewEventHandler);
                }
                updateEnabledState();
                updateSlider();
            } else if (name.equals(attributeKey.getKey())) {
                updateSlider();
            }
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
    public JDoubleDrawingAttributeSlider() {
        this(null, null);
    }

    public JDoubleDrawingAttributeSlider(int orientation, int min, int max, int value) {
        super(orientation, min, max, value);
        getModel().addChangeListener(changeHandler);
    }

    public JDoubleDrawingAttributeSlider(DrawingEditor editor, AttributeKey<Double> attributeKey) {
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
        }
        this.editor = editor;
        if (this.editor != null) {
            this.editor.addPropertyChangeListener(eventHandler);
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
            setEnabled(getView().isEnabled());
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
                Figure f = getView().getDrawing();
                sliderValue = attributeKey.get(f);
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
                    Figure f = getView().getDrawing();
                    attributeRestoreData.add(f.getAttributesRestoreData());
                    attributeKey.set(f, value);
                } else {
                    Figure f = getView().getDrawing();
                    attributeKey.set(f, value);
                }
                if (!getModel().getValueIsAdjusting()) {
                    final Figure editedFigure = getView().getDrawing();
                    final LinkedList<Object> editUndoData = new LinkedList<Object>(attributeRestoreData);
                    final double editRedoValue = value;
                    UndoableEdit edit = new AbstractUndoableEdit() {

                        public String getPresentationName() {
                            return labels.getString(attributeKey.getKey());
                        }

                        public void undo() throws CannotRedoException {
                            super.undo();
                            Iterator<Object> di = editUndoData.iterator();
                            editedFigure.willChange();
                            editedFigure.restoreAttributesTo(di.next());
                            editedFigure.changed();
                        }

                        public void redo() throws CannotRedoException {
                            super.redo();
                            editedFigure.willChange();
                            attributeKey.basicSet(editedFigure, editRedoValue);
                            editedFigure.changed();
                        }
                    };
                    getView().getDrawing().fireUndoableEditHappened(edit);
                }
            }
        }
        isUpdatingSlider--;
    }

    public void dispose() {
        if (this.editor != null) {
            this.editor.removePropertyChangeListener(eventHandler);
        }
        this.editor = null;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
