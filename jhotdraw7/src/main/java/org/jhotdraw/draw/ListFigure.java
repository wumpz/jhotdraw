/*
 * @(#)ListFigure.java  1.1  2006-07-08
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

import java.awt.*;
import java.util.*;
import org.jhotdraw.geom.*;
import static org.jhotdraw.draw.AttributeKeys.*;
/**
 * A ListFigure consists of a list of Figures and a RectangleFigure.
 *
 * @author  Werner Randelshofer
 * @version 1.2 2006-07-08 Method setInsets() removed, because it is
 * redundant with the setter/getter in LAYOUT_INSETS.
 * <br>1.0 30. Januar 2004  Created.
 */
public class ListFigure
extends GraphicalCompositeFigure {
    /** Creates a new instance. */
    public ListFigure() {
        this(null);
    }
    public ListFigure(Figure presentationFigure) {
        super(presentationFigure); 
        setLayouter(new VerticalLayouter());
        LAYOUT_INSETS.basicSet(this, new Insets2D.Double(4,8,4,8));
    }
}
