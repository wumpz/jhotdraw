/*
 * @(#)HSBColorSpace.java
 * 
 * Copyright (c) 2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.color;

import java.awt.color.ColorSpace;

/**
 * A {@code ColorSpace} for CMYK color components (cyan, magenta, yellow, black) with
 * nominally converted color components from/to an RGB color model.
 * <p>
 * This model may not be very useful. It assumes that the color components
 * perfectly absorb the desired wavelenghts.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CMYKNominalColorSpace extends AbstractNamedColorSpace {
    private static final long serialVersionUID = 1L;

    private static CMYKNominalColorSpace instance;

    public static CMYKNominalColorSpace getInstance() {
        if (instance == null) {
            instance = new CMYKNominalColorSpace();
        }
        return instance;
    }

    public CMYKNominalColorSpace() {
        super(ColorSpace.TYPE_CMYK, 4);
    }

    @Override
    public float[] toRGB(float[] component, float[] rgb) {
        float cyan, magenta, yellow, black;

        cyan = component[0];
        magenta = component[1];
        yellow = component[2];
        black = component[3];

        float red, green, blue;
        red = 1f - cyan * (1f - black) - black;
        green = 1f - magenta * (1f - black) - black;
        blue = 1f - yellow * (1f - black) - black;

        // clamp values
        red = Math.min(1f, Math.max(0f, red));
        green = Math.min(1f, Math.max(0f, green));
        blue = Math.min(1f, Math.max(0f, blue));


        rgb[0]=red;rgb[1]= green;rgb[2]= blue;
        return rgb;
    }

    @Override
    public float[] fromRGB(float[] rgbvalue, float[] colorvalue) {
        float r = rgbvalue[0];
        float g = rgbvalue[1];
        float b = rgbvalue[2];

        float cyan, magenta, yellow, black;

        cyan = 1f - r;
        magenta = 1f - g;
        yellow = 1f - b;
        if (Math.min(Math.min(cyan, magenta), yellow) >= 1f) {
            cyan = magenta = yellow = 0f;
            black = 1f;
        } else {
            black = Math.min(Math.min(cyan, magenta), yellow);

            if (black > 0f) {
                cyan = (cyan - black) / (1 - black);
                magenta = (magenta - black) / (1 - black);
                yellow = (yellow - black) / (1 - black);
            }
        }

        // clamp values
        cyan = Math.min(1f, Math.max(0f, cyan));
        yellow = Math.min(1f, Math.max(0f, yellow));
        magenta = Math.min(1f, Math.max(0f, magenta));
        black = Math.min(1f, Math.max(0f, black));

        colorvalue[0]=cyan;colorvalue[1]= magenta;colorvalue[2]= yellow;colorvalue[3]= black;
        return colorvalue;
    }

    @Override
    public String getName(int idx) {
        switch (idx) {
            case 0:
                return "Cyan";
            case 1:
                return "Magenta";
            case 2:
                return "Yellow";
            case 3:
                return "Black";
            default:
                throw new IllegalArgumentException("index must be between 0 and 3:" + idx);
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
        return (o instanceof CMYKNominalColorSpace);
    }

    @Override
    public int hashCode() {
        return getClass().getSimpleName().hashCode();
    }

    @Override
    public String getName() {
        return "nominal CMYK";
    }
}
