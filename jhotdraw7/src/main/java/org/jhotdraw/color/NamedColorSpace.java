/*
 * @(#)NamedColorSpace.java
 * 
 * Copyright (c) 2010 by the original authors of JHotDraw and all its
 * contributors. All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * license agreement you entered into with the copyright holders. For details
 * see accompanying license terms.
 */
package org.jhotdraw.color;

import edu.umd.cs.findbugs.annotations.NonNull;
/**
 * Interface for {@code ColorSpace} classes which have a name.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface NamedColorSpace {

    public String getName();
    
    /** Faster toRGB method which uses the provided output array. */
    public float[] toRGB(float[] colorvalue, float[] rgb);
    /** Faster fromRGB method which uses the provided output array. */
    public float[] fromRGB(float[] rgb, float[] colorvalue);
    /** Faster toCIEXYZ method which uses the provided output array. */
    public float[] toCIEXYZ(float[] colorvalue, float[] xyz);
    /** Faster fromCIEXYZ method which uses the provided output array. */
    public float[] fromCIEXYZ(float[] xyz, float[] colorvalue);
    
}
