/*
 * @(#)TextFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.FontSizeHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.MoveHandle;
import org.jhotdraw.draw.handle.RotateHandle;
import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.draw.tool.TextEditingTool;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.geom.Insets2D;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * A {@code TextHolderFigure} which holds a single line of text.
 *
 * <p>A DrawingEditor should provide the {@link org.jhotdraw.draw.tool.TextCreationTool} to create a
 * {@code TextFigure}.
 */
public class TextFigure extends AbstractAttributedDecoratedFigure
    implements TextHolderFigure, Origin, Rotation {

  private static final long serialVersionUID = 1L;
  protected Point2D.Double origin = new Point2D.Double();

  public static final Point2D.Double HOIZONTAL_DIRECTION = new Point2D.Double(1, 0);

  // always starting from 0,0
  protected Point2D.Double direction = new Point2D.Double(1, 0);

  protected boolean editable = true;
  // cache of the TextFigure's layout
  protected transient TextLayout textLayout;

  protected double alignX;
  protected double alignY;

  public TextFigure() {
    this(ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels")
        .getString("TextFigure.defaultText"));
  }

  public TextFigure(String text) {
    setText(text);
  }

  // DRAWING
  @Override
  protected void drawStroke(java.awt.Graphics2D g) {}

  @Override
  protected void drawFill(java.awt.Graphics2D g) {}

  @Override
  protected void drawText(java.awt.Graphics2D g) {
    if (getText() != null || isEditable()) {
      TextLayout layout = getTextLayout(
          AttributeKeys.getGlobalValueFactor(this, AttributeKeys.getScaleFactorFromGraphics(g)));
      Graphics2D g2 = (Graphics2D) g.create();
      try {
        double alignDeltaX = layout.getAdvance() * attr().get(AttributeKeys.ALIGN_RELATIVE_X);
        double alignDeltaY =
            (layout.getAscent() + layout.getDescent()) * attr().get(AttributeKeys.ALIGN_RELATIVE_Y);

        // g2.draw(getBounds(AttributeKeys.getScaleFactorFromGraphics(g)));

        // Test if world to screen transformation mirrors the text. If so it tries to
        // unmirror it.
        if (g2.getTransform().getScaleY() * g2.getTransform().getScaleX() < 0) {
          AffineTransform at = new AffineTransform();
          at.translate(0, origin.y + layout.getAscent() / 2);
          at.scale(1, -1);
          at.translate(0, -origin.y - layout.getAscent() / 2);
          at.rotate(direction.x, -direction.y, origin.x, origin.y + layout.getAscent());
          g2.transform(at);
        } else {
          g2.transform(rotationMatrix());
        }

        // to avoid float imprecisions
        AffineTransform at2 = new AffineTransform();
        at2.translate(origin.x, origin.y);
        g2.transform(at2);

        layout.draw(g2, (float) (-alignDeltaX), (float) (+alignDeltaY + layout.getAscent()));
      } finally {
        g2.dispose();
      }
    }
  }

  // SHAPE AND BOUNDS
  @Override
  public void transform(AffineTransform tx) {
    Point2D.Double dirVector = new Point2D.Double(origin.x + direction.x, origin.y + direction.y);
    tx.transform(origin, origin);
    tx.transform(dirVector, dirVector);
    direction.x = dirVector.x - origin.x;
    direction.y = dirVector.y - origin.y;
  }

  @Override
  public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
    origin = new Point2D.Double(anchor.x, anchor.y);
  }

  @Override
  public void setRotation(double angle) {
    AffineTransform.getRotateInstance(angle).transform(HOIZONTAL_DIRECTION, direction);
  }

  @Override
  public Point2D.Double getOrigin() {
    return origin;
  }

  @Override
  public void setOrigin(Point2D.Double origin) {
    this.origin = origin;
  }

  public Point2D.Double getDirection() {
    return direction;
  }

  public void setDirection(Point2D.Double direction) {
    this.direction = direction;
  }

  @Override
  public boolean figureContains(Point2D.Double p, double scale) {
    double grow = AttributeKeys.getPerpendicularHitGrowth(
            this, AttributeKeys.getGlobalValueFactor(this, scale))
        + 1d;
    Rectangle2D.Double r = getBounds(scale);
    Geom.grow(r, grow, grow);
    return r.contains(p);
  }

  protected TextLayout getTextLayout(double sizeFactor) {
    if (textLayout == null || attr().get(IS_STROKE_PIXEL_VALUE)) {
      String text = getText();
      if (text == null || text.length() == 0) {
        text = " ";
      }
      FontRenderContext frc = getFontRenderContext();
      HashMap<TextAttribute, Object> textAttributes = new HashMap<>();
      textAttributes.put(
          TextAttribute.FONT,
          getFont()
              .deriveFont(
                  getFontSize() / (float) AttributeKeys.getGlobalValueFactor(this, sizeFactor)));
      if (attr().get(FONT_UNDERLINE)) {
        textAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
      }
      textLayout = new TextLayout(text, textAttributes, frc);
    }
    return textLayout;
  }

  @Override
  public Rectangle2D.Double getBounds(double scale) {
    TextLayout layout = getTextLayout(scale);

    double alignDeltaX = layout.getAdvance() * attr().get(AttributeKeys.ALIGN_RELATIVE_X);
    double alignDeltaY =
        (layout.getAscent() + layout.getDescent()) * attr().get(AttributeKeys.ALIGN_RELATIVE_Y);

    Rectangle2D.Double r = new Rectangle2D.Double(
        origin.x - alignDeltaX,
        origin.y - alignDeltaY,
        layout.getAdvance(),
        layout.getAscent() + layout.getDescent());

    r = (Rectangle2D.Double) rotationMatrix().createTransformedShape(r).getBounds2D();

    return r;
  }

  @Override
  public Dimension2DDouble getPreferredSize(double scale) {
    Rectangle2D.Double b = getBounds(scale);
    return new Dimension2DDouble(b.width, b.height);
  }

  @Override
  public double getBaseline() {
    TextLayout layout = getTextLayout(AttributeKeys.scaleFromContext(this));
    return origin.y + layout.getAscent() - getBounds().y;
  }

  private AffineTransform rotationMatrix() {
    return AffineTransform.getRotateInstance(direction.x, direction.y, origin.x, origin.y);
  }

  /** Gets the drawing area without taking the decorator into account. */
  @Override
  protected Rectangle2D.Double getFigureDrawingArea(double factor) {
    if (getText() == null) {
      return getBounds(factor);
    } else {
      TextLayout layout = getTextLayout(factor);

      double alignDeltaX = layout.getAdvance() * attr().get(AttributeKeys.ALIGN_RELATIVE_X);
      double alignDeltaY =
          (layout.getAscent() + layout.getDescent()) * attr().get(AttributeKeys.ALIGN_RELATIVE_Y);

      Rectangle2D.Double r = new Rectangle2D.Double(
          origin.x - alignDeltaX,
          origin.y - alignDeltaY,
          layout.getAdvance(),
          layout.getAscent() + layout.getDescent());
      Rectangle2D lBounds = layout.getBounds();
      if (!lBounds.isEmpty() && !Double.isNaN(lBounds.getX())) {
        r.add(new Rectangle2D.Double(
            lBounds.getX() + origin.x - alignDeltaX,
            (lBounds.getY() + origin.y - alignDeltaY + layout.getAscent()),
            lBounds.getWidth(),
            lBounds.getHeight()));
      }

      r = (Rectangle2D.Double) rotationMatrix().createTransformedShape(r).getBounds2D();

      // grow by two pixels to take antialiasing into account
      Geom.grow(r, 2d, 2d);

      return r;
    }
  }

  @Override
  public void restoreTransformTo(Object geometry) {
    Point2D.Double p = (Point2D.Double) geometry;
    origin.x = p.x;
    origin.y = p.y;
  }

  @Override
  public Object getTransformRestoreData() {
    return origin.clone();
  }

  // ATTRIBUTES
  /** Gets the text shown by the text figure. */
  @Override
  public String getText() {
    return attr().get(TEXT);
  }

  /**
   * Sets the text shown by the text figure. This is a convenience method for calling {@code
   * set(TEXT,newText)}.
   */
  @Override
  public void setText(String newText) {
    attr().set(TEXT, newText);
  }

  @Override
  public int getTextColumns() {
    // return (getText() == null) ? 4 : Math.max(getText().length(), 4);
    return 4;
  }

  /** Gets the number of characters used to expand tabs. */
  @Override
  public int getTabSize() {
    return 8;
  }

  @Override
  public TextHolderFigure getLabelFor() {
    return this;
  }

  @Override
  public Insets2D.Double getInsets() {
    return new Insets2D.Double();
  }

  @Override
  public Font getFont() {
    return AttributeKeys.getFont(this);
  }

  @Override
  public Color getTextColor() {
    return attr().get(TEXT_COLOR);
  }

  @Override
  public Color getFillColor() {
    return attr().get(FILL_COLOR);
  }

  @Override
  public void setFontSize(float size) {
    attr().set(FONT_SIZE, Double.valueOf(size));
  }

  @Override
  public float getFontSize() {
    return attr().get(FONT_SIZE).floatValue();
  }

  // EDITING
  @Override
  public boolean isEditable() {
    return editable;
  }

  public void setEditable(boolean b) {
    this.editable = b;
  }

  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    Collection<Handle> handles = new ArrayList<>();
    switch (detailLevel) {
      case -1:
        handles.add(new BoundsOutlineHandle(this, false, true));
        break;
      case 0:
        handles.add(new BoundsOutlineHandle(this));
        handles.add(new MoveHandle(this, RelativeLocator.northWest()));
        handles.add(new MoveHandle(this, RelativeLocator.northEast()));
        handles.add(new MoveHandle(this, RelativeLocator.southWest()));
        handles.add(new MoveHandle(this, RelativeLocator.southEast()));
        handles.add(new FontSizeHandle(this));
        break;
      case 1:
        handles.add(new BoundsOutlineHandle(this));
        handles.add(new RotateHandle(this) {
          @Override
          protected Point2D.Double getCenter() {
            return TextFigure.this.getOrigin();
          }
        });
        break;
    }
    return handles;
  }

  /**
   * Returns a specialized tool for the given coordinate.
   *
   * <p>Returns null, if no specialized tool is available.
   *
   * @param p
   * @return
   */
  @Override
  public Tool getTool(Point2D.Double p) {
    if (isEditable() && contains(p)) {
      TextEditingTool t = new TextEditingTool(this);
      return t;
    }
    return null;
  }

  // CONNECTING
  // COMPOSITE FIGURES
  // CLONING
  // EVENT HANDLING
  @Override
  public void invalidate() {
    super.invalidate();
    textLayout = null;
  }

  @Override
  protected void validate() {
    super.validate();
    textLayout = null;
  }

  @Override
  public TextFigure clone() {
    TextFigure that = (TextFigure) super.clone();
    that.origin = (Point2D.Double) this.origin.clone();
    that.direction = (Point2D.Double) this.direction.clone();
    that.textLayout = null;
    return that;
  }

  @Override
  public boolean isTextOverflow() {
    return false;
  }
}
