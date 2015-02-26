/*
 * @(#)AbstractTransferable.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.gui.datatransfer;

import java.awt.datatransfer.*;

/**
 * Base class for transferable objects.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractTransferable implements Transferable {
    private DataFlavor[] flavors;
    
    /** Creates a new instance. */
    public AbstractTransferable(DataFlavor flavor) {
        this.flavors = new DataFlavor[] {flavor};
    }
    /** Creates a new instance. */
    public AbstractTransferable(DataFlavor[] flavors) {
        this.flavors = flavors.clone();
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors.clone();
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (DataFlavor f : flavors) {
            if (f.equals(flavor)) {
                return true;
            }
        }
        return false;
    }
}
