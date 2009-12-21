/*
 * @(#)PasteAction.java
 *
 * Copyright (c) 1996-2009 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.app.action.edit;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.gui.datatransfer.ClipboardUtil;
import org.jhotdraw.util.*;

/**
 * Pastes the contents of the system clipboard at the caret position.
 * <p>
 * This action acts on the last {@link org.jhotdraw.app.EditableComponent} /
 * {@code JTextComponent} which had the focus when the {@code ActionEvent}
 * was generated.
 * <p>
 * This action is called when the user selects the Paste item in the Edit
 * menu. The menu item is automatically created by the application.
 * <p>
 * If you want this behavior in your application, you have to create an action
 * with this ID and put it in your {@code ApplicationModel} in method
 * {@link org.jhotdraw.app.ApplicationModel#initApplication}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PasteAction extends AbstractAction {

    public final static String ID = "edit.paste";

    /** Creates a new instance. */
    public PasteAction() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }

    public void actionPerformed(ActionEvent evt) {
        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().
                getPermanentFocusOwner();
        if (focusOwner != null && focusOwner instanceof JComponent) {
            JComponent component = (JComponent) focusOwner;
            Transferable t = ClipboardUtil.getClipboard().getContents(component);
            if (t != null && component.getTransferHandler() != null) {
                component.getTransferHandler().importData(
                        component,
                        t);
            }
        }
    }
}