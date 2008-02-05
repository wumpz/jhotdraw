/*
 * @(#)ImageInputFormat.java  1.1  2007-12-16
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
 * @author Werner Randelshor 
 * @version 1.1 2007-12-16 Adapted to changes in InputFormat.
 * <br>1.0 January 3, 2007 Created.
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
        figure.setBounds(
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
        figure.setBounds(
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

    public void read(Transferable t, Drawing drawing) throws UnsupportedFlavorException, IOException {
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
        figure.setBounds(
                new Point2D.Double(0,0), 
                new Point2D.Double(
                figure.getBufferedImage().getWidth(),
                figure.getBufferedImage().getHeight()
                ));
        LinkedList list = new LinkedList<Figure>();
        list.add(figure);
        drawing.addAll(list);
    }
}
