/*
 * @(#)ColorWheelImageProducer.java  2.0  2008-04-19
 *
 * Copyright (c) 2005-2008 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package org.jhotdraw.color;

import java.awt.*;
import java.awt.image.*;
import java.awt.color.*;

/**
 * Produces the image of a ColorWheel.
 *
 * @see ColorWheel
 *
 * @author  Werner Randelshofer
 * @version 2.0 2008-04-19 Made class more subclassing-friendly.
 * <br>1.0 August 27, 2005 Created.
 */
public class ColorWheelImageProducer extends MemoryImageSource {

    protected int[] pixels;
    protected int w,  h;
    protected float brightness = 1f;
    protected boolean isDirty = true;
    /** Lookup table for hues. */
    protected float[] hues;
    /** Lookup table for saturations. */
    protected float[] saturations;
    /** Lookup table for alphas. 
     * The alpha value is used for antialiasing the
     * color wheel.
     */
    protected int[] alphas;
    protected ColorSystem colorSystem;

    /** Creates a new instance. */
    public ColorWheelImageProducer(ColorSystem sys, int w, int h) {
        super(w, h, null, 0, w);
        pixels = new int[w * h];
        this.w = w;
        this.h = h;
        this.colorSystem = sys;
        generateLookupTables();
        newPixels(pixels, ColorModel.getRGBdefault(), 0, w);
        setAnimated(true);
        generateColorWheel();
    }

    public int getRadius() {
        return Math.min(w, h) / 2 - 2;
    }

    protected void generateLookupTables() {
        saturations = new float[w * h];
        hues = new float[w * h];
        alphas = new int[w * h];
        float radius = getRadius();

        // blend is used to create a linear alpha gradient of two extra pixels
        float blend = (radius + 2f) / radius - 1f;

        // Center of the color wheel circle
        int cx = w / 2;
        int cy = h / 2;

        for (int x = 0; x < w; x++) {
            int kx = x - cx; // Kartesian coordinates of x
            int squarekx = kx * kx; // Square of kartesian x

            for (int y = 0; y < h; y++) {
                int ky = cy - y; // Kartesian coordinates of y

                int index = x + y * w;
                saturations[index] = (float) Math.sqrt(squarekx + ky * ky) / radius;
                if (saturations[index] <= 1f) {
                    alphas[index] = 0xff000000;
                } else {
                    alphas[index] = (int) ((blend - Math.min(blend, saturations[index] - 1f)) * 255 / blend) << 24;
                    saturations[index] = 1f;
                }
                if (alphas[index] != 0) {
                    hues[index] = (float) (Math.atan2(ky, kx) / Math.PI / 2d);
                }
            }
        }
    }

    public void setBrightness(float newValue) {
        isDirty = isDirty || brightness != newValue;
        brightness = newValue;
    }

    public boolean needsGeneration() {
        return isDirty;
    }

    public void regenerateColorWheel() {
        if (isDirty) {
            generateColorWheel();
        }
    }

    public void generateColorWheel() {
        float radius = (float) Math.min(w, h);
        for (int index = 0; index < pixels.length; index++) {
            if (alphas[index] != 0) {
                pixels[index] = alphas[index] | 0xffffff & colorSystem.toRGB(hues[index], saturations[index], brightness);
            }
        }
        newPixels();
        isDirty = false;
    }

    protected Point getColorLocation(Color c, int width, int height) {
        float[] hsb = new float[3];
        hsb = colorSystem.toComponents(c.getRGB(), hsb);
        return getColorLocation(hsb[0], hsb[1], hsb[2], width, height);
    }

    protected Point getColorLocation(float hue, float saturation, float brightness, int width, int height) {
        float radius = Math.min(width, height) / 2f;
        Point p = new Point(
                width / 2 + (int) (radius * saturation * Math.cos(hue * Math.PI * 2d)),
                height / 2 - (int) (radius * saturation * Math.sin(hue * Math.PI * 2d)));
        return p;
    }

    protected Point getColorLocation(CompositeColor c, int width, int height) {
        return getColorLocation(c.getComponents(), width, height);
    }

    protected Point getColorLocation(float[] components, int width, int height) {
        return getColorLocation(components[0], components[1], components[2], width, height);
    }

    protected float[] getColorAt(int x, int y, int width, int height) {
        x -= width / 2;
        y -= height / 2;
        float r = (float) Math.sqrt(x * x + y * y);
        float theta = (float) Math.atan2(y, -x);

        float hue = (float) (0.5 + (theta / Math.PI / 2d));

        float[] hsb = {
            hue,
            Math.min(1f, (float) r / getRadius()),
            brightness
        };
        return hsb;
    }
}
