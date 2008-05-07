/**
 * @(#)AbstractHarmonicRule.java  1.0  May 1, 2008
 *
 * Copyright (c) 2008 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package org.jhotdraw.color;

/**
 * AbstractHarmonicRule.
 *
 * @author Werner Randelshofer
 * @version 1.0 May 1, 2008 Created.
 */
public abstract class AbstractHarmonicRule implements HarmonicRule {
    protected int baseIndex;
    protected int[] derivedIndices;
    
    
    public void setBaseIndex() {
        this.baseIndex = baseIndex;
    }

    public int getBaseIndex() {
        return baseIndex;
    }

    public void setDerivedIndices(int... indices) {
        this.derivedIndices = indices;
    }

    public int[] getDerivedIndices() {
        return derivedIndices;
    }

}
