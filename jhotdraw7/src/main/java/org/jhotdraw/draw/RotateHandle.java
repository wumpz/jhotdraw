/*
 * @(#)RotateHandle.java  3.0  2007-12-22
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

package org.jhotdraw.draw;

import org.jhotdraw.util.*;
import org.jhotdraw.undo.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.geom.*;
import static org.jhotdraw.draw.AttributeKeys.*;
/**
 * A Handle to rotate a Figure.
 *
 * @author Werner Randelshofer.
 * @version 3.0 2007-12-22 Huw Jones: Changed base class from AbstractHandle to
 * AbstractRotateHandle. 
 * <br>2.0 2007-04-14 Werner Randelshofer: Added support for AttributeKeys.TRANSFORM.
 * <br>1.0 12. July 2006 Werner Randelshofer: Created.
 */
public class RotateHandle extends AbstractRotateHandle {
	
    /** Creates a new instance. */
    public RotateHandle(Figure owner) {
        super(owner);
    }
    
    protected Point2D.Double getCenter() {
        Rectangle2D.Double bounds = getTransformedBounds();
    	return new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
    }

    protected Point2D.Double getOrigin() {
        // This handle is placed above the figure.
        // We move it up by a handlesizes, so that it won't overlap with
        // the handles from TransformHandleKit.
        Rectangle2D.Double bounds = getTransformedBounds();
        Point2D.Double origin = new Point2D.Double(bounds.getCenterX(),
                bounds.y - getHandlesize() / view.getScaleFactor());
        return origin;
    }
}
