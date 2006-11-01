/*
 * @(#)Project.java  1.0  October 4, 2005
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

import java.io.*;
import java.beans.*;
import javax.swing.*;
/**
 * A project represents a work space for a document or a set of related
 * documents within an Application.
 * 
 * @author Werner Randelshofer
 * @version 1.0 October 4, 2005 Created.
 */
public interface Project {
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
     * By convention this is never invoked on the AWT Event Dispatcher Thread.
     */
    public void write(File f) throws IOException;
    
    /**
     * Reads the project from the specified file.
     * By convention this is never invoked on the AWT Event Dispatcher Thread.
     */
    public void read(File f) throws IOException;
    
    /**
     * Clears the project.
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
     */
    public void init();
    
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
    
}
