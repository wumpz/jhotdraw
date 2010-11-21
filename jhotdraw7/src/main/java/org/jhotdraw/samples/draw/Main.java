/*
 * @(#)Main.java
 *
 * Copyright (c) 1996-2010 by the original authors of JHotDraw and all its
 * contributors. All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the 
 * license agreement you entered into with the copyright holders. For details
 * see accompanying license terms.
 */

package org.jhotdraw.samples.draw;

import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.app.*;
/**
 * Main.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class Main {
    
    /** Creates a new instance. */
    public static void main(String[] args) {
        ResourceBundleUtil.setVerbose(true);

        Application app;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("mac")) {
            app = new OSXApplication();
        } else if (os.startsWith("win")) {
            //app = new MDIApplication();
            app = new SDIApplication();
        } else {
            app = new SDIApplication();
        }

        DrawApplicationModel model = new DrawApplicationModel();
        model.setName("JHotDraw Draw");
        model.setVersion(Main.class.getPackage().getImplementationVersion());
        model.setCopyright("Copyright 2006-2009 (c) by the authors of JHotDraw and all its contributors.\n" +
                "This software is licensed under LGPL or Creative Commons 3.0 Attribution.");
        model.setViewClassName("org.jhotdraw.samples.draw.DrawView");
        app.setModel(model);
        app.launch(args);
    }
    
}
