/*
 * @(#)FigureLayerComparator.java  1.0  1. April 2004
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

import java.util.*;
/**
 * FigureLayerComparator.
 *
 * @author  Werner Randelshofer
 * @version 1.0 1. April 2004  Created.
 */
public class FigureLayerComparator implements Comparator<Figure> {
    public final static FigureLayerComparator INSTANCE = new FigureLayerComparator();
    
    /** Creates a new instance. */
    private FigureLayerComparator() {
    }
    
    public int compare(Figure f1, Figure f2) {
        return f1.getLayer() - f2.getLayer();
    }
    
}
