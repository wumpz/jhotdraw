/*
 * @(#)SerializationInputOutputFormat.java
 *
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.io;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jhotdraw.datatransfer.AbstractTransferable;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.io.OutputFormat;

/**
 * {@code SerializationInputOutputFormat} uses Java Serialization for reading and and writing {@code
 * Drawing} objects.
 */
public class SerializationOutputFormat implements OutputFormat {

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

  private Drawing prototype;

  /**
   * Creates a new instance with format name "Drawing", file extension "xml" and mime type
   * "image/x-jhotdraw".
   */
  public SerializationOutputFormat() {
    this("Drawing", "ser", new DefaultDrawing());
  }

  /** Creates a new instance using the specified parameters. */
  public SerializationOutputFormat(String description, String fileExtension, Drawing prototype) {
    this.description = description;
    this.fileExtension = fileExtension;
    this.mimeType = DataFlavor.javaSerializedObjectMimeType;
    this.prototype = prototype;
    this.dataFlavor = new DataFlavor(prototype.getClass(), description);
  }

  @Override
  public FileFilter getFileFilter() {
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
    ObjectOutputStream oout = new ObjectOutputStream(out);
    oout.writeObject(drawing);
    oout.flush();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Transferable createTransferable(Drawing drawing, List<Figure> figures, double scaleFactor)
      throws IOException {
    final Drawing d = (Drawing) prototype.clone();
    HashMap<Figure, Figure> originalToDuplicateMap = new HashMap<>(figures.size());
    final ArrayList<Figure> duplicates = new ArrayList<>(figures.size());
    for (Figure f : figures) {
      Figure df = f.clone();
      d.add(df);
      duplicates.add(df);
      originalToDuplicateMap.put(f, df);
    }
    for (Figure f : duplicates) {
      f.remap(originalToDuplicateMap, true);
    }
    return new AbstractTransferable(dataFlavor) {
      @Override
      public Object getTransferData(DataFlavor flavor)
          throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
          return d;
        } else {
          throw new UnsupportedFlavorException(flavor);
        }
      }
    };
  }
}
