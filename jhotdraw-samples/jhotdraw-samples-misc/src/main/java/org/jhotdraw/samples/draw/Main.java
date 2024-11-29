/*
 * @(#)Main.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.draw;

import org.jhotdraw.api.app.Application;
import org.jhotdraw.app.OSXApplication;
import org.jhotdraw.app.SDIApplication;
import org.jhotdraw.utils.util.ResourceBundleUtil;

/**
 * Main entry point of the Draw sample application. Creates an {@link Application} depending on the
 * operating system we run, sets the {@link DrawApplicationModel} and then launches the application.
 * The application then creates {@link DrawView}s and menu bars as specified by the application
 * model.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class Main {

  public static void main(String[] args) {
    ResourceBundleUtil.setVerbose(true);
    Application app;
    String os = System.getProperty("os.name").toLowerCase();
    if (os.startsWith("mac")) {
      app = new OSXApplication();
    } else if (os.startsWith("win")) {
      // app = new MDIApplication();
      app = new SDIApplication();
    } else {
      app = new SDIApplication();
    }
    DrawApplicationModel model = new DrawApplicationModel();
    model.setName("JHotDraw Draw");
    model.setVersion(Main.class.getPackage().getImplementationVersion());
    model.setCopyright(
        "Copyright 2006-2009 (c) by the authors of JHotDraw and all its contributors.\n"
            + "This software is licensed under LGPL or Creative Commons 3.0 Attribution.");
    model.setViewClassName("org.jhotdraw.samples.draw.DrawView");
    app.setModel(model);
    app.launch(args);
  }
}
