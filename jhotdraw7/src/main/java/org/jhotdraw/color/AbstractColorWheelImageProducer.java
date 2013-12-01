/*
 * @(#)AbstractColorWheelImageProducer.java
 * 
 * Copyright (c) 2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.color;

import java.awt.Color;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;

/**
 * AbstractColorWheelImageProducer.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractColorWheelImageProducer extends MemoryImageSource {

    protected int[] pixels;
    protected int w, h;
    protected ColorSpace colorSpace;
    protected int radialIndex = 1;
    protected int angularIndex = 0;
    protected int verticalIndex = 2;
    protected boolean isPixelsValid = false;
    protected float verticalValue = 1f;
    protected boolean isLookupValid = false;

    public AbstractColorWheelImageProducer(ColorSpace sys, int w, int h) {
        super(w, h, null, 0, w);
        this.colorSpace = sys;
        pixels = new int[w * h];
        this.w = w;
        this.h = h;
        setAnimated(true);

        newPixels(pixels, ColorModel.getRGBdefault(), 0, w);
    }

    public void setRadialComponentIndex(int newValue) {
        radialIndex = newValue;
        isPixelsValid = false;
    }

    public void setAngularComponentIndex(int newValue) {
        angularIndex = newValue;
        isPixelsValid = false;
    }

    public void setVerticalComponentIndex(int newValue) {
        verticalIndex = newValue;
        isPixelsValid = false;
    }

    public void setVerticalValue(float newValue) {
        isPixelsValid = isPixelsValid && verticalValue == newValue;
        verticalValue = newValue;
    }

    public boolean needsGeneration() {
        return !isPixelsValid;
    }

    public void regenerateColorWheel() {
        if (!isPixelsValid) {
            generateColorWheel();
        }
    }

    public float getRadius() {
        return Math.min(w, h) * 0.5f - 2;
    }
    public Point2D.Float getCenter() {
        return new Point2D.Float(w*0.5f, h*0.5f);
    }

    protected abstract void generateColorWheel();

    public Point getColorLocation(Color c) {
        float[] components =ColorUtil.fromColor(colorSpace, c);
        return getColorLocation(components);
    }


    public abstract Point getColorLocation(float[] components);

    public abstract float[] getColorAt(int x, int y);
}
