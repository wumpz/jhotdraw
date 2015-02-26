/*
 * @(#)ZoomAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.draw.action;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.awt.Rectangle;
import javax.swing.*;
import org.jhotdraw.draw.*;

/**
 * ZoomAction.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class ZoomAction extends AbstractDrawingViewAction {
    private static final long serialVersionUID = 1L;
    private double scaleFactor;
    @Nullable private AbstractButton button;
    private String label;
    /**
     * Creates a new instance.
     */
    public ZoomAction(@Nullable DrawingEditor editor, double scaleFactor, @Nullable AbstractButton button) {
        this((DrawingView) null, scaleFactor, button);
        setEditor(editor);
    }
    /**
     * Creates a new instance.
     */
    public ZoomAction(@Nullable DrawingView view, double scaleFactor, @Nullable AbstractButton button) {
        super(view);
        this.scaleFactor = scaleFactor;
        this.button = button;
        label = (int) (scaleFactor * 100)+" %";
        putValue(Action.DEFAULT, label);
        putValue(Action.NAME, label);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (button != null) {
            button.setText(label);
        }
        final Rectangle vRect = getView().getComponent().getVisibleRect();
        final double oldFactor = getView().getScaleFactor();
        getView().setScaleFactor(scaleFactor);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (vRect != null) {
                    vRect.x = (int) (vRect.x / oldFactor * scaleFactor);
                    vRect.y = (int) (vRect.y / oldFactor * scaleFactor);
                    vRect.width = (int) (vRect.width / oldFactor * scaleFactor);
                    vRect.height = (int) (vRect.height / oldFactor * scaleFactor);
                    vRect.x += vRect.width / 3;
                    vRect.y += vRect.height / 3;
                    vRect.width /= 3;
                    vRect.height /= 3;
                    getView().getComponent().scrollRectToVisible(vRect);
                }
            }
        });
    }
    
}
