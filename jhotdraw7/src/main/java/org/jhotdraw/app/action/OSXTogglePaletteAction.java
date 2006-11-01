/*
 * @(#)OSXTogglePaletteAction.java  1.0  June 11, 2006
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

package org.jhotdraw.app.action;

import org.jhotdraw.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.beans.*;
import java.awt.*;
import org.jhotdraw.app.DefaultOSXApplication;

/**
 * OSXTogglePaletteAction.
 * 
 * @author Werner Randelshofer.
 * @version 1.0 June 11, 2006 Created.
 */
public class OSXTogglePaletteAction extends AbstractAction {
    private Window palette;
    private DefaultOSXApplication app;
    private WindowListener windowHandler;
    
    /** Creates a new instance. */
    public OSXTogglePaletteAction(DefaultOSXApplication app, Window palette, String label) {
        super(label);
        this.app = app;
        
        windowHandler = new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                putValue(Actions.SELECTED_KEY, false);
            }
        };
        
        setPalette(palette);
        putValue(Actions.SELECTED_KEY, true);
    }
    
    public void putValue(String key, Object newValue) {
        super.putValue(key, newValue);
        if (key == Actions.SELECTED_KEY) {
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
        }
    }
    
    public void setPalette(Window newValue) {
        if (palette != null) {
            palette.removeWindowListener(windowHandler);
        }
        
        palette = newValue;
        
        if (palette != null) {
            palette.addWindowListener(windowHandler);
            if (getValue(Actions.SELECTED_KEY) == Boolean.TRUE) {
                app.addPalette(palette);
                palette.setVisible(true);
            } else {
                app.removePalette(palette);
                palette.setVisible(false);
            }
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        if (palette != null) {
            putValue(Actions.SELECTED_KEY, ! palette.isVisible());
        }
    }
}
