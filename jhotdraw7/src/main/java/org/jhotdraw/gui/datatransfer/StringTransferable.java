/*
 * @(#)StringTransferable.java  1.0  22. August 2007
 *
 * Copyright (c) 2007 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.gui.datatransfer;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
/**
 * StringTransferable.
 * <p>
 * Note: This transferable should always be used in conjunction with 
 * PlainTextTransferable.
 *
 * @author Werner Randelshofer
 * @version 1.0 22. August 2007 Created.
 */
public class StringTransferable extends AbstractTransferable {
    private String string;
    
    public StringTransferable(String string) {
        this(getDefaultFlavors(), string);
    }
    public StringTransferable(DataFlavor flavor, String string) {
        this(new DataFlavor[] { flavor }, string);
    }
    public StringTransferable(DataFlavor[] flavors, String string) {
        super(flavors);
        this.string = string;
    }
    
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (! isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }
        return string;
    }
    
    protected static DataFlavor[] getDefaultFlavors() {
        try {
            return new DataFlavor[] {
                new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+";class=java.lang.String"),
                DataFlavor.stringFlavor
            };
        } catch (ClassNotFoundException cle) {
            InternalError ie = new InternalError(
                    "error initializing StringTransferable");
            ie.initCause(cle);
            throw ie;
        }
    }
}
