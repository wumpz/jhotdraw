/*
 * @(#)HSBColorSpace.java
 * 
 * Copyright (c) 2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.color;

import java.awt.Color;
import java.awt.color.ColorSpace;

/**
 * A HSB color space with additive complements in the hue color wheel: red is
 * opposite cyan, magenta is opposite green, blue is opposite yellow.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class HSBColorSpace extends AbstractNamedColorSpace {
    private static final long serialVersionUID = 1L;

    private static HSBColorSpace instance;

    public static HSBColorSpace getInstance() {
        if (instance == null) {
            instance = new HSBColorSpace();
        }
        return instance;
    }

    public HSBColorSpace() {
        super(ColorSpace.TYPE_HSV, 3);
    }

    @Override
    public float[] toRGB(float[] c, float[] component) {
        int rgb = Color.HSBtoRGB(c[0], c[1], c[2]);

        component[0] = ((rgb & 0xff0000) >> 16) / 255f;
        component[1] = ((rgb & 0xff00) >> 8) / 255f;
        component[2] = (rgb & 0xff) / 255f;
        return component;
    }

    @Override
    public float[] fromRGB(float[] rgb, float[] component) {
        Color.RGBtoHSB(//
                (int) (rgb[0] * 255),//
                (int) (rgb[1] * 255),//
                (int) (rgb[2] * 255),//
                component);
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
                return "Brightness";
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
        return (o instanceof HSBColorSpace);
    }

    @Override
    public int hashCode() {

        return getClass().getSimpleName().hashCode();
    }

    @Override
    public String getName() {
        return "HSB";
    }
}
