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
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.xml.DOMFactory;
import org.jhotdraw.xml.JavaxDOMInput;

/** An OutputFormat that can write Drawings with DOMStorable Figure's. */
public class DOMStorableInputFormat implements InputFormat {

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
  public DOMStorableInputFormat(DOMFactory factory) {
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
  public DOMStorableInputFormat(
      DOMFactory factory, String description, String fileExtension, String mimeType) {
    this.factory = factory;
    this.description = description;
    this.fileExtension = fileExtension;
    this.mimeType = mimeType;
    try {
      this.dataFlavor = new DataFlavor(mimeType);
    } catch (ClassNotFoundException ex) {
      throw new InternalError("Unable to create data flavor for mime type:" + mimeType, ex);
    }
  }

  @Override
  public javax.swing.filechooser.FileFilter getFileFilter() {
    return new FileNameExtensionFilter(description, fileExtension);
  }

  @Override
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return flavor.equals(dataFlavor);
  }

  @Override
  public void read(InputStream in, Drawing drawing, boolean replace) throws IOException {
    JavaxDOMInput domi = new JavaxDOMInput(factory, in);
    domi.openElement(factory.getName(drawing));
    domi.openElement("figures");
    if (replace) {
      drawing.removeAllChildren();
    }
    for (int i = 0; i < domi.getElementCount(); i++) {
      drawing.add((Figure) domi.readObject(i));
    }
    domi.closeElement();
    domi.closeElement();
  }

  @Override
  public void read(Transferable t, Drawing drawing, boolean replace)
      throws UnsupportedFlavorException, IOException {
    List<Figure> figures = new ArrayList<>();
    InputStream in = (InputStream) t.getTransferData(new DataFlavor(mimeType, description));
    JavaxDOMInput domi = new JavaxDOMInput(factory, in);
    domi.openElement("Drawing-Clip");
    for (int i = 0, n = domi.getElementCount(); i < n; i++) {
      figures.add((Figure) domi.readObject(i));
    }
    domi.closeElement();
    if (replace) {
      drawing.removeAllChildren();
    }
    drawing.addAll(figures);
  }
}
