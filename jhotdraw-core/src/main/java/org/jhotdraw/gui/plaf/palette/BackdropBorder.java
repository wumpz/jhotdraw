/**
 * @(#)BackdropBorder.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.gui.plaf.palette;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.Border;

/**
 * BackdropBorder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BackdropBorder implements Border {

    private Border backgroundBorder;
    @Nullable private Border foregroundBorder;

    public BackdropBorder(Border backgroundBorder) {
        this(null, backgroundBorder);
    }

    public BackdropBorder(@Nullable Border foregroundBorder, Border backgroundBorder) {
        this.foregroundBorder = foregroundBorder;
        this.backgroundBorder = backgroundBorder;
    }

    public Border getBackdropBorder() {
        return backgroundBorder;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (foregroundBorder != null) {
            foregroundBorder.paintBorder(c, g, x, y, width, height);
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        if (foregroundBorder != null) {
            return foregroundBorder.getBorderInsets(c);
        } else {
            return backgroundBorder.getBorderInsets(c);
        }
    }

    @Override
    public boolean isBorderOpaque() {
        return backgroundBorder.isBorderOpaque();
    }

    public static class UIResource extends BackdropBorder implements javax.swing.plaf.UIResource {

        public UIResource(Border backgroundBorder) {
            this(null, backgroundBorder);
        }

        public UIResource(@Nullable Border foregroundBorder, Border backgroundBorder) {
            super(foregroundBorder, backgroundBorder);
        }
    }
}
