/*
 * @(#)ImageInputFormat.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.io;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.ImageHolderFigure;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.utils.util.Images;

/**
 * An input format for importing drawings using one of the image formats supported by javax.imageio.
 *
 * <p>This class uses the prototype design pattern. A ImageHolderFigure figure is used as a
 * prototype for creating a figure that holds the imported image.
 *
 * <p>If the drawing is replaced using the loaded image, the size of the drawing is set to match the
 * size of the image using the attributes {@code AttributeKeys.CANVAS_WIDTH} and {@code
 * AttributeKeys.CANVAS_HEIGHT}.
 *
 * <p><hr> <b>Design Patterns</b>
 *
 * <p><em>Prototype</em><br>
 * The image input format creates new image holder figures by cloning a prototype figure object and
 * assigning an image to it, which was read from data input. That's the reason why {@code Figure}
 * extends the {@code Cloneable} interface. <br>
 * Prototype: {@link org.jhotdraw.draw.ImageHolderFigure}; Client: {@link ImageInputFormat}. <hr>
 *
 * @author Werner Randelshor
 * @version $Id$
 */
public class ImageInputFormat implements InputFormat {

  /** The prototype for creating a figure that holds the imported image. */
  private ImageHolderFigure prototype;

  /** Format description used for the file filter. */
  private String description;

  /** File name extension used for the file filter. */
  private String[] fileExtensions;

  /** Image IO image format name. */
  private String formatName;

  /** The mime types which must be matched. */
  private String[] mimeTypes;

  /**
   * Creates a new image input format for all formats supported by {@code javax.imageio.ImageIO}.
   */
  public ImageInputFormat(ImageHolderFigure prototype) {
    this(
        prototype, "Image", "Image", ImageIO.getReaderFileSuffixes(), ImageIO.getReaderMIMETypes());
  }

  /**
   * Creates a new image input format for the specified image format.
   *
   * @param formatName The format name for the javax.imageio.ImageIO object.
   * @param description The format description to be used for the file filter.
   * @param fileExtension The file extension to be used for the file filter.
   * @param mimeType The mime type used for filtering data flavors from Transferable objects.
   */
  public ImageInputFormat(
      ImageHolderFigure prototype,
      String formatName,
      String description,
      String fileExtension,
      String mimeType) {
    this(prototype, formatName, description, new String[] {fileExtension}, new String[] {mimeType});
  }

  /**
   * Creates a new image input format for the specified image format.
   *
   * @param formatName The format name for the javax.imageio.ImageIO object.
   * @param description The format description to be used for the file filter.
   * @param fileExtensions The file extensions to be used for the file filter.
   * @param mimeTypes The mime typse used for filtering data flavors from Transferable objects.
   */
  public ImageInputFormat(
      ImageHolderFigure prototype,
      String formatName,
      String description,
      String fileExtensions[],
      String[] mimeTypes) {
    this.prototype = prototype;
    this.formatName = formatName;
    this.description = description;
    this.fileExtensions = fileExtensions.clone();
    this.mimeTypes = mimeTypes.clone();
  }

  @Override
  public javax.swing.filechooser.FileFilter getFileFilter() {
    return new FileNameExtensionFilter(description, fileExtensions);
  }

  public String[] getFileExtensions() {
    return fileExtensions.clone();
  }

  @Override
  public void read(InputStream in, Drawing drawing, boolean replace) throws IOException {
    ImageHolderFigure figure = createImageHolder(in);
    if (replace) {
      drawing.removeAllChildren();
      drawing.attr().set(CANVAS_WIDTH, figure.getBounds().width);
      drawing.attr().set(CANVAS_HEIGHT, figure.getBounds().height);
    }
    drawing.basicAdd(figure);
  }

  public ImageHolderFigure createImageHolder(InputStream in) throws IOException {
    ImageHolderFigure figure = (ImageHolderFigure) prototype.clone();
    figure.loadImage(in);
    figure.setBounds(
        new Point2D.Double(0, 0),
        new Point2D.Double(
            figure.getBufferedImage().getWidth(), figure.getBufferedImage().getHeight()));
    return figure;
  }

  @Override
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    if (DataFlavor.imageFlavor.match(flavor)) {
      return true;
    }
    for (String mimeType : mimeTypes) {
      if (flavor.isMimeTypeEqual(mimeType)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void read(Transferable t, Drawing drawing, boolean replace)
      throws UnsupportedFlavorException, IOException {
    DataFlavor importFlavor = null;
    SearchLoop:
    for (DataFlavor flavor : t.getTransferDataFlavors()) {
      if (DataFlavor.imageFlavor.match(flavor)) {
        importFlavor = flavor;
        break SearchLoop;
      }
      for (String mimeType : mimeTypes) {
        if (flavor.isMimeTypeEqual(mimeType)) {
          importFlavor = flavor;
          break SearchLoop;
        }
      }
    }
    Object data = t.getTransferData(importFlavor);
    Image img = null;
    if (data instanceof Image) {
      img = (Image) data;
    } else if (data instanceof InputStream) {
      img = ImageIO.read((InputStream) data);
    }
    if (img == null) {
      throw new IOException("Unsupported data format " + importFlavor);
    }
    ImageHolderFigure figure = (ImageHolderFigure) prototype.clone();
    figure.setBufferedImage(Images.toBufferedImage(img));
    figure.setBounds(
        new Point2D.Double(0, 0),
        new Point2D.Double(
            figure.getBufferedImage().getWidth(), figure.getBufferedImage().getHeight()));
    List<Figure> list = List.of(figure);
    if (replace) {
      drawing.removeAllChildren();
      drawing.attr().set(CANVAS_WIDTH, figure.getBounds().width);
      drawing.attr().set(CANVAS_HEIGHT, figure.getBounds().height);
    }
    drawing.addAll(list);
  }
}
