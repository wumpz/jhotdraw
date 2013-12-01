/*
 * @(#)Main.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.samples.odg;

import org.jhotdraw.app.*;

/**
 * Main entry point of the ODG sample application. Creates an {@link Application}
 * depending on the operating system we run, sets the {@link ODGApplicationModel}
 * and then launches the application. The application then creates
 * {@link ODGView}s and menu bars as specified by the application model.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Main {
    
    /** Creates a new instance. */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application app = new OSXApplication();
        ApplicationModel appModel = new ODGApplicationModel();
        app.setModel(appModel);
        app.launch(args);
        // TODO code application logic here
    }
    
}
