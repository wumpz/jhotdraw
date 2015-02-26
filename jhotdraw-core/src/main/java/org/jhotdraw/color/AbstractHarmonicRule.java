/**
 * @(#)AbstractHarmonicRule.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.color;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * AbstractHarmonicRule.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractHarmonicRule implements HarmonicRule {
    protected int baseIndex;
    protected int[] derivedIndices;
    
    
    @Override
    public void setBaseIndex() {
       // this.baseIndex = baseIndex;
    }

    @Override
    public int getBaseIndex() {
        return baseIndex;
    }

    @Override
    public void setDerivedIndices(int... indices) {
        this.derivedIndices = indices;
    }

    @Override
    public int[] getDerivedIndices() {
        return derivedIndices;
    }

}
