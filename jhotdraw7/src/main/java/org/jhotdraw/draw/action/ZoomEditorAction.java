/*
 * @(#)ZoomEditorAction.java  1.0  January 16, 2006
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

import org.jhotdraw.draw.DrawingEditor;

import org.jhotdraw.draw.DrawingView;

import javax.swing.*;
import javax.swing.undo.*;
/**
 * Zooms either the current view or all views of a DrawingEditor.
 *
 * @author  Werner Randelshofer
 * @version 1.0 January 16, 2006 Created.
 */
public class ZoomEditorAction extends AbstractEditorAction {
    public final static String ID = "zoomEditor";
    
    private double scaleFactor;
    private AbstractButton button;
    private String label;
    private boolean updateAllViews;
    /**
     * Creates a new instance.
     */
    public ZoomEditorAction(DrawingEditor editor, double scaleFactor, AbstractButton button) {
        this(editor, scaleFactor, button, true);
        
    }
    /**
     * Creates a new instance.
     */
    public ZoomEditorAction(DrawingEditor editor, double scaleFactor, AbstractButton button, boolean updateAllViews) {
        super(editor);
        this.scaleFactor = scaleFactor;
        this.button = button;
        this.updateAllViews = updateAllViews;
        label = (int) (scaleFactor * 100)+" %";
        putValue(Action.DEFAULT, label);
        putValue(Action.NAME, label);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (button != null) {
            button.setText(label);
        }
        if (updateAllViews) {
        for (DrawingView v : getEditor().getDrawingViews()) {
            v.setScaleFactor(scaleFactor);
        }} else {
            getView().setScaleFactor(scaleFactor);
        }
    }
}
