/*
 * @(#)JStringAttributeField.java  1.0  April 22, 2007
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

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.util.*;

/**
 * A JTextField that can be used to edit a String attribute of a Figure.
 *
 * @author Werner Randelshofer
 * @version 1.0 April 22, 2007 Created.
 */
public class JDoubleAttributeField extends JFormattedTextField {
    private static final boolean DEBUG = false;
    
    private double scaleFactor = 1d;
    private double min = Double.MIN_VALUE;
    private double max = Double.MAX_VALUE;
    
    private DrawingEditor editor;
    private AttributeKey<Double> attributeKey;
    private boolean isMultipleValues;
    protected ResourceBundleUtil labels =
            ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
    private int isUpdatingField = 0;
    
    private PropertyChangeListener viewEventHandler = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name.equals("enabled")) {
                updateEnabledState();
            }
        }
    };
    
    private class EditorEventHandler implements PropertyChangeListener, FigureSelectionListener {
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name.equals("focusedView")) {
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
                updateField();
            } else if (name.equals(attributeKey.getKey())) {
                updateField();
            }
        }
        public void selectionChanged(FigureSelectionEvent evt) {
            updateEnabledState();
            updateField();
        }
    };
    
    private EditorEventHandler eventHandler = new EditorEventHandler();
    
    /** Creates new instance. */
    public JDoubleAttributeField() {
        this(null, null);
    }
    public JDoubleAttributeField(DrawingEditor editor, AttributeKey<Double> attributeKey) {
        initComponents();
        this.attributeKey = attributeKey;
        setEditor(editor);
        this.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateFigures();
            }
            
            public void removeUpdate(DocumentEvent e) {
                updateFigures();
            }
            
            public void changedUpdate(DocumentEvent e) {
                updateFigures();
            }
        });
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                updateFigures();
            }
        });
    }
    
    public void setAttributeKey(AttributeKey<Double> attributeKey) {
        this.attributeKey = attributeKey;
    }
    public void setEditor(DrawingEditor editor) {
        if (DEBUG) System.out.println("JFigureAttributeField.setEditor("+editor+")");
        if (this.editor != null) {
            this.editor.removePropertyChangeListener(eventHandler);
            if (getView() != null) {
                getView().removeFigureSelectionListener(eventHandler);
            }
        }
        this.editor = editor;
        if (this.editor != null) {
            this.editor.addPropertyChangeListener(eventHandler);
        }
    }
    public DrawingEditor getEditor() {
        return editor;
    }
    protected DrawingView getView() {
        return (editor == null) ? null : editor.getFocusedView();
    }
    public void setScaleFactor(double newValue) {
        this.scaleFactor = newValue;
    }
    
    public double getScaleFactor() {
        return scaleFactor;
    }
    
    public void setMinimum(double newValue) {
        this.min = newValue;
    }
    
    public double getMinimum() {
        return min;
    }
    
    public void setMaximum(double newValue) {
        this.max = newValue;
    }
    
    public double getMaximum() {
        return max;
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
    protected void updateField() {
        if (DEBUG) System.out.println("JFigureAttributeField.updateText");
        //if (! isFocusOwner()) {
        if (isUpdatingField++ == 0) {
            if (getView() == null || attributeKey == null) {
                setValue(0d);
            } else {
                Double fieldValue = null;
                boolean isFirst = true;
                isMultipleValues = false;
                for (Figure f : getView().getSelectedFigures()) {
                    if (isFirst) {
                        isFirst = false;
                        fieldValue = attributeKey.get(f);
                    } else {
                        Double figureValue = attributeKey.get(f);
                        if (figureValue == fieldValue ||
                                figureValue != null && fieldValue != null &&
                                figureValue.equals(fieldValue)) {
                        } else {
                            fieldValue = null;
                            isMultipleValues = true;
                        }
                    }
                }
                if (fieldValue != null) {
                    setValue(fieldValue * scaleFactor);
                }
            }
            repaint();
        }
        isUpdatingField--;
    }
    
    private void updateFigures() {
        if (isUpdatingField++ == 0) {
            Double fieldValue = Math.min(Math.max(min, (Double) getValue()), max) / scaleFactor;
            if (getView() != null && attributeKey != null) {
                for (Figure f : getView().getSelectedFigures()) {
                    attributeKey.set(f, fieldValue);
                }
            }
            editor.setDefaultAttribute(attributeKey, fieldValue);
        }
        isUpdatingField--;
    }
    
    public void dispose() {
        if (this.editor != null) {
            this.editor.removePropertyChangeListener(eventHandler);
            if (this.editor.getView() != null) {
                this.editor.getView().removeFigureSelectionListener(eventHandler);
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
    
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (! isFocusOwner() && isMultipleValues) {
            Insets insets = getInsets();
            Insets margin = getMargin();
            int height = getHeight();
            FontMetrics fm = g.getFontMetrics(getFont());
            //g.setColor(Color.DARK_GRAY);
            g.setFont(getFont().deriveFont(Font.ITALIC));
            g.drawString(labels.getString("multipleValues"),
                    insets.left + margin.left,
                    insets.top + margin.top + fm.getAscent()
                    );
        }
    }
    
}
