/*
 * @(#)BorderRectangle2D.DoubleFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.mini;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.RectangleFigure;

/**
 * BorderRectangle2D.DoubleFigure.
 *
 * @deprecated This class should be in one of the samples package
 */
@Deprecated
public class BorderRectangleFigure extends RectangleFigure {

  private static final long serialVersionUID = 1L;
  protected Border border;
  protected static final JComponent BORDER_COMPONENT = new JPanel();

  public BorderRectangleFigure(Border border) {
    this.border = border;
  }

  public void drawFigure(Graphics2D g) {
    Rectangle bounds = getBounds().getBounds();
    border.paintBorder(BORDER_COMPONENT, g, bounds.x, bounds.y, bounds.width, bounds.height);
  }
}
