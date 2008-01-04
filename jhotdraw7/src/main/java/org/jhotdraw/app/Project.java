/*
 * @(#)Project.java  3.0  2007-12-25
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
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

import java.io.*;
import java.beans.*;
import javax.swing.*;
/**
 * A project represents a work space for a document or a set of related
 * documents within an Application.
 * <p>
 * After a project has been initialized using init(),
 * either method clear() must be called
 * or method read, in order to fully initialize a  Project.
 * <p>
 * XXX - Maybe Project should be renamed to Workspace.
 *
 * @author Werner Randelshofer
 * @version 3.0 2007-12-25 Added start, stop, activate and deactivate methods.
 * Added constants for property names. 
 * <br>2.0 2007-11-29 Method clear is now always invoked on a worker 
 * thread.
 * <br>1.0 October 4, 2005 Created.
 */
public interface Project {
    /**
     * The name of the application property.
     */
    public final static String APPLICATION_PROPERTY = "application";
    /**
     * The name of the file property.
     */
    public final static String FILE_PROPERTY = "file";
    /**
     * The name of the title property.
     */
    public final static String TITLE_PROPERTY = "title";
    /**
     * The name of the enabled property.
     */
    public final static String ENABLED_PROPERTY = "enabled";
    /**
     * The name of the hasUnsavedChanges property.
     */
    public final static String HAS_UNSAVED_CHANGES_PROPERTY = "hasUnsavedChanges";
    /**
     * The name of the multipleOpenId property.
     */
    public final static String MULTIPLE_OPEN_ID_PROPERTY = "multipleOpenId";
    /**
     * The name of the showing property.
     */
    public final static String SHOWING_PROPERTY = "showing";
    /**
     * Gets the application to which this project belongs.
     */
    public Application getApplication();
    
    /**
     * Sets the application of the project.
     * By convention, this is only invoked by Application.addProject() and
     * Application.removeProject().
     * This is a bound property.
     */
    public void setApplication(Application newValue);
    
    /**
     * Returns the visual component of the project.
     */
    public JComponent getComponent();
    
    /**
     * Returns the project file.
     */
    public File getFile();
    
    /**
     * Sets the project file.
     * This is a bound property.
     */
    public void setFile(File newValue);
    
    /**
     * Returns the enabled state of the project.
     */
    public boolean isEnabled();
    
    /**
     * Sets the enabled state of the project.
     *
     * The enabled state is used to prevent parallel invocation of actions
     * on the project. If an action consists of a sequential part and a
     * concurrent part, it must disable the project only for the sequential
     * part.
     *
     * Actions that act on the project must check in their actionPerformed
     * method whether the project is enabled.
     * If the project is disabled, they must do nothing.
     * If the project is enabled, they must disable the project,
     * perform the action and then enable the project again.
     *
     * This is a bound property.
     */
    public void setEnabled(boolean newValue);
    
    /**
     * Writes the project to the specified file.
     * By convention this method is never invoked on the AWT Event Dispatcher Thread.
     */
    public void write(File f) throws IOException;
    
    /**
     * Reads the project from the specified file.
     * By convention this method is never invoked on the AWT Event Dispatcher Thread.
     */
    public void read(File f) throws IOException;
    
    /**
     * Clears the project, for example by emptying the contents of
     * the project, or by reading a template contents from a file.
     * By convention this method is never invoked on the AWT Event Dispatcher Thread.
     */
    public void clear();
    
    
    /**
     * Gets the open file chooser for the project.
     */
    public JFileChooser getOpenChooser();
    /**
     * Gets the save file chooser for the project.
     */
    public JFileChooser getSaveChooser();
    /**
     * Returns true, if the project has unsaved changes.
     * This is a bound property.
     */
    public boolean hasUnsavedChanges();
    /**
     * Marks all changes as saved.
     * This changes the state of hasUnsavedChanges to false.
     */
    public void markChangesAsSaved();
    
    /**
     * Executes the specified runnable on the worker thread of the project.
     * Execution is perfomred sequentially in the same sequence as the
     * runnables have been passed to this method.
     */
    public void execute(Runnable worker);
    
    /**
     * Initializes the project.
     * This is invoked right before the application shows the project.
     * A project must not consume many resources before method init() is called.
     * This is crucial for the responsivenes of an application.
     * <p>
     * After a project has been initialized using init(),
     * either method clear() must be called
     * or method read, in order to fully initialize a  Project.
     */
    public void init();
    
    /**
     * Starts the project.
     * Invoked after a project has been made visible to the user.
     * Multiple projects can be visible at the same time.
     */
    public void start();
    /**
     * Activates the project.
     * This occurs, when the user activated the parent window of the project.
     * Only one project can be active at any given time.
     * This method is only invoked on a started project.
     */
    public void activate();
    /**
     * Deactivates the project.
     * This occurs, when the user closes the project, or activated another project.
     * This method is only invoked on a started project.
     */
     public void deactivate();    
    /**
     * Stops the project.
     * Invoked after a project window has been minimized or made invisible.
     */
     public void stop();    
    /**
     * Gets rid of all the resources of the project.
     * No other methods should be invoked on the project afterwards.
     * A project must not consume many resources after method dispose() has been called.
     * This is crucial for the responsivenes of an application.
     */
    public void dispose();
    
    /**
     * Returns the action with the specified id.
     */
    public Action getAction(String id);
    
    /**
     * Puts an action with the specified id.
     */
    public void putAction(String id, Action action);
    
    /**
     * Adds a property change listener.
     */
    public void addPropertyChangeListener(PropertyChangeListener l);
    
    /**
     * Removes a property change listener.
     */
    public void removePropertyChangeListener(PropertyChangeListener l);
    
    /**
     * Sets the multiple open id.
     * The id is used to help distinguish multiply opened projects.
     * The id should be displayed in the title of the project.
     */
    public void setMultipleOpenId(int newValue);
    
    /**
     * Returns the multiple open id.
     * If a project is open only once this should be 1.
     */
    public int getMultipleOpenId();
    
    /**
     * This is used by Application to keep track if a project is showing.
     */
    public boolean isShowing();
    /**
     * This is used by Application to keep track if a project is showing.
     */
    public void setShowing(boolean newValue);
    
    /**
     * Sets the title of the project. 
     * <p>
     * The title is generated by the application, based on the current
     * file of the project. The application ensures that the title uniquely
     * identifies each open project.
     * <p> 
     * The application displays the title in the title bar of the project 
     * window and in all windows which are associated to the project.
     * <p>
     * This is a bound property.
     */
    public void setTitle(String newValue);
    
    /**
     * Gets the title of the project. 
     */
    public String getTitle();
    
}
