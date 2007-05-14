/*
 * @(#)SelectAreaTracker.java  3.0  2006-02-14
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


package org.jhotdraw.draw;

import java.awt.event.*;
import java.awt.*;
import java.util.*;
/**
 * <code>SelectAreaTracker</code> implements interactions with the background
 * area of a <code>Drawing</code>.
 * <p>
 * The <code>SelectAreaTracker</code> handles one of the three states of the 
 * <code>SelectionTool</code>. It comes into action, when the user presses
 * the mouse button over the background of a <code>Drawing</code>.
 *
 * @see SelectionTool
 *
 * @author Werner Randelshofer
 * @version 3.0 2006-02-15 Updated to handle multiple views.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class SelectAreaTracker extends AbstractTool {
    /**
     * The bounds of the rubberband. 
     */
    private Rectangle rubberband = new Rectangle();
    /**
     * Rubberband color. When this is null, the tracker does not
     * draw the rubberband.
     */
    private Color rubberbandColor = Color.BLACK;
    /**
     * Rubberband stroke.
     */
    private Stroke rubberbandStroke = new BasicStroke();
    
    
    /** Creates a new instance. */
    public SelectAreaTracker() {
    }
    
    
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        clearRubberBand();
    }
    public void mouseReleased(MouseEvent evt) {
        selectGroup(evt.isShiftDown());
        clearRubberBand();
        
    }
    public void mouseDragged(MouseEvent evt) {
        Rectangle invalidatedArea = (Rectangle) rubberband.clone();
        rubberband.setBounds(
        Math.min(anchor.x, evt.getX()),
        Math.min(anchor.y, evt.getY()),
        Math.abs(anchor.x - evt.getX()),
        Math.abs(anchor.y - evt.getY())
        );
        if (invalidatedArea.isEmpty()) {
            invalidatedArea = (Rectangle) rubberband.clone();
        } else {
            invalidatedArea = invalidatedArea.union(rubberband);
        }
        fireAreaInvalidated(invalidatedArea);
    }
    public void mouseMoved(MouseEvent evt) {
        //System.out.println("SelectAreaTracker mouseMoved "+evt.getX()+","+evt.getY());
        clearRubberBand();
        updateCursor(editor.findView((Container) evt.getSource()), new Point(evt.getX(), evt.getY()));
    }
    
    private void clearRubberBand() {
        if (! rubberband.isEmpty()) {
            fireAreaInvalidated(rubberband);
            rubberband.width = -1;
        }
    }
    
    public void draw(Graphics2D g) {
        g.setStroke(rubberbandStroke);
        g.setColor(rubberbandColor);
        g.drawRect(rubberband.x, rubberband.y, rubberband.width - 1, rubberband.height - 1);
    }
    
    private void selectGroup(boolean toggle) {
        getView().addToSelection(getView().findFiguresWithin(rubberband));
    }
}
