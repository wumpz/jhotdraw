/*
 * @(#)InputStreamTransferable.java  1.0  December 31, 2006
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

import java.awt.datatransfer.*;
import java.io.*;

/**
 * A Transferable with an InputStream as its transfer class.
 *
 * @author Werner Randelshofer
 * @version 1.0 December 31, 2006 Created.
 */
public class InputStreamTransferable implements Transferable {
    private DataFlavor[] flavors;
    private byte[] data;
    
    /** Creates a new instance. */
    public InputStreamTransferable(DataFlavor flavor, byte[] data) {
        this(new DataFlavor[] { flavor }, data);
    }
    public InputStreamTransferable(DataFlavor[] flavors, byte[] data) {
        this.flavors = flavors;
        this.data = data;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return flavors.clone();
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (DataFlavor f : flavors) {
            if (f.equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (! isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }
        return new ByteArrayInputStream(data);
    }
}
