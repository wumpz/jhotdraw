/*
 * @(#)Main.java  1.0  2005-10-04
 *
 * Copyright (c) 2005 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
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
    public final static String NAME = "Teddy";
    public final static String VERSION = "1.0.1";
    public final static String COPYRIGHT = "(C) 2005-2006 Werner Randelshofer";
    
    /**
     * Launches the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TeddyApplicationModel tam = new TeddyApplicationModel();
        tam.setCopyright("© 2005-2007 Werner Randelshofer");
        tam.setName("Teddy");
        tam.setProjectClassName("org.jhotdraw.samples.teddy.TeddyProject");
        tam.setVersion("2.0");
        
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
