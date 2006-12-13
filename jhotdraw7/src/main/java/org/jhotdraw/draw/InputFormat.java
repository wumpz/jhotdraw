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

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import org.jhotdraw.xml.JavaxDOMInput;
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
     * Returns the MIME type string that can be used to identify clipboard
     * data transferables in this format.
     */
    public String getMimeType();
    /**
     * Return a FileFilter that can be used to identify files which can be restored
     * with this Storage Format. Typically, each input format has its own recognizable file
     * extension.
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
     * Reads figures from an input streams and adds them to a drawing.
     *
     * @param url The url from which the input stream was created. 
     * And which can be used to create more URL's, to read data associated 
     * with the input stream. The url can be null, e.g. if
     * the input stream was created from the clipboard.
     * @param in The input stream.
     * @param drawing The drawing to which the figures are added.
     * @param figures The Figure's that were added to the drawing are
     * added to this collection as well.
     */
    public void read(URL url, InputStream in, Drawing drawing, LinkedList<Figure> figures) throws IOException;
}