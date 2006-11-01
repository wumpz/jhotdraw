/*
 * @(#)Options.java  1.0.1  2005-03-14
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

package org.jhotdraw.draw;

/**
 * Options.
 *
 * @author  Werner Randelshofer
 * @version 1.0.1 2005-03-13 Fractional metrics and antialiased text turned on.
 *  <br>1.0 8. April 2004  Created.
 */
public class Options {
    
    /** Creates a new instance. */
    public Options() {
    }
    
    public static boolean isFractionalMetrics() {
        return true;
    }
    public static boolean isTextAntialiased() {
        return true;
    }
}
