/*
 * @(#)ImageTransferable.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
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
 * @version $Id$
 */
public class ImageTransferable implements Transferable {
    private Image image;
    
    public static final DataFlavor IMAGE_PNG_FLAVOR;
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

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DataFlavor.imageFlavor) ||
                flavor.equals(IMAGE_PNG_FLAVOR);
    }

    @Override
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

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { DataFlavor.imageFlavor, IMAGE_PNG_FLAVOR };
    }
    
}
