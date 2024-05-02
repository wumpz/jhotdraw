/**
 * @(#)PaletteButtonBorder.java
 *
 * <p>Copyright (c) 2008 The authors and contributors of JHotDraw. You may not use, copy or modify
 * this file, except in compliance with the accompanying license terms.
 */
package org.jhotdraw.gui.plaf.palette;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.geom.Point2D;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

/** PaletteButtonBorder. */
public class PaletteButtonBorder implements Border, UIResource {

  private static final float[] ENABLED_STOPS = new float[] {0f, 0.35f, 0.4f, 1f};
  private static final Color[] ENABLED_STOP_COLOR = new Color[] {
    new Color(0xf8f8f8), new Color(0xeeeeee), new Color(0xcacaca), new Color(0xffffff)
  };
  private static final float[] SELECTED_STOPS = new float[] {0f, 0.1f, 0.9f, 1f};
  private static final Color[] SELECTED_STOP_COLORS = new Color[] {
    new Color(0x666666), new Color(0xcccccc), new Color(0x999999), new Color(0xb1b1b1)
  };

  @Override
  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    if (c instanceof AbstractButton) {
      paintBorder((AbstractButton) c, g, x, y, width, height);
    }
  }

  public void paintBorder(AbstractButton c, Graphics gr, int x, int y, int width, int height) {
    Graphics2D g = (Graphics2D) gr;
    ButtonModel m = c.getModel();
    int borderColor;
    float[] stops;
    Color[] stopColors;
    if (!m.isEnabled()) {
      borderColor = 0x80a5a5a5;
      stops = ENABLED_STOPS;
      stopColors = ENABLED_STOP_COLOR;
    } else {
      if (m.isSelected() || m.isPressed() && m.isArmed()) {
        borderColor = 0xff333333;
        stops = SELECTED_STOPS;
        stopColors = SELECTED_STOP_COLORS;
      } else {
        borderColor = 0xffa5a5a5;
        stops = ENABLED_STOPS;
        stopColors = ENABLED_STOP_COLOR;
      }
    }
    String segmentPosition = getSegmentPosition(c);
    if ("first".equals(segmentPosition) || "middle".equals(segmentPosition)) {
      width += 1;
    }
    g.setColor(new Color(borderColor, true));
    g.drawRect(x, y, width - 1, height - 1);
    LinearGradientPaint lgp = new LinearGradientPaint(
        new Point2D.Float(x, y),
        new Point2D.Float(x, y + height - 1),
        stops,
        stopColors,
        MultipleGradientPaint.CycleMethod.REPEAT);
    g.setPaint(lgp);
    g.fillRect(x + 1, y + 1, width - 2, height - 2);
  }

  private String getSegmentPosition(Component c) {
    String segmentPosition = null;
    if (c instanceof JComponent) {
      segmentPosition =
          (String) ((JComponent) c).getClientProperty("Palette.Component.segmentPosition");
    }
    return (segmentPosition == null) ? "only" : segmentPosition;
  }

  @Override
  public Insets getBorderInsets(Component c) {
    Insets insets;
    String segmentPosition = getSegmentPosition(c);
    if ("first".equals(segmentPosition) || "middle".equals(segmentPosition)) {
      insets = new Insets(3, 3, 3, 2);
    } else {
      insets = new Insets(3, 3, 3, 3);
    }
    return insets;
  }

  @Override
  public boolean isBorderOpaque() {
    return true;
  }
}
