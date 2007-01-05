/*
 * @(#)ImageHolderFigure.java  1.0  December 14, 2006
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

import java.awt.image.*;
import java.io.*;

/**
 * The interface of a figure that has some editable image contents.
 *
 * @author Werner Randelshofer
 * @version 1.0 December 14, 2006 Created.
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
}
