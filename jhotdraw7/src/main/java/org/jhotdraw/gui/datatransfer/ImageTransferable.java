/*
 * @(#)ImageTransferable.java  1.0  January 2, 2007
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

package org.jhotdraw.gui.datatransfer;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

/**
 * A Transferable with an Image as its transfer class.
 *
 * @author Werner Randelshofer
 * @version 1.0 January 2, 2007 Created.
 */
public class ImageTransferable implements Transferable {
    private Image image;
    
    /** Creates a new instance. */
    public ImageTransferable(Image image) {
        this.image = image;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DataFlavor.imageFlavor);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (! isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }
        return image;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { DataFlavor.imageFlavor };
    }
    
}
