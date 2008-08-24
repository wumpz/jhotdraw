/*
 * @(#)ImageHolderFigure.java  2.0  2008-05-24
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

import java.awt.image.*;
import java.io.*;

/**
 * The interface of a figure that has some editable image contents.
 *
 * @author Werner Randelshofer
 * @version 2.0 2008-05-24 Added setImage and getImageData methods. 
 * <br>1.0 December 14, 2006 Created.
 */
public interface ImageHolderFigure extends Figure {
    /**
     * Loads an image from a File.
     * By convention this method is never invoked on the AWT Event Dispatcher 
     * Thread.
     */
    public void loadImage(File f) throws IOException;
    /**
     * Loads an image from an Input Stream.
     * By convention this method is never invoked on the AWT Event Dispatcher 
     * Thread.
     */
    public void loadImage(InputStream in) throws IOException;
    /**
     * Gets the buffered image from the figure.
     */
    public BufferedImage getBufferedImage();
    /**
     * Sets the buffered image for the figure.
     */
    public void setBufferedImage(BufferedImage image);
    
    /**
     * Sets the image.
     *
     * @param imageData The image data. If this is null, a buffered image must
     * be provided.
     * @param bufferedImage An image constructed from the imageData. If this
     * is null, imageData must be provided.
     */
    public void setImage(byte[] imageData, BufferedImage bufferedImage) throws IOException;
    /**
     * Gets the image data.
     *
     * @return imageData The image data, or null, if the ImageHolderFigure does
     * not have an image.
     */
    public byte[] getImageData();

}
