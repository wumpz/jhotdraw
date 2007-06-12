/*
 * @(#)AboutAction.java  1.0  04 January 2005
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

package org.jhotdraw.application.action;

import application.*;
import org.jhotdraw.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.application.*;

/**
 * Displays a dialog showing information about the application.
 *
 * @author  Werner Randelshofer
 * @version 1.0  04 January 2005  Created.
 */
public class AboutAction extends AbstractApplicationAction {
    public final static String ID = "Application.about";
    
    /** Creates a new instance. */
    public AboutAction() {
        initActionProperties(ID);
    }
    
    public void actionPerformed(ActionEvent evt) {
        DocumentOrientedApplication application = getApplication();
        ResourceMap r = getResourceMap();
        JOptionPane.showMessageDialog(
                application.getComponent(),
                r.getString("Application.name")+" "+
                r.getString("Application.version")+"\n"+
                r.getString("Application.vendor")+
                "\n\nRunning on Java "+
                System.getProperty("java.vm.version")+
                ", "+System.getProperty("java.vendor"),
                "About", JOptionPane.PLAIN_MESSAGE);
    }
}
