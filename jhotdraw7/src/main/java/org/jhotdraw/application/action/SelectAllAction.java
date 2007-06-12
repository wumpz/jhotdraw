/*
 * @(#)SelectAllActioin.java  1.0  February 27, 2006
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

package org.jhotdraw.application.action;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.beans.*;
import java.util.*;
import org.jhotdraw.util.*;
import org.jhotdraw.application.EditableComponent;

/**
 * SelectAllAction.
 *
 * @author Werner Randelshofer.
 * @version 1.0 February 27, 2006 Created.
 */
public class SelectAllAction extends AbstractApplicationAction {
    public final static String ID = "Edit.selectAll";
    
    /** Creates a new instance. */
    public SelectAllAction() {
        initActionProperties(ID);
    }
    
    public void actionPerformed(ActionEvent evt) {
        Component focusOwner = KeyboardFocusManager.
                getCurrentKeyboardFocusManager().
                getPermanentFocusOwner();
        if (focusOwner != null) {
            if (focusOwner instanceof EditableComponent) {
                ((EditableComponent) focusOwner).selectAll();
            } else if (focusOwner instanceof JTextComponent) {
                ((JTextComponent) focusOwner).selectAll();
            } else {
                focusOwner.getToolkit().beep();
            }
        }
    }
}
