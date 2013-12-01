/**
 * @(#)SimpleHarmonicRule.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.color;

import java.awt.Color;

/**
 * SimpleHarmonicRule.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleHarmonicRule extends AbstractHarmonicRule {

    private float difference;
    private int componentIndex;

    public SimpleHarmonicRule(int componentIndex, float difference, int baseIndex, int... derivedIndices) {
        this.componentIndex = componentIndex;
        this.difference = difference;
        this.baseIndex = baseIndex;
        this.derivedIndices = derivedIndices;
    }

    public void setConstraint(float constraint) {
        this.difference = constraint;
    }

    public float getConstraint(float constraint) {
        return constraint;
    }

    public void setComponentIndex(int newValue) {
        this.componentIndex = newValue;
    }

    public int getComponentIndex() {
        return componentIndex;
    }

    @Override
    public void apply(HarmonicColorModel model) {
        if (derivedIndices != null) {
            Color baseColor = model.get(getBaseIndex());
            if (baseColor != null) {
                float[] derivedComponents = null;
                for (int i = 0; i < derivedIndices.length; i++) {
                    derivedComponents = baseColor.getComponents(derivedComponents);
                    derivedComponents[componentIndex] += difference * (i + 1);
                    model.set(derivedIndices[i], new Color(model.getColorSpace(), derivedComponents,1f));
                }
            }
        }
    }

    @Override
    public void colorChanged(HarmonicColorModel model, int index, Color oldValue, Color newValue) {
        //
    }
}
