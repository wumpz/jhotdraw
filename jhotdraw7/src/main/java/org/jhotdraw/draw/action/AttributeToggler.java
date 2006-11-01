/*
 * @(#)AttributeToggler.java  4.0  2006-06-07
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.util.*;
import org.jhotdraw.draw.*;
/**
 * AttributeToggler toggles an attribute of the selected figures between two
 * different values.
 * If the name of a compatible JTextComponent action is specified, the toggler
 * checks if the current permant focus owner is a JTextComponent, and if it is,
 * it will apply the text action to the JTextComponent.
 *
 * @author  Werner Randelshofer
 * @version 4.0 2006-06-07 Reworked.
 * <br>3.0 2006-02-27 Support for compatible text action added.
 * <br>2.0 2006-02-27 Toggle attributes regardles from action state.
 * <br>1.0 27. November 2003  Created.
 */
public class AttributeToggler implements ActionListener {
    private DrawingEditor editor;
    private AttributeKey key;
    private Object value1;
    private Object value2;
    private Action compatibleTextAction;
    
    /** Creates a new instance. */
    public AttributeToggler(DrawingEditor editor, AttributeKey key, Object value1, Object value2) {
        this(editor, key, value1, value2, null);
    }
    public AttributeToggler(DrawingEditor editor, AttributeKey key, Object value1, Object value2, Action compatibleTextAction) {
        this.editor = editor;
        this.key = key;
        this.value1 = value1;
        this.value2 = value2;
        this.compatibleTextAction = compatibleTextAction;
    }
    
    public DrawingView getView() {
        return editor.getView();
    }
    public DrawingEditor getEditor() {
        return editor;
    }
    
    public void actionPerformed(ActionEvent evt) {
        if (compatibleTextAction != null) {
            Component focusOwner = KeyboardFocusManager.
                    getCurrentKeyboardFocusManager().
                    getPermanentFocusOwner();
            if (focusOwner != null && focusOwner instanceof JTextComponent) {
                compatibleTextAction.actionPerformed(evt);
                return;
            }
        }
        
        Iterator i = getView().getSelectedFigures().iterator();
        Object newValue = null;
        if (i.hasNext()) {
            Figure f = (Figure) i.next();
            Object attr = f.getAttribute(key);
            if (value1 == null && attr == null ||
                    (value1 != null && attr != null && attr.equals(value1))) {
                newValue = value2;
            } else {
                newValue = value1;
            }
            getEditor().setDefaultAttribute(key, newValue);
            f.setAttribute(key, newValue);
        }
        while (i.hasNext()) {
            Figure f = (Figure) i.next();
            f.setAttribute(key, newValue);
        }
    }
}
