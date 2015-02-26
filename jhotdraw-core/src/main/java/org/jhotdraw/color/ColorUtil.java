/*
 * @(#)ColorUtil.java
 * 
 * Copyright (c) 2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.color;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.ParseException;
import org.jhotdraw.text.ColorToolTipTextFormatter;

/**
 * A utility class for {@code Color} and {@code ColorSpace} objects.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ColorUtil {

    private static ColorToolTipTextFormatter formatter;
    private static final ColorSpace sRGB = ColorSpace.getInstance(ColorSpace.CS_sRGB);

    /**
     * Prevent instance creation.
     */
    private ColorUtil() {
    }

    /**
     * Returns the color components in the specified color space from a
     * {@code Color} object.
     */
    public static float[] fromColor(ColorSpace colorSpace, Color c) {
        if (isEqual(c.getColorSpace(), colorSpace)) {
            float[] components = c.getComponents(null);
            return components;
        } else {
            return c.getComponents(colorSpace, null);
        }
    }

    /**
     * Returns a color object from color components in the specified color
     * space.
     */
    public static Color toColor(ColorSpace colorSpace, float... components) {
        return new CompositeColor(colorSpace, components, 1f);

    }

    /**
     * Returns the color components in the specified color space from an rgb
     * value.
     */
    public static float[] fromRGB(ColorSpace colorSpace, int rgb) {
        return fromRGB(colorSpace, (rgb >>> 16) & 0xff, (rgb >>> 8) & 0xff, rgb & 0xff);
    }

    /**
     * Returns the color components in the specified color space from RGB
     * values.
     */
    public static float[] fromRGB(ColorSpace colorSpace, int r, int g, int b) {
        return colorSpace.fromRGB(new float[]{r / 255f, g / 255f, b / 255f});
    }

    /**
     * Returns an rgb value from color components in the specified color space.
     */
    public static int toRGB24(ColorSpace colorSpace, float... components) {
        return CStoRGB24(colorSpace, components, new float[3]);
    }

    public static int CStoRGB24(ColorSpace colorSpace, float[] components, float[] rgb) {
        CStoRGB(colorSpace, components, rgb);

        // If the color is not displayable in RGB, we return transparent black.
        if (rgb[0] < 0f || rgb[1] < 0f || rgb[2] < 0f || rgb[0] > 1f || rgb[1] > 1f || rgb[2] > 1f) {
            return 0;
        }
        return 0xff000000 | ((int) (rgb[0] * 255f) << 16) | ((int) (rgb[1] * 255f) << 8) | (int) (rgb[2] * 255f);
    }

    /**
     * Returns a tool tip text for the specified color with information in the
     * color space of the color.
     */
    public static String toToolTipText(Color c) {
        if (formatter == null) {
            formatter = new ColorToolTipTextFormatter();
        }
        try {
            return formatter.valueToString(c);
        } catch (ParseException ex) {
            InternalError error = new InternalError("Unable to generate tool tip text from color " + c);
            error.initCause(ex);
            throw error;
        }
    }

    /**
     * Returns true, if the two color spaces are equal.
     */
    public static boolean isEqual(ColorSpace a, ColorSpace b) {
        if ((a instanceof ICC_ColorSpace) && (b instanceof ICC_ColorSpace)) {
            ICC_ColorSpace aicc = (ICC_ColorSpace) a;
            ICC_ColorSpace bicc = (ICC_ColorSpace) b;
            ICC_Profile ap = aicc.getProfile();
            ICC_Profile bp = bicc.getProfile();
            return ap.equals(bp);
        } else {
            return a.equals(b);
        }
    }

    /**
     * Returns the name of the color space. If the color space is an
     * {@code ICC_ColorSpace} the name is retrieved from the "desc" data element
     * of the color profile.
     *
     * @param a A ColorSpace.
     * @return The name.
     */
    public static String getName(ColorSpace a) {
        if (a instanceof NamedColorSpace) {
            return ((NamedColorSpace) a).getName();
        }
        if ((a instanceof ICC_ColorSpace)) {
            ICC_ColorSpace icc = (ICC_ColorSpace) a;
            ICC_Profile p = icc.getProfile();
            // Get the name from the profile description tag
            byte[] desc = p.getData(0x64657363);
            if (desc != null) {
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(desc));
                try {
                    int magic = in.readInt();
                    int reserved = in.readInt();
                    if (magic != 0x64657363) {
                        throw new IOException("Illegal magic:" + Integer.toHexString(magic));
                    }
                    if (reserved != 0x0) {
                        throw new IOException("Illegal reserved:" + Integer.toHexString(reserved));
                    }
                    long nameLength = in.readInt() & 0xffffffffL;
                    StringBuilder buf = new StringBuilder();
                    for (int i = 0; i < nameLength - 1; i++) {
                        buf.append((char) in.readUnsignedByte());
                    }
                    return buf.toString();
                } catch (IOException e) {
                    // fall back
                    e.printStackTrace();
                }
            }
        }

        if (a instanceof ICC_ColorSpace) {
            // Fall back if no description is available
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < a.getNumComponents(); i++) {
                if (buf.length() > 0) {
                    buf.append("-");
                }
                buf.append(a.getName(i));
            }
            return buf.toString();
        } else {
            return a.getClass().getSimpleName();
        }
    }

    /**
     * Blackens the specified color by casting a black shadow of the specified
     * amount on the color.
     */
    public static Color shadow(Color c, int amount) {
        return new Color(
                Math.max(0, c.getRed() - amount),
                Math.max(0, c.getGreen() - amount),
                Math.max(0, c.getBlue() - amount),
                c.getAlpha());
    }

    /**
     * Faster toRGB method which uses the provided output array.
     */
    public static void CStoRGB(ColorSpace cs, float[] colorvalue, float[] rgb) {
        if (cs.isCS_sRGB()) {
            System.arraycopy(colorvalue,0,rgb,0,3);
        } else if (cs instanceof NamedColorSpace) {
            CStoRGB((NamedColorSpace) cs, colorvalue, rgb);
        } else {
            float[] tmp = cs.toRGB(colorvalue);
            System.arraycopy(tmp, 0, rgb, 0, tmp.length);
        }
    }

    /**
     * Faster fromRGB method which uses the provided output array.
     */
    public static float[] CSfromRGB(ColorSpace cs, float[] rgb, float[] colorvalue) {
        if (cs instanceof NamedColorSpace) {
            CSfromRGB((NamedColorSpace) cs, rgb, colorvalue);
        } else {
            float[] tmp = cs.fromRGB(rgb);
            System.arraycopy(tmp, 0, colorvalue, 0, tmp.length);
        }
        return colorvalue;
    }

    /**
     * Faster toCIEXYZ method which uses the provided output array.
     */
    public static float[] CStoCIEXYZ(ColorSpace cs, float[] colorvalue, float[] xyz) {
        if (cs instanceof NamedColorSpace) {
            CStoCIEXYZ((NamedColorSpace) cs, colorvalue, xyz);
        } else {
            float[] tmp = cs.toCIEXYZ(colorvalue);
            System.arraycopy(tmp, 0, xyz, 0, tmp.length);
        }
        return xyz;
    }

    /**
     * Faster fromCIEXYZ method which uses the provided output array.
     */
    public static float[] CSfromCIEXYZ(ColorSpace cs, float[] xyz, float[] colorvalue) {
        if (cs instanceof NamedColorSpace) {
            CSfromCIEXYZ((NamedColorSpace) cs, xyz, colorvalue);
        } else {
            float[] tmp = cs.toRGB(xyz);
            System.arraycopy(tmp, 0, colorvalue, 0, tmp.length);
        }
        return colorvalue;
    }

    /**
     * Faster toRGB method which uses the provided output array.
     */
    public static float[] CStoRGB(NamedColorSpace cs, float[] colorvalue, float[] rgb) {
        cs.toRGB(colorvalue, rgb);
        return rgb;
    }

    /**
     * Faster CIEXYZtoRGB method which uses the provided output array.
     */
    public static float[] CIEXYZtoRGB(float[] xyz, float[] rgb) {
        float[] tmp = sRGB.fromCIEXYZ(xyz);
        System.arraycopy(tmp, 0, rgb, 0, 3);
        return rgb;
    }

    /**
     * Faster RGBtoCIEXYZ method which uses the provided output array.
     */
    public static float[] RGBtoCIEXYZ(float[] rgb, float[] xyz) {
        float[] tmp = sRGB.toCIEXYZ(rgb);
        System.arraycopy(tmp, 0, xyz, 0, 3);
        return xyz;
    }

    /**
     * Faster fromRGB method which uses the provided output array.
     */
    public static void CSfromRGB(NamedColorSpace cs, float[] rgb, float[] colorvalue) {
        cs.fromRGB(rgb, colorvalue);
    }

    /**
     * Faster toCIEXYZ method which uses the provided output array.
     */
    public static void CStoCIEXYZ(NamedColorSpace cs, float[] colorvalue, float[] xyz) {
        cs.toCIEXYZ(colorvalue, xyz);
    }

    /**
     * Faster fromCIEXYZ method which uses the provided output array.
     */
    public static void CSfromCIEXYZ(NamedColorSpace cs, float[] xyz, float[] colorvalue) {
        cs.fromCIEXYZ(xyz, xyz);
    }
}
