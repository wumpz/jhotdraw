/*
 * @(#)WindowManager.java  1.0  22. März 2007
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

import application.AbstractBean;
import java.awt.*;
import java.beans.*;
import java.util.*;
import java.io.*;

/**
 * WindowManager for document oriented applications.
 * 
 * 
 * 
 * @author Werner Randelshofer
 * @version 1.0 22. März 2007 Created.
 */
public abstract class WindowManager extends AbstractBean {
    private static WindowManager instance;
    private DocumentOrientedApplication application;
    
    public static void setInstance(WindowManager instance) {
        WindowManager.instance = instance;
    }
    public static WindowManager getInstance() {
        return instance;
    }
    
    public void setApplication(DocumentOrientedApplication app) {
        this.application = app;
    }
    public DocumentOrientedApplication getApplication() {
        return application;
    }
    
    /**
     * Launches the window manager. This must be invoked on the main thread,
     * just before method launch of DocumentOrientedApplication
     * is called.
     * @see DocumentOrientedApplication#launch
     */
    public abstract void preLaunch();
    /**
     * Initializes the window manager.
     * This method must be invoked before method init of DocumentOrientedApplication¨
     * is called.
     * @see DocumentOrientedApplication#init
     */
    public abstract void preInit();
    /**
     * Starts the window manager.
     * This method must be invoked before method start of DocumentOrientedApplication¨
     * is called.
     * @see DocumentOrientedApplication#start
     */
    public abstract void preStart();
    
    /**
     * Stops the application without saving any unsaved projects.
     * <code>startup()</code> must have been invoked before the application is stopped.
     */
    public abstract void disposeAllProjects();
    
   
    /**
     * Adds a project to this application.
     * Fires a "documentCount" property change event.
     * Invokes method setApplication(this) on the project object.
     */
    public abstract void add(Project p);
    
    /**
     * Removes a project from this application and removes it from the users
     * view.
     * Fires a "documentCount" property change event.
     * Invokes method setApplication(null) on the project object.
     */
    public abstract void remove(Project p);
    
    /**
     * Shows a project.
     */
    public abstract void show(Project p);
    /**
     * Hides a project.
     */
    public abstract void hide(Project p);
    
    /**
     * This is a convenience method for removing a project and disposing it.
     */
    public abstract void dispose(Project p);
    
    /**
     * Returns a read only collection view of the projects of this application.
     */
    public abstract Collection<Project> projects();
    
    /**
     * Returns the current project. This is used for OSXApplication and 
     * MDIApplication which share actions among multiple Project instances.
     * Current project may be become null, if the
     * application has no project.
     * <p>
     * This is a bound property.
     */
    public abstract Project getCurrentProject();
    
    
    /**
     * Returns true, if this application shares tools among multiple projects.
     */
    public abstract boolean isSharingToolsAmongProjects();
    
    /**
     * Returns the application component. 
     * This may return null, if the application is not represented by a component
     * of its own on the user interface.
     */
    public abstract Component getComponent();
    
    /**
     * Returns the recently opened files.
     * By convention, this is an immutable list.
     */
    public abstract java.util.List<File> recentFiles();
    /**
     * Appends a file to the list of recent files.
     * This fires a property change event for the property "recentFiles".
     */
    public abstract void addRecentFile(File file);
    /**
     * Clears the list of recent files.
     * This fires a property change event for the property "recentFiles".
     */
    public abstract void clearRecentFiles();
    
    /**
     * Convenience method for retrieving the application name.
     */
    public abstract String getName();
    
}
