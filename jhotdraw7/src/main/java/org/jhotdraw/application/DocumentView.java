/*
 * @(#)DocumentView.java  2.0  2007-07-07
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
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

import java.io.*;
import java.beans.*;
import javax.swing.*;
/**
 * Provides a view to a document inside of a document oriented Application.
 * <p>
 * A DocumentView usually goes through the following method calls
 * after its creation:
 * {@code init),
 * {@code setEnabled(false)}, 
 * ({@code clear} or {@code read}),
 * {@code setEnabled(true)}, 
 * {@code start}
 *
 * @author Werner Randelshofer
 * @version 2.0 2007-07-07 Renamed from Project to DocumentView. Overhauled
 * overall design.
 * <br>1.0 2007-03-22 Created.
 */
public interface DocumentView {
    // ------------------------------
    // general purpose methods
    // ------------------------------
    /**
     * Returns the visual component of the view.
     *
     * @return The visual component of the view.
     */
    public JComponent getComponent();
    
    /**
     * Sets the name of the view. The name is used by the user to uniquely
     * identify a view.
     *
     * @param newValue the new name of the view
     */
    public void setName(String newValue);
    
    /**
     * Returns the name of the view.
     * 
     * @return the name of the view
     */
    public String getName();
    
    /**
     * Called by the WindowManager to inform this view that it has been loaded
     * into the system. It is always called before the first time that the 
     * {@code start} method is called.
     */
    public void init();
    
    /**
     * Called by the WindowManager to inform this view that it is being
     * reclaimed and that it should destroy of any resources that it has
     * allocated. The {@code stop} method will always be called before 
     * {@code destroy}.
     */
    public void destroy();
    /**
     * Called by the WindowManager to inform this view that it should start
     * its execution. It is called after the {@code init} method and each 
     * time the window wich contains the view is made visible or restored from
     * minimized state.
     */
    public void start();
    
    /**
     * Called by the WindowManager to inform this view that it should stop
     * its execution. It is called when the window which contains the view
     * has been hidded or minimized, and also just before the view is to be 
     * destroyed.
     */
    public void stop();
    
    /**
     * Determines whether this view is enabled. An enabled view can respond 
     * to user input and can perform actions on behalf of the user. Views are 
     * enabled initially by default. A view may be enabled or disabled by 
     * calling its setEnabled method.
     * <p>
     * Actions that act on the view must check in their actionPerformed
     * method whether the view is enabled. If the document view is 
     * disabled, they must do nothing. Ideally, Actions which perform
     * on a view, couple their enabled state with the enabled state of
     * the view.
     *
     * @return the enabled state of the view.
     */
    public boolean isEnabled();
    
    /**
     * Sets the enabled state of the view.
     * <p>
     * The enabled state is used to prevent parallel invocation of actions
     * on the document view. If an action consists of a sequential part and a
     * concurrent part, it must disable the document view only for the sequential
     * part.
     *
     * This is a bound property.
     */
    public void setEnabled(boolean newValue);
    
    /**
     * Executes the specified runnable on the worker thread of the view.
     * Execution is performed sequentially in the same sequence as the
     * runnables have been passed to this method.
     */
    public void execute(Runnable worker);
    
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
    
    // ------------------------------
    // document view specific methods
    // ------------------------------
    /**
     * Returns the file which is associated to this document view.
     * Returns null, if the document view has been created in memory
     * and never been saved yet.
     *
     * @param the file associated with this document view, or null.
     */
    public File getFile();
    
    /**
     * Associates the document view with a file.
     * <p>
     * Changing the value of this property does not trigger file loading
     * or saving.
     *
     * @param file the file associated with this document view. Null
     * values are allowed.
     */
    public void setFile(File newValue);
    
    
    /**
     * Clears the contents of the document view.
     * <p>
     * The cleared contents does not need to be entirely empty. 
     * A view may read a template from the file system or another slow media, 
     * to get its cleared state. 
     * <p>
     * By convention this method is never invoked on the AWT Event Dispatcher
     * Thread. The view needs to be disabled before this method is invoked,
     * and needs to be enabled afterwards.
     */
    public void clear() throws IOException;
    
    /**
     * Writes the contents of the view to the specified file.
     * <p>
     * By convention this method is never invoked on the AWT Event Dispatcher 
     * Thread. The view needs to be disabled before this method is invoked,
     * and needs to be enabled afterwards.
     *
     * @param f The file.
     */
    public void write(File f) throws IOException;
    
    /**
     * Reads contents for the document view from the specified file.
     * <p>
     * By convention this method is never invoked on the AWT Event Dispatcher
     * Thread. The view needs to be disabled before this method is invoked,
     * and needs to be enabled afterwards.
     *
     * @param f The file.
     */
    public void read(File f) throws IOException;
    
    
    /**
     * Gets the open file chooser for the document view.
     * <p>
     * The document view can accessorize the file chooser. The document view
     * should keep the file chooser instance over multiple invocations
     * of this method, so that it remembers its state. 
     */
    public JFileChooser getOpenChooser();
    /**
     * Gets the save file chooser for the document view.
     * <p>
     * The document view can accessorize the file chooser. The document view
     * should keep the file chooser instance over multiple invocations
     * of this method, so that it remembers its state. 
     */
    public JFileChooser getSaveChooser();
    
    /**
     * Returns true, if the document view has unsaved changes.
     * This is a bound property.
     */
    public boolean isModified();
    /**
     * Sets the modified state of the document view.
     * This is a bound property.
     * <p>
     * A view should change its modified state by itself, when its
     * contents is modified.
     */
    public void setModified(boolean newValue);
}
