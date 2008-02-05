/*
 * @(#)DefaultAppletApplication.java  1.0  June 10, 2006
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
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
        p.start();
        p.activate();
    }

    public void hide(Project p) {
        p.deactivate();
        p.stop();
        applet.getContentPane().removeAll();
        this.project = null;
    }

    public Project getActiveProject() {
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
