/*
 * @(#)Main.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.samples.teddy;

import java.util.HashMap;
import org.jhotdraw.app.Application;

/**
 * Main entry point of the Teddy sample application. Creates an
 * {@link Application} depending on the operating system we run, sets the
 * {@link TeddyApplicationModel} and then launches the application. The
 * application then creates {@link TeddyView}s and menu bars as specified by the
 * application model.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class Main {

    public static final String NAME = "JHotDraw Teddy";
    public static final String COPYRIGHT = "© 1996-2013 by the original authors of JHotDraw and all its contributors.";

    /**
     * Launches the application. <p> Supported command line parameters:
     * <pre>
     * -app osx|mdi|sdi|cross     // Application type
     * </pre>
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        TeddyApplicationModel tam = new TeddyApplicationModel();
        tam.setCopyright(COPYRIGHT);
        tam.setName(NAME);
        tam.setViewClassName("org.jhotdraw.samples.teddy.TeddyView");
        tam.setVersion(Main.class.getPackage().getImplementationVersion());

        HashMap<String, String> types = new HashMap<String, String>();
        types.put("mdi", "org.jhotdraw.app.MDIApplication");
        types.put("sdi", "org.jhotdraw.app.SDIApplication");
        types.put("osx", "org.jhotdraw.app.OSXApplication");
        types.put("cross", "org.jhotdraw.app.CrossPlatformApplication");

        String type;
        if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
            type = "osx";
        } else {
            type = "sdi";
        }
        for (int i = 0; i < args.length; i++) {
            if ("-app".equals(args[i]) && i < args.length - 1) {
                type = args[++i];
                if (!types.containsKey(type)) {
                    throw new IllegalArgumentException("-app " + args[i]);
                }
            }
        }

        Application app = (Application) Class.forName(types.get(type)).newInstance();
        app.setModel(tam);
        app.launch(args);
    }
}
