/*
 * @(#)NewFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import org.jhotdraw.api.app.Application;

/**
 * Creates a new view.
 *
 * <p>This action is called when the user selects the New item in the File menu.
 *
 * <p>If you want this behavior in your application, you have to create it and put it in your {@code
 * ApplicationModel} in method {@link org.jhotdraw.app.ApplicationModel#initApplication}.
 *
 * <p>This action is designed for applications which do not automatically create a new view for each
 * opened file. This action goes together with {@link ClearFileAction}, {@link LoadFileAction} and
 * {@link CloseFileAction}. It should not be used together with {@link NewFileAction}.
 */
public class NewWindowAction extends NewFileAction {

  private static final long serialVersionUID = 1L;
  public static final String ID = "file.newWindow";

  public NewWindowAction(Application app) {
    super(app, ID);
  }
}
