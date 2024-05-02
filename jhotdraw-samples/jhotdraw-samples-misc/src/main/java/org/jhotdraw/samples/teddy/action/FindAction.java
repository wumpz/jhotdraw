/*
 * @(#)AbstractFindAction.java
 *
 * Copyright (c) 2005 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.teddy.action;

import java.awt.event.*;
import org.jhotdraw.action.edit.AbstractFindAction;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.View;
import org.jhotdraw.app.OSXApplication;
import org.jhotdraw.samples.teddy.FindDialog;

/** AbstractFindAction shows the find dialog. */
public class FindAction extends AbstractFindAction {

  private static final long serialVersionUID = 1L;
  public static final String ID = AbstractFindAction.ID;
  private FindDialog findDialog;

  public FindAction(Application app, View v) {
    super(app, v);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (findDialog == null) {
      findDialog = new FindDialog(getApplication());
      if (getApplication() instanceof OSXApplication) {
        findDialog.addWindowListener(new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent evt) {
            if (findDialog != null) {
              ((OSXApplication) getApplication()).removePalette(findDialog);
              findDialog.setVisible(false);
            }
          }
        });
      }
    }
    findDialog.setVisible(true);
    if (getApplication() instanceof OSXApplication) {
      ((OSXApplication) getApplication()).addPalette(findDialog);
    }
  }
}
