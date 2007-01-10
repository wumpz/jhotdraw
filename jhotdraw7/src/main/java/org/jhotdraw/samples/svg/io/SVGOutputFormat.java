/*
 * @(#)SVGOutputFormat.java  1.0  December 12, 2006
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.samples.svg.io;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.nio.Buffer;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.text.*;
import net.n3.nanoxml.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.OutputFormat;
import org.jhotdraw.geom.*;
import org.jhotdraw.gui.datatransfer.*;
import org.jhotdraw.io.*;
import org.jhotdraw.samples.svg.*;
import org.jhotdraw.samples.svg.figures.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;
import static org.jhotdraw.samples.svg.SVGConstants.*;
import org.jhotdraw.xml.*;

/**
 * An output format for storing drawings as Scalable Vector Graphics SVG Tiny 1.2.
 *
 * XXX - Implement me.
 *
 * @author Werner Randelshofer
 * @version 1.0 December 12, 2006 Created.
 */
public class SVGOutputFormat implements OutputFormat {
    private URL url;
    
    /**
     * This is a counter used to create the next unique identification.
     */
    private int nextId;
    
    /**
     * In this hash map we store all elements to which we have assigned
     * an id.
     */
    private HashMap<IXMLElement,String> identifiedElements;
    
    /**
     * This element holds all definitions of the SVG file.
     */
    private IXMLElement defs;
    
    /**
     * Holds the document that is currently being written.
     */
    private IXMLElement document;
    
    private final static HashMap<Integer, String> strokeLinejoinMap;
    static {
        strokeLinejoinMap = new HashMap<Integer, String>();
        strokeLinejoinMap.put(BasicStroke.JOIN_MITER, "miter");
        strokeLinejoinMap.put(BasicStroke.JOIN_ROUND, "round");
        strokeLinejoinMap.put(BasicStroke.JOIN_BEVEL, "bevel");
    }
    private final static HashMap<Integer, String> strokeLinecapMap;
    static {
        strokeLinecapMap = new HashMap<Integer, String>();
        strokeLinecapMap.put(BasicStroke.CAP_BUTT, "butt");
        strokeLinecapMap.put(BasicStroke.CAP_ROUND, "round");
        strokeLinecapMap.put(BasicStroke.CAP_SQUARE, "square");
    }
    
    
    
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
    
    protected void writeElement(IXMLElement parent, Figure f) throws IOException {
        if (f instanceof SVGEllipseFigure) {
            SVGEllipseFigure ellipse = (SVGEllipseFigure) f;
            if (ellipse.getWidth() == ellipse.getHeight()) {
                writeCircleElement(parent, ellipse);
            } else {
                writeEllipseElement(parent, ellipse);
            }
        } else if (f instanceof SVGGroupFigure) {
            writeGElement(parent, (SVGGroupFigure) f);
        } else if (f instanceof SVGImageFigure) {
            writeImageElement(parent, (SVGImageFigure)  f);
        } else if (f instanceof SVGPathFigure) {
            SVGPathFigure path = (SVGPathFigure) f;
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
        } else if (f instanceof SVGRectFigure) {
            writeRectElement(parent, (SVGRectFigure) f);
        } else if (f instanceof SVGTextFigure) {
            writeTextElement(parent, (SVGTextFigure) f);
        } else if (f instanceof SVGTextAreaFigure) {
            writeTextAreaElement(parent, (SVGTextAreaFigure) f);
        } else {
            System.out.println("Unable to write: "+f);
        }
    }
    protected void writeCircleElement(IXMLElement parent, SVGEllipseFigure f) throws IOException {
        parent.addChild(
                createCircle(
                document,
                f.getX() + f.getWidth() / 2d,
                f.getY() + f.getHeight() / 2d,
                f.getWidth() / 2d,
                f.getAttributes()
                )
                );
    }
    
    protected IXMLElement createCircle(IXMLElement doc,
            double cx, double cy, double r,
            Map<AttributeKey,Object> attributes) throws IOException {
        IXMLElement elem = doc.createElement("circle");
        writeAttribute(elem, "cx", cx, 0d);
        writeAttribute(elem, "cy", cy, 0d);
        writeAttribute(elem, "r", r, 0d);
        writeShapeAttributes(elem, attributes);
        writeTransformAttribute(elem, attributes);
        return elem;
    }
    
    
    protected IXMLElement createG(IXMLElement doc,
            Map<AttributeKey,Object> attributes) {
        IXMLElement elem = doc.createElement("g");
        return elem;
    }
    
    
    protected IXMLElement createLinearGradient(IXMLElement doc,
            double x1, double y1, double x2, double y2,
            double[] stopOffsets, Color[] stopColors,
            boolean isRelativeToFigureBounds) throws IOException {
        IXMLElement elem = doc.createElement("linearGradient");
        
        writeAttribute(elem, "x1", toNumber(x1), "0");
        writeAttribute(elem, "y1", toNumber(y1), "0");
        writeAttribute(elem, "x2", toNumber(x2), "1");
        writeAttribute(elem, "y2", toNumber(y2), "0");
        writeAttribute(elem, "gradientUnits", 
                (isRelativeToFigureBounds) ? "objectBoundingBox" : "useSpaceOnUse",
                "objectBoundingBox"
                );
        
        for (int i=0; i < stopOffsets.length; i++) {
            IXMLElement stop = new XMLElement("stop");
            writeAttribute(stop, "offset", toNumber(stopOffsets[i]), null);
            writeAttribute(stop, "stop-color", toColor(stopColors[i]), null);
            writeAttribute(stop, "stop-opacity", toNumber(stopColors[i].getAlpha() / 255d), "1");
            elem.addChild(stop);
        }
        
        return elem;
    }
    
    protected IXMLElement createRadialGradient(IXMLElement doc,
            double cx, double cy, double r,
            double[] stopOffsets, Color[] stopColors,
            boolean isRelativeToFigureBounds) throws IOException {
        IXMLElement elem = doc.createElement("radialGradient");

        writeAttribute(elem, "cx", toNumber(cx), "0.5");
        writeAttribute(elem, "cy", toNumber(cy), "0.5");
        writeAttribute(elem, "r", toNumber(r), "0.5");
        writeAttribute(elem, "gradientUnits", 
                (isRelativeToFigureBounds) ? "objectBoundingBox" : "useSpaceOnUse",
                "objectBoundingBox"
                );
        
        for (int i=0; i < stopOffsets.length; i++) {
            IXMLElement stop = new XMLElement("stop");
            writeAttribute(stop, "offset", toNumber(stopOffsets[i]), null);
            writeAttribute(stop, "stop-color", toColor(stopColors[i]), null);
            writeAttribute(stop, "stop-opacity", toNumber(stopColors[i].getAlpha() / 255d), "1");
            elem.addChild(stop);
        }
        
        return elem;
    }
    
    protected void writeEllipseElement(IXMLElement parent, SVGEllipseFigure f) throws IOException {
        parent.addChild(createEllipse(
                document,
                f.getX() + f.getWidth() / 2d,
                f.getY() + f.getHeight() / 2d,
                f.getWidth() / 2d,
                f.getHeight() / 2d,
                f.getAttributes()
                ));
    }
    protected IXMLElement createEllipse(IXMLElement doc,
            double cx, double cy, double rx, double ry,
            Map<AttributeKey,Object> attributes) throws IOException {
        IXMLElement elem = doc.createElement("ellipse");
        writeAttribute(elem, "cx", cx, 0d);
        writeAttribute(elem, "cy", cy, 0d);
        writeAttribute(elem, "rx", rx, 0d);
        writeAttribute(elem, "ry", ry, 0d);
        writeShapeAttributes(elem, attributes);
        writeTransformAttribute(elem, attributes);
        return elem;
    }
    protected void writeGElement(IXMLElement parent, SVGGroupFigure f) throws IOException {
        IXMLElement elem = document.createElement("g");
        for (Figure child : f.getChildren()) {
            writeElement(elem, child);
        }
        parent.addChild(elem);
    }
    protected void writeImageElement(IXMLElement parent, SVGImageFigure f) throws IOException {
        parent.addChild(
                createImage(document,
                f.getX(),
                f.getY(),
                f.getWidth(),
                f.getHeight(),
                f.getImageData(),
                f.getAttributes()
                ));
    }
    protected IXMLElement createImage(IXMLElement doc,
            double x, double y, double w, double h,
            byte[] imageData,
            Map<AttributeKey,Object> attributes) throws IOException {
        IXMLElement elem = doc.createElement("image");
        writeAttribute(elem, "x", x, 0d);
        writeAttribute(elem, "y", y, 0d);
        writeAttribute(elem, "width", w, 0d);
        writeAttribute(elem, "height", h, 0d);
        writeAttribute(elem, "xlink:href", "data:image;base64,"+Base64.encodeBytes(imageData), "");
        writeTransformAttribute(elem, attributes);
        return elem;
    }
    
    protected void writePathElement(IXMLElement parent, SVGPathFigure f) throws IOException {
        BezierPath[] beziers = new BezierPath[f.getChildCount()];
        for (int i=0; i < beziers.length; i++) {
            beziers[i] = ((BezierFigure) f.getChild(i)).getBezierPath();
        }
        parent.addChild(createPath(
                document,
                beziers,
                f.getAttributes()
                ));
    }
    protected IXMLElement createPath(IXMLElement doc,
            BezierPath[] beziers,
            Map<AttributeKey,Object> attributes) throws IOException {
        IXMLElement elem = doc.createElement("path");
        writeShapeAttributes(elem, attributes);
        writeTransformAttribute(elem, attributes);
        writeAttribute(elem, "d", toPath(beziers), null);
        return elem;
    }
    
    protected void writePolygonElement(IXMLElement parent, SVGPathFigure f) throws IOException {
        LinkedList<Point2D.Double> points = new LinkedList<Point2D.Double>();
        BezierPath[] beziers = new BezierPath[f.getChildCount()];
        for (int i=0, n = f.getChildCount(); i < n; i++) {
            BezierPath bezier = ((BezierFigure) f.getChild(i)).getBezierPath();
            for (BezierPath.Node node: bezier) {
                points.add(new Point2D.Double(node.x[0], node.y[0]));
            }
        }
        
        parent.addChild(createPolygon(
                document,
                points.toArray(new Point2D.Double[points.size()]),
                f.getAttributes()
                ));
    }
    protected IXMLElement createPolygon(IXMLElement doc,
            Point2D.Double[] points,
            Map<AttributeKey,Object> attributes)
            throws IOException {
        IXMLElement elem = doc.createElement("polygon");
        writeAttribute(elem, "points", toPoints(points), null);
        writeShapeAttributes(elem, attributes);
        writeTransformAttribute(elem, attributes);
        return elem;
    }
    
    protected void writePolylineElement(IXMLElement parent, SVGPathFigure f) throws IOException {
        LinkedList<Point2D.Double> points = new LinkedList<Point2D.Double>();
        BezierPath[] beziers = new BezierPath[f.getChildCount()];
        for (int i=0, n = f.getChildCount(); i < n; i++) {
            BezierPath bezier = ((BezierFigure) f.getChild(i)).getBezierPath();
            for (BezierPath.Node node: bezier) {
                points.add(new Point2D.Double(node.x[0], node.y[0]));
            }
        }
        
        parent.addChild(createPolyline(
                document,
                points.toArray(new Point2D.Double[points.size()]),
                f.getAttributes()
                ));
    }
    protected IXMLElement createPolyline(IXMLElement doc,
            Point2D.Double[] points,
            Map<AttributeKey,Object> attributes) throws IOException {
        
        IXMLElement elem = doc.createElement("polyline");
        writeAttribute(elem, "points", toPoints(points), null);
        writeShapeAttributes(elem, attributes);
        writeTransformAttribute(elem, attributes);
        return elem;
    }
    
    
    protected void writeLineElement(IXMLElement parent, SVGPathFigure f)
    throws IOException {
        BezierFigure bezier = (BezierFigure) f.getChild(0);
        parent.addChild(createLine(
                document,
                bezier.getNode(0).x[0],
                bezier.getNode(0).y[0],
                bezier.getNode(1).x[0],
                bezier.getNode(1).y[0],
                f.getAttributes()
                )
                );
    }
    protected IXMLElement createLine(IXMLElement doc,
            double x1, double y1, double x2, double y2,
            Map<AttributeKey,Object> attributes) throws IOException {
        IXMLElement elem = doc.createElement("line");
        writeAttribute(elem, "x1", x1, 0d);
        writeAttribute(elem, "y1", y1, 0d);
        writeAttribute(elem, "x2", x2, 0d);
        writeAttribute(elem, "y2", y2, 0d);
        writeShapeAttributes(elem, attributes);
        writeTransformAttribute(elem, attributes);
        return elem;
    }
    
    protected void writeRectElement(IXMLElement parent, SVGRectFigure f) throws IOException {
        parent.addChild(
                createRect(
                document,
                f.getX(),
                f.getY(),
                f.getWidth(),
                f.getHeight(),
                f.getArcWidth(),
                f.getArcHeight(),
                f.getAttributes()
                )
                );
    }
    protected IXMLElement createRect(IXMLElement doc,
            double x, double y, double width, double height,
            double rx, double ry,
            Map<AttributeKey,Object> attributes)
            throws IOException {
        IXMLElement elem = doc.createElement("rect");
        writeAttribute(elem, "x", x, 0d);
        writeAttribute(elem, "y", y, 0d);
        writeAttribute(elem, "width", width, 0d);
        writeAttribute(elem, "height", height, 0d);
        writeAttribute(elem, "rx", rx, 0d);
        writeAttribute(elem, "ry", ry, 0d);
        writeShapeAttributes(elem, attributes);
        writeTransformAttribute(elem, attributes);
        return elem;
    }
    
    protected void writeTextElement(IXMLElement parent, SVGTextFigure f) throws IOException {
        DefaultStyledDocument styledDoc = new DefaultStyledDocument();
        try {
            styledDoc.insertString(0, f.getText(), null);
        } catch (BadLocationException e) {
            InternalError error = new InternalError(e.getMessage());
            error.initCause(e);
            throw error;
        }
        parent.addChild(
                createText(
                document,
                f.getCoordinates(),
                f.getRotates(),
                styledDoc,
                f.getAttributes()
                )
                );
    }
    protected IXMLElement createText(IXMLElement doc,
            Point2D.Double[] coordinates, double[] rotate,
            StyledDocument text,
            Map<AttributeKey,Object> attributes) throws IOException {
        IXMLElement elem = doc.createElement("text");
        StringBuilder bufX = new StringBuilder();
        StringBuilder bufY = new StringBuilder();
        for (int i=0; i < coordinates.length; i++) {
            if (i != 0) {
                bufX.append(',');
                bufY.append(',');
            }
            bufX.append(toNumber(coordinates[i].getX()));
            bufY.append(toNumber(coordinates[i].getY()));
        }
        StringBuilder bufR = new StringBuilder();
        if (rotate != null) {
            for (int i=0; i < rotate.length; i++) {
                if (i != 0) {
                    bufR.append(',');
                }
                bufR.append(toNumber(rotate[i]));
            }
        }
        writeAttribute(elem, "x", bufX.toString(), "0");
        writeAttribute(elem, "y", bufY.toString(), "0");
        writeAttribute(elem, "rotate", bufR.toString(), "");
        String str;
        try {
            str = text.getText(0, text.getLength());
        } catch (BadLocationException e) {
            InternalError error = new InternalError(e.getMessage());
            error.initCause(e);
            throw error;
        }
        
        elem.setContent(str);
        
        writeShapeAttributes(elem, attributes);
        writeTransformAttribute(elem, attributes);
        writeFontAttributes(elem, attributes);
        return elem;
    }
    protected void writeTextAreaElement(IXMLElement parent, SVGTextAreaFigure f) throws IOException {
        
    }
    protected IXMLElement createTextArea(IXMLElement doc,
            double x, double y, double w, double h,
            StyledDocument text,
            Map<AttributeKey,Object> attributes)
            throws IOException {
        IXMLElement elem = doc.createElement("textArea");
        writeAttribute(elem, "x", toNumber(x), "0");
        writeAttribute(elem, "y", toNumber(y), "0");
        writeAttribute(elem, "width", toNumber(w), "0");
        writeAttribute(elem, "height", toNumber(h), "0");
        
        String str;
        try {
            str = text.getText(0, text.getLength());
        } catch (BadLocationException e) {
            InternalError error = new InternalError(e.getMessage());
            error.initCause(e);
            throw error;
        }
        String[] lines = str.split("\n");
        for (int i=0; i < lines.length; i++) {
            if (i != 0) {
                elem.addChild(doc.createElement("tbreak"));
            }
            elem.setContent(lines[i]);
        }
        
        writeShapeAttributes(elem, attributes);
        writeTransformAttribute(elem, attributes);
        writeFontAttributes(elem, attributes);
        return elem;
    }
    
    // ------------
    // Attributes
    // ------------
    /* Reads shape attributes.
     */
    protected void writeShapeAttributes(IXMLElement elem, Map<AttributeKey,Object> f)
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
        // Nothing to do: Attribute 'color' is not needed.
        
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
        // Nothing to do: Attribute 'color-rendering' is not needed.
        
        // 'fill'
        // Value:  	<paint> | inherit (See Specifying paint)
        // Initial:  	 black
        // Applies to:  	 shapes and text content elements
        // Inherited:  	 yes
        // Percentages:  	 N/A
        // Media:  	 visual
        // Animatable:  	 yes
        // Computed value:  	 "none", system paint, specified <color> value or absolute IRI
        Object gradient = FILL_GRADIENT.get(f);
        if (gradient != null) {
            IXMLElement gradientElem;
            if (gradient instanceof LinearGradient) {
                LinearGradient lg = (LinearGradient) gradient;
                gradientElem = createLinearGradient(document,
                        lg.getX1(), lg.getY1(),
                        lg.getX2(), lg.getY2(),
                        lg.getStopOffsets(),
                        lg.getStopColors(),
                        lg.isRelativeToFigureBounds()
                        );
            } else /*if (gradient instanceof RadialGradient)*/ {
                RadialGradient rg = (RadialGradient) gradient;
                gradientElem = createRadialGradient(document,
                        rg.getCX(), rg.getCY(),
                        rg.getR(),
                        rg.getStopOffsets(),
                        rg.getStopColors(),
                        rg.isRelativeToFigureBounds()
                        );
            }
            String id = getId(gradientElem);
            gradientElem.setAttribute("id","xml",id);
            defs.addChild(gradientElem);
            writeAttribute(elem, "fill", "url(#"+id+")", "#000");
        } else {
            writeAttribute(elem, "fill", toColor(FILL_COLOR.get(f)), "#000");
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
        writeAttribute(elem, "fill-opacity", FILL_OPACITY.get(f), 1d);
        
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
        gradient = STROKE_GRADIENT.get(f);
        if (gradient != null) {
            IXMLElement gradientElem;
            if (gradient instanceof LinearGradient) {
                LinearGradient lg = (LinearGradient) gradient;
                gradientElem = createLinearGradient(document,
                        lg.getX1(), lg.getY1(),
                        lg.getX2(), lg.getY2(),
                        lg.getStopOffsets(),
                        lg.getStopColors(),
                        lg.isRelativeToFigureBounds()
                        );
            } else /*if (gradient instanceof RadialGradient)*/ {
                RadialGradient rg = (RadialGradient) gradient;
                gradientElem = createRadialGradient(document,
                        rg.getCX(), rg.getCY(),
                        rg.getR(),
                        rg.getStopOffsets(),
                        rg.getStopColors(),
                        rg.isRelativeToFigureBounds()
                        );
            }
            String id = getId(gradientElem);
            gradientElem.setAttribute("id","xml",id);
            defs.addChild(gradientElem);
            writeAttribute(elem, "stroke", "url(#"+id+")", "none");
        } else {
            writeAttribute(elem, "stroke", toColor(STROKE_COLOR.get(f)), "none");
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
        double[] dashes = STROKE_DASHES.get(f);
        if (dashes != null) {
            StringBuilder buf = new StringBuilder();
            for (int i=0; i < dashes.length; i++) {
                if (i != 0) {
                    buf.append(',');
                }
                buf.append(toNumber(dashes[i]));
            }
            writeAttribute(elem, "stroke-dasharray", buf.toString(), null);
        }
        
        //'stroke-dashoffset'
        //Value:  	<length> | inherit
        //Initial:  	 0
        //Applies to:  	 shapes and text content elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        writeAttribute(elem, "stroke-dashoffset", STROKE_DASH_PHASE.get(f), 0d);
        
        //'stroke-linecap'
        //Value:  	 butt | round | square | inherit
        //Initial:  	 butt
        //Applies to:  	 shapes and text content elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        writeAttribute(elem, "stroke-linecap", strokeLinecapMap.get(STROKE_CAP.get(f)), "butt");
        
        //'stroke-linejoin'
        //Value:  	 miter | round | bevel | inherit
        //Initial:  	 miter
        //Applies to:  	 shapes and text content elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        writeAttribute(elem, "stroke-linejoin", strokeLinejoinMap.get(STROKE_JOIN.get(f)), "miter");
        
        //'stroke-miterlimit'
        //Value:  	 <miterlimit> | inherit
        //Initial:  	 4
        //Applies to:  	 shapes and text content elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        writeAttribute(elem, "stroke-miterlimit", STROKE_MITER_LIMIT.get(f), 4d);
        
        //'stroke-opacity'
        //Value:  	 <opacity-value> | inherit
        //Initial:  	 1
        //Applies to:  	 shapes and text content elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        writeAttribute(elem, "stroke-opacity", STROKE_OPACITY.get(f), 1d);
        
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
    /* Writes the transform attribute as specified in
     * http://www.w3.org/TR/SVGMobile12/coords.html#TransformAttribute
     *
     */
    protected void writeTransformAttribute(IXMLElement elem, Map<AttributeKey,Object> a)
    throws IOException {
        AffineTransform t = TRANSFORM.get(a);
        if (t != null) {
            writeAttribute(elem, "transform", toTransform(t), "none");
        }
    }
    /* Reads font attributes as listed in
     * http://www.w3.org/TR/SVGMobile12/feature.html#Font
     */
    private void writeFontAttributes(IXMLElement elem, Map<AttributeKey,Object> a)
    throws IOException {
        String value;
        double doubleValue;
        
        // 'font-family'
        // Value:  	[[ <family-name> |
        // <generic-family> ],]* [<family-name> |
        // <generic-family>] | inherit
        // Initial:  	depends on user agent
        // Applies to:  	text content elements
        // Inherited:  	yes
        // Percentages:  	N/A
        // Media:  	visual
        // Animatable:  	yes
        // Computed value:  	 Specified value, except inherit
        writeAttribute(elem, "font-family", FONT_FACE.get(a).getFamily(), "Dialog");
        
        // 'font-size'
        // Value:  	<absolute-size> | <relative-size> |
        // <length> | inherit
        // Initial:  	medium
        // Applies to:  	text content elements
        // Inherited:  	yes, the computed value is inherited
        // Percentages:  	N/A
        // Media:  	visual
        // Animatable:  	yes
        // Computed value:  	 Absolute length
        writeAttribute(elem, "font-size", FONT_SIZE.get(a), 0d);
        
        // 'font-style'
        // Value:  	normal | italic | oblique | inherit
        // Initial:  	normal
        // Applies to:  	text content elements
        // Inherited:  	yes
        // Percentages:  	N/A
        // Media:  	visual
        // Animatable:  	yes
        // Computed value:  	 Specified value, except inherit
        writeAttribute(elem, "font-style", (FONT_ITALIC.get(a)) ? "italic" : "normal", "normal");
        
        
        //'font-variant'
        //Value:  	normal | small-caps | inherit
        //Initial:  	normal
        //Applies to:  	text content elements
        //Inherited:  	yes
        //Percentages:  	N/A
        //Media:  	visual
        //Animatable:  	no
        //Computed value:  	 Specified value, except inherit
        // XXX - Implement me
        writeAttribute(elem, "font-variant", "normal", "normal");
        
        // 'font-weight'
        // Value:  	normal | bold | bolder | lighter | 100 | 200 | 300
        // | 400 | 500 | 600 | 700 | 800 | 900 | inherit
        // Initial:  	normal
        // Applies to:  	text content elements
        // Inherited:  	yes
        // Percentages:  	N/A
        // Media:  	visual
        // Animatable:  	yes
        // Computed value:  	 one of the legal numeric values, non-numeric
        // values shall be converted to numeric values according to the rules
        // defined below.
        writeAttribute(elem, "font-weight", (FONT_BOLD.get(a)) ? "bold" : "normal", "normal");
    }
    
    protected void writeAttribute(IXMLElement elem, String name, String value, String defaultValue) {
        writeAttribute(elem, name, SVG_NAMESPACE, value, defaultValue);
    }
    protected void writeAttribute(IXMLElement elem, String name, String namespace, String value, String defaultValue) {
        if (! value.equals(defaultValue)) {
            elem.setAttribute(name, value);
        }
    }
    protected void writeAttribute(IXMLElement elem, String name, double value, double defaultValue) {
        writeAttribute(elem, name, SVG_NAMESPACE, value, defaultValue);
    }
    protected void writeAttribute(IXMLElement elem, String name, String namespace, double value, double defaultValue) {
        if (value != defaultValue) {
            elem.setAttribute(name, toNumber(value));
        }
    }
    public static String toPath(BezierPath[] paths) {
        StringBuilder buf = new StringBuilder();
        
        for (int j=0; j < paths.length; j++) {
            BezierPath path = paths[j];
            
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
        }
        return buf.toString();
    }
    
    
    /**
     * Returns a double array as a number attribute value.
     */
    public static String toNumber(double number) {
        String str = Double.toString(number);
        if (str.endsWith(".0")) {
            str = str.substring(0, str.length() -  2);
        }
        return str;
    }
    
    /**
     * Returns a Point2D.Double array as a Points attribute value.
     * as specified in http://www.w3.org/TR/SVGMobile12/shapes.html#PointsBNF
     */
    public static String toPoints(Point2D.Double[] points) throws IOException {
        StringBuilder buf = new StringBuilder();
        for (int i=0; i < points.length; i++) {
            if (i != 0) {
                buf.append(", ");
            }
            buf.append(toNumber(points[i].x));
            buf.append(',');
            buf.append(toNumber(points[i].y));
        }
        return buf.toString();
    }
    /* Converts an AffineTransform into an SVG transform attribute value as specified in
     * http://www.w3.org/TR/SVGMobile12/coords.html#TransformAttribute
     */
    public static String toTransform(AffineTransform t) throws IOException {
        StringBuilder buf = new StringBuilder();
        switch (t.getType()) {
            case AffineTransform.TYPE_IDENTITY :
                buf.append("none");
                break;
            case AffineTransform.TYPE_TRANSLATION :
                // translate(<tx> [<ty>]), specifies a translation by tx and ty.
                // If <ty> is not provided, it is assumed to be zero.
                buf.append("translate(");
                buf.append(toNumber(t.getTranslateX()));
                if (t.getTranslateY() != 0d) {
                    buf.append(' ');
                    buf.append(toNumber(t.getTranslateY()));
                }
                buf.append(')');
                break;
                /*
            case AffineTransform.TYPE_GENERAL_ROTATION :
            case AffineTransform.TYPE_QUADRANT_ROTATION :
            case AffineTransform.TYPE_MASK_ROTATION :
                // rotate(<rotate-angle> [<cx> <cy>]), specifies a rotation by
                // <rotate-angle> degrees about a given point.
                // If optional parameters <cx> and <cy> are not supplied, the
                // rotate is about the origin of the current user coordinate
                // system. The operation corresponds to the matrix
                // [cos(a) sin(a) -sin(a) cos(a) 0 0].
                // If optional parameters <cx> and <cy> are supplied, the rotate
                // is about the point (<cx>, <cy>). The operation represents the
                // equivalent of the following specification:
                // translate(<cx>, <cy>) rotate(<rotate-angle>)
                // translate(-<cx>, -<cy>).
                buf.append("rotate(");
                buf.append(toNumber(t.getScaleX()));
                buf.append(')');
                break;*/
            case AffineTransform.TYPE_UNIFORM_SCALE :
                // scale(<sx> [<sy>]), specifies a scale operation by sx
                // and sy. If <sy> is not provided, it is assumed to be equal
                // to <sx>.
                buf.append("scale(");
                buf.append(toNumber(t.getScaleX()));
                buf.append(')');
                break;
            case AffineTransform.TYPE_GENERAL_SCALE :
            case AffineTransform.TYPE_MASK_SCALE :
                // scale(<sx> [<sy>]), specifies a scale operation by sx
                // and sy. If <sy> is not provided, it is assumed to be equal
                // to <sx>.
                buf.append("scale(");
                buf.append(toNumber(t.getScaleX()));
                buf.append(' ');
                buf.append(toNumber(t.getScaleY()));
                buf.append(')');
                break;
            default :
                // matrix(<a> <b> <c> <d> <e> <f>), specifies a transformation
                // in the form of a transformation matrix of six values.
                // matrix(a,b,c,d,e,f) is equivalent to applying the
                // transformation matrix [a b c d e f].
                buf.append("matrix(");
                double[] matrix = new double[6];
                t.getMatrix(matrix);
                for (int i=0; i < matrix.length; i++) {
                    if (i != 0) {
                        buf.append(' ');
                    }
                    buf.append(toNumber(matrix[i]));
                }
                buf.append(')');
                break;
        }
        
        return buf.toString();
    }
    
    public static String toColor(Color color) {
        if (color == null) {
            return "none";
        }
        
        
        String value;
        value = "000000"+Integer.toHexString(color.getRGB());
        value = "#"+value.substring(value.length() - 6);
        if (value.charAt(1) == value.charAt(2) &&
                value.charAt(3) == value.charAt(4) &&
                value.charAt(5) == value.charAt(6)) {
            value = "#"+value.charAt(1)+value.charAt(3)+value.charAt(5);
        }
        return value;
    }
    
    public String getFileExtension() {
        return "svg";
    }
    
    public void write(File file, Drawing drawing) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        try {
            write(out, drawing);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    public void write(OutputStream out, Drawing drawing) throws IOException {
        write(out, drawing.getFigures());
    }
    public void write(OutputStream out, java.util.List<Figure> figures) throws IOException {
        document = new XMLElement("svg", SVG_NAMESPACE);
        document.setAttribute("xmlns:xlink","http://www.w3.org/1999/xlink");
        document.setAttribute("version","1.2");
        
        nextId = 0;
        identifiedElements = new HashMap<IXMLElement,String>();
        
        defs = new XMLElement("defs");
        document.addChild(defs);
        
        for (Figure f: figures) {
            writeElement(document, f);
        }
        
        new XMLWriter(out).write(document);
    }
    
    /**
     * Gets a unique ID for the specified element.
     */
    public String getId(IXMLElement element) {
        if (identifiedElements.containsKey(element)) {
            return identifiedElements.get(element);
        } else {
            String id = Integer.toString(nextId++, Character.MAX_RADIX);
            identifiedElements.put(element, id);
            return id;
        }
    }
    
    
    
    public Transferable createTransferable(java.util.List<Figure> figures, double scaleFactor) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        write(buf, figures);
        return new InputStreamTransferable(new DataFlavor("image/svg+xml", "Image SVG"), buf.toByteArray());
    }
}
