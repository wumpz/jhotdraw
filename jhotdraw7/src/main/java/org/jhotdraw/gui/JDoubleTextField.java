/*
 * @(#)JIntegerTextField.java  2.0  2007-12-22
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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * A JTextField which only accepts integer values as input.
 * <p>
 * Use methods setValue/getValue to retrieve the integer value of the
 * JTextField.
 *
 * @author Werner Randelshofer
 * @version 2.0 2007-12-22 Formatting and parsing is now done using a DecimalFormat object. 
 * <br>1.0 August 1, 2007 Created.
 */
public class JDoubleTextField extends JTextField {

    private double value;
    private double minimum = Double.MIN_VALUE;
    private double maximum = Double.MAX_VALUE;
    private DecimalFormat format;
    /**
     * This variable is used to prevent endless update loops.
     * We increase its value on each entry in one of the update methods
     * and decrease it on each exit.
     */
    private int updatingDepth;

    private class DocumentHandler implements DocumentListener {

        public void insertUpdate(DocumentEvent e) {
            updateValue();
        }

        public void removeUpdate(DocumentEvent e) {
            updateValue();
        }

        public void changedUpdate(DocumentEvent e) {
            updateValue();
        }
    }
    private DocumentHandler documentHandler;

    /** Creates new instance. */
    public JDoubleTextField() {
        initComponents();

        format = createFormat();

        addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
                selectAll();
            }

            public void focusLost(FocusEvent e) {
                updateField();
            }
        });
    }

    protected DecimalFormat createFormat() {
        DecimalFormat f = new DecimalFormat();
        f.setParseBigDecimal(false);
        f.setMinimumFractionDigits(0);
        return f;
    }

    public void setDocument(Document newValue) {
        Document oldValue = getDocument();
        super.setDocument(newValue);

        if (documentHandler == null) {
            documentHandler = new DocumentHandler();
        }

        if (oldValue != null) {
            oldValue.removeDocumentListener(documentHandler);
        }
        if (newValue != null) {
            newValue.addDocumentListener(documentHandler);
        }
        updateValue();
    }

    protected void updateValue() {
        if (updatingDepth++ == 0) {
            if (format != null) {
                try {
                    double newValue = format.parse(getText()).doubleValue();
                    if (newValue >= minimum && newValue <= maximum) {
                        setValue(newValue);
                    }
                } catch (ParseException ex) {
                //ex.printStackTrace(); do nothing
                }
            }
        }
        updatingDepth--;
    }

    protected void updateField() {
        if (updatingDepth++ == 0) {
            if (format != null) {
                if (!isFocusOwner()) {
                    String text = format.format(value);
                    if (text.endsWith(".0")) {
                        text = text.substring(0, text.length() - 2);
                    }
                    setText(text);
                }
            }
        }
        updatingDepth--;
    }

    public void setValue(double newValue) {
        double oldValue = value;
        value = newValue;

        if (newValue != oldValue) {
            firePropertyChange("value", oldValue, newValue);
            updateField();
        }
    }

    public double getValue() {
        return value;
    }

    public void setMinimum(double newValue) {
        double oldValue = minimum;
        minimum = newValue;
        firePropertyChange("minimum", oldValue, newValue);
    }

    public double getMinimum() {
        return minimum;
    }

    public void setMaximum(double newValue) {
        double oldValue = maximum;
        maximum = newValue;
        firePropertyChange("maximum", oldValue, newValue);
    }

    public double getMaximum() {
        return maximum;
    }

    public void setFormat(DecimalFormat newValue) {
        DecimalFormat oldValue = format;
        format = newValue;
        firePropertyChange("format", oldValue, newValue);
    }

    public DecimalFormat getFormat() {
        return format;
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
