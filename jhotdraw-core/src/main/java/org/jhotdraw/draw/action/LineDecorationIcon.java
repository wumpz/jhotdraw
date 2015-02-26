/*
 * @(#)LineDecorationIcon.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.draw.action;

import org.jhotdraw.draw.decoration.LineDecoration;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import org.jhotdraw.draw.*;

import static org.jhotdraw.draw.AttributeKeys.*;
/**
 * LineDecorationIcon.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class LineDecorationIcon implements Icon {
    private LineFigure lineFigure;
    
    /** Creates a new instance. */
    public LineDecorationIcon(LineDecoration decoration, boolean isStartDecoration) {
        lineFigure = new LineFigure();
        lineFigure.setBounds(new Point2D.Double(2,8),new Point2D.Double(23,8));
        if (isStartDecoration) {
            lineFigure.set(START_DECORATION,  decoration);
        } else {
            lineFigure.set(END_DECORATION,  decoration);
        }
        lineFigure.set(STROKE_COLOR,  Color.black);
    }
    
    @Override
    public int getIconHeight() {
        return 16;
    }
    
    @Override
    public int getIconWidth() {
        return 25;
    }
    
    @Override
    public void paintIcon(java.awt.Component c, java.awt.Graphics gr, int x, int y) {
        Graphics2D g = (Graphics2D) gr;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        lineFigure.draw(g);
    }
}
