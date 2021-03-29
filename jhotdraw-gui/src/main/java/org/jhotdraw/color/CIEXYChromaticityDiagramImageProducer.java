/*
 * @(#)CIEXYChromaticityDiagramImageProducer.java
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
import java.awt.color.ICC_ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.util.Arrays;

/**
 * Produces a CIE xy Chromaticity Diagram.
 * <p>
 * The diagram shows a projection of the CIE XYZ cube on a xy plane.
 * The projection is based on the following equations:
 * <p>
 * x = X / (X + Y + Z), y = Y / (X + Y + Z), z = 1 - x - y.
 * </p>
 * The equations can be rewritten as:
 * <p>
 * X = (x*(Y+Z)/(1-x), Y = (y*(X+Z)/(1-y).
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CIEXYChromaticityDiagramImageProducer extends MemoryImageSource {

    private static final float EPS = 0f; // 0.000001f;
    private static final float CEPS = 0f;
    protected int[] pixels;
    protected int w, h;
    protected ColorSpace colorSpace;
    protected int radialIndex = 1;
    protected int angularIndex = 0;
    protected int verticalIndex = 2;
    protected boolean isPixelsValid = false;
    protected float verticalValue = 1f;
    protected boolean isLookupValid = false;

    public enum OutsideGamutHandling {
        CLAMP,
        LEAVE_OUTSIDE
    };
    /**
     * By default, clamps non-displayable RGB values.
     */
    private OutsideGamutHandling outsideGamutHandling = OutsideGamutHandling.LEAVE_OUTSIDE;

    public CIEXYChromaticityDiagramImageProducer(int w, int h) {
        super(w, h, null, 0, w);
        this.colorSpace = ICC_ColorSpace.getInstance(ICC_ColorSpace.CS_CIEXYZ);
        pixels = new int[w * h];
        this.w = w;
        this.h = h;
        setAnimated(true);
        newPixels(pixels, ColorModel.getRGBdefault(), 0, w);
    }

    /**
     * checks if Diagram image needs generation
     * @return true if needs false otherwise
     */
    public boolean needsGeneration() {
        return !isPixelsValid;
    }
    
    /**
     * allows to regenerate the diagram
     */
    public void regenerateDiagram() {
        if (needsGeneration()) {
            generateImage();
        }
    }
    /**
     * Auxiliary function to generateImage 
     */
    private void auxGenImg(int ix, int iy, float x, float[] XYZ, float[] rgb, float hf, float Y) {
        float y = 0.9f - iy * hf;
        float z = 1f - x - y;
        if (y == 0) XYZ[0] = XYZ[1] = XYZ[2] = 0;
        else {
            XYZ[1] = Y; // Y=Y
            XYZ[0] = x * XYZ[1] / y; // X=x*Y/y
            XYZ[2] = z * XYZ[1] / y; // Z = (1-x-y)*Y/y
        }
        int alpha = XYZ[0] >= CEPS && XYZ[1] >= CEPS && XYZ[2] >= CEPS
                && XYZ[0] <= 1 - CEPS && XYZ[1] <= 1 - CEPS && XYZ[2] <= 1 - CEPS ? 255 : 0;
        if (alpha == 255) {
            toRGB(XYZ, rgb);
            alpha = (rgb[0] >= EPS && rgb[1] >= EPS && rgb[2] >= EPS
                    && rgb[0] <= 1 - EPS && rgb[1] <= 1 - EPS && rgb[2] <= 1 - EPS)
                            ? 255 : 0;
            if (alpha == 255) {
                pixels[ix + iy * w] = (alpha << 24) | ((0xff & (int) (rgb[0] * 255f)) << 16) | ((0xff & (int) (rgb[1] * 255f)) << 8) | (0xff & (int) (rgb[2] * 255f));
            }
        }
    }
    
    public void generateImage() {
        float wf = 0.8f / (float) w;
        float hf = 0.9f / (float) h;
        Arrays.fill(pixels, 0);
        float[] rgb = new float[3];
        for (int iY = 0; iY <= 100; iY++) {
            float Y = (100 - iY) / 100f;
            float[] XYZ = new float[3];
            for (int ix = 0; ix < w; ix++) {
                float x = ix * wf;
                for (int iy = 0; iy < h; iy++) {
                	if (pixels[ix + iy * w] != 0)
                        continue;
                	auxGenImg(ix,iy,x,XYZ,rgb,hf,Y);

                }
            }
        }
    }
    /**
     * gets the color c location 
     * @param c color to search its location 
     * @return Point color location coordinate
     */
    public Point getColorLocation(Color c) {
        return getColorLocation(ColorUtil.fromColor(colorSpace, c));
    }
    // maybe futur use
    public Point getColorLocation(float[] components) {
        return null;
    }


    /**
     * @return the width
     */
    public int getWidth() {
        return w;
    }
    /**
     * @return the height 
     */
    public int getHeight() {
        return h;
    }
    /**
     * @param val RGBs values
     * @return modified values
     */
    private double checkValRGBS(double val) {
    	return (val<=0.00304)?
    			12.92 * val:
    			1.055 * Math.pow(val, 1 / 2.4) - 0.055;
    }
    
    /**
     * converts ciexyz format to rgb 
     * @param ciexyz ciexyz array values 
     * @param rgb converted values stored here 
     */
    public void toRGB(float[] ciexyz, float[] rgb) {
        double X = ciexyz[0];
        double Y = ciexyz[1];
        double Z = ciexyz[2];
        // Convert to Wide Gamut RGB as described in
        // http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
        double Rs = 1.4628067 * X + -0.1840623 * Y + -0.2743606 * Z;
        double Gs = -0.5217933 * X + 1.4472381 * Y + 0.0677227 * Z;
        double Bs = 0.0349342 * X + -0.0968930 * Y + 1.2884099 * Z;
        Rs=checkValRGBS(Rs);
        Gs=checkValRGBS(Gs);
        Bs=checkValRGBS(Bs);
        switch (outsideGamutHandling) {
            case CLAMP:
                Rs = Math.min(1, Math.max(0, Rs));
                Gs = Math.min(1, Math.max(0, Gs));
                Bs = Math.min(1, Math.max(0, Bs));
                break;
            default:
            	break;
        }
        rgb[0] = (float) Rs;
        rgb[1] = (float) Gs;
        rgb[2] = (float) Bs;
    }
}
