/*
 * @(#)AbstractDocumentOrientedApplication.java  2.0  2007-07-08
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

import application.*;
import java.awt.*;
import org.jhotdraw.application.action.*;
import org.jhotdraw.beans.*;
import org.jhotdraw.gui.Worker;
import org.jhotdraw.util.*;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import java.io.*;
/**
 * Base class for {@code DocumentOrientedApplication}s.
 *
 * @author Werner Randelshofer
 * @version 2007-07-08 Reworked for JSR-296 version 0.42.
 * <br>1.1 2006-05-01 System.exit(0) explicitly in method stop().
 * <br>1.0 October 4, 2005 Created.
 */
public abstract class AbstractDocumentOrientedApplication extends application.Application implements DocumentOrientedApplication {
    private boolean isExtensionVisible;
    /**
     * List of views managed by this application.
     */
    private LinkedList<DocumentView> views = new LinkedList<DocumentView>();
    /**
     * Globaly enable or disable the application.
     * FIXME - Replace with application.Task$InputBlocker?
     */
    private boolean isEnabled = true;
    /**
     * List of recently opened files.
     */
    private LinkedList<File> recentFiles = new LinkedList<File>();
    /**
     * Maximum number of recently opened files.
     */
    private final static int maxRecentFilesCount = 10;
    
    /**
     * Preferences.
     * FIXME - Replace with application.SessionStore?
     */
    private Preferences prefs;
    
    private ResourceMap resourceMap;
    
    public final static String PROP_VIEW_COUNT = "viewCount";
    public final static String PROP_CURRENT_VIEW = "currentView";
    
    /**
     * Launches the application. This method must be called from the Main thread,
     * before AWT is started.
     */
    public static <T extends AbstractDocumentOrientedApplication> void launch(Class<T> applicationClass, String[] args) {
        initAWT(args);
        application.Application.launch(applicationClass, args);
    }
    
    /**
     * Initializes AWT before it is started. This method is called by
     * by @code launch} on the Main thread.
     */
    public static void initAWT(String[] args) {
    }
    
    /**
     * Initializes the application.
     * If you override this method, make sure to call {@code initDefaults}.
     */
    public void initialize(String[] args) {
        initDefaults();
    }
    /**
     * Initializes the defaults of the application..
     */
    protected void initDefaults() {
        initRecentFiles();
        initActionMap();
        initLookAndFeel();
        initMainFrame();
    }
    
    /**
     * Initializes the main frame of the application - if there is one.
     */
    protected void initMainFrame() {
        
    }
    
    /**
     * Initializes the recent files of the application. This method is called
     * by {@code initDefaults}.
     */
    protected void initRecentFiles() {
        prefs = Preferences.userNodeForPackage(getClass());
        int count = prefs.getInt("recentFileCount", 0);
        for (int i=0; i < count; i++) {
            String path = prefs.get("recentFile."+i, null);
            if (path != null) {
                recentFiles.add(new File(path));
            }
        }
    }
    /**
     * Initializes the action map of the application. This method is called
     * by {@code initDefaults}.
     */
    protected void initActionMap() {
        ActionMap am = createActionMap();
        if (am != null) {
            ActionMap acam = ApplicationContext.getInstance().getActionMap();
            for (Object key : am.keys()) {
                acam.put(key, am.get(key));
            }
        }
    }
    /**
     * Initializes the look and feel of the application. This method is called
     * by {@code initDefaults}.
     */
    protected void initLookAndFeel() {
        if (UIManager.getString("OptionPane.css") == null) {
            UIManager.put("OptionPane.css", "");
        }
    }
    
    /**
     * Starts the application.
     * This usually creates a new view, and adds it to the application.
     * <code>initialize()</code> must have been invoked before the application
     * is started.
     */
    public void startup() {
        final DocumentView v = createView();
        add(v);
        v.setEnabled(false);
        v.execute(new Worker() { // FIXME - Replace by Task
            public Object construct() {
                try {
                    v.clear();
                    return null;
                } catch (IOException e) {
                    return e;
                }
            }
            public void finished(Object result) {
                v.setEnabled(true);
            }
        });
        show(v);
    }
    
    /**
     * Stops the application without saving any unsaved getViews.
     * <code>initialize()</code> must have been invoked before the application is stopped.
     */
    public void shutdown() {
        for (DocumentView v : new LinkedList<DocumentView>(getViews())) {
            remove(v);
        }
        super.shutdown();
    }
    
    /**
     * Creates an action map for actions which haven not been defined using the
     * @Action annotation.
     * This default implementation just returns an empty ActionMap.
     */
    protected ActionMap createActionMap() {
        return new ActionMap();
    }
    /**
     * Convenience method for getting an action with the specified key from the
     * ActionMap of the application.
     */
    public javax.swing.Action getAction(Object key) {
        return ApplicationContext.getInstance().getActionMap().get(key);
    }
    
    /**
     * Creates a new view.
     */
    public final DocumentView createView() {
        try {
            return (DocumentView) getViewClass().newInstance();
        } catch (IllegalAccessException ex) {
            InternalError error = new InternalError(ex.getMessage());
            error.initCause(ex);
            throw error;
        } catch (InstantiationException ex) {
            InternalError error = new InternalError(ex.getMessage());
            error.initCause(ex);
            throw error;
        }
    }
    
    public abstract Class getViewClass();
    
    /**
     * Initializes the view.
     */
    protected void initView(DocumentView v) {
        v.init();
    }
    /**
     * Destroys the view.
     */
    protected void destroyView(DocumentView v) {
        v.destroy();
    }
    
    /**
     * Creates the toolbars for the application.
     */
    protected LinkedList<JToolBar> createToolBars(DocumentView v) {
        LinkedList<JToolBar> l = new LinkedList<JToolBar>();
        return l;
    }
    
    /**
     * Creates a menu bar.
     */
    protected JMenuBar createMenuBar(final DocumentView v, java.util.List<javax.swing.Action> toolBarActions) {
        JMenuBar mb = new JMenuBar();
        for (JMenu mm : createMenus(v)) {
            if (mm != null) mb.add(mm);
        }
        return mb;
    }
    /**
     * Creates the menus for the application. Most applications will put the
     * menu bars into JMenuBar.
     */
    protected LinkedList<JMenu> createMenus(DocumentView v) {
        LinkedList<JMenu> l = new LinkedList<JMenu>();
        JMenu m;
        
        if (null != (m = createFileMenu(v))) { l.add(m); }
        if (null != (m = createEditMenu(v))) { l.add(m); }
        if (null != (m = createViewMenu(v))) { l.add(m); }
        if (null != (m = createWindowMenu(v))) { l.add(m); }
        if (null != (m = createHelpMenu(v))) { l.add(m); }
        
        return l;
    }
    
    /**
     * Creates the file menu. Return null, if you don't want this menu.
     */
    protected JMenu createFileMenu(final DocumentView v) {
        return null;
    }
    
    protected void updateOpenRecentMenu(JMenu openRecentMenu) {
        if (openRecentMenu.getItemCount() > 0) {
            JMenuItem clearRecentFilesItem = (JMenuItem) openRecentMenu.getItem(
                    openRecentMenu.getItemCount() - 1
                    );
            openRecentMenu.removeAll();
            for (File f : recentFiles()) {
                openRecentMenu.add(new LoadRecentAction(f));
            }
            if (recentFiles().size() > 0) {
                openRecentMenu.addSeparator();
            }
            openRecentMenu.add(clearRecentFilesItem);
        }
    }
    
    /**
     * Creates the edit menu. Return null, if you don't want this menu.
     */
    protected JMenu createEditMenu(final DocumentView v) {
        ResourceMap labels = getFrameworkResourceMap();
        
        JMenu m;
        JMenuItem mi;
        
        m = new JMenu();
        m.setName("Edit.Menu");
        labels.injectComponent(m);
        mi = m.add((v != null) ? v.getAction(UndoAction.ID) : getAction(UndoAction.ID));
        mi.setIcon(null);
        mi = m.add((v != null) ? v.getAction(RedoAction.ID) : getAction(RedoAction.ID));
        mi.setIcon(null);
        m.addSeparator();
        mi = m.add(getAction(CutAction.ID));
        mi.setIcon(null);
        mi = m.add(getAction(CopyAction.ID));
        mi.setIcon(null);
        mi = m.add(getAction(PasteAction.ID));
        mi.setIcon(null);
        mi = m.add(getAction(DuplicateAction.ID));
        mi.setIcon(null);
        mi = m.add(getAction(DeleteAction.ID));
        mi.setIcon(null);
        m.addSeparator();
        mi = m.add(getAction(SelectAllAction.ID));
        mi.setIcon(null);
        if (getAction(FindAction.ID) != null) {
            m.addSeparator();
            m.add(getAction(FindAction.ID));
        }
        return m;
    }
    
    /**
     * Creates the view menu. Return null, if you don't want this menu.
     *
     * @param v The document view.
     * @param toolbarActions Toolbar actions. This value is null, if the toolbar
     * actions do not get into this menu.
     */
    protected JMenu createViewMenu(final DocumentView v) {
        return null;
    }
    
    /**
     * Creates the window menu. Return null, if you don't want this menu.
     *
     * @param v The document view.
     * @param toolbarActions Toolbar actions. This value is null, if the toolbar
     * actions do not get into this menu.
     */
    protected JMenu createWindowMenu(final DocumentView v) {
        return null;
    }
    
    /**
     * Creates the help menu. Return null, if you don't want this menu.
     */
    protected JMenu createHelpMenu(final DocumentView v) {
        return null;
    }
    
    /**
     * Adds a view to this application.
     *
     * Calls {@code init} on the view.
     * Fires a "viewCount" property change event.
     * Invokes method setApplication(this) on the view object.
     */
    public void add(DocumentView v) {
        int oldCount = views.size();
        views.add(v);
        initView(v);
        firePropertyChange(PROP_VIEW_COUNT, oldCount, views.size());
    }
    
    /**
     * Removes a view from this application.
     *
     * Calls {@code destroy} on the view.
     * Fires a "documentCount" property change event.
     * Invokes method setApplication(null) on the view object.
     */
    public void remove(DocumentView v) {
        hide(v);
        int oldCount = views.size();
        views.remove(v);
        destroyView(v);
        firePropertyChange(PROP_VIEW_COUNT, oldCount, views.size());
    }
    
    /**
     * Returns a read only collection of all the views of this application.
     */
    public java.util.List<DocumentView> getViews() {
        return Collections.unmodifiableList(views);
    }
    
    /**
     * Shows a view.
     *
     * Calls {@code start} on the view.
     */
    public abstract void show(DocumentView v);
    
    /**
     * Hides a view.
     *
     * Calls {@code stop} on the view.
     */
    public abstract void hide(DocumentView v);
    
    /**
     * Returns the current view. This is used by Actions which act on the
     * current DocumentView.
     *
     * Current view is null, if the application has no visible view.
     * <p>
     * This is a bound property.
     */
    public abstract DocumentView getCurrentView();
    
    /**
     * Returns true, if the application is globally enabled.
     */
    public boolean isEnabled() {
        return isEnabled;
    }
    
    /**
     * Globally enable/disable the application.
     */
    public void setEnabled(boolean newValue) {
        boolean oldValue = isEnabled;
        isEnabled = newValue;
        firePropertyChange("enabled", oldValue, newValue);
    }
    
    /**
     * Returns true, if this application shares an editor among multiple views.
     * This is true for MDI and OSX applications. This is false for SDI applications.
     */
    public abstract boolean isEditorShared();
    
    /**
     * Returns the recently opened files.
     * By convention, this is an immutable list.
     */
    public java.util.List<File> recentFiles() {
        return Collections.unmodifiableList(recentFiles);
    }
    
    /**
     * Clears the list of recent files.
     * This fires a property change event for the property "recentFiles".
     */
    public void clearRecentFiles() {
        java.util.List<File> oldValue = (java.util.List<File>) recentFiles.clone();
        recentFiles.clear();
        prefs.putInt("recentFileCount", recentFiles.size());
        firePropertyChange("recentFiles",
                Collections.unmodifiableList(oldValue),
                Collections.unmodifiableList(recentFiles)
                );
    }
    
    /**
     * Appends a file to the list of recent files.
     * This fires a property change event for the property "recentFiles".
     */
    public void addRecentFile(File file) {
        java.util.List<File> oldValue = (java.util.List<File>) recentFiles.clone();
        if (recentFiles.contains(file)) {
            recentFiles.remove(file);
        }
        recentFiles.addFirst(file);
        if (recentFiles.size() > maxRecentFilesCount) {
            recentFiles.removeLast();
        }
        
        prefs.putInt("recentFileCount", recentFiles.size());
        int i=0;
        for (File f : recentFiles) {
            prefs.put("recentFile."+i, f.getPath());
            i++;
        }
        
        firePropertyChange("recentFiles", oldValue, 0);
        firePropertyChange("recentFiles",
                Collections.unmodifiableList(oldValue),
                Collections.unmodifiableList(recentFiles)
                );
    }
    
    
    public void addPalette(Window w) {
    }
    
    public void removePalette(Window w) {
    }
    
    /**
     * Updates the name of the supplied view.
     */
    public void updateName(DocumentView v) {
        ResourceMap labels = getFrameworkResourceMap();
        String baseName;
        if (v.getFile() == null) {
            baseName = labels.getString("File.unnamedFile");
        } else {
            baseName = v.getFile().getName();
            if (! isExtensionVisible) {
                int p = baseName.lastIndexOf('.');
                if (p != -1) {
                    baseName = baseName.substring(0, p);
                }
            }
        }
        String name = baseName;
        if (name == null || name.startsWith("null")) {
            new Throwable().printStackTrace();
        }
        int count = 1;
        boolean success;
        do {
            success = true;
            for (DocumentView vv : getViews()) {
                if (vv != v && vv.getName() != null && vv.getName().equals(name)) {
                    name = baseName+" "+(++count);
                    success = false;
                    break;
                }
            }
        } while (!success);
        v.setName(name);
    }
    
    /**
     * Convenience method for getting the resource map of the current application.
     */
    protected ResourceMap getResourceMap() {
        if (resourceMap == null) {
            return ApplicationContext.getInstance().getResourceMap(getViewClass());
        }
        return resourceMap;
    }
    /**
     * Convenience method for getting the resource map of the JHotDraw application framework.
     */
    protected ResourceMap getFrameworkResourceMap() {
        return ApplicationContext.getInstance().getResourceMap(AbstractDocumentOrientedApplication.class);
    }
    
}
