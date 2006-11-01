/*
 * @(#)Actions.java  2.0  2006-02-13
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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.beans.*;
import java.util.*;
/**
 * Provides constants and static operations on <code>Action</code> objects.
 *
 * @author Werner Randelshofer
 * @version  2.0 2006-02-13 Merged from org.jhotdraw.draw.app.Actions.
 * <br>1.0 7. Februar 2006 Created.
 */
public class Actions {
    /**
     * Key for the selected state of an action.
     * The value must be a Boolean.
     */
    public final static String SELECTED_KEY = "selected";
    /**
     * All actions with equal value are put into the
     * same submenu.
     * The value must be a String or an array of Strings.
     * Each element of the array represents a menu.
     */
    public final static String SUBMENU_KEY = "submenu";
    /**
     * All actions with equal value are created as
     * a radio button and put into the same group.
     * The value must be an object.
     */
    public final static String BUTTON_GROUP_KEY = "buttonGroup";
    
    /** Prevent instance creation. */
    private Actions() {
    }
    
    /**
     * Configures a JCheckBoxMenuItem for an Action.
     */
    public static void configureJCheckBoxMenuItem(final JCheckBoxMenuItem mi, final Action a) {
        mi.setSelected((Boolean) a.getValue(Actions.SELECTED_KEY));
        PropertyChangeListener propertyHandler = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(Actions.SELECTED_KEY)) {
                    mi.setSelected((Boolean) a.getValue(Actions.SELECTED_KEY));
                }
            }
        };
        a.addPropertyChangeListener(propertyHandler);
        mi.putClientProperty("actionPropertyHandler", propertyHandler);
    }
    
    /**
     * Unconfigures a JCheckBoxMenuItem for an Action.
     */
    public static void unconfigureJCheckBoxMenuItem(JCheckBoxMenuItem mi, Action a) {
        PropertyChangeListener propertyHandler = (PropertyChangeListener) mi.getClientProperty("actionPropertyHandler");
        if (propertyHandler != null) {
            a.removePropertyChangeListener(propertyHandler);
        mi.putClientProperty("actionPropertyHandler", null);
        }
    }
}