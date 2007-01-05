/*
 * @(#)InputFormat.java  1.0  December 12, 2006
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.draw;

import java.awt.datatransfer.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * Interface to define an input format for a Drawing. An InputFormat is a 
 * strategy that knows how to restore a Drawing according to a specific encoding.
 * Typically it can be recognized by a Mime type or by a file extension. 
 * To identify a valid file format for a Drawing an appropriate FileFilter for a
 * javax.swing.JFileChooser component can be requested.
 * <p>
 * This interface intentionally contains many identical operations like
 * OutputFormat to make it easy, to write classes that implement both interfaces.
 *
 * @author Werner Randelshofer
 * @version 1.0 December 12, 2006 Created.
 */
public interface InputFormat {
    /**
     * Return a FileFilter that can be used to identify files which can be restored
     * with this Storage Format. Typically, each input format has its own 
     * recognizable file naming convention.
     *
     * @return FileFilter to be used with a javax.swing.JFileChooser
     */
    public javax.swing.filechooser.FileFilter getFileFilter();
    
    /**
     * Return a JFileChooser accessory that can be used to customize the input
     * format.
     *
     * @return A JFileChooser accessory to be used with a javax.swing.JFileChooser
     * Returns null, if no accessory is provided for this format.
     */
    public JComponent getInputFormatAccessory();
    
    /**
     * Reads figures from a file and adds them to the specified drawing.
     *
     * @param file The file.
     * @param drawing The drawing.
     */
    public void read(File file, Drawing drawing) throws IOException;

    /**
     * Reads figures from a file and adds them to the specified drawing.
     *
     * @param in The input stream.
     * @param drawing The drawing.
     */
    public void read(InputStream in, Drawing drawing) throws IOException;

    /**
     * Returns true, if this InputFormat can read TransferData using the 
     * specified DataFlavor.
     *
     * @param flavor A DataFlavor. 
     */
    public boolean isDataFlavorSupported(DataFlavor flavor);
    
    /**
     * Reads figures from the specified Transferable.
     *
     * @param t The Transferable.
     * @return The figures that were read from the input stream.
     */
    public List<Figure> readFigures(Transferable t) throws UnsupportedFlavorException, IOException;
}