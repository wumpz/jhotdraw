/**
 * @(#)HueHarmonicRule.java  1.0  Apr 27, 2008
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
 * HueHarmonicRule.
 *
 * @author Werner Randelshofer
 * @version 1.0 Apr 27, 2008 Created.
 */
public class HueHarmonicRule implements HarmonicRule {
    

        private int[] indices;
        private float constraint;

        public HueHarmonicRule(float constraint) {
            this.constraint = constraint;
            if (Math.abs(constraint) == 0.5f) {
                indices = new int[] {5};
            } else {
                indices = new int[] {5, 10};
                //indices = new int[]{5, 10, 15, 20};
            }
        }

        public void init(HarmonicColorModel m) {
            m.setSize(indices.length * 5 + 5);
            CompositeColor bc = m.get(m.getBase());
            float baseSat = bc.getComponent(1);
            float baseVal = bc.getComponent(2);
            for (int i=0; i < indices.length; i++) {
                m.set(indices[i], new CompositeColor(m.getColorSystem(), 0f, baseSat, baseVal));
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
                float mo = (i == 0) ? -1 : 1;
                m.set(indices[i],
                        new CompositeColor(
                        m.getColorSystem(),
                        bc.getComponent(0) + constraint * mo,
                        (satFactor == 0) ? cc.getComponent(1) : cc.getComponent(1) * satFactor,
                        (valFactor == 0) ? cc.getComponent(2) : cc.getComponent(2) * valFactor));
            }
        }

        public void adjust(HarmonicColorModel m, int index) {
            CompositeColor bc = m.get(m.getBase());
            for (int i = 0; i < indices.length; i++) {
                if (indices[i] == index) {
                    float mo = (i == 0) ? -1 : 1;
                    CompositeColor cc = m.get(indices[i]);
                    m.set(indices[i],
                            new CompositeColor(
                            m.getColorSystem(),
                            bc.getComponent(0) + constraint * mo,
                            cc.getComponent(1),
                            cc.getComponent(2)));
                }
            }
        }
    }


