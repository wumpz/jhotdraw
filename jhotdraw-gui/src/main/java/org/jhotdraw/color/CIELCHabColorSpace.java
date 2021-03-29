/*
 * @(#)CIELCHabColorSpace.java
 *
 * Copyright (c) 2010 The authors and contributors of JHotDraw.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.color;

import java.awt.color.ColorSpace;
import static java.lang.Math.*;

/**
 * The 1976 CIE L*CHa*b* color space (CIELCH).
 * <p>
 * The L* coordinate of an object is the lightness intensity as measured on a
 * scale from 0 to 100, where 0 represents black and 100 represents white.
 * <p>
 * The C and H coordinates are projections of the a* and b* colors of the
 * CIE L*a*b* color space into polar coordinates.
 * <pre>
 * a = C * cos(H)
 * b = C * sin(H)
 * </pre>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CIELCHabColorSpace extends AbstractNamedColorSpace {

    private static final long serialVersionUID = 1L;
    /**
     * The XYZ coordinates of the CIE Standard Illuminant D65 reference white.
     */
    private static final double[] D65 = {0.9505d, 1d, 1.0890d};
    private double Xr;
    /**
     * The Y coordinate of the D50 reference white.
     */
    private double Yr;
    /**
     * The Z coordinate of the D50 reference white.
     */
    private double Zr;
    private static final double EPS = 216d / 24389d;
    private static final double K = 24389d / 27d;
    /**
     * By default, clamps non-displayable RGB values.
     */
    private boolean isClampRGB = true;

    public CIELCHabColorSpace() {
        super(ColorSpace.TYPE_Lab, 3);
        Xr = D65[0];
        Yr = D65[1];
        Zr = D65[2];
    }

    @Override
    public float[] toRGB(float[] colorvalue, float[] rgb) {
        float[] ciexyz = rgb; //reuse array
        toCIEXYZ(colorvalue, ciexyz);
        // Convert to sRGB as described in
        // http://www.w3.org/Graphics/Color/sRGB.html
        double X = ciexyz[0];
        double Y = ciexyz[1];
        double Z = ciexyz[2];
        double Rs = 3.2410 * X + -1.5374 * Y + -0.4986 * Z;
        double Gs = -0.9692 * X + 1.8760 * Y + -0.0416 * Z;
        double Bs = 0.0556 * X + -0.2040 * Y + 1.0570 * Z;
        Rs=checkValSRGBS(Rs);
        Gs=checkValSRGBS(Gs);
        Bs=checkValSRGBS(Bs);
        if (isClampRGB) {
            Rs = Math.min(1, Math.max(0, Rs));
            Gs = Math.min(1, Math.max(0, Gs));
            Bs = Math.min(1, Math.max(0, Bs));
        }
        rgb[0] = (float) Rs;
        rgb[1] = (float) Gs;
        rgb[2] = (float) Bs;
        return rgb;
    }
    private double checkValSRGBS(double val) {
    	return (val<=0.00304)?
    			12.92 * val:
    			1.055 * Math.pow(val, 1 / 2.4) - 0.055;
    }
    @Override
    public float[] fromRGB(float[] rgb, float[] colorvalue) {
        return fromCIEXYZ(ColorUtil.RGBtoCIEXYZ(rgb, colorvalue), colorvalue);
    }

    /**
     * Lab to XYZ.
     * <pre>
     * X = xr*Xr;
     * Y = yr*Yr;
     * Z = zr*Zr;
     * </pre>
     * where
     * <pre>
     * xr = fx^3, if fx^3 &gt; eps
     *    = (116*fx - 16)/k, if fx^3 &lt;= eps
     *
     * yr = ((L+16)/116)^3, if L &gt; k*eps
     *    = L/k, if L &lt;= k*eps
     *
     * zr = fz^3, if fz^3 &gt; eps
     *    = (116*fz - 16)/k, if fz^3 &lt;= eps
     *
     * fx = a/500+fy
     *
     * fz = fy - b / 200
     *
     * fy = (L+16)/116
     *
     * eps = 216/24389
     * k = 24389/27
     * </pre>
     *
     * Source: <a href="http://www.brucelindbloom.com/index.html?Equations.html"
     * >http://www.brucelindbloom.com/index.html?Equations.html</a>
     *
     * @param colorvalue Lab color value.
     * @return CIEXYZ color value.
     */
    @Override
    public float[] toCIEXYZ(float[] colorvalue, float[] xyz) {
        double L = colorvalue[0];
        double C = colorvalue[1];
        double H = colorvalue[2] / 180 * PI;
        double a = C * Math.cos(H);
        double b = C * Math.sin(H);
        double fy = (L + 16d) / 116d;
        double fx = a / 500d + fy;
        double fz = fy - b / 200d;
        double xr, yr, zr;
        xr=checkTomodifiValR(fx);
        if (L > K * EPS) {
            yr = ((L + 16d) / 116d);
            yr = yr * yr * yr;
        } else yr = L / K;
        zr = checkTomodifiValR(fz);
        double X = xr * Xr;
        double Y = yr * Yr;
        double Z = zr * Zr;
        xyz[0] = (float) X;
        xyz[1] = (float) Y;
        xyz[2] = (float) Z;
        return xyz;
    }
    /**
     * checks power 3 of x-y-zR values to modify them 
     * @return modified values 
     */
    private double checkTomodifiValR(double f) {
		double fxf3 = f * f * f;
		return (fxf3 > EPS)? fxf3 : (116d * f - 16f) / K;
		    }

    /**
     * XYZ to Lab.
     * <pre>
     * L = 116*fy - 16
     * a = 500 * (fx - fy)
     * b = 200 * (fy - fz)
     * </pre>
     * where
     * <pre>
     * fx = xr^(1/3), if xr &gt; eps
     *    = (k*xr + 16) / 116 if xr &lt;= eps
     *
     * fy = yr^(1/3), if yr &gt; eps
     *    = (k*yr + 16) / 116 if yr &lt;= eps
     *
     * fz = zr^(1/3), if zr &gt; eps
     *    = (k*zr + 16) / 116 if zr &lt;= eps
     *
     * xr = X / Xr
     * yr = Y / Yr
     * zr = Z / Zr
     *
     * eps = 216/24389
     * k = 24389/27
     * </pre>
     *
     * Source: <a href="http://www.brucelindbloom.com/index.html?Equations.html"
     * >http://www.brucelindbloom.com/index.html?Equations.html</a>
     *
     * @param colorvalue CIEXYZ color value.
     * @return Lab color value.
     */
    @Override
    public float[] fromCIEXYZ(float[] xyz, float[] colorvalue) {
        double X = xyz[0];
        double Y = xyz[1];
        double Z = xyz[2];
        double xr = X / Xr;
        double yr = Y / Yr;
        double zr = Z / Zr;
        double fx, fy, fz;
        fx=checkValRCIEXYZ(xr);
        fy=checkValRCIEXYZ(yr);
        fz=checkValRCIEXYZ(zr);
        double L = 116d * fy - 16;
        double a = 500d * (fx - fy);
        double b = 200d * (fy - fz);
        double C = Math.sqrt(a * a + b * b);
        double H = Math.atan2(b, a);
        colorvalue[0] = (float) L;
        colorvalue[1] = (float) C;
        colorvalue[2] = (float) (H * 180 / PI);
        return colorvalue;
    }
    
    private double checkValRCIEXYZ(double valR) {
    	return (valR> EPS)?
    			Math.pow(valR, 1d / 3d):
    				(K * valR + 16d) / 116d;
    }
    
    @Override
    public String getName() {
        return "CIE 1976 L*CHa*b*";
    }

    @Override
    /**
     * get component minimum value 
     * @return float component min value 
     * @throws IllegalArgumentException if component not in [0..2]
     */
    public float getMinValue(int component) {
        switch (component) {
            case 0:
            case 1:
            case 2:
                return 0f;
            default :
            	throw new IllegalArgumentException("Illegal component:" + component);
        }
    }

    @Override
    /**
     * get component maximum value 
     * @return float component max value 
     * @throws IllegalArgumentException if component not in [0..2]
     */
    public float getMaxValue(int component) {
        switch (component) {
            case 0:
                return 100f;
            case 1:
                return 127f;
            case 2:
                return 360f;
            default :
            	throw new IllegalArgumentException("Illegal component:" + component);
        }
    }

    @Override
    /**
     * @return String component's name 
     */
    public String getName(int component) {
        switch (component) {
            case 0:
                return "L*";
            case 1:
                return "a*";
            case 2:
                return "b*";
            default :
            	throw new IllegalArgumentException("Illegal component:" + component);
        }
    }
    /**
     * allows to set or not ClampRGB 
     * @param b boolean, new ClampRGB value
     */
    public void setClampRGBValues(boolean b) {
        isClampRGB = b;
    }
    /**
     * 
     * @return true if ClampRGB is set false otherwise
     */
    public boolean isClampRGBValues() {
        return isClampRGB;
    }
    
    private static String concatVals(float[] xyz) {
    	return xyz[0] + "," + xyz[1] + "," + xyz[2];
    }
    
    public static void main(String[] arg) {
        CIELCHabColorSpace cs = new CIELCHabColorSpace();
        float[] lchab = cs.fromRGB(new float[]{1, 1, 1});
        System.out.println("rgb->LCHab:" + concatVals(lchab));
        float[] xyz = cs.toCIEXYZ(new float[]{0.75f, 0.25f, 0.1f});
        System.out.println("    lab->xyz:" + concatVals(xyz));
        lchab = cs.fromCIEXYZ(xyz);
        System.out.println("R xyz->LCHab:" + concatVals(lchab));
        lchab = cs.fromCIEXYZ(new float[]{1, 1, 1});
        System.out.println("xyz->LCHab:" + concatVals(lchab));
        lchab = cs.fromCIEXYZ(new float[]{0.5f, 1, 1});
        System.out.println("xyz->LCHab:" + concatVals(lchab));
    }
}
