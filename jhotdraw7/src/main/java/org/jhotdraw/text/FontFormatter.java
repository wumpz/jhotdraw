/*
 * @(#)FontFormatter.java  1.0  2009-04-17
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

import java.awt.Font;
import java.text.ParseException;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

/**
 * {@code FontFormatter} is used to format fonts into a textual representation
 * which can be edited in an entry field.
 * <p>
 *
 * @author Werner Randelshofer
 * @version 1.0 2009-04-17 Created.
 */
public class FontFormatter extends DefaultFormatter {

    /**
     * Specifies whether the formatter allows null values.
     */
    private boolean allowsNullValue = false;

    public FontFormatter() {
        this(true);
    }

    public FontFormatter(boolean allowsNullValue) {
        this.allowsNullValue = allowsNullValue;
        setOverwriteMode(false);
    }

    /**
     * Sets whether a null value is allowed.
     * @param newValue
     */
    public void setAllowsNullValue(boolean newValue) {
        allowsNullValue = newValue;
    }

    /**
     * Returns true, if null value is allowed.
     */
    public boolean getAllowsNullValue() {
        return allowsNullValue;
    }

    @Override
    public Object stringToValue(String str) throws ParseException {

        // Handle null and empty case
        if (str == null || str.trim().length() == 0) {
            if (allowsNullValue) {
                return null;
            } else {
                throw new ParseException("Null value is not allowed.", 0);
            }
        }

        Font f = Font.decode(str);
        if (f == null) {
            throw new ParseException(str, 0);
        }
        String fontName = f.getFontName();
        if (!fontName.equals(str) &&
                !fontName.equals(str + "-Derived")) {
            throw new ParseException(str, 0);
        }
        return f;
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        String str = null;

        if (value == null) {
            if (allowsNullValue) {
                str = "";
            } else {
                throw new ParseException("Null value is not allowed.", 0);
            }
        } else {
            if (!(value instanceof Font)) {
                throw new ParseException("Value is not a font " + value, 0);
            }

            Font f = (Font) value;
            str = f.getFontName();

        }
        return str;
    }

    /**
     * Convenience method for creating a formatter factory with a
     * {@code FontFormatter}.
     * Uses the RGB_INTEGER format and disallows null values.
     */
    public static AbstractFormatterFactory createFormatterFactory() {
        return createFormatterFactory(false);
    }

    /**
     * Convenience method for creating a formatter factory with a
     * 8@code FontFormatter}.
     */
    public static AbstractFormatterFactory createFormatterFactory(boolean allowsNullValue) {
        return new DefaultFormatterFactory(new FontFormatter(allowsNullValue));
    }
}
