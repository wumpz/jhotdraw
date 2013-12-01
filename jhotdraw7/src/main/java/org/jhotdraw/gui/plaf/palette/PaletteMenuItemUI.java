/*
 * @(#)PaletteMenuItemUI.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.gui.plaf.palette;

import javax.swing.*;
import java.awt.*;

/**
 * PaletteMenuItemUI.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class PaletteMenuItemUI extends javax.swing.plaf.basic.BasicMenuItemUI {
    /** Creates a new instance. */
    public PaletteMenuItemUI() {
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        defaultTextIconGap = 0;   // Should be from table
        //menuItem.setBorderPainted(false);
        //menuItem.setBorder(null);
        arrowIcon = null;
        checkIcon = null;
    }
    @Override
    protected Dimension getPreferredMenuItemSize(JComponent c,
                                                     Icon checkIcon,
                                                     Icon arrowIcon,
                                                     int defaultTextIconGap) {
        JMenuItem b = (JMenuItem) c;
        Icon icon = b.getIcon(); 
        if (icon == null) {
            return new Dimension(22, 22);
        }
        return new Dimension(icon.getIconWidth() + 2, icon.getIconHeight() + 2);
    }
    @Override
    public void paint(Graphics g, JComponent c) {
        JMenuItem b = (JMenuItem) c;

        // Paint background
	paintBackground(g, b, selectionBackground);

        // Paint the icon
        //((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Icon icon = b.getIcon(); 
        if (icon != null) {
        icon.paintIcon(b, g, 1, 1);
        }
    }
}
