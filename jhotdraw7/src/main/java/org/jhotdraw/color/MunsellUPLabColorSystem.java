/*
 * @(#)MunsellUPLabColorSystem.java
 *
 * Copyright (c) 20108 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.color;

import java.awt.color.*;
import java.io.*;

/**
 * A Munsell Lab color system with a uniform perceptual distribution of the
 * colors.
 * <p>
 * The three coordinates of CIELAB represent the lightness of the color
 * (L* = 0 yields black and L* = 100 indicates diffuse white; specular white
 * may be higher),
 * its position between red/magenta and green (a*, negative values indicate
 * green while positive values indicate magenta) and its position between
 * yellow and blue (b*, negative values indicate blue and positive values
 * indicate yellow).
 * <p>
 * In this color model all LAB values are normalized to lie between 0 and 1.
 * <p>
 * The ICC profile used by this color system has been taken from
 * <a href="http://www.brucelindbloom.com/index.html?MunsellCalcHelp.html">
 * http://www.brucelindbloom.com/index.html?MunsellCalcHelp.html
 * </a>
 * <p>
 * CIE Lab to Uniform Perceptual Lab profile is
 * copyright © 2003 Bruce Justin Lindbloom.
 * All rights reserved.
 * <a href="http://www.brucelindbloom.com">http://www.brucelindbloom.com</a>
 * 
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class MunsellUPLabColorSystem extends AbstractColorSystem {

    private ICC_ColorSpace colorSpace;

    /**
     * Creates a new instance.
     */
    public MunsellUPLabColorSystem() {
        try {
            read(getClass().getResourceAsStream("Munsell CIELab_to_UPLab2.icc"));
        } catch (IOException e) {
            InternalError err = new InternalError("Couldn't load \"Munsell CIELab_to_UPLab2.icc\".");
            err.initCause(e);
            throw err;
        }

    }

    /**
     * Creates a new instance.
     */
    public MunsellUPLabColorSystem(InputStream iccProfile) throws IOException {
        read(iccProfile);
    }

    public void read(InputStream iccProfile) throws IOException {
        this.colorSpace = new ICC_ColorSpace(ICC_Profile.getInstance(iccProfile));
    }

    @Override
    public float[] toComponents(int r, int g, int b, float[] component) {
        if (component == null || component.length != getComponentCount()) {
            component = new float[getComponentCount()];
        }
        // We abuse the component array to temporarily store the rgb values as floats
        component[0] = r / 255f;
        component[1] = g / 255f;
        component[2] = b / 255f;
        float[] lab = colorSpace.fromRGB(component);
        // Scale color components
        for (int i = 0; i < lab.length; i++) {
            component[i] = (lab[i] - colorSpace.getMinValue(i)) / (colorSpace.getMaxValue(i) - colorSpace.getMinValue(i));
        }

        return component;
    }

    @Override
    public int toRGB(float... component) {
        float[] lab = new float[getComponentCount()];
        // Scale color components
        for (int i = 0; i < lab.length; i++) {
            lab[i] = component[i] * (colorSpace.getMaxValue(i) - colorSpace.getMinValue(i)) + colorSpace.getMinValue(i);
        }

        float[] rgb = colorSpace.toRGB(lab);
        return 0xff000000 | ((int) (rgb[0] * 255f) << 16) | ((int) (rgb[1] * 255f) << 8) | (int) (rgb[2] * 255f);
    }

    @Override
    public int getComponentCount() {
        return colorSpace.getNumComponents();
    }

    public static void main(String[] args) {
        System.out.println(new MunsellUPLabColorSystem().getComponentCount());
    }
}
