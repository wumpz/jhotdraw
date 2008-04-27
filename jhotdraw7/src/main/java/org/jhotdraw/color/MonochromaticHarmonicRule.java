/**
 * @(#)MonochromaticHarmonicRule.java  1.0  Apr 27, 2008
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
 * MonochromaticHarmonicRule.
 *
 * @author Werner Randelshofer
 * @version 1.0 Apr 27, 2008 Created.
 */
public class MonochromaticHarmonicRule implements HarmonicRule {

    private float constraint;

    public void init(HarmonicColorModel model) {
        constraint = 0.1f;
    }

    public void apply(HarmonicColorModel model, CompositeColor oldBaseValue) {
        CompositeColor base = model.get(0);
        for (int i = 0; i < model.size(); i++) {
            float m = (i % 5);
            if (m == 0) {
                base = model.get(i);
            } else if (m < 3) {
                if (base != null) {
                    if (base.getComponent(2) + m * constraint <= 1f) {
                    model.set(i, new CompositeColor(model.getColorSystem(),
                            base.getComponent(0),
                            base.getComponent(1),
                            base.getComponent(2) + m* constraint));
                    } else {
                    model.set(i, new CompositeColor(model.getColorSystem(),
                            base.getComponent(0),
                            base.getComponent(1) - (base.getComponent(2) + m * constraint) +1f,
                            1f));
                    }
                }
            }
        }
    }

    public void adjust(HarmonicColorModel model, int adjustedIndex) {
        apply(model, model.get(model.getBase()));
    }
}
