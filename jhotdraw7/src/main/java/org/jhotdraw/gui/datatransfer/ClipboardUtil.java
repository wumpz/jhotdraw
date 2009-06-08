/*
 * @(#)ClipboardUtil.java
 * 
 * Copyright (c) 2009 by the original authors of JHotDraw
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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

/**
 * {@code ClipboardUtil} can be used as a proxy when the system clipboard is not
 * available due to security restrictions.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ClipboardUtil {
    /** Holds the clipbard proxy when the system clipboard is not available. */
    private static Clipboard proxy;

    /** Gets the system clipboard if permitted, gets a proxy otherwise.
     *
     * @return system clipboard or a proxy.
     */
    public static Clipboard getClipboard() {
        if (proxy != null) {
            return proxy;
        }

        try {
            return Toolkit.getDefaultToolkit().getSystemClipboard();
        } catch (SecurityException e) {
            if (proxy == null) {
                proxy = new Clipboard("Clipboard Proxy");
            }
            return getClipboard();
        }
    }
}
