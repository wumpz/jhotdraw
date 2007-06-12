/*
 * @(#)ToggleVisibleAction.java  1.0  June 17, 2006
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * Toggles the visible state of a Component.
 * Is selected, when the Component is visible.
 *
 * @author Werner Randelshofer.
 * @version 1.0 June 17, 2006 Created.
 */
public class ToggleVisibleAction extends AbstractAction {
    private Component component;
    
    /** Creates a new instance. */
    public ToggleVisibleAction(Component c, String name) {
        this.component = c;
        putValue(Action.NAME, name);
        putValue(Actions.SELECTED_KEY, c.isVisible());
        c.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                putValue(Actions.SELECTED_KEY, component.isVisible());
            }
            
            public void componentHidden(ComponentEvent e) {
                putValue(Actions.SELECTED_KEY, component.isVisible());
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        component.setVisible(! component.isVisible());
    }
}
