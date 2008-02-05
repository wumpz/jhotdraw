/*
 * @(#)OutputFormat.java  2.0  2007-12-16
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.draw;

import java.awt.Dimension;
import java.awt.datatransfer.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * Interface to define an output format. An OutputFormat is a strategy that
 * knows how to store a Drawing according to a specific encoding. Typically it
 * can be recognized by a Mime type or by a file extension. To identify a valid
 * file format for a Drawing an appropriate FileFilter for a
 * javax.swing.JFileChooser component can be requested.
 * <p>
 * This interface intentionally contains many identical operations like
 * InputFormat to make it easy, to write classes that implement both interfaces.
 *
 * @author Werner Randelshofer
 * @version 2.0 2007-12-16 Method createTransferable needs Drawing object 
 * parameter in order to support attributes on the Drawing object.
 * <br>1.0 December 12, 2006 Created.
 */
public interface OutputFormat {
    /**
     * Return a FileFilter that can be used to identify files which can be stored 
     * with this output format. Typically, each output format has its own
     * recognizable file extension.
     *
     * @return FileFilter to be used with a javax.swing.JFileChooser
     */
    public javax.swing.filechooser.FileFilter getFileFilter();

    /**
     * Returns the file extension for the output format.
     * The file extension should be appended to a file name when storing a
     * Drawing with the specified file format.
     */
    public String getFileExtension();
    
    /**
     * Return a JFileChooser accessory that can be used to customize the output
     * format.
     *
     * @return A JFileChooser accessory to be used with a javax.swing.JFileChooser
     * Returns null, if no accessory is provided for this format.
     */
    public JComponent getOutputFormatAccessory();
    
    /**
     * Writes a Drawing into a file.
     *
     * @param file The file.
     * @param drawing The drawing.
     */
    public void write(File file, Drawing drawing) throws IOException;

    /**
     * Writes a Drawing into an output stream.
     *
     * @param out The output stream.
     * @param drawing The drawing.
     */
    public void write(OutputStream out, Drawing drawing) throws IOException;
    
    /**
     * Creates a Transferable for the specified list of Figures.
     *
     * @param drawing The drawing.
     * @param figures A list of figures of the drawing.
     * @param scaleFactor The factor to be used, when the Transferable creates
     * an image with a fixed size from the figures.
     * @return The Transferable.
     */
    public Transferable createTransferable(Drawing drawing, List<Figure> figures, double scaleFactor) throws IOException;
}
