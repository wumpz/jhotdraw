/*
 * @(#)TeddyApplicationModel.java  1.0  March 10, 2007
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

package org.jhotdraw.samples.teddy;

import javax.swing.*;
import org.jhotdraw.app.*;
import java.util.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.samples.teddy.action.*;
import org.jhotdraw.util.*;

/**
 * TeddyApplicationModel.
 *
 * @author Werner Randelshofer
 * @version 1.0 March 10, 2007 Created.
 */
public class TeddyApplicationModel extends DefaultApplicationModel {
    
    /** Creates a new instance. */
    public TeddyApplicationModel() {
    }
    
    @Override public void initApplication(Application a) {
        putAction(org.jhotdraw.samples.teddy.action.FindAction.ID, new org.jhotdraw.samples.teddy.action.FindAction(a));
        putAction(ToggleLineWrapAction.ID, new ToggleLineWrapAction(a));
        putAction(ToggleStatusBarAction.ID, new ToggleStatusBarAction(a));
        putAction(ToggleLineNumbersAction.ID, new ToggleLineNumbersAction(a));
        putAction(PrintAction.ID, null);
    }
    
    @Override public void initProject(Application a, Project p) {
    }
    
    @Override public List<JMenu> createMenus(Application a, Project p) {
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
