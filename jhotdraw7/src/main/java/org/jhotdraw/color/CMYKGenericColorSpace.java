/*
 * @(#)CMYKGenericColorSpace.java
 * 
 * Copyright (c) 2010 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 * 
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
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
