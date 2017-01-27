/*
 * @(#)ActionUtil.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.app.action;

import javax.swing.*;

/**
 * Provides constants and static operations on <code>Action</code> objects.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ActionUtil {
    /**
     * Key for the selected state of an action.
     * The value must be a Boolean.
     */
    public static final String SELECTED_KEY = Action.SELECTED_KEY;
    /**
     * All actions with equal value are put into the
     * same submenu.
     * The value must be a String or an array of Strings.
     * Each element of the array represents a menu.
     */
    public static final String SUBMENU_KEY = "submenu";
    /**
     * All actions with equal value are created as
     * a radio button and put into the same group.
     * The value must be an object.
     */
    public static final String BUTTON_GROUP_KEY = "buttonGroup";
    /**
     * UndoableEdit presentation name key.
     *
     * @see javax.swing.undo.UndoableEdit#getPresentationName
     */
    public static final String UNDO_PRESENTATION_NAME_KEY = "undoPresentationName";
    
    /** Prevent instance creation. */
    private ActionUtil() {
    }
    
    /**
     * Configures a JCheckBoxMenuItem for an Action.
     */
    public static void configureJCheckBoxMenuItem(final JCheckBoxMenuItem mi, final Action a) {
        /*mi.setSelected((Boolean) a.getValue(ActionUtil.SELECTED_KEY));
        PropertyChangeListener propertyHandler = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ActionUtil.SELECTED_KEY)) {
                    mi.setSelected((Boolean) a.getValue(ActionUtil.SELECTED_KEY));
                }
            }
        };
        a.addPropertyChangeListener(propertyHandler);
        mi.putClientProperty("actionPropertyHandler", propertyHandler);
        */
        mi.setAction(a);
    }
    
    /**
     * Unconfigures a JCheckBoxMenuItem for an Action.
     */
    public static void unconfigureJCheckBoxMenuItem(JCheckBoxMenuItem mi, Action a) {
        /*PropertyChangeListener propertyHandler = (PropertyChangeListener) mi.getClientProperty("actionPropertyHandler");
        if (propertyHandler != null) {
            a.removePropertyChangeListener(propertyHandler);
        mi.putClientProperty("actionPropertyHandler", null);
        }*/
        mi.setAction(null);
    }
}