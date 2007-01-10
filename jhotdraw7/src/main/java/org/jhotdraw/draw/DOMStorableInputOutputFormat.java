/*
 * @(#)DOMStorableOutputFormat.java  1.0  December 26, 2006
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

package org.jhotdraw.draw;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.*;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import org.jhotdraw.gui.datatransfer.InputStreamTransferable;
import org.jhotdraw.io.*;
import org.jhotdraw.xml.*;
/**
 * An OutputFormat that can write Drawings with DOMStorable Figure's.
 *
 * @author Werner Randelshofer
 * @version 1.0 December 26, 2006 Created.
 */
public class DOMStorableInputOutputFormat implements OutputFormat, InputFormat {
    private DOMFactory factory;
    
    /**
     * Format description used for the file filter.
     */
    private String description;
    /**
     * File name extension used for the file filter.
     */
    private String fileExtension;
    /**
     * Image IO image format name.
     */
    private String formatName;
    /**
     * The mime type is used for clipboard access.
     */
    private String mimeType;
    
    /**
     * The data flavor constructed from the mime type.
     */
    private DataFlavor dataFlavor;
    
    /** Creates a new instance with format name "Drawing", file extension "xml"
     * and mime type "image/x-jhotdraw".
     */
    public DOMStorableInputOutputFormat(DOMFactory factory) {
        this(factory, "Drawing", "xml", "image/x-jhotdraw");
    }
    
    /** Creates a new instance using the specified parameters.
     *
     * @param factory The factory for creating Figures from XML elements.
     * @param description The format description to be used for the file filter.
     * @param fileExtension The file extension to be used for file filter.
     * @param mimeType The Mime Type is used for clipboard access.
     */
    public DOMStorableInputOutputFormat(
            DOMFactory factory, 
            String description, String fileExtension, String mimeType) {
        this.factory = factory;
        this.fileExtension = fileExtension;
        this.mimeType = mimeType;
        try {
            this.dataFlavor = new DataFlavor(mimeType);
        } catch (ClassNotFoundException ex) {
            InternalError error = new InternalError("Unable to create data flavor for mime type:"+mimeType);
            error.initCause(ex);
            throw error;
        }
    }
    
    public javax.swing.filechooser.FileFilter getFileFilter() {
        return new ExtensionFileFilter(description, fileExtension);
    }
    
    public JComponent getOutputFormatAccessory() {
        return null;
    }
    
    public JComponent getInputFormatAccessory() {
        return null;
    }

    /**
     * Reads a list of figures into the specified drawing.
     * This method expects that there is a child element named "figures"
     * in the element that represents the drawing.
     */
    protected void read(URL url, InputStream in, Drawing drawing, LinkedList<Figure> figures) throws IOException {
        NanoXMLDOMInput domi = new NanoXMLDOMInput(factory, in);
        domi.openElement(factory.getName(drawing));
        domi.openElement("figures",0);
        figures.clear();
        for (int i=0, n = domi.getElementCount(); i < n; i++) {
            Figure f = (Figure) domi.readObject();
            figures.add(f);
        }
        domi.closeElement();
        domi.closeElement();
        drawing.basicAddAll(drawing.getFigureCount(), figures);
    }
    
    public String getFileExtension() {
        return fileExtension;
    }
    
    public void write(File file, Drawing drawing) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        try {
            write(out, drawing);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    public void write(OutputStream out, Drawing drawing) throws IOException {
        NanoXMLDOMOutput domo = new NanoXMLDOMOutput(factory);
        domo.openElement(factory.getName(drawing));
        drawing.write(domo);
        domo.closeElement();
        domo.save(out);
    }
    
    
    public void read(File file, Drawing drawing) throws IOException {
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            read(in, drawing);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
    
    public void read(InputStream in, Drawing drawing) throws IOException {
        NanoXMLDOMInput domi = new NanoXMLDOMInput(factory, in);
        domi.openElement(factory.getName(drawing));
        drawing.read(domi);
        domi.closeElement();
    }
    
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(dataFlavor);
    }
    
    public List<Figure> readFigures(Transferable t) throws UnsupportedFlavorException, IOException {
        LinkedList<Figure> figures = new LinkedList<Figure>();
        InputStream in = (InputStream) t.getTransferData(new DataFlavor(mimeType,description));
        NanoXMLDOMInput domi = new NanoXMLDOMInput(factory, in);
        domi.openElement("Drawing-Clip");
        for (int i=0, n = domi.getElementCount(); i < n; i++) {
            Figure f = (Figure) domi.readObject(i);
            figures.add(f);
        }
        domi.closeElement();
        return figures;
    }
    public Transferable createTransferable(List<Figure> figures, double scaleFactor) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        NanoXMLDOMOutput domo = new NanoXMLDOMOutput(factory);
        domo.openElement("Drawing-Clip");
        for (Figure f : figures) {
            domo.writeObject(f);
        }
        domo.closeElement();
        domo.save(buf);
        return new InputStreamTransferable(new DataFlavor(mimeType, description), buf.toByteArray());
    }    
}
