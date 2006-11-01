/*
 * @(#)DefaultApplicationModel.java  1.0  June 10, 2006
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

import org.jhotdraw.beans.*;
import java.util.*;
import javax.swing.*;
/**
 * DefaultApplicationModel.
 *
 * @author Werner Randelshofer.
 * @version 1.0 June 10, 2006 Created.
 */
public class DefaultApplicationModel
        extends AbstractBean
        implements ApplicationModel {
    
    private HashMap<String,Action> actions;
    private String name;
    private String version;
    private String copyright;
    private Class projectClass;
    private String projectClassName;
    
    
    /** Creates a new instance. */
    public DefaultApplicationModel() {
    }
    
    public void setName(String newValue) {
        String oldValue = name;
        name = newValue;
        firePropertyChange("name", oldValue, newValue);
    }
    
    public String getName() {
        return name;
    }
    
    public void setVersion(String newValue) {
        String oldValue = version;
        version = newValue;
        firePropertyChange("version", oldValue, newValue);
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setCopyright(String newValue) {
        String oldValue = copyright;
        copyright = newValue;
        firePropertyChange("copyright", oldValue, newValue);
    }
    
    public String getCopyright() {
        return copyright;
    }
    /**
     * Use this method for best application startup performance.
     */
    public void setProjectClassName(String newValue) {
        String oldValue = projectClassName;
        projectClassName = newValue;
        firePropertyChange("projectClassName", oldValue, newValue);
    }
    
    /**
     * Use this method only, if setProjectClassName() does not suit you. 
     */
    public void setProjectClass(Class newValue) {
        Class oldValue = projectClass;
        projectClass = newValue;
        firePropertyChange("projectClass", oldValue, newValue);
    }
    
    public Class getProjectClass() {
        if (projectClass == null) {
            if (projectClassName != null) {
        try {
                projectClass = Class.forName(projectClassName);
        } catch (Exception e) {
            InternalError error = new InternalError("unable to get project class");
            error.initCause(e);
            throw error;
        }
            }
        }
        return projectClass;
    }
    
    public Project createProject() {
        try {
            return (Project) getProjectClass().newInstance();
        } catch (Exception e) {
            InternalError error = new InternalError("unable to create project");
            error.initCause(e);
            throw error;
        }
    }
    
    /**
     * Creates toolbars for the application.
     * This class always returns an empty list. Subclasses may return other
     * values.
     *
     * XXX - We should not need to create a toolbar until the user needs to work
     * with it.
     */
    public List<JToolBar> createToolBars(Application a, Project p) {
        return new LinkedList<JToolBar>();
    }
    public List<JMenu> createMenus(Application a, Project p) {
        return new LinkedList<JMenu>();
    }

    public void initProject(Application a, Project p) {
    }

    public void initApplication(Application a) {
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
}
