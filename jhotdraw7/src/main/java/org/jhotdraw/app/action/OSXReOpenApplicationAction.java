/*
 * @(#)OSXOpenApplicationAction.java
 * 
 * Copyright (c) 2009 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 * 
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.app.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;

/**
 * Called when the application receives an Open Application event from the
 * Finder or another application. Usually this will come from the Finder when
 * a user double-clicks your application icon. If there is any special code
 * that you want to run when you user launches your application from the Finder
 * or by sending an Open Application event from another application, include
 * that code as part of this handler. The Open Application event is sent after
 * AWT has been loaded.
 *
 * @author Werner Randelshofer
 * @version 1.0 2009-12-18 Created.
 */
public class OSXReOpenApplicationAction extends AbstractApplicationAction {

    public final static String ID = "application.reopen";
    /** Creates a new instance. */
    public OSXReOpenApplicationAction(Application app) {
        super(app);
        putValue(Action.NAME, "OSX Reopen Application");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Application a = getApplication();
        View v = a.getActiveView();
        if (v != null) {
           Component c= SwingUtilities.getRootPane(v.getComponent()).getParent();
           if (c instanceof JFrame) {
               JFrame f = (JFrame)c;
               if ((f.getExtendedState()&JFrame.ICONIFIED)!=0) {
               f.setExtendedState(f.getExtendedState()^JFrame.ICONIFIED);
               }
               f.requestFocus();
           }
        }
    }

}
