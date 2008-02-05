/*
 * @(#)BorderRectangle2D.DoubleFigure.java  1.0  8. April 2004
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

package org.jhotdraw.draw;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
/**
 * BorderRectangle2D.DoubleFigure.
 *
 * @deprecated This class should be in one of the samples package
 *
 * @author  Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 8. April 2004  Created.
 */
public class BorderRectangleFigure extends RectangleFigure {
    protected Border border;
    protected final static JComponent borderComponent = new JPanel();
    
    /** Creates a new instance. */
    public BorderRectangleFigure(Border border) {
        this.border = border;
    }
    
    public void drawFigure(Graphics2D g) {
        Rectangle bounds = getBounds().getBounds();
        border.paintBorder(borderComponent, g, bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
