/*
 * @(#)PasteAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.action.edit;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.datatransfer.ClipboardUtil;
import org.jhotdraw.util.*;

/**
 * Pastes the contents of the system clipboard at the caret position.
 *
 * <p>This action acts on the last {@link org.jhotdraw.gui.EditableComponent} / {@code
 * JTextComponent} which had the focus when the {@code ActionEvent} was generated.
 *
 * <p>This action is called when the user selects the Paste item in the Edit menu. The menu item is
 * automatically created by the application.
 *
 * <p>If you want this behavior in your application, you have to create an action with this ID and
 * put it in your {@code ApplicationModel} in method {@link
 * org.jhotdraw.app.ApplicationModel#initApplication}.
 */
public class PasteAction extends AbstractSelectionAction {

  private static final long serialVersionUID = 1L;
  public static final String ID = "edit.paste";

  /** Creates a new instance which acts on the currently focused component. */
  public PasteAction() {
    this(null);
  }

  /**
   * Creates a new instance which acts on the specified component.
   *
   * @param target The target of the action. Specify null for the currently focused component.
   */
  public PasteAction(JComponent target) {
    super(target);
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.action.Labels");
    labels.configureAction(this, ID);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    JComponent c = target;
    if (c == null
        && (KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner()
            instanceof JComponent)) {
      c = (JComponent)
          KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
    }
    if (c != null && c.isEnabled()) {
      Transferable t = ClipboardUtil.getClipboard().getContents(c);
      if (t != null && c.getTransferHandler() != null) {
        c.getTransferHandler().importData(c, t);
      }
    }
  }

  @Override
  protected void updateEnabled() {
    if (target != null) {
      setEnabled(target.isEnabled());
    }
  }
}
