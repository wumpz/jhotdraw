/*
 * @(#)SVGUtils.java  1.0  July 8, 2006
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.samples.svg;

import java.awt.Color;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.samples.svg.figures.*;
import org.jhotdraw.xml.*;
import static org.jhotdraw.draw.AttributeKeys.*;

/**
 * SVGUtils.
 *
 * @author Werner Randelshofer
 * @version 1.0 July 8, 2006 Created.
 */
public class SVGUtil {
    private final static Color INHERIT_COLOR = new Color(0xffff00);
    private final static Color CURRENT_COLOR = new Color(0xffff00);
    public final static Map<String,Color> SVG_COLORS;
    static {
        LinkedHashMap<String,Color> map = new LinkedHashMap<String,Color>();
        
        // SVG 1.2 Tiny colors
        map.put("black", new Color(0, 0, 0));
        map.put("green", new Color(0, 128, 0));
        map.put("silver", new Color(192, 192, 192));
        map.put("lime", new Color(0, 255, 0));
        map.put("gray", new Color(128, 128, 128));
        map.put("olive", new Color(128, 128, 0));
        map.put("white", new Color(255, 255, 255));
        map.put("yellow", new Color(255, 255, 0));
        map.put("maroon", new Color(128, 0, 0));
        map.put("navy", new Color(0, 0, 128));
        map.put("red", new Color(255, 0, 0));
        map.put("blue", new Color(0, 0, 255));
        map.put("purple", new Color(128, 0, 128));
        map.put("teal", new Color(0, 128, 128));
        map.put("fuchsia", new Color(255, 0, 255));
        map.put("aqua", new Color(0, 255, 255));
        
        // SVG 1.1 colors
        map.put("aliceblue", new Color(240, 248, 255));
        map.put("antiquewhite", new Color(250, 235, 215));
        map.put("aqua", new Color( 0, 255, 255));
        map.put("aquamarine", new Color(127, 255, 212));
        map.put("azure", new Color(240, 255, 255));
        map.put("beige", new Color(245, 245, 220));
        map.put("bisque", new Color(255, 228, 196));
        map.put("black", new Color( 0, 0, 0));
        map.put("blanchedalmond", new Color(255, 235, 205));
        map.put("blue", new Color( 0, 0, 255));
        map.put("blueviolet", new Color(138, 43, 226));
        map.put("brown", new Color(165, 42, 42));
        map.put("burlywood", new Color(222, 184, 135));
        map.put("cadetblue", new Color( 95, 158, 160));
        map.put("chartreuse", new Color(127, 255, 0));
        map.put("chocolate", new Color(210, 105, 30));
        map.put("coral", new Color(255, 127, 80));
        map.put("cornflowerblue", new Color(100, 149, 237));
        map.put("cornsilk", new Color(255, 248, 220));
        map.put("crimson", new Color(220, 20, 60));
        map.put("cyan", new Color( 0, 255, 255));
        map.put("darkblue", new Color( 0, 0, 139));
        map.put("darkcyan", new Color( 0, 139, 139));
        map.put("darkgoldenrod", new Color(184, 134, 11));
        map.put("darkgray", new Color(169, 169, 169));
        map.put("darkgreen", new Color( 0, 100, 0));
        map.put("darkgrey", new Color(169, 169, 169));
        map.put("darkkhaki", new Color(189, 183, 107));
        map.put("darkmagenta", new Color(139, 0, 139));
        map.put("darkolivegreen", new Color( 85, 107, 47));
        map.put("darkorange", new Color(255, 140, 0));
        map.put("darkorchid", new Color(153, 50, 204));
        map.put("darkred", new Color(139, 0, 0));
        map.put("darksalmon", new Color(233, 150, 122));
        map.put("darkseagreen", new Color(143, 188, 143));
        map.put("darkslateblue", new Color( 72, 61, 139));
        map.put("darkslategray", new Color( 47, 79, 79));
        map.put("darkslategrey", new Color( 47, 79, 79));
        map.put("darkturquoise", new Color( 0, 206, 209));
        map.put("darkviolet", new Color(148, 0, 211));
        map.put("deeppink", new Color(255, 20, 147));
        map.put("deepskyblue", new Color( 0, 191, 255));
        map.put("dimgray", new Color(105, 105, 105));
        map.put("dimgrey", new Color(105, 105, 105));
        map.put("dodgerblue", new Color( 30, 144, 255));
        map.put("firebrick", new Color(178, 34, 34));
        map.put("floralwhite", new Color(255, 250, 240));
        map.put("forestgreen", new Color( 34, 139, 34));
        map.put("fuchsia", new Color(255, 0, 255));
        map.put("gainsboro", new Color(220, 220, 220));
        map.put("ghostwhite", new Color(248, 248, 255));
        map.put("gold", new Color(255, 215, 0));
        map.put("goldenrod", new Color(218, 165, 32));
        map.put("gray", new Color(128, 128, 128));
        map.put("grey", new Color(128, 128, 128));
        map.put("green", new Color( 0, 128, 0));
        map.put("greenyellow", new Color(173, 255, 47));
        map.put("honeydew", new Color(240, 255, 240));
        map.put("hotpink", new Color(255, 105, 180));
        map.put("indianred", new Color(205, 92, 92));
        map.put("indigo", new Color( 75, 0, 130));
        map.put("ivory", new Color(255, 255, 240));
        map.put("khaki", new Color(240, 230, 140));
        map.put("lavender", new Color(230, 230, 250));
        map.put("lavenderblush", new Color(255, 240, 245));
        map.put("lawngreen", new Color(124, 252, 0));
        map.put("lemonchiffon", new Color(255, 250, 205));
        map.put("lightblue", new Color(173, 216, 230));
        map.put("lightcoral", new Color(240, 128, 128));
        map.put("lightcyan", new Color(224, 255, 255));
        map.put("lightgoldenrodyellow", new Color(250, 250, 210));
        map.put("lightgray", new Color(211, 211, 211));
        map.put("lightgreen", new Color(144, 238, 144));
        map.put("lightgrey", new Color(211, 211, 211));
        map.put("lightpink", new Color(255, 182, 193));
        map.put("lightsalmon", new Color(255, 160, 122));
        map.put("lightseagreen", new Color( 32, 178, 170));
        map.put("lightskyblue", new Color(135, 206, 250));
        map.put("lightslategray", new Color(119, 136, 153));
        map.put("lightslategrey", new Color(119, 136, 153));
        map.put("lightsteelblue", new Color(176, 196, 222));
        map.put("lightyellow", new Color(255, 255, 224));
        map.put("lime", new Color( 0, 255, 0));
        map.put("limegreen", new Color( 50, 205, 50));
        map.put("linen", new Color(250, 240, 230));
        map.put("magenta", new Color(255, 0, 255));
        map.put("maroon", new Color(128, 0, 0));
        map.put("mediumaquamarine", new Color(102, 205, 170));
        map.put("mediumblue", new Color( 0, 0, 205));
        map.put("mediumorchid", new Color(186, 85, 211));
        map.put("mediumpurple", new Color(147, 112, 219));
        map.put("mediumseagreen", new Color( 60, 179, 113));
        map.put("mediumslateblue", new Color(123, 104, 238));
        map.put("mediumspringgreen", new Color( 0, 250, 154));
        map.put("mediumturquoise", new Color( 72, 209, 204));
        map.put("mediumvioletred", new Color(199, 21, 133));
        map.put("midnightblue", new Color( 25, 25, 112));
        map.put("mintcream", new Color(245, 255, 250));
        map.put("mistyrose", new Color(255, 228, 225));
        map.put("moccasin", new Color(255, 228, 181));
        map.put("navajowhite", new Color(255, 222, 173));
        map.put("navy", new Color( 0, 0, 128));
        map.put("oldlace", new Color(253, 245, 230));
        map.put("olive", new Color(128, 128, 0));
        map.put("olivedrab", new Color(107, 142, 35));
        map.put("orange", new Color(255, 165, 0));
        map.put("orangered", new Color(255, 69, 0));
        map.put("orchid", new Color(218, 112, 214));
        map.put("palegoldenrod", new Color(238, 232, 170));
        map.put("palegreen", new Color(152, 251, 152));
        map.put("paleturquoise", new Color(175, 238, 238));
        map.put("palevioletred", new Color(219, 112, 147));
        map.put("papayawhip", new Color(255, 239, 213));
        map.put("peachpuff", new Color(255, 218, 185));
        map.put("peru", new Color(205, 133, 63));
        map.put("pink", new Color(255, 192, 203));
        map.put("plum", new Color(221, 160, 221));
        map.put("powderblue", new Color(176, 224, 230));
        map.put("purple", new Color(128, 0, 128));
        map.put("red", new Color(255, 0, 0));
        map.put("rosybrown", new Color(188, 143, 143));
        map.put("royalblue", new Color( 65, 105, 225));
        map.put("saddlebrown", new Color(139, 69, 19));
        map.put("salmon", new Color(250, 128, 114));
        map.put("sandybrown", new Color(244, 164, 96));
        map.put("seagreen", new Color( 46, 139, 87));
        map.put("seashell", new Color(255, 245, 238));
        map.put("sienna", new Color(160, 82, 45));
        map.put("silver", new Color(192, 192, 192));
        map.put("skyblue", new Color(135, 206, 235));
        map.put("slateblue", new Color(106, 90, 205));
        map.put("slategray", new Color(112, 128, 144));
        map.put("slategrey", new Color(112, 128, 144));
        map.put("snow", new Color(255, 250, 250));
        map.put("springgreen", new Color( 0, 255, 127));
        map.put("steelblue", new Color( 70, 130, 180));
        map.put("tan", new Color(210, 180, 140));
        map.put("teal", new Color( 0, 128, 128));
        map.put("thistle", new Color(216, 191, 216));
        map.put("tomato", new Color(255, 99, 71));
        map.put("turquoise", new Color( 64, 224, 208));
        map.put("violet", new Color(238, 130, 238));
        map.put("wheat", new Color(245, 222, 179));
        map.put("white", new Color(255, 255, 255));
        map.put("whitesmoke", new Color(245, 245, 245));
        map.put("yellow", new Color(255, 255, 0));
        map.put("yellowgreen", new Color(154, 205, 50));
        
        // SVG 1.2 Tiny system colors
        map.put("activeborder", UIManager.getColor("activeCaptionBorder"));
        map.put("activecaption", UIManager.getColor("activeCaption"));
        map.put("appworkspace", UIManager.getColor("window"));
        map.put("background", UIManager.getColor("desktop"));
        map.put("buttonface", UIManager.getColor("control"));
        map.put("buttonhighlight", UIManager.getColor("controlHighlight"));
        map.put("buttonshadow", UIManager.getColor("controlShadow"));
        map.put("buttontext", UIManager.getColor("controlText"));
        map.put("captiontext", UIManager.getColor("activeCaptionText"));
        map.put("graytext", UIManager.getColor("textInactiveText"));
        map.put("highlight", UIManager.getColor("textHighlight"));
        map.put("highlighttext", UIManager.getColor("textHighlightText"));
        map.put("inactiveborder", UIManager.getColor("inactiveCaptionBorder"));
        map.put("inactivecaption", UIManager.getColor("inactiveCaption"));
        map.put("inactivecaptiontext", UIManager.getColor("inactiveCaptionText"));
        map.put("infobackground", UIManager.getColor("info"));
        map.put("infotext", UIManager.getColor("infoText"));
        map.put("menu", UIManager.getColor("menu"));
        map.put("menutext", UIManager.getColor("menuText"));
        map.put("scrollbar", UIManager.getColor("scrollbar"));
        map.put("threeddarkshadow", UIManager.getColor("controlDkShadow"));
        map.put("threedface", UIManager.getColor("control"));
        map.put("threedhighlight", UIManager.getColor("controlHighlight"));
        map.put("threedlightshadow", UIManager.getColor("controlLtHighlight"));
        map.put("threedshadow", UIManager.getColor("controlShadow"));
        map.put("window", UIManager.getColor("window"));
        map.put("windowframe", UIManager.getColor("windowBorder"));
        map.put("windowtext", UIManager.getColor("windowText"));
        
        SVG_COLORS = map;
    }
    
    /** Prevent instance creation. */
    private SVGUtil() {
    }
    
    public static AffineTransform getTransform(DOMInput in, String attributeName) throws IOException {
        return getTransform(in.getAttribute(attributeName, ""));
    }
    public static AffineTransform getTransform(String str) throws IOException {
        AffineTransform t = new AffineTransform();
        
        if (str != null) {
            
            StreamTokenizer tt = new StreamTokenizer(new StringReader(str));
            tt.resetSyntax();
            tt.wordChars('a', 'z');
            tt.wordChars('A', 'Z');
            tt.wordChars(128 + 32, 255);
            tt.whitespaceChars(0, ' ');
            tt.whitespaceChars(',', ',');
            tt.parseNumbers();
            
            while (tt.nextToken() != StreamTokenizer.TT_EOF) {
                if (tt.ttype != StreamTokenizer.TT_WORD) {
                    throw new IOException("Illegal transform "+str);
                }
                String type = tt.sval;
                if (tt.nextToken() != '(') {
                    throw new IOException("'(' not found in transform "+str);
                }
                if (type.equals("matrix")) {
                    double[] m = new double[6];
                    for (int i=0; i < 6; i++) {
                        if (tt.nextToken() != StreamTokenizer.TT_NUMBER) {
                            throw new IOException("Matrix value "+i+" not found in transform "+str+" token:"+tt.ttype+" "+tt.sval);
                        }
                        if (tt.nextToken() == StreamTokenizer.TT_WORD && tt.sval.startsWith("E")) {
                            double mantissa = tt.nval;
                            tt.nval = Double.valueOf(tt.nval + tt.sval);
                        } else {
                            tt.pushBack();
                        }
                        m[i] = tt.nval;
                    }
                    t.concatenate(new AffineTransform(m));
                    
                } else if (type.equals("translate")) {
                    double tx, ty;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) {
                        throw new IOException("X-translation value not found in transform "+str);
                    }
                    tx = tt.nval;
                    if (tt.nextToken() == StreamTokenizer.TT_NUMBER) {
                        ty = tt.nval;
                    } else {
                        tt.pushBack();
                        ty = 0;
                    }
                    t.translate(tx, ty);
                    
                } else if (type.equals("scale")) {
                    double sx, sy;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) {
                        throw new IOException("X-scale value not found in transform "+str);
                    }
                    sx = tt.nval;
                    if (tt.nextToken() == StreamTokenizer.TT_NUMBER) {
                        sy = tt.nval;
                    } else {
                        tt.pushBack();
                        sy = sx;
                    }
                    t.scale(sx, sy);
                    
                } else if (type.equals("rotate")) {
                    double angle, cx, cy;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) {
                        throw new IOException("Angle value not found in transform "+str);
                    }
                    angle = tt.nval;
                    if (tt.nextToken() == StreamTokenizer.TT_NUMBER) {
                        cx = tt.nval;
                        if (tt.nextToken() != StreamTokenizer.TT_NUMBER) {
                            throw new IOException("Y-center value not found in transform "+str);
                        }
                        cy = tt.nval;
                    } else {
                        tt.pushBack();
                        cx = cy = 0;
                    }
                    t.rotate(angle * Math.PI / 180d, cx * Math.PI / 180d, cy * Math.PI / 180d);
                    
                    
                } else if (type.equals("skewX")) {
                    double angle;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) {
                        throw new IOException("Skew angle not found in transform "+str);
                    }
                    angle = tt.nval;
                    t.concatenate(new AffineTransform(
                            1, 0, Math.tan(angle * Math.PI / 180), 1, 0, 0
                            ));
                    
                } else if (type.equals("skewY")) {
                    double angle;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) {
                        throw new IOException("Skew angle not found in transform "+str);
                    }
                    angle = tt.nval;
                    t.concatenate(new AffineTransform(
                            1, Math.tan(angle * Math.PI / 180), 0, 1, 0, 0
                            ));
                    
                }else {
                    throw new IOException("Unknown transform "+type+" in "+str);
                }
                if (tt.nextToken() != ')') {
                    throw new IOException("')' not found in transform "+str);
                }
            }
        }
        return t;
    }
    
    public static double getDimension(DOMInput in, String attributeName) throws IOException {
        return getDimensionValue(in, in.getAttribute(attributeName, "0"));
    }
    public static double getDimensionValue(DOMInput in, String str) throws IOException {
        double scaleFactor = 1d;
        
        if (str == null || str.length() == 0) {
            return 0d;
        }
        
        if (str.endsWith("%")) {
            str = str.substring(0, str.length() - 1);
        } else if (str.endsWith("px")) {
            str = str.substring(0, str.length() - 2);
        } else if (str.endsWith("pt")) {
            str = str.substring(0, str.length() - 2);
            scaleFactor = 1.25;
        } else if (str.endsWith("pc")) {
            str = str.substring(0, str.length() - 2);
            scaleFactor = 15;
        } else if (str.endsWith("mm")) {
            str = str.substring(0, str.length() - 2);
            scaleFactor = 3.543307;
        } else if (str.endsWith("cm")) {
            str = str.substring(0, str.length() - 2);
            scaleFactor = 35.43307;
        } else if (str.endsWith("in")) {
            str = str.substring(0, str.length() - 2);
            scaleFactor = 90;
        } else if (str.endsWith("em")) {
            str = str.substring(0, str.length() - 2);
            
            List<Map<String,String>> styles = new ArrayList<Map<String,String>>();
            List<String> values = in.getInheritedAttribute("style");
            for (String v : values) {
                styles.add(getStyles(v));
            }
            String value = getInheritedAttribute("font-size", in, styles);
            if (value != null && ! value.endsWith("em")) {
                scaleFactor = getDimensionValue(in, value);
            }
        }
        
        return Double.parseDouble(str) * scaleFactor;
    }
    
    public static List<BezierPath> getPath(DOMInput in, String attributeName) throws IOException {
        return fromPathData(in.getAttribute(attributeName, ""));
    }
    
    public static void setPath(DOMOutput out, String attributeName, BezierPath path) {
        out.addAttribute(attributeName, toPathData(path));
    }
    
    public static String toPathData(BezierPath path) {
        StringBuilder buf = new StringBuilder();
        
        if (path.size() == 0) {
            // nothing to do
        } else if (path.size() == 1) {
            BezierPath.Node current = path.get(0);
            buf.append("M ");
            buf.append(current.x[0]);
            buf.append(' ');
            buf.append(current.y[0]);
            buf.append(" L ");
            buf.append(current.x[0]);
            buf.append(' ');
            buf.append(current.y[0] + 1);
        } else {
            BezierPath.Node previous;
            BezierPath.Node current;
            
            previous = current = path.get(0);
            buf.append("M ");
            buf.append(current.x[0]);
            buf.append(' ');
            buf.append(current.y[0]);
            for (int i=1, n = path.size(); i < n; i++) {
                previous = current;
                current = path.get(i);
                
                if ((previous.mask & BezierPath.C2_MASK) == 0) {
                    if ((current.mask & BezierPath.C1_MASK) == 0) {
                        buf.append(" L ");
                        buf.append(current.x[0]);
                        buf.append(' ');
                        buf.append(current.y[0]);
                    } else {
                        buf.append(" Q ");
                        buf.append(current.x[1]);
                        buf.append(' ');
                        buf.append(current.y[1]);
                        buf.append(' ');
                        buf.append(current.x[0]);
                        buf.append(' ');
                        buf.append(current.y[0]);
                    }
                } else {
                    if ((current.mask & BezierPath.C1_MASK) == 0) {
                        buf.append(" Q ");
                        buf.append(current.x[2]);
                        buf.append(' ');
                        buf.append(current.y[2]);
                        buf.append(' ');
                        buf.append(current.x[0]);
                        buf.append(' ');
                        buf.append(current.y[0]);
                    } else {
                        buf.append(" C ");
                        buf.append(previous.x[2]);
                        buf.append(' ');
                        buf.append(previous.y[2]);
                        buf.append(' ');
                        buf.append(current.x[1]);
                        buf.append(' ');
                        buf.append(current.y[1]);
                        buf.append(' ');
                        buf.append(current.x[0]);
                        buf.append(' ');
                        buf.append(current.y[0]);
                    }
                }
            }
            if (path.isClosed()) {
                if (path.size() > 1) {
                    previous = path.get(path.size() - 1);
                    current = path.get(0);
                    
                    if ((previous.mask & BezierPath.C2_MASK) == 0) {
                        if ((current.mask & BezierPath.C1_MASK) == 0) {
                            buf.append(" L ");
                            buf.append(current.x[0]);
                            buf.append(' ');
                            buf.append(current.y[0]);
                        } else {
                            buf.append(" Q ");
                            buf.append(current.x[1]);
                            buf.append(' ');
                            buf.append(current.y[1]);
                            buf.append(' ');
                            buf.append(current.x[0]);
                            buf.append(' ');
                            buf.append(current.y[0]);
                        }
                    } else {
                        if ((current.mask & BezierPath.C1_MASK) == 0) {
                            buf.append(" Q ");
                            buf.append(previous.x[2]);
                            buf.append(' ');
                            buf.append(previous.y[2]);
                            buf.append(' ');
                            buf.append(current.x[0]);
                            buf.append(' ');
                            buf.append(current.y[0]);
                        } else {
                            buf.append(" C ");
                            buf.append(previous.x[2]);
                            buf.append(' ');
                            buf.append(previous.y[2]);
                            buf.append(' ');
                            buf.append(current.x[1]);
                            buf.append(' ');
                            buf.append(current.y[1]);
                            buf.append(' ');
                            buf.append(current.x[0]);
                            buf.append(' ');
                            buf.append(current.y[0]);
                        }
                    }
                }
                buf.append(" Z");
            }
        }
        return buf.toString();
    }
    private final static HashSet pathCommands = new HashSet(Arrays.asList(new String[] {
        "M", "m", "Z", "z",
        "L", "l", "H", "h", "V", "v",
        "C", "c", "S", "s",
        "Q", "q", "T", "t"
    }));
    
    public static List<BezierPath> fromPathData(String str) throws IOException {
        LinkedList<BezierPath> paths = new LinkedList<BezierPath>();
        
        BezierPath path = null;
        Point2D.Double p = new Point2D.Double();
        Point2D.Double c1 = new Point2D.Double();
        Point2D.Double c2 = new Point2D.Double();
        StreamTokenizer tt = new StreamTokenizer(new StringReader(str));
        tt.resetSyntax();
        tt.parseNumbers();
        tt.whitespaceChars(0, ' ');
        tt.whitespaceChars(',',',');
        
        char nextCommand = 'M';
        char command = 'M';
        while (tt.nextToken() != StreamTokenizer.TT_EOF) {
            if (tt.ttype > 0) {
                command = (char) tt.ttype;
            } else {
                command = nextCommand;
                tt.pushBack();
            }
            
            BezierPath.Node node;
            switch (command) {
                // moveto
                case 'M' :
                    if (path != null) {
                        paths.add(path);
                    }
                    path = new BezierPath();
                    
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x = tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y = tt.nval;
                    path.moveTo(p.x, p.y);
                    nextCommand = 'L';
                    break;
                case 'm' :
                    if (path != null) {
                        paths.add(path);
                    }
                    path = new BezierPath();
                    
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x += tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y += tt.nval;
                    path.moveTo(p.x, p.y);
                    nextCommand = 'l';
                    
                    // close path
                    break;
                case 'Z' :
                case 'z' :
                    p.x = path.get(0).x[0];
                    p.y = path.get(0).y[0];
                    path.setClosed(true);
                    
                    // lineto
                    break;
                case 'L' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x = tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y = tt.nval;
                    path.lineTo(p.x, p.y);
                    nextCommand = 'L';
                    
                    break;
                case 'l' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x += tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y += tt.nval;
                    path.lineTo(p.x, p.y);
                    nextCommand = 'l';
                    
                    break;
                case 'H' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x = tt.nval;
                    path.lineTo(p.x, p.y);
                    nextCommand = 'H';
                    
                    break;
                case 'h' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x += tt.nval;
                    path.lineTo(p.x, p.y);
                    nextCommand = 'h';
                    
                    break;
                case 'V' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y = tt.nval;
                    path.lineTo(p.x, p.y);
                    nextCommand = 'V';
                    
                    break;
                case 'v' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y += tt.nval;
                    path.lineTo(p.x, p.y);
                    nextCommand = 'v';
                    
                    // curveto
                    break;
                case 'C' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c1.x = tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c1.y = tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c2.x = tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c2.y = tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x = tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y = tt.nval;
                    path.curveTo(c1.x, c1.y, c2.x, c2.y, p.x, p.y);
                    nextCommand = 'C';
                    
                    break;
                case 'c' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c1.x = p.x + tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c1.y = p.y + tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c2.x = p.x + tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c2.y = p.y + tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x += tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y += tt.nval;
                    path.curveTo(c1.x, c1.y, c2.x, c2.y, p.x, p.y);
                    nextCommand = 'c';
                    
                    break;
                case 'S' :
                    node = path.get(path.size() - 1);
                    c1.x = node.x[0] * 2d - node.x[1];
                    c1.y = node.y[0] * 2d - node.y[1];
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c2.x = tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c2.y = tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x = tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y = tt.nval;
                    path.curveTo(c1.x, c1.y, c2.x, c2.y, p.x, p.y);
                    nextCommand = 'S';
                    
                    break;
                case 's' :
                    node = path.get(path.size() - 1);
                    c1.x = node.x[0] * 2d - node.x[1];
                    c1.y = node.y[0] * 2d - node.y[1];
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c2.x = p.x + tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c2.y = p.y + tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x += tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y += tt.nval;
                    path.curveTo(c1.x, c1.y, c2.x, c2.y, p.x, p.y);
                    nextCommand = 's';
                    
                    // quadto
                    break;
                case 'Q' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c1.x = tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c1.y = tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x = tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y = tt.nval;
                    path.quadTo(c1.x, c1.y, p.x, p.y);
                    nextCommand = 'Q';
                    
                    break;
                case 'q' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c1.x = p.x + tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c1.y = p.y + tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x += tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y += tt.nval;
                    path.quadTo(c1.x, c1.y, p.x, p.y);
                    nextCommand = 'q';
                    
                    break;
                case 'T' :
                    node = path.get(path.size() - 1);
                    c1.x = node.x[0] * 2d - node.x[1];
                    c1.y = node.y[0] * 2d - node.y[1];
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x = tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y = tt.nval;
                    path.quadTo(c1.x, c1.y, p.x, p.y);
                    nextCommand = 'T';
                    
                    break;
                case 't' :
                    node = path.get(path.size() - 1);
                    c1.x = node.x[0] * 2d - node.x[1];
                    c1.y = node.y[0] * 2d - node.y[1];
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x += tt.nval;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y += tt.nval;
                    path.quadTo(c1.x, c1.y, p.x, p.y);
                    nextCommand = 's';
                    
                    break;
                default :
                    throw new IOException("Illegal command: "+command);
            }
        }
        if (path != null) {
            paths.add(path);
        }
        return paths;
    }
    
    public static String getInheritedAttribute(String name, DOMInput in, List<Map<String,String>> styles) {
        List<String> values = in.getInheritedAttribute(name);
        for (int i=values.size() - 1; i >= 0; i--) {
            if (values.get(i) != null) {
                return values.get(i);
            }
            if (styles.get(i) != null && styles.get(i).containsKey(name)) {
                return styles.get(i).get(name);
            }
        }
        return null;
    }
    
    /**
     * Reads the attributes from the specified DOMInput and assigns
     * them to the figure.
     */
    public static void readAttributes(Figure f, DOMInput in) throws IOException {
        // FIXME - This method is not working, when "style" and individual attributes
        // are both used in an SVG document.
        List<Map<String,String>> styles = new ArrayList<Map<String,String>>();
        List<String> values = in.getInheritedAttribute("style");
        for (String v : values) {
            styles.add(getStyles(v));
        }
        String value;
        
        // Fill color
        value = getInheritedAttribute("fill", in, styles);
        if (value != null) {
            Color color = getColor(value);
            if (color != INHERIT_COLOR && color != CURRENT_COLOR) {
                FILL_COLOR.set(f, color);
            }
        }
        value = getInheritedAttribute("fill-rule", in, styles);
        if (value != null) {
            WINDING_RULE.set(f, value.toUpperCase().equals("NONZERO") ?
                WindingRule.NON_ZERO : WindingRule.EVEN_ODD);
        } else {
            WINDING_RULE.set(f, WindingRule.NON_ZERO);
        }
        
        // Stroke color
        value = getInheritedAttribute("stroke", in, styles);
        if (value != null) {
            Color color = getColor(value);
            if (color != INHERIT_COLOR && color != CURRENT_COLOR) {
                STROKE_COLOR.set(f, color);
            }
        }
        
        value = getInheritedAttribute("stroke-width", in, styles);
        if (value != null) {
            STROKE_WIDTH.set(f, Double.valueOf(value));
        }
        value = getInheritedAttribute("stroke-miterlimit", in, styles);
        if (value != null) {
            STROKE_MITER_LIMIT_FACTOR.set(f, Double.valueOf(value));
        }
        value = getInheritedAttribute("stroke-dasharray", in, styles);
        if (value != null) {
            StringTokenizer tt = new StringTokenizer(value, " ,");
            double[] dashes = new double[tt.countTokens()];
            for (int i=0, n = dashes.length; i < n; i++) {
                dashes[i] = Double.valueOf(tt.nextToken());
            }
            STROKE_DASHES.set(f, dashes);
        }
        value = getInheritedAttribute("stroke-dashoffset", in, styles);
        if (value != null) {
            STROKE_DASH_PHASE.set(f, Math.abs(Double.valueOf(value)));
        }
        value = getInheritedAttribute("font-size", in, styles);
        if (value != null) {
            FONT_SIZE.set(f, getDimensionValue(in, value));
        }
        value = getInheritedAttribute("text-anchor", in, styles);
        if (value != null) {
            SVGText.TEXT_ANCHOR.set(f, Enum.valueOf(SVGText.TextAnchor.class, value.toUpperCase()));
        }
    }
    public static Map<String,String> getStyles(String str) throws IOException {
        HashMap<String,String> styles = new HashMap<String,String>();
        if (str == null) return styles;
        
        StreamTokenizer tt = new StreamTokenizer(new StringReader(str));
        tt.resetSyntax();
        tt.wordChars('!', '9');
        tt.wordChars('<', '~');
        tt.wordChars(128 + 32, 255);
        tt.whitespaceChars(0, ' ');
        
        while (tt.nextToken() != StreamTokenizer.TT_EOF) {
            if (tt.ttype != ';') {
                String key, value;
                if (tt.ttype != StreamTokenizer.TT_WORD) {
                    throw new IOException("Key token expected in "+str+" "+Integer.toHexString(tt.ttype));
                }
                key = tt.sval;
                if (tt.nextToken() != ':') {
                    throw new IOException("Colon expected after "+key+" in "+str);
                }
                if (tt.nextToken() != StreamTokenizer.TT_WORD) {
                    throw new IOException("Value token expected after "+key+" in "+str+" "+tt.ttype);
                }
                value = tt.sval;
                while (tt.nextToken() == StreamTokenizer.TT_WORD) {
                    value += ' ' + tt.sval;
                }
                tt.pushBack();
                styles.put(key, value);
            }
        }
        
        return styles;
    }
    public static Color getColor(String value) {
        value = value.trim().toLowerCase();
        
        if (value.equals("none")) {
            return null;
        } else if (value.equals("currentColor")) {
            // indicates that painting shall be done using the color specified by the 'color' property.
            return CURRENT_COLOR;
        } else if (value.equals("inherit")) {
            // Each property may also have a specified value of 'inherit', which
            // means that, for a given element, the property takes the same
            // computed value as the property for the element's parent
            return INHERIT_COLOR;
        } else if (SVG_COLORS.containsKey(value)) {
            return SVG_COLORS.get(value);
        } else if (value.startsWith("#") && value.length() == 7) {
            return new Color(Integer.decode(value));
        } else if (value.startsWith("#") && value.length() == 4) {
            // Three digits hex value
            int th = Integer.decode(value);
            return new Color(
                    (th & 0xf) | ((th & 0xf) << 4) |
                    ((th & 0xf0) << 4) | ((th & 0xf0) << 8) |
                    ((th & 0xf00) << 8) | ((th & 0xf00) << 12)
                    );
        } else if (value.startsWith("rgb")) {
            StringTokenizer tt = new StringTokenizer(value,"() ,");
            tt.nextToken();
            Color c = new Color(
                    Integer.decode(tt.nextToken()),
                    Integer.decode(tt.nextToken()),
                    Integer.decode(tt.nextToken())
                    );
            return c;
        } else {
            return null;
        }
    }
    
    public static void setDefaults(Figure f) {
        // Set SVG default values
        
        FILL_COLOR.set(f, Color.black);
        STROKE_COLOR.set(f, null);
        STROKE_DASH_FACTOR.set(f, 1d);
    }
    /**
     * Writes the attributes of the figure into the specified DOMOutput.
     */
    public static void writeAttributes(Figure f, DOMOutput out) throws IOException {
        Color color;
        Double dbl;
        String value;
        
        // Fill attributes
        color = FILL_COLOR.get(f);
        if (color == null) {
            value = "none";
        } else {
            value = "000000"+Integer.toHexString(color.getRGB());
            value = "#"+value.substring(value.length() - 6);
        }
        out.addAttribute("fill", value);
        if (WINDING_RULE.get(f) != WindingRule.NON_ZERO) {
            out.addAttribute("fill-rule", "evenodd");
        }
        
        // Stroke attributes
        color = STROKE_COLOR.get(f);
        if (color == null) {
            value = "none";
        } else {
            value = "000000"+Integer.toHexString(color.getRGB());
            value = "#"+value.substring(value.length() - 6);
        }
        out.addAttribute("stroke", value);
        out.addAttribute("stroke-width", STROKE_WIDTH.get(f), 1d);
        out.addAttribute("stroke-miterlimit", STROKE_MITER_LIMIT_FACTOR.get(f) / STROKE_WIDTH.get(f), 4d);
        double[] dashes = STROKE_DASHES.get(f);
        dbl = (STROKE_DASH_FACTOR.get(f) == null) ? STROKE_WIDTH.get(f) : STROKE_DASH_FACTOR.get(f);
        if (dashes != null) {
            StringBuilder buf = new StringBuilder();
            for (int i=0; i < dashes.length; i++) {
                if (i != 0) { buf.append(',');
                buf.append(dashes[i] * dbl);
                }
                out.addAttribute("stroke-dasharray", buf.toString());
            }
        }
        out.addAttribute("stroke-dashoffset", STROKE_DASH_PHASE.get(f), 0d);
        
        // Text attributes
        out.addAttribute("font-size", FONT_SIZE.get(f));
        //out.addAttribute("text-anchor", "start");
    }
}