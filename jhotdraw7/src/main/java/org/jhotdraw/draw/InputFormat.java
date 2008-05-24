/*
 * @(#)InputFormat.java  3.0  2008-05-24
 *
 * Copyright (c) 1996-2008 by the original authors of JHotDraw
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
 * @version 3.0 2008-05-24 Added parameter isReplaceDrawing. 
 * <br>2.0 2007-12-07 Method readFigures(Transferable) replaced by
 * read(Transferable, Drawing). 
 * <br>1.0 December 12, 2006 Created.
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
     * Reads figures from a file and replaces the children of the drawing
     * with them.
     * <p>
     * This is a convenience method for calling read(File,Drawing,true).
     *
     * @param file The file.
     * @param drawing The drawing.
     */
    public void read(File file, Drawing drawing) throws IOException;
    
    /**
     * Reads figures from a file and adds them to the specified drawing.
     *
     * @param file The file.
     * @param drawing The drawing.
     * @param replace Set this to true, if the contents of the file replaces the
     * contents of the drawing (for example, when loading a drawing from a file).
     * Set this to false, to add the contents of the file to the drawing (for
     * example, when the file has been dropped into the drawing view).
     */
    public void read(File file, Drawing drawing, boolean replace) throws IOException;

    /**
     * Reads figures from a file and adds them to the specified drawing.
     *
     * @param in The input stream.
     * @param drawing The drawing.
     * @param replace Set this to true, if the contents of the stream replaces the
     * contents of the drawing (for example, when loading a drawing from a stream).
     * Set this to false, to add the contents of the file to the drawing (for
     * example, when the stream has been dropped into the drawing view).
     */
    public void read(InputStream in, Drawing drawing, boolean replace) throws IOException;

    /**
     * Returns true, if this InputFormat can readFigures TransferData using the 
     * specified DataFlavor.
     * 
     * @param flavor A DataFlavor.
     */
    public boolean isDataFlavorSupported(DataFlavor flavor);
    
    /**
     * Reads figures from the specified Transferable and adds them to the
     * specified drawing.
     * 
     * @param t The Transferable. 
     * @param drawing The drawing.
     * @param replace Set this to true, if the contents of the transferable
     * replaces the contents of the drawing (for example, when loading a drawing
     * from a transferable). Set this to false, to add the contents of the 
     * transferable to the drawing (for example, when the transferable has been
     * dropped or pasted into the drawing view).
     */
    public void read(Transferable t, Drawing drawing, boolean replace) throws UnsupportedFlavorException, IOException;
}