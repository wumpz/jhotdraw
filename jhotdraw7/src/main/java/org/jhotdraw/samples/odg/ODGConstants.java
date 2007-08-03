/*
 * @(#)ODGConstants.java  1.0  April 11, 2007
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.samples.odg;

import java.util.*;

/**
 * ODGConstants.
 *
 * @author Werner Randelshofer
 * @version 1.0 April 11, 2007 Created.
 */
public class ODGConstants {
    public final static String OFFICE_NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:office:1.0";
    public final static String DRAWING_NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:drawing:1.0";
    public final static String SVG_NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0";
    public final static String STYLE_NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:style:1.0";
    public final static String TEXT_NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:text:1.0";
    
    public static enum StrokeStyle {
        NONE, DASH, SOLID
    }
    
    public final static Map<String,StrokeStyle> STROKE_STYLES;
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
    
    public final static Map<String,FillStyle> FILL_STYLES;
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
