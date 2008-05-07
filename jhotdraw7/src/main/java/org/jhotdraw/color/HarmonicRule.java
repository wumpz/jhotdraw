/**
 * @(#)HarmonicRule.java  1.0  Apr 27, 2008
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
 * HarmonicRule.
 *
 * @author Werner Randelshofer
 * @version 1.0 Apr 27, 2008 Created.
 */
public interface HarmonicRule {
    
    public void setBaseIndex();
    
    public int getBaseIndex();
    
    public void setDerivedIndices(int... indices);
    
    public int[] getDerivedIndices();
    
    public void apply(HarmonicColorModel model);
    
    public void colorChanged(HarmonicColorModel model, int index, CompositeColor oldValue, CompositeColor newValue);
}
