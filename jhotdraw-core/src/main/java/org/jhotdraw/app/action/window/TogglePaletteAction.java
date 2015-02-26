/*
 * @(#)TogglePaletteAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.app.action.window;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import org.jhotdraw.app.OSXApplication;
import org.jhotdraw.app.action.ActionUtil;

/**
 * TogglePaletteAction.
 * 
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class TogglePaletteAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
    private Window palette;
    private OSXApplication app;
    private WindowListener windowHandler;
    
    /** Creates a new instance. */
    public TogglePaletteAction(OSXApplication app, Window palette, String label) {
        super(label);
        this.app = app;
        
        windowHandler = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                putValue(ActionUtil.SELECTED_KEY, false);
            }
        };
        
        putValue(ActionUtil.SELECTED_KEY, false);
        setPalette(palette);
    }
    
    @Override
    public void putValue(String key, Object newValue) {
        super.putValue(key, newValue);
        /*
        if (key == ActionUtil.SELECTED_KEY) {
            if (palette != null) {
                boolean b = (Boolean) newValue;
                if (b) {
                    app.addPalette(palette);
                    palette.setVisible(true);
                } else {
                    app.removePalette(palette);
                    palette.setVisible(false);
                }
            }
        }*/
    }
    
    public void setPalette(Window newValue) {
        if (palette != null) {
            palette.removeWindowListener(windowHandler);
        }
        
        palette = newValue;
        
        if (palette != null) {
            palette.addWindowListener(windowHandler);
            if (getValue(ActionUtil.SELECTED_KEY) == Boolean.TRUE) {
                app.addPalette(palette);
                palette.setVisible(true);
            } else {
                app.removePalette(palette);
                palette.setVisible(false);
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (palette != null) {
           // putValue(ActionUtil.SELECTED_KEY, ! palette.isVisible());
            boolean b = (Boolean) getValue(ActionUtil.SELECTED_KEY);
                if (b) {
                    app.addPalette(palette);
                    palette.setVisible(true);
                } else {
                    app.removePalette(palette);
                    palette.setVisible(false);
                }
        }
    }
}
