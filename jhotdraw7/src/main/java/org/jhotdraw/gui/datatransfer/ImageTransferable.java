/*
 * @(#)ImageTransferable.java  2.0  2008-03-20
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

package org.jhotdraw.gui.datatransfer;

import org.jhotdraw.util.Images;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import javax.imageio.ImageIO;

/**
 * A Transferable with an Image as its transfer class.
 *
 * @author Werner Randelshofer
 * @version 2.0 2008-03-20 Explicitly export images in PNG format, to circumvent 
 * image clipboard issues on Mac OS X 10.5.2.
 * <br>1.0 January 2, 2007 Created.
 */
public class ImageTransferable implements Transferable {
    private Image image;
    
    public final static DataFlavor IMAGE_PNG_FLAVOR;
    static {
        try {
            IMAGE_PNG_FLAVOR = new DataFlavor("image/png");
        } catch (Exception e) {
            InternalError error = new InternalError("Unable to crate image/png data flavor");
            error.initCause(e);
            throw error;
        }
    }
    
    /** Creates a new instance. */
    public ImageTransferable(Image image) {
        this.image = image;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DataFlavor.imageFlavor) ||
                flavor.equals(IMAGE_PNG_FLAVOR);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        /*if (! isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }*/
        if (flavor.equals(DataFlavor.imageFlavor)) {
        return image;
        } else if (flavor.equals(IMAGE_PNG_FLAVOR)) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    ImageIO.write(Images.toBufferedImage(image), "PNG", buf);
                    return new ByteArrayInputStream(buf.toByteArray());

        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { DataFlavor.imageFlavor, IMAGE_PNG_FLAVOR };
    }
    
}
