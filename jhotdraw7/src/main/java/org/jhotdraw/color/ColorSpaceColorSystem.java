/*
 * @(#)ColorSpaceColorSystem.java
 * 
 * Copyright (c) 2010 by the original authors of JHotDraw
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

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.IOException;
import java.io.InputStream;

/**
 * A {@code ColorSystem} which uses an {@code ICC_ColorSpace} as its model.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ColorSpaceColorSystem extends AbstractColorSystem {

    private ColorSpace colorSpace;
    private String name;

    /**
     * Creates a new instance.
     */
    public ColorSpaceColorSystem(ColorSpace colorSpace) {
        this(colorSpace, null);
    }

    /**
     * Creates a new instance.
     */
    public ColorSpaceColorSystem(ColorSpace colorSpace, String name) {
        this.colorSpace = colorSpace;
        this.name = name;
    }

    /**
     * Creates a new instance.
     */
    public ColorSpaceColorSystem(InputStream iccProfile) throws IOException {
        this(iccProfile, null);
    }

    /**
     * Creates a new instance.
     */
    public ColorSpaceColorSystem(InputStream iccProfile, String name) throws IOException {
        read(iccProfile);
        this.name = name;
    }

    /**
     * Creates a new instance.
     */
    public ColorSpaceColorSystem(String resourceURL) {
        this(resourceURL, null);
    }

    /**
     * Creates a new instance.
     */
    public ColorSpaceColorSystem(String resourceURL, String name) {
        try {
            read(getClass().getResourceAsStream(resourceURL));
        } catch (IOException e) {
            InternalError err = new InternalError("Couldn't load \"" + resourceURL + "\".");
            err.initCause(e);
            throw err;
        }
        this.name = name;
    }

    protected void read(InputStream iccProfile) throws IOException {
        this.colorSpace = new ICC_ColorSpace(ICC_Profile.getInstance(iccProfile));
    }

    @Override
    public float[] toComponents(int r, int g, int b, float[] components) {
        float[] fromRGB = colorSpace.fromRGB(new float[]{r / 255f, g / 255f, b / 255f});
        if (components == null || components.length != fromRGB.length) {
            components = fromRGB;
        } else {
            System.arraycopy(fromRGB, 0, components, 0, fromRGB.length);
        }
        return components;
    }

    @Override
    public int toRGB(float... component) {
        float[] rgb = colorSpace.toRGB(component);
        return 0xff000000 | ((int) (rgb[0] * 255f) << 16) | ((int) (rgb[1] * 255f) << 8) | (int) (rgb[2] * 255f);
    }

    @Override
    public int getComponentCount() {
        return colorSpace.getNumComponents();
    }

    @Override
    public float getMinValue(int component) {
        return colorSpace.getMinValue(component);
    }

    @Override
    public float getMaxValue(int component) {
        return colorSpace.getMaxValue(component);
    }

    @Override
    public String getName() {
        return (name == null) ? colorSpace.getClass().getSimpleName() + "ColorSystem" : name;
    }
}
