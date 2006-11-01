/*
 * @(#)ColorAttributeIcon.java  2.0  2006-06-07
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
import java.awt.color.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.net.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.geom.*;
/**
 * ColorAttributeIcon.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2006-06-07 Reworked.
 * <br>1.0 25. November 2003  Created.
 */
public class ColorAttributeIcon extends javax.swing.ImageIcon {
    private DrawingEditor editor;
    AttributeKey<Color> key;
    //private Rectangle colorRect = new Rectangle(0, 12, 16, 4);
    private Rectangle colorRect = new Rectangle(1, 17, 20, 4);
    
    /** Creates a new instance. */
    public ColorAttributeIcon(DrawingEditor editor, AttributeKey<Color> key, URL imageLocation) {
        super(imageLocation);
        this.editor = editor;
        this.key = key;
    }
    public ColorAttributeIcon(DrawingEditor editor, AttributeKey<Color> key, Image image) {
        super(image);
        this.editor = editor;
        this.key = key;
    }
    
    public void paintIcon(java.awt.Component c, java.awt.Graphics gr, int x, int y) {
        Graphics2D g = (Graphics2D) gr;
        super.paintIcon(c, g, x, y);
        Color color = (Color) editor.getDefaultAttribute(key);
        if (color == null) {
           // g.setColor(Color.red);
           // g.drawLine(x, y + 12 + 3, x + 15, y + 13);
        } else {
            g.setColor(color);
            g.translate(x, y);
            g.fill(colorRect);
            g.translate(-x, -y);
        }
    }    
}
