/*
 * @(#)DefaultMDIApplication.java
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
import org.jhotdraw.app.action.window.ToggleToolBarAction;
import org.jhotdraw.app.action.window.WindowFocusAction;
import org.jhotdraw.app.action.window.WindowArrangeAction;
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
import org.jhotdraw.app.action.app.AboutAction;
import org.jhotdraw.app.action.app.OpenApplicationFileAction;
import org.jhotdraw.app.action.app.ExitAction;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jhotdraw.gui.*;
import org.jhotdraw.util.*;
import org.jhotdraw.util.prefs.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import javax.swing.event.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.app.action.file.ClearFileAction;
import org.jhotdraw.app.action.file.ClearRecentFilesMenuAction;
import org.jhotdraw.app.action.file.LoadDirectoryAction;
import org.jhotdraw.app.action.file.LoadFileAction;
import org.jhotdraw.app.action.file.NewWindowAction;
import org.jhotdraw.net.URIUtil;

/**
 * {@code DefaultMDIApplication} handles the lifecycle of {@link FileView}s
 * using a multiple document interface (MDI).
 * <p>
 * An application consists of a parent {@code JFrame} which holds a {@code JDesktopPane}.
 * The views reside in {@code JInternalFrame}s inside of the {@code JDesktopPane}. 
 * The parent frame also contains a menu bar, toolbars and palette windows for
 * the views.
 * <p>
 * The life cycle of the application is tied to the parent {@code JFrame}.
 * Closing the parent {@code JFrame} quits the application.
 *
 * The parent frame has the following standard menus:
 * <pre>
 * File Edit Window Help</pre>
 *
 * The <b>file menu</b> has the following standard menu items:
 * <pre>
 *  New ({@link NewFileAction#ID}})
 *  Open... ({@link OpenFileAction#ID}})
 *  Open Recent &gt; "Filename" ({@link OpenRecentAction#ID}})
 *  -
 *  Close ({@link CloseFileAction#ID})
 *  Save ({@link SaveFileAction#ID})
 *  Save As... ({@link SaveFileAsAction#ID})
 *  -
 *  Print... ({@link PrintFileAction#ID})
 *  -
 *  Exit ({@link ExitAction#ID})
 * </pre>
 *
 * The <b>edit menu</b> has the following standard menu items:
 * <pre>
 *  Settings ({@link AbstractPreferencesAction#ID})
 * </pre>
 *
 * The <b>window menu</b> has the following standard menu items:
 * <pre>
 *  Minimize ({@link WindowMinimizeAction#ID})
 *  Maximize ({@link WindowMaximizeAction#ID})
 *  -
 *  "Filename" ({@link WindowFocusAction#ID})
 * </pre>
 *
 * The <b>help menu</b> has the following standard menu items:
 * <pre>
 *  About ({@link AboutAction#ID})
 * </pre>
 *
 * The menus provided by the {@code ApplicationModel} are inserted between
 * the file menu and the window menu. In case the application model supplies
 * a menu with the title "Edit" or "Help", the standard menu items are added
 * with a seperator to the end of the menu.
 *
 *
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class DefaultMDIApplication extends AbstractApplication {

    private JFrame parentFrame;
    private JScrollPane scrollPane;
    private JMDIDesktopPane desktopPane;
    private Preferences prefs;
    private LinkedList<Action> toolBarActions;

    /** Creates a new instance. */
    public DefaultMDIApplication() {
    }

    @Override
    public void init() {
        super.init();
        initLookAndFeel();
        prefs = PreferencesUtil.userNodeForPackage((getModel() == null) ? getClass() : getModel().getClass());
        initLabels();

        parentFrame = new JFrame(getName());
        parentFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        parentFrame.setPreferredSize(new Dimension(600, 400));

        desktopPane = new JMDIDesktopPane();
        desktopPane.setTransferHandler(new DropFileTransferHandler());

        scrollPane = new JScrollPane();
        scrollPane.setViewportView(desktopPane);
        toolBarActions = new LinkedList<Action>();

        initApplicationActions(getModel());
        getModel().initApplication(this);
        parentFrame.getContentPane().add(
                wrapDesktopPane(scrollPane, toolBarActions));

        parentFrame.addWindowListener(new WindowAdapter() {

            public void windowClosing(final WindowEvent evt) {
                getModel().getAction(ExitAction.ID).actionPerformed(
                        new ActionEvent(parentFrame, ActionEvent.ACTION_PERFORMED, "windowClosing"));
            }
        });
        parentFrame.setJMenuBar(createMenuBar(null));

        PreferencesUtil.installFramePrefsHandler(prefs, "parentFrame", parentFrame);

        parentFrame.setVisible(true);
    }

    protected void initApplicationActions(ApplicationModel mo) {
        mo.putAction(AboutAction.ID, new AboutAction(this));
        mo.putAction(ExitAction.ID, new ExitAction(this));
        mo.putAction(ClearRecentFilesMenuAction.ID, new ClearRecentFilesMenuAction(this));

        mo.putAction(WindowMaximizeAction.ID, new WindowMaximizeAction(this));
        mo.putAction(WindowMinimizeAction.ID, new WindowMinimizeAction(this));

        mo.putAction(WindowArrangeAction.VERTICAL_ID, new WindowArrangeAction(desktopPane, Arrangeable.Arrangement.VERTICAL));
        mo.putAction(WindowArrangeAction.HORIZONTAL_ID, new WindowArrangeAction(desktopPane, Arrangeable.Arrangement.HORIZONTAL));
        mo.putAction(WindowArrangeAction.CASCADE_ID, new WindowArrangeAction(desktopPane, Arrangeable.Arrangement.CASCADE));
    }

    @Override
    protected void initViewActions(View p) {
        p.putAction(WindowFocusAction.ID, new WindowFocusAction(p));
    }

    @Override
    public void launch(String[] args) {
        super.launch(args);
    }

    public void configure(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "false");
        System.setProperty("com.apple.macos.useScreenMenuBar", "false");
        System.setProperty("apple.awt.graphics.UseQuartz", "false");
        System.setProperty("swing.aatext", "true");
    }

    protected void initLookAndFeel() {
        try {
            String lafName = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(lafName);
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

    public void show(final View p) {
        if (!p.isShowing()) {
            p.setShowing(true);
            final JInternalFrame f = new JInternalFrame();
            f.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
            f.setClosable(getModel().getAction(CloseFileAction.ID) != null);
            f.setMaximizable(true);
            f.setResizable(true);
            f.setIconifiable(false);
            f.setPreferredSize(new Dimension(400, 400));
            updateViewTitle(p, f);

            PreferencesUtil.installInternalFramePrefsHandler(prefs, "view", f, desktopPane);
            Point loc = new Point(desktopPane.getInsets().left, desktopPane.getInsets().top);
            boolean moved;
            do {
                moved = false;
                for (Iterator i = views().iterator(); i.hasNext();) {
                    View aView = (View) i.next();
                    if (aView != p && aView.isShowing()
                            && SwingUtilities.getRootPane(aView.getComponent()).getParent().
                            getLocation().equals(loc)) {
                        Point offset = SwingUtilities.convertPoint(SwingUtilities.getRootPane(aView.getComponent()), 0, 0, SwingUtilities.getRootPane(aView.getComponent()).getParent());
                        loc.x += Math.max(offset.x, offset.y);
                        loc.y += Math.max(offset.x, offset.y);
                        moved = true;
                        break;
                    }
                }
            } while (moved);
            f.setLocation(loc);

            //paletteHandler.add(f, v);

            f.addInternalFrameListener(new InternalFrameAdapter() {

                @Override
                public void internalFrameClosing(final InternalFrameEvent evt) {
                    getModel().getAction(CloseFileAction.ID).actionPerformed(
                            new ActionEvent(f, ActionEvent.ACTION_PERFORMED,
                            "windowClosing"));
                }

                @Override
                public void internalFrameClosed(final InternalFrameEvent evt) {
                    if (p == getActiveView()) {
                        setActiveView(null);
                    }
                    p.stop();
                }
            });

            p.addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    String name = evt.getPropertyName();
                    if (name == View.HAS_UNSAVED_CHANGES_PROPERTY
                            || name == View.URI_PROPERTY) {
                        updateViewTitle(p, f);
                    }
                }
            });

            f.addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    String name = evt.getPropertyName();
                    if (name.equals("selected")) {
                        if (evt.getNewValue().equals(Boolean.TRUE)) {
                            setActiveView(p);
                        }
                    }
                }
            });

            //f.setJMenuBar(createMenuBar(v));

            f.getContentPane().add(p.getComponent());
            f.setVisible(true);
            desktopPane.add(f);
            if (desktopPane.getComponentCount() == 1) {
                try {
                    f.setMaximum(true);
                } catch (PropertyVetoException ex) {
                    // ignore veto
                }
            }
            f.toFront();
            try {
                f.setSelected(true);
            } catch (PropertyVetoException e) {
                // Don't care.
            }
            p.getComponent().requestFocusInWindow();
            p.start();
        }
    }

    public void hide(View p) {
        if (p.isShowing()) {
            JInternalFrame f = (JInternalFrame) SwingUtilities.getRootPane(p.getComponent()).getParent();
            f.setVisible(false);
            f.remove(p.getComponent());

            // Setting the JMenuBar to null triggers action disposal of
            // actions in the openRecentMenu and the windowMenu. This is
            // important to prevent memory leaks.
            f.setJMenuBar(null);

            desktopPane.remove(f);
            f.dispose();
        }
    }

    public boolean isSharingToolsAmongViews() {
        return true;
    }

    public Component getComponent() {
        return parentFrame;
    }

    /**
     * Returns the wrapped desktop pane.
     */
    protected Component wrapDesktopPane(Component c, LinkedList<Action> toolBarActions) {
        if (getModel() != null) {
            int id = 0;
            for (JToolBar tb : new ReversedList<JToolBar>(getModel().createToolBars(this, null))) {
                id++;
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(tb, BorderLayout.NORTH);
                panel.add(c, BorderLayout.CENTER);
                c = panel;
                PreferencesUtil.installToolBarPrefsHandler(prefs, "toolbar." + id, tb);
                toolBarActions.addFirst(new ToggleToolBarAction(tb, tb.getName()));
            }
        }
        return c;
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

        maybeAddSeparator(m);
        addAction(m, ExitAction.ID);

        return m;
    }

    /**
     * Updates the title of a view and displays it in the given frame.
     *
     * @param v The view.
     * @param f The frame.
     */
    protected void updateViewTitle(View v, JInternalFrame f) {
        URI uri = v.getURI();
        String title;
        if (uri == null) {
            title = labels.getString("unnamedFile");
        } else {
            title = URIUtil.getName(uri);
        }
        if (v.hasUnsavedChanges()) {
            title += "*";
        }
        v.setTitle(labels.getFormatted("internalFrame.title", title, getName(), v.getMultipleOpenId()));
        f.setTitle(v.getTitle());
    }

    public JMenu createViewMenu(View v) {
        return null;
    }

    public JMenu createWindowMenu(View v) {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        ApplicationModel mo = getModel();

        JMenu m;
        JMenuItem mi;

        m = new JMenu();
        JMenu windowMenu = m;
        labels.configureMenu(m, "window");
        addAction(m, WindowArrangeAction.CASCADE_ID);
        addAction(m, WindowArrangeAction.VERTICAL_ID);
        addAction(m, WindowArrangeAction.HORIZONTAL_ID);

       maybeAddSeparator(m);
        for (View pr : views()) {
            if (pr.getAction(WindowFocusAction.ID) != null) {
                addAction(m, pr.getAction(WindowFocusAction.ID));
            }
        }
        if (toolBarActions.size() > 0) {
       maybeAddSeparator(m);
            for (Action a : toolBarActions) {
                JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(a);
                ActionUtil.configureJCheckBoxMenuItem(cbmi, a);
                addMenuItem(m, cbmi);
            }
        }

        addPropertyChangeListener(new WindowMenuHandler(windowMenu));

        return m;
    }

    @Override
    public JMenu createEditMenu(View view) {

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
        maybeAddSeparator(m);
        addAction(m, AbstractPreferencesAction.ID);
        return (m.getPopupMenu().getComponentCount() == 0) ? null : m;
    }

    public JMenu createHelpMenu(View v) {
        ApplicationModel mo = getModel();

        JMenu m;
        JMenuItem mi;

        m = new JMenu();
        labels.configureMenu(m, "help");
        addAction(m, AboutAction.ID);
        return m;
    }

    /** Updates the menu items in the "Window" menu. */
    private class WindowMenuHandler implements PropertyChangeListener {

        private JMenu windowMenu;

        public WindowMenuHandler(JMenu windowMenu) {
            this.windowMenu = windowMenu;
            addPropertyChangeListener(this);
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
            ApplicationModel mo = getModel();
            m.removeAll();

            m.add(mo.getAction(WindowArrangeAction.CASCADE_ID));
            m.add(mo.getAction(WindowArrangeAction.VERTICAL_ID));
            m.add(mo.getAction(WindowArrangeAction.HORIZONTAL_ID));

            m.addSeparator();
            for (Iterator i = views().iterator(); i.hasNext();) {
                View pr = (View) i.next();
                if (pr.getAction(WindowFocusAction.ID) != null) {
                    m.add(pr.getAction(WindowFocusAction.ID));
                }
            }
            if (toolBarActions.size() > 0) {
                m.addSeparator();
                for (Action a : toolBarActions) {
                    JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(a);
                    ActionUtil.configureJCheckBoxMenuItem(cbmi, a);
                    m.add(cbmi);
                }
            }
        }
    }

    /** This transfer handler opens a new view for each dropped file. */
    private class DropFileTransferHandler extends TransferHandler {

        @Override
        public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
            Action a = getModel().getAction(OpenApplicationFileAction.ID);
            if (a == null) {
                return false;
            }
            for (DataFlavor f : transferFlavors) {
                if (f.isFlavorJavaFileListType()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean importData(JComponent comp, Transferable t) {
            Action a = getModel().getAction(OpenApplicationFileAction.ID);
            if (a == null) {
                return false;
            }
            try {
                @SuppressWarnings("unchecked")
                java.util.List<File> files = (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                for (final File f : files) {
                    a.actionPerformed(new ActionEvent(desktopPane, ActionEvent.ACTION_PERFORMED, f.toString()));
                }
                return true;
            } catch (UnsupportedFlavorException ex) {
                return false;
            } catch (IOException ex) {
                return false;
            }
        }
    }
}
