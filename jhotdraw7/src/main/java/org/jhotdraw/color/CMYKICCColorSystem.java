/*
 * @(#)CMYKICCColorSystem.java
 *
 * Copyright (c) 2008 by the original authors of JHotDraw
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

/**
 * A {@code ColorSystem} for CMYK color components (cyan, magenta, yellow, black) in
 * a color space defined by a ICC color profile (International Color Consortium).
 * <p>
 * XXX - This does not work. I think this is because of 
 * Java bug #4760025 at
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4760025
 * but maybe I am doing something in the wrong way.
 * 
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class CMYKICCColorSystem extends ColorSpaceColorSystem {

    /**
     * Creates a new instance.
     */
    public CMYKICCColorSystem() {
        super("Generic CMYK Profile.icc");
    }
}
