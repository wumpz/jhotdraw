/*
 * @(#)AbstractAttributedFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.geom.Geom;

/**
 * This abstract class can be extended to implement a {@link Figure} which has its own attribute
 * set.
 *
 * @author Werner Randelshofer
 * @version $Id: AbstractAttributedFigure.java 778 2012-04-13 15:37:19Z rawcoder $
 */
public abstract class AbstractAttributedFigure extends AbstractFigure {

  private static final long serialVersionUID = 1L;

  private Attributes attributes = new Attributes(this::fireAttributeChanged);

  public Attributes attr() {
    return attributes;
  }

  //  /** Holds the attributes of the figure. */
  //  private HashMap<AttributeKey<?>, Object> attributes = new HashMap<>();
  //  /**
  //   * Forbidden attributes can't be put by the put() operation. They can only be changed by
  // put().
  //   */
  //  private HashSet<AttributeKey<?>> forbiddenAttributes;
  //
  //  /** Creates a new instance. */
  //  public AbstractAttributedFigure() {}
  //
  //  public void setAttributeEnabled(AttributeKey<?> key, boolean b) {
  //    if (forbiddenAttributes == null) {
  //      forbiddenAttributes = new HashSet<>();
  //    }
  //    if (b) {
  //      forbiddenAttributes.remove(key);
  //    } else {
  //      forbiddenAttributes.add(key);
  //    }
  //  }
  //
  //  public boolean isAttributeEnabled(AttributeKey<?> key) {
  //    return forbiddenAttributes == null || !forbiddenAttributes.contains(key);
  //  }
  //
  //  @SuppressWarnings("unchecked")
  //  public void setAttributes(Map<AttributeKey<?>, Object> map) {
  //    for (Map.Entry<AttributeKey<?>, Object> entry : map.entrySet()) {
  //      set((AttributeKey<Object>) entry.getKey(), entry.getValue());
  //    }
  //  }
  //
  //  @Override
  //  public Map<AttributeKey<?>, Object> getAttributes() {
  //    return (Map<AttributeKey<?>, Object>) new HashMap<>(attributes);
  //  }
  //
  //  @Override
  //  public Object getAttributesRestoreData() {
  //    return getAttributes();
  //  }
  //
  //  @Override
  //  public void restoreAttributesTo(Object restoreData) {
  //    attributes.clear();
  //    @SuppressWarnings("unchecked")
  //    HashMap<AttributeKey<?>, Object> restoreDataHashMap =
  //        (HashMap<AttributeKey<?>, Object>) restoreData;
  //    setAttributes(restoreDataHashMap);
  //  }
  //
  //  /**
  //   * Sets an attribute of the figure. AttributeKey name and semantics are defined by the class
  //   * implementing the figure interface.
  //   */
  //  @Override
  //  public <T> void set(AttributeKey<T> key, T newValue) {
  //    if (forbiddenAttributes == null || !forbiddenAttributes.contains(key)) {
  //      @SuppressWarnings("unchecked")
  //      T oldValue = key.put(attributes, newValue);
  //      fireAttributeChanged(key, oldValue, newValue);
  //    }
  //  }
  //
  //  /** Gets an attribute from the figure. */
  //  @Override
  //  public <T> T get(AttributeKey<T> key) {
  //    return key.get(attributes);
  //  }

  @Override
  public void draw(Graphics2D g) {
    if (attr().get(FILL_COLOR) != null) {
      g.setColor(attr().get(FILL_COLOR));
      drawFill(g);
    }
    if (attr().get(STROKE_COLOR) != null && attr().get(STROKE_WIDTH) >= 0d) {
      g.setStroke(AttributeKeys.getStroke(this, AttributeKeys.getScaleFactorFromGraphics(g)));
      g.setColor(attr().get(STROKE_COLOR));
      drawStroke(g);
    }
    if (attr().get(TEXT_COLOR) != null) {
      if (attr().get(TEXT_SHADOW_COLOR) != null && attr().get(TEXT_SHADOW_OFFSET) != null) {
        Dimension2DDouble d = attr().get(TEXT_SHADOW_OFFSET);
        g.translate(d.width, d.height);
        g.setColor(attr().get(TEXT_SHADOW_COLOR));
        drawText(g);
        g.translate(-d.width, -d.height);
      }
      g.setColor(attr().get(TEXT_COLOR));
      drawText(g);
    }
  }

  public double getStrokeMiterLimitFactor() {
    Number value = (Number) attr().get(AttributeKeys.STROKE_MITER_LIMIT);
    return (value != null) ? value.doubleValue() : 10f;
  }

  @Override
  public Rectangle2D.Double getDrawingArea() {
    return getDrawingArea(1.0);
  }

  @Override
  public Rectangle2D.Double getDrawingArea(double factor) {
    double strokeTotalWidth = AttributeKeys.getStrokeTotalWidth(this, factor);
    double width = strokeTotalWidth / 2d;
    if (attr().get(STROKE_JOIN) == BasicStroke.JOIN_MITER) {
      width *= attr().get(STROKE_MITER_LIMIT);
    } else if (attr().get(STROKE_CAP) != BasicStroke.CAP_BUTT) {
      width += strokeTotalWidth * 2;
    }
    width++;
    Rectangle2D.Double r = getBounds();
    Geom.grow(r, width, width);
    return r;
  }

  /**
   * This method is called by method draw() to draw the fill area of the figure.
   * AbstractAttributedFigure configures the Graphics2D object with the FILL_COLOR attribute before
   * calling this method. If the FILL_COLOR attribute is null, this method is not called.
   */
  protected abstract void drawFill(java.awt.Graphics2D g);

  /**
   * This method is called by method draw() to draw the lines of the figure . AttributedFigure
   * configures the Graphics2D object with the STROKE_COLOR attribute before calling this method. If
   * the STROKE_COLOR attribute is null, this method is not called.
   */
  protected abstract void drawStroke(java.awt.Graphics2D g);

  /**
   * This method is called by method draw() to draw the text of the figure .
   * AbstractAttributedFigure configures the Graphics2D object with the TEXT_COLOR attribute before
   * calling this method. If the TEXT_COLOR attribute is null, this method is not called.
   */
  protected void drawText(java.awt.Graphics2D g) {}

  @Override
  public AbstractAttributedFigure clone() {
    AbstractAttributedFigure that = (AbstractAttributedFigure) super.clone();
    that.attributes = Attributes.from(attributes, that::fireAttributeChanged);
    return that;
  }

  //  protected AttributeKey<?> getAttributeKey(String name) {
  //    return AttributeKeys.SUPPORTED_ATTRIBUTES_MAP.attr().get(name);
  //  }
  //
  //  /** Applies all attributes of this figure to that figure. */
  //  @SuppressWarnings("unchecked")
  //  protected void applyAttributesTo(Figure that) {
  //    for (Map.Entry<AttributeKey<?>, Object> entry : attributes.entrySet()) {
  //      that.set((AttributeKey<Object>) entry.getKey(), entry.getValue());
  //    }
  //  }
  //
  //  public <T> void removeAttribute(AttributeKey<T> key) {
  //    if (hasAttribute(key)) {
  //      T oldValue = attr().get(key);
  //      attributes.remove(key);
  //      fireAttributeChanged(key, oldValue, key.getDefaultValue());
  //    }
  //  }
  //
  //  public boolean hasAttribute(AttributeKey<?> key) {
  //    return attributes.containsKey(key);
  //  }
}
