/*
 * @(#)SVGZInputFormat.java  1.0  February 7, 2007
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.samples.svg.io;

import java.io.*;
import java.util.zip.GZIPInputStream;
import javax.swing.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.io.*;

/**
 * SVGZInputFormat supports reading of uncompressed and compressed SVG images.
 *
 * @author Werner Randelshofer
 * @version 1.0 February 7, 2007 Created.
 */
public class SVGZInputFormat extends SVGInputFormat {
    
    /** Creates a new instance. */
    public SVGZInputFormat() {
    }
    
    public javax.swing.filechooser.FileFilter getFileFilter() {
        return new ExtensionFileFilter("Scalable Vector Graphics (SVG, SVGZ)", new String[] {"svg", "svgz"});
    }
    
    @Override public void read(InputStream in, Drawing drawing) throws IOException {
        BufferedInputStream bin = (in instanceof BufferedInputStream) ? (BufferedInputStream) in : new BufferedInputStream(in);
        bin.mark(2);
        int magic = (bin.read() & 0xff) | ((bin.read() & 0xff) << 8);
        bin.reset();
        
        if (magic == GZIPInputStream.GZIP_MAGIC) {
            super.read(new GZIPInputStream(bin), drawing);
        } else {
            super.read(bin, drawing);
        }
        
    }
}
