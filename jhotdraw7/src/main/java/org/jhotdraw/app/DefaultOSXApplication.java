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

import org.jhotdraw.app.action.app.AbstractPreferencesAction;
import org.jhotdraw.app.action.window.TogglePaletteAction;
import org.jhotdraw.app.action.window.WindowFocusAction;
import org.jhotdraw.app.action.window.WindowMaximizeAction;
import org.jhotdraw.app.action.window.WindowMinimizeAction;
import org.jhotdraw.app.action.file.SaveFileAsAction;
import org.jhotdraw.app.action.file.SaveFileAction;
import org.jhotdraw.app.action.file.PrintFileAction;
import org.jhotdraw.app.action.file.NewFileAction;
import org.jhotdraw.app.action.file.OpenFileAction;
import org.jhotdraw.app.action.file.CloseFileAction;
import org.jhotdraw.app.action.file.OpenDirectoryAction;
import org.jhotdraw.app.action.file.ExportFileAction;
import org.jhotdraw.app.action.app.OpenApplicationAction;
import org.jhotdraw.app.action.app.OpenApplicationFileAction;
import org.jhotdraw.app.action.app.AboutAction;
import org.jhotdraw.app.action.app.ExitAction;
import org.jhotdraw.app.action.app.PrintApplicationFileAction;
import org.jhotdraw.app.action.app.ReOpenApplicationAction;
import org.jhotdraw.app.osx.OSXPaletteHandler;
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
import java.net.URI;
import org.jhotdraw.app.action.ActionUtil;
import org.jhotdraw.app.action.edit.AbstractFindAction;
import org.jhotdraw.app.action.edit.ClearSelectionAction;
import org.jhotdraw.app.action.edit.CopyAction;
import org.jhotdraw.app.action.edit.CutAction;
import org.jhotdraw.app.action.edit.DeleteAction;
import org.jhotdraw.app.action.edit.DuplicateAction;
import org.jhotdraw.app.action.edit.PasteAction;
import org.jhotdraw.app.action.edit.RedoAction;
import org.jhotdraw.app.action.edit.SelectAllAction;
import org.jhotdraw.app.action.edit.UndoAction;
import org.jhotdraw.app.action.file.ClearFileAction;
import org.jhotdraw.app.action.file.ClearRecentFilesMenuAction;
import org.jhotdraw.app.action.file.LoadDirectoryAction;
import org.jhotdraw.app.action.file.LoadFileAction;
import org.jhotdraw.app.action.file.NewWindowAction;
import org.jhotdraw.app.osx.OSXAdapter;
import org.jhotdraw.beans.Disposable;
import org.jhotdraw.net.URIUtil;

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
 *  New ({@link NewFileAction#ID}})
 *  Open... ({@link OpenFileAction#ID}})
 *  Open Recent &gt; "Filename" ({@link OpenRecentAction#ID})
 *  -
 *  Close ({@link CloseFileAction#ID})
 *  Save ({@link SaveFileAction#ID})
 *  Save As... ({@link SaveFileAsAction#ID})
 *  -
 *  Print... ({@link PrintFileAction#ID})
 * </pre>
 *
 * The <b>window menu</b> has the following standard menu items:
 * <pre>
 *  Minimize ({@link WindowMinimizeAction#ID})
 *  Zoom ({@link WindowMaximizeAction#ID})
 *  -
 *  "Filename" ({@link WindowFocusAction#ID})
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
    /** The "invisible" frame is used to hold the frameless menu bar on Mac OS X.
     */
    private JFrame invisibleFrame;

    /** Creates a new instance. */
    public DefaultOSXApplication() {
    }

    @Override
    public void init() {
        super.init();
        ResourceBundleUtil.putPropertyNameModifier("os", "mac", "default");
        prefs = PreferencesUtil.userNodeForPackage((getModel() == null) ? getClass() : getModel().getClass());
        initLookAndFeel();
        paletteHandler = new OSXPaletteHandler(this);

        initLabels();
        ApplicationModel m = getModel();

        paletteActions = new LinkedList<Action>();
        initApplicationActions(getModel());
        getModel().initApplication(this);
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
            UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (UIManager.getString("OptionPane.css") == null) {
            UIManager.put("OptionPane.css", "<head>"
                    + "<style type=\"text/css\">"
                    + "b { font: 13pt \"Dialog\" }"
                    + "p { font: 11pt \"Dialog\"; margin-top: 8px }"
                    + "</style>"
                    + "</head>");
        }
    }

    protected void initViewActions(View p) {
        p.putAction(WindowFocusAction.ID, new WindowFocusAction(p));
    }

    public void dispose(View p) {
        WindowFocusAction a = (WindowFocusAction) p.getAction(WindowFocusAction.ID);
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
            f.setPreferredSize(new Dimension(600, 400));
            f.setSize(f.getPreferredSize());
            updateViewTitle(view, f);

            PreferencesUtil.installFramePrefsHandler(prefs, "view", f);
            Point loc = f.getLocation();
            boolean moved;
            do {
                moved = false;
                for (Iterator i = views().iterator(); i.hasNext();) {
                    View aView = (View) i.next();
                    if (aView != view && aView.isShowing()
                            && SwingUtilities.getWindowAncestor(aView.getComponent()).
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
        URI uri = p.getURI();
        if (uri == null) {
            title = labels.getString("unnamedFile");
        } else {
            title = URIUtil.getName(uri);
        }
        p.setTitle(labels.getFormatted("frame.title", title, getName(), p.getMultipleOpenId()));
        f.setTitle(p.getTitle());

        // Adds a proxy icon for the file to the title bar
        // See http://developer.apple.com/technotes/tn2007/tn2196.html#WINDOW_DOCUMENTFILE
        if (uri != null && uri.getScheme() != null && uri.getScheme().equals("file")) {
            f.getRootPane().putClientProperty("Window.documentFile", new File(uri));
        } else {
            f.getRootPane().putClientProperty("Window.documentFile", null);
        }
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
     */
    protected JMenuBar createMenuBar(View v) {
        JMenuBar mb = new JMenuBar();

        // Get menus from application model
        JMenu fileMenu = null;
        JMenu editMenu = null;
        JMenu helpMenu = null;
        JMenu viewMenu = null;
        JMenu windowMenu = null;
        String fileMenuText = labels.getString("file.text");
        String editMenuText = labels.getString("edit.text");
        String viewMenuText = labels.getString("view.text");
        String windowMenuText = labels.getString("window.text");
        String helpMenuText = labels.getString("help.text");
        for (JMenu mm : getModel().createMenus(this, null)) {
            if (mm.getText().equals(fileMenuText)) {
                fileMenu = mm;
                continue;
            } else if (mm.getText().equals(editMenuText)) {
                editMenu = mm;
                continue;
            } else if (mm.getText().equals(viewMenuText)) {
                viewMenu = mm;
                continue;
            } else if (mm.getText().equals(windowMenuText)) {
                windowMenu = mm;
                continue;
            } else if (mm.getText().equals(helpMenuText)) {
                helpMenu = mm;
                continue;
            }
            mb.add(mm);
        }

        // Create missing standard menus
        if (fileMenu == null) {
            fileMenu = createFileMenu(v);
        }
        if (editMenu == null) {
            editMenu = createEditMenu(v);
        }
        if (viewMenu == null) {
            viewMenu = createViewMenu(v);
        }
        if (windowMenu == null) {
            windowMenu = createWindowMenu(v);
        }
        if (helpMenu == null) {
            helpMenu = createHelpMenu(v);
        }

        // Insert standard menus into menu bar
        if (fileMenu != null) {
            mb.add(fileMenu, 0);
        }
        if (editMenu != null) {
            mb.add(editMenu, Math.min(1, mb.getComponentCount()));
        }
        if (viewMenu != null) {
            mb.add(viewMenu, Math.min(2, mb.getComponentCount()));
        }
        if (windowMenu != null) {
            mb.add(windowMenu);
        }
        if (helpMenu != null) {
            mb.add(helpMenu);
        }

        return mb;
    }

    @Override
    public JMenu createViewMenu(View view) {
        return null;
    }

    @Override
    public JMenu createWindowMenu(View view) {
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

    @Override
    public JMenu createFileMenu(View view) {
        JMenuBar mb = new JMenuBar();
        JMenu m;

        m = new JMenu();
        labels.configureMenu(m, "file");
        addAction(m, ClearFileAction.ID);
        addAction(m, NewFileAction.ID);
        addAction(m, NewWindowAction.ID);

        addAction(m, LoadFileAction.ID);
        addAction(m, OpenFileAction.ID);
        addAction(m, LoadDirectoryAction.ID);
        addAction(m, OpenDirectoryAction.ID);

        if (model.getAction(LoadFileAction.ID) != null ||//
                model.getAction(OpenFileAction.ID) != null ||//
                model.getAction(LoadDirectoryAction.ID) != null ||//
                model.getAction(OpenDirectoryAction.ID) != null) {
            m.add(createOpenRecentFileMenu(null));
        }
        maybeAddSeparator(m);

        addAction(m, CloseFileAction.ID);
        addAction(m, SaveFileAction.ID);
        addAction(m, SaveFileAsAction.ID);
        addAction(m, ExportFileAction.ID);
        addAction(m, PrintFileAction.ID);

        return (m.getPopupMenu().getComponentCount() == 0) ? null : m;
    }

    public JMenu createEditMenu(View p) {

        JMenu m;
        JMenuItem mi;
        Action a;
        m = new JMenu();
        labels.configureMenu(m, "edit");
        addAction(m, UndoAction.ID);
        addAction(m, RedoAction.ID);

        maybeAddSeparator(m);

        addAction(m, CutAction.ID);
        addAction(m, CopyAction.ID);
        addAction(m, PasteAction.ID);
        addAction(m, DuplicateAction.ID);
        addAction(m, DeleteAction.ID);
        maybeAddSeparator(m);
        addAction(m, SelectAllAction.ID);
        addAction(m, ClearSelectionAction.ID);
        maybeAddSeparator(m);
        addAction(m, AbstractFindAction.ID);
        return (m.getPopupMenu().getComponentCount() == 0) ? null : m;
    }

    public JMenu createHelpMenu(View p) {
        return null;
    }

    protected void initScreenMenuBar() {
        ApplicationModel model = getModel();
        setScreenMenuBar(createMenuBar(null));
        paletteHandler.add((JFrame) getComponent(), null);

        Action a;
        if (null != (a = model.getAction(OpenApplicationAction.ID))) {
            OSXAdapter.setOpenApplicationHandler(a);
        }
        if (null != (a = model.getAction(ReOpenApplicationAction.ID))) {
            OSXAdapter.setReOpenApplicationHandler(a);
        }
        if (null != (a = model.getAction(OpenApplicationFileAction.ID))) {
            OSXAdapter.setOpenFileHandler(a);
        }
        if (null != (a = model.getAction(PrintApplicationFileAction.ID))) {
            OSXAdapter.setPrintFileHandler(a);
        }
        if (null != (a = model.getAction(AboutAction.ID))) {
            OSXAdapter.setAboutHandler(a);
        }
        if (null != (a = model.getAction(AbstractPreferencesAction.ID))) {
            OSXAdapter.setPreferencesHandler(a);
        }
        if (null != (a = model.getAction(ExitAction.ID))) {
            OSXAdapter.setQuitHandler(a);
        }
        if (null != (a = model.getAction(OpenApplicationFileAction.ID))) {
            OSXAdapter.setOpenFileHandler(a);
        }
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

                    paletteActions.add(new TogglePaletteAction(DefaultOSXApplication.this, d, tb.getName()));
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

    /** Returns the Frame which holds the frameless JMenuBar.
     */
    public Component getComponent() {
        if (invisibleFrame == null) {
            invisibleFrame = new JFrame();
            invisibleFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            invisibleFrame.setUndecorated(true);
            // Move it way off screen
            invisibleFrame.setLocation(10000, 10000);
            // make the frame transparent and shadowless
            // see https://developer.apple.com/mac/library/technotes/tn2007/tn2196.html
            invisibleFrame.getRootPane().putClientProperty("Window.alpha", 0f);
            invisibleFrame.getRootPane().putClientProperty("Window.shadow", false);
            // make it visible, so the menu bar will show
            invisibleFrame.setVisible(true);
        }
        return invisibleFrame;
    }

    protected void setScreenMenuBar(JMenuBar mb) {
        ((JFrame) getComponent()).setJMenuBar(mb);
        // pack it (without calling pack, the screen menu bar won't work for some reason)
        invisibleFrame.pack();
    }

    protected void initApplicationActions(ApplicationModel m) {
        m.putAction(AboutAction.ID, new AboutAction(this));
        m.putAction(ExitAction.ID, new ExitAction((this)));
        m.putAction(OpenApplicationAction.ID, new OpenApplicationAction(this));
        m.putAction(ReOpenApplicationAction.ID, new ReOpenApplicationAction(this));
        m.putAction(WindowMaximizeAction.ID, new WindowMaximizeAction(this));
        m.putAction(WindowMinimizeAction.ID, new WindowMinimizeAction(this));
        m.putAction(ClearRecentFilesMenuAction.ID, new ClearRecentFilesMenuAction(this));
    }

    /** Updates the menu items in the "Window" menu. */
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
            if (name == VIEW_COUNT_PROPERTY || name == "paletteCount") {
                updateWindowMenu();
            }
        }

        protected void updateWindowMenu() {
            JMenu m = windowMenu;
            JMenuItem mi;

            m.removeAll();
            ApplicationModel model = getModel();
            mi = m.add(model.getAction(WindowMinimizeAction.ID));
            mi.setIcon(null);
            mi = m.add(model.getAction(WindowMaximizeAction.ID));
            mi.setIcon(null);
            m.addSeparator();
            for (Iterator i = views().iterator(); i.hasNext();) {
                View pr = (View) i.next();
                if (pr.getAction(WindowFocusAction.ID) != null) {
                    mi = m.add(pr.getAction(WindowFocusAction.ID));
                }
            }
            if (paletteActions.size() > 0) {
                m.addSeparator();
                for (Action a : paletteActions) {
                    JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(a);
                    ActionUtil.configureJCheckBoxMenuItem(cbmi, a);
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
            } else if (name.equals(View.URI_PROPERTY)) {
                updateViewTitle(view, frame);
            }
        }

        @Override
        public void windowClosing(final WindowEvent evt) {
            setActiveView(view);

            // If there is no means provided to open a new windows, we quit
            // the appliction when the view is closed.
            ApplicationModel m = getModel();
            Action a = (m.getAction(NewFileAction.ID) == null ||//
                    m.getAction(CloseFileAction.ID) == null) ?//
                    m.getAction(ExitAction.ID) : //
                    m.getAction(CloseFileAction.ID);
            if (a != null) {
                a.actionPerformed(
                        new ActionEvent(frame, ActionEvent.ACTION_PERFORMED,
                        "windowClosing"));
            }
        }

        @Override
        public void windowClosed(final WindowEvent evt) {
            if (view == getActiveView()) {
                setActiveView(null);
            }
            view.stop();
        }

        @Override
        public void windowIconified(WindowEvent e) {
            if (view == getActiveView()) {
                setActiveView(null);
            }
            view.stop();
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            view.start();
        }

        public void dispose() {
            frame.removeWindowListener(this);
            view.removePropertyChangeListener(this);
        }
    }

    private class QuitHandler {

        /** This method is invoked, when the user has selected the Quit menu item.
         *
         * @return Returns true if the application has no unsaved changes and
         * can be closed.
         */
        public boolean handleQuit() {
            return false;
        }
    }
}
