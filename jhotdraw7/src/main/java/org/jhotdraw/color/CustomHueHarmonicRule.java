/**
 * @(#)CustomHueHarmonicRule.java  1.0  Apr 27, 2008
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
 * CustomHueHarmonicRule.
 *
 * @author Werner Randelshofer
 * @version 1.0 Apr 27, 2008 Created.
 */
public class CustomHueHarmonicRule implements HarmonicRule {

        private int[] indices = {5, 10, 15, 20};
        private float constraint = 30f / 360f;

        public CustomHueHarmonicRule(float constraint) {
            this.constraint = constraint;
        }
        
        public void init(HarmonicColorModel m) {
            m.setSize(indices.length * 5 + 5);
            CompositeColor bc = m.get(m.getBase());
            float baseSat = bc.getComponent(1);
            float baseVal = bc.getComponent(2);
            for (int i=0; i < indices.length; i++) {
            m.set(indices[i], new CompositeColor(m.getColorSystem(), 0f, baseSat, (i % 2 == 0) ? baseVal * 0.9f : baseVal));
            }
        }

        public void apply(HarmonicColorModel m, CompositeColor oldValue) {
            CompositeColor bc = m.get(m.getBase());
            float oldSat = oldValue.getComponent(1);
            float baseSat = bc.getComponent(1);
            float satFactor;
            if (oldSat == baseSat || oldSat == 0) {
                satFactor = 0f;
            } else {
                satFactor = baseSat / oldSat;
            }
            float oldVal = oldValue.getComponent(2);
            float baseVal = bc.getComponent(2);
            float valFactor;
            if (oldVal == baseVal || oldVal == 0) {
                valFactor = 0f;
            } else {
                valFactor = baseVal / oldVal;
            }

            for (int i = 0; i < indices.length; i++) {
                CompositeColor cc = m.get(indices[i]);
                if (cc == null) {
                    cc = bc.clone();
                }
                float mo = ((i % 2) + 1) / 2f;
                if (i >= indices.length / 2) {
                    mo = -mo;
                }
                m.set(indices[i],
                        new CompositeColor(
                        m.getColorSystem(),
                        bc.getComponent(0) + constraint * mo,
                        (satFactor == 0) ? cc.getComponent(1) : cc.getComponent(1) * satFactor,
                        (valFactor == 0) ? cc.getComponent(2) : cc.getComponent(2) * valFactor));
            }
        }

        public void adjust(HarmonicColorModel m, int index) {
            float oldValue = constraint;
            CompositeColor bc = m.get(m.getBase());

            for (int i = 0; i < indices.length; i++) {
                if (index == indices[i]) {
                    float mo = ((i % 2) + 1) / 2f;
                    if (i >= indices.length / 2) {
                        mo = -mo;
                    }
                    CompositeColor cc = m.get(index);
                    if (bc.getComponent(0) < cc.getComponent(0)) {
                        constraint = bc.getComponent(0) - cc.getComponent(0);
                    } else {
                        constraint = bc.getComponent(0) - 1 -cc.getComponent(0);
                    }
                    constraint /= mo;
                    if (constraint > 1f) {
                        constraint -= Math.floor(constraint);
                    }
                    if (constraint < 0f) {
                        constraint = -constraint;
                    }
                    if (i > 1) {
                        constraint = 1f - constraint;
                    }
                    break;
                }
            }

            for (int i = 0; i < indices.length; i++) {
                //if (indices[i] != index) {
                CompositeColor cc = m.get(indices[i]);
                float mo = ((i % 2) + 1) / 2f;
                if (i >= indices.length / 2) {
                    mo = -mo;
                }
                m.set(indices[i],
                        new CompositeColor(
                        m.getColorSystem(),
                        bc.getComponent(0) + constraint * mo,
                        cc.getComponent(1),
                        cc.getComponent(2)));
            //}
            }
            //firePropertyChange(CUSTOM_HUE_CONSTRAINT_PROPERTY, oldValue, constraint);
        }
    }


