/*
 * @(#)FigureLayerComparator.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.draw;

import java.util.*;
/**
 * A {@code Comparator} used to sort figures by their layer property.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class FigureLayerComparator implements Comparator<Figure> {
    public static final FigureLayerComparator INSTANCE = new FigureLayerComparator();
    
    /** Creates a new instance. */
    private FigureLayerComparator() {
    }
    
    @Override
    public int compare(Figure f1, Figure f2) {
        return f1.getLayer() - f2.getLayer();
    }
    
}
