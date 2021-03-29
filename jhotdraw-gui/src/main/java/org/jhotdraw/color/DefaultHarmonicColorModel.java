/**
 * @(#)DefaultHarmonicColorModel.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.color;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.beans.*;
import java.util.ArrayList;
import javax.swing.*;

/**
 * DefaultHarmonicColorModel.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DefaultHarmonicColorModel extends AbstractListModel implements HarmonicColorModel, Cloneable {

    private static final long serialVersionUID = 1L;
    protected PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
    private ArrayList<Color> colors;
    private ColorSliderModel sliderModel;
    private int base;
    private ArrayList<HarmonicRule> rules;
    private int adjusting;

    public DefaultHarmonicColorModel() {
        ColorSpace sys = HSLPhysiologicColorSpace.getInstance();
        sliderModel = new DefaultColorSliderModel(sys);
        colors = new ArrayList<>();
        rules = new ArrayList<>();
        base = 0;
        add(Color.RED);
        DefaultListModel x;
    }

    @Override
    /**
     * sets the size to a new value
     */
    public void setSize(int newValue) {
        int oldSize = size();
        while (colors.size() > newValue) {
            colors.remove(colors.size() - 1);
        }
        while (colors.size() < newValue) {
            colors.add(null);
        }
        if (oldSize < newValue) {
            fireIntervalRemoved(this, oldSize, newValue - 1);
        } else if (oldSize > newValue) {
            fireIntervalRemoved(this, newValue, oldSize - 1);
        }
    }

    @Override
    /**
     * @return colors size
     */
    public int size() {
        return colors.size();
    }

    @Override
    /**
     * @return true if colors are adjusted 
     */
    public boolean isAdjusting() {
        return adjusting > 0;
    }

    @Override
    /**
     * sets the color a t index index to a new value 
     *@param index old color index 
     *@param newValue new color value 
     */
    public void set(int index, Color newValue) {
        adjusting++;
        Color oldValue = colors.set(index, newValue);
        for (HarmonicRule r : rules)
            r.colorChanged(this, index, oldValue, newValue);
        applyRules();
        adjusting--;
        fireContentsChanged(this, index, index);
    }

    @Override
    /**
     * apply instance harmonic rules 
     */
    public void applyRules() {
        for (HarmonicRule r : rules) 
            if (r.getBaseIndex() == base) 
                r.apply(this);
    }

    @Override
    /**
     * gets the color at the index index
     * @param index of the color 
     */
    public Color get(int index) {
        return colors.get(index);
    }

    @Override
    /**
     * add a color 
     * @param c color to add
     */
    public boolean add(Color c) {
        if (colors.add(c)) {
            fireIntervalAdded(this, size() - 1, size() - 1);
            return true;
        }
        return false;
    }

    @Override
    /**
     * set the base to a new value 
     * @param newValue new base value 
     */
    public void setBase(int newValue) {
        base = newValue;
    }

    @Override
    /**
     * @return base value 
     */
    public int getBase() {
        return base;
    }

    @Override
    /**
     * converts rgb values to components 
     * @param rgb rgb values
	 * @return converted rgb values 
     */
    public float[] RGBtoComponent(int rgb, float[] hsb) {
        return ColorUtil.fromColor(sliderModel.getColorSpace(), new Color(rgb));
    }

    @Override
    /**
     * connverts hsb values to rgb 
     */
    public int componentToRGB(float h, float s, float b) {
        return ColorUtil.toRGB24(sliderModel.getColorSpace(), h, s, b);
    }

    @Override
    /**
     * @return size
     */
    public int getSize() {
        return size();
    }

    @Override
    /**
     * @return the element at index index 
     */
    public Object getElementAt(int index) {
        return get(index);
    }

    @Override
    /**
     * @return color space 
     */
    public ColorSpace getColorSpace() {
        return sliderModel.getColorSpace();
    }

    @Override
    /**
     * add new harmonic rule to the model 
     */
    public void addRule(HarmonicRule newValue) {
        rules.add(newValue);
    }

    @Override
    /**
     * remove all model's harmonic rules 
     */
    public void removeAllRules() {
        rules.clear();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(propertyName, listener);
    }

    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    /**
     * returns a copy of the Default Harmonic Color Model
     */
    public DefaultHarmonicColorModel clone() {
        DefaultHarmonicColorModel that;
        try {
            that = (DefaultHarmonicColorModel) super.clone();
        } catch (CloneNotSupportedException ex) {
            InternalError error = new InternalError("Clone failed");
            error.initCause(ex);
            throw error;
        }
        that.propertySupport = new PropertyChangeSupport(that);
        return that;
    }

    @Override
    /**
     * sets the color space to a new value 
     */
    public void setColorSpace(ColorSpace newValue) {
        ColorSpace oldValue = sliderModel.getColorSpace();
        sliderModel.setColorSpace(newValue);
        firePropertyChange(COLOR_SPACE_PROPERTY, oldValue, newValue);
        for (int i = 0; i < colors.size(); i++) {
            if (get(i) != null) {
                set(i, new Color(newValue, ColorUtil.fromColor(newValue, get(i)), 1f));
            }
        }
        fireContentsChanged(this, 0, size() - 1);
    }
}
