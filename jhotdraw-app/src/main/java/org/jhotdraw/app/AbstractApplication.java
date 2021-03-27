/*
 * @(#)AbstractApplication.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app;

import java.awt.Container;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.*;
import javax.swing.*;
import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.ApplicationModel;
import org.jhotdraw.api.app.Disposable;
import org.jhotdraw.api.app.View;
import org.jhotdraw.api.gui.URIChooser;
import org.jhotdraw.app.action.file.ClearRecentFilesMenuAction;
import org.jhotdraw.app.action.file.LoadDirectoryAction;
import org.jhotdraw.app.action.file.LoadFileAction;
import org.jhotdraw.app.action.file.LoadRecentFileAction;
import org.jhotdraw.app.action.file.OpenRecentFileAction;
import org.jhotdraw.beans.AbstractBean;
import org.jhotdraw.util.*;
import org.jhotdraw.util.prefs.PreferencesUtil;

/**
 * This abstract class can be extended to implement an {@link Application}.
 * <p>
 * {@code AbstractApplication} supports the command line parameter
 * {@code -open filename} to open views for specific URI's upon launch of
 * the application.
 *
 * <hr>
 * <b>Features</b>
 *
 * <p>
 * <em>Open last URI on launch</em><br>
 * When the application is started, the last opened URI is opened in a view.<br>
 * The following methods participate in this feature:<br>
 * Data suppliers {@link #addRecentURI}, {@link #getRecentURIs}.<br>
 * Behavior: {@link #start}.<br>
 * See {@link org.jhotdraw.app} for a list of participating classes.
 *
 * <p>
 * <em>Allow multiple views for URI</em><br>
 * Allows opening the same URI in multiple views.
 * When the feature is disabled, opening multiple views is prevented, and saving
 * to a file for which a view is currently open is prevented.<br>
 * The following methods participate in this feature:<br>
 * Data suppliers {@link #getViews}.
 * See {@link org.jhotdraw.app} for a list of participating classes.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractApplication extends AbstractBean implements Application {

    private static final long serialVersionUID = 1L;
    private LinkedList<View> views = new LinkedList<>();
    private Collection<View> unmodifiableViews;
    private boolean isEnabled = true;
    protected ResourceBundleUtil labels;
    protected ApplicationModel model;
    private Preferences prefs;
    private View activeView;
    public static final String VIEW_COUNT_PROPERTY = "viewCount";
    public static final String RECENTFILE_COUNT_PROPERTY = "recentFileCount";
    public static final String NEEDS_SEPARATOR_PROPERTY = "needsSeparator";
    public static final String APPLICATION_CONST = "application";
    private LinkedList<URI> recentURIs = new LinkedList<>();
    private static final int MAX_RECENT_FILES_COUNT = 10;
    private ActionMap actionMap;
    private URIChooser openChooser;
    private URIChooser saveChooser;
    private URIChooser importChooser;
    private URIChooser exportChooser;

    /**
     * Creates a new instance.
     */
    protected AbstractApplication() {
    }

    /**
     * Initializes the application after it has been configured.
     */
    @Override
    public void init() {
        prefs = PreferencesUtil.userNodeForPackage((getModel() == null) ? getClass() : getModel().getClass());
        int count = prefs.getInt(RECENTFILE_COUNT_PROPERTY, 0);
        for (int i = 0; i < count; i++) {
            String path = prefs.get("recentFile." + i, null);
            if (path != null) {
                try {
                    recentURIs.add(new URI(path));
                } catch (URISyntaxException ex) {
                    // Silently don't add this URI
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(List<URI> uris) {
        if (uris.isEmpty()) {
            final View v = createView();
            add(v);
            v.setEnabled(false);
            show(v);
            // Set the start view immediately active, so that
            // ApplicationOpenFileAction picks it up on Mac OS X.
            setActiveView(v);
            
            new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    v.clear();
                    return null;
                }

                @Override
                protected void done() {
                    v.setEnabled(true);
                }
            }.execute();
        } else {
            for (final URI uri : uris) {
                final View v = createView();
                add(v);
                v.setEnabled(false);
                show(v);
                // Set the start view immediately active, so that
                // ApplicationOpenFileAction picks it up on Mac OS X.
                setActiveView(v);
                new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        v.read(uri, null);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                            v.setURI(uri);
                        } catch (InterruptedException | ExecutionException ex) {
                            Logger.getLogger(AbstractApplication.class.getName()).log(Level.SEVERE, null, ex);
                            v.clear();
                        }
                        
                        v.setEnabled(true);
                    }
                }.execute();
            }
        }
    }

    @Override
    public final View createView() {
        View v = basicCreateView();
        v.setActionMap(createViewActionMap(v));
        return v;
    }

    @Override
    public void setModel(ApplicationModel newValue) {
        ApplicationModel oldValue = model;
        model = newValue;
        firePropertyChange("model", oldValue, newValue);
    }

    @Override
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
    @Override
    public View getActiveView() {
        return activeView;
    }

    @Override
    public String getName() {
        return model.getName();
    }

    @Override
    public String getVersion() {
        return model.getVersion();
    }

    @Override
    public String getCopyright() {
        return model.getCopyright();
    }

    @Override
    public void stop() {
        for (View p : new LinkedList<>(views())) {
            dispose(p);
        }
    }

    @Override
    public void destroy() {
        stop();
        model.destroyApplication(this);
        System.exit(0);
    }

    @Override
    public void remove(View v) {
        hide(v);
        if (v == getActiveView()) {
            setActiveView(null);
        }
        int oldCount = views.size();
        views.remove(v);
        v.setApplication(null);
        firePropertyChange(VIEW_COUNT_PROPERTY, oldCount, views.size());
    }

    @Override
    public void add(View v) {
        if (v.getApplication() != this) {
            int oldCount = views.size();
            views.add(v);
            v.setApplication(this);
            v.init();
            model.initView(this, v);
            firePropertyChange(VIEW_COUNT_PROPERTY, oldCount, views.size());
        }
    }

    @Override
    public List<View> getViews() {
        return Collections.unmodifiableList(views);
    }

    protected abstract ActionMap createViewActionMap(View p);

    @Override
    public void dispose(View view) {
        remove(view);
        model.destroyView(this, view);
        view.dispose();
    }

    @Override
    public Collection<View> views() {
        if (unmodifiableViews == null) {
            unmodifiableViews = Collections.unmodifiableCollection(views);
        }
        return unmodifiableViews;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean newValue) {
        boolean oldValue = isEnabled;
        isEnabled = newValue;
        firePropertyChange("enabled", oldValue, newValue);
    }

    public Container createContainer() {
        return new JFrame();
    }

    /**
     * Launches the application.
     *
     * @param args This implementation supports the command-line parameter "-open"
     * which can be followed by one or more filenames or URI's.
     */
    @Override
    public void launch(String[] args) {
        configure(args);
        // Get URI's from command line
        final List<URI> uris = getOpenURIsFromMainArgs(args);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                init();
                // Call this right after init.
                model.initApplication(AbstractApplication.this);
                // Get start URIs
                final LinkedList<URI> startUris;
                if (uris.isEmpty()) {
                    startUris = new LinkedList<>();
                    if (model.isOpenLastURIOnLaunch() && !recentURIs.isEmpty()) {
                        startUris.add(recentURIs.getFirst());
                    }
                } else {
                    startUris = new LinkedList<>(uris);
                }
                // Start with start URIs
                start(startUris);
            }
        });
    }

    /**
     * Parses the arguments to the main method and returns a list of URI's
     * for which views need to be opened upon launch of the application.
     * <p>
     * This implementation supports the command-line parameter "-open"
     * which can be followed by one or more filenames or URI's.
     * <p>
     * This method is invoked from the {@code Application.launch} method.
     *
     * @param args Arguments to the main method.
     * @return A list of URI's parsed from the arguments. Returns an empty list
     * if no URI's shall be opened.
     */
    protected List<URI> getOpenURIsFromMainArgs(String[] args) {
        LinkedList<URI> uris = new LinkedList<>();
        for (int i = 0; i < args.length; ++i) {
            if ("-open".equals(args[i])) {
                for (++i; i < args.length; ++i) {
                    if (args[i].startsWith("-")) {
                        break;
                    }
                    URI uri;
                    uri = new File(args[i]).toURI();
                    uris.add(uri);
                }
            }
        }
        return uris;
    }

    protected void initLabels() {
        labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
    }

    /**
     * Configures the application using the provided arguments array.
     */
    @Override
    public void configure(String[] args) {
    }

    @Override
    public void removePalette(Window palette) {
    }

    @Override
    public void addPalette(Window palette) {
    }

    @Override
    public void removeWindow(Window window) {
    }

    @Override
    public void addWindow(Window window, View p) {
    }

    protected Action getAction(View view, String actionID) {
        return getActionMap(view).get(actionID);
    }

    /**
     * Adds the specified action as a menu item to the supplied menu.
     *
     * @param m the menu
     * @param view the view
     * @param actionID the action id
     */
    protected void addAction(JMenu m, View view, String actionID) {
        addAction(m, getAction(view, actionID));
    }

    /**
     * Adds the specified action as a menu item to the supplied menu.
     *
     * @param m the menu
     * @param a the action
     */
    protected void addAction(JMenu m, Action a) {
        if (a != null) {
            if (m.getClientProperty(NEEDS_SEPARATOR_PROPERTY) == Boolean.TRUE) {
                m.addSeparator();
                m.putClientProperty(NEEDS_SEPARATOR_PROPERTY, null);
            }
            JMenuItem mi;
            mi = m.add(a);
            mi.setIcon(null);
            mi.setToolTipText(null);
        }
    }

    /**
     * Adds the specified action as a menu item to the supplied menu.
     *
     * @param m the menu
     * @param mi the menu item
     */
    protected void addMenuItem(JMenu m, JMenuItem mi) {
        if (mi != null) {
            if (m.getClientProperty(NEEDS_SEPARATOR_PROPERTY) == Boolean.TRUE) {
                m.addSeparator();
                m.putClientProperty(NEEDS_SEPARATOR_PROPERTY, null);
            }
            m.add(mi);
        }
    }

    /**
     * Adds a separator to the supplied menu. The separator will only
     * be added, if the previous item is not a separator.
     *
     * @param m the menu
     */
    protected void maybeAddSeparator(JMenu m) {
        JPopupMenu pm = m.getPopupMenu();
        if (pm.getComponentCount() > 0
                && !(pm.getComponent(pm.getComponentCount() - 1) instanceof JSeparator)) {
            m.addSeparator();
        }
    }

    protected void removeTrailingSeparators(JMenu m) {
        JPopupMenu pm = m.getPopupMenu();
        for (int i = pm.getComponentCount() - 1; i > 0 && (pm.getComponent(i) instanceof JSeparator); i--) {
            pm.remove(i);
        }
    }

    @Override
    public java.util.List<URI> getRecentURIs() {
        return Collections.unmodifiableList(recentURIs);
    }

    @Override
    public void clearRecentURIs() {
        @SuppressWarnings("unchecked")
        java.util.List<URI> oldValue = (java.util.List<URI>) recentURIs.clone();
        recentURIs.clear();
        prefs.putInt(RECENTFILE_COUNT_PROPERTY, recentURIs.size());
        firePropertyChange(RECENT_URIS_PROPERTY,
                Collections.unmodifiableList(oldValue),
                Collections.unmodifiableList(recentURIs));
    }

    @Override
    public void addRecentURI(URI uri) {
        @SuppressWarnings("unchecked")
        java.util.List<URI> oldValue = (java.util.List<URI>) recentURIs.clone();
        if (recentURIs.contains(uri)) {
            recentURIs.remove(uri);
        }
        recentURIs.addFirst(uri);
        if (recentURIs.size() > MAX_RECENT_FILES_COUNT) {
            recentURIs.removeLast();
        }
        prefs.putInt(RECENTFILE_COUNT_PROPERTY, recentURIs.size());
        int i = 0;
        for (URI f : recentURIs) {
            prefs.put("recentFile." + i, f.toString());
            i++;
        }
        firePropertyChange(RECENT_URIS_PROPERTY, oldValue, 0);
        firePropertyChange(RECENT_URIS_PROPERTY,
                Collections.unmodifiableList(oldValue),
                Collections.unmodifiableList(recentURIs));
    }

    protected JMenu createOpenRecentFileMenu(View view) {
        JMenuItem mi;
        JMenu m;
        m = new JMenu();
        labels.configureMenu(m,
                (getAction(view, LoadFileAction.ID) != null
                || getAction(view, LoadDirectoryAction.ID) != null)
                ? "file.loadRecent"
                : "file.openRecent"
        );
        m.setIcon(null);
        m.add(getAction(view, ClearRecentFilesMenuAction.ID));
        new OpenRecentMenuHandler(m, view);
        return m;
    }

    /**
     * Updates the menu items in the "Open Recent" file menu.
     */
    private class OpenRecentMenuHandler implements PropertyChangeListener, Disposable {

        private JMenu openRecentMenu;
        private LinkedList<Action> openRecentActions = new LinkedList<>();
        private View view;

        public OpenRecentMenuHandler(JMenu openRecentMenu, View view) {
            this.openRecentMenu = openRecentMenu;
            this.view = view;
            if (view != null) {
                view.addDisposable(this);
            }
            updateOpenRecentMenu();
            addPropertyChangeListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if ((name == null && RECENT_URIS_PROPERTY == null) || (name != null && name.equals(RECENT_URIS_PROPERTY))) {
                updateOpenRecentMenu();
            }
        }

        /**
         * Updates the "File &gt; Open Recent" menu.
         */
        protected void updateOpenRecentMenu() {
            if (openRecentMenu.getItemCount() > 0) {
                JMenuItem clearRecentFilesItem = openRecentMenu.getItem(
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
                if (getAction(view, LoadFileAction.ID) != null
                        || getAction(view, LoadDirectoryAction.ID) != null) {
                    for (URI f : getRecentURIs()) {
                        LoadRecentFileAction action = new LoadRecentFileAction(AbstractApplication.this, view, f);
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

        @Override
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

    /**
     * Gets an open chooser for the specified view or for the application.
     * <p>
     * If the chooser has an accessory panel, it can access the view using
     * the client property "view" on the component of the chooser. It can
     * access the application using the client property "application" on the
     * chooser.
     * </p>
     *
     * @param v The view. Specify null to get a chooser for the application.
     */
    @Override
    public URIChooser getOpenChooser(View v) {
        if (v == null) {
            if (openChooser == null) {
                openChooser = model.createOpenChooser(this, null);
                openChooser.getComponent().putClientProperty(APPLICATION_CONST, this);
                List<URI> ruris = getRecentURIs();
                if (ruris.size() > 0) {
                    try {
                        openChooser.setSelectedURI(ruris.get(0));
                    } catch (IllegalArgumentException e) {
                        // Ignore illegal values in recent URI list.
                    }
                }
            }
            return openChooser;
        } else {
            URIChooser chooser = (URIChooser) v.getComponent().getClientProperty("openChooser");
            if (chooser == null) {
                chooser = model.createOpenChooser(this, v);
                v.getComponent().putClientProperty("openChooser", chooser);
                chooser.getComponent().putClientProperty("view", v);
                chooser.getComponent().putClientProperty(APPLICATION_CONST, this);
                List<URI> ruris = getRecentURIs();
                if (ruris.size() > 0) {
                    try {
                        chooser.setSelectedURI(ruris.get(0));
                    } catch (IllegalArgumentException e) {
                        // Ignore illegal values in recent URI list.
                    }
                }
            }
            return chooser;
        }
    }

    /**
     * Gets a save chooser for the specified view or for the application.
     * <p>
     * If the chooser has an accessory panel, it can access the view using
     * the client property "view" on the component of the chooser. It can
     * access the application using the client property "application" on the
     * chooser.
     * </p>
     *
     * @param v The view. Specify null to get a chooser for the application.
     */
    @Override
    public URIChooser getSaveChooser(View v) {
        if (v == null) {
            if (saveChooser == null) {
                saveChooser = model.createSaveChooser(this, null);
                saveChooser.getComponent().putClientProperty(APPLICATION_CONST, this);
            }
            return saveChooser;
        } else {
            URIChooser chooser = (URIChooser) v.getComponent().getClientProperty("saveChooser");
            if (chooser == null) {
                chooser = model.createSaveChooser(this, v);
                v.getComponent().putClientProperty("saveChooser", chooser);
                chooser.getComponent().putClientProperty("view", v);
                chooser.getComponent().putClientProperty(APPLICATION_CONST, this);
                try {
                    chooser.setSelectedURI(v.getURI());
                } catch (IllegalArgumentException e) {
                    // ignore illegal values
                }
            }
            return chooser;
        }
    }

    /**
     * Gets an import chooser for the specified view or for the application.
     * <p>
     * If the chooser has an accessory panel, it can access the view using
     * the client property "view" on the component of the chooser. It can
     * access the application using the client property "application" on the
     * chooser.
     * </p>
     *
     * @param v The view. Specify null to get a chooser for the application.
     */
    @Override
    public URIChooser getImportChooser(View v) {
        if (v == null) {
            if (importChooser == null) {
                importChooser = model.createImportChooser(this, null);
                importChooser.getComponent().putClientProperty(APPLICATION_CONST, this);
            }
            return importChooser;
        } else {
            URIChooser chooser = (URIChooser) v.getComponent().getClientProperty("importChooser");
            if (chooser == null) {
                chooser = model.createImportChooser(this, v);
                v.getComponent().putClientProperty("importChooser", chooser);
                chooser.getComponent().putClientProperty("view", v);
                chooser.getComponent().putClientProperty(APPLICATION_CONST, this);
            }
            return chooser;
        }
    }

    /**
     * Gets an export chooser for the specified view or for the application.
     * <p>
     * If the chooser has an accessory panel, it can access the view using
     * the client property "view" on the component of the chooser. It can
     * access the application using the client property "application" on the
     * chooser.
     * </p>
     *
     * @param v The view. Specify null to get a chooser for the application.
     */
    @Override
    public URIChooser getExportChooser(View v) {
        if (v == null) {
            if (exportChooser == null) {
                exportChooser = model.createExportChooser(this, null);
                exportChooser.getComponent().putClientProperty(APPLICATION_CONST, this);
            }
            return exportChooser;
        } else {
            URIChooser chooser = (URIChooser) v.getComponent().getClientProperty("exportChooser");
            if (chooser == null) {
                chooser = model.createExportChooser(this, v);
                v.getComponent().putClientProperty("exportChooser", chooser);
                chooser.getComponent().putClientProperty("view", v);
                chooser.getComponent().putClientProperty(APPLICATION_CONST, this);
            }
            return chooser;
        }
    }

    /**
     * Sets the application-wide action map.
     *
     * @param m the map
     */
    public void setActionMap(ActionMap m) {
        actionMap = m;
    }

    /**
     * Gets the action map.
     *
     * @return the map
     */
    @Override
    public ActionMap getActionMap(View v) {
        return (v == null) ? actionMap : v.getActionMap();
    }
}
