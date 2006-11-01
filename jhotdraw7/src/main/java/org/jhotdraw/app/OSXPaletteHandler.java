/*
 * @(#)FloatingPaletteHandler.java  1.1  2006-06-11
 *
 * Copyright (c) 2005-2006 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.app;

import java.awt.*;
import java.awt.event.*;
import java.util.prefs.*;
import javax.swing.*;
import java.util.*;
/**
 * Hides all registered floating palettes, if none of the registered project
 * windows has focus anymore.
 *
 * @author Werner Randelshofer
 * @version 1.1 2006-06-11 Palettes can now be any subclass of java.awt.Window.
 * <br>1.0 October 9, 2005 Created.
 */
public class OSXPaletteHandler {
    private HashSet<Window> palettes = new HashSet<Window>();
    private HashMap<Window,Project> windows = new HashMap<Window,Project>();
    private static OSXPaletteHandler instance;
    private javax.swing.Timer timer;
    private DefaultOSXApplication app;
    private WindowFocusListener focusHandler = new WindowFocusListener() {
        /**
         * Invoked when the Window is set to be the focused Window, which means
         * that the Window, or one of its subcomponents, will receive keyboard
         * events.
         */
        public void windowGainedFocus(WindowEvent e) {
            timer.stop();
            if (windows.containsKey(e.getWindow())) {
                app.setCurrentProject((Project) windows.get(e.getWindow()));
                showPalettes();
            }
        }
        
        /**
         * Invoked when the Window is no longer the focused Window, which means
         * that keyboard events will no longer be delivered to the Window or any of
         * its subcomponents.
         */
        public void windowLostFocus(WindowEvent e) {
            timer.restart();
        }
    };
    
    /** Creates a new instance. */
    public OSXPaletteHandler(DefaultOSXApplication app) {
        this.app = app;
        timer = new javax.swing.Timer(60, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                maybeHidePalettes();
            }
        });
        timer.setRepeats(false);
    }
    
    public void add(Window window, Project project) {
        window.addWindowFocusListener(focusHandler);
        windows.put(window, project);
    }
    
    public void remove(Window window, Project project) {
        windows.remove(window);
        window.removeWindowFocusListener(focusHandler);
    }
    
    public void addPalette(Window palette) {
        palette.addWindowFocusListener(focusHandler);
        palettes.add(palette);
    }
    
    public void removePalette(Window palette) {
        palettes.remove(palette);
        palette.removeWindowFocusListener(focusHandler);
    }
    
    public Set<Window> getPalettes() {
        return Collections.unmodifiableSet(palettes);
    }
    
    private void showPalettes() {
        for (Window palette : palettes) {
            if (! palette.isVisible()) {
                palette.setVisible(true);
            }
        }
    }
    
    private boolean isFocused(Window w) {
        if (w.isFocused()) return true;
        Window[] ownedWindows = w.getOwnedWindows();
        for (int i=0; i < ownedWindows.length; i++) {
            if (isFocused(ownedWindows[i])) {
                return true;
            }
        }
        return false;
    }
    private void maybeHidePalettes() {
        boolean hasFocus = false;
        for (Window window : windows.keySet()) {
            if (isFocused(window)) {
                hasFocus = true;
                break;
            }
        }
        if (! hasFocus && windows.size() > 0) {
            for (Window palette : palettes) {
                if (isFocused(palette)) {
                    hasFocus = true;
                    break;
                }
            }
        }
        if (! hasFocus) {
            for (Window palette : palettes) {
                palette.setVisible(false);
            }
        }
    }
}
