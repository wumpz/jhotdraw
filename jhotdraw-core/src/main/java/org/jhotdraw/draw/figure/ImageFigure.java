/*
 * @(#)ImageFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import javax.imageio.ImageIO;
import javax.swing.Action;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.util.*;

/** A default implementation of {@link ImageHolderFigure} which can hold a buffered image. */
public class ImageFigure extends AbstractAttributedDecoratedFigure implements ImageHolderFigure {

  private static final long serialVersionUID = 1L;

  /** This rectangle describes the bounds into which we draw the image. */
  private Rectangle2D.Double rectangle;

  /** The image data. This can be null, if the image was created from a BufferedImage. */
  private byte[] imageData;

  /** The buffered image. This can be null, if we haven't yet parsed the imageData. */
  private transient BufferedImage bufferedImage;

  public ImageFigure() {
    this(0, 0, 0, 0);
  }

  public ImageFigure(double x, double y, double width, double height) {
    rectangle = new Rectangle2D.Double(x, y, width, height);
  }

  // DRAWING
  @Override
  protected void drawFigure(Graphics2D g) {
    if (attr().get(FILL_COLOR) != null) {
      g.setColor(attr().get(FILL_COLOR));
      drawFill(g);
    }
    drawImage(g);
    if (attr().get(STROKE_COLOR) != null && attr().get(STROKE_WIDTH) > 0d) {
      g.setStroke(AttributeKeys.getStroke(this, AttributeKeys.getScaleFactorFromGraphics(g)));
      g.setColor(attr().get(STROKE_COLOR));
      drawStroke(g);
    }
    if (attr().get(TEXT_COLOR) != null) {
      if (attr().get(TEXT_SHADOW_COLOR) != null && attr().get(TEXT_SHADOW_OFFSET) != null) {
        Dimension2DDouble d = attr().get(TEXT_SHADOW_OFFSET);
        g.translate(d.width, d.height);
        g.setColor(attr().get(TEXT_SHADOW_COLOR));
        drawText(g);
        g.translate(-d.width, -d.height);
      }
      g.setColor(attr().get(TEXT_COLOR));
      drawText(g);
    }
  }

  @Override
  protected void drawFill(Graphics2D g) {
    Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
    double grow =
        AttributeKeys.getPerpendicularFillGrowth(this, AttributeKeys.getScaleFactorFromGraphics(g));
    Geom.grow(r, grow, grow);
    g.fill(r);
  }

  protected void drawImage(Graphics2D g) {
    BufferedImage image = getBufferedImage();
    if (image != null) {
      g.drawImage(
          image,
          (int) rectangle.x,
          (int) rectangle.y,
          (int) rectangle.width,
          (int) rectangle.height,
          null);
    } else {
      g.setStroke(new BasicStroke());
      g.setColor(Color.red);
      g.draw(rectangle);
      g.draw(new Line2D.Double(
          rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height));
      g.draw(new Line2D.Double(
          rectangle.x + rectangle.width, rectangle.y, rectangle.x, rectangle.y + rectangle.height));
    }
  }

  @Override
  protected void drawStroke(Graphics2D g) {
    Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
    double grow =
        AttributeKeys.getPerpendicularDrawGrowth(this, AttributeKeys.getScaleFactorFromGraphics(g));
    Geom.grow(r, grow, grow);
    g.draw(r);
  }

  // SHAPE AND BOUNDS
  @Override
  public Rectangle2D.Double getBounds(double scale) {
    Rectangle2D.Double bounds = (Rectangle2D.Double) rectangle.clone();
    return bounds;
  }

  @Override
  public Rectangle2D.Double getFigureDrawingArea(double scale) {
    Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
    double grow = AttributeKeys.getPerpendicularHitGrowth(this, scale);
    Geom.grow(r, grow, grow);
    return r;
  }

  /** Checks if a Point2D.Double is inside the figure. */
  @Override
  public boolean figureContains(Point2D.Double p, double scale) {
    Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
    double grow = AttributeKeys.getPerpendicularHitGrowth(this, scale) + 1d;
    Geom.grow(r, grow, grow);
    return r.contains(p);
  }

  @Override
  public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
    rectangle.x = Math.min(anchor.x, lead.x);
    rectangle.y = Math.min(anchor.y, lead.y);
    rectangle.width = Math.max(0.1, Math.abs(lead.x - anchor.x));
    rectangle.height = Math.max(0.1, Math.abs(lead.y - anchor.y));
  }

  /**
   * Transforms the figure.
   *
   * @param tx The transformation.
   */
  @Override
  public void transform(AffineTransform tx) {
    Point2D.Double anchor = getStartPoint();
    Point2D.Double lead = getEndPoint();
    setBounds(
        (Point2D.Double) tx.transform(anchor, anchor), (Point2D.Double) tx.transform(lead, lead));
  }

  // ATTRIBUTES
  @Override
  public void restoreTransformTo(Object geometry) {
    rectangle.setRect((Rectangle2D.Double) geometry);
  }

  @Override
  public Object getTransformRestoreData() {
    return (Rectangle2D.Double) rectangle.clone();
  }

  // EDITING
  @Override
  public Collection<Action> getActions(Point2D.Double p) {
    return Collections.emptyList();
  }

  // CONNECTING
  @Override
  public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
    // XXX - This doesn't work with a transformed rect
    return new ChopRectangleConnector(this);
  }

  @Override
  public Connector findCompatibleConnector(Connector c, boolean isStartConnector) {
    // XXX - This doesn't work with a transformed rect
    return new ChopRectangleConnector(this);
  }

  // COMPOSITE FIGURES
  // CLONING
  @Override
  public ImageFigure clone() {
    ImageFigure that = (ImageFigure) super.clone();
    that.rectangle = (Rectangle2D.Double) this.rectangle.clone();
    return that;
  }

  /**
   * Sets the image.
   *
   * @param imageData The image data. If this is null, a buffered image must be provided.
   * @param bufferedImage An image constructed from the imageData. If this is null, imageData must
   *     be provided.
   */
  @Override
  public void setImage(byte[] imageData, BufferedImage bufferedImage) {
    willChange();
    this.imageData = imageData;
    this.bufferedImage = bufferedImage;
    changed();
  }

  /**
   * Sets the image data. This clears the buffered image.
   *
   * <p>Note: For performance reasons this method stores a reference to the imageData array instead
   * of cloning it. Do not modify the image data array after invoking this method.
   */
  public void setImageData(byte[] imageData) {
    willChange();
    this.imageData = imageData;
    this.bufferedImage = null;
    changed();
  }

  /** Sets the buffered image. This clears the image data. */
  @Override
  public void setBufferedImage(BufferedImage image) {
    willChange();
    this.imageData = null;
    this.bufferedImage = image;
    changed();
  }

  /**
   * Gets the buffered image. If necessary, this method creates the buffered image from the image
   * data.
   */
  @Override
  public BufferedImage getBufferedImage() {
    if (bufferedImage == null && imageData != null) {
      try {
        bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
      } catch (IOException e) {
        e.printStackTrace();
        // If we can't create a buffered image from the image data,
        // there is no use to keep the image data and try again, so
        // we drop the image data.
        imageData = null;
      }
    }
    return bufferedImage;
  }

  /**
   * Gets the image data. If necessary, this method creates the image data from the buffered image.
   *
   * <p>Note: For performance reasons this method returns a reference to the internally used image
   * data array instead of cloning it. Do not modify this array.
   */
  @Override
  public byte[] getImageData() {
    if (bufferedImage != null && imageData == null) {
      try {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "PNG", bout);
        bout.close();
        imageData = bout.toByteArray();
      } catch (IOException e) {
        e.printStackTrace();
        // If we can't create image data from the buffered image,
        // there is no use to keep the buffered image and try again, so
        // we drop the buffered image.
        bufferedImage = null;
      }
    }
    return imageData;
  }

  @Override
  public void loadImage(File file) throws IOException {
    try (InputStream in = new FileInputStream(file)) {
      loadImage(in);
    } catch (Throwable t) {
      ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
      IOException e =
          new IOException(labels.getFormatted("file.failedToLoadImage.message", file.getName()));
      e.initCause(t);
      throw e;
    }
  }

  @Override
  public void loadImage(InputStream in) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buf = new byte[512];
    int bytesRead;
    while ((bytesRead = in.read(buf)) > 0) {
      baos.write(buf, 0, bytesRead);
    }
    BufferedImage img = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
    if (img == null) {
      ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
      throw new IOException(labels.getFormatted("file.failedToLoadImage.message", in.toString()));
    }
    imageData = baos.toByteArray();
    bufferedImage = img;
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    // The call to getImageData() ensures that we have serializable data
    // in the imageData array.
    getImageData();
    out.defaultWriteObject();
  }
}
