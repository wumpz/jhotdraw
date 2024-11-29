/*
 * @(#)HorizontalLayouter.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.layouter;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.geom.*;
import org.jhotdraw.draw.AttributeKeys.Alignment;
import org.jhotdraw.draw.figure.CompositeFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.utils.geom.Dimension2DDouble;
import org.jhotdraw.utils.geom.Insets2D;

/**
 * A {@link Layouter} which lays out all children of a {@link CompositeFigure} in horizontal
 * direction.
 *
 * <p>The preferred size of the figures is used to determine the layout. This may cause some figures
 * to resize.
 *
 * <p>The HorizontalLayouter honors the LAYOUT_INSETS and the COMPOSITE_ALIGNMENT AttributeKey when
 * laying out a CompositeFigure.
 *
 * <p>If COMPOSITE_ALIGNMENT is not set on the composite figure, the layout assigns the same height
 * to all figures.
 */
public class HorizontalLayouter extends AbstractLayouter {

  @Override
  public Rectangle2D.Double calculateLayout(
      CompositeFigure compositeFigure, Point2D.Double anchor, Point2D.Double lead, double scale) {
    Insets2D.Double layoutInsets = compositeFigure.attr().get(LAYOUT_INSETS);
    Rectangle2D.Double layoutBounds = new Rectangle2D.Double(anchor.x, anchor.y, 0, 0);
    for (Figure child : compositeFigure.getChildren()) {
      if (child.isVisible()) {
        Dimension2DDouble preferredSize = child.getPreferredSize(scale);
        Insets2D.Double ins = getInsets(child);
        layoutBounds.height =
            Math.max(layoutBounds.height, preferredSize.height + ins.top + ins.bottom);
        layoutBounds.width += preferredSize.width + ins.left + ins.right;
      }
    }
    layoutBounds.width += layoutInsets.left + layoutInsets.right;
    layoutBounds.height += layoutInsets.top + layoutInsets.bottom;
    return layoutBounds;
  }

  @Override
  public Rectangle2D.Double layout(
      CompositeFigure compositeFigure, Point2D.Double anchor, Point2D.Double lead, double scale) {
    Insets2D.Double layoutInsets = compositeFigure.attr().get(LAYOUT_INSETS);
    Alignment compositeAlignment = compositeFigure.attr().get(COMPOSITE_ALIGNMENT);
    Rectangle2D.Double layoutBounds = calculateLayout(compositeFigure, anchor, lead, scale);
    double x = layoutBounds.x + layoutInsets.left;
    for (Figure child : compositeFigure.getChildren()) {
      if (child.isVisible()) {
        Insets2D.Double insets = getInsets(child);
        double width = child.getPreferredSize(scale).width;
        double height = child.getPreferredSize(scale).height;
        // --
        switch (compositeAlignment) {
          case LEADING:
            child.setBounds(
                new Point2D.Double(x + insets.left, layoutBounds.y + layoutInsets.top + insets.top),
                new Point2D.Double(
                    x + insets.left + width,
                    layoutBounds.y + layoutInsets.top + insets.top + height));
            break;
          case TRAILING:
            child.setBounds(
                new Point2D.Double(
                    x + insets.left,
                    layoutBounds.y
                        + layoutBounds.height
                        - layoutInsets.bottom
                        - insets.bottom
                        - height),
                new Point2D.Double(
                    x + insets.left + width,
                    layoutBounds.y + layoutBounds.height - layoutInsets.bottom - insets.bottom));
            break;
          case CENTER:
            child.setBounds(
                new Point2D.Double(
                    x + insets.left,
                    layoutBounds.y + layoutInsets.top + (layoutBounds.height - height) / 2d),
                new Point2D.Double(
                    x + insets.left + width,
                    layoutBounds.y + layoutInsets.top + (layoutBounds.height + height) / 2d));
            break;
          case BLOCK:
          default:
            child.setBounds(
                new Point2D.Double(x + insets.left, layoutBounds.y + layoutInsets.top + insets.top),
                new Point2D.Double(
                    x + insets.left + width,
                    layoutBounds.y + layoutBounds.height - layoutInsets.bottom - insets.bottom));
            break;
        }
        // ---
        x += width + insets.left + insets.right;
      }
    }
    return layoutBounds;
  }
}
