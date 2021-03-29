/*
 * @(#)DiskColorWheelImageProducer.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.color;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import static java.lang.Math.*;

/**
 * Produces the image of a {@link JColorWheel} by interpreting two components
 * of a {@code ColorSpace} as x,y Cartesian coordinates within a disk boundary.
 *
 *
 * @see JColorWheel
 *
 * @author Werner Randelshofer
 * @version $Id: DiskColorWheelImageProducer.java 527 2009-06-07 14:28:19Z rawcoder $
 */
public class DiskColorWheelImageProducer extends AbstractColorWheelImageProducer {

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
    public DiskColorWheelImageProducer(ColorSpace sys, int w, int h) {
        this(sys, w, h, false, false);
    }

    /**
     * Creates a new instance.
     */
    public DiskColorWheelImageProducer(ColorSpace sys, int w, int h, boolean flipX, boolean flipY) {
        super(sys, w, h);
        this.flipX = flipX;
        this.flipY = flipY;
    }
    /**
     * allows to generate the lookup table 
     */
    protected void generateLookupTables() {
        radials = new float[w * h];
        angulars = new float[w * h];
        alphas = new int[w * h];
        float radius = getRadius();
        Point2D.Float center = getCenter();
        // blend is used to create a linear alpha gradient of two extra pixels
        float blend = (radius + 2f) / radius - 1f;
        // Center of the color wheel circle
        float maxR = colorSpace.getMaxValue(radialIndex);
        float minR = colorSpace.getMinValue(radialIndex);
        float extentR = maxR - minR;
        float maxA = colorSpace.getMaxValue(angularIndex);
        float minA = colorSpace.getMinValue(angularIndex);
        float extentA = maxA - minA;
        float cx = center.x;
        float cy = center.y;
        for (int x = 0; x < w; x++) {
            float kx = (x - cx) / radius;
            if (flipX) kx = -kx;
            float squarekx = kx * kx;
            for (int y = 0; y < h; y++) {
                float ky = (y - cy) / radius;
                if (flipY) ky = -ky;
                int index = x + y * w;
                float radiusRatio = (float) Math.sqrt(squarekx + ky * ky);
                if (radiusRatio <= 1f) alphas[index] = 0xff000000;
                else
                    alphas[index] = (int) ((blend - Math.min(blend, radiusRatio - 1f)) * 255 / blend) << 24;
                // scale from disk to box
                double scale = 1.0;
                radials[index] = (float) ((kx * scale + 1) / 2 * extentR + minR);
                angulars[index] = (float) ((ky * scale + 1) / 2 * extentA + minA);
            }
        }
        isLookupValid = true;
    }

    @Override
    /**
     * checks if the disk color needs generation 
     */
    public boolean needsGeneration() {
        return !isPixelsValid;
    }

    @Override
    /**
     * Regenerate the color wheel if pixels is not valid
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
                pixels[index] = alphas[index] | 0xffffff & ColorUtil.CStoRGB24(colorSpace, components, rgb);
            }
        }
        newPixels();
        isPixelsValid = true;
    }
    
    /**
     * compute radial or angular index of a component 
     * @param index radial or angular 
     * @param components components
     * @return float calculated index value 
     */
    private float computeRadialAngular(int index, float[] components) {
    	return (components[index] - colorSpace.getMinValue(index))
                / (colorSpace.getMaxValue(index) - colorSpace.getMinValue(index)) * 2 - 1;
    }
    
    /**
     * checks the value of FlipsX-Y 
     * @param flip FlipX-Y
     * @param index radial or angular index 
     * @return float calculated index value 
     */
    private float checkFlips(boolean flip,float index) {
    	return (flip)? -index :index;
    }
    
    @Override
    /**
     * Searches a color location in the color wheel 
     */
    public Point getColorLocation(float[] components) {
        float radius = getRadius();
        Point2D.Float center = getCenter();
        float radial = computeRadialAngular(radialIndex,components);
        float angular = computeRadialAngular(angularIndex,components);
        radial =checkFlips(flipX,radial);
        angular =checkFlips(flipY,angular);
        // clamp to disk
        float r = calculateR(angular,radial);
        if (r > 1f) {
            angular /= r;
            radial /= r;
        }
        return new Point(
                (int) (radius * radial + center.x),
                (int) (radius * angular + center.y)
        );
    }
    /**
     * calculates r value 
     * @param angular angular index
     * @param radial radial index 
     * @return r value 
     */
    private float calculateR(float angular,float radial) {
    	return (float) sqrt(angular * angular + radial * radial);
    }

    
    private float computeRadialAngularHsb(float index) {
    	return (index + 1f) / 2f
                * (colorSpace.getMaxValue(angularIndex) - colorSpace.getMinValue(angularIndex))
                + colorSpace.getMinValue(angularIndex);
    }
    @Override
    /**
     * gets the color by its position 
     */
    public float[] getColorAt(int x, int y) {
        float radius = getRadius();
        Point2D.Float center = getCenter();
        float radial = (x - center.x) / radius;
        float angular = (y - center.y) / radius;
        radial =checkFlips(flipY,radial);
        angular =checkFlips(flipX,angular);
        // clamp to disk
        float r = calculateR(angular,radial);
        if (r > 1f) {
            angular /= r;
            radial /= r;
        }
        float[] hsb = new float[3];
        hsb[angularIndex] = computeRadialAngularHsb(angular);
        hsb[radialIndex] = computeRadialAngularHsb(radial);
        hsb[verticalIndex] = verticalValue;
        return hsb;
    }
}
