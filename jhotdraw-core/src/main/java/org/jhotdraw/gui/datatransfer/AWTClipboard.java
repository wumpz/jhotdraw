/*
 * @(#)AWTClipboard.java
 * 
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.gui.datatransfer;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

/**
 * {@code AWTClipboard} acts as a proxy to an AWT {@code Clipboard} object.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Proxy</em><br>
 * {@code AWTClipboard} acts as a proxy to an AWT {@code Clipboard} object.<br>
 * Proxy: {@link AWTClipboard}; Target: {@code java.awt.datatransfer.Clipboard}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class AWTClipboard extends AbstractClipboard {
    /** The proxy target. */
    private Clipboard target;

    /**
     * Creates a new proxy for the specified target object.
     *
     * @param target A Clipboard object.
     */
    public AWTClipboard(Clipboard target) {
        this.target = target;
    }

    /** Returns the proxy target. */
    public Clipboard getTarget() {
        return target;
    }

    @Override
    public Transferable getContents(Object requestor) {
        return target.getContents(requestor);
    }

    /** Sets the current contents of the clipboard to the specified
     * {@code Transferable} object.
     *
     * @param contents The {@code Transferable} object representing clipboard
     * content.
     */
    @Override
    public void setContents(Transferable contents, ClipboardOwner owner) {
        target.setContents(contents, owner);
    }

}
