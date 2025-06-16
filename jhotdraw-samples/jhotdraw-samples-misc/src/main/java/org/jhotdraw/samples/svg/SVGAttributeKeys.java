/*
 * @(#)SVGAttributeKeys.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg;

import java.awt.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.utils.util.ResourceBundleUtil;

/** SVGAttributeKeys. */
public class SVGAttributeKeys extends AttributeKeys {

  private static final ResourceBundleUtil LABELS =
      ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");

  public enum TextAnchor {
    START,
    MIDDLE,
    END
  }

  /**
   * Specifies the title of an SVG drawing. This attribute can be null, to indicate that the drawing
   * has no title.
   */
  public static final AttributeKey<String> TITLE =
      new AttributeKey<String>("title", String.class, null, true, LABELS);

  /**
   * Specifies the description of an SVG drawing. This attribute can be null, to indicate that the
   * drawing has no description.
   */
  public static final AttributeKey<String> DESCRIPTION =
      new AttributeKey<String>("description", String.class, null, true, LABELS);

  /**
   * Specifies the viewport-fill of an SVG viewport. This attribute can be null, to indicate that
   * the viewport has no viewport-fill.
   */
  public static final AttributeKey<Color> VIEWPORT_FILL = CANVAS_FILL_COLOR;

  /** Specifies the viewport-fill-opacity of an SVG viewport. */
  public static final AttributeKey<Double> VIEWPORT_FILL_OPACITY = CANVAS_FILL_OPACITY;

  /** Specifies the width of an SVG viewport. */
  public static final AttributeKey<Double> VIEWPORT_WIDTH = CANVAS_WIDTH;

  /** Specifies the height of an SVG viewport. */
  public static final AttributeKey<Double> VIEWPORT_HEIGHT = CANVAS_HEIGHT;

  /** Specifies the text anchor of a SVGText figure. */
  public static final AttributeKey<TextAnchor> TEXT_ANCHOR =
      new AttributeKey<TextAnchor>("textAnchor", TextAnchor.class, TextAnchor.START, false, LABELS);

  public enum TextAlign {
    START,
    CENTER,
    END
  }

  /** Specifies the text alignment of a SVGText figure. */
  public static final AttributeKey<TextAlign> TEXT_ALIGN =
      new AttributeKey<TextAlign>("textAlign", TextAlign.class, TextAlign.START, false, LABELS);

  /** Specifies the fill gradient of a SVG figure. */
  public static final AttributeKey<Gradient> FILL_GRADIENT =
      new AttributeKey<Gradient>("fillGradient", Gradient.class, null, true, LABELS);

  /**
   * Specifies the fill opacity of a SVG figure. This is a value between 0 and 1 whereas 0 is
   * translucent and 1 is fully opaque.
   */
  public static final AttributeKey<Double> FILL_OPACITY =
      new AttributeKey<Double>("fillOpacity", Double.class, 1d, false, LABELS);

  /**
   * Specifies the overall opacity of a SVG figure. This is a value between 0 and 1 whereas 0 is
   * translucent and 1 is fully opaque.
   */
  public static final AttributeKey<Double> OPACITY =
      new AttributeKey<Double>("opacity", Double.class, 1d, false, LABELS);

  /** Specifies the stroke gradient of a SVG figure. */
  public static final AttributeKey<Gradient> STROKE_GRADIENT =
      new AttributeKey<Gradient>("strokeGradient", Gradient.class, null, true, LABELS);

  /**
   * Specifies the stroke opacity of a SVG figure. This is a value between 0 and 1 whereas 0 is
   * translucent and 1 is fully opaque.
   */
  public static final AttributeKey<Double> STROKE_OPACITY =
      new AttributeKey<Double>("strokeOpacity", Double.class, 1d, false, LABELS);

  /**
   * Specifies a link. In an SVG file, the link is stored in a "a" element which encloses the
   * figure. http://www.w3.org/TR/SVGMobile12/linking.html#AElement
   */
  public static final AttributeKey<String> LINK =
      new AttributeKey<String>("link", String.class, null, true, LABELS);

  /**
   * Specifies a link target. In an SVG file, the link is stored in a "a" element which encloses the
   * figure. http://www.w3.org/TR/SVGMobile12/linking.html#AElement
   */
  public static final AttributeKey<String> LINK_TARGET =
      new AttributeKey<String>("linkTarget", String.class, null, true, LABELS);

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

  /** Sets SVG default values. */
  public static void setDefaults(Figure f) {
    // Fill properties
    // http://www.w3.org/TR/SVGMobile12/painting.html#FillProperties
    f.attr().set(FILL_COLOR, Color.black);
    f.attr().set(WINDING_RULE, WindingRule.NON_ZERO);
    // Stroke properties
    // http://www.w3.org/TR/SVGMobile12/painting.html#StrokeProperties
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

  /**
   * Returns the distance, that a Rectangle needs to grow (or shrink) to make hit detections on a
   * shape as specified by the FILL_UNDER_STROKE and STROKE_POSITION attributes of a figure. The
   * value returned is the number of units that need to be grown (or shrunk) perpendicular to a
   * stroke on an outline of the shape.
   */
  public static double getPerpendicularHitGrowth(Figure f, double factor) {
    double grow;
    if (f.attr().get(STROKE_COLOR) == null && f.attr().get(STROKE_GRADIENT) == null) {
      grow = getPerpendicularFillGrowth(f, factor);
    } else {
      double strokeWidth = AttributeKeys.getStrokeTotalWidth(f, factor);
      grow = getPerpendicularDrawGrowth(f, factor) + strokeWidth / 2d;
    }
    return grow;
  }
}
