/*
 * @(#)PlainTextTransferable.java  1.0  22. August 2007
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
import java.io.*;
/**
 * PlainTextTransferable.
 * <p>
 * Note: This transferable should (almost) always be used in conjunction with
 * PlainTextTransferable.
 *
 * @author Werner Randelshofer
 * @version 1.0 22. August 2007 Created.
 */
public class PlainTextTransferable extends AbstractTransferable {
    private String plainText;
    
    public PlainTextTransferable(String plainText) {
        this(getDefaultFlavors(), plainText);
    }
    public PlainTextTransferable(DataFlavor flavor, String plainText) {
        this(new DataFlavor[] { flavor }, plainText);
    }
    public PlainTextTransferable(DataFlavor[] flavors, String plainText) {
        super(flavors);
        this.plainText = plainText;
    }
    
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (! isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }
        plainText = (plainText == null) ? "" : plainText;
        if (String.class.equals(flavor.getRepresentationClass())) {
            return plainText;
        } else if (Reader.class.equals(flavor.getRepresentationClass())) {
            return new StringReader(plainText);
        } else if (InputStream.class.equals(flavor.getRepresentationClass())) {
            return new StringBufferInputStream(plainText);
        } // fall through to unsupported
        
	throw new UnsupportedFlavorException(flavor);
    }
    
    protected static DataFlavor[] getDefaultFlavors() {
        try {
            return new DataFlavor[] {
                new DataFlavor("text/plain;class=java.lang.String"),
                new DataFlavor("text/plain;class=java.io.Reader"),
                new DataFlavor("text/plain;charset=unicode;class=java.io.InputStream")
            };
        } catch (ClassNotFoundException cle) {
            InternalError ie = new InternalError(
                    "error initializing PlainTextTransferable");
            ie.initCause(cle);
            throw ie;
        }
    }
}
