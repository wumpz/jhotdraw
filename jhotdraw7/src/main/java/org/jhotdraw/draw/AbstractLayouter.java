/*
 * @(#)AbstractLayouter.java  2.0  2006-01-14
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

import org.jhotdraw.util.*;
import java.awt.*;
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
        Insets2D.Double value = (Insets2D.Double) child.getAttribute(CompositeFigure.LAYOUT_INSETS);
        return (value == null) ? new Insets2D.Double() : (Insets2D.Double) value.clone();
    }
}
