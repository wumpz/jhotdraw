/*
 * @(#)TeddyApplication.java  1.0  22. März 2007
 *
 * Copyright (c) 2006 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
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
import javax.swing.*;
import org.jhotdraw.application.*;
import org.jhotdraw.application.action.Actions;
import org.jhotdraw.samples.teddyapplication.action.*;
import org.jhotdraw.util.*;

/**
 * TeddyApplication.
 *
 * @author Werner Randelshofer
 * @version 1.0 22. März 2007 Created.
 */
public class TeddyApplication extends DocumentOrientedApplication {
    
    /** Creates a new instance. */
    public TeddyApplication() {
    }

    public void startup(String[] args) {
        putAction(org.jhotdraw.samples.teddyapplication.action.FindAction.ID, new org.jhotdraw.samples.teddyapplication.action.FindAction());
        putAction(ToggleLineWrapAction.ID, new ToggleLineWrapAction());
        putAction(ToggleStatusBarAction.ID, new ToggleStatusBarAction());
        putAction(ToggleLineNumbersAction.ID, new ToggleLineNumbersAction());
    }
    
    public Project basicCreateProject() {
        return new TeddyProject();
    }

    public List<JToolBar> createToolBars(WindowManager a, Project p) {
        return new LinkedList<JToolBar>();
    }

    public List<JMenu> createMenus(WindowManager a, Project p) {
        LinkedList<JMenu> mb = new LinkedList<JMenu>();
        
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.teddy.Labels");
        
        JMenu m;
        JCheckBoxMenuItem cbmi;
        
        mb.add(createEditMenu(a, p));
        
        m = new JMenu();
        labels.configureMenu(m, "format");
        cbmi = new JCheckBoxMenuItem(getAction(ToggleLineWrapAction.ID));
        Actions.configureJCheckBoxMenuItem(cbmi, getAction(ToggleLineWrapAction.ID));
        m.add(cbmi);
        cbmi = new JCheckBoxMenuItem(getAction(ToggleLineNumbersAction.ID));
        Actions.configureJCheckBoxMenuItem(cbmi, getAction(ToggleLineNumbersAction.ID));
        m.add(cbmi);
        cbmi = new JCheckBoxMenuItem(getAction(ToggleStatusBarAction.ID));
        Actions.configureJCheckBoxMenuItem(cbmi, getAction(ToggleStatusBarAction.ID));
        m.add(cbmi);
        mb.add(m);
        
        return mb;
    }
    
}
