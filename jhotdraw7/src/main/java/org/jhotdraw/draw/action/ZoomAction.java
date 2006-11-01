/*
 * @(#)ZoomAction.java  1.1 2006-04-21
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

import javax.swing.*;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
/**
 * ZoomAction.
 *
 * @author  Werner Randelshofer
 * @version 1.1 2006-04-21 Constructor with DrawingEditor paremeter added.
 * <br>1.0 January 16, 2006 Created.
 */
public class ZoomAction extends AbstractViewAction {
    private double scaleFactor;
    private AbstractButton button;
    private String label;
    /**
     * Creates a new instance.
     */
    public ZoomAction(DrawingEditor editor, double scaleFactor, AbstractButton button) {
        this((DrawingView) null, scaleFactor, button);
        setEditor(editor);
    }
    /**
     * Creates a new instance.
     */
    public ZoomAction(DrawingView view, double scaleFactor, AbstractButton button) {
        super(view);
        this.scaleFactor = scaleFactor;
        this.button = button;
        label = (int) (scaleFactor * 100)+" %";
        putValue(Action.DEFAULT, label);
        putValue(Action.NAME, label);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (button != null) {
            button.setText(label);
        }
        getView().setScaleFactor(scaleFactor);
    }
    
}
