/*
 * @(#)LineDecorationIcon.java  2.0  2006-01-15
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
import java.awt.geom.*;
import javax.swing.*;
import org.jhotdraw.draw.*;

import static org.jhotdraw.draw.AttributeKeys.*;
/**
 * LineDecorationIcon.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2006-01-15 Changed to support double precision coordinates.
 * <br>1.0 26. November 2003  Created.
 */
public class LineDecorationIcon implements Icon {
    private LineFigure lineFigure;
    
    /** Creates a new instance. */
    public LineDecorationIcon(LineDecoration decoration, boolean isStartDecoration) {
        lineFigure = new LineFigure();
        lineFigure.setBounds(new Point2D.Double(2,8),new Point2D.Double(23,8));
        if (isStartDecoration) {
            START_DECORATION.set(lineFigure, decoration);
        } else {
            END_DECORATION.set(lineFigure, decoration);
        }
        STROKE_COLOR.set(lineFigure, Color.black);
    }
    
    public int getIconHeight() {
        return 16;
    }
    
    public int getIconWidth() {
        return 25;
    }
    
    public void paintIcon(java.awt.Component c, java.awt.Graphics gr, int x, int y) {
        Graphics2D g = (Graphics2D) gr;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        lineFigure.draw(g);
    }
}
