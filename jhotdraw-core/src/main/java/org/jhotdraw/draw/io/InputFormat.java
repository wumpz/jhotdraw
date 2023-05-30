/*
 * @(#)InputFormat.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.io;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.swing.filechooser.FileFilter;
import org.jhotdraw.draw.Drawing;

/**
 * An <em>input format</em> implements a strategy for reading a {@link Drawing} using a specific
 * format from either an {@code InputStream}, an {@code URI} or a {@code Transferable}.
 *
 * <p>Typically the format can be recognized by a Mime type or by a file extension. To identify the
 * format used by a file, an appropriate {@code FileFilter} for a javax.swing.JFileChooser component
 * can be requested from {@code InputFormat}.
 *
 * <p>This interface intentionally contains many identical operations like OutputFormat to make it
 * easy, to write classes that implement both interfaces.
 *
 * <p><hr> <b>Design Patterns</b>
 *
 * <p><em>Strategy</em><br>
 * {@code InputFormat} encapsulates a strategy for reading drawings from input streams.<br>
 * Strategy: {@link InputFormat}; Context: {@link Drawing}. <hr>
 */
public interface InputFormat {

  /**
   * Return a FileFilter that can be used to identify files which can be read with this input
   * format. Typically, each input format has its own recognizable file naming convention.
   *
   * @return FileFilter to be used with a javax.swing.JFileChooser
   */
  public FileFilter getFileFilter();

  /**
   * Reads figures from an URI and replaces the children of the drawing with them.
   *
   * <p>This is a convenience method for calling read(URI,Drawing,true).
   *
   * @param uri The URI.
   * @param drawing The drawing.
   */
  public default void read(URI uri, Drawing drawing) throws IOException {
    read(uri, drawing, true);
  }

  /**
   * Reads figures from an URI and adds them to the specified drawing.
   *
   * @param uri The URI.
   * @param drawing The drawing.
   * @param replace Set this to true, if the contents of the file replaces the contents of the
   *     drawing (for example, when loading a drawing from a file). Set this to false, to add the
   *     contents of the file to the drawing (for example, when the file has been dropped into the
   *     drawing view).
   */
  public default void read(URI uri, Drawing drawing, boolean replace) throws IOException {
    read(uri.toURL().openStream(), drawing, replace);
  }

  public default void read(File file, Drawing drawing) throws IOException {
    read(file, drawing, true);
  }

  public default void read(File file, Drawing drawing, boolean replace) throws IOException {
    try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
      read(in, drawing, replace);
    }
  }

  /**
   * Reads figures from an InputStream and adds them to the specified drawing.
   *
   * @param in The input stream.
   * @param drawing The drawing.
   * @param replace Set this to true, if the contents of the stream replaces the contents of the
   *     drawing (for example, when loading a drawing from a stream). Set this to false, to add the
   *     contents of the file to the drawing (for example, when the stream has been dropped into the
   *     drawing view).
   */
  public void read(InputStream in, Drawing drawing, boolean replace) throws IOException;

  /**
   * Returns true, if this InputFormat can readFigures TransferData using the specified DataFlavor.
   *
   * @param flavor A DataFlavor.
   */
  public boolean isDataFlavorSupported(DataFlavor flavor);

  /**
   * Reads figures from the specified Transferable and adds them to the specified drawing.
   *
   * @param t The Transferable.
   * @param drawing The drawing.
   * @param replace Set this to true, if the contents of the transferable replaces the contents of
   *     the drawing (for example, when loading a drawing from a transferable). Set this to false,
   *     to add the contents of the transferable to the drawing (for example, when the transferable
   *     has been dropped or pasted into the drawing view).
   */
  public void read(Transferable t, Drawing drawing, boolean replace)
      throws UnsupportedFlavorException, IOException;
}
