/*
 * @(#)CutAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.action.edit;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.datatransfer.ClipboardUtil;
import org.jhotdraw.util.*;

/**
 * Cuts the selected region and places its contents into the system clipboard.
 * <p>
 * This action acts on the last {@link org.jhotdraw.gui.EditableComponent} /
 * {@code JTextComponent} which had the focus when the {@code ActionEvent}
 * was generated.
 * <p>
 * This action is called when the user selects the Cut item in the Edit
 * menu. The menu item is automatically created by the application.
 * <p>
 * If you want this behavior in your application, you have to create an action
 * with this ID and put it in your {@code ApplicationModel} in method
 * {@link org.jhotdraw.app.ApplicationModel#initApplication}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CutAction extends AbstractCopyCutAction {

    public static final String ID = "edit.cut";

    /**
     * Creates a new instance which acts on the currently focused component.
     */
    public CutAction() {
        super(null);
    }

    /**
     * Creates a new instance which acts on the specified component.
     *
     * @param target The target of the action. Specify null for the currently
     * focused component.
     */
    public CutAction(JComponent target) {
        super(target, ID);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        super.actionPerformed(evt,TransferHandler.MOVE);
    }
}
