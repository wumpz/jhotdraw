/*
 * @(#)AbstractApplication.java
 *
 * Copyright (c) 1996-2009 by the original authors of JHotDraw
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jhotdraw.beans.*;
import org.jhotdraw.gui.Worker;
import org.jhotdraw.util.*;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import java.io.*;
import java.net.URI;
import org.jhotdraw.app.action.file.ClearRecentFilesMenuAction;
import org.jhotdraw.app.action.file.LoadDirectoryAction;
import org.jhotdraw.app.action.file.LoadFileAction;
import org.jhotdraw.app.action.file.LoadRecentFileAction;
import org.jhotdraw.app.action.file.OpenFileAction;
import org.jhotdraw.app.action.file.OpenDirectoryAction;
import org.jhotdraw.app.action.file.OpenRecentFileAction;
import org.jhotdraw.util.prefs.PreferencesUtil;

/**
 * This abstract class can be extended to implement an {@link Application}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractApplication extends AbstractBean implements Application {

    private LinkedList<View> views = new LinkedList<View>();
    private Collection<View> unmodifiableViews;
    private boolean isEnabled = true;
    protected ResourceBundleUtil labels;
    protected ApplicationModel model;
    private Preferences prefs;
    private View activeView;
    public final static String VIEW_COUNT_PROPERTY = "viewCount";
    private LinkedList<URI> recentFiles = new LinkedList<URI>();
    private final static int maxRecentFilesCount = 10;

    /** Creates a new instance. */
    public AbstractApplication() {
    }

    public void init() {
        prefs = PreferencesUtil.userNodeForPackage((getModel() == null) ? getClass() : getModel().getClass());
        int count = prefs.getInt("recentFileCount", 0);
        for (int i = 0; i < count; i++) {
            String path = prefs.get("recentFile." + i, null);
            if (path != null) {
                try {
                    recentFiles.add(new URI(path));
                } catch (URISyntaxException ex) {
                    // Silently don't add this URI
                }
            }
        }
    }

    public void start() {
        final View p = createView();
        add(p);
        p.setEnabled(false);
        show(p);
        p.execute(new Worker<Object>() {

            public Object construct() {
                p.clear();
                return null;
            }

            @Override
            public void finished() {
                p.setEnabled(true);
            }
        });
    }

    public final View createView() {
        View p = basicCreateView();
        p.init();
        if (getModel() != null) {
            getModel().initView(this, p);
        }
        return p;
    }

    public void setModel(ApplicationModel newValue) {
        ApplicationModel oldValue = model;
        model = newValue;
        firePropertyChange("model", oldValue, newValue);
    }

    public ApplicationModel getModel() {
        return model;
    }

    protected View basicCreateView() {
        return model.createView();
    }

    /**
     * Sets the active view. Calls deactivate on the previously
     * active view, and then calls activate on the given view.
     * 
     * @param newValue Active view, can be null.
     */
    public void setActiveView(View newValue) {
        View oldValue = activeView;
        if (activeView != null) {
            activeView.deactivate();
        }
        activeView = newValue;
        if (activeView != null) {
            activeView.activate();
        }
        firePropertyChange(ACTIVE_VIEW_PROPERTY, oldValue, newValue);
    }

    /**
     * Gets the active view.
     * 
     * @return The active view can be null.
     */
    public View getActiveView() {
        return activeView;
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

    public void stop() {
        for (View p : new LinkedList<View>(views())) {
            dispose(p);
        }
        System.exit(0);
    }

    public void remove(View p) {
        hide(p);
        if (p == getActiveView()) {
            setActiveView(null);
        }
        int oldCount = views.size();
        views.remove(p);
        p.setApplication(null);
        firePropertyChange(VIEW_COUNT_PROPERTY, oldCount, views.size());
    }

    public void add(View p) {
        if (p.getApplication() != this) {
            int oldCount = views.size();
            views.add(p);
            p.setApplication(this);
            initViewActions(p);
            firePropertyChange(VIEW_COUNT_PROPERTY, oldCount, views.size());
        }
    }

    protected void initViewActions(View p) {
    }

    public void dispose(View view) {
        remove(view);
        view.dispose();
    }

    public Collection<View> views() {
        if (unmodifiableViews == null) {
            unmodifiableViews = Collections.unmodifiableCollection(views);
        }
        return unmodifiableViews;
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
        labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
    }

    public void configure(String[] args) {
    }

    public void removePalette(Window palette) {
    }

    public void addPalette(Window palette) {
    }

    public void removeWindow(Window window) {
    }

    public void addWindow(Window window, View p) {
    }

    /** Adds the specified action as a menu item to the supplied menu. */
    protected void addAction(JMenu m, String actionID) {
        addAction(m, model.getAction(actionID));
    }

    /** Adds the specified action as a menu item to the supplied menu. */
    protected void addAction(JMenu m, Action a) {
        if (a != null) {
            if (m.getClientProperty("needsSeparator") == Boolean.TRUE) {
                m.addSeparator();
                m.putClientProperty("needsSeparator", null);
            }
            JMenuItem mi;
            mi = m.add(a);
            mi.setIcon(null);
            mi.setToolTipText(null);
        }
    }

    /** Adds the specified action as a menu item to the supplied menu. */
    protected void addMenuItem(JMenu m, JMenuItem mi) {
        if (mi != null) {
            if (m.getClientProperty("needsSeparator") == Boolean.TRUE) {
                m.addSeparator();
                m.putClientProperty("needsSeparator", null);
            }
            m.add(mi);
        }
    }

    /** Adds a separator to the supplied menu. The separator will only
    be added, if additional items are added using addAction. */
    protected void maybeAddSeparator(JMenu m) {
        m.putClientProperty("needsSeparator", Boolean.TRUE);
    }

    public java.util.List<URI> getRecentURIs() {
        return Collections.unmodifiableList(recentFiles);
    }

    public void clearRecentURIs() {
        @SuppressWarnings("unchecked")
        java.util.List<URI> oldValue = (java.util.List<URI>) recentFiles.clone();
        recentFiles.clear();
        prefs.putInt("recentFileCount", recentFiles.size());
        firePropertyChange("recentFiles",
                Collections.unmodifiableList(oldValue),
                Collections.unmodifiableList(recentFiles));
    }

    public void addRecentURI(URI uri) {
        @SuppressWarnings("unchecked")
        java.util.List<URI> oldValue = (java.util.List<URI>) recentFiles.clone();
        if (recentFiles.contains(uri)) {
            recentFiles.remove(uri);
        }
        recentFiles.addFirst(uri);
        if (recentFiles.size() > maxRecentFilesCount) {
            recentFiles.removeLast();
        }

        prefs.putInt("recentFileCount", recentFiles.size());
        int i = 0;
        for (URI f : recentFiles) {
            prefs.put("recentFile." + i, f.toString());
            i++;
        }

        firePropertyChange("recentFiles", oldValue, 0);
        firePropertyChange("recentFiles",
                Collections.unmodifiableList(oldValue),
                Collections.unmodifiableList(recentFiles));
    }

    protected JMenu createOpenRecentFileMenu(View view) {
        JMenuItem mi;
        JMenu m;

        m = new JMenu();
        labels.configureMenu(m, //
                (model.getAction(LoadFileAction.ID) != null || //
                model.getAction(LoadDirectoryAction.ID) != null) ?//
                "file.loadRecent" ://
                "file.openRecent"//
                );
        m.setIcon(null);
        m.add(model.getAction(ClearRecentFilesMenuAction.ID));

        OpenRecentMenuHandler handler = new OpenRecentMenuHandler(m, view);
        return m;
    }

    /** Updates the menu items in the "Open Recent" file menu. */
    private class OpenRecentMenuHandler implements PropertyChangeListener, Disposable {

        private JMenu openRecentMenu;
        private LinkedList<Action> openRecentActions = new LinkedList<Action>();

        public OpenRecentMenuHandler(JMenu openRecentMenu, View view) {
            this.openRecentMenu = openRecentMenu;
            if (view != null) {
                view.addDisposable(this);
            }
            updateOpenRecentMenu();
            addPropertyChangeListener(this);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name == "recentFiles") {
                updateOpenRecentMenu();
            }
        }

        /**
         * Updates the "File &gt; Open Recent" menu.
         */
        protected void updateOpenRecentMenu() {
            if (openRecentMenu.getItemCount() > 0) {
                JMenuItem clearRecentFilesItem = (JMenuItem) openRecentMenu.getItem(
                        openRecentMenu.getItemCount() - 1);
                openRecentMenu.remove(openRecentMenu.getItemCount() - 1);

                // Dispose the actions and the menu items that are currently in the menu
                for (Action action : openRecentActions) {
                    if (action instanceof Disposable) {
                        ((Disposable) action).dispose();
                    }
                }
                openRecentActions.clear();
                openRecentMenu.removeAll();

                // Create new actions and add them to the menu
                if (model.getAction(LoadFileAction.ID) != null || //
                        model.getAction(LoadDirectoryAction.ID) != null) {
                    for (URI f : getRecentURIs()) {
                        LoadRecentFileAction action = new LoadRecentFileAction(AbstractApplication.this, f);
                        openRecentMenu.add(action);
                        openRecentActions.add(action);
                    }
                } else {
                    for (URI f : getRecentURIs()) {
                        OpenRecentFileAction action = new OpenRecentFileAction(AbstractApplication.this, f);
                        openRecentMenu.add(action);
                        openRecentActions.add(action);
                    }
                }
                if (getRecentURIs().size() > 0) {
                    openRecentMenu.addSeparator();
                }

                // Add a separator and the clear recent files item.
                openRecentMenu.add(clearRecentFilesItem);
            }
        }

        public void dispose() {
            removePropertyChangeListener(this);
            // Dispose the actions and the menu items that are currently in the menu
            for (Action action : openRecentActions) {
                if (action instanceof Disposable) {
                    ((Disposable) action).dispose();
                }
            }
            openRecentActions.clear();
        }
    }
}
