/*
 * @(#)ColorSquareImageProducer.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.color;

import java.awt.*;
import java.awt.color.ColorSpace;

/**
 * Produces a square image for {@link JColorWheel} by interpreting two
 * components of a {@code ColorSpace} as x and y coordinates.
 *
 * @see JColorWheel
 *
 * @author Werner Randelshofer
 * @version $Id: ColorWheelImageProducer.java 527 2009-06-07 14:28:19Z rawcoder $
 */
public class ColorSquareImageProducer extends AbstractColorWheelImageProducer {

    /**
     * Lookup table for angular component values.
     */
    protected float[] angulars;
    /**
     * Lookup table for radial component values.
     */
    protected float[] radials;
    /**
     * Lookup table for alphas.
     * The alpha value is used for antialiasing the
     * color wheel.
     */
    protected int[] alphas;
    private boolean flipX, flipY;

    /**
     * Creates a new instance.
     */
    public ColorSquareImageProducer(ColorSpace sys, int w, int h) {
        this(sys, w, h, false, false);
    }

    /**
     * Creates a new instance.
     */
    public ColorSquareImageProducer(ColorSpace sys, int w, int h, boolean flipX, boolean flipY) {
        super(sys, w, h);
        this.flipX = flipX;
        this.flipY = flipY;
    }

    protected void generateLookupTables() {
        radials = new float[w * h];
        angulars = new float[w * h];
        alphas = new int[w * h];
        float maxR = colorSpace.getMaxValue(radialIndex);
        float minR = colorSpace.getMinValue(radialIndex);
        float extentR = maxR - minR;
        float maxA = colorSpace.getMaxValue(angularIndex);
        float minA = colorSpace.getMinValue(angularIndex);
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

    @Override
    /**
     * checks if pixels is valid 
     */
    public boolean needsGeneration() {
        return !isPixelsValid;
    }

    @Override
    /**
     * allows to regenerate color wheel if it pixels is not valid 
     */
    public void regenerateColorWheel() {
        if (needsGeneration())
            generateColorWheel();
    }

    @Override
    /**
     * allows to generate the color wheel 
     */
    public void generateColorWheel() {
        if (!isLookupValid)
            generateLookupTables();
        float[] components = new float[colorSpace.getNumComponents()];
        float[] rgb = new float[3];
        for (int index = 0; index < pixels.length; index++) {
            if (alphas[index] != 0) {
                components[angularIndex] = angulars[index];
                components[radialIndex] = radials[index];
                components[verticalIndex] = verticalValue;
                pixels[index] = (alphas[index] | 0xffffff) & ColorUtil.CStoRGB24(colorSpace, components, rgb);
            }
        }
        newPixels();
        isPixelsValid = true;
    }

    private float[] checkFlipXY(float[] flips) {
    	if (flipX) flips[0] = 1f - flips[0];
        if (flipY) flips[1] = 1f - flips[1];
        return flips;
    }
    
    private float computeRadAng(int val, float[] components) {
    	return (components[val] - colorSpace.getMinValue(val))
        / (colorSpace.getMaxValue(val) - colorSpace.getMinValue(val));
    }
    
    @Override
    /**
     * gets a color location in the color wheel 
     */
    public Point getColorLocation(float[] components) {
    	float[] flips = new float[2];
        flips[0] = computeRadAng(radialIndex,components);
        flips[1] = computeRadAng(angularIndex,components);
        flips=checkFlipXY(flips);
        int side = Math.min(w - 1, h - 1); // side length
        int xOffset = (w - side) / 2;
        int yOffset = (h - side) / 2;
        return new Point(
                (int) (side * flips[0]) + xOffset,
                (int) (side * flips[1]) + yOffset);
    }

    private float computeNew(float index,int val) {
    	return index * (colorSpace.getMaxValue(val) - colorSpace.getMinValue(val))
                + colorSpace.getMinValue(val);
    }
    @Override
    /**
     * gets the color by its location in the color wheel
     */
    public float[] getColorAt(int x, int y) {
    	float[] flips = new float[2];
        int side = Math.min(w - 1, h - 1); // side length
        int xOffset = (w - side) / 2;
        int yOffset = (h - side) / 2;
        flips[0] = (x - xOffset) / (float) side; //radial
        flips[1] = (y - yOffset) / (float) side; // angular
        flips=checkFlipXY(flips);
        float[] hsb = new float[3];
        hsb[angularIndex] = computeNew(flips[1],angularIndex);
        hsb[radialIndex] = computeNew(flips[0],radialIndex);
        hsb[verticalIndex] = verticalValue;
        return hsb;
    }
}
