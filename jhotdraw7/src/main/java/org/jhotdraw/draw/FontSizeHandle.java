/*
 * @(#)FontSizeHandle.java  3.0  2007-04-14
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
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

import java.util.Locale;
import javax.swing.undo.*;
import java.awt.*;
import java.awt.geom.*;
import org.jhotdraw.util.ResourceBundleUtil;
import static org.jhotdraw.draw.AttributeKeys.*;
/**
 * FontSizeHandle.
 *
 * @author Werner Randelshofer
 * @version 3.0 2007-04-14 Changed to support AttributeKeys.TRANSFORM.
 * <br>2.0 2006-01-14 Changed to support double precison coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class FontSizeHandle extends LocatorHandle {
    private float oldSize;
    private float newSize;
    private Object restoreData;
    
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
        TextHolderFigure textOwner = (TextHolderFigure) getOwner();
        oldSize = newSize = textOwner.getFontSize();
        restoreData = textOwner.getAttributesRestoreData();
    }
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        TextHolderFigure textOwner = (TextHolderFigure) getOwner();
        
        Point2D.Double anchor2D = view.viewToDrawing(anchor);
        Point2D.Double lead2D = view.viewToDrawing(lead);
        if (TRANSFORM.get(textOwner) != null) {
            try {
                TRANSFORM.get(textOwner).inverseTransform(anchor2D, anchor2D);
                TRANSFORM.get(textOwner).inverseTransform(lead2D, lead2D);
            } catch (NoninvertibleTransformException ex) {
                ex.printStackTrace();
            }
        }
        newSize = (float) Math.max(1, oldSize + lead2D.y - anchor2D.y);
        textOwner.willChange();
        textOwner.setFontSize(newSize);
        textOwner.changed();
    }
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        final TextHolderFigure textOwner = (TextHolderFigure) getOwner();
        final Object editRestoreData = restoreData;
        final float editNewSize = newSize;
        UndoableEdit edit = new AbstractUndoableEdit() {
            public String getPresentationName() {
                ResourceBundleUtil labels =
                        ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
                return labels.getString("attributeFontSize");
            }
            public void undo() {
                super.undo();
                textOwner.willChange();
                textOwner.restoreAttributesTo(editRestoreData);
                textOwner.changed();
            }
            public void redo() {
                super.redo();
                textOwner.willChange();
                textOwner.setFontSize(newSize);
                textOwner.changed();
            }
        };
        fireUndoableEditHappened(edit);
    }
    public String getToolTipText(Point p) {
        return ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels").getString("fontSizeHandle.tip");
    }
}
