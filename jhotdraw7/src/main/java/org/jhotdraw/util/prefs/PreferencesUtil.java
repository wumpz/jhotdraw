/*
 * @(#)PreferencesUtil.java  1.1  2008-09-11
 *
 * Copyright (c) 2005-2008 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.util.prefs;

import java.awt.*;
import java.awt.event.*;
import java.util.prefs.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * PreferencesUtil.
 *
 * @author Werner Randelshofer
 * @version 1.1 2008-09-11 Added method installTabbedPanePrefsHandler.
 * <br>1.0 October 13, 2005 Created.
 */
public class PreferencesUtil {

    public static void installPrefsHandler(Preferences prefs, String string, JTabbedPane tabbedPane) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** Creates a new instance. */
    private PreferencesUtil() {
    }

    /**
     * Installs a frame preferences handler.
     * On first run, sets the window to its preferred size at the top left
     * corner of the screen.
     * On subsequent runs, sets the window the last size and location where
     * the user had placed it before.
     *
     * @param prefs Preferences for storing/retrieving preferences values.
     * @param name Base name of the preference.
     * @param window The window for which to track preferences.
     */
    public static void installFramePrefsHandler(final Preferences prefs, final String name, Window window) {
        GraphicsConfiguration conf = window.getGraphicsConfiguration();
        Rectangle screenBounds = conf.getBounds();
        Insets screenInsets = window.getToolkit().getScreenInsets(conf);

        screenBounds.x += screenInsets.left;
        screenBounds.y += screenInsets.top;
        screenBounds.width -= screenInsets.left + screenInsets.right;
        screenBounds.height -= screenInsets.top + screenInsets.bottom;

        Dimension preferredSize = window.getPreferredSize();
        Dimension minSize = window.getMinimumSize();

        Rectangle bounds = new Rectangle(
                prefs.getInt(name + ".x", 0),
                prefs.getInt(name + ".y", 0),
                Math.max(minSize.width, prefs.getInt(name + ".width", preferredSize.width)),
                Math.max(minSize.height, prefs.getInt(name + ".height", preferredSize.height)));

        if (!screenBounds.contains(bounds)) {
            bounds.x = screenBounds.x + (screenBounds.width - bounds.width) / 2;
            bounds.y = screenBounds.y + (screenBounds.height - bounds.height) / 2;
            Rectangle.intersect(screenBounds, bounds, bounds);
        }
        window.setBounds(bounds);

        window.addComponentListener(new ComponentAdapter() {

            public void componentMoved(ComponentEvent evt) {
                prefs.putInt(name + ".x", evt.getComponent().getX());
                prefs.putInt(name + ".y", evt.getComponent().getY());
            }

            public void componentResized(ComponentEvent evt) {
                prefs.putInt(name + ".width", evt.getComponent().getWidth());
                prefs.putInt(name + ".height", evt.getComponent().getHeight());
            }
        });

    }

    /**
     * Installs a palette preferences handler.
     * On first run, sets the palette to its preferred location at the top left
     * corner of the screen.
     * On subsequent runs, sets the palette the last location where
     * the user had placed it before.
     *
     * @param prefs Preferences for storing/retrieving preferences values.
     * @param name Base name of the preference.
     * @param window The window for which to track preferences.
     */
    public static void installPalettePrefsHandler(final Preferences prefs, final String name, Window window) {
        installPalettePrefsHandler(prefs, name, window, 0);
    }

    public static void installPalettePrefsHandler(final Preferences prefs, final String name, Window window, int x) {
        GraphicsConfiguration conf = window.getGraphicsConfiguration();
        Rectangle screenBounds = conf.getBounds();
        Insets screenInsets = window.getToolkit().getScreenInsets(conf);

        screenBounds.x += screenInsets.left;
        screenBounds.y += screenInsets.top;
        screenBounds.width -= screenInsets.left + screenInsets.right;
        screenBounds.height -= screenInsets.top + screenInsets.bottom;

        Dimension preferredSize = window.getPreferredSize();

        Rectangle bounds = new Rectangle(
                prefs.getInt(name + ".x", x + screenBounds.x),
                prefs.getInt(name + ".y", 0 + screenBounds.y),
                preferredSize.width,
                preferredSize.height);

        if (!screenBounds.contains(bounds)) {
            bounds.x = screenBounds.x;
            bounds.y = screenBounds.y;
        }
        window.setBounds(bounds);

        window.addComponentListener(new ComponentAdapter() {

            public void componentMoved(ComponentEvent evt) {
                prefs.putInt(name + ".x", evt.getComponent().getX());
                prefs.putInt(name + ".y", evt.getComponent().getY());
            }
            /*
            public void componentResized(ComponentEvent evt) {
            prefs.putInt(name+".width", evt.getComponent().getWidth());
            prefs.putInt(name+".height", evt.getComponent().getHeight());
            }*/
        });

    }

    /**
     * Installs an intenal frame preferences handler.
     * On first run, sets the frame to its preferred size at the top left
     * corner of the desktop pane.
     * On subsequent runs, sets the frame the last size and location where
     * the user had placed it before.
     *
     * @param prefs Preferences for storing/retrieving preferences values.
     * @param name Base name of the preference.
     * @param window The window for which to track preferences.
     */
    public static void installInternalFramePrefsHandler(final Preferences prefs, final String name, JInternalFrame window, JDesktopPane desktop) {
        Rectangle screenBounds = desktop.getBounds();
        screenBounds.setLocation(0, 0);
        Insets screenInsets = desktop.getInsets();

        screenBounds.x += screenInsets.left;
        screenBounds.y += screenInsets.top;
        screenBounds.width -= screenInsets.left + screenInsets.right;
        screenBounds.height -= screenInsets.top + screenInsets.bottom;

        Dimension preferredSize = window.getPreferredSize();
        Dimension minSize = window.getMinimumSize();

        Rectangle bounds = new Rectangle(
                prefs.getInt(name + ".x", 0),
                prefs.getInt(name + ".y", 0),
                Math.max(minSize.width, prefs.getInt(name + ".width", preferredSize.width)),
                Math.max(minSize.height, prefs.getInt(name + ".height", preferredSize.height)));
        if (!screenBounds.contains(bounds)) {
            bounds.x = screenBounds.x + (screenBounds.width - bounds.width) / 2;
            bounds.y = screenBounds.y + (screenBounds.height - bounds.height) / 2;
            Rectangle.intersect(screenBounds, bounds, bounds);
        }
        window.setBounds(bounds);

        window.addComponentListener(new ComponentAdapter() {

            public void componentMoved(ComponentEvent evt) {
                prefs.putInt(name + ".x", evt.getComponent().getX());
                prefs.putInt(name + ".y", evt.getComponent().getY());
            }

            public void componentResized(ComponentEvent evt) {
                prefs.putInt(name + ".width", evt.getComponent().getWidth());
                prefs.putInt(name + ".height", evt.getComponent().getHeight());
            }
        });

    }

    /**
     * Installs a toolbar preferences handler.
     * On first run, sets the toolbar to BorderLayout.TOP.
     * On subsequent runs, set the toolbar to the last BorderLayout location.
     *
     * @param prefs Preferences for storing/retrieving preferences values.
     * @param name Base name of the preference.
     * @param toolbar The JToolBar for which to track preferences.
     */
    public static void installToolBarPrefsHandler(final Preferences prefs, final String name, JToolBar toolbar) {
        new ToolBarPrefsHandler(toolbar, name, prefs);

    }

    /**
     * Installs a JTabbedPane preferences handler.
     * On first run, sets the JTabbedPane to its preferred tab.
     *
     * @param prefs Preferences for storing/retrieving preferences values.
     * @param name Base name of the preference.
     * @param tabbedPane The JTabbedPane for which to track preferences.
     */
    public static void installTabbedPanePrefsHandler(final Preferences prefs, final String name, final JTabbedPane tabbedPane) {
        int selectedTab = prefs.getInt(name, 0);
        try {
            tabbedPane.setSelectedIndex(selectedTab);
        } catch (IndexOutOfBoundsException e) {
        }
        tabbedPane.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                prefs.putInt(name, tabbedPane.getSelectedIndex());
            }
        });
    }
}
