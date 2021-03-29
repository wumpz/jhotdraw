/*
 * @(#)ColorTrackImageProducer.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.color;

import java.awt.color.ColorSpace;
import java.awt.image.*;

/**
 * ColorTrackImageProducer creates the image for the track of a
 * color slider.
 *
 * @see ColorSliderUI
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ColorTrackImageProducer extends MemoryImageSource {

    private int[] pixels;
    private int w, h;
    private int trackBuffer;
    private ColorSliderModel colorizer = new DefaultColorSliderModel(ColorSpace.getInstance(ColorSpace.CS_sRGB));
    private boolean isDirty = true;
    private int componentIndex = 0;
    private boolean isHorizontal;

    /**
     * Creates a new instance.
     */
    public ColorTrackImageProducer(int w, int h, int trackBuffer, boolean isHorizontal) {
        super(w, h, null, 0, w);
        pixels = new int[w * h];
        this.w = w;
        this.h = h;
        // trackBuffer must be even
        this.trackBuffer = ((trackBuffer & 1) == 0) ? trackBuffer : trackBuffer - 1;
        this.isHorizontal = isHorizontal;
        newPixels(pixels, new DirectColorModel(24,
                0x00ff0000, // Red
                0x0000ff00, // Green
                0x000000ff // Blue
        ),
                0, w);
        setAnimated(true);
    }
    
    /**
     * @return width
     */
    public int getWidth() {
        return w;
    }
    /**
     * @return height
     */
    public int getHeight() {
        return h;
    }
    /**
     * mark the color track as dirty
     */
    public void markAsDirty() {
        isDirty = true;
    }
    /**
     * check if color track is dirty 
     * @return true if the color track is dirty 
     */
    public boolean needsGeneration() {
        return isDirty;
    }
    /**
     * regenerate the color track if it marked as dirty
     */
    public void regenerateColorTrack() {
        if (needsGeneration())
            generateColorTrack();
    }
    /**
     * generate the color track , not dirty after generation 
     */
    public void generateColorTrack() {
        if (isHorizontal)
            generateHorizontalColorTrack();
        else
            generateVerticalColorTrack();
        newPixels();
        isDirty = false;
    }
    /**
     * allows to generate the horizontal color track 
     */
    private void generateHorizontalColorTrack() {
        float[] components = colorizer.getComponents();
        float[] rgb = new float[3];
        ColorSpace cs = colorizer.getColorSpace();
        int offset = trackBuffer / 2;
        float minv = cs.getMinValue(componentIndex);
        float maxv = cs.getMaxValue(componentIndex);
        for (int x = 0, n = w - trackBuffer - 1; x <= n; x++) {
            components[componentIndex] = (x / (float) n) * (maxv - minv) + minv;
            pixels[x + offset] = ColorUtil.CStoRGB24(cs, components, rgb);
        }
        for (int x = 0; x < offset; x++) {
            pixels[x] = pixels[offset];
            pixels[w - x - 1] = pixels[w - offset - 1];
        }
        for (int y = w, n = w * h; y < n; y += w) {
            System.arraycopy(pixels, 0, pixels, y, w);
        }
    }
    /**
     * allows to generate the vertical color track 
     */
    private void generateVerticalColorTrack() {
        float[] components = colorizer.getComponents();
        float[] rgb = new float[3];
        ColorSpace cs = colorizer.getColorSpace();
        int offset = trackBuffer / 2;
        float minv = cs.getMinValue(componentIndex);
        float maxv = cs.getMaxValue(componentIndex);
        for (int y = 0, n = h - trackBuffer - 1; y <= n; y++) {
            // Note: removed + minv - minv from formula below
            components[componentIndex] = maxv - (y / (float) n) * (maxv - minv);
            pixels[(y + offset) * w] = ColorUtil.CStoRGB24(cs, components, rgb);
        }
        for (int y = 0; y < offset; y++) {
            pixels[y * w] = pixels[offset * w];
            pixels[(h - y - 1) * w] = pixels[(h - offset - 1) * w];
        }
        for (int x = 1; x < w; x++) {
            for (int y = 0, n = w * h; y < n; y += w) {
                pixels[x + y] = pixels[x - 1 + y];
            }
        }
    }
    /**
     * sets the color slide model to a specific value and mark it as dirty 
     * @param colorizer new value 
     */
    public void setColorSliderModel(ColorSliderModel colorizer) {
        this.colorizer = colorizer;
        isDirty = true;
    }
    /**
     * sets the color component index to a new value and mark it as dirty
     * @param index new value 
     */
    public void setColorComponentIndex(int index) {
        this.componentIndex = index;
        isDirty = true;
    }

    public void componentChanged(int index) {
        isDirty |= this.componentIndex != index;
    }
}
