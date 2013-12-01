/**
 * @(#)EmptyIcon.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.gui;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

/**
 * EmptyIcon.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EmptyIcon implements Icon {
    private int width;
    private int height;
    
    public EmptyIcon(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

}
