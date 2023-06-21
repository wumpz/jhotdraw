/*
 * @(#)TextInputFormat.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.io;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.TextHolderFigure;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.geom.Dimension2DDouble;

/**
 * An input format for importing text into a drawing.
 *
 * <p>This class uses the prototype design pattern. A TextHolderFigure figure is used as a prototype
 * for creating a figure that holds the imported text.
 *
 * <p>For text that spans multiple lines, TextInputFormat can either add all the text to the same
 * Figure, or it can create a new Figure for each line.
 *
 * <p><hr> <b>Design Patterns</b>
 *
 * <p><em>Prototype</em><br>
 * The text input format creates new text holder figures by cloning a prototype figure object and
 * assigning an image to it, which was read from data input. That's the reason why {@code Figure}
 * extends the {@code Cloneable} interface. <br>
 * Prototype: {@link TextHolderFigure}; Client: {@link org.jhotdraw.io.TextInputFormat}. <hr>
 */
public class TextInputFormat implements InputFormat {

  /** The prototype for creating a figure that holds the imported text. */
  private TextHolderFigure prototype;
  /** Format description used for the file filter. */
  private String description;
  /** File name extension used for the file filter. */
  private String fileExtension;
  /** Image IO image format name. */
  private String formatName;
  /** This should be set to true for ImageHolderFigures that can hold multiple lines of text. */
  private boolean isMultiline;

  /**
   * Creates a new image output format for text, for a figure that can not. hold multiple lines of
   * text.
   */
  public TextInputFormat(TextHolderFigure prototype) {
    this(prototype, "Text", "Text", "txt", false);
  }

  /**
   * Creates a new image output format for the specified image format.
   *
   * @param formatName The format name for the javax.imageio.ImageIO object.
   * @param description The format description to be used for the file filter.
   * @param fileExtension The file extension to be used for file filter.
   * @param isMultiline Set this to true, if the TextHolderFigure can hold multiple lines of text.
   *     If this is true, multiple lines of text are added to the same figure. If this is false, a
   *     new Figure is created for each line of text.
   */
  public TextInputFormat(
      TextHolderFigure prototype,
      String formatName,
      String description,
      String fileExtension,
      boolean isMultiline) {
    this.prototype = prototype;
    this.formatName = formatName;
    this.description = description;
    this.fileExtension = fileExtension;
    this.isMultiline = isMultiline;
  }

  @Override
  public javax.swing.filechooser.FileFilter getFileFilter() {
    return new FileNameExtensionFilter(description, fileExtension);
  }

  public String getFileExtension() {
    return fileExtension;
  }

  @Override
  public void read(InputStream in, Drawing drawing, boolean replace) throws IOException {
    if (replace) {
      drawing.removeAllChildren();
    }
    drawing.basicAddAll(0, createTextHolderFigures(in));
  }

  public List<Figure> createTextHolderFigures(InputStream in) throws IOException {
    List<Figure> list = new ArrayList<>();
    BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF8"));
    if (isMultiline) {
      TextHolderFigure figure = (TextHolderFigure) prototype.clone();
      StringBuilder buf = new StringBuilder();
      for (String line = null; line != null; line = r.readLine()) {
        if (buf.length() != 0) {
          buf.append('\n');
        }
        buf.append(line);
      }
      figure.setText(buf.toString());
      Dimension2DDouble s = figure.getPreferredSize();
      figure.setBounds(new Point2D.Double(0, 0), new Point2D.Double(s.width, s.height));
    } else {
      double y = 0;
      for (String line = null; line != null; line = r.readLine()) {
        TextHolderFigure figure = (TextHolderFigure) prototype.clone();
        figure.setText(line);
        Dimension2DDouble s = figure.getPreferredSize();
        figure.setBounds(new Point2D.Double(0, y), new Point2D.Double(s.width, s.height));
        list.add(figure);
        y += s.height;
      }
    }
    if (list.size() == 0) {
      throw new IOException("No text found");
    }
    return list;
  }

  @Override
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return flavor.equals(DataFlavor.stringFlavor);
  }

  @Override
  public void read(Transferable t, Drawing drawing, boolean replace)
      throws UnsupportedFlavorException, IOException {
    String text = (String) t.getTransferData(DataFlavor.stringFlavor);
    List<Figure> list = new ArrayList<>();
    if (isMultiline) {
      TextHolderFigure figure = (TextHolderFigure) prototype.clone();
      figure.setText(text);
      Dimension2DDouble s = figure.getPreferredSize();
      figure.willChange();
      figure.setBounds(new Point2D.Double(0, 0), new Point2D.Double(s.width, s.height));
      figure.changed();
      list.add(figure);
    } else {
      double y = 0;
      for (String line : text.split("\n")) {
        TextHolderFigure figure = (TextHolderFigure) prototype.clone();
        figure.setText(line);
        Dimension2DDouble s = figure.getPreferredSize();
        y += s.height;
        figure.willChange();
        figure.setBounds(new Point2D.Double(0, 0 + y), new Point2D.Double(s.width, s.height + y));
        figure.changed();
        list.add(figure);
      }
    }
    if (replace) {
      drawing.removeAllChildren();
    }
    drawing.addAll(list);
  }
}
