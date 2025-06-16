/*
 * @(#)LoadDirectoryAction.java
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
 * Les the user save unsaved changes of the active view, then presents an {@code URIChooser} and
 * then loads the selected URI into the active view.
 *
 * <p>This action is called when the user selects the Load Directory item in the File menu. The menu
 * item is automatically created by the application.
 *
 * <p>This action is designed for applications which do not automatically create a new view for each
 * opened file. This action goes together with {@code ClearFileAction}, {@code NewWindowAction},
 * {@code LoadFileAction}, {@code LoadDirectoryAction} and {@code CloseFileAction}. This action
 * should not be used together with {@code OpenDirectoryAction}.
 */
public class LoadDirectoryAction extends LoadFileAction {

  private static final long serialVersionUID = 1L;
  public static final String ID = "file.loadDirectory";

  public LoadDirectoryAction(Application app, View view) {
    super(app, view);
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
    labels.configureAction(this, ID);
  }

  @Override
  protected URIChooser getChooser(View view) {
    return getApplication().getModel().createOpenDirectoryChooser(getApplication(), view);
  }
}
