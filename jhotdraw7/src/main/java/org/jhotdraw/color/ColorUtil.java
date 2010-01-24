/*
 * @(#)ColorUtil.java
 *
 * Copyright (c) 1996-2010 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.color;

import java.awt.*;

/**
 * ColorUtil.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class ColorUtil {
    
    /** Prevent instance creation. */
    private ColorUtil() {
    }

    /**
     * Blackens the specified color by casting a black shadow of the specified 
     * amount on the color.
     */
    public static Color shadow(Color c, int amount) {
       return new Color(
        Math.max(0, c.getRed() - amount),
        Math.max(0, c.getGreen() - amount),
        Math.max(0, c.getBlue() - amount),
        c.getAlpha()
        );
    }
    
}
