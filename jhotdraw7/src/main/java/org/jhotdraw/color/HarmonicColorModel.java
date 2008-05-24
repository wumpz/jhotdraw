/**
 * @(#)HarmonicColorModel.java  1.0  Apr 19, 2008
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

import java.awt.Color;
import java.beans.PropertyChangeListener;
import javax.swing.ListModel;

/**
 * HarmonicColorModel.
 *
 * @author Werner Randelshofer
 * @version 1.0 Apr 19, 2008 Created.
 */
public interface HarmonicColorModel extends ListModel {
    public final static String COLOR_SYSTEM_PROPERTY = "colorSystem";
    
    public void setBase(int newValue);
    public int getBase();
    
    public void addRule(HarmonicRule rule);
    public void removeAllRules();
    public void applyRules();

    public ColorSystem getColorSystem();
    public void setColorSystem(ColorSystem newValue);
   
    public void setSize(int newValue);
    public int size();
    
    public boolean isAdjusting();
    
    public boolean add(CompositeColor c);
    public void set(int index, CompositeColor color);
    public CompositeColor get(int index);
    public float[] RGBtoComponent(int rgb, float[] hsb);
    public int componentToRGB(float h, float s, float b);

    public void addPropertyChangeListener(PropertyChangeListener listener);
    public void removePropertyChangeListener(PropertyChangeListener listener);
}
