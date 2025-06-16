/*
 * @(#)LocaleUtil.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.utils.util;

import java.util.*;

/**
 * LocaleUtil provides a setDefault()/getDefault() wrapper to java.util.Locale in order to overcome
 * the security restriction preventing Applets from using their own locale.
 */
public class LocaleUtil {

  private static Locale defaultLocale;

  public LocaleUtil() {}

  public static void setDefault(Locale newValue) {
    defaultLocale = newValue;
  }

  public static Locale getDefault() {
    return (defaultLocale == null) ? Locale.getDefault() : defaultLocale;
  }
}
