/**
 * @(#)BackdropBorder.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.gui.plaf.palette;

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
    private Border foregroundBorder;
    
    /**
     * initiate the editor's background borders 
     * @param backgroundBorder editor's background border
     */
    public BackdropBorder(Border backgroundBorder) {
        this(null, backgroundBorder);
    }
    /**
     * initiates editor borders with specific values of boths 
     * @param foregroundBorder editor foreground border
     * @param backgroundBorder editor background border
     */
    public BackdropBorder(Border foregroundBorder, Border backgroundBorder) {
        this.foregroundBorder = foregroundBorder;
        this.backgroundBorder = backgroundBorder;
    }
    /**
     * @return editor's background border
     */
    public Border getBackdropBorder() {
        return backgroundBorder;
    }

    @Override
    /**
     * paint the editor's foreground border 
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (foregroundBorder != null) {
            foregroundBorder.paintBorder(c, g, x, y, width, height);
        }
    }

    @Override
    /**
     * returns the insets of a specific component 
     */
    public Insets getBorderInsets(Component c) {
        if (foregroundBorder != null) {
            return foregroundBorder.getBorderInsets(c);
        } else {
            return backgroundBorder.getBorderInsets(c);
        }
    }

    @Override
    /**
     * check whether the background border is opaque or not 
     */
    public boolean isBorderOpaque() {
        return backgroundBorder.isBorderOpaque();
    }

    public static class UIResource extends BackdropBorder implements javax.swing.plaf.UIResource {

        public UIResource(Border backgroundBorder) {
            this(null, backgroundBorder);
        }

        public UIResource(Border foregroundBorder, Border backgroundBorder) {
            super(foregroundBorder, backgroundBorder);
        }
    }
}
