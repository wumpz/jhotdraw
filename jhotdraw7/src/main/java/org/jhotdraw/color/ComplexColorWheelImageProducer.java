/*
 * @(#)ColorWheelImageProducer.java
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
 * Produces the image of a {@link JColorWheel} by interpreting two components of
 * a {@code ColorSpace} as complex numbers (real and imaginary).
 *
 *
 * @see JColorWheel
 *
 * @author Werner Randelshofer
 * @version $Id: ColorWheelImageProducer.java 527 2009-06-07 14:28:19Z rawcoder
 * $
 */
public class ComplexColorWheelImageProducer extends AbstractColorWheelImageProducer {

    /**
     * Lookup table for angular component values.
     */
    protected float[] angulars;
    /**
     * Lookup table for radial component values.
     */
    protected float[] radials;
    /**
     * Lookup table for alphas. The alpha value is used for antialiasing the
     * color wheel.
     */
    protected int[] alphas;
    private boolean flipX, flipY;

    /**
     * Creates a new instance.
     */
    public ComplexColorWheelImageProducer(ColorSpace sys, int w, int h) {
        this(sys, w, h, false, false);
    }

    /**
     * Creates a new instance.
     */
    public ComplexColorWheelImageProducer(ColorSpace sys, int w, int h, boolean flipX, boolean flipY) {
        super(sys, w, h);
        this.flipX = flipX;
        this.flipY = flipY;
    }

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
        int side = Math.min(w, h); // side length
        float cx = center.x;
        float cy = center.y;
        float extentX = side - 1;
        float extentY = extentX;
        for (int x = 0; x < w; x++) {
            float kx = (x - cx) / radius;
            if (flipX) {
                kx = -kx;
            }
            float squarekx = kx * kx;

            for (int y = 0; y < h; y++) {
                float ky = (y - cy) / radius;
                if (flipY) {
                    ky = -ky;
                }

                int index = x + y * w;

                float r = (float) Math.sqrt(squarekx + ky * ky);
                if (r <= 1f) {
                    alphas[index] = 0xff000000;
                    //radials[index] = radiusRatio;
                } else {
                    alphas[index] = (int) ((blend - Math.min(blend, r - 1f)) * 255 / blend) << 24;
                    //radials[index] = maxR;
                }
                if (alphas[index] != 0) {
                    //angulars[index] = (float) (Math.atan2(ky, kx));
                }
                double angle = atan2(ky, kx);

                // distort from disk to box
                float scale = (float) max(abs(sin(angle)), abs(cos(angle)));

                // we don't want too much distortion at the center of the disk
                scale = (1 - r) + scale * r;

                // perform distortion
                radials[index] = max(minR,min((kx / scale + 1) / 2 * extentR + minR,maxR));
                angulars[index] = max(minA,min((ky / scale + 1) / 2 * extentA + minA,maxA));
            }
        }
        isLookupValid = true;
    }

    @Override
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

        float[] components = new float[colorSpace.getNumComponents()];
        float[] rgb=new float[3];
        for (int index = 0; index < pixels.length; index++) {
            if (alphas[index] != 0) {
                components[angularIndex] = angulars[index];
                components[radialIndex] = radials[index];
                components[verticalIndex] = verticalValue;
                pixels[index] = alphas[index] | 0xffffff & ColorUtil.CStoRGB24(colorSpace, components,rgb);
            }
        }
        newPixels();
        isPixelsValid = true;
    }

    @Override
    public Point getColorLocation(float[] components) {
        float radius = getRadius();
        Point2D.Float center = getCenter();

        float radial = (components[radialIndex] - colorSpace.getMinValue(radialIndex))//
                / (colorSpace.getMaxValue(radialIndex) - colorSpace.getMinValue(radialIndex)) * 2 - 1;
        float angular = (components[angularIndex] - colorSpace.getMinValue(angularIndex))//
                / (colorSpace.getMaxValue(angularIndex) - colorSpace.getMinValue(angularIndex)) * 2 - 1;
        if (flipX) {
            radial = -radial;
        }
        if (flipY) {
            angular = -angular;
        }

        radial = max(-1, min(radial, 1));
        angular = max(-1, min(angular, 1));


        double a = atan2(radial, angular);
        double sina = sin(a);
        double cosa = cos(a);
        double d = max(abs(sina), abs(cosa));

        double dx = (abs(sina) > abs(cosa)) ? sina : cosa;
        double bx = (abs(sina) > abs(cosa)) ? radial : angular;
        double r;
        if (d == 1 && dx != 0) {
            r = bx / dx;
        } else {
            r = (d * dx - sqrt(d * dx * (-4 * bx * d + 4 * bx + d * dx))) / (2 * (d - 1) * dx);
            if (r < 0) {
                r = (d * dx + sqrt(d * dx * (-4 * bx * d + 4 * bx + d * dx))) / (2 * (d - 1) * dx);
            }
        }


        Point p = new Point(
                (int) (r * sina * radius + center.x),
                (int) (r * cosa * radius + center.y)//
                );
        return p;
    }

    @Override
    public float[] getColorAt(int x, int y) {

        float radius = getRadius();
        Point2D.Float center = getCenter();

        float maxR = colorSpace.getMaxValue(radialIndex);
        float minR = colorSpace.getMinValue(radialIndex);
        float extentR = maxR - minR;
        float maxA = colorSpace.getMaxValue(angularIndex);
        float minA = colorSpace.getMinValue(angularIndex);
        float extentA = maxA - minA;
        int side = Math.min(w, h); // side length
        float cx = center.x;
        float cy = center.y;
        float extentX = side - 1;
        float extentY = extentX;

        float radial, angular;


        float kx = (x - cx) / radius;
        if (flipX) {
            kx = -kx;
        }
        float squarekx = kx * kx;


        float ky = (y - cy) / radius;
        if (flipY) {
            ky = -ky;
        }

        int index = x + y * w;

        float r = (float) Math.sqrt(squarekx + ky * ky);
        double angle = atan2(ky, kx);

        // distort from disk to box
        float scale = (float) max(abs(sin(angle)), abs(cos(angle)));

        // we don't want too much distortion at the center of the disk
        scale = (1 - r) + scale * r;

        // perform distortion
        radial = (kx / scale + 1) / 2 * extentR + minR;
        angular = (ky / scale + 1) / 2 * extentA + minA;



        float[] rav = new float[3];
        rav[angularIndex] = angular;
        rav[radialIndex] = radial;
        rav[verticalIndex] = verticalValue;

        return rav;
    }

    public float[] getColorAtOld(int x, int y) {
        int side = Math.min(w - 1, h - 1); // side length
        int xOffset = (w - side) / 2;
        int yOffset = (h - side) / 2;

        float radial = (x - xOffset) / (float) side;
        float angular = (y - yOffset) / (float) side;
        if (flipX) {
            radial = 1f - radial;
        }
        if (!flipY) {
            angular = 1f - angular;
        }

        float[] rav = new float[3];
        rav[angularIndex] = angular//
                * (colorSpace.getMaxValue(angularIndex) - colorSpace.getMinValue(angularIndex))//
                + colorSpace.getMinValue(angularIndex);
        rav[radialIndex] = radial//
                * (colorSpace.getMaxValue(radialIndex) - colorSpace.getMinValue(radialIndex))//
                + colorSpace.getMinValue(radialIndex);
        rav[verticalIndex] = verticalValue;

        int xy = x + y * w;
        System.out.println("ComplexColorWheelImageProducer.getColorAt " + rav[angularIndex] + "," + rav[radialIndex] + " ~ " + angulars[xy] + "," + radials[xy]);
        rav[angularIndex] = angulars[xy];
        rav[radialIndex] = radials[xy];

        Point p = getColorLocation(rav);
        System.out.println("ComplexColorWheelImageProducer.getColorAt( " + x + "," + y + " => " + p.x + "," + p.y);

        return rav;
    }
}
