/*
 * @(#)ImageOutputFormat.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.io;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.datatransfer.Transferable;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jhotdraw.datatransfer.ImageTransferable;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.io.OutputFormat;

/**
 * An output format for exporting drawings using one of the image formats supported by
 * javax.imageio.
 */
public class ImageOutputFormat implements OutputFormat {

  /** Format description used for the file filter. */
  private String description;

  /** File name extension used for the file filter. */
  private String fileExtension;

  /** Image IO image format name. */
  private String formatName;

  /**
   * The image type must match the output format, for example, PNG supports
   * BufferedImage.TYPE_INT_ARGB whereas GIF needs BufferedImage.TYPE_
   */
  private int imageType;

  /** Creates a new image output format for Portable Network Graphics PNG. */
  public ImageOutputFormat() {
    this("PNG", "Portable Network Graphics (PNG)", "png", BufferedImage.TYPE_INT_ARGB);
  }

  /**
   * Creates a new image output format for the specified image format.
   *
   * @param formatName The format name for the javax.imageio.ImageIO object.
   * @param description The format description to be used for the file filter.
   * @param fileExtension The file extension to be used for file filter.
   * @param bufferedImageType The BufferedImage type used to produce the image. The value of this
   *     parameter must match with the format name.
   */
  public ImageOutputFormat(
      String formatName, String description, String fileExtension, int bufferedImageType) {
    this.formatName = formatName;
    this.description = description;
    this.fileExtension = fileExtension;
    this.imageType = bufferedImageType;
  }

  @Override
  public javax.swing.filechooser.FileFilter getFileFilter() {
    return new FileNameExtensionFilter(description, fileExtension);
  }

  @Override
  public String getFileExtension() {
    return fileExtension;
  }

  @Override
  public void write(URI uri, Drawing drawing) throws IOException {
    write(new File(uri), drawing);
  }

  /**
   * Writes the drawing to the specified file. This method ensures that all figures of the drawing
   * are visible on the image.
   */
  public void write(File file, Drawing drawing) throws IOException {
    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
      write(out, drawing);
    }
  }

  /**
   * Writes the drawing to the specified output stream. This method ensures that all figures of the
   * drawing are visible on the image.
   */
  @Override
  public void write(OutputStream out, Drawing drawing) throws IOException {
    write(out, drawing, drawing.getChildren(), null, null);
  }

  /**
   * Writes the drawing to the specified output stream. This method applies the specified transform
   * to the drawing, and draws it on an image of the specified size.
   */
  public void write(
      OutputStream out, Drawing drawing, AffineTransform drawingTransform, Dimension imageSize)
      throws IOException {
    write(out, drawing, drawing.getChildren(), drawingTransform, imageSize);
  }

  /**
   * Writes the drawing to the specified output stream. This method ensures that all figures of the
   * drawing are visible on the image.
   */
  @Override
  public Transferable createTransferable(
      Drawing drawing, java.util.List<Figure> figures, double scaleFactor) throws IOException {
    return new ImageTransferable(toImage(drawing, figures, scaleFactor, true));
  }

  /**
   * Writes the figures to the specified output stream. This method ensures that all figures of the
   * drawing are visible on the image.
   */
  public void write(OutputStream out, Drawing drawing, java.util.List<Figure> figures)
      throws IOException {
    write(out, drawing, figures, null, null);
  }

  /**
   * Writes the figures to the specified output stream. This method applies the specified transform
   * to the drawing, and draws it on an image of the specified size.
   */
  public void write(
      OutputStream out,
      Drawing drawing,
      java.util.List<Figure> figures,
      AffineTransform drawingTransform,
      Dimension imageSize)
      throws IOException {
    BufferedImage img;
    if (drawingTransform == null || imageSize == null) {
      img = toImage(drawing, figures, 1d, false);
    } else {
      img = toImage(drawing, figures, drawingTransform, imageSize);
    }
    ImageIO.write(img, formatName, out);
    img.flush();
  }

  /**
   * Creates a BufferedImage from the specified list of figures.
   *
   * <p>The images are drawn using the specified scale factor. If some figures have a drawing area
   * located at negative coordinates, then the drawing coordinates are translated, so that all
   * figures are visible on the image.
   *
   * @param drawing The drawing.
   * @param figures A list of figures of the drawing.
   * @param scaleFactor The scale factor used when drawing the figures.
   * @param clipToFigures If this is true, the image is clipped to the figures. If this is false,
   *     the image includes the drawing area,
   */
  public BufferedImage toImage(
      Drawing drawing, java.util.List<Figure> figures, double scaleFactor, boolean clipToFigures) {
    // Return a transparent 1-pixel image if the drawing is empty.
    if (drawing.getChildCount() == 0) {
      return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }
    // Determine the draw bounds of the figures
    Rectangle2D.Double drawBounds = null;
    for (Figure f : figures) {
      if (drawBounds == null) {
        drawBounds = f.getDrawingArea();
      } else {
        drawBounds.add(f.getDrawingArea());
      }
    }
    if (clipToFigures) {
      AffineTransform transform = new AffineTransform();
      transform.translate(-drawBounds.x * scaleFactor, -drawBounds.y * scaleFactor);
      transform.scale(scaleFactor, scaleFactor);
      return toImage(
          drawing, figures, transform, new Dimension((int) (drawBounds.width * scaleFactor), (int)
              (drawBounds.height * scaleFactor)));
    } else {
      AffineTransform transform = new AffineTransform();
      if (drawBounds.x < 0) {
        transform.translate(-drawBounds.x * scaleFactor, 0);
      }
      if (drawBounds.y < 0) {
        transform.translate(0, -drawBounds.y * scaleFactor);
      }
      transform.scale(scaleFactor, scaleFactor);
      return toImage(
          drawing,
          figures,
          transform,
          new Dimension((int) ((Math.max(0, drawBounds.x) + drawBounds.width) * scaleFactor), (int)
              ((Math.max(0, drawBounds.y) + drawBounds.height) * scaleFactor)));
    }
  }

  /**
   * Creates a BufferedImage from the specified list of figures.
   *
   * @param drawing The drawing.
   * @param figures A list of figures of the drawing.
   * @param transform The AffineTransform to be used when drawing the figures.
   * @param imageSize The width and height of the image.
   */
  public BufferedImage toImage(
      Drawing drawing,
      java.util.List<Figure> figures,
      AffineTransform transform,
      Dimension imageSize) {
    // Create the buffered image and clear it
    Color background = drawing.attr().get(CANVAS_FILL_COLOR);
    double opacity = drawing.attr().get(CANVAS_FILL_OPACITY);
    if (background == null) {
      background = new Color(0xff, 0xff, 0xff, 0x0);
    } else {
      background = new Color(background.getRed(), background.getGreen(), background.getBlue(), (int)
          (background.getAlpha() * opacity));
    }
    BufferedImage buf = new BufferedImage(
        Math.max(1, imageSize.width),
        Math.max(1, imageSize.height),
        (background.getAlpha() == 255) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = buf.createGraphics();
    // Clear the buffered image with the background color
    Composite savedComposite = g.getComposite();
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
    g.setColor(background);
    g.fillRect(0, 0, buf.getWidth(), buf.getHeight());
    g.setComposite(savedComposite);
    // Draw the figures onto the buffered image
    setRenderingHints(g);
    g.transform(transform);
    for (Figure f : figures) {
      f.draw(g);
    }
    g.dispose();
    // Convert the image, if it does not have the specified image type
    if (imageType != BufferedImage.TYPE_INT_ARGB) {
      BufferedImage buf2 = new BufferedImage(buf.getWidth(), buf.getHeight(), imageType);
      g = buf2.createGraphics();
      setRenderingHints(g);
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
      g.drawImage(buf, 0, 0, null);
      g.dispose();
      buf.flush();
      buf = buf2;
    }
    return buf;
  }

  protected void setRenderingHints(Graphics2D g) {
    g.setRenderingHint(
        RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(
        RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    g.setRenderingHint(
        RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    g.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
    g.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
  }
}
