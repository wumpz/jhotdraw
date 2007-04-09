/*
 * @(#)SVGZOutputFormat.java  1.0  April 7, 2007
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
import java.util.zip.*;
import javax.swing.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.io.*;

/**
 * SVGZOutputFormat.
 *
 * @author Werner Randelshofer
 * @version 1.0 April 7, 2007 Created.
 */
public class SVGZOutputFormat extends SVGOutputFormat {
    
    /** Creates a new instance. */
    public SVGZOutputFormat() {
    }
    
    public String getFileExtension() {
        return "svgz";
    }
    public javax.swing.filechooser.FileFilter getFileFilter() {
        return new ExtensionFileFilter("Compressed Scalable Vector Graphics (SVGZ)", "svgz");
    }
    
    
    @Override public void write(OutputStream out, Drawing drawing) throws IOException {
        GZIPOutputStream gout = new GZIPOutputStream(out);
        super.write(gout, drawing.getFigures());
        gout.finish();
        
    }
}
