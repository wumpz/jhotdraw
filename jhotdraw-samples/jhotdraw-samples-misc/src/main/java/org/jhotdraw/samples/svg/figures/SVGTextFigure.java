/*
 * @(#)SVGText.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg.figures;

import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.FONT_SIZE;
import static org.jhotdraw.draw.AttributeKeys.FONT_UNDERLINE;
import static org.jhotdraw.draw.AttributeKeys.TEXT;
import static org.jhotdraw.draw.AttributeKeys.TRANSFORM;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.draw.figure.TextHolderFigure;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.FontSizeHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.MoveHandle;
import org.jhotdraw.draw.handle.TransformHandleKit;
import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.draw.tool.TextEditingTool;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.geom.Insets2D;
import org.jhotdraw.samples.svg.Gradient;
import org.jhotdraw.samples.svg.SVGAttributeKeys;

/**
 * SVGText.
 *
 * <p>XXX - At least on Mac OS X - Always draw text using TextLayout.getOutline(), because outline
 * layout does not match with TextLayout.draw() output. Cache outline to improve performance. <br>
 * 2.1 2007-05-13 Fixed transformation issues. <br>
 * 2.0 2007-04-14 Adapted for new AttributeKeys.TRANSFORM support. <br>
 * 1.0 July 8, 2006 Created.
 */
public class SVGTextFigure extends SVGAttributedFigure implements TextHolderFigure, SVGFigure {

  private static final long serialVersionUID = 1L;
  protected Point2D.Double[] coordinates = new Point2D.Double[] {new Point2D.Double()};
  protected double[] rotates = new double[] {0};
  private boolean editable = true;

  /** This is used to perform faster drawing and hit testing. */
  private transient Shape cachedTextShape;

  private transient Rectangle2D.Double cachedBounds;
  private transient Rectangle2D.Double cachedDrawingArea;

  public SVGTextFigure() {
    this("Text");
  }

  public SVGTextFigure(String text) {
    setText(text);
    SVGAttributeKeys.setDefaults(this);
    setConnectable(false);
  }

  // DRAWING
  @Override
  protected void drawText(java.awt.Graphics2D g) {}

  @Override
  protected void drawFill(Graphics2D g) {
    g.fill(getTextShape());
  }

  @Override
  protected void drawStroke(Graphics2D g) {
    g.draw(getTextShape());
  }

  // SHAPE AND BOUNDS
  public void setCoordinates(Point2D.Double[] coordinates) {
    this.coordinates = coordinates.clone();
    invalidate();
  }

  public Point2D.Double[] getCoordinates() {
    Point2D.Double[] c = new Point2D.Double[coordinates.length];
    for (int i = 0; i < c.length; i++) {
      c[i] = (Point2D.Double) coordinates[i].clone();
    }
    return c;
  }

  public void setRotates(double[] rotates) {
    this.rotates = rotates.clone();
    invalidate();
  }

  public double[] getRotates() {
    return rotates.clone();
  }

  @Override
  public Rectangle2D.Double getBounds(double scale) {
    if (cachedBounds == null) {
      cachedBounds = new Rectangle2D.Double();
      cachedBounds.setRect(getTextShape().getBounds2D());
      String text = getText();
      if (text == null || text.length() == 0) {
        text = " ";
      }
      FontRenderContext frc = getFontRenderContext();
      HashMap<TextAttribute, Object> textAttributes = new HashMap<TextAttribute, Object>();
      textAttributes.put(TextAttribute.FONT, getFont());
      if (attr().get(FONT_UNDERLINE)) {
        textAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
      }
      TextLayout textLayout = new TextLayout(text, textAttributes, frc);
      cachedBounds.setRect(
          coordinates[0].x,
          coordinates[0].y - textLayout.getAscent(),
          textLayout.getAdvance(),
          textLayout.getAscent());
      AffineTransform tx = new AffineTransform();
      tx.translate(coordinates[0].x, coordinates[0].y);
      switch (attr().get(TEXT_ANCHOR)) {
        case END:
          cachedBounds.x -= textLayout.getAdvance();
          break;
        case MIDDLE:
          cachedBounds.x -= textLayout.getAdvance() / 2d;
          break;
        case START:
          break;
      }
      tx.rotate(rotates[0]);
    }
    return (Rectangle2D.Double) cachedBounds.clone();
  }

  @Override
  public Rectangle2D.Double getDrawingArea(double scale) {
    if (cachedDrawingArea == null) {
      Rectangle2D rx = getTextShape().getBounds2D();
      Rectangle2D.Double r = (rx instanceof Rectangle2D.Double)
          ? (Rectangle2D.Double) rx
          : new Rectangle2D.Double(rx.getX(), rx.getY(), rx.getWidth(), rx.getHeight());
      double g = SVGAttributeKeys.getPerpendicularHitGrowth(this, 1.0) + 1;
      Geom.grow(r, g, g);
      if (attr().get(TRANSFORM) == null) {
        cachedDrawingArea = r;
      } else {
        cachedDrawingArea = new Rectangle2D.Double();
        cachedDrawingArea.setRect(
            attr().get(TRANSFORM).createTransformedShape(r).getBounds2D());
      }
    }
    return (Rectangle2D.Double) cachedDrawingArea.clone();
  }

  /** Checks if a Point2D.Double is inside the figure. */
  @Override
  public boolean contains(Point2D.Double p, double scaleDenominator) {
    if (attr().get(TRANSFORM) != null) {
      try {
        p = (Point2D.Double) attr().get(TRANSFORM).inverseTransform(p, new Point2D.Double());
      } catch (NoninvertibleTransformException ex) {
        ex.printStackTrace();
      }
    }
    return getTextShape().getBounds2D().contains(p);
  }

  private Shape getTextShape() {
    if (cachedTextShape == null) {
      String text = getText();
      if (text == null || text.length() == 0) {
        text = " ";
      }
      FontRenderContext frc = getFontRenderContext();
      HashMap<TextAttribute, Object> textAttributes = new HashMap<TextAttribute, Object>();
      textAttributes.put(TextAttribute.FONT, getFont());
      if (attr().get(FONT_UNDERLINE)) {
        textAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
      }
      TextLayout textLayout = new TextLayout(text, textAttributes, frc);
      AffineTransform tx = new AffineTransform();
      tx.translate(coordinates[0].x, coordinates[0].y);
      switch (attr().get(TEXT_ANCHOR)) {
        case END:
          tx.translate(-textLayout.getAdvance(), 0);
          break;
        case MIDDLE:
          tx.translate(-textLayout.getAdvance() / 2d, 0);
          break;
        case START:
          break;
      }
      tx.rotate(rotates[0]);
      /*
      if (get(TRANSFORM) != null) {
      tx.preConcatenate(get(TRANSFORM));
      }*/
      cachedTextShape = tx.createTransformedShape(textLayout.getOutline(tx));
      cachedTextShape = textLayout.getOutline(tx);
    }
    return cachedTextShape;
  }

  @Override
  public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
    coordinates = new Point2D.Double[] {new Point2D.Double(anchor.x, anchor.y)};
    rotates = new double[] {0d};
  }

  /**
   * Transforms the figure.
   *
   * @param tx the transformation.
   */
  @Override
  public void transform(AffineTransform tx) {
    if (attr().get(TRANSFORM) != null
        || tx.getType() != (tx.getType() & AffineTransform.TYPE_TRANSLATION)) {
      if (attr().get(TRANSFORM) == null) {
        attr().set(TRANSFORM, (AffineTransform) tx.clone());
      } else {
        AffineTransform t = TRANSFORM.getClone(this);
        t.preConcatenate(tx);
        attr().set(TRANSFORM, t);
      }
    } else {
      for (int i = 0; i < coordinates.length; i++) {
        tx.transform(coordinates[i], coordinates[i]);
      }
      if (attr().get(FILL_GRADIENT) != null
          && !attr().get(FILL_GRADIENT).isRelativeToFigureBounds()) {
        Gradient g = FILL_GRADIENT.getClone(this);
        g.transform(tx);
        attr().set(FILL_GRADIENT, g);
      }
      if (attr().get(STROKE_GRADIENT) != null
          && !attr().get(STROKE_GRADIENT).isRelativeToFigureBounds()) {
        Gradient g = STROKE_GRADIENT.getClone(this);
        g.transform(tx);
        attr().set(STROKE_GRADIENT, g);
      }
    }
    invalidate();
  }

  @Override
  public void restoreTransformTo(Object geometry) {
    Object[] restoreData = (Object[]) geometry;
    TRANSFORM.setClone(this, (AffineTransform) restoreData[0]);
    Point2D.Double[] restoredCoordinates = (Point2D.Double[]) restoreData[1];
    for (int i = 0; i < this.coordinates.length; i++) {
      coordinates[i] = (Point2D.Double) restoredCoordinates[i].clone();
    }
    FILL_GRADIENT.setClone(this, (Gradient) restoreData[2]);
    STROKE_GRADIENT.setClone(this, (Gradient) restoreData[3]);
    invalidate();
  }

  @Override
  public Object getTransformRestoreData() {
    Point2D.Double[] restoredCoordinates = this.coordinates.clone();
    for (int i = 0; i < this.coordinates.length; i++) {
      restoredCoordinates[i] = (Point2D.Double) this.coordinates[i].clone();
    }
    return new Object[] {
      TRANSFORM.getClone(this),
      restoredCoordinates,
      FILL_GRADIENT.getClone(this),
      STROKE_GRADIENT.getClone(this)
    };
  }

  // ATTRIBUTES
  /** Gets the text shown by the text figure. */
  @Override
  public String getText() {
    return attr().get(TEXT);
  }

  /** Sets the text shown by the text figure. */
  @Override
  public void setText(String newText) {
    attr().set(TEXT, newText);
  }

  @Override
  public boolean isEditable() {
    return editable;
  }

  public void setEditable(boolean b) {
    this.editable = b;
  }

  @Override
  public int getTextColumns() {
    // return (getText() == null) ? 4 : Math.min(getText().length(), 4);
    return 4;
  }

  @Override
  public Font getFont() {
    return SVGAttributeKeys.getFont(this);
  }

  @Override
  public Color getTextColor() {
    return attr().get(FILL_COLOR);
    //   return get(TEXT_COLOR);
  }

  @Override
  public Color getFillColor() {
    return attr().get(FILL_COLOR) == null || attr().get(FILL_COLOR).equals(Color.white)
        ? Color.black
        : Color.WHITE;
    //  return get(FILL_COLOR);
  }

  @Override
  public void setFontSize(float size) {
    // put(FONT_SIZE,  new Double(size));
    Point2D.Double p = new Point2D.Double(0, size);
    AffineTransform tx = attr().get(TRANSFORM);
    if (tx != null) {
      try {
        tx.inverseTransform(p, p);
        Point2D.Double p0 = new Point2D.Double(0, 0);
        tx.inverseTransform(p0, p0);
        p.y -= p0.y;
      } catch (NoninvertibleTransformException ex) {
        ex.printStackTrace();
      }
    }
    attr().set(FONT_SIZE, Math.abs(p.y));
  }

  @Override
  public float getFontSize() {
    //   return get(FONT_SIZE).floatValue();
    Point2D.Double p = new Point2D.Double(0, attr().get(FONT_SIZE));
    AffineTransform tx = attr().get(TRANSFORM);
    if (tx != null) {
      tx.transform(p, p);
      Point2D.Double p0 = new Point2D.Double(0, 0);
      tx.transform(p0, p0);
      p.y -= p0.y;
      /*
      try {
      tx.inverseTransform(p, p);
      } catch (NoninvertibleTransformException ex) {
      ex.printStackTrace();
      }*/
    }
    return (float) Math.abs(p.y);
  }

  // EDITING
  // CONNECTING
  @Override
  public void invalidate() {
    super.invalidate();
    cachedTextShape = null;
    cachedBounds = null;
    cachedDrawingArea = null;
  }

  @Override
  public Dimension2DDouble getPreferredSize(double scale) {
    Rectangle2D.Double b = getBounds(scale);
    return new Dimension2DDouble(b.width, b.height);
  }

  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    LinkedList<Handle> handles = new LinkedList<Handle>();
    switch (detailLevel % 2) {
      case -1: // Mouse hover handles
        handles.add(new BoundsOutlineHandle(this, false, true));
        break;
      case 0:
        handles.add(new BoundsOutlineHandle(this));
        handles.add(new MoveHandle(this, RelativeLocator.northWest()));
        handles.add(new MoveHandle(this, RelativeLocator.northEast()));
        handles.add(new MoveHandle(this, RelativeLocator.southWest()));
        handles.add(new MoveHandle(this, RelativeLocator.southEast()));
        handles.add(new FontSizeHandle(this));
        handles.add(new LinkHandle(this));
        break;
      case 1:
        TransformHandleKit.addTransformHandles(this, handles);
        break;
    }
    return handles;
  }

  // EDITING
  /**
   * Returns a specialized tool for the given coordinate.
   *
   * <p>Returns null, if no specialized tool is available.
   */
  @Override
  public Tool getTool(Point2D.Double p) {
    if (isEditable() && contains(p)) {
      TextEditingTool tool = new TextEditingTool(this);
      return tool;
    }
    return null;
  }

  @Override
  public double getBaseline() {
    return coordinates[0].y - getBounds().y;
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
  public SVGTextFigure clone() {
    SVGTextFigure that = (SVGTextFigure) super.clone();
    that.coordinates = new Point2D.Double[this.coordinates.length];
    for (int i = 0; i < this.coordinates.length; i++) {
      that.coordinates[i] = (Point2D.Double) this.coordinates[i].clone();
    }
    that.rotates = this.rotates.clone();
    that.cachedBounds = null;
    that.cachedDrawingArea = null;
    that.cachedTextShape = null;
    return that;
  }

  @Override
  public boolean isEmpty() {
    return getText() == null || getText().length() == 0;
  }

  @Override
  public boolean isTextOverflow() {
    return false;
  }
}
