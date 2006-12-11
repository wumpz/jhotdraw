/*
 * @(#)StorageFormat.java  0.1  November 25, 2006
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

import java.util.*;
import java.io.*;
import javax.swing.filechooser.FileFilter;
/**
 * A StorageFormat is a strategy that knows how to read/write the
 * <code>Figure</code>'s of a <code>Drawing</code> from/into a stream.
 *
 * @author Werner Randelshofer
 * @version 0.1 2006-11-25 Created (Experimental).
 */
public interface StorageFormat {
    /**
     * Returns the MIME type string that can be used to identify clipboard
     * data transferables in this format.
     */
    public String getMimeType();
    /**
     * Return a FileFilter that can be used to identify files which can be stored and restored
     * with this Storage Format. Typically, each storage format has its own recognizable file
     * extension.
     *
     * @return FileFilter to be used with a javax.swing.JFileChooser
     */
    public FileFilter getFileFilter();
    
    /**
     * Every format has to identify itself as able to read and/or write from
     * the format it represents. If the storage format can write to the
     * format, it should return true in this method.
     * @return boolean <code>true</code> if the format can write
     */
    public boolean isWriteFormat();
    
    /**
     * Every format has to identify itself as able to read and/or write from
     * the format it represents. If the storage format can write to the
     * format, it should return true in this method.
     * @return boolean <code>true</code> if the format can read
     */
    public boolean isReadFormat();
    /**
     * Writes figure of a drawing into an output stream.
     *
     * @param out The output stream.
     * @param drawing The drawing that contains the Figure's.
     * @return figures The Figure's to be written.
     */
    public void write(OutputStream out, Drawing drawing, Collection<Figure> figures) throws IOException;
    
    /**
     * Reads figures from an input streams and adds them to a drawing.
     *
     * @param in The input stream.
     * @param drawing The drawing to which the figures are added.
     * @param figures The Figure's that were added to the drawing are
     * added to this collection as well.
     */
    public void read(InputStream in, Drawing drawing, LinkedList<Figure> figures) throws IOException;
}
