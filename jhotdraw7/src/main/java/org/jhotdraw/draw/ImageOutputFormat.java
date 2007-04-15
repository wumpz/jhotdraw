/*
 * @(#)ImageOutputFormat.java  1.0  January 2, 2007
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
 * An output format for exporting drawings using one of the image formats
 * supported by javax.imageio.
 *
 * @author Werner Randelshofer
 * @version 1.0 January 2, 2007 Created.
 */
public class ImageOutputFormat implements OutputFormat {
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
    public ImageOutputFormat() {
        this("PNG", "Portable Network Graphics (PNG)", "png", BufferedImage.TYPE_INT_ARGB);
    }
    
    /** Creates a new image output format for the specified image format.
     *
     * @param formatName The format name for the javax.imageio.ImageIO object.
     * @param description The format description to be used for the file filter.
     * @param fileExtension The file extension to be used for file filter.
     * @param bufferedImageType The BufferedImage type used to produce the image.
     *          The value of this parameter must match with the format name.
     */
    public ImageOutputFormat(String formatName, String description, String fileExtension,
            int bufferedImageType) {
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
    
    public JComponent getOutputFormatAccessory() {
        return null;
    }
    
    /**
     * Writes the drawing to the specified file.
     * This method ensures that all figures of the drawing are visible on
     * the image.
     */
    public void write(File file, Drawing drawing) throws IOException {
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            write(out, drawing);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    /**
     * Writes the drawing to the specified output stream.
     * This method ensures that all figures of the drawing are visible on
     * the image.
     */
    public void write(OutputStream out, Drawing drawing) throws IOException {
        write(out, drawing.getFigures(), null, null);
    }
    /**
     * Writes the drawing to the specified output stream.
     * This method applies the specified transform to the drawing, and draws
     * it on an image of the specified size.
     */
    public void write(OutputStream out, Drawing drawing,
            AffineTransform drawingTransform, Dimension imageSize) throws IOException {
        write(out, drawing.getFigures(), drawingTransform, imageSize);
    }
    
    /**
     * Writes the drawing to the specified output stream.
     * This method ensures that all figures of the drawing are visible on
     * the image.
     */
    public Transferable createTransferable(java.util.List<Figure> figures, double scaleFactor) throws IOException {
        return new ImageTransferable(toImage(figures, scaleFactor));
    }
    
    /**
     * Writes the figures to the specified output stream.
     * This method ensures that all figures of the drawing are visible on
     * the image.
     */
    public void write(OutputStream out, java.util.List<Figure> figures) throws IOException {
        write(out, figures, null, null);
    }
    /**
     * Writes the figures to the specified output stream.
     * This method applies the specified transform to the drawing, and draws
     * it on an image of the specified size.
     */
    public void write(OutputStream out, java.util.List<Figure> figures,
            AffineTransform drawingTransform, Dimension imageSize) throws IOException {
        BufferedImage img;
        if (drawingTransform == null || imageSize == null) {
            img = toImage(figures, 1d);
        } else {
            img = toImage(figures, drawingTransform, imageSize);
        }
        ImageIO.write(img, formatName, out);
        img.flush();
    }
    
    /**
     * Creates a BufferedImage from the specified list of figures.
     * <p>
     * The images are drawn using the specified scale factor. If some figures
     * have a drawing area located at negative coordinates, then the drawing
     * coordinates are translated, so that all figures are visible on the
     * image.
     *
     * @param figures The list of figures.
     * @param scaleFactor The scale factor used when drawing the figures.
     */
    public BufferedImage toImage(java.util.List<Figure> figures,
            double scaleFactor) {
        
        // Determine the draw bounds of the figures
        Rectangle2D.Double drawBounds = null;
        for (Figure f : figures) {
            if (drawBounds == null) {
                drawBounds = f.getDrawingArea();
            } else {
                drawBounds.add(f.getDrawingArea());
            }
        }
        
        AffineTransform transform = new AffineTransform();
        if (drawBounds.x < 0) {
            transform.translate(-drawBounds.x * scaleFactor, 0);
        }
        if (drawBounds.y < 0) {
            transform.translate(0, -drawBounds.y * scaleFactor);
        }
        transform.scale(scaleFactor, scaleFactor);
        
        
        return toImage(figures, transform,
                new Dimension(
                (int) ((Math.max(0, drawBounds.x)+drawBounds.width) * scaleFactor),
                (int) ((Math.max(0, drawBounds.y)+drawBounds.height) * scaleFactor)
                )
                );
    }
    
    /**
     * Creates a BufferedImage from the specified list of figures.
     *
     * @param figures The list of figures.
     * @param transform The AffineTransform to be used when drawing
     * the figures.
     * @param imageSize The width and height of the image.
     */
    public BufferedImage toImage(
            java.util.List<Figure> figures,
            AffineTransform transform,
            Dimension imageSize) {
        
        // Create the buffered image and clear
        BufferedImage buf = new BufferedImage(
                imageSize.width, imageSize.height,
                BufferedImage.TYPE_INT_ARGB
                );
        Graphics2D g = buf.createGraphics();
        
        // Clear the buffered image with transparent white
        Composite savedComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        g.setColor(new Color(0x00ffffff,true));
        g.fillRect(0,0,buf.getWidth(),buf.getHeight());
        g.setComposite(savedComposite);
        
        // Draw the figures onto the buffered image
        setRenderingHints(g);
        g.transform(transform);
        for (Figure f : figures) {
            f.draw(g);
        }
        g.dispose();
        
        // Convert the image, if it does not have the specified image type
        if (imageType != BufferedImage.TYPE_INT_ARGB) {
            BufferedImage buf2 = new BufferedImage(
                    buf.getWidth(), buf.getHeight(),
                    imageType
                    );
            g = buf2.createGraphics();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
            g.drawImage(buf, 0, 0, null);
            g.dispose();
            buf.flush();
            buf = buf2;
        }
        
        return buf;
    }
    
    protected void setRenderingHints(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_NORMALIZE);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}
