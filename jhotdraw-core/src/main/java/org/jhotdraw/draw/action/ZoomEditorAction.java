/*
 * @(#)ZoomEditorAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.draw.action;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import javax.swing.*;

/**
 * Zooms either the current view or all views of a DrawingEditor.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class ZoomEditorAction extends AbstractDrawingEditorAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "zoomEditor";
    
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
