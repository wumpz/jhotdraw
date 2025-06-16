/*
 * @(#)TextOverflowHandle.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.TextHolderFigure;
import org.jhotdraw.utils.util.ResourceBundleUtil;

/**
 * The TextOverflowHandle indicates when the text does not fit into the bounds of a TextAreaFigure.
 *
 * @author Werner Randelshofer
 * @version $Id: TextOverflowHandle.java -1 $
 */
public class TextOverflowHandle extends AbstractHandle {

  public TextOverflowHandle(TextHolderFigure owner) {
    super(owner);
  }

  @Override
  public TextHolderFigure getOwner() {
    return (TextHolderFigure) super.getOwner();
  }

  @Override
  public boolean contains(Point p) {
    return false;
  }

  /** Draws this handle. */
  @Override
  public void draw(Graphics2D g) {
    if (getOwner().isTextOverflow()) {
      drawRectangle(
          g,
          getEditor().getHandleAttribute(HandleAttributeKeys.OVERFLOW_HANDLE_FILL_COLOR),
          getEditor().getHandleAttribute(HandleAttributeKeys.OVERFLOW_HANDLE_STROKE_COLOR));
      g.setColor(getEditor().getHandleAttribute(HandleAttributeKeys.OVERFLOW_HANDLE_STROKE_COLOR));
      Rectangle r = basicGetBounds();
      g.drawLine(r.x + 1, r.y + 1, r.x + r.width - 2, r.y + r.height - 2);
      g.drawLine(r.x + r.width - 2, r.y + 1, r.x + 1, r.y + r.height - 2);
    }
  }

  @Override
  protected Rectangle basicGetBounds() {
    Rectangle2D.Double b = getOwner().getBounds();
    Point2D.Double p = new Point2D.Double(b.x + b.width, b.y + b.height);
    Figure o = getOwner();
    if (o.attr().get(TRANSFORM) != null) {
      o.attr().get(TRANSFORM).transform(p, p);
    }
    Rectangle r = new Rectangle(view.drawingToView(p));
    int h = getHandlesize();
    r.x -= h;
    r.y -= h;
    r.width = r.height = h;
    return r;
  }

  @Override
  public String getToolTipText(Point p) {
    return (getOwner().isTextOverflow())
        ? ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels")
            .getString("handle.textOverflow.toolTipText")
        : null;
  }
}
