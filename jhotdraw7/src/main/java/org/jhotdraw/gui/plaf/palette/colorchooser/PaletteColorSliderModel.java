/*
 * @(#)PaletteColorSliderModel.java
 * 
 * Copyright (c) 2010 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 * 
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.gui.plaf.palette.colorchooser;

import java.awt.color.ColorSpace;
import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;
import org.jhotdraw.color.DefaultColorSliderModel;
import org.jhotdraw.gui.plaf.palette.PaletteColorSliderUI;

/**
 * PaletteColorSliderModel.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PaletteColorSliderModel extends DefaultColorSliderModel {

    PaletteColorSliderModel(ColorSpace colorSpace) {
        super(colorSpace);
    }
    /**
     * Configures a JSlider for this model.
     * If the JSlider is already configured for another model,
     * it is unconfigured first.
     */
    @Override
    public void configureSlider(int componentIndex, JSlider slider) {
        if (slider.getClientProperty("colorSliderModel") != null) {
            ((DefaultColorSliderModel) slider.getClientProperty("colorSliderModel")).unconfigureSlider(slider);
        }
        if (!(slider.getUI() instanceof PaletteColorSliderUI)) {
            slider.setUI((PaletteColorSliderUI) PaletteColorSliderUI.createUI(slider));
        }
        BoundedRangeModel brm = getBoundedRangeModel(componentIndex);
        slider.setModel(brm);
        
        slider.putClientProperty("colorSliderModel", this);
        slider.putClientProperty("colorComponentIndex", new Integer(componentIndex));
        addColorSlider(slider);
    }

}
