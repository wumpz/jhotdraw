/*
 * @(#)DOMStorableOutputFormat.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.io;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.*;
import java.net.URI;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jhotdraw.datatransfer.InputStreamTransferable;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.io.OutputFormat;
import org.jhotdraw.xml.*;

/** An OutputFormat that can write Drawings with DOMStorable Figure's. */
public class DOMStorableOutputFormat implements OutputFormat {

  private DOMFactory factory;
  /** Format description used for the file filter. */
  private String description;
  /** File name extension used for the file filter. */
  private String fileExtension;
  /** Image IO image format name. */
  private String formatName;
  /** The mime type is used for clipboard access. */
  private String mimeType;
  /** The data flavor constructed from the mime type. */
  private DataFlavor dataFlavor;

  /**
   * Creates a new instance with format name "Drawing", file extension "xml" and mime type
   * "image/x-jhotdraw".
   */
  public DOMStorableOutputFormat(DOMFactory factory) {
    this(factory, "Drawing", "xml", "image/x-jhotdraw");
  }

  /**
   * Creates a new instance using the specified parameters.
   *
   * @param factory The factory for creating Figures from XML elements.
   * @param description The format description to be used for the file filter.
   * @param fileExtension The file extension to be used for file filter.
   * @param mimeType The Mime Type is used for clipboard access.
   */
  public DOMStorableOutputFormat(
      DOMFactory factory, String description, String fileExtension, String mimeType) {
    this.factory = factory;
    this.description = description;
    this.fileExtension = fileExtension;
    this.mimeType = mimeType;
    try {
      this.dataFlavor = new DataFlavor(mimeType);
    } catch (ClassNotFoundException ex) {
      InternalError error =
          new InternalError("Unable to create data flavor for mime type:" + mimeType);
      error.initCause(ex);
      throw error;
    }
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

  public void write(File file, Drawing drawing) throws IOException {
    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
      write(out, drawing);
    }
  }

  @Override
  public void write(OutputStream out, Drawing drawing) throws IOException {
    JavaxDOMOutput domo = new JavaxDOMOutput(factory);
    domo.openElement(factory.getName(drawing));
    //    drawing.write(domo);
    domo.openElement("figures");
    for (Figure f : drawing.getChildren()) {
      domo.writeObject(f);
    }
    domo.closeElement();
    domo.closeElement();
    domo.save(out);
  }

  @Override
  public Transferable createTransferable(Drawing drawing, List<Figure> figures, double scaleFactor)
      throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    JavaxDOMOutput domo = new JavaxDOMOutput(factory);
    domo.openElement("Drawing-Clip");
    for (Figure f : figures) {
      domo.writeObject(f);
    }
    domo.closeElement();
    domo.save(buf);
    return new InputStreamTransferable(new DataFlavor(mimeType, description), buf.toByteArray());
  }
}
