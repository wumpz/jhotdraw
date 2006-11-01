/*
 * @(#)Main.java  1.0  June 10, 2006
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.samples.pert;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.DefaultApplicationModel;
import org.jhotdraw.app.DefaultOSXApplication;
import org.jhotdraw.app.DefaultSDIApplication;
/**
 * Main.
 *
 * @author Werner Randelshofer.
 * @version 1.0 June 10, 2006 Created.
 */
public class Main {
    
    /** Creates a new instance. */
    public static void main(String[] args) {
        Application app;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("mac")) {
            app = new DefaultOSXApplication();
        } else if (os.startsWith("win")) {
          //  app = new DefaultMDIApplication();
            app = new DefaultSDIApplication();
        } else {
            app = new DefaultSDIApplication();
        }
        
        
        DefaultApplicationModel model = new PertApplicationModel();
        model.setName("Pert");
        model.setVersion("1.0");
        model.setCopyright("Copyright 2006 (c) Werner Randelshofer.");
        model.setProjectClassName("org.jhotdraw.samples.pert.PertProject");
        app.setModel(model);
        app.launch(args);
    }
    
}
