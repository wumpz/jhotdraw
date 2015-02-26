/*
 * @(#)NamedColorSpace.java
 * 
 * Copyright (c) 2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
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
