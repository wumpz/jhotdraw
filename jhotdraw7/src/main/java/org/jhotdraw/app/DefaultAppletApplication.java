/*
 * @(#)DefaultAppletApplication.java  1.0  June 10, 2006
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

package org.jhotdraw.app;

import java.awt.*;
import javax.swing.*;

/**
 * Default Application that can be run as an Applet.
 * <p>
 * FIXME - To be implemented.
 *
 * @author Werner Randelshofer
 * @version 1.0 2006-06-10 Created.
 */
public class DefaultAppletApplication extends AbstractApplication {
    private JApplet applet;
    private Project project;
    
    /** Creates a new instance of DefaultAppletApplication */
    public DefaultAppletApplication(JApplet applet) {
        this.applet = applet;
    }
    
    public void show(Project p) {
        this.project = p;
        applet.getContentPane().removeAll();
        applet.getContentPane().add(p.getComponent());
    }

    public void hide(Project p) {
        applet.getContentPane().removeAll();
        this.project = null;
    }

    public Project getCurrentProject() {
        return project;
    }

    public boolean isSharingToolsAmongProjects() {
        return false;
    }

    public Component getComponent() {
        return applet;
    }

    protected void initProjectActions(Project p) {
    }
    
}
