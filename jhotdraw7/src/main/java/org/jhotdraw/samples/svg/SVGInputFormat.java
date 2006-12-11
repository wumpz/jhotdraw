/*
 * @(#)SVGStorageFormat.java  0.1  November 25, 2006
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

package org.jhotdraw.samples.svg;

import ch.randelshofer.quaqua.util.ArrayUtil;
import com.sun.org.apache.bcel.internal.verifier.statics.DOUBLE_Upper;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import org.jhotdraw.draw.*;
import net.n3.nanoxml.*;
import org.jhotdraw.geom.BezierPath;
import org.jhotdraw.io.ExtensionFileFilter;
import org.jhotdraw.samples.svg.figures.*;
import static org.jhotdraw.samples.svg.SVGConstants.*;
import org.jhotdraw.xml.JavaxDOMInput;

/**
 * SVGStorageFormat.
 * This format is aimed to comply to the Scalable Vector Graphics (SVG) Tiny 1.2
 * Specification supporting the <code>SVG-static</code> feature string.
 * <a href="http://www.w3.org/TR/SVGMobile12/">http://www.w3.org/TR/SVGMobile12/</a>
 *
 *
 * @author Werner Randelshofer
 * @version 0.1 November 25, 2006 Created (Experimental).
 */
public class SVGInputFormat implements StorageFormat {
    private String SVG_NAMESPACE = "http://www.w3.org/2000/svg";
    private SVGFigureFactory factory;
    private final static HashMap<String,SVGAttributeKeys.WindingRule> fillRuleMap;
    static {
        fillRuleMap = new HashMap<String, SVGAttributeKeys.WindingRule>();
        fillRuleMap.put("nonzero", SVGAttributeKeys.WindingRule.NON_ZERO);
        fillRuleMap.put("evenodd", SVGAttributeKeys.WindingRule.EVEN_ODD);
    }
    private final static HashMap<String,Integer> strokeLinecapMap;
    static {
        strokeLinecapMap = new HashMap<String, Integer>();
        strokeLinecapMap.put("butt", BasicStroke.CAP_BUTT);
        strokeLinecapMap.put("round", BasicStroke.CAP_ROUND);
        strokeLinecapMap.put("square", BasicStroke.CAP_SQUARE);
    }
    private final static HashMap<String,Integer> strokeLinejoinMap;
    static {
        strokeLinejoinMap = new HashMap<String, Integer>();
        strokeLinejoinMap.put("miter", BasicStroke.JOIN_MITER);
        strokeLinejoinMap.put("round", BasicStroke.JOIN_ROUND);
        strokeLinejoinMap.put("bevel", BasicStroke.JOIN_BEVEL);
    }
    private final static HashMap<String,Double> absoluteFontSizeMap;
    static {
        absoluteFontSizeMap = new HashMap<String,Double>();
        absoluteFontSizeMap.put("xx-small",6.944444);
        absoluteFontSizeMap.put("x-small",8.3333333);
        absoluteFontSizeMap.put("small", 10d);
        absoluteFontSizeMap.put("medium", 12d);
        absoluteFontSizeMap.put("large", 14.4);
        absoluteFontSizeMap.put("x-large", 17.28);
        absoluteFontSizeMap.put("xx-large",20.736);
    }
    private final static HashMap<String,Double> relativeFontSizeMap;
    static {
        relativeFontSizeMap = new HashMap<String,Double>();
        relativeFontSizeMap.put("larger", 1.2);
        relativeFontSizeMap.put("smaller",0.83333333);
    }
    private final static HashMap<String,SVGAttributeKeys.TextAnchor> textAnchorMap;
    static {
        textAnchorMap = new HashMap<String, SVGAttributeKeys.TextAnchor>();
        textAnchorMap.put("start", SVGAttributeKeys.TextAnchor.START);
        textAnchorMap.put("middle", SVGAttributeKeys.TextAnchor.MIDDLE);
        textAnchorMap.put("end", SVGAttributeKeys.TextAnchor.END);
    }
    
    /**
     * Maps to all XML elements that are identified by an xml:id.
     */
    public HashMap<String,IXMLElement> identifiedElements;
    /**
     * Maps to all drawing objects from the XML elements they were created from.
     */
    public HashMap<IXMLElement,Object> elementObjects;
    
    /**
     * Factor for percent values on the x-axis (width) in the user coordinate system.
     */
    private double widthPercentFactor = 1d;
    /**
     * Factor for percent values on the y-axis (height) in the user coordinate system.
     */
    private double heightPercentFactor = 1d;
    
    /**
     * Factor for length values on the x-axis (width) in the user coordinate system.
     */
    private double widthFactor = 1d;
    /**
     * Factor for length values on the y-axis (height) in the user coordinate sytem.
     */
    private double heightFactor = 1d;
    /**
     * Factor for number values in the user coordinate system.
     */
    private double numberFactor = 1d;
    
    
    /** Creates a new instance. */
    public SVGInputFormat() {
        this(new DefaultSVGFigureFactory());
    }
    public SVGInputFormat(SVGFigureFactory factory) {
        this.factory = factory;
    }
    
    public void write(OutputStream out, Drawing drawing, Collection<Figure> figures) throws IOException {
    }
    
    public void read(InputStream in, Drawing drawing, LinkedList<Figure> figures) throws IOException {
        try {
            IXMLElement elem;
            IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
            IXMLReader reader = new StdXMLReader(in);
            parser.setReader(reader);
            elem = (IXMLElement) parser.parse();
            
            // Search for the first 'svg' element in the XML document
            // in preorder sequence
            Stack<Iterator> stack = new Stack<Iterator>();
            LinkedList<IXMLElement> ll = new LinkedList<IXMLElement>();
            ll.add(elem);
            stack.push(ll.iterator());
            //stack.push(elem.iterateChildren());
            while (!stack.empty() && stack.peek().hasNext()) {
                Iterator iter = stack.peek();
                IXMLElement node = (IXMLElement)iter.next();
                Iterator children = node.iterateChildren();
                
                if (! iter.hasNext()) {
                    stack.pop();
                }
                if (children.hasNext()) {
                    stack.push(children);
                }
                if (node.getName() != null &&
                        node.getName().equals("svg") &&
                        (node.getNamespace() == null ||
                        node.getNamespace().equals(SVG_NAMESPACE))) {
                    elem = node;
                    break;
                }
            }
            
            if (elem.getName() == null ||
                    ! elem.getName().equals("svg") ||
                    (elem.getNamespace() != null &&
                    ! elem.getNamespace().equals(SVG_NAMESPACE))) {
                throw new IOException("'svg' element expected: "+elem.getName()+" line: "+elem.getLineNr());
            }
            
            // Flatten CSS Styles
            initStorageContext();
            flattenStyles(elem);
            
            // Read attributes of 'svg' element
            AffineTransform svgTransform = new AffineTransform();
            boolean is100PercentWidth = readAttribute(elem, "width", "100%").equals("100%");
            boolean is100PercentHeight = readAttribute(elem, "height", "100%").equals("100%");
           double svgWidth = toWidth(elem, readAttribute(elem, "width", "100%"));
            double svgHeight = toHeight(elem, readAttribute(elem, "height", "100%"));
            String[] viewBoxValues = toWhiteSpaceOrCommaSeparatedArray(elem, readAttribute(elem, "viewBox", "none"));
            if (viewBoxValues.length == 4) {
                double x = toNumber(elem, viewBoxValues[0]);
                 double y = toNumber(elem, viewBoxValues[1]);
                double w = toNumber(elem, viewBoxValues[2]);
                double h = toNumber(elem, viewBoxValues[3]);
                widthPercentFactor = w / 100d;
                heightPercentFactor = h / 100d;
                svgTransform.translate(-x, -y);
                //svgTransform.scale(svgWidth / w, svgHeight / h);
                widthFactor = (is100PercentWidth) ? 1 : svgWidth / w;
                heightFactor = (is100PercentHeight) ? 1 : svgHeight / h;
                numberFactor = Math.min(widthFactor, heightFactor);
            }
            
            // Read the figures
            for (int i=0; i < elem.getChildrenCount(); i++) {
                IXMLElement child = elem.getChildAtIndex(i);
                Figure childFigure = readElement(child);
                if (childFigure != null) {
                    childFigure.basicTransform(svgTransform);
                    drawing.add(childFigure);
                    figures.add(childFigure);
                }
            }
            
            
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            IOException error = new IOException(e.getMessage());
            error.initCause(e);
            throw error;
        }
    }
    private void initStorageContext() {
        identifiedElements = new HashMap<String,IXMLElement>();
        elementObjects = new HashMap<IXMLElement,Object>();
     widthPercentFactor = 1d;
     heightPercentFactor = 1d;
     widthFactor = 1d;
     heightFactor = 1d;
    numberFactor = 1d;
    }
    
    /**
     * Flattens all CSS styles.
     * Styles defined in a "style" attribute and in CSS rules are converted
     * into attributes with the same name.
     */
    private void flattenStyles(IXMLElement elem)
    throws IOException {
        if (elem.getNamespace() == null ||
                elem.getNamespace().equals(SVG_NAMESPACE)) {
            
            String style = elem.getAttribute("style", null);
            if (style != null) {
                for (String styleProperty : style.split(";"))  {
                    String[] stylePropertyElements = styleProperty.split(":");
                    if (stylePropertyElements.length == 2 &&
                            ! elem.hasAttribute(stylePropertyElements[0], SVG_NAMESPACE)) {
                        elem.setAttribute(stylePropertyElements[0], SVG_NAMESPACE, stylePropertyElements[1]);
                    }
                }
            }
            
            for (int i=0; i < elem.getChildrenCount(); i++) {
                IXMLElement child = elem.getChildAtIndex(i);
                flattenStyles(child);
            }
        }
    }
    
    
    /**
     * Reads an SVG element of any kind.
     * @return Returns the Figure, if the SVG element represents a Figure.
     * Returns null in all other cases.
     */
    private Figure readElement(IXMLElement elem)
    throws IOException {
        Figure f = null;
        if (elem.getNamespace() == null ||
                elem.getNamespace().equals(SVG_NAMESPACE)) {
            String name = elem.getName();
            if (name.equals("circle")) {
                f = readCircleElement(elem);
            } else if (name.equals("defs")) {
                readDefsElement(elem);
                f = null;
            } else if (name.equals("ellipse")) {
                f = readEllipseElement(elem);
            } else if (name.equals("svg")) {
                // treat svg element like g element
                f = readGElement(elem);
            } else if (name.equals("g")) {
                f = readGElement(elem);
            } else if (name.equals("line")) {
                f = readLineElement(elem);
            } else if (name.equals("linearGradient")) {
                readLinearGradientElement(elem);
                f = null;
            } else if (name.equals("path")) {
                f = readPathElement(elem);
            } else if (name.equals("polygon")) {
                f = readPolygonElement(elem);
            } else if (name.equals("polyline")) {
                f = readPolylineElement(elem);
            } else if (name.equals("radialGradient")) {
                readRadialGradientElement(elem);
                f = null;
            } else if (name.equals("rect")) {
                f = readRectElement(elem);
            } else if (name.equals("solidColor")) {
                readSolidColorElement(elem);
                f = null;
            } else if (name.equals("text")) {
                f = readTextElement(elem);
            } else if (name.equals("use")) {
                f = readUseElement(elem);
            } else {
                System.out.println("<"+name+">");
            }
        }
        if (f instanceof SVGFigure) {
            if (((SVGFigure) f).isEmpty()) {
                System.out.println("Empty figure "+f);
                return null;
            }
        } else if (f != null) {
            System.out.println("not an SVGFigure "+f);
        }
        
        return f;
    }
    
    /**
     * Reads an SVG "defs" element.
     */
    private void readDefsElement(IXMLElement elem)
    throws IOException {
        for (int i=0; i < elem.getChildrenCount(); i++) {
            IXMLElement child = elem.getChildAtIndex(i);
            Figure childFigure = readElement(child);
        }
    }
    
    /**
     * Reads an SVG "g" element.
     */
    private Figure readGElement(IXMLElement elem)
    throws IOException {
        HashMap<AttributeKey,Object> a = new HashMap<AttributeKey,Object>();
        readCoreAttributes(elem, a);
        readTransformAttribute(elem, a);
        AffineTransform transform = (AffineTransform) a.get(SVGAttributeKeys.TRANSFORM);
        CompositeFigure g = factory.createG(a);
        
        for (int i=0; i < elem.getChildrenCount(); i++) {
            IXMLElement child = elem.getChildAtIndex(i);
            Figure childFigure = readElement(child);
            if (childFigure != null) {
                g.add(childFigure);
            }
        }
        if (transform != null) {
            g.basicTransform(transform);
        }
        return g;
    }
    
    /**
     * Reads an SVG "rect" element.
     */
    private Figure readRectElement(IXMLElement elem)
    throws IOException {
        HashMap<AttributeKey,Object> a = new HashMap<AttributeKey,Object>();
        readCoreAttributes(elem, a);
        readTransformAttribute(elem, a);
        readShapeAttributes(elem, a);
        
        double x = toNumber(elem, readAttribute(elem, "x", "0"));
        double y = toNumber(elem, readAttribute(elem, "y", "0"));
        double w = toWidth(elem, readAttribute(elem, "width", "0"));
        double h = toHeight(elem, readAttribute(elem, "height", "0"));
        
        String rxValue = readAttribute(elem, "rx", "none");
        String ryValue = readAttribute(elem, "ry", "none");
        if (rxValue.equals("none")) {
            rxValue = ryValue;
        }
        if (ryValue.equals("none")) {
            ryValue = rxValue;
        }
        double rx = toNumber(elem, rxValue.equals("none") ? "0" : rxValue);
        double ry = toNumber(elem, ryValue.equals("none") ? "0" : ryValue);
        
        Figure figure = factory.createRect(x, y, w, h, rx, ry, a);
        elementObjects.put(elem, figure);
        return figure;
    }
    /**
     * Reads an SVG "circle" element.
     */
    private Figure readCircleElement(IXMLElement elem)
    throws IOException {
        HashMap<AttributeKey,Object> a = new HashMap<AttributeKey,Object>();
        readCoreAttributes(elem, a);
        readTransformAttribute(elem, a);
        readShapeAttributes(elem, a);
        
        double cx = toNumber(elem, readAttribute(elem, "cx", "0"));
        double cy = toNumber(elem, readAttribute(elem, "cy", "0"));
        double r = toNumber(elem, readAttribute(elem, "r", "0"));
        
        Figure figure = factory.createCircle(cx, cy, r, a);
        elementObjects.put(elem, figure);
        return figure;
    }
    /**
     * Reads an SVG "ellipse" element.
     */
    private Figure readEllipseElement(IXMLElement elem)
    throws IOException {
        HashMap<AttributeKey,Object> a = new HashMap<AttributeKey,Object>();
        readCoreAttributes(elem, a);
        readTransformAttribute(elem, a);
        readShapeAttributes(elem, a);
        
        double cx = toNumber(elem, readAttribute(elem, "cx", "0"));
        double cy = toNumber(elem, readAttribute(elem, "cy", "0"));
        double rx = toNumber(elem, readAttribute(elem, "rx", "0"));
        double ry = toNumber(elem, readAttribute(elem, "ry", "0"));
        
        Figure figure = factory.createEllipse(cx, cy, rx, ry, a);
        elementObjects.put(elem, figure);
        return figure;
    }
    /**
     * Reads an SVG "line" element.
     */
    private Figure readLineElement(IXMLElement elem)
    throws IOException {
        HashMap<AttributeKey,Object> a = new HashMap<AttributeKey,Object>();
        readCoreAttributes(elem, a);
        readTransformAttribute(elem, a);
        readShapeAttributes(elem, a);
        
        double x1 = toNumber(elem, readAttribute(elem, "x1", "0"));
        double y1 = toNumber(elem, readAttribute(elem, "y1", "0"));
        double x2 = toNumber(elem, readAttribute(elem, "x2", "0"));
        double y2 = toNumber(elem, readAttribute(elem, "y2", "0"));
        
        Figure figure = factory.createLine(x1, y1, x2, y2, a);
        elementObjects.put(elem, figure);
        return figure;
    }
    /**
     * Reads an SVG "polyline" element.
     */
    private Figure readPolylineElement(IXMLElement elem)
    throws IOException {
        HashMap<AttributeKey,Object> a = new HashMap<AttributeKey,Object>();
        readCoreAttributes(elem, a);
        readTransformAttribute(elem, a);
        readShapeAttributes(elem, a);
        
        Point2D.Double[] points = toPoints(elem, readAttribute(elem, "points", ""));
        
        Figure figure = factory.createPolyline(points, a);
        elementObjects.put(elem, figure);
        return figure;
    }
    /**
     * Reads an SVG "polygon" element.
     */
    private Figure readPolygonElement(IXMLElement elem)
    throws IOException {
        HashMap<AttributeKey,Object> a = new HashMap<AttributeKey,Object>();
        readCoreAttributes(elem, a);
        readTransformAttribute(elem, a);
        readShapeAttributes(elem, a);
        
        Point2D.Double[] points = toPoints(elem, readAttribute(elem, "points", ""));
        
        Figure figure = factory.createPolygon(points, a);
        elementObjects.put(elem, figure);
        return figure;
    }
    /**
     * Reads an SVG "path" element.
     */
    private Figure readPathElement(IXMLElement elem)
    throws IOException {
        HashMap<AttributeKey,Object> a = new HashMap<AttributeKey,Object>();
        readCoreAttributes(elem, a);
        readTransformAttribute(elem, a);
        readShapeAttributes(elem, a);
        
        BezierPath[] beziers = toPath(elem, readAttribute(elem, "d", ""));
        
        Figure figure = factory.createPath(beziers, a);
        elementObjects.put(elem, figure);
        return figure;
    }
    /**
     * Reads an SVG "text" element.
     */
    private Figure readTextElement(IXMLElement elem)
    throws IOException {
        HashMap<AttributeKey,Object> a = new HashMap<AttributeKey,Object>();
        readCoreAttributes(elem, a);
        readTransformAttribute(elem, a);
        readShapeAttributes(elem, a);
        readFontAttributes(elem, a);
        readTextAttributes(elem, a);
        
        String[] xStr = toCommaSeparatedArray(elem, readAttribute(elem, "x", "0"));
        String[] yStr = toCommaSeparatedArray(elem, readAttribute(elem, "y", "0"));
        
        Point2D.Double[] coordinates = new Point2D.Double[Math.max(xStr.length, yStr.length)];
        double lastX = 0;
        double lastY = 0;
        for (int i=0; i < coordinates.length; i++) {
            if (xStr.length > i) {
                try {
                    lastX = toNumber(elem, xStr[i]);
                } catch (NumberFormatException ex) {
                }
            }
            if (yStr.length > i) {
                try {
                    lastY = toNumber(elem, yStr[i]);
                } catch (NumberFormatException ex) {
                }
            }
            coordinates[i] = new Point2D.Double(lastX, lastY);
        }
        
        
        String[] rotateStr = toCommaSeparatedArray(elem, readAttribute(elem, "rotate", ""));
        double[] rotate = new double[rotateStr.length];
        for (int i=0; i < rotateStr.length; i++) {
            try {
                rotate[i] = toDouble(elem, rotateStr[i]);
            } catch (NumberFormatException ex) {
                rotate[i] = 0;
            }
        }
        
        DefaultStyledDocument doc = new DefaultStyledDocument();
        
        try {
            if (elem.getContent() != null) {
                doc.insertString(0, toText(elem, elem.getContent()), null);
            } else {
                for (Object childObj : elem.getChildren()) {
                    IXMLElement node = (IXMLElement) childObj;
                    if (node.getName() == null) {
                        doc.insertString(0, toText(elem, node.getContent()), null);
                    } else if (node.getName().equals("tspan")) {
                        readTSpanElement(node, doc);
                    } else {
                        System.out.println("  text node "+node.getName());
                    }
                }
            }
        } catch (BadLocationException e) {
            InternalError ex = new InternalError(e.getMessage());
            ex.initCause(e);
            throw ex;
        }
        
        Figure figure = factory.createText(coordinates, rotate, doc, a);
        elementObjects.put(elem, figure);
        return figure;
    }
    /**
     * Reads an SVG "tspan" element.
     */
    private void readTSpanElement(IXMLElement elem, DefaultStyledDocument doc)
    throws IOException {
        try {
            if (elem.getContent() != null) {
                doc.insertString(0, toText(elem, elem.getContent()), null);
            } else {
                for (Object childObj : elem.getChildren()) {
                    IXMLElement node = (IXMLElement) childObj;
                    if (node.getName() == null) {
                        doc.insertString(0, toText(node, node.getContent()), null);
                    } else if (node.getName().equals("tspan")) {
                        readTSpanElement(node, doc);
                    } else {
                        System.out.println("  text node "+node.getName());
                    }
                }
            }
        } catch (BadLocationException e) {
            InternalError ex = new InternalError(e.getMessage());
            ex.initCause(e);
            throw ex;
        }
    }
    
    /**
     * Reads an SVG "use" element.
     */
    private Figure readUseElement(IXMLElement elem)
    throws IOException {
        HashMap<AttributeKey,Object> a = new HashMap<AttributeKey,Object>();
        readCoreAttributes(elem, a);
        readTransformAttribute(elem, a);
        readShapeAttributes(elem, a);
        
        String href = elem.getAttribute("xlink:href",null);
        if (href != null && href.startsWith("#")) {
            
            IXMLElement refElem = identifiedElements.get(href.substring(1));
            if (refElem == null) {
                System.out.println("SVGInputFormat couldntt find href for <use> element:"+href);
            } else {
                Object obj = readElement(refElem);
                if (obj instanceof Figure) {
                    Figure figure = (Figure) ((Figure) obj).clone();
                    
                    for (Map.Entry<AttributeKey, Object> entry : a.entrySet()) {
                        if (! figure.getAttributes().containsKey(entry.getKey())) {
                            figure.setAttribute(entry.getKey(), entry.getValue());
                        }
                    }
                    AffineTransform tx = new AffineTransform();
                    double x = toNumber(elem, readAttribute(elem, "x", "0"));
                    double y = toNumber(elem, readAttribute(elem, "y", "0"));
                    tx.translate(x, y);
                    figure.basicTransform(tx);
                    
                    return figure;
                }
            }
        }
        return null;
    }
    /**
     * Reads an attribute that is inherited.
     */
    private String readInheritAttribute(IXMLElement elem, String attributeName, String defaultValue) {
        if (elem.hasAttribute(attributeName, SVG_NAMESPACE)) {
            String value = elem.getAttribute(attributeName, SVG_NAMESPACE, defaultValue);
            if (value.equals("inherit")) {
                return readInheritAttribute(elem.getParent(), attributeName, defaultValue);
            } else {
                return value;
            }
        } else if (elem.hasAttribute(attributeName)) {
            String value = elem.getAttribute(attributeName, defaultValue);
            if (value.equals("inherit")) {
                return readInheritAttribute(elem.getParent(), attributeName, defaultValue);
            } else {
                return value;
            }
        } else if (elem.getParent() != null &&
                (elem.getParent().getNamespace() == null || elem.getParent().getNamespace().equals(SVG_NAMESPACE))) {
            return readInheritAttribute(elem.getParent(), attributeName, defaultValue);
        } else {
            return defaultValue;
        }
    }
    /**
     * Reads a font size attribute that is inherited.
     * As specified by
     * http://www.w3.org/TR/SVGMobile12/text.html#FontPropertiesUsedBySVG
     * http://www.w3.org/TR/2006/CR-xsl11-20060220/#font-size
     */
    private double readInheritFontSizeAttribute(IXMLElement elem, String attributeName, String defaultValue)
    throws IOException {
        String value = null;
        if (elem.hasAttribute(attributeName, SVG_NAMESPACE)) {
            value = elem.getAttribute(attributeName, SVG_NAMESPACE, defaultValue);
        } else if (elem.hasAttribute(attributeName)) {
            value = elem.getAttribute(attributeName, defaultValue);
        } else {
            value = defaultValue;
        }
        
        if (value.equals("inherit")) {
            return readInheritFontSizeAttribute(elem.getParent(), attributeName, defaultValue);
        } else if (absoluteFontSizeMap.containsKey(value)) {
            return absoluteFontSizeMap.get(value);
        } else if (relativeFontSizeMap.containsKey(value)) {
            return relativeFontSizeMap.get(value) * readInheritFontSizeAttribute(elem.getParent(), attributeName, defaultValue);
        } else if (value.endsWith("%")) {
            double factor = Double.valueOf(value.substring(0, value.length() - 1));
            return factor * readInheritFontSizeAttribute(elem.getParent(), attributeName, defaultValue);
        } else {
            return toHeight(elem, value);
        }
        
    }
    
    /**
     * Reads an attribute that is not inherited, unless its value is "inherit".
     */
    private String readAttribute(IXMLElement elem, String attributeName, String defaultValue) {
        if (elem.hasAttribute(attributeName, SVG_NAMESPACE)) {
            String value = elem.getAttribute(attributeName, SVG_NAMESPACE, defaultValue);
            if (value.equals("inherit")) {
                return readAttribute(elem.getParent(), attributeName, defaultValue);
            } else {
                return value;
            }
        } else if (elem.hasAttribute(attributeName) ) {
            String value = elem.getAttribute(attributeName, defaultValue);
            if (value.equals("inherit")) {
                return readAttribute(elem.getParent(), attributeName, defaultValue);
            } else {
                return value;
            }
        } else {
            return defaultValue;
        }
    }
    
    /**
     * Returns a value as a width.
     * http://www.w3.org/TR/SVGMobile12/types.html#DataTypeLength
     */
    private double toWidth(IXMLElement elem, String str) throws IOException {
        // XXX - Compute xPercentFactor from viewport
        return toLength(elem, str, widthPercentFactor, widthFactor);
    }
    /**
     * Returns a value as a height.
     * http://www.w3.org/TR/SVGMobile12/types.html#DataTypeLength
     */
    private double toHeight(IXMLElement elem, String str) throws IOException {
        // XXX - Compute yPercentFactor from viewport
        return toLength(elem, str, heightPercentFactor, heightFactor);
    }
    /**
     * Returns a value as a number.
     * http://www.w3.org/TR/SVGMobile12/types.html#DataTypeNumber
     */
    private double toNumber(IXMLElement elem, String str) throws IOException {
        return toLength(elem, str, 1d, numberFactor);
    }
    /**
     * Returns a value as a length.
     * http://www.w3.org/TR/SVGMobile12/types.html#DataTypeLength
     */
    private double toLength(IXMLElement elem, String str, double percentFactor, double lengthFactor) throws IOException {
        double scaleFactor = 1d;
        if (str == null || str.length() == 0) {
            return 0d;
        }
        
        if (str.endsWith("%")) {
            str = str.substring(0, str.length() - 1);
            scaleFactor = percentFactor;
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
            // XXX - This doesn't work
            scaleFactor = toLength(elem, readAttribute(elem, "font-size", "0"), percentFactor, lengthFactor);
        } else {
            scaleFactor = lengthFactor;
        }
        
        return Double.parseDouble(str) * scaleFactor;
    }
    /**
     * Returns a value as a String array.
     * The values are separated by commas with optional white space.
     */
    private String[] toCommaSeparatedArray(IXMLElement elem, String str) throws IOException {
        return str.split("\\s*,\\s*");
    }
    /**
     * Returns a value as a String array.
     * The values are separated by whitespace or by commas with optional white
     * space.
     */
    private String[] toWhiteSpaceOrCommaSeparatedArray(IXMLElement elem, String str) throws IOException {
        return str.split("(\\s*,\\s*|\\s+)");
    }
    /**
     * Returns a value as a Point2D.Double array.
     * as specified in http://www.w3.org/TR/SVGMobile12/shapes.html#PointsBNF
     */
    private Point2D.Double[] toPoints(IXMLElement elem, String str) throws IOException {
        
        StringTokenizer tt = new StringTokenizer(str," ,");
        Point2D.Double[] points =new Point2D.Double[tt.countTokens() / 2];
        for (int i=0; i < points.length; i++) {
            
            points[i] = new Point2D.Double(
                    toNumber(elem, tt.nextToken()),
                    toNumber(elem, tt.nextToken())
                    );
        }
        return points;
    }
    /**
     * Returns a value as a BezierPath array.
     * as specified in http://www.w3.org/TR/SVGMobile12/shapes.html#PointsBNF
     */
    private BezierPath[] toPath(IXMLElement elem, String str) throws IOException {
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
                    p.x = tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y = tt.nval * numberFactor;
                    path.moveTo(p.x, p.y);
                    nextCommand = 'L';
                    break;
                case 'm' :
                    if (path != null) {
                        paths.add(path);
                    }
                    path = new BezierPath();
                    
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x += tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y += tt.nval * numberFactor;
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
                    p.x = tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y = tt.nval * numberFactor;
                    path.lineTo(p.x, p.y);
                    nextCommand = 'L';
                    
                    break;
                case 'l' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x += tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y += tt.nval * numberFactor;
                    path.lineTo(p.x, p.y);
                    nextCommand = 'l';
                    
                    break;
                case 'H' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x = tt.nval * numberFactor;
                    path.lineTo(p.x, p.y);
                    nextCommand = 'H';
                    
                    break;
                case 'h' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x += tt.nval * numberFactor;
                    path.lineTo(p.x, p.y);
                    nextCommand = 'h';
                    
                    break;
                case 'V' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y = tt.nval * numberFactor;
                    path.lineTo(p.x, p.y);
                    nextCommand = 'V';
                    
                    break;
                case 'v' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y += tt.nval * numberFactor;
                    path.lineTo(p.x, p.y);
                    nextCommand = 'v';
                    
                    // curveto
                    break;
                case 'C' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c1.x = tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c1.y = tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c2.x = tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c2.y = tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x = tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y = tt.nval * numberFactor;
                    path.curveTo(c1.x, c1.y, c2.x, c2.y, p.x, p.y);
                    nextCommand = 'C';
                    
                    break;
                case 'c' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c1.x = p.x + tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c1.y = p.y + tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c2.x = p.x + tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c2.y = p.y + tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x += tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y += tt.nval * numberFactor;
                    path.curveTo(c1.x, c1.y, c2.x, c2.y, p.x, p.y);
                    nextCommand = 'c';
                    
                    break;
                case 'S' :
                    node = path.get(path.size() - 1);
                    c1.x = node.x[0] * 2d - node.x[1];
                    c1.y = node.y[0] * 2d - node.y[1];
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c2.x = tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c2.y = tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x = tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y = tt.nval * numberFactor;
                    path.curveTo(c1.x, c1.y, c2.x, c2.y, p.x, p.y);
                    nextCommand = 'S';
                    
                    break;
                case 's' :
                    node = path.get(path.size() - 1);
                    c1.x = node.x[0] * 2d - node.x[1];
                    c1.y = node.y[0] * 2d - node.y[1];
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c2.x = p.x + tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c2.y = p.y + tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x += tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y += tt.nval * numberFactor;
                    path.curveTo(c1.x, c1.y, c2.x, c2.y, p.x, p.y);
                    nextCommand = 's';
                    
                    // quadto
                    break;
                case 'Q' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c1.x = tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c1.y = tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x = tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y = tt.nval * numberFactor;
                    path.quadTo(c1.x, c1.y, p.x, p.y);
                    nextCommand = 'Q';
                    
                    break;
                case 'q' :
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c1.x = p.x + tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    c1.y = p.y + tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x += tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y += tt.nval * numberFactor;
                    path.quadTo(c1.x, c1.y, p.x, p.y);
                    nextCommand = 'q';
                    
                    break;
                case 'T' :
                    node = path.get(path.size() - 1);
                    c1.x = node.x[0] * 2d - node.x[1];
                    c1.y = node.y[0] * 2d - node.y[1];
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x = tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y = tt.nval * numberFactor;
                    path.quadTo(c1.x, c1.y, p.x, p.y);
                    nextCommand = 'T';
                    
                    break;
                case 't' :
                    node = path.get(path.size() - 1);
                    c1.x = node.x[0] * 2d - node.x[1];
                    c1.y = node.y[0] * 2d - node.y[1];
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.x += tt.nval * numberFactor;
                    if (tt.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Number expected");
                    p.y += tt.nval * numberFactor;
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
        return paths.toArray(new BezierPath[paths.size()]);
    }
    /* Reads core attributes as listed in
     * http://www.w3.org/TR/SVGMobile12/feature.html#CoreAttribute
     */
    private void readCoreAttributes(IXMLElement elem, HashMap<AttributeKey,Object> a)
    throws IOException {
        // read "id" or "xml:id"
        identifiedElements.put(elem.getAttribute("id", null), elem);
        identifiedElements.put(elem.getAttribute("xml:id", null), elem);
        
        // XXX - Add
        // xml:base
        // xml:lang
        // xml:space
        // class
        
    }
    /* Reads text attributes as listed in
     * http://www.w3.org/TR/SVGMobile12/feature.html#Text
     */
    private void readTextAttributes(IXMLElement elem, HashMap<AttributeKey,Object> a)
    throws IOException {
        Object value;
        
        //'text-anchor'
        //Value:  	start | middle | end | inherit
        //Initial:  	start
        //Applies to:  	'text' Element
        //Inherited:  	yes
        //Percentages:  	N/A
        //Media:  	visual
        //Animatable:  	yes
        //Computed value:  	 Specified value, except inherit
        value = readInheritAttribute(elem, "text-anchor", "start");
        a.put(SVGAttributeKeys.TEXT_ANCHOR, textAnchorMap.get(value));
        
        //'display-align'
        //Value:  	auto | before | center | after | inherit
        //Initial:  	auto
        //Applies to:  	'textArea'
        //Inherited:  	yes
        //Percentages:  	N/A
        //Media:  	visual
        //Animatable:  	yes
        //Computed value:  	 Specified value, except inherit
        value = readInheritAttribute(elem, "display-align", "auto");
        System.out.println("display-align="+value);
        
        //text-align
        //Value:	 start | end | center | inherit
        //Initial:	 start
        //Applies to:	 textArea elements
        //Inherited:	 yes
        //Percentages:	 N/A
        //Media:	 visual
        //Animatable:	 yes
        value = readInheritAttribute(elem, "text-align", "start");
        System.out.println("text-align="+value);
        
    }
    /* Reads text flow attributes as listed in
     * http://www.w3.org/TR/SVGMobile12/feature.html#TextFlow
     */
    private void readTextFlowAttributes(IXMLElement elem, HashMap<AttributeKey,Object> a)
    throws IOException {
        Object value;
        
        //'line-increment'
        //Value:  	auto | <number> | inherit
        //Initial:  	auto
        //Applies to:  	'textArea'
        //Inherited:  	yes
        //Percentages:  	N/A
        //Media:  	visual
        //Animatable:  	yes
        //Computed value:  	 Specified value, except inherit
        value = readInheritAttribute(elem, "line-increment", "auto");
        System.out.println("line-increment="+value);
        
    }
    /* Reads the transform attribute as specified in
     * http://www.w3.org/TR/SVGMobile12/coords.html#TransformAttribute
     *
     */
    private void readTransformAttribute(IXMLElement elem, HashMap<AttributeKey,Object> a)
    throws IOException {
        String value;
        value = readAttribute(elem, "transform", "none");
        if (! value.equals("none")) {
            a.put(SVGAttributeKeys.TRANSFORM, toTransform(elem, value));
        }
    }
    /* Reads solid color attributes.
     */
    private void readSolidColorElement(IXMLElement elem)
    throws IOException {
        HashMap<AttributeKey,Object> a = new HashMap<AttributeKey,Object>();
        readCoreAttributes(elem, a);
        
        // 'solid-color'
        //Value:	 currentColor | <color> | inherit
        //Initial:	 black
        //Applies to:	 'solidColor' elements
        //Inherited:	 no
        //Percentages:	 N/A
        //Media:	 visual
        //Animatable:	 yes
        //Computed value:  	 Specified <color> value, except inherit
        Color color = toColor(elem, readAttribute(elem, "solid-color", "black"));
        
        //'solid-opacity'
        //Value:	<opacity-value> | inherit
        //Initial:	 1
        //Applies to:	 'solidColor' elements
        //Inherited:	 no
        //Percentages:	 N/A
        //Media:	 visual
        //Animatable:	 yes
        //Computed value:  	 Specified value, except inherit
        double opacity = toDouble(elem, readAttribute(elem, "solid-opacity", "1"), 1, 0, 1);
        if (opacity != 1) {
            color = new Color(((int) (255 * opacity) << 24) | (0xffffff & color.getRGB()), true);
        }
        
        elementObjects.put(elem, color);
        
        
    }
    /* Reads image attributes.
, 1,,, ,
     */
    private void readImageAttributes(IXMLElement elem, HashMap<AttributeKey,Object> a)
    throws IOException {
        Object value;
        
        //'opacity'
        //Value:  	<opacity-value> | inherit
        //Initial:  	1
        //Applies to:  	 'image' element
        //Inherited:  	no
        //Percentages:  	N/A
        //Media:  	visual
        //Animatable:  	yes
        //Computed value:  	 Specified value, except inherit
        value = readAttribute(elem, "opacity", "1");
        System.out.println("  opacity="+value);
        
        
    }
    /* Reads shape attributes.
     */
    private void readShapeAttributes(IXMLElement elem, HashMap<AttributeKey,Object> a)
    throws IOException {
        Object objectValue;
        String value;
        double doubleValue;
        
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
        objectValue = toPaint(elem, readInheritAttribute(elem, "fill", "black"));
        if (objectValue instanceof Color) {
            a.put(SVGAttributeKeys.FILL_COLOR, (Color) objectValue);
        } else if (objectValue instanceof Gradient) {
            a.put(SVGAttributeKeys.FILL_GRADIENT, (Gradient) objectValue);
        } else if (objectValue == null) {
            a.put(SVGAttributeKeys.FILL_COLOR, null);
        } else {
            a.put(SVGAttributeKeys.FILL_COLOR, null);
            System.out.println("  fill="+objectValue);
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
        doubleValue = toDouble(elem, readInheritAttribute(elem, "fill-opacity", "1"), 1d, 0d, 1d);
        if (doubleValue != 1d) {
            Color c = (Color) a.get(SVGAttributeKeys.FILL_COLOR);
            if (c != null) {
                a.put(SVGAttributeKeys.FILL_COLOR,
                        new Color((c.getRGB() & 0xffffff) | ((int) (255 * doubleValue) << 24), true)
                        );
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
        value = readInheritAttribute(elem, "fill-rule", "nonzero");
        a.put(SVGAttributeKeys.WINDING_RULE, fillRuleMap.get(value));
        
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
        objectValue = toPaint(elem, readInheritAttribute(elem, "stroke", "none"));
        if (objectValue instanceof Color) {
            a.put(SVGAttributeKeys.STROKE_COLOR, (Color) objectValue);
        } else if (objectValue instanceof Gradient) {
            a.put(SVGAttributeKeys.STROKE_GRADIENT, (Gradient) objectValue);
        } else if (objectValue == null) {
            a.put(SVGAttributeKeys.STROKE_COLOR, null);
        } else {
            a.put(SVGAttributeKeys.STROKE_COLOR, null);
            System.out.println("  stroke="+objectValue);
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
        value = readInheritAttribute(elem, "stroke-dasharray", "none");
        if (! value.equals("none")) {
            String[] values = toCommaSeparatedArray(elem, value);
            double[] dashes = new double[values.length];
            for (int i=0; i < values.length; i++) {
                dashes[i] = toNumber(elem, values[i]);
            }
            a.put(SVGAttributeKeys.STROKE_DASHES, dashes);
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
        doubleValue = toNumber(elem, readInheritAttribute(elem, "stroke-dashoffset", "0"));
        a.put(SVGAttributeKeys.STROKE_DASH_PHASE, doubleValue);
        a.put(SVGAttributeKeys.IS_STROKE_DASH_FACTOR, false);
        
        //'stroke-linecap'
        //Value:  	 butt | round | square | inherit
        //Initial:  	 butt
        //Applies to:  	 shapes and text content elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        value = readInheritAttribute(elem, "stroke-linecap", "butt");
        a.put(SVGAttributeKeys.STROKE_CAP, strokeLinecapMap.get(value));
        
        
        //'stroke-linejoin'
        //Value:  	 miter | round | bevel | inherit
        //Initial:  	 miter
        //Applies to:  	 shapes and text content elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        value = readInheritAttribute(elem, "stroke-linejoin", "miter");
        a.put(SVGAttributeKeys.STROKE_JOIN, strokeLinejoinMap.get(value));
        
        //'stroke-miterlimit'
        //Value:  	 <miterlimit> | inherit
        //Initial:  	 4
        //Applies to:  	 shapes and text content elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        doubleValue = toDouble(elem, readInheritAttribute(elem, "stroke-miterlimit", "4"));
        a.put(SVGAttributeKeys.STROKE_MITER_LIMIT, doubleValue);
        a.put(SVGAttributeKeys.IS_STROKE_MITER_LIMIT_FACTOR, false);
        
        //'stroke-opacity'
        //Value:  	 <opacity-value> | inherit
        //Initial:  	 1
        //Applies to:  	 shapes and text content elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        doubleValue = toDouble(elem, readInheritAttribute(elem, "stroke-opacity", "1"), 1d, 0d, 1d);
        if (doubleValue != 1d) {
            Color c = (Color) a.get(SVGAttributeKeys.STROKE_COLOR);
            if (c != null) {
                a.put(SVGAttributeKeys.STROKE_COLOR,
                        new Color((c.getRGB() & 0xffffff) | ((int) (255 * doubleValue) << 24), true)
                        );
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
        doubleValue = toNumber(elem, readInheritAttribute(elem, "stroke-width", "1"));
        a.put(SVGAttributeKeys.STROKE_WIDTH, doubleValue);
    }
    /* Reads viewport attributes.
     */
    private void readViewportAttributes(IXMLElement elem, HashMap<AttributeKey,Object> a)
    throws IOException {
        Object value;
        //'viewport-fill'
        //Value:	 "none" | <color> | inherit
        //Initial:	 none
        //Applies to:	viewport-creating elements
        //Inherited:	 no
        //Percentages:	 N/A
        //Media:	 visual
        //Animatable:	 yes
        //Computed value:  	 "none" or specified <color> value, except inherit
        value = readAttribute(elem, "viewport-fill", "none");
        System.out.println("  viewport-fill="+value);
        
        //'viewport-fill-opacity'
        //Value:	<opacity-value> | inherit
        //Initial:	 1.0
        //Applies to:	viewport-creating elements
        //Inherited:	 no
        //Percentages:	 N/A
        //Media:	 visual
        //Animatable:	 yes
        //Computed value:  	 Specified value, except inherit
        value = readAttribute(elem, "viewport-fill-opacity", "1.0");
        System.out.println("  viewport-fill-opacity="+value);
        
    }
    /* Reads graphics attributes as listed in
     * http://www.w3.org/TR/SVGMobile12/feature.html#GraphicsAttribute
     */
    private void readGraphicsAttributes(IXMLElement elem, Figure f)
    throws IOException {
        Object value;
        // 'display'
        // Value:  	 inline | block | list-item |
        // run-in | compact | marker |
        // table | inline-table | table-row-group | table-header-group |
        // table-footer-group | table-row | table-column-group | table-column |
        // table-cell | table-caption | none | inherit
        // Initial:  	 inline
        // Applies to:  	 'svg' , 'g' , 'switch' , 'a' , 'foreignObject' , graphics elements (including the text content block elements) and text sub-elements (e.g., 'tspan' and 'a' )
        // Inherited:  	 no
        // Percentages:  	 N/A
        // Media:  	 all
        // Animatable:  	 yes
        // Computed value:  	 Specified value, except inherit
        value = readAttribute(elem, "display", "inline");
        System.out.println("display="+value);
        
        
        //'image-rendering'
        //Value:  	 auto | optimizeSpeed | optimizeQuality | inherit
        //Initial:  	 auto
        //Applies to:  	 images
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        value = readInheritAttribute(elem, "image-rendering", "auto");
        System.out.println("image-rendering="+value);
        
        //'pointer-events'
        //Value:  	boundingBox | visiblePainted | visibleFill | visibleStroke | visible |
        //painted | fill | stroke | all | none | inherit
        //Initial:  	visiblePainted
        //Applies to:  	graphics elements
        //Inherited:  	yes
        //Percentages:  	N/A
        //Media:  	visual
        //Animatable:  	yes
        //Computed value:  	Specified value, except inherit
        value = readInheritAttribute(elem, "pointer-events", "visiblePainted");
        System.out.println("pointer-events="+value);
        
        // 'shape-rendering'
        //Value:  	 auto | optimizeSpeed | crispEdges |
        //geometricPrecision | inherit
        //Initial:  	 auto
        //Applies to:  	 shapes
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        value = readInheritAttribute(elem, "shape-rendering", "auto");
        System.out.println("shape-rendering="+value);
        
        //'text-rendering'
        //Value:  	 auto | optimizeSpeed | optimizeLegibility |
        //geometricPrecision | inherit
        //Initial:  	 auto
        //Applies to:  	text content block elements
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        value = readInheritAttribute(elem, "text-rendering", "auto");
        System.out.println("text-rendering="+value);
        
        //'vector-effect'
        //Value:  	 non-scaling-stroke | none | inherit
        //Initial:  	 none
        //Applies to:  	 graphics elements
        //Inherited:  	 no
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        value = readAttribute(elem, "vector-effect", "none");
        System.out.println("vector-effect="+value);
        
        //'visibility'
        //Value:  	 visible | hidden | collapse | inherit
        //Initial:  	 visible
        //Applies to:  	 graphics elements (including the text content block elements) and text sub-elements (e.g., 'tspan' and 'a' )
        //Inherited:  	 yes
        //Percentages:  	 N/A
        //Media:  	 visual
        //Animatable:  	 yes
        //Computed value:  	 Specified value, except inherit
        value = readInheritAttribute(elem, "visibility", null);
        System.out.println("visibility="+value);
    }
    /**
     * Reads an SVG "linearGradient" element.
     */
    private void readLinearGradientElement(IXMLElement elem)
    throws IOException {
        HashMap<AttributeKey,Object> a = new HashMap<AttributeKey,Object>();
        readCoreAttributes(elem, a);
        
        double x1 = toNumber(elem, readAttribute(elem, "x1", "0"));
        double y1 = toNumber(elem, readAttribute(elem, "y1", "0"));
        double x2 = toNumber(elem, readAttribute(elem, "x2", "1"));
        double y2 = toNumber(elem, readAttribute(elem, "y2", "0"));
        boolean isRelativeToFigureBounds = readAttribute(elem, "gradientUnits", "objectBoundingBox").equals("objectBoundingBox");
        
        ArrayList stops = elem.getChildrenNamed("stop",SVG_NAMESPACE);
        if (stops.size() == 0) {
            stops = elem.getChildrenNamed("stop");
        }
        
        double[] stopOffsets = new double[stops.size()];
        Color[] stopColors = new Color[stops.size()];
        for (int i=0; i < stops.size(); i++) {
            IXMLElement stopElem = (IXMLElement) stops.get(i);
            stopOffsets[i] = toDouble(stopElem, readAttribute(stopElem, "offset", "0"), 0, 0, 1);
            // 'stop-color'
            // Value:  	currentColor | <color> | inherit
            // Initial:  	black
            // Applies to:  	 'stop' elements
            // Inherited:  	no
            // Percentages:  	N/A
            // Media:  	visual
            // Animatable:  	yes
            // Computed value:  	 Specified <color> value, except i
            stopColors[i] = toColor(stopElem, readAttribute(stopElem, "stop-color", "black"));
            //'stop-opacity'
            //Value:  	<opacity-value> | inherit
            //Initial:  	1
            //Applies to:  	 'stop' elements
            //Inherited:  	no
            //Percentages:  	N/A
            //Media:  	visual
            //Animatable:  	yes
            //Computed value:  	 Specified value, except inherit
            double doubleValue = toDouble(stopElem, readAttribute(stopElem, "stop-opacity", "1"), 1, 0, 1);
            if (doubleValue != 1) {
                stopColors[i] = new Color(((int) (doubleValue * 255) << 24) | (stopColors[i].getRGB() & 0xffffff), true);
            }
        }
        Gradient gradient = factory.createLinearGradient(
                x1, y1, x2, y2,
                stopOffsets, stopColors,
                isRelativeToFigureBounds
                );
        elementObjects.put(elem, gradient);
    }
    /**
     * Reads an SVG "radialGradient" element.
     */
    private void readRadialGradientElement(IXMLElement elem)
    throws IOException {
        HashMap<AttributeKey,Object> a = new HashMap<AttributeKey,Object>();
        readCoreAttributes(elem, a);
        
        // XXX - Implement me
        double cx = toNumber(elem, readAttribute(elem, "cx", "0"));
        double cy = toNumber(elem, readAttribute(elem, "cy", "0"));
        double r = toNumber(elem, readAttribute(elem, "r", "0.5"));
        boolean isRelativeToFigureBounds = readAttribute(elem, "gradientUnits", "objectBoundingBox").equals("objectBoundingBox");
        
        ArrayList stops = elem.getChildrenNamed("stop",SVG_NAMESPACE);
        if (stops.size() == 0) {
            stops = elem.getChildrenNamed("stop");
        }
        
        double[] stopOffsets = new double[stops.size()];
        Color[] stopColors = new Color[stops.size()];
        for (int i=0; i < stops.size(); i++) {
            IXMLElement stopElem = (IXMLElement) stops.get(i);
            stopOffsets[i] = toDouble(stopElem, readAttribute(stopElem, "offset", "0"), 0, 0, 1);
            // 'stop-color'
            // Value:  	currentColor | <color> | inherit
            // Initial:  	black
            // Applies to:  	 'stop' elements
            // Inherited:  	no
            // Percentages:  	N/A
            // Media:  	visual
            // Animatable:  	yes
            // Computed value:  	 Specified <color> value, except i
            stopColors[i] = toColor(stopElem, readAttribute(stopElem, "stop-color", "black"));
            //'stop-opacity'
            //Value:  	<opacity-value> | inherit
            //Initial:  	1
            //Applies to:  	 'stop' elements
            //Inherited:  	no
            //Percentages:  	N/A
            //Media:  	visual
            //Animatable:  	yes
            //Computed value:  	 Specified value, except inherit
            double doubleValue = toDouble(stopElem, readAttribute(stopElem, "stop-opacity", "1"), 1, 0, 1);
            if (doubleValue != 1) {
                stopColors[i] = new Color(((int) (doubleValue * 255) << 24) | (stopColors[i].getRGB() & 0xffffff), true);
            }
        }
        
        Gradient gradient = factory.createRadialGradient(
                cx, cy, r,
                stopOffsets, stopColors,
                isRelativeToFigureBounds
                );
        elementObjects.put(elem, gradient);
    }
    /* Reads font attributes as listed in
     * http://www.w3.org/TR/SVGMobile12/feature.html#Font
     */
    private void readFontAttributes(IXMLElement elem, HashMap<AttributeKey,Object> a)
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
        value = readInheritAttribute(elem, "font-family", "Dialog");
        a.put(AttributeKeys.FONT_FACE, new Font(value, Font.PLAIN, 12));
        
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
        doubleValue = readInheritFontSizeAttribute(elem, "font-size", "medium");
        a.put(AttributeKeys.FONT_SIZE, doubleValue);
        
        // 'font-style'
        // Value:  	normal | italic | oblique | inherit
        // Initial:  	normal
        // Applies to:  	text content elements
        // Inherited:  	yes
        // Percentages:  	N/A
        // Media:  	visual
        // Animatable:  	yes
        // Computed value:  	 Specified value, except inherit
        value = readInheritAttribute(elem, "font-style", "normal");
        System.out.println("font-style="+value);
        
        
        //'font-variant'
        //Value:  	normal | small-caps | inherit
        //Initial:  	normal
        //Applies to:  	text content elements
        //Inherited:  	yes
        //Percentages:  	N/A
        //Media:  	visual
        //Animatable:  	no
        //Computed value:  	 Specified value, except inherit
        value = readInheritAttribute(elem, "font-variant", "normal");
        System.out.println("font-variant="+value);
        
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
        value = readInheritAttribute(elem, "font-weight", "normal");
        System.out.println("font-weight="+value);
        
    }
    
    /**
     * Reads a paint style attribute. This can be a Color or a Gradient or null.
     * XXX - Doesn't support url(...) colors yet.
     */
    private Object toPaint(IXMLElement elem, String value) throws IOException {
        String str = value;
        if (str == null) {
            return null;
            /*
            if (elem.getParent() != null && elem.getParent().getNamespace().equals(SVG_NAMESPACE)) {
                return readPaint(elem.getParent(), attributeName);
            }*/
        }
        
        
        str = str.trim().toLowerCase();
        
        if (str.equals("none")) {
            return null;
        } else if (str.equals("currentColor")) {
            // XXX - This may cause endless recursion
            return toPaint(elem, readAttribute(elem, "color", "black"));
        } else if (SVG_COLORS.containsKey(str)) {
            return SVG_COLORS.get(str);
        } else if (str.startsWith("#") && str.length() == 7) {
            return new Color(Integer.decode(str));
        } else if (str.startsWith("#") && str.length() == 4) {
            // Three digits hex value
            int th = Integer.decode(str);
            return new Color(
                    (th & 0xf) | ((th & 0xf) << 4) |
                    ((th & 0xf0) << 4) | ((th & 0xf0) << 8) |
                    ((th & 0xf00) << 8) | ((th & 0xf00) << 12)
                    );
        } else if (str.startsWith("rgb")) {
            StringTokenizer tt = new StringTokenizer(str,"() ,");
            tt.nextToken();
            Color c = new Color(
                    Integer.decode(tt.nextToken()),
                    Integer.decode(tt.nextToken()),
                    Integer.decode(tt.nextToken())
                    );
            return c;
        } else if (str.startsWith("url(")) {
            String href = value.substring(4,value.length() - 1);
            if (identifiedElements.containsKey(href.substring(1))) {
                return elementObjects.get(identifiedElements.get(href.substring(1)));
            }
            // XXX - Implement me
            System.out.println("toPaint "+href);
            return null;
        } else {
            return null;
        }
    }
    /**
     * Reads a color style attribute. This can be a Color or null.
     * XXX - Doesn't support url(...) colors yet.
     */
    private Color toColor(IXMLElement elem, String value) throws IOException {
        String str = value;
        if (str == null) {
            return null;
            /*
            if (elem.getParent() != null && elem.getParent().getNamespace().equals(SVG_NAMESPACE)) {
                return readPaint(elem.getParent(), attributeName);
            }*/
        }
        
        str = str.trim().toLowerCase();
        
        if (str.equals("currentColor")) {
            // XXX - This may cause endless recursion
            return toColor(elem, readAttribute(elem, "color", "black"));
        } else if (SVG_COLORS.containsKey(str)) {
            return SVG_COLORS.get(str);
        } else if (str.startsWith("#") && str.length() == 7) {
            return new Color(Integer.decode(str));
        } else if (str.startsWith("#") && str.length() == 4) {
            // Three digits hex value
            int th = Integer.decode(str);
            return new Color(
                    (th & 0xf) | ((th & 0xf) << 4) |
                    ((th & 0xf0) << 4) | ((th & 0xf0) << 8) |
                    ((th & 0xf00) << 8) | ((th & 0xf00) << 12)
                    );
        } else if (str.startsWith("rgb")) {
            StringTokenizer tt = new StringTokenizer(str,"() ,");
            tt.nextToken();
            Color c = new Color(
                    Integer.decode(tt.nextToken()),
                    Integer.decode(tt.nextToken()),
                    Integer.decode(tt.nextToken())
                    );
            return c;
        } else if (str.startsWith("url")) {
            // XXX - Implement me
            System.out.println("toColor "+str);
            return null;
        } else {
            return null;
        }
    }
   /**
     * Reads a double attribute.
     */
    private double toDouble(IXMLElement elem, String value) throws IOException {
        return toDouble(elem, value, 0, Double.MIN_VALUE, Double.MAX_VALUE);
}
        /**
     * Reads a double attribute.
     */
    private double toDouble(IXMLElement elem, String value, double defaultValue, double min, double max) throws IOException {
        try {
            double d = Double.valueOf(value);
            return Math.max(Math.min(d, max), min);
        } catch (NumberFormatException e) {
            return defaultValue;
           /*
           IOException ex = new IOException(elem.getName()+"@"+elem.getLineNr()+" "+e.getMessage());
           ex.initCause(e);
           throw ex;*/
        }
    }
    /**
     * Reads a text attribute.
     * This method takes the "xml:space" attribute into account.
     * http://www.w3.org/TR/SVGMobile12/text.html#WhiteSpace
     */
    private String toText(IXMLElement elem, String value) throws IOException {
        String space = readInheritAttribute(elem,"xml:space","default");
        if (space.equals("default")) {
            return value.trim().replaceAll("\\s++"," ");
        } else /*if (space.equals("preserve"))*/ {
            return value;
        }
    }
    public static AffineTransform toTransform(IXMLElement elem, String str) throws IOException {
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
                        m[i] = tt.nval;
                        if (tt.nextToken() == StreamTokenizer.TT_WORD && tt.sval.startsWith("E")) {
                            double mantissa = tt.nval;
                            m[i] = Double.valueOf(m[i] + tt.sval);
                        } else {
                            tt.pushBack();
                        }
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
    
    public static void main(String[] args) {
        final Drawing drawing;
        try {
            File f = new File(
                    System.getProperty("user.home")+
                    //"/../Shared/Developer/Java/JHotDraw/Assets/SVG Examples/11 Painting",
                    // "13_01.svg"
                    //       "/../Shared/Developer/Java/JHotDraw/Assets/Batik Examples",
                    //       "chessboard.svg"
                    "/Desktop/Spirale.svg"
                    );
            SVGStorageFormat sf = new SVGStorageFormat();
            drawing = new DefaultDrawing();
            sf.read(new FileInputStream(f),drawing, new LinkedList<Figure>());
            
            
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                // Show the drawing
                JFrame f = new JFrame("My Drawing");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(400,300);
                
                DrawingView view = new DefaultDrawingView();
                view.setDrawing(drawing);
                f.getContentPane().add(view.getJComponent());
                
                f.show();
            }
        });
    }
    
    public String getMimeType() {
        return "image/svg+xml";
    }
    
    public javax.swing.filechooser.FileFilter getFileFilter() {
        return new ExtensionFileFilter("Scalable Vector Graphics (SVG)", "svg");
    }
    
    public boolean isWriteFormat() {
        return false;
    }
    
    public boolean isReadFormat() {
        return true;
    }
}
