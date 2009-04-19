/*
 * @(#)AbstractLayouter.java  2.0  2006-01-14
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

import org.jhotdraw.geom.*;
/**
 * AbstractLayouter.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 1. Dezember 2003  Created.
 */
public abstract class AbstractLayouter implements Layouter {
    
    public Insets2D.Double getInsets(Figure child) {
        Insets2D.Double value =  CompositeFigure.LAYOUT_INSETS.get(child);
        return (value == null) ? new Insets2D.Double() : (Insets2D.Double) value.clone();
    }
}
