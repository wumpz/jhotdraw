/*
 * @(#)ImageHolder.java  1.0  December 14, 2006
 *
 * Copyright (c) 2006 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
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
public interface ImageHolder extends Figure {
    /**
     * Loads an image from a File.
     * By convention this method is never invoked on the AWT Event Dispatcher 
     * Thread.
     */
    public void loadImage(File f) throws IOException;
    /**
     * Gets the buffered image from the figure.
     */
    public BufferedImage getBufferedImage();
}
