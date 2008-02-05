/*
 * @(#)Options.java  1.0.1  2005-03-14
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
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
