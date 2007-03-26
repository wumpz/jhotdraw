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
        WindowManager wm;
        /*
        if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
            wm = new DefaultOSXWindowManager();
        } else if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            wm = new DefaultMDIWindowManager();
        } else {
            wm = new DefaultSDIWindowManager();
        }*/
            wm = new DefaultSDIWindowManager();
        WindowManager.setInstance(wm);
        WindowManager.getInstance().preLaunch();
        DocumentOrientedApplication.launch(TeddyApplication.class, args);
    }
    
}
