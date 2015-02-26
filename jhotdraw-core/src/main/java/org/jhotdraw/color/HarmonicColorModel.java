/**
 * @(#)HarmonicColorModel.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.color;

import javax.annotation.Nullable;
import java.awt.Color;
import java.awt.color.ColorSpace;
import java.beans.PropertyChangeListener;
import javax.swing.ListModel;

/**
 * HarmonicColorModel.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface HarmonicColorModel extends ListModel {
    public static final String COLOR_SPACE_PROPERTY = "colorSpace";
    
    public void setBase(int newValue);
    public int getBase();
    
    public void addRule(HarmonicRule rule);
    public void removeAllRules();
    public void applyRules();

    public ColorSpace getColorSpace();
    public void setColorSpace(ColorSpace newValue);
   
    public void setSize(int newValue);
    public int size();
    
    public boolean isAdjusting();
    
    public boolean add(Color c);
    public void set(int index, @Nullable Color color);
    @Nullable public Color get(int index);
    public float[] RGBtoComponent(int rgb, float[] hsb);
    public int componentToRGB(float h, float s, float b);

    public void addPropertyChangeListener(PropertyChangeListener listener);
    public void removePropertyChangeListener(PropertyChangeListener listener);
}
