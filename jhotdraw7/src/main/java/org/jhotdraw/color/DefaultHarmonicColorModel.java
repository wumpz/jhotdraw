/**
 * @(#)DefaultHarmonicColorModel.java  1.0  Apr 19, 2008
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

import java.awt.Color;
import java.beans.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.*;

import static org.jhotdraw.color.HarmonicColorModel.*;

/**
 * DefaultHarmonicColorModel.
 *
 * @author Werner Randelshofer
 * @version 1.0 Apr 19, 2008 Created.
 */
public class DefaultHarmonicColorModel extends AbstractListModel implements HarmonicColorModel {

    protected PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
    private ArrayList<CompositeColor> colors;
    private ColorSliderModel sliderModel;
    private int base;
    private HarmonicRule hueRule;
    private HarmonicRule saturationRule;
    private HarmonicRule lightnessRule;
    private float customHueConstraint = 30f / 360f;
    private int adjusting;

    public static class ColorSet {

        public String name;
        public ArrayList<Color> colors = new ArrayList<Color>();
    }

    public DefaultHarmonicColorModel() {
        ColorSystem sys = new HSLRYBColorSystem();
        sliderModel = new DefaultColorSliderModel(sys);
        colors = new ArrayList<CompositeColor>();

        base = 0;
        add(new CompositeColor(sys, Color.RED));

        setHueRule(new HueHarmonicRule(30f / 360f));
        setSaturationRule(null);
        setLightnessRule(null);

        initRules();
        applyRules(get(0));

        DefaultListModel x;
    }

    public void setSize(int newValue) {
        int oldSize = size();
        while (colors.size() > newValue) {
            colors.remove(colors.size() - 1);
        }
        ColorSystem sys = sliderModel.getColorSystem();
        while (colors.size() < newValue) {
            colors.add(null);
        }

        if (oldSize < newValue) {
            fireIntervalRemoved(this, oldSize, newValue - 1);
        } else if (oldSize > newValue) {
            fireIntervalRemoved(this, newValue, oldSize - 1);
        }
    }

    public int size() {
        return colors.size();
    }

    public void set(int index, CompositeColor newValue) {
        CompositeColor oldValue = colors.set(index, newValue);
        fireContentsChanged(this, index, index);

        if (index == base) {
            applyRules(oldValue);
        } else {
            adjustRule(index);
        }
    }

    private void monochromatic(float[] orig, float[] der, float adjust) {
        System.arraycopy(orig, 0, der, 0, 3);

        if (adjust <= 0) {
            if (orig[2] >= 1f && orig[1] + adjust < 0.2f) {
                adjust = 1.4f + adjust;
            }
        } else {
            if (orig[2] <= 1f && orig[2] - adjust / 2f <= 0.5f) {
                adjust = adjust - 1.4f;
            }
        }

        if (adjust <= 0) {
            if (orig[2] < 1f) { // outer ring
                der[2] = orig[2] - adjust / 2;
                if (der[2] > 1f) {
                    //der[1] = 2f - der[2];
                    der[1] = 3f - der[2] * 2f;
                    der[2] = 1f;
                } else {
                    der[1] = 1f;
                }
            } else { // inner ring
                der[1] = orig[1] + adjust;
                der[2] = 1f;
            }
        } else {
            if (orig[2] < 1f) { // outer ring
                der[1] = 1f;
                der[2] = orig[2] - adjust / 2;
            } else { // inner ring
                der[1] = orig[1] + adjust;
                if (der[1] > 1f) {
                    //der[2] = 2f - der[1];
                    der[2] = 1.5f - der[1] / 2f;
                    der[1] = 1f;
                } else {
                    der[2] = 1f;
                }
            }
        }
    }

    private void achromatic(float[] orig, float[] der, float adjust) {
        System.arraycopy(orig, 0, der, 0, 3);

        if (adjust <= 0) {
            if (orig[2] >= 1f && orig[1] + adjust < 0.05f) {
                adjust = 1.4f + adjust;
            }
        } else {
            if (orig[2] <= 1f && orig[2] - adjust / 2f <= 0.5f) {
                adjust = adjust - 1.4f;
            }
        }

        if (adjust <= 0) {
            if (orig[2] < 1f) { // outer ring
                der[2] = orig[2] - adjust / 2;
                if (der[2] > 1f) {
                    //der[1] = 2f - der[2];
                    der[1] = 3f - der[2] * 2f;
                    der[2] = 1f;
                } else {
                    der[1] = 1f;
                }
            } else { // inner ring
                der[1] = orig[1] + adjust;
                der[2] = 1f;
            }
        } else {
            if (orig[2] < 1f) { // outer ring
                der[1] = 1f;
                der[2] = orig[2] - adjust / 2;
            } else { // inner ring
                der[1] = orig[1] + adjust;
                if (der[1] > 1f) {
                    //der[2] = 2f - der[1];
                    der[2] = 1.5f - der[1] / 2f;
                    der[1] = 1f;
                } else {
                    der[2] = 1f;
                }
            }
        }

        if (der[2] == 1f) {
            der[2] = Math.max(0f, Math.min(1f, (3f - Math.abs(der[1])) / 3f));
        } else {
            der[2] = Math.min(1f, der[2] / 1.5f);
        }
        der[1] = 0f;

    }

    private void analogous(float[] orig, float[] der, float adjust) {
        System.arraycopy(orig, 0, der, 0, 3);

        der[0] += adjust;
    }

    private void clash(float[] orig, float[] der, float adjust) {
        System.arraycopy(orig, 0, der, 0, 3);

        der[0] += adjust;
    }

    public CompositeColor get(int index) {
        return colors.get(index);
    }

    public boolean add(CompositeColor c) {
        boolean b = colors.add(c);
        if (b) {
            fireIntervalAdded(this, size() - 1, size() - 1);
        }
        return b;
    }

    public void setBase(int newValue) {
        base = newValue;
    }

    public int getBase() {
        return base;
    }

    public float[] RGBtoComponent(int rgb, float[] hsb) {
        return sliderModel.getColorSystem().toComponents(rgb, hsb);
    }

    public int componentToRGB(float h, float s, float b) {
        return sliderModel.getColorSystem().toRGB(h, s, b);
    }

    public int getSize() {
        return size();
    }

    public Object getElementAt(int index) {
        return get(index);
    }

    public ColorSystem getColorSystem() {
        return sliderModel.getColorSystem();
    }

    public void setHueRule(HarmonicRule newValue) {
        HarmonicRule oldValue = hueRule;
        hueRule = newValue;

        initRules();
    }

    public HarmonicRule getHueRule() {
        return hueRule;
    }

    public void setLightnessRule(HarmonicRule newValue) {
        HarmonicRule oldValue = lightnessRule;
        lightnessRule = newValue;
        initRules();
    }

    public HarmonicRule getLightnessRule() {
        return lightnessRule;
    }

    public void setSaturationRule(HarmonicRule newValue) {
        HarmonicRule oldValue = saturationRule;
        saturationRule = newValue;

        initRules();
    }

    public HarmonicRule getSaturationRule() {
        return saturationRule;
    }

    protected void initRules() {
        adjusting++;
        if (hueRule != null) {
            hueRule.init(this);
            hueRule.apply(this, get(getBase()));
        }
        if (lightnessRule != null) {
            lightnessRule.init(this);
            lightnessRule.apply(this, get(getBase()));
        } else {
            for (int i=0; i < size(); i++) {
                if (i % 5 != 0) {
                    colors.set(i, null);
                }
            }
        }
        fireContentsChanged(this, 1, size() - 1);
        adjusting--;
    }

    protected void applyRules(CompositeColor oldValue) {
        if (adjusting == 0) {
        adjusting++;
        if (hueRule != null) {
            hueRule.apply(this, oldValue);
        }
        if (lightnessRule != null) {
            lightnessRule.apply(this, oldValue);
        }
        adjusting--;
        fireContentsChanged(this, 1, size() - 1);
        }
    }

    private void adjustRule(int index) {
        if (adjusting == 0) {
        adjusting++;
        if (index == base) {
            return;
        }
        if (hueRule != null) {
            hueRule.adjust(this, index);
        }
        if (lightnessRule != null) {
            lightnessRule.adjust(this, index);
        }
        adjusting--;
        fireContentsChanged(this, 1, size() - 1);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }

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

    public void setColorSystem(ColorSystem newValue) {
        ColorSystem oldValue = sliderModel.getColorSystem();
        sliderModel.setColorSystem(newValue);
        firePropertyChange(COLOR_SYSTEM_PROPERTY, oldValue, newValue);
        for (int i=0; i < colors.size(); i++) {
            if (get(i) != null) {
                set(i, new CompositeColor(newValue, get(i).getColor()));
            }
        }
        fireContentsChanged(this, 0, size() - 1);
    }
}
