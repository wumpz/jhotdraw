/*
 * @(#)ODGConstants.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.samples.odg;

import java.util.*;

/**
 * ODGConstants.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ODGConstants {
    public static final String OFFICE_NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:office:1.0";
    public static final String DRAWING_NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:drawing:1.0";
    public static final String SVG_NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0";
    public static final String STYLE_NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:style:1.0";
    public static final String TEXT_NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:text:1.0";
    
    public static enum StrokeStyle {
        NONE, DASH, SOLID
    }
    
    public static final Map<String,StrokeStyle> STROKE_STYLES;
    static {
        HashMap<String, StrokeStyle> m = new HashMap<String, StrokeStyle>();
        m.put("none", StrokeStyle.NONE);
        m.put("dash", StrokeStyle.DASH);
        m.put("solid", StrokeStyle.SOLID);
        STROKE_STYLES = Collections.unmodifiableMap(m);
    }
    
    public static enum FillStyle {
        NONE, SOLID, BITMAP, GRADIENT, HATCH
    }
    
    public static final Map<String,FillStyle> FILL_STYLES;
    static {
        HashMap<String, FillStyle> m = new HashMap<String, FillStyle>();
        m.put("none", FillStyle.NONE);
        m.put("solid", FillStyle.SOLID);
        m.put("bitmap", FillStyle.BITMAP);
        m.put("gradient", FillStyle.GRADIENT);
        m.put("hatch", FillStyle.HATCH);
        FILL_STYLES = Collections.unmodifiableMap(m);
    }
    

    /** Prevent instance creation. */
    private ODGConstants() {
    }
    
}
