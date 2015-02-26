/*
 * @(#)OSXOpenApplicationAction.java
 * 
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.app;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractApplicationAction;

/**
 * If all views are iconified, de-iconifies a view and brings it to the front.
 * <p>
 * This action is called when the Mac OS X Finder or another Mac OS X application
 * sends an Open Application request to the application.
 * <p>
 * This action is automatically created by {@code DefaultOSXApplication}
 * and put into the {@code ApplicationModel} before
 * {@link org.jhotdraw.app.ApplicationModel#initApplication} is called.
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ReOpenApplicationAction extends AbstractApplicationAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "application.reOpen";

    /** Creates a new instance. */
    public ReOpenApplicationAction(Application app) {
        super(app);
        putValue(Action.NAME, "OSX ReOpen Application");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Application a = getApplication();
        if (a.getActiveView() == null && a.views().size() > 0) {
            View v = a.views().iterator().next();
            Component c = SwingUtilities.getRootPane(v.getComponent()).getParent();
            if (c instanceof JFrame) {
                JFrame f = (JFrame) c;
                if ((f.getExtendedState() & JFrame.ICONIFIED) != 0) {
                    f.setExtendedState(f.getExtendedState() ^ JFrame.ICONIFIED);
                }
                f.requestFocus();
            }
        }
    }
}
