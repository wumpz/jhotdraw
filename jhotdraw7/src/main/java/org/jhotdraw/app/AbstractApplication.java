/*
 * @(#)AbstractApplication.java  1.3  2007-12-24
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.app;

import java.awt.*;
import org.jhotdraw.beans.*;
import org.jhotdraw.gui.Worker;
import org.jhotdraw.util.*;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import java.io.*;
/**
 * AbstractApplication.
 *
 *
 * @author Werner Randelshofer
 * @version 1.3 2007-12-24 Added support for active project. 
 * <br>1.2 2007-11-25 Method Project.clear is now invoked on a worker
 * thread.
 * <br>1.1 2006-05-01 System.exit(0) explicitly in method stop().
 * <br>1.0 October 4, 2005 Created.
 */
public abstract class AbstractApplication extends AbstractBean implements Application {
    private LinkedList<Project> projects = new LinkedList<Project>();
    private Collection unmodifiableDocuments;
    private boolean isEnabled = true;
    protected ResourceBundleUtil labels;
    private ApplicationModel model;
    private LinkedList<File> recentFiles = new LinkedList();
    private final static int maxRecentFilesCount = 10;
    private Preferences prefs;
    private Project activeProject;
    
    /** Creates a new instance. */
    public AbstractApplication() {
    }
    
    public void init() {
        prefs = Preferences.userNodeForPackage((getModel() == null) ? getClass() : getModel().getClass());
        
        int count = prefs.getInt("recentFileCount", 0);
        for (int i=0; i < count; i++) {
            String path = prefs.get("recentFile."+i, null);
            if (path != null) {
                recentFiles.add(new File(path));
            }
        }
        
        if (model != null) {
            model.initApplication(this);
        }
    }
    
    public void start() {
        final Project p = createProject();
        add(p);
        p.setEnabled(false);
        show(p);
        p.execute(new Worker() {
            public Object construct() {
                p.clear();
                return null;
            }
            public void finished(Object result) {
                p.setEnabled(true);
            }
        });
    }
    
    public final Project createProject() {
        Project p = basicCreateProject();
        p.init();
        if (getModel() != null) {
            getModel().initProject(this, p);
        }
        initProjectActions(p);
        return p;
    }
    
    public void setModel(ApplicationModel newValue) {
        ApplicationModel oldValue = model;
        model = newValue;
        firePropertyChange("model",oldValue,newValue);
    }
    public ApplicationModel getModel() {
        return model;
    }
    
    protected Project basicCreateProject() {
        return model.createProject();
    }
    
    /**
     * Sets the active project. Calls deactivate on the previously
     * active project, and then calls activate on the given project.
     * 
     * @param newValue Active project, can be null.
     */
    public void setActiveProject(Project newValue) {
        Project oldValue = activeProject;
        if (activeProject != null) {
            activeProject.deactivate();
        }
        activeProject = newValue;
        if (activeProject != null) {
            activeProject.activate();
        }
        firePropertyChange("activeProject", oldValue, newValue);
    }
    
    /**
     * Gets the active project.
     * 
     * @return The active project, can be null.
     */
    public Project getActiveProject() {
        if (activeProject == null && projects.size() > 0) {
            return projects.getLast();
        }
        return activeProject;
    }
    
    public String getName() {
        return model.getName();
    }
    
    public String getVersion() {
        return model.getVersion();
    }
    
    public String getCopyright() {
        return model.getCopyright();
    }
    protected abstract void initProjectActions(Project p);
    
    
    public void stop() {
        for (Project p : new LinkedList<Project>(projects())) {
            dispose(p);
        }
        System.exit(0);
    }
    
    public void remove(Project p) {
        hide(p);
        int oldCount = projects.size();
        projects.remove(p);
        p.setApplication(null);
        firePropertyChange("projectCount", oldCount, projects.size());
    }
    
    public void add(Project p) {
        if (p.getApplication() != this) {
            int oldCount = projects.size();
            projects.add(p);
            p.setApplication(this);
            firePropertyChange("projectCount", oldCount, projects.size());
        }
    }
    
    public void dispose(Project p) {
        remove(p);
        p.dispose();
    }
    
    public Collection<Project> projects() {
        if (unmodifiableDocuments == null) {
            unmodifiableDocuments = Collections.unmodifiableCollection(projects);
        }
        return unmodifiableDocuments;
    }
    
    public boolean isEnabled() {
        return isEnabled;
    }
    
    public void setEnabled(boolean newValue) {
        boolean oldValue = isEnabled;
        isEnabled = newValue;
        firePropertyChange("enabled", oldValue, newValue);
    }
    
    public Container createContainer() {
        return new JFrame();
    }
    
    public void launch(String[] args) {
        configure(args);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                init();
                start();
            }
        });
    }
    
    protected void initLabels() {
        labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
    }
    
    public void configure(String[] args) {
    }
    
    public java.util.List<File> recentFiles() {
        return Collections.unmodifiableList(recentFiles);
    }
    
    public void clearRecentFiles() {
        java.util.List<File> oldValue = (java.util.List<File>) recentFiles.clone();
        recentFiles.clear();
        prefs.putInt("recentFileCount", recentFiles.size());
        firePropertyChange("recentFiles",
                Collections.unmodifiableList(oldValue),
                Collections.unmodifiableList(recentFiles)
                );
    }
    
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
    
    public void removePalette(Window palette) {
    }
    
    public void addPalette(Window palette) {
    }
    
    public void removeWindow(Window window) {
    }
    
    public void addWindow(Window window, Project p) {
    }
}
