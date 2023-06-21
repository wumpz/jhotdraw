/*
 * @(#)PaletteColorSliderUI.java
 *
 * Copyright (c) 2010 The authors and contributors of JHotDraw.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.gui.plaf.palette;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import org.jhotdraw.color.ColorSliderUI;

/** PaletteColorSliderUI. */
public class PaletteColorSliderUI extends ColorSliderUI {

  public PaletteColorSliderUI(JSlider b) {
    super(b);
  }

  public static ComponentUI createUI(JComponent b) {
    return new PaletteColorSliderUI((JSlider) b);
  }

  @Override
  protected Icon getThumbIcon() {
    String key;
    if (slider.getOrientation() == JSlider.HORIZONTAL) {
      key = "Slider.northThumb.small";
    } else {
      key = "Slider.westThumb.small";
    }
    Icon icon = PaletteLookAndFeel.getInstance().getIcon(key);
    if (icon == null) {
      throw new InternalError(key + " missing in PaletteLookAndFeel");
    }
    return icon;
  }
}
