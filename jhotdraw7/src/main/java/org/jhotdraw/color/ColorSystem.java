/**
 * @(#)ColorSystem.java  1.0  Apr 24, 2008
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
 * A ColorSystem defines a system to describe colors using a number of
 * components. Each component has a normalized value between 0 and 1.
 *
 * @author Werner Randelshofer
 * @version 1.0 Apr 24, 2008 Created.
 */
public interface ColorSystem {
    /**
     * Returns the number of color components used by the color system.
     * 
     * @return component count.
     */
    public int getComponentCount();
    
    /**
     * Converts the specified color components to RGB.
     * 
     * @param components The color components.
     * 
     * @return rgb value.
     */
    public int toRGB(float... components);
    /**
     * Converts the specified color components to RGB.
     * 
     * @param rgb value.
     * @param components A component array for reuse.
     * 
     * @return color components for the rgb value.
     */
    public float[] toComponents(int rgb, float[] components);
    
}
