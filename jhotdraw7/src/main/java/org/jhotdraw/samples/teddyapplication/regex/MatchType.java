/*
 * @(#)MatchType.java  1.0  November 14, 2004
 *
 * Copyright (c) 2004 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.samples.teddyapplication.regex;

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
