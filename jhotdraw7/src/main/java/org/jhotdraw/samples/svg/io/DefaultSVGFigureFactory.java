/*
 * @(#)DefaultSVGFigureFactory.java  1.0  December 7, 2006
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

import java.awt.Color;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.text.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.geom.BezierPath;
import org.jhotdraw.samples.svg.*;
import org.jhotdraw.samples.svg.figures.*;

/**
 * DefaultSVGFigureFactory.
 *
 * @author Werner Randelshofer
 * @version 1.0 December 7, 2006 Created.
 */
public class DefaultSVGFigureFactory implements SVGFigureFactory {
    
    /** Creates a new instance. */
    public DefaultSVGFigureFactory() {
    }
    
    public Figure createRect(double x, double y, double w, double h, double rx, double ry, Map<AttributeKey, Object> a) {
        SVGRectFigure figure = new SVGRectFigure();
        figure.basicSetBounds(new Point2D.Double(x,y),new Point2D.Double(x+w,y+h));
        figure.setArc(rx, ry);
        figure.basicSetAttributes(a);
        return figure;
    }
    
    public Figure createCircle(double cx, double cy, double r, Map<AttributeKey, Object> a) {
        return createEllipse(cx, cy, r, r, a);
    }
    
    public Figure createEllipse(double cx, double cy, double rx, double ry, Map<AttributeKey, Object> a) {
        SVGEllipseFigure figure = new SVGEllipseFigure();
        figure.basicSetBounds(new Point2D.Double(cx-rx,cy-ry),new Point2D.Double(cx+rx,cy+ry));
        figure.basicSetAttributes(a);
        return figure;
    }
    
    public Figure createLine(
            double x1, double y1, double x2, double y2,
            Map<AttributeKey,Object> a) {
        SVGPathFigure figure = new SVGPathFigure();
        figure.removeAllChildren();
        BezierFigure bf = new BezierFigure();
        bf.addNode(new BezierPath.Node(x1, y1));
        bf.addNode(new BezierPath.Node(x2, y2));
        figure.add(bf);
        figure.basicSetAttributes(a);
        return figure;
    }
    
    public Figure createPolyline(Point2D.Double[] points, Map<AttributeKey, Object> a) {
        SVGPathFigure figure = new SVGPathFigure();
        figure.removeAllChildren();
        BezierFigure bf = new BezierFigure();
        for (int i=0; i < points.length; i++) {
            bf.addNode(new BezierPath.Node(points[i].x, points[i].y));
        }
        figure.add(bf);
        figure.basicSetAttributes(a);
        return figure;
    }
    
    public Figure createPolygon(Point2D.Double[] points, Map<AttributeKey, Object> a) {
        SVGPathFigure figure = new SVGPathFigure();
        figure.removeAllChildren();
        BezierFigure bf = new BezierFigure();
        for (int i=0; i < points.length; i++) {
            bf.addNode(new BezierPath.Node(points[i].x, points[i].y));
        }
        bf.setClosed(true);
        figure.add(bf);
        figure.basicSetAttributes(a);
        return figure;
    }
    public Figure createPath(BezierPath[] beziers, Map<AttributeKey, Object> a) {
        SVGPathFigure figure = new SVGPathFigure();
        figure.removeAllChildren();
        for (int i=0; i < beziers.length; i++) {
            BezierFigure bf = new BezierFigure();
            bf.basicSetBezierPath(beziers[i]);
            figure.add(bf);
        }
        figure.basicSetAttributes(a);
        return figure;
    }
    
    public CompositeFigure createG(Map<AttributeKey, Object> a) {
        SVGGroupFigure figure = new SVGGroupFigure();
        //figure.basicSetAttributes(a);
        return figure;
    }
    
    public Figure createImage(double x, double y, double w, double h, 
            byte[] imageData, BufferedImage bufferedImage, Map<AttributeKey, Object> a) {
        SVGImageFigure figure = new SVGImageFigure();
        figure.basicSetBounds(new Point2D.Double(x,y),new Point2D.Double(x+w,y+h));
        figure.setImage(imageData, bufferedImage);
        figure.basicSetAttributes(a);
        return figure;
    }
    public Figure createTextArea(double x, double y, double w, double h, StyledDocument doc, Map<AttributeKey, Object> attributes) {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();
        figure.basicSetBounds(new Point2D.Double(x,y),new Point2D.Double(x+w,y+h));
        try {
            figure.basicSetText(doc.getText(0, doc.getLength()));
        } catch (BadLocationException e) {
            InternalError ex = new InternalError(e.getMessage());
            ex.initCause(e);
            throw ex;
        }
        figure.basicSetAttributes(attributes);
        return figure;
    }
    
    public Figure createText(Point2D.Double[] coordinates, double[] rotates, StyledDocument text, Map<AttributeKey, Object> a) {
        SVGTextFigure figure = new SVGTextFigure();
        figure.basicSetCoordinates(coordinates);
        figure.basicSetRotates(rotates);
        try {
            figure.basicSetText(text.getText(0, text.getLength()));
        } catch (BadLocationException e) {
            InternalError ex = new InternalError(e.getMessage());
            ex.initCause(e);
            throw ex;
        }
        figure.basicSetAttributes(a);
        return figure;
    }
    
    public Gradient createRadialGradient(
            double cx, double cy, double r,
            double[] stopOffsets, Color[] stopColors,
            boolean isRelativeToFigureBounds) {
        RadialGradient g = new RadialGradient();
        g.setGradientCircle(cx, cy, r);
        g.setStops(stopOffsets, stopColors);
        g.setRelativeToFigureBounds(isRelativeToFigureBounds);
        return g;
    }
    
    public Gradient createLinearGradient(
            double x1, double y1, double x2, double y2,
            double[] stopOffsets, Color[] stopColors,
            boolean isRelativeToFigureBounds) {
        LinearGradient g = new LinearGradient();
        g.setGradientVector(x1, y1, x2, y2);
        g.setStops(stopOffsets, stopColors);
        g.setRelativeToFigureBounds(isRelativeToFigureBounds);
        return g;
    }

}
