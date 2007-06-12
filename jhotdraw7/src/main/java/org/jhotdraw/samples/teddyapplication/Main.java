/*
 * @(#)Main.java  1.0  2007-02-22
 *
 * Copyright (c) 2007 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.samples.teddyapplication;

import java.util.*;
import java.util.prefs.Preferences;
import org.jhotdraw.application.*;

/**
 * Main class.
 *
 * @author Werner Randelshofer.
 * @version 1.0 2007-02-22 Created.
 */
public class Main {
    /**
     * Launches the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Class app;
        Preferences prefs = Preferences.userNodeForPackage(TeddyView.class);
        String ui = prefs.get("UserInterface","SDI");
        if (ui.equals("SDI")) {
            app = TeddySDIApplication.class;
        } else if (ui.equals("MDI")) {
            app = TeddyMDIApplication.class;
        } else {
            app = null;
            // app = TeddyOSXApplication.class;
        }
        
        AbstractDocumentOrientedApplication.launch(app, args);
    }
    
}
