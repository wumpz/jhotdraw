/*
 * @(#)CMYKGenericColorSpace.java
 * 
 * Copyright (c) 2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.color;

import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.IOException;

/**
 * A {@code ColorSpace} for CMYK color components using a generic CMYK profile.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CMYKGenericColorSpace extends ICC_ColorSpace {
    private static final long serialVersionUID = 1L;

    private static CMYKGenericColorSpace instance;

    public static CMYKGenericColorSpace getInstance() {
        if (instance == null) {
            try {
                instance = new CMYKGenericColorSpace();
            } catch (IOException ex) {
                InternalError error = new InternalError("Can't instanciate CMYKColorSpace");
                error.initCause(ex);
                throw error;
            }
        }
        return instance;
    }

    public CMYKGenericColorSpace() throws IOException {
        super(ICC_Profile.getInstance(CMYKGenericColorSpace.class.getResourceAsStream("Generic CMYK Profile.icc")));
    }
}
