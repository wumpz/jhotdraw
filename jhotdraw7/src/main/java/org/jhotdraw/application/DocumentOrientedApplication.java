/*
 * @(#)DocumentOrientedApplication.java  1.0  22. März 2007
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

package org.jhotdraw.application;

import application.*;
import javax.swing.*;
import javax.swing.Action;
import java.util.*;
import org.jhotdraw.application.action.*;
import org.jhotdraw.util.*;
/**
 * Abstract superclass for applications with document interfaces.
 *
 * @author Werner Randelshofer
 * @version 1.0 22. März 2007 Created.
 */
public abstract class DocumentOrientedApplication extends Application {
    private boolean isEnabled;
    private HashMap<String,Action> actions;
    
    /** Creates a new instance. */
    public DocumentOrientedApplication() {
    }
    
    protected final void initialize(String[] args) {
            WindowManager.getInstance().preInit();
            init(args);
            }
    protected final void startup() {
            WindowManager.getInstance().preStart();
            start();
    }

    public final static void launch(java.lang.Class<? extends DocumentOrientedApplication> applicationClass, String[] args) {
        if (WindowManager.getInstance() == null) {
            WindowManager.setInstance(new DefaultSDIWindowManager());
            WindowManager.getInstance().preLaunch();
        }
        Application.launch(applicationClass, args);
    }
    
   public final Project createProject() {
        Project p = basicCreateProject();
        p.init();
            initProject(p);
        return p;
    }
     public abstract Project basicCreateProject();
    
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean newValue) {
        boolean oldValue = isEnabled;
        isEnabled = newValue;
firePropertyChange("enabled", oldValue, newValue);
    }
    
    public void init(String[] args) {
    }
    public void initProject(Project p) {
    }
    
    protected WindowManager getWindowManager() {
        return WindowManager.getInstance();
    }
    
    public void start() {
        Project p = createProject();
        getWindowManager().add(p);
        getWindowManager().show(p);
        setEnabled(true);
    }
    /**
     * Returns the action with the specified id.
     */
    public Action getAction(String id) {
        return (actions == null) ? null : (Action) actions.get(id);
    }
    
    /**
     * Puts an action with the specified id.
     */
    public void putAction(String id, Action action) {
        if (actions == null) {
            actions = new HashMap<String,Action>();
        }
        if (action == null) {
            actions.remove(id);
        } else {
            actions.put(id, action);
        }
    }
    /**
     * Creates tool bars.
     * <p>
     * Depending on the document interface of the application, this method
     * may be invoked only once for the application, or for each opened project.
     * <p>
     * @param a Application.
     * @param p The project for which the toolbars need to be created, or null
     * if the toolbar needs to be shared with multiple projects.
     */
    public abstract List<JToolBar> createToolBars(WindowManager a, Project p);
    
    /**
     * Creates menus.
     * <p>
     * Depending on the document interface of the application, this method
     * may be invoked only once for the application, or for each opened project.
     * <p>
     * @param a Application.
     * @param p The project for which the toolbars need to be created, or null
     * if the toolbar needs to be shared with multiple projects.
     */
    public abstract List<JMenu> createMenus(WindowManager a, Project p);
    protected JMenu createEditMenu(WindowManager a, Project p) {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        
        JMenu m;
        JMenuItem mi;
        
        m = new JMenu();
        labels.configureMenu(m, "edit");
        mi = m.add(getAction(UndoAction.ID));
        //mi.setIcon(null);
        mi = m.add(getAction(RedoAction.ID));
        //mi.setIcon(null);
        m.addSeparator();
        mi = m.add(getAction(CutAction.ID));
        //mi.setIcon(null);
        mi = m.add(getAction(CopyAction.ID));
        //mi.setIcon(null);
        mi = m.add(getAction(PasteAction.ID));
        //mi.setIcon(null);
        mi = m.add(getAction(DuplicateAction.ID));
        //mi.setIcon(null);
        mi = m.add(getAction(DeleteAction.ID));
        //mi.setIcon(null);
        m.addSeparator();
        mi = m.add(getAction(SelectAllAction.ID));
        //mi.setIcon(null);
        if (getAction(FindAction.ID) != null) {
            m.addSeparator();
            m.add(getAction(FindAction.ID));
        }
        return m;
    }
}
