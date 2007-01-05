/*
 * @(#)ImageInputFormat.java  1.0  January 3, 2007
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

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import org.jhotdraw.gui.datatransfer.*;
import org.jhotdraw.io.*;

/**
 * An input format for importing drawings using one of the image formats 
 * supported by javax.imageio.
 * <p>
 * This class uses the prototype design pattern. A ImageHolderFigure figure is used
 * as a prototype for creating a figure that holds the imported image.
 * 
 * @author Werner RandelshoImageHolderFiguren 1.0 January 3, 2007 Created.
 * @see org.jhotdraw.draw.ImageHolderFigure
 */
public class ImageInputFormat implements InputFormat {
    /**
     * The prototype for creating a figure that holds the imported image.
     */
    private ImageHolderFigure prototype;
    
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
     * The image type must match the output format, for example, PNG supports
     * BufferedImage.TYPE_INT_ARGB whereas GIF needs BufferedImage.TYPE_
     */
    private int imageType;
    
    /** Creates a new image output format for Portable Network Graphics PNG. */
    public ImageInputFormat(ImageHolderFigure prototype) {
        this(prototype, "PNG", "Portable Network Graphics (PNG)", "png", BufferedImage.TYPE_INT_ARGB);
    }
    
    /** Creates a new image output format for the specified image format.
     *
     * @param formatName The format name for the javax.imageio.ImageIO object.
     * @param description The format description to be used for the file filter.
     * @param fileExtension The file extension to be used for file filter.
     * @param bufferedImageType The BufferedImage type used to produce the image.
     *          The value of this parameter must match with the format name.
     */
    public ImageInputFormat(ImageHolderFigure prototype, String formatName, String description, String fileExtension,
            int bufferedImageType) {
        this.prototype = prototype;
        this.formatName = formatName;
        this.description = description;
        this.fileExtension = fileExtension;
        this.imageType = bufferedImageType;
    }
    
    public javax.swing.filechooser.FileFilter getFileFilter() {
        return new ExtensionFileFilter(description, fileExtension);
    }
    
    public String getFileExtension() {
        return fileExtension;
    }
    
    public JComponent getInputFormatAccessory() {
        return null;
    }

    public void read(File file, Drawing drawing) throws IOException {
        ImageHolderFigure figure = (ImageHolderFigure) prototype.clone();
        figure.loadImage(file);
        figure.basicSetBounds(
                new Point2D.Double(0,0), 
                new Point2D.Double(
                figure.getBufferedImage().getWidth(),
                figure.getBufferedImage().getHeight()
                ));
        drawing.basicAdd(figure);
    }

    public void read(InputStream in, Drawing drawing) throws IOException {
        drawing.basicAdd(createImageHolder(in));
    }
    
    public ImageHolderFigure createImageHolder(InputStream in) throws IOException {
        ImageHolderFigure figure = (ImageHolderFigure) prototype.clone();
        figure.loadImage(in);
        figure.basicSetBounds(
                new Point2D.Double(0,0), 
                new Point2D.Double(
                figure.getBufferedImage().getWidth(),
                figure.getBufferedImage().getHeight()
                ));
        return figure;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DataFlavor.imageFlavor);
    }

    public java.util.List<Figure> readFigures(Transferable t) throws UnsupportedFlavorException, IOException {
        Image img = (Image) t.getTransferData(DataFlavor.imageFlavor);
        if (! (img instanceof BufferedImage)) {
            MediaTracker tracker = new MediaTracker(new JLabel());
            tracker.addImage(img, 0);
            try {
                tracker.waitForAll();
            } catch (InterruptedException ex) {
                IOException e = new IOException("MediaTracker interrupted");
                e.initCause(ex);
                throw e;
            }
            BufferedImage buf = new BufferedImage(img.getWidth(null), img.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = buf.createGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
            img.flush();
            img = buf;
        }
        ImageHolderFigure figure = (ImageHolderFigure) prototype.clone();
        figure.setBufferedImage((BufferedImage) img);
        figure.basicSetBounds(
                new Point2D.Double(0,0), 
                new Point2D.Double(
                figure.getBufferedImage().getWidth(),
                figure.getBufferedImage().getHeight()
                ));
        LinkedList list = new LinkedList<Figure>();
        list.add(figure);
        return list;
    }
}
