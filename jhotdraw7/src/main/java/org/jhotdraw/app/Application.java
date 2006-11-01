/*
 * @(#)Application.java  1.0  October 4, 2005
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
import java.beans.*;
import java.util.*;
import javax.swing.*;
import java.io.*;
/**
 * An Application handles the lifecycle of Projects and provides windows
 * to present them on screen. Depending on the document interface style 
 * used by the Application, the Application can handle multiple Projects 
 * at the same time, or only one.
 * <p>
 * Typical document interface styles are the Single Document Interface (SDI),
 * the Multiple Document Interface (MDI) and the Mac OS X Application Document
 * Interface (OSX).
 * <p>
 * Typical usage of this class:
 * <pre>
 * public class MyMainClass {
 *     public static void main(String[] args) {
 *         Application app = new DefaultADIApplication();
 *         DefaultApplicationModel model = new DefaultApplicationModel();
 *         model.setName("MyAppliciation");
 *         model.setVersion("1.0");
 *         model.setCopyright("Copyright 2006 (c) Werner Randelshofer. All Rights Reserved.");
 *         model.setProjectClassName("org.jhotdraw.myapplication.MyProject");
 *         app.setModel(model);
 *         app.launch(args);
 *     } 
 * </pre>
 *
 * @author Werner Randelshofer
 * @version 1.0 October 4, 2005 Created.
 */
public interface Application {
    /**
     * Launches the application from the main method.
     * This method is typically invoked on the main Thread.
     * This will invoke configure() on the current thread and then 
     * init() and start() on the AWT Event Dispatcher Thread.
     */
    public void launch(String[] args);
    /**
     * Configures the application using the provided arguments array.
     */
    public void configure(String[] args);
    
    /**
     * Initializes the application.
     * <code>configure()</code> should have been invoked before the application
     * is inited. Alternatively an application can be configured using setter
     * methods.
     */
    public void init();
    
    /**
     * Starts the application.
     * This usually creates a new project, and adds it to the application.
     * <code>init()</code> must have been invoked before the application is started.
     */
    public void start();
    /**
     * Stops the application without saving any unsaved projects.
     * <code>init()</code> must have been invoked before the application is stopped.
     */
    public void stop();
    
    /**
     * Creates a new project for this application.
     */
    public Project createProject();
    
    /**
     * Adds a project to this application.
     * Fires a "documentCount" property change event.
     * Invokes method setApplication(this) on the project object.
     */
    public void add(Project p);
    
    /**
     * Removes a project from this application and removes it from the users
     * view.
     * Fires a "documentCount" property change event.
     * Invokes method setApplication(null) on the project object.
     */
    public void remove(Project p);
    
    /**
     * Shows a project.
     */
    public void show(Project p);
    /**
     * Hides a project.
     */
    public void hide(Project p);
    
    /**
     * This is a convenience method for removing a project and disposing it.
     */
    public void dispose(Project p);
    
    /**
     * Returns a read only collection view of the projects of this application.
     */
    public Collection<Project> projects();
    
    /**
     * Returns the current project. This is used for OSXApplication and 
     * MDIApplication which share actions among multiple Project instances.
     * Current project may be become null, if the
     * application has no project.
     * <p>
     * This is a bound property. 
     */
    public Project getCurrentProject();
    
    /**
     * Returns the enabled state of the application.
     */
    public boolean isEnabled();
    
    
    /**
     * Sets the enabled state of the application.
     *
     * The enabled state is used to prevent parallel invocation of actions
     * on the application. If an action consists of a sequential part and a
     * concurrent part, it must disable the application only for the sequential
     * part.
     *
     * Actions that act on the application must check in their actionPerformed
     * method whether the application is enabled.
     * If the application is disabled, they must do nothing. 
     * If the application is enabled, they must disable the application,
     * perform the action and then enable the application again.
     *
     * This is a bound property.
     */
    public void setEnabled(boolean newValue);
    /**
     * Adds a property change listener.
     */
    public void addPropertyChangeListener(PropertyChangeListener l);

    /**
     * Removes a property change listener.
     */
    public void removePropertyChangeListener(PropertyChangeListener l);
    
    /**
     * Returns the name of the application.
     */
    public String getName();
    /**
     * Returns the version of the application.
     */
    public String getVersion();
    /**
     * Returns the copyright of the application.
     */
    public String getCopyright();
    
    /**
     * Sets the application model.
     */
    public void setModel(ApplicationModel newValue);

    /**
     * Returns the application model.
     */
    public ApplicationModel getModel();
    
    /**
     * Returns true, if this application shares tools among multiple projects.
     */
    public boolean isSharingToolsAmongProjects();
    
    /**
     * Returns the application component. 
     * This may return null, if the application is not represented by a component
     * of its own on the user interface.
     */
    public Component getComponent();
    
    /**
     * Returns the recently opened files.
     * By convention, this is an immutable list.
     */
    public java.util.List<File> recentFiles();
    /**
     * Appends a file to the list of recent files.
     * This fires a property change event for the property "recentFiles".
     */
    public void addRecentFile(File file);
    /**
     * Clears the list of recent files.
     * This fires a property change event for the property "recentFiles".
     */
    public void clearRecentFiles();
}
