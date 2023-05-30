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
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Map;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.io.InputFormat;

/**
 * {@code SerializationInputOutputFormat} uses Java Serialization for reading and and writing {@code
 * Drawing} objects.
 */
public class SerializationInputFormat implements InputFormat {

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
  public SerializationInputFormat() {
    this("Drawing", "ser", new DefaultDrawing());
  }

  /** Creates a new instance using the specified parameters. */
  public SerializationInputFormat(String description, String fileExtension, Drawing prototype) {
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

  @SuppressWarnings("unchecked")
  @Override
  public void read(InputStream in, Drawing drawing, boolean replace) throws IOException {
    try {
      ObjectInputStream oin = new ObjectInputStream(in);
      Drawing d = (Drawing) oin.readObject();
      if (replace) {
        for (Map.Entry<AttributeKey<?>, Object> e : d.attr().getAttributes().entrySet()) {
          drawing.attr().set((AttributeKey<Object>) e.getKey(), e.getValue());
        }
      }
      for (Figure f : d.getChildren()) {
        drawing.add(f);
      }
    } catch (ClassNotFoundException ex) {
      IOException ioe = new IOException("Couldn't read drawing.");
      ioe.initCause(ex);
      throw ioe;
    }
  }

  @Override
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return flavor.equals(dataFlavor);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void read(Transferable t, Drawing drawing, boolean replace)
      throws UnsupportedFlavorException, IOException {
    try {
      Drawing d = (Drawing) t.getTransferData(dataFlavor);
      if (replace) {
        for (Map.Entry<AttributeKey<?>, Object> e : d.attr().getAttributes().entrySet()) {
          drawing.attr().set((AttributeKey<Object>) e.getKey(), e.getValue());
        }
      }
      for (Figure f : d.getChildren()) {
        drawing.add(f);
      }
    } catch (Throwable th) {
      th.printStackTrace();
    }
  }
}
