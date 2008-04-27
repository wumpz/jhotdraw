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

    public void init(HarmonicColorModel model);

    public void apply(HarmonicColorModel model, CompositeColor oldBaseValue);

    public void adjust(HarmonicColorModel model, int adjustedIndex);
}
