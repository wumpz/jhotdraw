/*
 * @(#)ToggleGridAction.java  2.0 2007-07-31
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.draw.action;

import org.jhotdraw.app.action.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.util.ResourceBundleUtil;
/**
 * Toggles the grid of the current view.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2007-07-31 Rewritten to act on a GridProject instead
 * of acting directly on DrawingView.
 * <br>1.2 2007-04-16 Added getOffConstrainer, getOnConstrainer methods.
 * <br>1.1 2006-04-21 Constructor with DrawingEditor paremeter added.
 * <br>1.0 January 16, 2006 Created.
 */
public class ToggleGridAction extends AbstractDrawingEditorAction {
    public final static String ID = "view.toggleGrid";
    /**
     * Creates a new instance.
     */
    public ToggleGridAction(DrawingEditor editor) {
        super(editor);
        ResourceBundleUtil labels =
                ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        labels.configureAction(this, ID);
        updateViewState();
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        DrawingView view = getView();
        if (view != null) {
            view.setConstrainerVisible(! view.isConstrainerVisible());
        }
    }
    
    @Override
    protected void updateViewState() {
        DrawingView view = getView();
        putValue(Actions.SELECTED_KEY, view != null && view.isConstrainerVisible());
    }
}
