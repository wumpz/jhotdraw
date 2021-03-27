/*
 * @(#)AbstractNamedColorSpace.java
 *
 * Copyright (c) 2013 The authors and contributors of JHotDraw.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with the copyright holders. For details
 * see accompanying license terms.
 */
package org.jhotdraw.color;

import java.awt.color.ColorSpace;

/**
 * {@code AbstractNamedColorSpace}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractNamedColorSpace extends ColorSpace implements NamedColorSpace {

    private static final long serialVersionUID = 1L;

    /**
     * @param type color type
     * @param numcomponents number of components
     */
    protected AbstractNamedColorSpace(int type, int numcomponents) {
        super(type, numcomponents);
    }
    /**
     * @param color values array
     * @return array of converted CIEXYZ color value 
     */
    @Override
    public float[] fromCIEXYZ(float[] colorvalue) {
        return fromCIEXYZ(colorvalue, new float[getNumComponents()]);
    }

    @Override
    /**
     * @param color values array
     * @return an array of rgb color value
     */
    public final float[] toRGB(float[] colorvalue) {
        return toRGB(colorvalue, new float[3]);
    }

    @Override
    /**
     * @param array of rgb colors values 
     */
    public float[] fromRGB(float[] rgb) {
        return fromRGB(rgb, new float[getNumComponents()]);
    }

    @Override
    /**
     * @param color values array
     */
    public final float[] toCIEXYZ(float[] colorvalue) {
        return toCIEXYZ(colorvalue, new float[3]);
    }

    @Override
    /**
     * converts rgb color array to CIEXYZ color array 
     * @param xyz a float array of CIEXYZ
     * @param colorvalue color values array
     * @return CIEXYZ array
     */
    public float[] toCIEXYZ(float[] colorvalue, float[] xyz) {
        return ColorUtil.RGBtoCIEXYZ(toRGB(colorvalue, xyz), xyz);
    }

    @Override
    /**
     * converts CIEXYZ color array to rgb color array 
     * @param xyz xyz array 
     * @param colorvalue color values array
     * @return rgb array 
     */
    public float[] fromCIEXYZ(float[] xyz, float[] colorvalue) {
        return fromRGB(ColorUtil.CIEXYZtoRGB(xyz, colorvalue), colorvalue);
    }
}
