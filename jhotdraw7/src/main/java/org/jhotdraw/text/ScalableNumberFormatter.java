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
package org.jhotdraw.text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * {@code ScaledNumberFormatter} is used to format numbers. This class
 * adds support for a scale factor to {@code java.text.NumberFormat}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2009-04-15 Created.
 */
public class ScalableNumberFormatter extends NumberFormatter {

    private double scaleFactor = 1d;
    private boolean allowsNullValue = false;

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
    public ScalableNumberFormatter(double min, double max, double scaleFactor, boolean allowsNullValue) {
        super();
        setMinimum(min);
        setMaximum(max);
        setScaleFactor(scaleFactor);
        setAllowsNullValue(allowsNullValue);
    }
    /**
     * Creates a NumberFormatter with the specified Format instance.
     *
     * @param format Format used to dictate legal values
     */
    public ScalableNumberFormatter(NumberFormat format, double min, double max, double scaleFactor, boolean allowsNullValue) {
        super(format);
        setMinimum(min);
        setMaximum(max);
        setScaleFactor(scaleFactor);
        setAllowsNullValue(allowsNullValue);
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
    public void setAllowsNullValue(boolean newValue) {
        allowsNullValue = newValue;
    }

    /**
     * Returns true if null values are allowed.
     *
     * @param newValue
     */
    public boolean getAllowsNullValue() {
        return allowsNullValue;
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
        if (value == null && getAllowsNullValue()) {
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
        if ((text == null || text.length() == 0) && getAllowsNullValue()) {
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
     * {@code ScalableNumberFormatter} and a Java-style DecimalFormat.
     */
    public static AbstractFormatterFactory createFormatterFactory(double min, double max, double scaleFactor) {
        return createFormatterFactory( min,  max,  scaleFactor, false);
    }
    /**
     * Convenience method for creating a formatter factory with a
     * {@code ScalableNumberFormatter} and a Java-style DecimalFormat.
     */
    public static AbstractFormatterFactory createFormatterFactory(double min, double max, double scaleFactor, boolean allowsNullValue) {
        DecimalFormat df = new DecimalFormat("#0.#");
        DecimalFormatSymbols sym = df.getDecimalFormatSymbols();
        sym.setGroupingSeparator('\''); // We don't want a grouping separator, but we set one here to avoid conflicts.
        sym.setDecimalSeparator('.');
        sym.setMinusSign('-');
        //sym.setExponentSeparator("E");
        return new DefaultFormatterFactory(new ScalableNumberFormatter(df,min, max, scaleFactor, allowsNullValue));
    }
    /**
     * Convenience method for creating a formatter factory with a
     * {@code ScalableNumberFormatter}.
     */
    public static AbstractFormatterFactory createFormatterFactory(NumberFormat format, double min, double max, double scaleFactor, boolean allowsNullValue) {
        return new DefaultFormatterFactory(new ScalableNumberFormatter(format, min, max, scaleFactor, allowsNullValue));
    }
}
