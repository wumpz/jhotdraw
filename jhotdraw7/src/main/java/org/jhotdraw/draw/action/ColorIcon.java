/*
 * @(#)ColorIcon.java  2.0  2007-06-26
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.draw.action;

import java.awt.*;
import javax.swing.*;
/**
 * ColorIcon.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2007-06-26 Added support for name and size. 
 * <br>1.1 2006-02-26 Draw lines instead of draw rect.
 * <br>1.0 25. November 2003  Created.
 */
public class ColorIcon implements javax.swing.Icon {
    private Color fillColor;
    private Color borderColor;
    private int width;
    private int height;
    private String name;
    
    /** Creates a new instance. */
    public ColorIcon(int rgb) {
        this(new Color(rgb), Integer.toHexString(0xff000000 | rgb).substring(2), 14, 10);
    }
    public ColorIcon(Color color) {
        this(color, Integer.toHexString(color.getRGB()), 14, 10);
    }
    public ColorIcon(int rgb, String name) {
        this(new Color(rgb), name, 14, 10);
    }
    public ColorIcon(Color color, String name) {
        this(color, name, 14, 10);
    }
    public ColorIcon(Color color, String name, int width, int height) {
        this.fillColor = color;
        borderColor = (color == null) 
        ? new Color(0, 0, 0, 38) 
        : Colors.shadow(color, 38)
        ;
        this.name = name;
        this.width = width;
        this.height = height;
    }
    
    public Color getColor() {
        return fillColor;
    }
    
    public String getName() {
        return name;
    }
    
    public int getIconWidth() {
        return width;
    }
    
    public int getIconHeight() {
        return height;
    }
    
    public void paintIcon(Component c, Graphics g, int x, int y) {
        //Graphics2D g = (Graphics2D) gr;
        if (fillColor == null) {
            g.setColor(Color.red);
            g.drawLine(x, y + getIconHeight() - 1, x + getIconWidth() - 1, y);
        } else {
            g.setColor(fillColor);
            g.fillRect(x, y, getIconWidth() - 1, getIconHeight() - 1);
        }
        g.setColor(borderColor);
        
        // Draw the rectangle using drawLine to work around a drawing bug in
        // Apples MRJ for Java 1.5
       // g.drawRect(x, y, getIconWidth() - 1, getIconHeight() - 1);
            g.drawLine(x, y, x + getIconWidth() - 1, y);
            g.drawLine(x + getIconWidth() - 1, y, x + getIconWidth() - 1, y + getIconHeight() - 1);
             g.drawLine(x + getIconWidth() - 1, y + getIconHeight() - 1, x, y + getIconHeight() - 1);
             g.drawLine(x, y + getIconHeight() - 1, x, y);
   }
}
