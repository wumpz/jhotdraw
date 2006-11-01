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

package org.jhotdraw.app.action;

import org.jhotdraw.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.app.*;

/**
 * Displays a dialog showing information about the application.
 *
 * @author  Werner Randelshofer
 * @version 1.0  04 January 2005  Created.
 */
public class AboutAction extends AbstractApplicationAction {
    public final static String ID = "about";
    
    /** Creates a new instance. */
    public AboutAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
        }
    
    public void actionPerformed(ActionEvent evt) {
        Application app = getApplication();
        JOptionPane.showMessageDialog(app.getComponent(),
                app.getName()+" "+app.getVersion()+"\n"+app.getCopyright()+
                "\n\nRunning on Java "+System.getProperty("java.vm.version")+
                ", "+System.getProperty("java.vendor"), 
                "About", JOptionPane.PLAIN_MESSAGE);
    }
}
