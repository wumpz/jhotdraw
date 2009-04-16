/*
 * @(#)MatchType.java  1.0  November 14, 2004
 *
  * Copyright (c) 2004-2005 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and
 * contributors of the JHotDraw project ("the copyright holders").
 * You may not use, copy or modify this software, except in
 * accordance with the license agreement you entered into with
 * the copyright holders. For details see accompanying license terms.
 */

package org.jhotdraw.samples.teddy.regex;

/**
 * Typesafe Enumeration of Syntaxes for the Parser.
 *
 * @author  Werner Randelshofer
 * @version 5.0 2005-01-31 Reworked.
 * <br>1.0  November 14, 2004  Created.
 */
public class MatchType  /*implements Comparable*/ {
    private MatchType() {
    }

    public static final MatchType CONTAINS = new MatchType();
    public static final MatchType STARTS_WITH = new MatchType();
    public static final MatchType FULL_WORD = new MatchType();
}
