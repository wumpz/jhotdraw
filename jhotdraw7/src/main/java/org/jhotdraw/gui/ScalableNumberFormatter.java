/*
 * @(#)ScalableNumberFormatter.java  1.0  2009-04-15
 * 
 * Copyright (c) 2009 by the original authors of JHotDraw
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

import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * A NumberFormatter whitch supports a scale factor.
 *
 * @author Werner Randelshofer
 * @version 1.0 2009-04-15 Created.
 */
public class ScalableNumberFormatter extends NumberFormatter {

    private double scaleFactor = 1d;
    private boolean isNullValueAllowed = false;

    /**
     * Creates a <code>NumberFormatter</code> with the a default
     * <code>NumberFormat</code> instance obtained from
     * <code>NumberFormat.getNumberInstance()</code>.
     */
    public ScalableNumberFormatter() {
        super();
    }

    /**
     * Creates a NumberFormatter with the specified Format instance.
     *
     * @param format Format used to dictate legal values
     */
    public ScalableNumberFormatter(NumberFormat format) {
        super(format);
    }

    /**
     * Creates a NumberFormatter with the specified Format instance.
     *
     * @param format Format used to dictate legal values
     */
    public ScalableNumberFormatter(double min, double max, double scaleFactor) {
        this(min,max,scaleFactor,false);
    }
    /**
     * Creates a NumberFormatter with the specified Format instance.
     *
     * @param format Format used to dictate legal values
     */
    public ScalableNumberFormatter(double min, double max, double scaleFactor, boolean isNullValueAllowed) {
        super();
        setMinimum(min);
        setMaximum(max);
        setScaleFactor(scaleFactor);
        setNullValueAllowed(isNullValueAllowed);
    }

    /**
     * Changes the scale factor of the number formatter.
     *
     * @param newValue
     */
    public void setScaleFactor(double newValue) {
        scaleFactor = newValue;
    }

    /**
     * Returns the scale factor of the number formatter.
     *
     * @param newValue
     */
    public double getScaleFactor() {
        return scaleFactor;
    }

    /**
     * Allows/Disallows null values.
     *
     * @param newValue
     */
    public void setNullValueAllowed(boolean newValue) {
        isNullValueAllowed = newValue;
    }

    /**
     * Returns true if null values are allowed.
     *
     * @param newValue
     */
    public boolean isNullValueAllowed() {
        return isNullValueAllowed;
    }

    /**
     * Returns a String representation of the Object <code>value</code>.
     * This invokes <code>format</code> on the current <code>Format</code>.
     *
     * @throws ParseException if there is an error in the conversion
     * @param value Value to convert
     * @return String representation of value
     */
    @Override
    public String valueToString(Object value) throws ParseException {
        if (value == null && isNullValueAllowed()) {
            return "";
        }

        if (value instanceof Double) {
            value = ((Double) value) * scaleFactor;
        } else if (value instanceof Float) {
            value = (float) (((Float) value) * scaleFactor);
        } else if (value instanceof Long) {
            value = (long) (((Long) value) * scaleFactor);
        } else if (value instanceof Integer) {
            value = (int) (((Integer) value) * scaleFactor);
        } else if (value instanceof Byte) {
            value = (byte) (((Byte) value) * scaleFactor);
        } else if (value instanceof Short) {
            value = (short) (((Short) value) * scaleFactor);
        }

        return super.valueToString(value);
    }

    /**
     * Returns the <code>Object</code> representation of the
     * <code>String</code> <code>text</code>.
     *
     * @param text <code>String</code> to convert
     * @return <code>Object</code> representation of text
     * @throws ParseException if there is an error in the conversion
     */
    @Override
    public Object stringToValue(String text) throws ParseException {
        if ((text == null || text.length() == 0) && isNullValueAllowed()) {
            return null;
        }
         Object value = super.stringToValue(text);
        if (value instanceof Double) {
            value = ((Double) value) / scaleFactor;
        } else if (value instanceof Float) {
            value = (float) (((Float) value) / scaleFactor);
        } else if (value instanceof Long) {
            value = (long) (((Long) value) / scaleFactor);
        } else if (value instanceof Integer) {
            value = (int) (((Integer) value) / scaleFactor);
        } else if (value instanceof Byte) {
            value = (byte) (((Byte) value) / scaleFactor);
        } else if (value instanceof Short) {
            value = (short) (((Short) value) / scaleFactor);
        }
        return value;
    }

    /**
     * Convenience method for creating a formatter factory with a
     * ScalableNumberFormatter.
     */
    public static AbstractFormatterFactory createFormatterFactory(double min, double max, double scaleFactor) {
        return createFormatterFactory( min,  max,  scaleFactor, false);
    }
    /**
     * Convenience method for creating a formatter factory with a
     * ScalableNumberFormatter.
     */
    public static AbstractFormatterFactory createFormatterFactory(double min, double max, double scaleFactor, boolean isNullValueAllowed) {
        return new DefaultFormatterFactory(new ScalableNumberFormatter(min, max, scaleFactor, isNullValueAllowed));
    }
}
