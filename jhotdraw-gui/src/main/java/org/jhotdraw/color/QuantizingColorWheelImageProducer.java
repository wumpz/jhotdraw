/*
 * @(#)ColorWheelImageProducer.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.color;

import java.awt.*;
import java.awt.color.ColorSpace;

/**
 * Produces the image of a ColorWheel.
 *
 * @see JColorWheel
 * @author Werner Randelshofer
 * @version $Id: ColorWheelImageProducer.java 628 2010-01-20 14:51:38Z rawcoder $
 */
public class QuantizingColorWheelImageProducer extends AbstractColorWheelImageProducer {

  /** Lookup table for angular component values. */
  protected float[] angulars;

  /** Lookup table for radial component values. */
  protected float[] radials;

  /** Lookup table for alphas. The alpha value is used for antialiasing the color wheel. */
  protected int[] alphas;

  protected int angularQuantization = 12;
  protected int radialQuantization = 5;

  public QuantizingColorWheelImageProducer(ColorSpace sys, int w, int h) {
    super(sys, w, h);
  }

  protected void generateLookupTables() {
    radials = new float[w * h];
    angulars = new float[w * h];
    alphas = new int[w * h];
    float radius = getRadius();
    // blend is used to create a linear alpha gradient of two extra pixels
    float blend = (radius + 2f) / radius - 1f;
    // Center of the color wheel circle
    int cx = w / 2;
    int cy = h / 2;
    float maxR = colorSpace.getMaxValue(radialIndex);
    float minR = colorSpace.getMinValue(radialIndex);
    float extentR = maxR - minR;
    float maxA = colorSpace.getMaxValue(angularIndex);
    float minA = colorSpace.getMinValue(angularIndex);
    float extentA = maxA - minA;
    for (int x = 0; x < w; x++) {
      int kx = x - cx; // Kartesian coordinates of x
      int squarekx = kx * kx; // Square of kartesian x
      for (int y = 0; y < h; y++) {
        int ky = cy - y; // Kartesian coordinates of y
        int index = x + y * w;
        float radiusRatio = (float) (Math.sqrt(squarekx + ky * ky) / radius);
        if (radiusRatio <= 1f) {
          alphas[index] = 0xff000000;
          radials[index] = radiusRatio * extentR + minR;
          radials[index] =
              (float) Math.round(radials[index] * radialQuantization) / (float) radialQuantization;
        } else {
          alphas[index] = (int) ((blend - Math.min(blend, radiusRatio - 1f)) * 255 / blend) << 24;
          radials[index] = maxR;
        }
        if (alphas[index] != 0) {
          angulars[index] = (float) (Math.atan2(ky, kx) / Math.PI / 2d) * extentA + minA;
          angulars[index] = (float) Math.round(angulars[index] * angularQuantization)
              / (float) angularQuantization;
        }
      }
    }
    isLookupValid = true;
  }

  @Override
  public boolean needsGeneration() {
    return !isPixelsValid;
  }

  @Override
  public void regenerateColorWheel() {
    if (!isPixelsValid) {
      generateColorWheel();
    }
  }

  @Override
  public void generateColorWheel() {
    if (!isLookupValid) {
      generateLookupTables();
    }
    float[] components = new float[colorSpace.getNumComponents()];
    float[] rgb = new float[3];
    for (int index = 0; index < pixels.length; index++) {
      if (alphas[index] != 0) {
        components[angularIndex] = angulars[index];
        components[radialIndex] = radials[index];
        components[verticalIndex] = verticalValue;
        pixels[index] = alphas[index] | 0xffffff & ColorUtil.CStoRGB24(colorSpace, components, rgb);
      }
    }
    newPixels();
    isPixelsValid = true;
  }

  @Override
  public Point getColorLocation(float[] components) {
    float radial = (components[radialIndex] - colorSpace.getMinValue(radialIndex))
        / (colorSpace.getMaxValue(radialIndex) - colorSpace.getMinValue(radialIndex));
    float angular = (components[angularIndex] - colorSpace.getMinValue(angularIndex))
        / (colorSpace.getMaxValue(angularIndex) - colorSpace.getMinValue(angularIndex));
    float radius = Math.min(w, h) / 2f;
    radial = Math.max(0f, Math.min(1f, radial));
    Point p = new Point(
        w / 2 + (int) (radius * radial * Math.cos(angular * Math.PI * 2d)),
        h / 2 - (int) (radius * radial * Math.sin(angular * Math.PI * 2d)));
    return p;
  }

  @Override
  public float[] getColorAt(int x, int y) {
    x -= w / 2;
    y -= h / 2;
    float r = (float) Math.sqrt(x * x + y * y);
    float theta = (float) Math.atan2(y, -x);
    float angular = (float) (0.5 + (theta / Math.PI / 2d));
    float radial = Math.min(1f, r / getRadius());
    float[] hsb = new float[3];
    hsb[angularIndex] =
        angular * (colorSpace.getMaxValue(angularIndex) - colorSpace.getMinValue(angularIndex))
            + colorSpace.getMinValue(angularIndex);
    hsb[radialIndex] =
        radial * (colorSpace.getMaxValue(radialIndex) - colorSpace.getMinValue(radialIndex))
            + colorSpace.getMinValue(radialIndex);
    hsb[verticalIndex] = verticalValue;
    return hsb;
  }
}
