/*
 * @(#)DefaultOSXApplication.java
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

import ch.randelshofer.quaqua.*;
import org.jhotdraw.gui.Worker;
import org.jhotdraw.util.*;
import org.jhotdraw.util.prefs.*;
import java.util.*;
import java.util.prefs.*;
import java.awt.event.*;
import java.beans.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.beans.Disposable;

/**
 * {@code DefaultOSXApplication} handles the lifecycle of {@link View}s using a
 * Mac OS X document interface.
 * <p>
 * An application consists of a screen menu bar and {@code JFrame}s for the
 * {@code View}s. The application also provides floating toolbars and palette
 * windows for the views.
 * <p>
 * The life cycle of the application is tied to the screen menu bar. Choosing
 * the quit action in the screen menu bar quits the application.
 * <p>
 * The screen menu bar has the following standard menus:
 * <pre>
 * "Application-Name" File Window</pre>
 *
 * The first menu, is the <b>application menu</b>. It has the following standard
 * menu items: 
 * <pre>
 *  About "Application-Name" ({@link AboutAction#ID})
 *  -
 *  Preferences... ({@link AbstractPreferencesAction#ID})
 *  -
 *  Services
 *  -
 *  Hide "Application-Name"
 *  Hide Others
 *  Show All
 *  -
 *  Quit "Application-Name" ({@link ExitAction#ID})
 * </pre>
 *
 * The <b>file menu</b> has the following standard menu items:
 * <pre>
 *  New ({@link NewAction#ID}})
 *  Open... ({@link OpenAction#ID}})
 *  Open Recent &gt; "Filename" ({@link OpenRecentAction#ID})
 *  -
 *  Close ({@link CloseAction#ID})
 *  Save ({@link SaveAction#ID})
 *  Save As... ({@link SaveAsAction#ID})
 *  -
 *  Print... ({@link PrintAction#ID})
 * </pre>
 *
 * The <b>window menu</b> has the following standard menu items:
 * <pre>
 *  Minimize ({@link MinimizeAction#ID})
 *  Zoom ({@link MaximizeAction#ID})
 *  -
 *  "Filename" ({@link FocusAction#ID})
 * </pre>
 *
 * The menus provided by the {@code ApplicationModel} are inserted between
 * the file menu and the window menu. In case the application model supplies
 * a menu with the title "Help", it is inserted after the window menu.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DefaultOSXApplication extends AbstractApplication {

    private OSXPaletteHandler paletteHandler;
    private Preferences prefs;
    private LinkedList<Action> paletteActions;

    /** Creates a new instance. */
    public DefaultOSXApplication() {
    }

    @Override
    public void init() {
        ResourceBundleUtil.putPropertyNameModifier("os", "mac", "default");
        super.init();

        prefs = PreferencesUtil.userNodeForPackage((getModel() == null) ? getClass() : getModel().getClass());
        initLookAndFeel();
        paletteHandler = new OSXPaletteHandler(this);

        initLabels();
        initApplicationActions();
        getModel().initApplication(this);
        paletteActions = new LinkedList<Action>();
        initPalettes(paletteActions);
        initScreenMenuBar();
    }

    @Override
    public void launch(String[] args) {
        System.setProperty("apple.awt.graphics.UseQuartz", "false");
        super.launch(args);
    }

    @Override
    public void configure(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.macos.useScreenMenuBar", "true");
    }

    protected void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(QuaquaManager.getLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initApplicationActions() {
        ApplicationModel mo = getModel();
        mo.putAction(AboutAction.ID, new AboutAction(this));
        mo.putAction(ExitAction.ID, new ExitAction(this));
        mo.putAction(OSXDropOnDockAction.ID, new OSXDropOnDockAction(this));

        mo.putAction(NewAction.ID, new NewAction(this));
        mo.putAction(OpenAction.ID, new OpenAction(this));
        mo.putAction(ClearRecentFilesAction.ID, new ClearRecentFilesAction(this));
        mo.putAction(SaveAction.ID, new SaveAction(this));
        mo.putAction(SaveAsAction.ID, new SaveAsAction(this));
        mo.putAction(PrintAction.ID, new PrintAction(this));
        mo.putAction(CloseAction.ID, new CloseAction(this));

        mo.putAction(UndoAction.ID, new UndoAction(this));
        mo.putAction(RedoAction.ID, new RedoAction(this));
        mo.putAction(CutAction.ID, new CutAction());
        mo.putAction(CopyAction.ID, new CopyAction());
        mo.putAction(PasteAction.ID, new PasteAction());
        mo.putAction(DeleteAction.ID, new DeleteAction());
        mo.putAction(DuplicateAction.ID, new DuplicateAction());
        mo.putAction(SelectAllAction.ID, new SelectAllAction());

        mo.putAction(MaximizeAction.ID, new MaximizeAction(this));
        mo.putAction(MinimizeAction.ID, new MinimizeAction(this));
    }

    protected void initViewActions(View p) {
        p.putAction(FocusAction.ID, new FocusAction(p));
    }

    public void dispose(View p) {
        FocusAction a = (FocusAction) p.getAction(FocusAction.ID);
        if (a != null) {
            a.dispose();
        }
        super.dispose(p);
    }

    @Override
    public void addPalette(Window palette) {
        paletteHandler.addPalette(palette);
    }

    @Override
    public void removePalette(Window palette) {
        paletteHandler.removePalette(palette);
    }

    @Override
    public void addWindow(Window window, final View view) {
        if (window instanceof JFrame) {
            ((JFrame) window).setJMenuBar(createMenuBar(view));
        } else if (window instanceof JDialog) {
            // ((JDialog) window).setJMenuBar(createMenuBar(null));
        }

        paletteHandler.add(window, view);
    }

    @Override
    public void removeWindow(Window window) {
        if (window instanceof JFrame) {
            // We explicitly set the JMenuBar to null to facilitate garbage
            // collection
            ((JFrame) window).setJMenuBar(null);
        }
        paletteHandler.remove(window);
    }

    public void show(View view) {
        if (!view.isShowing()) {
            view.setShowing(true);
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            f.setPreferredSize(new Dimension(400, 400));
            updateViewTitle(view, f);

            PreferencesUtil.installFramePrefsHandler(prefs, "view", f);
            Point loc = f.getLocation();
            boolean moved;
            do {
                moved = false;
                for (Iterator i = views().iterator(); i.hasNext();) {
                    View aView = (View) i.next();
                    if (aView != view && aView.isShowing() &&
                            SwingUtilities.getWindowAncestor(aView.getComponent()).
                            getLocation().equals(loc)) {
                        loc.x += 22;
                        loc.y += 22;
                        moved = true;
                        break;
                    }
                }
            } while (moved);
            f.setLocation(loc);

            FrameHandler frameHandler = new FrameHandler(f, view);
            addWindow(f, view);

            f.getContentPane().add(view.getComponent());
            f.setVisible(true);
            view.start();
        }
    }

    /**
     * Updates the title of a view and displays it in the given frame.
     * 
     * @param p The view.
     * @param f The frame.
     */
    protected void updateViewTitle(View p, JFrame f) {
        String title;
        File file = p.getFile();
        if (file == null) {
            title = labels.getString("unnamedFile");
        } else {
            title = file.getName();
        }
        p.setTitle(labels.getFormatted("frame.title", title, getName(), p.getMultipleOpenId()));
        f.setTitle(p.getTitle());

        // Adds a proxy icon for the file to the title bar
        // See http://developer.apple.com/technotes/tn2007/tn2196.html#WINDOW_DOCUMENTFILE
        f.getRootPane().putClientProperty("Window.documentFile", file);
    }

    public void hide(View p) {
        if (p.isShowing()) {
            JFrame f = (JFrame) SwingUtilities.getWindowAncestor(p.getComponent());
            f.setVisible(false);
            removeWindow(f);
            f.remove(p.getComponent());
            f.dispose();
        }
    }

    /**
     * Creates a menu bar.
     *
     * @param p The view for which the menu bar is created. This may be
     * <code>null</code> if the menu bar is attached to an application
     * component, such as the screen menu bar or a floating palette window.
     */
    protected JMenuBar createMenuBar(View p) {
        JMenuBar mb = new JMenuBar();

        // Add the file menu
        mb.add(createFileMenu(p));

        // Add menus provided by the application model except the help menu
        JMenu helpMenu = null;
        String helpMenuText = labels.getString("help.text");
        for (JMenu mm : getModel().createMenus(this, p)) {
            if (mm.getText().equals(helpMenuText)) {
                helpMenu = mm;
            } else {
            mb.add(mm);
            }
        }

        // Add the window menu b
        mb.add(createWindowMenu(p));

        // Add the help menu
        if (helpMenu != null) {
            mb.add(helpMenu);
        }

        return mb;
    }

    protected JMenu createWindowMenu(View view) {
        ApplicationModel model = getModel();

        JMenu m;
        JMenuItem mi;

        m = new JMenu();
        JMenu windowMenu = m;
        labels.configureMenu(m, "window");
        m.addSeparator();

        new WindowMenuHandler(windowMenu, view);

        return m;
    }

    protected JMenu createFileMenu(View view) {
        //ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        ApplicationModel model = getModel();

        JMenu m;
        JMenuItem mi;
        JMenu openRecentMenu;

        m = new JMenu();
        labels.configureMenu(m, "file");
        mi = m.add(model.getAction(NewAction.ID));
        mi.setIcon(null);
        mi = m.add(model.getAction(OpenAction.ID));
        mi.setIcon(null);
        if (model.getAction(OpenDirectoryAction.ID) != null) {
            mi = m.add(model.getAction(OpenDirectoryAction.ID));
            mi.setIcon(null);
        }
        openRecentMenu = new JMenu();
        labels.configureMenu(openRecentMenu, "file.openRecent");
        openRecentMenu.setIcon(null);
        openRecentMenu.add(model.getAction(ClearRecentFilesAction.ID));
        m.add(openRecentMenu);
        m.addSeparator();
        mi = m.add(model.getAction(CloseAction.ID));
        mi.setIcon(null);
        mi = m.add(model.getAction(SaveAction.ID));
        mi.setIcon(null);
        mi = m.add(model.getAction(SaveAsAction.ID));
        mi.setIcon(null);
        if (model.getAction(ExportAction.ID) != null) {
            mi = m.add(model.getAction(ExportAction.ID));
            mi.setIcon(null);
        }
        if (model.getAction(PrintAction.ID) != null) {
            m.addSeparator();
            mi = m.add(model.getAction(PrintAction.ID));
            mi.setIcon(null);
        }

        OpenRecentMenuHandler handler = new OpenRecentMenuHandler(openRecentMenu, view);
        return m;
    }

    protected void initScreenMenuBar() {
        ApplicationModel model = getModel();
        net.roydesign.app.Application mrjapp = net.roydesign.app.Application.getInstance();
        mrjapp.setFramelessJMenuBar(createMenuBar(null));
        paletteHandler.add(SwingUtilities.getWindowAncestor(mrjapp.getFramelessJMenuBar()), null);
        mrjapp.getAboutJMenuItem().setAction(model.getAction(AboutAction.ID));
        mrjapp.getQuitJMenuItem().setAction(model.getAction(ExitAction.ID));
        if (model.getAction(AbstractPreferencesAction.ID) != null) {
            mrjapp.getPreferencesJMenuItem().setAction(model.getAction(AbstractPreferencesAction.ID));
        }
        mrjapp.addOpenDocumentListener(model.getAction(OSXDropOnDockAction.ID));
    }

    protected void initPalettes(final LinkedList<Action> paletteActions) {
        SwingUtilities.invokeLater(new Worker<LinkedList<JFrame>>() {

            public LinkedList<JFrame> construct() {
                LinkedList<JFrame> palettes = new LinkedList<JFrame>();
                LinkedList<JToolBar> toolBars = new LinkedList<JToolBar>(getModel().createToolBars(DefaultOSXApplication.this, null));

                int i = 0;
                int x = 0;
                for (JToolBar tb : toolBars) {
                    i++;
                    tb.setFloatable(false);
                    tb.setOrientation(JToolBar.VERTICAL);
                    tb.setFocusable(false);

                    JFrame d = new JFrame();

                    // Note: Client properties must be set before heavy-weight
                    // peers are created
                    d.getRootPane().putClientProperty("Window.style", "small");
                    d.getRootPane().putClientProperty("Quaqua.RootPane.isVertical", Boolean.FALSE);
                    d.getRootPane().putClientProperty("Quaqua.RootPane.isPalette", Boolean.TRUE);

                    d.setFocusable(false);
                    d.setResizable(false);
                    d.getContentPane().setLayout(new BorderLayout());
                    d.getContentPane().add(tb, BorderLayout.CENTER);
                    d.setAlwaysOnTop(true);
                    d.setUndecorated(true);
                    d.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
                    d.getRootPane().setFont(
                            new Font("Lucida Grande", Font.PLAIN, 11));

                    d.setJMenuBar(createMenuBar(null));

                    d.pack();
                    d.setFocusableWindowState(false);
                    PreferencesUtil.installPalettePrefsHandler(prefs, "toolbar." + i, d, x);
                    x += d.getWidth();

                    paletteActions.add(new OSXTogglePaletteAction(DefaultOSXApplication.this, d, tb.getName()));
                    palettes.add(d);
                }
                return palettes;

            }

            @Override
            protected void done(LinkedList<JFrame> result) {
                @SuppressWarnings("unchecked")
                LinkedList<JFrame> palettes = (LinkedList<JFrame>) result;
                if (palettes != null) {
                    for (JFrame p : palettes) {
                        addPalette(p);
                    }
                    firePropertyChange("paletteCount", 0, palettes.size());
                }
            }
        });
    }

    public boolean isSharingToolsAmongViews() {
        return true;
    }

    public Component getComponent() {
        net.roydesign.app.Application mrjapp = net.roydesign.app.Application.getInstance();
        return mrjapp.getFramelessJMenuBar().getParent();
    }

    /** Updates the menu items in the "Open Recent" file menu. */
    private class OpenRecentMenuHandler implements PropertyChangeListener, Disposable {

        private JMenu openRecentMenu;
        private LinkedList<OpenRecentAction> openRecentActions = new LinkedList<OpenRecentAction>();

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
         *
         * @param openRecentMenu
         */
        protected void updateOpenRecentMenu() {
            if (openRecentMenu.getItemCount() > 0) {
                JMenuItem clearRecentFilesItem = (JMenuItem) openRecentMenu.getItem(
                        openRecentMenu.getItemCount() - 1);
                openRecentMenu.remove(openRecentMenu.getItemCount() - 1);

                // Dispose the actions and the menu items that are currently in the menu
                for (OpenRecentAction action : openRecentActions) {
                    action.dispose();
                }
                openRecentActions.clear();
                openRecentMenu.removeAll();

                // Create new actions and add them to the menu
                for (File f : recentFiles()) {
                    OpenRecentAction action = new OpenRecentAction(DefaultOSXApplication.this, f);
                    openRecentMenu.add(action);
                    openRecentActions.add(action);
                }
                if (recentFiles().size() > 0) {
                    openRecentMenu.addSeparator();
                }

                // Add a separator and the clear recent files item.
                openRecentMenu.add(clearRecentFilesItem);
            }
        }

        public void dispose() {
            removePropertyChangeListener(this);
            // Dispose the actions and the menu items that are currently in the menu
            for (OpenRecentAction action : openRecentActions) {
                action.dispose();
            }
            openRecentActions.clear();
        }
    }

    /** Updates the menu items in the "Open Recent" file menu. */
    private class WindowMenuHandler implements PropertyChangeListener, Disposable {

        private JMenu windowMenu;
        private View view;

        public WindowMenuHandler(JMenu windowMenu, View view) {
            this.windowMenu = windowMenu;
            this.view = view;
            addPropertyChangeListener(this);
            if (view != null) {
                view.addDisposable(this);
            }
            updateWindowMenu();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name == "viewCount" || name == "paletteCount") {
                updateWindowMenu();
            }
        }

        protected void updateWindowMenu() {
            JMenu m = windowMenu;
            JMenuItem mi;

            m.removeAll();
            ApplicationModel model = getModel();
            mi = m.add(model.getAction(MinimizeAction.ID));
            mi.setIcon(null);
            mi = m.add(model.getAction(MaximizeAction.ID));
            mi.setIcon(null);
            m.addSeparator();
            for (Iterator i = views().iterator(); i.hasNext();) {
                View pr = (View) i.next();
                if (pr.getAction(FocusAction.ID) != null) {
                    mi = m.add(pr.getAction(FocusAction.ID));
                }
            }
            if (paletteActions.size() > 0) {
                m.addSeparator();
                for (Action a : paletteActions) {
                    JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(a);
                    Actions.configureJCheckBoxMenuItem(cbmi, a);
                    cbmi.setIcon(null);
                    m.add(cbmi);
                }
            }
        }

        public void dispose() {
            windowMenu.removeAll();
            removePropertyChangeListener(this);
            view = null;
        }
    }

    /** Updates the modifedState of the frame. */
    private class FrameHandler extends WindowAdapter implements PropertyChangeListener, Disposable {

        private JFrame frame;
        private View view;

        public FrameHandler(JFrame frame, View view) {
            this.frame = frame;
            this.view = view;
            view.addPropertyChangeListener(this);
            frame.addWindowListener(this);
            view.addDisposable(this);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name.equals(View.HAS_UNSAVED_CHANGES_PROPERTY)) {
                frame.getRootPane().putClientProperty("windowModified", new Boolean(view.hasUnsavedChanges()));
            } else if (name.equals(View.FILE_PROPERTY)) {
                updateViewTitle(view, frame);
            }
        }

        @Override
        public void windowClosing(final WindowEvent evt) {
            setActiveView(view);
            getModel().getAction(CloseAction.ID).actionPerformed(
                    new ActionEvent(frame, ActionEvent.ACTION_PERFORMED,
                    "windowClosing"));
        }

        @Override
        public void windowClosed(final WindowEvent evt) {
            if (view == getActiveView()) {
                setActiveView(null);
            }
            view.stop();
        }

        public void dispose() {
            frame.removeWindowListener(this);
            view.removePropertyChangeListener(this);
        }
    }
}
