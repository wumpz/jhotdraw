/*
 * @(#)SVGZInputFormat.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg.io;

import java.io.*;
import java.util.zip.GZIPInputStream;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jhotdraw.draw.*;

/** SVGZInputFormat supports reading of uncompressed and compressed SVG images. */
public class SVGZInputFormat extends SVGInputFormat {

  public SVGZInputFormat() {}

  @Override
  public javax.swing.filechooser.FileFilter getFileFilter() {
    return new FileNameExtensionFilter(
        "Scalable Vector Graphics (SVG, SVGZ)", new String[] {"svg", "svgz"});
  }

  @Override
  public void read(InputStream in, Drawing drawing, boolean replace) throws IOException {
    BufferedInputStream bin = (in instanceof BufferedInputStream)
        ? (BufferedInputStream) in
        : new BufferedInputStream(in);
    bin.mark(2);
    int magic = (bin.read() & 0xff) | ((bin.read() & 0xff) << 8);
    bin.reset();
    if (magic == GZIPInputStream.GZIP_MAGIC) {
      super.read(new GZIPInputStream(bin), drawing, replace);
    } else {
      super.read(bin, drawing, replace);
    }
  }
}
