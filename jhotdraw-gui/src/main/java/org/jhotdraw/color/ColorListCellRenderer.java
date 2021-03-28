/**
 * @(#)ColorListCellRenderer.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.color;

import java.awt.*;
import javax.swing.*;

/**
 * ColorListCellRenderer.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ColorListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;
    
    private static final int ICONWIDTH=24;
    private static final int ICONHEIGHT=18;
    private ColorIcon icon;
    private static class ColorIcon implements Icon {

        private Color color;
        /**
         * set list's cell color  
         * @param newValue new color value 
         */
        public void setColor(Color newValue) {
            color = newValue;
        }

        @Override
        /**
         * allows to paint an icon with stored color previously selected 
         */
        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (color != null) {
                g.setColor(new Color(0x333333));
                g.drawRect(x, y, getIconWidth() - 1, getIconHeight() - 1);
                g.setColor(Color.WHITE);
                g.drawRect(x + 1, y + 1, getIconWidth() - 3, getIconHeight() - 3);
                g.setColor(color);
                g.fillRect(x + 2, y + 2, getIconWidth() - 4, getIconHeight() - 4);
            }
        }

        @Override
        /**
         * @return Icon's width
         */
        public int getIconWidth() {
            return ICONWIDTH;
        }

        @Override
        /**
         * @return Icon's height 
         */
        public int getIconHeight() {
            return ICONHEIGHT;
        }
    }
    

    public ColorListCellRenderer() {
        icon = new ColorIcon();
        setIcon(icon);
    }

    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Color) {
            Color c = (Color) value;
            icon.setColor(c);
            setToolTipText(ColorUtil.toToolTipText(c));
        } else icon.setColor(null);
        setText("");
        setIcon(icon);
        return this;
    }
}
