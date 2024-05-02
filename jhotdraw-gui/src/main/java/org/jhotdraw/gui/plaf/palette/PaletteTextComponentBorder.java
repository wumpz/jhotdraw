/**
 * @(#)PaletteTextComponentBorder.java
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

/** PaletteTextComponentBorder. */
public class PaletteTextComponentBorder implements Border, UIResource {

  private static final float[] ENABLED_STOPS = new float[] {0f, 0.2f};
  private static final Color[] ENABLED_STOP_COLORS =
      new Color[] {new Color(0xc8c8c8), new Color(0xffffff)};

  @Override
  public void paintBorder(Component c, Graphics gr, int x, int y, int width, int height) {
    Graphics2D g = (Graphics2D) gr;
    int borderColor;
    float[] stops;
    Color[] stopColors;
    if (!c.isEnabled()) {
      borderColor = 0x80a5a5a5;
      stops = ENABLED_STOPS;
      stopColors = ENABLED_STOP_COLORS;
    } else {
      borderColor = 0xffa5a5a5;
      stops = ENABLED_STOPS;
      stopColors = ENABLED_STOP_COLORS;
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
