/**
 * @(#)ColorSystem.java
 *
 * Copyright (c) 2008 by the original authors of JHotDraw
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

/**
 * A ColorSystem defines a system to describe colors using a number of
 * components. 
 *
 * @author Werner Randelshofer
 * @version $Id$
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
    
    /**
     * Returns the minimum normalized color component value for the
     * specified component.

     * @param component The component.
     * @return The minimal value.
     */
    public float getMinValue(int component);

    /**
     * Returns the maximum normalized color component value for the
     * specified component.

     * @param component The component.
     * @return The maximal value.
     */
    public float getMaxValue(int component);

    /** Returns the name of the color system. */
    public String getName();

    @Override
    public boolean equals(Object that);

    @Override
    public int hashCode();
}
