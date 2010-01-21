/*
 * @(#)ColorWheelImageProducer.java
 *
 * Copyright (c) 2008 by the original authors of JHotDraw
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

import java.awt.*;
import java.awt.image.*;

/**
 * Produces the image of a ColorWheel.
 *
 * @see JColorWheel
 *
 * @author  Werner Randelshofer
 * @version $Id: ColorWheelImageProducer.java 527 2009-06-07 14:28:19Z rawcoder $
 */
public class ColorSquareImageProducer extends AbstractColorWheelImageProducer {

    /** Lookup table for angular component values. */
    protected float[] angulars;
    /** Lookup table for radial component values. */
    protected float[] radials;
    /** Lookup table for alphas.
     * The alpha value is used for antialiasing the
     * color wheel.
     */
    protected int[] alphas;
    private boolean flipX, flipY;

    /** Creates a new instance. */
    public ColorSquareImageProducer(ColorSystem sys, int w, int h) {
        this(sys,w,h,false,false);
    }
    /** Creates a new instance. */
    public ColorSquareImageProducer(ColorSystem sys, int w, int h, boolean flipX, boolean flipY) {
        super(sys, w, h);
        this.flipX = flipX;
        this.flipY = flipY;
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

        float maxR = colorSystem.getMaxValue(radialIndex);
        float minR = colorSystem.getMinValue(radialIndex);
        float extentR = maxR - minR;
        float maxA = colorSystem.getMaxValue(angularIndex);
        float minA = colorSystem.getMinValue(angularIndex);
        float extentA = maxA - minA;

        int side = Math.min(w - 1, h - 1); // side length
        int xOffset = (w - side) / 2;
        int yOffset = (h - side) / 2 * w;
        float extentX = side - 1;
        float extentY = extentX;
        for (int x = 0; x < side; x++) {
            float xRatio = (flipX) ? 1f - x / extentX : x / extentX;

            for (int y = 0; y < side; y++) {
                float yRatio = (flipY) ? 1f - y / extentY : y / extentY;

                int index = x + y * w + xOffset + yOffset;

                alphas[index] = 0xff000000;
                radials[index] = xRatio * extentR + minR;
                angulars[index] = yRatio * extentA + minA;
            }
        }
        isLookupValid = true;
    }

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

        float[] components = new float[colorSystem.getComponentCount()];
        float radius = (float) Math.min(w, h);
        for (int index = 0; index < pixels.length; index++) {
            if (alphas[index] != 0) {
                components[angularIndex] = angulars[index];
                components[radialIndex] = radials[index];
                components[verticalIndex] = verticalValue;
                pixels[index] = alphas[index] | 0xffffff & colorSystem.toRGB(components);
            }
        }
        newPixels();
        isPixelsValid = true;
    }

    @Override
    public Point getColorLocation(Color c) {
        float[] hsb = new float[3];
        hsb = colorSystem.toComponents(c.getRGB(), hsb);
        return getColorLocation(hsb);
    }

    @Override
    public Point getColorLocation(float[] components) {
        float radial = (components[radialIndex] - colorSystem.getMinValue(radialIndex))//
                / (colorSystem.getMaxValue(radialIndex) - colorSystem.getMinValue(radialIndex));
        float angular = (components[angularIndex] - colorSystem.getMinValue(angularIndex))//
                / (colorSystem.getMaxValue(angularIndex) - colorSystem.getMinValue(angularIndex));
        if (flipX) radial=1f-radial;
        if (flipY) angular=1f-angular;


        int side = Math.min(w - 1, h - 1); // side length
        int xOffset = (w - side) / 2;
        int yOffset = (h - side) / 2;

        Point p = new Point(
                (int) (side * radial) + xOffset,
                (int) (side * angular) + yOffset//
                );
        return p;
    }

    @Override
    public Point getColorLocation(CompositeColor c) {
        return getColorLocation(c.getComponents());
    }

    @Override
    public float[] getColorAt(int x, int y) {
        int side = Math.min(w - 1, h - 1); // side length
        int xOffset = (w - side) / 2;
        int yOffset = (h - side) / 2;

        float radial = (x - xOffset) / (float) side;
        float angular = (y - yOffset) / (float) side;
        if (flipX) radial=1f-radial;
        if (flipY) angular=1f-angular;

        float[] hsb = new float[3];
        hsb[angularIndex] = angular//
                * (colorSystem.getMaxValue(angularIndex) - colorSystem.getMinValue(angularIndex))//
                + colorSystem.getMinValue(angularIndex);
        hsb[radialIndex] = radial//
                * (colorSystem.getMaxValue(radialIndex) - colorSystem.getMinValue(radialIndex))//
                + colorSystem.getMinValue(radialIndex);
        hsb[verticalIndex] = verticalValue;
        return hsb;
    }
}
