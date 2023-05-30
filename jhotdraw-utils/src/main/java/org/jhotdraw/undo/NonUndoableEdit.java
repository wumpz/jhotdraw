/*
 * @(#)NonUndoableEdit.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.undo;

import javax.swing.undo.*;

/** NonUndoableEdit. */
public class NonUndoableEdit extends AbstractUndoableEdit {

  private static final long serialVersionUID = 1L;

  public NonUndoableEdit() {}

  @Override
  public boolean canUndo() {
    return false;
  }

  @Override
  public boolean canRedo() {
    return false;
  }
}
