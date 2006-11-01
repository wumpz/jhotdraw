/*
 * @(#)VerticalLayouter.java  2.1  2006-07-08
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
 * The VerticalLayouter honors the LAYOUT_INSETS AttributeKey when laying
 * out a Figure.
 * 
 * 
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 1. Dezember 2003  Created.
 */
public class VerticalLayouter extends AbstractLayouter {
    
    public Rectangle2D.Double calculateLayout(CompositeFigure layoutable, Point2D.Double anchor, Point2D.Double lead) {
        Insets2DDouble layoutInsets = LAYOUT_INSETS.get(layoutable);
        if (layoutInsets == null) layoutInsets = new Insets2DDouble(0,0,0,0);
        
        Rectangle2D.Double layoutBounds = new Rectangle2D.Double(anchor.x,anchor.y,0,0);
        for (Figure child : layoutable.getChildren()) {
            if (child.isVisible()) {
                Dimension2DDouble preferredSize = child.getPreferredSize();
                Insets2DDouble ins = getInsets(child);
                layoutBounds.width = Math.max(layoutBounds.width, preferredSize.width + ins.left + ins.right);
                layoutBounds.height += preferredSize.height + ins.top + ins.bottom;
                }
        }
        layoutBounds.width += layoutInsets.left + layoutInsets.right;
        layoutBounds.height += layoutInsets.top + layoutInsets.bottom;
        
        return layoutBounds;
    }
    
    public Rectangle2D.Double layout(CompositeFigure layoutable, Point2D.Double anchor, Point2D.Double lead) {
        Insets2DDouble layoutInsets = LAYOUT_INSETS.get(layoutable);
        
        if (layoutInsets == null) layoutInsets = new Insets2DDouble(0,0,0,0);
        
        Rectangle2D.Double layoutBounds = calculateLayout(layoutable, anchor, lead);
        double y = layoutBounds.y + layoutInsets.top;
        for (Figure child : layoutable.getChildren()) {
            if (child.isVisible()) {
                Insets2DDouble insets = getInsets(child);
                double height = child.getPreferredSize().height;
                child.basicSetBounds(
                        new Point2D.Double(
                        layoutBounds.x + layoutInsets.left + insets.left,
                        y + insets.top
                        ),
                        new Point2D.Double(
                        layoutBounds.x + layoutBounds.width - layoutInsets.right - insets.right,
                        y + insets.top + height
                        )
                        );
                y += height + insets.top + insets.bottom;
                }
        }
        return layoutBounds;
    }
}
