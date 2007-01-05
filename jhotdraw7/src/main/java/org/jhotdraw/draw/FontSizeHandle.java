/*
 * @(#)FontSizeHandle.java  2.0  2006-01-14
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

import org.jhotdraw.undo.CompositeEdit;

import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.util.ResourceBundleUtil;
/**
 * FontSizeHandle.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class FontSizeHandle extends LocatorHandle {
    private float oldSize;
    private CompositeEdit edit;
    
    /** Creates a new instance. */
    public FontSizeHandle(TextHolderFigure owner) {
        super(owner, new FontSizeLocator());
    }
    public FontSizeHandle(TextHolderFigure owner, Locator locator) {
        super(owner, locator);
    }
    
    /**
     * Draws this handle.
     */
    public void draw(Graphics2D g) {
        drawDiamond(g, Color.yellow, Color.black);
    }
    public Cursor getCursor() {
        return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
    }
    protected Rectangle basicGetBounds() {
        Rectangle r = new Rectangle(getLocation());
        r.grow(getHandlesize() / 2 + 1, getHandlesize() / 2 + 1);
        return r;
    }
    
    public void trackStart(Point anchor, int modifiersEx) {
        view.getDrawing().fireUndoableEditHappened(edit = new CompositeEdit("Schriftgr\u00f6sse"));
        TextHolderFigure textOwner = (TextHolderFigure) getOwner();
        oldSize = textOwner.getFontSize();
    }
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        TextHolderFigure textOwner = (TextHolderFigure) getOwner();
        
        float newSize = (float) Math.max(1, oldSize + view.viewToDrawing(new Point(0, lead.y - anchor.y)).y);
        textOwner.setFontSize(newSize);
    }
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        view.getDrawing().fireUndoableEditHappened(edit);
    }
    public String getToolTipText(Point p) {
        return ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels").getString("fontSizeHandle.tip");
    }
}
