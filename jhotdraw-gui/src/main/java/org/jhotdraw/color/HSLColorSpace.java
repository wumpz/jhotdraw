/*
 * @(#)HSLColorSpace.java
 *
 * Copyright (c) 2010 The authors and contributors of JHotDraw.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.color;

import java.awt.color.ColorSpace;
import static java.lang.Math.*;

/**
 * A HSL color space with additive complements in the hue color wheel: red is
 * opposite cyan, magenta is opposite green, blue is opposite yellow.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class HSLColorSpace extends AbstractNamedColorSpace {

    private static final long serialVersionUID = 1L;
    private static HSLColorSpace instance;

    public static HSLColorSpace getInstance() {
        if (instance == null) {
            instance = new HSLColorSpace();
        }
        return instance;
    }

    public HSLColorSpace() {
        super(ColorSpace.TYPE_HSV, 3);
    }

    private float normalizeRgbVals(float com) {
    	return (com<0)? com+1f:com-1f;
    }
    
    private float adjustRgbVals(float col, float p, float q) {
    	if (col < 1f / 6f) {
            return p + ((q - p) * 6 * col);
        } else if (col < 0.5f) {
            return q;
        } else if (col < 2f / 3f) {
            return  p + ((q - p) * 6 * (2f / 3f - col));
        } else {
            return  p;
        }
    }
    @Override
    public float[] toRGB(float[] components, float[] rgb) {
        float hue = components[0];
        float saturation = components[1];
        float lightness = components[2];
        // compute p and q from saturation and lightness
        float q;
        if (lightness < 0.5f) {
            q = lightness * (1f + saturation);
        } else {
            q = lightness + saturation - (lightness * saturation);
        }
        float p = 2f * lightness - q;
        // normalize hue to -1..+1
        float hk = hue - (float) Math.floor(hue); // / 360f;
        // compute red, green and blue
        float red = hk + 1f / 3f;
        float green = hk;
        float blue = hk - 1f / 3f;
        // normalize rgb values
        red = normalizeRgbVals(red);
        green=normalizeRgbVals(green);
        blue=normalizeRgbVals(blue);
        // adjust rgb values
        red =adjustRgbVals(red,p,q);
        green =adjustRgbVals(green,p,q);
        blue =adjustRgbVals(blue,p,q);
        rgb[0] = clamp(red, 0, 1);
        rgb[1] = clamp(green, 0, 1);
        rgb[2] = clamp(blue, 0, 1);
        return rgb;
    }

    @Override
    public float[] fromRGB(float[] rgbvalue, float[] component) {
        float r = rgbvalue[0];
        float g = rgbvalue[1];
        float b = rgbvalue[2];
        float max = Math.max(Math.max(r, g), b);
        float min = Math.min(Math.min(r, g), b);
        float hue;
        float saturation;
        float luminance;
        if (max == min) {
            hue = 0;
        } else if (max == r && g >= b) {
            hue = 60f * (g - b) / (max - min);
        } else if (max == r && g < b) {
            hue = 60f * (g - b) / (max - min) + 360f;
        } else if (max == g) {
            hue = 60f * (b - r) / (max - min) + 120f;
        } else /*if (max == b)*/ {
            hue = 60f * (r - g) / (max - min) + 240f;
        }
        luminance = (max + min) / 2f;
        if (max == min) {
            saturation = 0;
        } else if (luminance <= 0.5f) {
            saturation = (max - min) / (max + min);
        } else /* if (lightness  > 0.5f)*/ {
            saturation = (max - min) / (2 - (max + min));
        }
        component[0] = hue / 360f;
        component[1] = saturation;
        component[2] = luminance;
        return component;
    }

    @Override
    public String getName(int idx) {
        switch (idx) {
            case 0:
                return "Hue";
            case 1:
                return "Saturation";
            case 2:
                return "Lightness";
            default:
                throw new IllegalArgumentException("index must be between 0 and 2:" + idx);
        }
    }

    @Override
    public float getMaxValue(int component) {
        return 1f;
    }

    @Override
    public float getMinValue(int component) {
        return 0f;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof HSLColorSpace);
    }

    @Override
    public int hashCode() {
        return getClass().getSimpleName().hashCode();
    }

    @Override
    public String getName() {
        return "HSL";
    }

    private static float clamp(float v, float minv, float maxv) {
        return max(minv, min(v, maxv));
    }
}
