/*
 * @(#)ColorIcon.java  1.1  2006-02-26
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.draw.action;

import java.awt.*;
import javax.swing.*;
/**
 * ColorIcon.
 *
 * @author  Werner Randelshofer
 * @version 1.1 2006-02-26 Draw lines instead of draw rect.
 * <br>1.0 25. November 2003  Created.
 */
public class ColorIcon implements javax.swing.Icon {
    private Color fillColor;
    private Color borderColor;
    
    /** Creates a new instance. */
    public ColorIcon(Color fillColor) {
        this.fillColor = fillColor;
        borderColor = (fillColor == null) 
        ? new Color(0, 0, 0, 38) 
        : Colors.shadow(fillColor, 38)
        ;
    }
    
    public int getIconHeight() {
        return 10;
    }
    
    public int getIconWidth() {
        return 14;
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
