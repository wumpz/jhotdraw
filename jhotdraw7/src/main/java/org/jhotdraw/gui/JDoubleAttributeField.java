/*
 * @(#)JStringAttributeField.java  1.0  April 22, 2007
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

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.util.*;

/**
 * An entry field that can be used to edit a {@code Double} attribute of a
 * {@code Figure}.
 * <p>
 * The {@code JDoubleAttributeField} can either be global to all
 * {@code DrawingView}s of a {@code DrawingEditor}, or it can be local to a
 * single {@code DrawingView}.
 * <p>
 * In both cases, the drawing editor must be basicSet using method {@code setEditor}.
 * To make the entry field local to a single {@code DrawingView}, the view must
 * be basicSet using method {@code setView}.
 * 
 * FIXME We have got many kinds of attribute fields. Factor out all reusable
 * code.
 * 
 * @author Werner Randelshofer
 * @version 1.0 April 22, 2007 Created.
 */
public class JDoubleAttributeField extends JFormattedTextField
implements AttributeField {
    private static final boolean DEBUG = false;
    
    private double scaleFactor = 1d;
    private double min = Double.MIN_VALUE;
    private double max = Double.MAX_VALUE;
    
    private AttributeKey<Double> attributeKey;
    private boolean isMultipleValues;
    
    protected ResourceBundleUtil labels =
            ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
    
    private int isUpdatingField = 0;
    
    private AttributeFieldEventHandler eventHandler = new AttributeFieldEventHandler(this);
    
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
    
    public void setAttributeKey(AttributeKey<Double> newValue) {
        AttributeKey<Double> oldValue = attributeKey;
        attributeKey = newValue;
        updateField(eventHandler.getCurrentSelection());
        firePropertyChange("attributeKey", oldValue, newValue);
    }
    /**
     * Sets the drawing editor. This must be basicSet to a non-null value to make
     *  the attribute field functional.
     * <p>
     * This is a bound property. The default value is null.
     */
    public void setEditor(DrawingEditor newValue) {
        DrawingEditor oldValue = eventHandler.getEditor();
        eventHandler.setEditor(newValue);
        firePropertyChange("editor", oldValue, newValue);
    }
    public DrawingEditor getEditor() {
        return eventHandler.getEditor();
    }
    
    /**
     * Sets the drawing view. Setting this to a non-null value, makes the
     * attribute field local to the drawing view. Setting this to null, makes
     * the attribute field global to all drawing views in the drawing editor.
     * <p>
     * This is a bound property. The default value is null.
     */
    public void setView(DrawingView newValue) {
        DrawingView oldValue = eventHandler.getView();
        eventHandler.setView(newValue);
        firePropertyChange("view", oldValue, newValue);
    }
    
    public DrawingView getView() {
        return eventHandler.getView();
    }
    
    public void setScaleFactor(double newValue) {
        this.scaleFactor = newValue;
        updateField(eventHandler.getCurrentSelection());
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
    
    
    public void updateField(Set<Figure> currentSelection) {
        if (isUpdatingField++ == 0) {
            if (currentSelection.isEmpty() || attributeKey == null) {
                setValue(0d);
            } else {
                Double fieldValue = null;
                boolean isFirst = true;
                isMultipleValues = false;
                for (Figure f : currentSelection) {
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
            if (! eventHandler.getCurrentSelection().isEmpty() && attributeKey != null) {
                for (Figure f : eventHandler.getCurrentSelection()) {
                    attributeKey.set(f, fieldValue);
                }
            }
            eventHandler.getEditor().setDefaultAttribute(attributeKey, fieldValue);
        }
        isUpdatingField--;
    }
    
    public void dispose() {
        eventHandler.dispose();
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
        } else {
            super.paintComponent(g);
        }
    }
    
    public JDoubleAttributeField clone() {
        try {
            JDoubleAttributeField that;
            that = (JDoubleAttributeField) super.clone();
            that.eventHandler = new AttributeFieldEventHandler(that);
            return that;
        } catch (CloneNotSupportedException ex) {
            InternalError error = new InternalError(ex.getMessage());
            error.initCause(ex);
            throw error;
        }
    }

    public JComponent getComponent() {
        return this;
    }
}
