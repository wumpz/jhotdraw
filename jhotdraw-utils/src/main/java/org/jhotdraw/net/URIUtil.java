/*
 * @(#)URIUtil.java
 * 
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.net;

import java.io.File;
import java.net.URI;

/**
 * URIUtil.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class URIUtil {

    /** Prevent instance creation. */
    private void URIUtil() {
    }

    /** Returns the name of an URI for display in the title bar of a window. */
    public static String getName(URI uri) {
        if (uri.getScheme()!=null&&"file".equals(uri.getScheme())) {
            return new File(uri).getName();
        }
        return uri.toString();
    }
}
