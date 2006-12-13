/*
 * @(#)SVGOutputFormat.java  1.0  December 12, 2006
 *
 * Copyright (c) 2006 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.samples.svg.io;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.text.*;
import net.n3.nanoxml.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.io.*;
import org.jhotdraw.samples.svg.*;
import org.jhotdraw.samples.svg.figures.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;
import static org.jhotdraw.samples.svg.SVGConstants.*;
import org.jhotdraw.xml.*;

/**
 * SVGOutputFormat.
 *
 * XXX - Implement me.
 *
 * @author Werner Randelshofer
 * @version 1.0 December 12, 2006 Created.
 */
public class SVGOutputFormat implements OutputFormat {
    private URL url;
    
    /** Creates a new instance. */
    public SVGOutputFormat() {
    }
    public String getMimeType() {
        return "image/svg+xml";
    }
    
    public javax.swing.filechooser.FileFilter getFileFilter() {
        return new ExtensionFileFilter("Scalable Vector Graphics (SVG)", "svg");
    }
    
    public JComponent getOutputFormatAccessory() {
        return null;
    }
    
    public void write(URL url, OutputStream out, Drawing drawing, Collection<Figure> figures) throws IOException {
        this.url = url;
        XMLElement svg;
        svg = new XMLElement("svg", SVG_NAMESPACE);
        svg.setAttribute("xmlns:xlink","http://www.w3.org/1999/xlink");
        
        for (Figure f: figures) {
            writeElement(svg, f);
        }
        
        XMLWriter writer = new XMLWriter(out);
        writer.write(svg, true);
    }
    public void writeElement(XMLElement parent, Figure f) throws IOException {
        if (f instanceof SVGEllipse) {
            SVGEllipse ellipse = (SVGEllipse) f;
            if (ellipse.getWidth() == ellipse.getHeight()) {
                writeCircleElement(parent, ellipse);
            } else {
                writeEllipseElement(parent, ellipse);
            }
        } else if (f instanceof SVGGroup) {
            writeGElement(parent, (SVGGroup) f);
        } else if (f instanceof SVGImage) {
            writeImageElement(parent, (SVGImage)  f);
        } else if (f instanceof SVGPath) {
            SVGPath path = (SVGPath) f;
            if (path.getChildCount() == 1) {
                BezierFigure bezier = (BezierFigure) path.getChild(0);
                boolean isLinear = true;
                for (int i=0, n = bezier.getNodeCount(); i < n; i++) {
                    if (bezier.getNode(i).getMask() != 0) {
                        isLinear = false;
                        break;
                    }
                }
                if (isLinear) {
                    if (bezier.isClosed()) {
                        writePolygonElement(parent, path);
                    } else {
                        if (bezier.getNodeCount() == 2) {
                            writeLineElement(parent, path);
                        } else  {
                            writePolylineElement(parent, path);
                        }
                    }
                } else {
                    writePathElement(parent, path);
                }
            } else {
                writePathElement(parent, path);
            }
        } else if (f instanceof SVGRect) {
            writeRectElement(parent, (SVGRect) f);
        } else if (f instanceof SVGText) {
            writeTextElement(parent, (SVGText) f);
        } else if (f instanceof SVGTextArea) {
            writeTextAreaElement(parent, (SVGTextArea) f);
        } else {
            System.out.println("Unable to write: "+f);
        }
    }
    public void writeCircleElement(XMLElement parent, SVGEllipse f) throws IOException {
        XMLElement elem = new XMLElement("circle");
        writeAttribute(elem, "cx", f.getX() + f.getWidth() / 2d, 0d);
        writeAttribute(elem, "cy", f.getY() + f.getHeight() / 2d, 0d);
        writeAttribute(elem, "r", f.getWidth() / 2d, 0d);
        writeShapeAttributes(elem, f);
        parent.addChild(elem);
    }
    public void writeEllipseElement(XMLElement parent, SVGEllipse f) throws IOException {
        XMLElement elem = new XMLElement("ellipse");
        writeAttribute(elem, "cx", f.getX() + f.getWidth() / 2d, 0d);
        writeAttribute(elem, "cy", f.getY() + f.getHeight() / 2d, 0d);
        writeAttribute(elem, "rx", f.getWidth() / 2d, 0d);
        writeAttribute(elem, "ry", f.getHeight() / 2d, 0d);
        writeShapeAttributes(elem, f);
        parent.addChild(elem);
    }
    public void writeGElement(XMLElement parent, SVGGroup f) throws IOException {
        XMLElement elem = new XMLElement("g");
        for (Figure child : f.getChildren()) {
            writeElement(elem, child);
        }
        parent.addChild(elem);
    }
    public void writeImageElement(XMLElement parent, SVGImage f) throws IOException {
        XMLElement elem = new XMLElement("image");
        writeAttribute(elem, "x", f.getX(), 0d);
        writeAttribute(elem, "y", f.getY(), 0d);
        writeAttribute(elem, "width", f.getWidth(), 0d);
        writeAttribute(elem, "height", f.getHeight(), 0d);
       writeAttribute(elem, "xlink:href", "data:image;base64,"+Base64.encodeBytes(f.getImageData()/*, Base64.DONT_BREAK_LINES*/), "");
       // writeShapeAttributes(elem, f);
        parent.addChild(elem);
    }
    public void writePathElement(XMLElement parent, SVGPath f) throws IOException {
        XMLElement elem = new XMLElement("path");
        writeShapeAttributes(elem, f);
        parent.addChild(elem);
    }
    public void writePolygonElement(XMLElement parent, SVGPath f) throws IOException {
        XMLElement elem = new XMLElement("polygon");
        writeShapeAttributes(elem, f);
        parent.addChild(elem);
    }
    public void writePolylineElement(XMLElement parent, SVGPath f) throws IOException {
        XMLElement elem = new XMLElement("polyline");
        writeShapeAttributes(elem, f);
        parent.addChild(elem);
    }
    public void writeLineElement(XMLElement parent, SVGPath f) throws IOException {
        XMLElement elem = new XMLElement("line");
        BezierFigure bezier = (BezierFigure) f.getChild(0);
        writeAttribute(elem, "x1", bezier.getNode(0).x[0], 0d);
        writeAttribute(elem, "y1", bezier.getNode(0).y[0], 0d);
        writeAttribute(elem, "x2", bezier.getNode(1).x[0], 0d);
        writeAttribute(elem, "y2", bezier.getNode(1).y[0], 0d);
        writeShapeAttributes(elem, f);
        parent.addChild(elem);
    }
    public void writeRectElement(XMLElement parent, SVGRect f) throws IOException {
        XMLElement elem = new XMLElement("rect");
        writeAttribute(elem, "x", f.getX(), 0d);
        writeAttribute(elem, "y", f.getY(), 0d);
        writeAttribute(elem, "width", f.getWidth(), 0d);
        writeAttribute(elem, "height", f.getHeight(), 0d);
        writeAttribute(elem, "rx", f.getArcWidth(), 0d);
        writeAttribute(elem, "ry", f.getArcHeight(), 0d);
        writeShapeAttributes(elem, f);
        parent.addChild(elem);
    }
    public void writeTextElement(XMLElement parent, SVGText f) throws IOException {
        
    }
    public void writeTextAreaElement(XMLElement parent, SVGTextArea f) throws IOException {
        
    }
    // ------------
    // Attributes
    // ------------
    /* Reads shape attributes.
     */
    private void writeShapeAttributes(XMLElement elem, Figure f)
    throws IOException {
        Color color;
        String value;
        int intValue;
        
        //'color'
        // Value:  	<color> | inherit
        // Initial:  	 depends on user agent
        // Applies to:  	None. Indirectly affects other properties via currentColor
        // Inherited:  	 yes
        // Percentages:  	 N/A
        // Media:  	 visual
        // Animatable:  	 yes
        // Computed value:  	 Specified <color> value, except inherit
        //
        // value = readInheritAttribute(elem, "color", "black");
        // System.out.println("color="+value);
        
        //'color-rendering'
        // Value:  	 auto | optimizeSpeed | optimizeQuality | inherit
        // Initial:  	 auto
        // Applies to:  	 container elements , graphics elements and 'animateColor'
        // Inherited:  	 yes
        // Percentages:  	 N/A
        // Media:  	 visual
        // Animatable:  	 yes
        // Computed value:  	 Specified value, except inherit
        //
        // value = readInheritAttribute(elem, "color-rendering", "auto");
        // System.out.println("color-rendering="+value);
        
        // 'fill'
        // Value:  	<paint> | inherit (See Specifying paint)
        // Initial:  	 black
        // Applies to:  	 shapes and text content elements
        // Inherited:  	 yes
        // Percentages:  	 N/A
        // Media:  	 visual
        // Animatable:  	 yes
        // Computed value:  	 "none", system paint, specified <color> value or absolute IRI
        
        // XXX - Write paint if we have a gradient!
        color = FILL_COLOR.get(f);
        if (color == null) {
            writeAttribute(elem, "fill", "none", "black");
        } else if (! color.equals(Color.black)) {
            value = "000000"+Integer.toHexString(color.getRGB());
            value = "#"+value.substring(value.length() - 6);
            if (value.substring(1,4).equals(value.substring(4))) {
                value = value.substring(0, 4);
            }
            writeAttribute(elem, "fill", value, "#000");
        }
        
        //'fill-opacity'
        //Value:  	 <opacity-value> | inherit
        //Initial:  	 1
        //Applies to:  	 shapes and text content elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        color = FILL_COLOR.get(f);
        if (color != null) {
            int alpha = color.getAlpha();
            if (alpha != 255) {
                writeAttribute(elem, "fill-opacity", alpha / 255d, 1d);
            }
        }
        
        // 'fill-rule'
        // Value:	 nonzero | evenodd | inherit
        // Initial: 	 nonzero
        // Applies to:  	 shapes and text content elements
        // Inherited:  	 yes
        // Percentages:  	 N/A
        // Media:  	 visual
        // Animatable:  	 yes
        // Computed value:  	 Specified value, except inherit
        if (WINDING_RULE.get(f) != WindingRule.NON_ZERO) {
            writeAttribute(elem, "fill-rule", "evenodd", "nonzero");
        }
        
        //'stroke'
        //Value:  	<paint> | inherit (See Specifying paint)
        //Initial:  	 none
        //Applies to:  	 shapes and text content elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 "none", system paint, specified <color> value
        // or absolute IRI
        
        // XXX - Write paint if we have a gradient!
        color = STROKE_COLOR.get(f);
        if (color != null) {
            value = "000000"+Integer.toHexString(color.getRGB());
            value = "#"+value.substring(value.length() - 6);
            if (value.substring(1,4).equals(value.substring(4))) {
                value = value.substring(0, 4);
            }
            writeAttribute(elem, "stroke", value, "none");
        }
        
        //'stroke-dasharray'
        //Value:  	 none | <dasharray> | inherit
        //Initial:  	 none
        //Applies to:  	 shapes and text content elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes (non-additive)
        //Computed value:  	 Specified value, except inherit
        
        // XXX - Implement me
        /*
        value = readInheritAttribute(elem, "stroke-dasharray", "none");
        if (! value.equals("none")) {
            String[] values = toCommaSeparatedArray(elem, value);
            double[] dashes = new double[values.length];
            for (int i=0; i < values.length; i++) {
                dashes[i] = toNumber(elem, values[i]);
            }
            a.put(SVGAttributeKeys.STROKE_DASHES, dashes);
        }*/
        
        //'stroke-dashoffset'
        //Value:  	<length> | inherit
        //Initial:  	 0
        //Applies to:  	 shapes and text content elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        
        // XXX - Implement me
        /*
        doubleValue = toNumber(elem, readInheritAttribute(elem, "stroke-dashoffset", "0"));
        a.put(SVGAttributeKeys.STROKE_DASH_PHASE, doubleValue);
        a.put(SVGAttributeKeys.IS_STROKE_DASH_FACTOR, false);
         */
        
        //'stroke-linecap'
        //Value:  	 butt | round | square | inherit
        //Initial:  	 butt
        //Applies to:  	 shapes and text content elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        
        // XXX - Implement me
        /*
        value = readInheritAttribute(elem, "stroke-linecap", "butt");
        a.put(SVGAttributeKeys.STROKE_CAP, strokeLinecapMap.get(value));
         */
        
        //'stroke-linejoin'
        //Value:  	 miter | round | bevel | inherit
        //Initial:  	 miter
        //Applies to:  	 shapes and text content elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        
        // XXX - Implement me
        /*
        value = readInheritAttribute(elem, "stroke-linejoin", "miter");
        a.put(SVGAttributeKeys.STROKE_JOIN, strokeLinejoinMap.get(value));
         */
        
        //'stroke-miterlimit'
        //Value:  	 <miterlimit> | inherit
        //Initial:  	 4
        //Applies to:  	 shapes and text content elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        
        // XXX - Implement me
        /*
        doubleValue = toDouble(elem, readInheritAttribute(elem, "stroke-miterlimit", "4"));
        a.put(SVGAttributeKeys.STROKE_MITER_LIMIT, doubleValue);
        a.put(SVGAttributeKeys.IS_STROKE_MITER_LIMIT_FACTOR, false);
         */
        
        //'stroke-opacity'
        //Value:  	 <opacity-value> | inherit
        //Initial:  	 1
        //Applies to:  	 shapes and text content elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        color = STROKE_COLOR.get(f);
        if (color != null) {
            int alpha = color.getAlpha();
            if (alpha != 255) {
                writeAttribute(elem, "stroke-opacity", alpha / 255d, 1d);
            }
        }
        
        //'stroke-width'
        //Value:  	<length> | inherit
        //Initial:  	 1
        //Applies to:  	 shapes and text content elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        writeAttribute(elem, "stroke-width", STROKE_WIDTH.get(f), 1d);
    }
    
    protected void writeAttribute(XMLElement elem, String name, String value, String defaultValue) {
        writeAttribute(elem, name, SVG_NAMESPACE, value, defaultValue);
    }
    protected void writeAttribute(XMLElement elem, String name, String namespace, String value, String defaultValue) {
        if (! value.equals(defaultValue)) {
            elem.setAttribute(name, value);
        }
    }
    protected void writeAttribute(XMLElement elem, String name, double value, double defaultValue) {
        writeAttribute(elem, name, SVG_NAMESPACE, value, defaultValue);
    }
    protected void writeAttribute(XMLElement elem, String name, String namespace, double value, double defaultValue) {
        if (value != defaultValue) {
            String str = Double.toString(value);
            if (str.endsWith(".0")) {
                str = str.substring(0, str.length() -  2);
            }
            elem.setAttribute(name, str);
        }
    }
}
