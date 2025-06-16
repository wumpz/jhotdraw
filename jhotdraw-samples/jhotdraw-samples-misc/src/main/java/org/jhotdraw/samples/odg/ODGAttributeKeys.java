/*
 * @(#)ODGAttributeKeys.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.odg;

import java.awt.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.samples.odg.ODGConstants.FillStyle;
import org.jhotdraw.samples.odg.ODGConstants.StrokeStyle;
import org.jhotdraw.utils.util.ResourceBundleUtil;

/**
 * ODGAttributeKeys.
 *
 * <p>The descriptions of the attributes have been taken from the Open Document Specification
 * version 1.1. <a href="http://docs.oasis-open.org/office/v1.1/OS/OpenDocument-v1.1.pdf">
 * http://docs.oasis-open.org/office/v1.1/OS/OpenDocument-v1.1.pdf</a>
 */
public class ODGAttributeKeys extends AttributeKeys {

  private static final ResourceBundleUtil LABELS =
      ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");

  /** Prevent instance creation */
  private ODGAttributeKeys() {}

  /** The attribute draw:name assigns a name to the drawing shape. */
  public static final AttributeKey<String> NAME =
      new AttributeKey<String>("name", String.class, null, true, LABELS);

  /**
   * Specifies the overall opacity of a ODG figure. This is a value between 0 and 1 whereas 0 is
   * translucent and 1 is fully opaque.
   */
  public static final AttributeKey<Double> OPACITY =
      new AttributeKey<Double>("opacity", Double.class, 1d, false, LABELS);

  /**
   * Specifies the fill style of a ODG figure.
   *
   * <p>The attribute draw:fill specifies the fill style for a graphic object. Graphic objects that
   * are not closed, such as a path without a closepath at the end, will not be filled. The fill
   * operation does not automatically close all open subpaths by connecting the last point of the
   * subpath with the first point of the subpath before painting the fill. The attribute has the
   * following values: • none: the drawing object is not filled. • solid: the drawing object is
   * filled with color specified by the draw:fill-color attribute. • bitmap: the drawing object is
   * filled with the bitmap specified by the draw:fill-image- name attribute. • gradient: the
   * drawing object is filled with the gradient specified by the draw:fill- gradient-name attribute.
   * • hatch: the drawing object is filled with the hatch specified by the draw:fill-hatch-name
   * attribute.
   */
  public static final AttributeKey<FillStyle> FILL_STYLE =
      new AttributeKey<FillStyle>("fill", FillStyle.class, FillStyle.SOLID, false, LABELS);

  /** Specifies the fill gradient of a ODG figure. */
  public static final AttributeKey<Gradient> FILL_GRADIENT =
      new AttributeKey<Gradient>("fillGradient", Gradient.class, null, true, LABELS);

  /**
   * Specifies the fill opacity of a ODG figure. This is a value between 0 and 1 whereas 0 is
   * translucent and 1 is fully opaque.
   */
  public static final AttributeKey<Double> FILL_OPACITY =
      new AttributeKey<Double>("fillOpacity", Double.class, 1d, false, LABELS);

  /**
   * Specifies the stroke style of a ODG figure.
   *
   * <p>The attribute draw:stroke specifies the style of the stroke on the current object. The value
   * none means that no stroke is drawn, and the value solid means that a solid stroke is drawn. If
   * the value is dash, the stroke referenced by the draw:stroke-dash property is drawn.
   */
  public static final AttributeKey<StrokeStyle> STROKE_STYLE =
      new AttributeKey<StrokeStyle>("stroke", StrokeStyle.class, StrokeStyle.SOLID, false, LABELS);

  /** Specifies the stroke gradient of a ODG figure. */
  public static final AttributeKey<Gradient> STROKE_GRADIENT =
      new AttributeKey<Gradient>("strokeGradient", Gradient.class, null, true, LABELS);

  /**
   * Specifies the stroke opacity of a ODG figure. This is a value between 0 and 1 whereas 0 is
   * translucent and 1 is fully opaque.
   */
  public static final AttributeKey<Double> STROKE_OPACITY =
      new AttributeKey<Double>("strokeOpacity", Double.class, 1d, false, LABELS);

  /**
   * Gets the fill paint for the specified figure based on the attributes FILL_GRADIENT,
   * FILL_OPACITY, FILL_PAINT and the bounds of the figure. Returns null if the figure is not
   * filled.
   */
  public static Paint getFillPaint(Figure f) {
    double opacity = f.attr().get(FILL_OPACITY);
    if (f.attr().get(FILL_GRADIENT) != null) {
      return f.attr().get(FILL_GRADIENT).getPaint(f, opacity);
    }
    Color color = f.attr().get(FILL_COLOR);
    if (color != null) {
      if (opacity != 1) {
        color = new Color((color.getRGB() & 0xffffff) | (int) (opacity * 255) << 24, true);
      }
    }
    return color;
  }

  /**
   * Gets the stroke paint for the specified figure based on the attributes STROKE_GRADIENT,
   * STROKE_OPACITY, STROKE_PAINT and the bounds of the figure. Returns null if the figure is not
   * filled.
   */
  public static Paint getStrokePaint(Figure f) {
    double opacity = f.attr().get(STROKE_OPACITY);
    if (f.attr().get(STROKE_GRADIENT) != null) {
      return f.attr().get(STROKE_GRADIENT).getPaint(f, opacity);
    }
    Color color = f.attr().get(STROKE_COLOR);
    if (color != null) {
      if (opacity != 1) {
        color = new Color((color.getRGB() & 0xffffff) | (int) (opacity * 255) << 24, true);
      }
    }
    return color;
  }

  public static Stroke getStroke(Figure f) {
    double strokeWidth = f.attr().get(STROKE_WIDTH);
    if (strokeWidth == 0) {
      strokeWidth = 1;
    }
    return new BasicStroke((float) strokeWidth);
  }

  /** Sets ODG default values. */
  public static void setDefaults(Figure f) {
    // Fill properties
    f.attr().set(FILL_COLOR, Color.black);
    f.attr().set(WINDING_RULE, WindingRule.NON_ZERO);
    // Stroke properties
    f.attr().set(STROKE_COLOR, null);
    f.attr().set(STROKE_WIDTH, 1d);
    f.attr().set(STROKE_CAP, BasicStroke.CAP_BUTT);
    f.attr().set(STROKE_JOIN, BasicStroke.JOIN_MITER);
    f.attr().set(STROKE_MITER_LIMIT, 4d);
    f.attr().set(IS_STROKE_MITER_LIMIT_FACTOR, false);
    f.attr().set(STROKE_DASHES, null);
    f.attr().set(STROKE_DASH_PHASE, 0d);
    f.attr().set(IS_STROKE_DASH_FACTOR, false);
  }
}
