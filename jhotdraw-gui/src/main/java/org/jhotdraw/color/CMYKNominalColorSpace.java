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

    /**
     * computes red green blue values 
     * @param color RGG components
     * @return computed value 
     */
    private float reComputecColorsClampValue(float color) {
    	return Math.min(1f, Math.max(0f, color));
    }
    @Override
    /**
     * converts component's colors to rgb 
     * @param component component to handle
     * @param rgb array where rgb values will be stored
     * @return float array of rgb values 
     */
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
        // clamp value
        rgb[0] = reComputecColorsClampValue(red);
        rgb[1] =reComputecColorsClampValue(green);
        rgb[2] = reComputecColorsClampValue(blue);
        return rgb;
    }

    @Override
    /**
     * converts rgb color values  
     * @param rgbvalue array of rgb values 
     * @param colorvalue array where color values will be stored
     * @return float array of color values
     */
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
        colorvalue[0] = reComputecColorsClampValue(cyan);
        colorvalue[1] = reComputecColorsClampValue(yellow);
        colorvalue[2] = reComputecColorsClampValue(magenta);
        colorvalue[3] =reComputecColorsClampValue(black);
        return colorvalue;
    }
    @Override
    /**
     * @return max value of component 
     */
    public float getMaxValue(int component) {
        return 1f;
    }

    @Override
    /**
     * @return min value of component 
     */
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
    /**
     * @return name
     */
    public String getName() {
        return "nominal CMYK";
    }
}
