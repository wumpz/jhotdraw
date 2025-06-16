/*
 * @(#)SaveFileAsAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.View;
import org.jhotdraw.utils.util.*;

/**
 * Presents an {@code URIChooser} and then saves the active view to the specified location.
 *
 * <p>This action is called when the user selects the Save As item in the File menu. The menu item
 * is automatically created by the application.
 *
 * <p>If you want this behavior in your application, you have to create it and put it in your {@code
 * ApplicationModel} in method {@link org.jhotdraw.app.ApplicationModel#initApplication}.
 */
public class SaveFileAsAction extends SaveFileAction {

  private static final long serialVersionUID = 1L;
  public static final String ID = "file.saveAs";

  public SaveFileAsAction(Application app, View view) {
    super(app, view, true);
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
    labels.configureAction(this, ID);
  }
}
