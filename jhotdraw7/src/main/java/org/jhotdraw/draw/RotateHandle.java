/*
 * @(#)RotateHandle.java  3.0  2007-12-22
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
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
