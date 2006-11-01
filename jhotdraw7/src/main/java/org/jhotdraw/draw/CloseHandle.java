/*
 * @(#)CloseHandle.java  2.0  2006-01-14
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

import java.awt.*;
import java.awt.geom.*;
/**
 * CloseHandle.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 31. M�rz 2004  Created.
 */
public class CloseHandle extends LocatorHandle {
    private boolean pressed;
    
    /** Creates a new instance. */
    public CloseHandle(Figure owner) {
        super(owner, new RelativeLocator(1.0, 0.0));
    }
    
    protected int getHandlesize() {
        return 9;
    }
    
    /**
     * Draws this handle.
     */
    public void draw(Graphics2D g) {
        drawRectangle(g, (pressed) ? Color.orange : Color.white, Color.black);
        Rectangle r = getBounds();
        g.drawLine(r.x, r.y, r.x+r.width, r.y+r.height);
        g.drawLine(r.x+r.width, r.y, r.x, r.y+r.height);
    }
    
    
    /**
     * Returns a cursor for the handle.
     */
    public Cursor getCursor() {
        return Cursor.getDefaultCursor();
    }
    
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        pressed = basicGetBounds().contains(lead);
        if (pressed) getOwner().requestRemove();
        fireAreaInvalidated(getDrawBounds());
    }
    
    public void trackStart(Point anchor, int modifiersEx) {
        pressed = true;
        fireAreaInvalidated(getDrawBounds());
    }
    
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        boolean oldValue = pressed;
        pressed = basicGetBounds().contains(lead);
        if (oldValue != pressed) fireAreaInvalidated(getDrawBounds());
    }
}
