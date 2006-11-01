/*
 * @(#)LocaleUtil.java  1.0  22. Mai 2006
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.util;

import java.util.*;
/**
 * LocaleUtil provides a setDefault()/getDefault() wrapper to java.util.Locale
 * in order to overcome the security restriction preventing Applets from using
 * their own locale.
 *
 * @author Werner Randelshofer
 * @version 1.0 22. Mai 2006 Created.
 */
public class LocaleUtil {
    private static Locale defaultLocale;
    
    /** Creates a new instance. */
    public LocaleUtil() {
    }
    
    public static void setDefault(Locale newValue) {
        defaultLocale = newValue;
    }
    public static Locale getDefault() {
        return (defaultLocale == null) ? Locale.getDefault() : defaultLocale;
    }
}
