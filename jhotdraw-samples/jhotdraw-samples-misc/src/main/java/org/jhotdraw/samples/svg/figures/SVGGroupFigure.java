/*
 * @(#)SVGGroupFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg.figures;

import static org.jhotdraw.draw.AttributeKeys.TRANSFORM;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.GroupFigure;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.TransformHandleKit;
import org.jhotdraw.samples.svg.SVGAttributeKeys;

/** SVGGroupFigure. */
public class SVGGroupFigure extends GroupFigure implements SVGFigure {

  private static final long serialVersionUID = 1L;
  private HashMap<AttributeKey<?>, Object> attributes = new HashMap<AttributeKey<?>, Object>();

  public SVGGroupFigure() {
    SVGAttributeKeys.setDefaults(this);
  }

  @Override
  public void draw(Graphics2D g) {
    double opacity = attr().get(OPACITY);
    opacity = Math.min(Math.max(0d, opacity), 1d);
    if (opacity != 0d) {
      if (opacity != 1d) {
        Rectangle2D.Double drawingArea = getDrawingArea();
        Rectangle2D clipBounds = g.getClipBounds();
        if (clipBounds != null) {
          Rectangle2D.intersect(drawingArea, clipBounds, drawingArea);
        }
        if (!drawingArea.isEmpty()) {
          BufferedImage buf = new BufferedImage(
              Math.max(1, (int) ((2 + drawingArea.width) * g.getTransform().getScaleX())),
              Math.max(1, (int) ((2 + drawingArea.height) * g.getTransform().getScaleY())),
              BufferedImage.TYPE_INT_ARGB);
          Graphics2D gr = buf.createGraphics();
          gr.scale(g.getTransform().getScaleX(), g.getTransform().getScaleY());
          gr.translate((int) -drawingArea.x, (int) -drawingArea.y);
          gr.setRenderingHints(g.getRenderingHints());
          super.draw(gr);
          gr.dispose();
          Composite savedComposite = g.getComposite();
          g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity));
          g.drawImage(
              buf,
              (int) drawingArea.x,
              (int) drawingArea.y,
              2 + (int) drawingArea.width,
              2 + (int) drawingArea.height,
              null);
          g.setComposite(savedComposite);
        }
      } else {
        super.draw(g);
      }
    }
  }

  @Override
  public Rectangle2D.Double getBounds(double scale) {
    if (cachedBounds == null) {
      if (getChildCount() == 0) {
        cachedBounds = new Rectangle2D.Double();
      } else {
        for (Figure f : children) {
          Rectangle2D.Double bounds = f.getBounds(scale);
          if (f.attr().get(TRANSFORM) != null) {
            bounds.setRect(
                f.attr().get(TRANSFORM).createTransformedShape(bounds).getBounds2D());
          }
          if (cachedBounds == null) {
            cachedBounds = bounds;
          } else {
            cachedBounds.add(bounds);
          }
        }
      }
    }
    return (Rectangle2D.Double) cachedBounds.clone();
  }

  @Override
  public LinkedList<Handle> createHandles(int detailLevel) {
    LinkedList<Handle> handles = new LinkedList<Handle>();
    switch (detailLevel) {
      case -1: // Mouse hover handles
        TransformHandleKit.addGroupHoverHandles(this, handles);
        break;
      case 0:
        TransformHandleKit.addGroupTransformHandles(this, handles);
        handles.add(new LinkHandle(this));
        break;
    }
    return handles;
  }

  @Override
  public boolean isEmpty() {
    return getChildCount() == 0;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append(getClass().getName().substring(getClass().getName().lastIndexOf('.') + 1));
    buf.append('@');
    buf.append(hashCode());
    if (getChildCount() > 0) {
      buf.append('(');
      for (Iterator<Figure> i = getChildren().iterator(); i.hasNext(); ) {
        Figure child = i.next();
        buf.append(child);
        if (i.hasNext()) {
          buf.append(',');
        }
      }
      buf.append(')');
    }
    return buf.toString();
  }

  @Override
  public SVGGroupFigure clone() {
    SVGGroupFigure that = (SVGGroupFigure) super.clone();
    that.attributes = new HashMap<AttributeKey<?>, Object>(this.attributes);
    return that;
  }
}
