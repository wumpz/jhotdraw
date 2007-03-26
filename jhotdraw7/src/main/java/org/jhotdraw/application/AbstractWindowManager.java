/*
 * @(#)AbstractWindowManager.java  1.0  22. März 2007
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

import application.ApplicationContext;
import application.ResourceMap;
import java.awt.*;
import org.jhotdraw.beans.*;
import org.jhotdraw.util.*;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import java.io.*;
/**
 * AbstractWindowManager.
 * 
 * @author Werner Randelshofer
 * @version 1.0 22. März 2007 Created.
 */
public abstract class AbstractWindowManager extends WindowManager {
    private LinkedList projects = new LinkedList();
    private Collection unmodifiableDocuments;
    private LinkedList<File> recentFiles = new LinkedList();
    private final static int maxRecentFilesCount = 10;
    private Preferences prefs;
    protected ResourceBundleUtil labels;
    
    /** Creates a new instance. */
    public AbstractWindowManager() {
    }
    
    public void preInit() {
        prefs = Preferences.userNodeForPackage((getApplication() == null) ? getClass() : getApplication().getClass());
        
        int count = prefs.getInt("recentFileCount", 0);
        for (int i=0; i < count; i++) {
            String path = prefs.get("recentFile."+i, null);
            if (path != null) {
                recentFiles.add(new File(path));
            }
        }
    }
    public void preStart() {
        
    }
    
    protected void initLabels() {
        labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
    }
    
    public DocumentOrientedApplication getApplication() {
        return (DocumentOrientedApplication) ApplicationContext.getInstance().getApplication();
    }
    
    public void add(Project p) {
        if (p.getWindowManager() != this) {
            int oldCount = projects.size();
            projects.add(p);
            p.setWindowManager(this);
            firePropertyChange("projectCount", oldCount, projects.size());
        }
    }
    
    public void remove(Project p) {
        hide(p);
        int oldCount = projects.size();
        projects.remove(p);
        p.setWindowManager(null);
        firePropertyChange("projectCount", oldCount, projects.size());
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
    
    public void disposeAllProjects() {
        for (Project p : new LinkedList<Project>(projects())) {
            dispose(p);
        }
    }
    
    protected abstract void initProjectActions(Project p);
    
    public java.util.List<File> recentFiles() {
        return Collections.unmodifiableList(recentFiles);
    }
    
    public void clearRecentFiles() {
        java.util.List<File> oldValue = (java.util.List<File>) recentFiles.clone();
        recentFiles.clear();
        Preferences prefs = Preferences.userNodeForPackage(getApplication().getClass());
        prefs.putInt("recentFileCount", recentFiles.size());
        firePropertyChange("recentFiles",
                Collections.unmodifiableList(oldValue),
                Collections.unmodifiableList(recentFiles)
                );
    }
    
    public void addRecentFile(File file) {
        Preferences prefs = Preferences.userNodeForPackage(getApplication().getClass());
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
    
    public String getName() {
        ApplicationContext ac = ApplicationContext.getInstance();
        ResourceMap rm = ac.getResourceMap();
        return rm.getString("Application.title");
    }
}
