/*
 * @(#)Gradient.java  1.0  December 9, 2006
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.samples.svg;

import java.awt.*;
import java.awt.geom.AffineTransform;
import org.jhotdraw.draw.*;

/**
 * Represents an SVG Gradient.
 *
 * @author Werner Randelshofer
 * @version 1.0 December 9, 2006 Created.
 */
public interface Gradient extends Cloneable {
    public Paint getPaint(Figure f, double opacity);
    public boolean isRelativeToFigureBounds();
    public void transform(AffineTransform tx);
    public Object clone();
    public void makeRelativeToFigureBounds(Figure f);
}
