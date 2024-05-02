/*
 * @(#)ImageMapOutputFormat.java
 *
 * Copyright (c) 2007-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg.io;

import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.geom.*;
import java.io.*;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jhotdraw.datatransfer.InputStreamTransferable;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.BezierFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.io.OutputFormat;
import org.jhotdraw.geom.GrowStroke;
import org.jhotdraw.samples.svg.figures.SVGBezierFigure;
import org.jhotdraw.samples.svg.figures.SVGEllipseFigure;
import org.jhotdraw.samples.svg.figures.SVGFigure;
import org.jhotdraw.samples.svg.figures.SVGGroupFigure;
import org.jhotdraw.samples.svg.figures.SVGImageFigure;
import org.jhotdraw.samples.svg.figures.SVGPathFigure;
import org.jhotdraw.samples.svg.figures.SVGRectFigure;
import org.jhotdraw.samples.svg.figures.SVGTextAreaFigure;
import org.jhotdraw.samples.svg.figures.SVGTextFigure;
import org.jhotdraw.util.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * ImageMapOutputFormat exports a SVG drawing as an HTML 4.01 <code>MAP</code> element. For more
 * information see: http://www.w3.org/TR/html401/struct/objects.html#h-13.6.2
 */
public class ImageMapOutputFormat implements OutputFormat {

  /** The affine transformation for the output. This is used to create scaled image maps. */
  private AffineTransform drawingTransform = new AffineTransform();

  /**
   * Set this to true, if AREA elements with <code>nohref="true"</code> shall e included in the
   * image map.
   */
  private boolean isIncludeNohref = false;

  /** Image dimension. We only include AREA elements which are within the image dimension. */
  private Rectangle bounds = new Rectangle(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);

  public ImageMapOutputFormat() {}

  @Override
  public javax.swing.filechooser.FileFilter getFileFilter() {
    return new FileNameExtensionFilter("HTML Image Map", "html");
  }

  @Override
  public String getFileExtension() {
    return "html";
  }

  @Override
  public void write(URI uri, Drawing drawing) throws IOException {
    write(new File(uri), drawing);
  }

  public void write(File file, Drawing drawing) throws IOException {
    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
    try {
      write(out, drawing);
    } finally {
      out.close();
    }
  }

  @Override
  public void write(OutputStream out, Drawing drawing) throws IOException {
    write(out, drawing.getChildren());
  }

  /**
   * Writes the drawing to the specified output stream. This method applies the specified
   * drawingTransform to the drawing, and draws it on an image of the specified getChildCount.
   */
  public void write(
      OutputStream out, Drawing drawing, AffineTransform drawingTransform, Dimension imageSize)
      throws IOException {
    write(out, drawing.getChildren(), drawingTransform, imageSize);
  }

  /**
   * Writes the figures to the specified output stream. This method applies the specified
   * drawingTransform to the drawing, and draws it on an image of the specified getChildCount.
   *
   * <p>All other write methods delegate their work to here.
   */
  public void write(
      OutputStream out,
      java.util.List<Figure> figures,
      AffineTransform drawingTransform,
      Dimension imageSize)
      throws IOException {
    this.drawingTransform = (drawingTransform == null) ? new AffineTransform() : drawingTransform;
    this.bounds = (imageSize == null)
        ? new Rectangle(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE)
        : new Rectangle(0, 0, imageSize.width, imageSize.height);
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder;
    try {
      dBuilder = dbFactory.newDocumentBuilder();
    } catch (ParserConfigurationException ex) {
      Logger.getLogger(ImageMapOutputFormat.class.getName()).log(Level.SEVERE, null, ex);
      throw new IOException(ex);
    }
    Element document = dBuilder.newDocument().createElement("map");
    // Note: Image map elements need to be written from front to back
    for (Figure f : new ReversedList<Figure>(figures)) {
      writeElement(document, f);
    }
    // Strip AREA elements with "nohref" attributes from the end of the
    // map
    if (!isIncludeNohref) {
      NodeList list = document.getChildNodes();
      for (int i = list.getLength() - 1; i >= 0; i--) {
        Element child = (Element) list.item(i);
        if (child.hasAttribute("nohref")) {
          document.removeChild(child);
        }
      }
    }
    try {
      // Write XML content
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer t = tf.newTransformer();
      NodeList list = document.getChildNodes();
      for (int i = list.getLength() - 1; i >= 0; i--) {
        Element child = (Element) list.item(i);
        t.transform(new DOMSource(child), new StreamResult(out));
      }
    } catch (TransformerException ex) {
      Logger.getLogger(ImageMapOutputFormat.class.getName()).log(Level.SEVERE, null, ex);
      throw new IOException(ex);
    }
  }

  /** All other write methods delegate their work to here. */
  public void write(OutputStream out, java.util.List<Figure> figures) throws IOException {
    Rectangle2D.Double drawingRect = null;
    for (Figure f : figures) {
      if (drawingRect == null) {
        drawingRect = f.getBounds();
      } else {
        drawingRect.add(f.getBounds());
      }
    }
    AffineTransform tx = new AffineTransform();
    tx.translate(-Math.min(0, drawingRect.x), -Math.min(0, drawingRect.y));
    write(out, figures, tx, new Dimension((int) (Math.abs(drawingRect.x) + drawingRect.width), (int)
        (Math.abs(drawingRect.y) + drawingRect.height)));
  }

  @Override
  public Transferable createTransferable(
      Drawing drawing, java.util.List<Figure> figures, double scaleFactor) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    write(buf, figures);
    return new InputStreamTransferable(
        new DataFlavor("text/html", "HTML Image Map"), buf.toByteArray());
  }

  protected void writeElement(Element parent, Figure f) throws IOException {
    if (f instanceof SVGEllipseFigure) {
      writeEllipseElement(parent, (SVGEllipseFigure) f);
    } else if (f instanceof SVGGroupFigure) {
      writeGElement(parent, (SVGGroupFigure) f);
    } else if (f instanceof SVGImageFigure) {
      writeImageElement(parent, (SVGImageFigure) f);
    } else if (f instanceof SVGPathFigure) {
      SVGPathFigure path = (SVGPathFigure) f;
      if (path.getChildCount() == 1) {
        BezierFigure bezier = (BezierFigure) path.getChild(0);
        boolean isLinear = true;
        for (int i = 0, n = bezier.getNodeCount(); i < n; i++) {
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
            } else {
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
      System.out.println("Unable to write: " + f);
    }
  }

  /**
   * Writes the <code>shape</code>, <code>coords</code>, <code>href</code>, <code>nohref</code>
   * Attribute for the specified figure and ellipse.
   *
   * @return Returns true, if the circle is inside of the image bounds.
   */
  private boolean writeCircleAttributes(Element elem, SVGFigure f, Ellipse2D.Double ellipse) {
    AffineTransform t = TRANSFORM.getClone(f);
    if (t == null) {
      t = drawingTransform;
    } else {
      t.preConcatenate(drawingTransform);
    }
    if ((t.getType() & (AffineTransform.TYPE_UNIFORM_SCALE | AffineTransform.TYPE_TRANSLATION))
            == t.getType()
        && ellipse.width == ellipse.height) {
      Point2D.Double start = new Point2D.Double(ellipse.x, ellipse.y);
      Point2D.Double end =
          new Point2D.Double(ellipse.x + ellipse.width, ellipse.y + ellipse.height);
      t.transform(start, start);
      t.transform(end, end);
      ellipse.x = Math.min(start.x, end.x);
      ellipse.y = Math.min(start.y, end.y);
      ellipse.width = Math.abs(start.x - end.x);
      ellipse.height = Math.abs(start.y - end.y);
      elem.setAttribute("shape", "circle");
      elem.setAttribute(
          "coords",
          (int) (ellipse.x + ellipse.width / 2d)
              + ","
              + (int) (ellipse.y + ellipse.height / 2d)
              + ","
              + (int) (ellipse.width / 2d));
      writeHrefAttribute(elem, f);
      return bounds.intersects(ellipse.getBounds());
    } else {
      return writePolyAttributes(elem, f, (Shape) ellipse);
    }
  }

  /**
   * Writes the <code>shape</code>, <code>coords</code>, <code>href</code>, <code>nohref</code>
   * Attribute for the specified figure and rectangle.
   *
   * @return Returns true, if the rect is inside of the image bounds.
   */
  private boolean writeRectAttributes(Element elem, SVGFigure f, Rectangle2D.Double rect) {
    AffineTransform t = TRANSFORM.getClone(f);
    if (t == null) {
      t = drawingTransform;
    } else {
      t.preConcatenate(drawingTransform);
    }
    if ((t.getType() & (AffineTransform.TYPE_UNIFORM_SCALE | AffineTransform.TYPE_TRANSLATION))
        == t.getType()) {
      Point2D.Double start = new Point2D.Double(rect.x, rect.y);
      Point2D.Double end = new Point2D.Double(rect.x + rect.width, rect.y + rect.height);
      t.transform(start, start);
      t.transform(end, end);
      Rectangle r = new Rectangle(
          (int) Math.min(start.x, end.x),
          (int) Math.min(start.y, end.y),
          (int) Math.abs(start.x - end.x),
          (int) Math.abs(start.y - end.y));
      elem.setAttribute("shape", "rect");
      elem.setAttribute("coords", r.x + "," + r.y + "," + (r.x + r.width) + "," + (r.y + r.height));
      writeHrefAttribute(elem, f);
      return bounds.intersects(r);
    } else {
      return writePolyAttributes(elem, f, (Shape) rect);
    }
  }

  private void writeHrefAttribute(Element elem, SVGFigure f) {
    String link = f.attr().get(LINK);
    if (link != null && link.trim().length() > 0) {
      elem.setAttribute("href", link);
      elem.setAttribute("title", link);
      elem.setAttribute("alt", link);
      String linkTarget = f.attr().get(LINK_TARGET);
      if (linkTarget != null && linkTarget.trim().length() > 0) {
        elem.setAttribute("target", linkTarget);
      }
    } else {
      elem.setAttribute("nohref", "true");
    }
  }

  /**
   * Writes the <code>shape</code>, <code>coords</code>, <code>href</code>, <code>nohref</code>
   * Attribute for the specified figure and shape.
   *
   * @return Returns true, if the polygon is inside of the image bounds.
   */
  private boolean writePolyAttributes(Element elem, SVGFigure f, Shape shape) {
    AffineTransform t = TRANSFORM.getClone(f);
    if (t == null) {
      t = drawingTransform;
    } else {
      t.preConcatenate(drawingTransform);
    }
    StringBuilder buf = new StringBuilder();
    float[] coords = new float[6];
    Path2D.Double path = new Path2D.Double();
    for (PathIterator i = shape.getPathIterator(t, 1.5f); !i.isDone(); i.next()) {
      switch (i.currentSegment(coords)) {
        case PathIterator.SEG_MOVETO:
          if (buf.length() != 0) {
            throw new IllegalArgumentException("Illegal shape " + shape);
          }
          if (buf.length() != 0) {
            buf.append(',');
          }
          buf.append((int) coords[0]);
          buf.append(',');
          buf.append((int) coords[1]);
          path.moveTo(coords[0], coords[1]);
          break;
        case PathIterator.SEG_LINETO:
          if (buf.length() != 0) {
            buf.append(',');
          }
          buf.append((int) coords[0]);
          buf.append(',');
          buf.append((int) coords[1]);
          path.lineTo(coords[0], coords[1]);
          break;
        case PathIterator.SEG_CLOSE:
          path.closePath();
          break;
        default:
          throw new InternalError("Illegal segment type " + i.currentSegment(coords));
      }
    }
    elem.setAttribute("shape", "poly");
    elem.setAttribute("coords", buf.toString());
    writeHrefAttribute(elem, f);
    return path.intersects(new Rectangle2D.Float(bounds.x, bounds.y, bounds.width, bounds.height));
  }

  private void writePathElement(Element parent, SVGPathFigure f) throws IOException {
    GrowStroke growStroke =
        new GrowStroke((getStrokeTotalWidth(f, 1.0) / 2d), getStrokeTotalWidth(f, 1.0));
    BasicStroke basicStroke = new BasicStroke((float) getStrokeTotalWidth(f, 1.0));
    for (Figure child : f.getChildren()) {
      SVGBezierFigure bezier = (SVGBezierFigure) child;
      Element elem = parent.getOwnerDocument().createElement("area");
      if (bezier.isClosed()) {
        writePolyAttributes(elem, f, growStroke.createStrokedShape(bezier.getBezierPath()));
      } else {
        writePolyAttributes(elem, f, basicStroke.createStrokedShape(bezier.getBezierPath()));
      }
      parent.appendChild(elem);
    }
  }

  private void writePolygonElement(Element parent, SVGPathFigure f) throws IOException {
    Element elem = parent.getOwnerDocument().createElement("area");
    if (writePolyAttributes(
        elem,
        f,
        new GrowStroke((getStrokeTotalWidth(f, 1.0) / 2d), getStrokeTotalWidth(f, 1.0))
            .createStrokedShape(f.getChild(0).getBezierPath()))) {
      parent.appendChild(elem);
    }
  }

  private void writePolylineElement(Element parent, SVGPathFigure f) throws IOException {
    Element elem = parent.getOwnerDocument().createElement("area");
    if (writePolyAttributes(
        elem,
        f,
        new BasicStroke((float) getStrokeTotalWidth(f, 1.0))
            .createStrokedShape(f.getChild(0).getBezierPath()))) {
      parent.appendChild(elem);
    }
  }

  private void writeLineElement(Element parent, SVGPathFigure f) throws IOException {
    Element elem = parent.getOwnerDocument().createElement("area");
    if (writePolyAttributes(
        elem,
        f,
        new GrowStroke((getStrokeTotalWidth(f, 1.0) / 2d), getStrokeTotalWidth(f, 1.0))
            .createStrokedShape(new Line2D.Double(f.getStartPoint(), f.getEndPoint())))) {
      parent.appendChild(elem);
    }
  }

  private void writeRectElement(Element parent, SVGRectFigure f) throws IOException {
    Element elem = parent.getOwnerDocument().createElement("AREA");
    boolean isContained;
    if (f.getArcHeight() == 0 && f.getArcWidth() == 0) {
      Rectangle2D.Double rect = f.getBounds();
      double grow = getPerpendicularHitGrowth(f, 1.0);
      rect.x -= grow;
      rect.y -= grow;
      rect.width += grow;
      rect.height += grow;
      isContained = writeRectAttributes(elem, f, rect);
    } else {
      isContained = writePolyAttributes(
          elem,
          f,
          new GrowStroke((getStrokeTotalWidth(f, 1.0) / 2d), getStrokeTotalWidth(f, 1.0))
              .createStrokedShape(new RoundRectangle2D.Double(
                  f.getX(),
                  f.getY(),
                  f.getWidth(),
                  f.getHeight(),
                  f.getArcWidth(),
                  f.getArcHeight())));
    }
    if (isContained) {
      parent.appendChild(elem);
    }
  }

  private void writeTextElement(Element parent, SVGTextFigure f) throws IOException {
    Element elem = parent.getOwnerDocument().createElement("AREA");
    Rectangle2D.Double rect = f.getBounds();
    double grow = getPerpendicularHitGrowth(f, 1.0);
    rect.x -= grow;
    rect.y -= grow;
    rect.width += grow;
    rect.height += grow;
    if (writeRectAttributes(elem, f, rect)) {
      parent.appendChild(elem);
    }
  }

  private void writeTextAreaElement(Element parent, SVGTextAreaFigure f) throws IOException {
    Element elem = parent.getOwnerDocument().createElement("AREA");
    Rectangle2D.Double rect = f.getBounds();
    double grow = getPerpendicularHitGrowth(f, 1.0);
    rect.x -= grow;
    rect.y -= grow;
    rect.width += grow;
    rect.height += grow;
    if (writeRectAttributes(elem, f, rect)) {
      parent.appendChild(elem);
    }
  }

  private void writeEllipseElement(Element parent, SVGEllipseFigure f) throws IOException {
    Element elem = parent.getOwnerDocument().createElement("area");
    Rectangle2D.Double r = f.getBounds();
    double grow = getPerpendicularHitGrowth(f, 1.0);
    Ellipse2D.Double ellipse =
        new Ellipse2D.Double(r.x - grow, r.y - grow, r.width + grow, r.height + grow);
    if (writeCircleAttributes(elem, f, ellipse)) {
      parent.appendChild(elem);
    }
  }

  private void writeGElement(Element parent, SVGGroupFigure f) throws IOException {
    // Note: Image map elements need to be written from front to back
    for (Figure child : new ReversedList<Figure>(f.getChildren())) {
      writeElement(parent, child);
    }
  }

  private void writeImageElement(Element parent, SVGImageFigure f) {
    Element elem = parent.getOwnerDocument().createElement("area");
    Rectangle2D.Double rect = f.getBounds();
    writeRectAttributes(elem, f, rect);
    parent.appendChild(elem);
  }
}
