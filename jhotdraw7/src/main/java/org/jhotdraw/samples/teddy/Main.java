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

package org.jhotdraw.samples.teddy;

import org.jhotdraw.app.*;

/**
 * Main class.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class Main {
    public final static String NAME = "JHotDraw Teddy";
    public final static String COPYRIGHT = "© 1996-2010 by the original authors of JHotDraw and all its contributors.";
    
    /**
     * Launches the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TeddyApplicationModel tam = new TeddyApplicationModel();
        tam.setCopyright(COPYRIGHT);
        tam.setName(NAME);
        tam.setViewClassName("org.jhotdraw.samples.teddy.TeddyView");
        tam.setVersion(Main.class.getPackage().getImplementationVersion());
        
        Application app;
        if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
            app = new OSXApplication();
        } else if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            //app = new DefaultMDIApplication();
            app = new SDIApplication();
        } else {
            app = new SDIApplication();
        }
        app.setModel(tam);
        app.launch(args);
    }
}
