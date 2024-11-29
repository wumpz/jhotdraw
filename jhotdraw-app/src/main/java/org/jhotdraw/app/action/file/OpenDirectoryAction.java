/*
 * @(#)OpenDirectoryAction.java
 *
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.View;
import org.jhotdraw.api.gui.URIChooser;
import org.jhotdraw.utils.util.ResourceBundleUtil;

/**
 * Presents an {@code URIChooser} for selecting a directory and loads the selected URI into an empty
 * view. If no empty view is available, a new view is created.
 *
 * <p>This action is called when the user selects the Open Directory item in the File menu. The menu
 * item is automatically created by the application.
 *
 * <p>If you want this behavior in your application, you have to create it and put it in your {@code
 * ApplicationModel} in method {@link ApplicationModel#initApplication}.
 *
 * <p>This action is designed for applications which automatically create a new view for each opened
 * file. This action goes together with {@link NewFileAction}, {@link OpenFileAction} and {@link
 * CloseFileAction}. This action should not be used together with {@link LoadFileAction}.
 */
public class OpenDirectoryAction extends OpenFileAction {

  private static final long serialVersionUID = 1L;
  public static final String ID = "file.openDirectory";

  public OpenDirectoryAction(Application app) {
    super(app);
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
    labels.configureAction(this, ID);
  }

  @Override
  protected URIChooser getChooser(View view) {
    return getApplication().getModel().createOpenDirectoryChooser(getApplication(), view);
  }
}
