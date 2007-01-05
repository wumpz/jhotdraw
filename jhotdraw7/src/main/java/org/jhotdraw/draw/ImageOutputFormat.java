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
    
    public void write(OutputStream out, Drawing drawing) throws IOException {
        write(out, drawing.getFigures());
    }
    
    public Transferable createTransferable(java.util.List<Figure> figures, double scaleFactor) throws IOException {
        return new ImageTransferable(toImage(figures, scaleFactor));
    }
    
    public void write(OutputStream out, java.util.List<Figure> figures) throws IOException {
        BufferedImage img = toImage(figures, 1d);
        ImageIO.write(img, formatName, out);
        img.flush();
    }
    
    public BufferedImage toImage(java.util.List<Figure> figures, double scaleFactor) {
        // Determine the draw bounds of the figures
        Rectangle2D.Double drawBounds = null;
        for (Figure f : figures) {
            if (drawBounds == null) {
                drawBounds = f.getDrawingArea();
            } else {
                drawBounds.add(f.getDrawingArea());
            }
        }
        
        // Create the buffered image and clear it with white if it is opaque
        BufferedImage buf = new BufferedImage(
                (int) (drawBounds.width * scaleFactor), (int) (drawBounds.height * scaleFactor),
                BufferedImage.TYPE_INT_ARGB
                );
        Graphics2D g = buf.createGraphics();
        /*
        Composite savedComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        g.setColor(new Color(0x00ffffff,true));
            g.fillRect(0,0,buf.getWidth(),buf.getHeight());
            g.setComposite(savedComposite);
        *//*
        if (buf.getTransparency() == BufferedImage.OPAQUE) {
            g.setBackground(Color.white);
            g.clearRect(0,0,buf.getWidth(),buf.getHeight());
        }*/
        
        // Draw the figures onto the buffered image
        g.translate(-drawBounds.x * scaleFactor, -drawBounds.y * scaleFactor);
        g.scale(scaleFactor, scaleFactor);
        setRenderingHints(g);
        for (Figure f : figures) {
            f.draw(g);
        }
        g.dispose();
        
        if (imageType != BufferedImage.TYPE_INT_ARGB) {
            BufferedImage buf2 = new BufferedImage(
                    buf.getWidth(), buf.getHeight(),
                    imageType
                    );
            g = buf2.createGraphics();
            if (buf2.getTransparency() == BufferedImage.OPAQUE) {
                g.setBackground(Color.white);
                g.fillRect(0,0,buf2.getWidth(),buf2.getHeight());
            }
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
