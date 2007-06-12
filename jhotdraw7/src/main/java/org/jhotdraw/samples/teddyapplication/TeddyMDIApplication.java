/*
 * @(#)TeddyMDIApplication.java  1.0  22. März 2007
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

import application.ApplicationContext;
import application.ResourceMap;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.application.*;
import org.jhotdraw.application.action.Actions;
import org.jhotdraw.samples.teddyapplication.action.*;
import org.jhotdraw.util.*;

/**
 * TeddyMDIApplication.
 * 
 * @author Werner Randelshofer
 * @version 1.0 22. März 2007 Created.
 */
public class TeddyMDIApplication extends AbstractMDIApplication {
    
    /** Creates a new instance. */
    public TeddyMDIApplication() {
    }

    /**
     * Gets the class of the document view.
     */
    @Override public Class getViewClass() {
        return TeddyView.class;
    }

    /**
     * Put actions into the application action map, which we haven't defined
     * using the @Action annotation.
     */
    @Override public ActionMap createActionMap() {
        ActionMap m = super.createActionMap();
        m.put(ToggleLineWrapAction.ID, new ToggleLineWrapAction());
        m.put(ToggleStatusBarAction.ID, new ToggleStatusBarAction());
        m.put(ToggleLineNumbersAction.ID, new ToggleLineNumbersAction());
        m.put(FindAction.ID, new FindAction());
        m.put(PreferencesAction.ID, new PreferencesAction());
        return m;
    }
    
    /**
     * Create toolbars.
     */
    @Override public LinkedList<JToolBar> createToolBars(DocumentView p) {
        return new LinkedList<JToolBar>();
    }

    /**
     * Creates the edit menu.
     */
    @Override public JMenu createEditMenu(DocumentView v) {
        JMenu m = super.createEditMenu(v);
        m.addSeparator();
        m.add(getAction(PreferencesAction.ID));
        return m;
        }
    /**
     * Creates the view menu.
     */
    @Override public JMenu createViewMenu(DocumentView p) {
        ResourceMap labels = getResourceMap();
        
        JMenu m;
        JCheckBoxMenuItem cbmi;
        
        m = super.createViewMenu(p);
        if (m == null) {
            m = new JMenu();
            m.setName("View.Menu");
            getFrameworkResourceMap().injectComponent(m);
        }
        
        cbmi = new JCheckBoxMenuItem(getAction(ToggleLineWrapAction.ID));
        Actions.configureJCheckBoxMenuItem(cbmi, getAction(ToggleLineWrapAction.ID));
        m.add(cbmi);
        cbmi = new JCheckBoxMenuItem(getAction(ToggleLineNumbersAction.ID));
        Actions.configureJCheckBoxMenuItem(cbmi, getAction(ToggleLineNumbersAction.ID));
        m.add(cbmi);
        cbmi = new JCheckBoxMenuItem(getAction(ToggleStatusBarAction.ID));
        Actions.configureJCheckBoxMenuItem(cbmi, getAction(ToggleStatusBarAction.ID));
        m.add(cbmi);
        
        return m;
    }

}
