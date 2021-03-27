/**
 * @(#)AbstractHarmonicRule.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.color;

/**
 * AbstractHarmonicRule.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractHarmonicRule implements HarmonicRule {

    protected int baseIndex;
    protected int[] derivedIndices;

    /**
     * set the baseIndex to a specific value
     * @param baseIndex new value of baseIndex
     */
    public void setBaseIndex(int baseIndex) {
         this.baseIndex = baseIndex;
    }

    @Override
    /**
     * @return current baseIndex
     */
    public int getBaseIndex() {
        return baseIndex;
    }

    @Override
    /**
     * @param list of  derived indices
     */
    public void setDerivedIndices(int... indices) {
        this.derivedIndices = indices;
    }

    @Override
    /**
     * @return derived Indices list 
     */
    public int[] getDerivedIndices() {
        return derivedIndices;
    }
}
