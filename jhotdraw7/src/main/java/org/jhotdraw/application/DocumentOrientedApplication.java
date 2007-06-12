/*
 * @(#)DocumentOrientedApplication.java  2.0  2007-07-08
 *
 * Copyright (c) 2005-2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.application;

import java.awt.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import java.io.*;
/**
 * An DocumentOrientedApplication handles the lifecycle of Projects and provides windows
 * to present them on screen. Depending on the document interface style 
 * used by the DocumentOrientedApplication, the DocumentOrientedApplication can handle multiple Projects 
 * at the same time, or only one.
 * <p>
 * Typical document interface styles are the Single Document Interface (SDI),
 * the Multiple Document Interface (MDI) and the Mac OS X DocumentOrientedApplication Document
 * Interface (OSX).
 * <p>
 * Typical usage of this class:
 * <pre>
 * public class MyMainClass {
 *     public static void main(String[] args) {
 *         DocumentOrientedApplication application = new DefaultADIApplication();
 *         DefaultApplicationModel model = new DefaultApplicationModel();
 *         model.setName("MyAppliciation");
 *         model.setVersion("1.0");
 *         model.setCopyright("Copyright 2006 (c) Werner Randelshofer. All Rights Reserved.");
 *         model.setProjectClassName("org.jhotdraw.myapplication.MyProject");
 *         application.setModel(model);
 *         application.launch(args);
 *     } 
 * </pre>
 * 
 * @author Werner Randelshofer
 * @version 2007-07-08 Reworked for JSR-296 version 0.42. 
 * <br>1.0 October 4, 2005 Created.
 */
public interface DocumentOrientedApplication {
    /**
     * Launches the application from the main method.
     * This method is typically invoked on the main Thread.
     * This will invoke configureAWT() on the current thread and then 
     * initialize() and startup() on the AWT Event Dispatcher Thread.
     */
    //public void launch(String[] args);
    /**
     * Configures AWT using the provided arguments array. This method
     * is called by launch, before the AWT Event Dispatcher is started.
     */
    //public void configureAWT(String[] args);
    
    /**
     * Initializes the application.
     * <code>configureAWT()</code> should have been invoked before the application
     * is inited. Alternatively an application can be configured using setter
     * methods.
     */
    public void initialize(String[] args);
    
    /**
     * Starts the application.
     * This usually creates a new view, and adds it to the application.
     * <code>initialize()</code> must have been invoked before the application 
     * is started.
     */
    public void startup();
    /**
     * Stops the application without saving any unsaved getViews.
     * <code>initialize()</code> must have been invoked before the application is stopped.
     */
    public void shutdown();
    
    /**
     * Creates a new view for this application.
     */
    public DocumentView createView();
    
    /**
     * Adds a view to this application.
     * Calls {@code init} on the view.
     * Fires a "documentCount" property change event.
     * Invokes method setApplication(this) on the view object.
     */
    public void add(DocumentView v);
    
    /**
     * Removes a view from this application and removes it from the users
     * view.
     * Calls {@code dispose} on the view.
     * Fires a "documentCount" property change event.
     * Invokes method setApplication(null) on the view object.
     */
    public void remove(DocumentView v);
    
    /**
     * Shows a view.
     * Calls {@code start} on the view.
     */
    public void show(DocumentView v);
    /**
     * Hides a view.
     * Calls {@code stop} on the view.
     */
    public void hide(DocumentView v);
    
    /**
     * Returns a read only collection view of the getViews of this application.
     */
    public Collection<DocumentView> getViews();
    
    /**
     * Returns the current view. This is used for OSXApplication and 
     * MDIApplication which share actions among multiple DocumentView instances.
     * Current view may be become null, if the
     * application has no view.
     * <p>
     * This is a bound property.
     */
    public DocumentView getCurrentView();
    
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
     * Returns true, if this application shares tools among multiple getViews.
     */
    public boolean isEditorShared();
    
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

    /**
     * Adds a palette window to the application.
     */
    public void addPalette(Window w);
    /**
     * Removes a palette window from the application.
     */
    public void removePalette(Window w);
    
    /**
     * Gets the action with the specified key from the ActionMap of the application.
     */
    public Action getAction(Object key);
}
