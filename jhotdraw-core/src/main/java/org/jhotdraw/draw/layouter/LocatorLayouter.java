/*
 * @(#)LocatorLayouter.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.layouter;

import java.awt.geom.*;
import java.util.List;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.CompositeFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.Origin;
import org.jhotdraw.draw.figure.Rotation;
import org.jhotdraw.draw.locator.Locator;
import org.jhotdraw.draw.locator.Locator.Position;
import org.jhotdraw.geom.Dimension2DDouble;

/**
 * A layouter which lays out all children of a CompositeFigure according to their LayoutLocator
 * property..
 */
public class LocatorLayouter implements Layouter {

  /**
   * LayoutLocator property used by the children to specify their location relative to the
   * compositeFigure.
   */
  public static final AttributeKey<Locator> LAYOUT_LOCATOR =
      new AttributeKey<>("layoutLocator", Locator.class, null);

  public LocatorLayouter() {}

  @Override
  public Rectangle2D.Double calculateLayout(
      CompositeFigure compositeFigure, Point2D.Double anchor, Point2D.Double lead, double scale) {
    Rectangle2D.Double bounds = null;
    for (Figure child : extractFiguresToLayout(compositeFigure)) {
      Locator locator = getLocator(child);
      Rectangle2D.Double r;
      if (locator == null) {
        r = child.getBounds(1.0);
      } else {
        Position p = locator.locate(extractBaseFigure(compositeFigure), scale);
        Dimension2DDouble d = child.getPreferredSize(scale);
        r = new Rectangle2D.Double(p.location().x, p.location().y, d.width, d.height);
      }
      if (!r.isEmpty()) {
        if (bounds == null) {
          bounds = r;
        } else {
          bounds.add(r);
        }
      }
    }
    return (bounds == null) ? new Rectangle2D.Double() : bounds;
  }

  @Override
  public Rectangle2D.Double layout(
      CompositeFigure compositeFigure, Point2D.Double anchor, Point2D.Double lead, double scale) {
    Rectangle2D.Double bounds = null;
    for (Figure child : extractFiguresToLayout(compositeFigure)) {
      Locator locator = getLocator(child);
      Rectangle2D.Double r;
      Position position = null;
      if (locator == null) {
        r = child.getBounds(1.0);
      } else {
        position = locator.locate(extractBaseFigure(compositeFigure), child, scale);
        if (Double.isNaN(position.location().x)) {
          continue;
        }
        Dimension2DDouble d = child.getPreferredSize(scale);
        r = new Rectangle2D.Double(position.location().x, position.location().y, d.width, d.height);
      }
      child.willChange();

      if (position != null && child instanceof Origin originChild) {
        originChild.setOrigin(position.location());
      } else {
        child.setBounds(
            new Point2D.Double(r.getMinX(), r.getMinY()),
            new Point2D.Double(r.getMaxX(), r.getMaxY()));
      }
      if (position != null) {
        if (child instanceof Rotation rotateChild) {
          rotateChild.setRotation(position.angle());
        } else {
          ((Figure) child)
              .transform(
                  AffineTransform.getRotateInstance(
                      -position.angle(), position.location().x, position.location().y));
        }
      }

      child.changed();
      if (!r.isEmpty()) {
        if (bounds == null) {
          bounds = r;
        } else {
          bounds.add(r);
        }
      }
    }
    return (bounds == null) ? new Rectangle2D.Double() : bounds;
  }

  private Locator getLocator(Figure f) {
    return f.attr().get(LAYOUT_LOCATOR);
  }

  /**
   * Filters the Elements used as a base to layout subcomponents. Default element is the composite
   * figure itself. However one could use the first element of a composite figure to use as the main
   * element and layout the remaining children.
   *
   * @param compositeFigure
   * @return
   */
  public Figure extractBaseFigure(CompositeFigure compositeFigure) {
    return compositeFigure;
  }

  /**
   * Using extractBaseFigure one could mark one child as the main element and layout the remaining
   * elements.
   */
  public List<Figure> extractFiguresToLayout(CompositeFigure compositeFigure) {
    return compositeFigure.getChildren();
  }

  /** Layout main element is first child. Other childs are layouted. */
  public static class LocatorLayouterFirstFigure extends LocatorLayouter {

    @Override
    public List<Figure> extractFiguresToLayout(CompositeFigure compositeFigure) {
      List<Figure> figures = compositeFigure.getChildren();
      return figures.subList(1, figures.size());
    }

    @Override
    public Figure extractBaseFigure(CompositeFigure compositeFigure) {
      return compositeFigure.getChildren().get(0);
    }
  }
}
