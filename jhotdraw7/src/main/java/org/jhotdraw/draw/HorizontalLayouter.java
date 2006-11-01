/*
 * @(#)HorizontalLayouter.java  2.1  2006-07-08
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
import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.geom.*;
import static org.jhotdraw.draw.AttributeKeys.*;
/**
 * A layouter which lays out all children of a CompositeFigure in vertical
 * direction. The preferredSize of the figures is used to determine the layout.
 * This may cause some figures to resize.
 * <p>
 * The HorizontalLayouter honors the LAYOUT_INSETS AttributeKey when laying
 * out a Figure.
 * 
 * 
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 1. Dezember 2003  Created.
 */
public class HorizontalLayouter extends AbstractLayouter {
    public Rectangle2D.Double calculateLayout(CompositeFigure compositeFigure, Point2D.Double anchor, Point2D.Double lead) {
        Insets2DDouble layoutInsets = LAYOUT_INSETS.get(compositeFigure);
        
        Rectangle2D.Double layoutBounds = new Rectangle2D.Double(anchor.x, anchor.y, 0, 0);
        for (Figure child : compositeFigure.getChildren()) {
            if (child.isVisible()) {
                Dimension2DDouble preferredSize = child.getPreferredSize();
                Insets2DDouble ins = getInsets(child);
                layoutBounds.height = Math.max(layoutBounds.height, preferredSize.height + ins.top + ins.bottom);
                layoutBounds.width += preferredSize.width + ins.left + ins.right;
                }
        }
        layoutBounds.width += layoutInsets.left + layoutInsets.right;
        layoutBounds.height += layoutInsets.top + layoutInsets.bottom;
        
        return layoutBounds;
    }
    
    public Rectangle2D.Double layout(CompositeFigure compositeFigure, Point2D.Double anchor, Point2D.Double lead) {
        Insets2DDouble layoutInsets = LAYOUT_INSETS.get(compositeFigure);
        
        Rectangle2D.Double layoutBounds = calculateLayout(compositeFigure, anchor, lead);
        double x = layoutBounds.x + layoutInsets.left;
        for (Figure child : compositeFigure.getChildren()) {
            if (child.isVisible()) {
                Insets2DDouble insets = getInsets(child);
                double width = child.getPreferredSize().width;
                child.basicSetBounds(
                new Point2D.Double(
                x + insets.left,
                layoutBounds.y + layoutInsets.top + insets.top
                ),
                new Point2D.Double(
                x + insets.left + width,
                layoutBounds.y + layoutBounds.height - layoutInsets.bottom - insets.bottom
                )
                );
                x += width + insets.left + insets.right;
                }
        }
        
        return layoutBounds;
    }
}
