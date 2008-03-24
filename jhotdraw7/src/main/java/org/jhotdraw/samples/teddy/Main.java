/*
 * @(#)Main.java  1.0  2005-10-04
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.samples.teddy;

import java.util.*;
import org.jhotdraw.app.*;

/**
 * Main class.
 *
 * @author Werner Randelshofer.
 * @version 1.0 2005-10-04 Created.
 */
public class Main {
    public final static String NAME = "JHotDraw Teddy";
    public final static String COPYRIGHT = "© 2005-2006 Werner Randelshofer";
    
    /**
     * Launches the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TeddyApplicationModel tam = new TeddyApplicationModel();
        tam.setCopyright("© 2005-2008 Werner Randelshofer");
        tam.setName("Teddy");
        tam.setViewClassName("org.jhotdraw.samples.teddy.TeddyView");
        tam.setVersion(Main.class.getPackage().getImplementationVersion());
        
        AbstractApplication app;
        if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
            app = new DefaultOSXApplication();
        } else if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            app = new DefaultMDIApplication();
        } else {
            app = new DefaultSDIApplication();
        }
        app.setModel(tam);
        app.launch(args);
    }
}
