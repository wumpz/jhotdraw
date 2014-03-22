/*
 * @(#)AboutAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.app;

import java.awt.Component;
import org.jhotdraw.util.*;
import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.app.*;
import org.jhotdraw.app.action.AbstractApplicationAction;

/**
 * Displays a dialog showing information about the application.
 * <p>
 * This action is called when the user selects the "About" menu item.
 * The menu item is automatically created by the application.
 * {@link OSXApplication} places the menu item in the "Application" menu,
 * {@link SDIApplication} and {@link MDIApplication} in the "Help" menu.
 * <p>
 * This action is automatically created by the application and put into the
 * {@code ApplicationModel} before {@link ApplicationModel#initApplication} is
 * called.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class AboutAction extends AbstractApplicationAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "application.about";

    /** Creates a new instance. */
    public AboutAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Application app = getApplication();

        Component c = app.getComponent();

        // This ensures that we open the option pane on the center of the screen
        // on Mac OS X.
        if (c == null || c.getBounds().isEmpty()) {
            c = null;
        }


        JOptionPane.showMessageDialog(c,
                "<html>" + UIManager.getString("OptionPane.css")
                + "<p><b>" + app.getName() + (app.getVersion() == null ? "" : " " + app.getVersion()) + "</b><br>" + app.getCopyright().replace("\n", "<br>")
                + "<br><br>Running on"
                + "<br>  Java: " + System.getProperty("java.version")
                + ", " + System.getProperty("java.vendor")
                + "<br>  JVM: " + System.getProperty("java.vm.version")
                + ", " + System.getProperty("java.vm.vendor")
                + "<br>  OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version")
                + ", " + System.getProperty("os.arch"),
                "About", JOptionPane.PLAIN_MESSAGE);
    }
}
