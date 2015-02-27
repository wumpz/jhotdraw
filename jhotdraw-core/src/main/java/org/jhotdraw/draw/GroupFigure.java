/*
 * @(#)GroupFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.draw;

import org.jhotdraw.geom.Geom;
import java.awt.geom.*;

/**
 * A {@link org.jhotdraw.draw.Figure} which groups a collection of figures.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class GroupFigure extends AbstractCompositeFigure {
    private static final long serialVersionUID = 1L;
    /** Creates a new instance. */
    public GroupFigure() {
        setConnectable(false);
    }
    
    /**
     * This is a default implementation that chops the point at the rectangle
     * returned by getBounds() of the figure.
     * <p>
     * Figures which have a non-rectangular shape need to override this method.
     * <p>
     * FIXME Invoke chop on each child and return the closest point.
     */
    public Point2D.Double chop(Point2D.Double from) {
        Rectangle2D.Double r = getBounds();
        return Geom.angleToPoint(r, Geom.pointToAngle(r, from));
    }

    /** Returns true if all children of the group are transformable. */
    @Override
    public boolean isTransformable() {
        for (Figure f : children) {
            if (! f.isTransformable()) {
                return false;
            }
        }
        return true;
    }


}
