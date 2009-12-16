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
 *  Open... ({@link OpenAction#ID}})
 *  Open Recent &gt; "Filename" ({@link OpenRecentAction#ID}})
 *  -
 *  Close ({@link CloseAction#ID})
 *  Save ({@link SaveAction#ID})
 *  Save As... ({@link SaveAsAction#ID})
 *  -
 *  Print... ({@link PrintAction#ID})
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
 *  Minimize ({@link MinimizeAction#ID})
 *  Maximize ({@link MaximizeAction#ID})
 *  -
 *  "Filename" ({@link FocusAction#ID})
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

    protected void initApplicationActions() {
        ApplicationModel mo = getModel();
        mo.putAction(AboutAction.ID, new AboutAction(this));
        mo.putAction(ExitAction.ID, new ExitAction(this));

        mo.putAction(NewFileAction.ID, new NewFileAction(this));
        mo.putAction(OpenAction.ID, new OpenAction(this));
        mo.putAction(ClearRecentFilesAction.ID, new ClearRecentFilesAction(this));
        mo.putAction(SaveAction.ID, new SaveAction(this));
        mo.putAction(SaveAsAction.ID, new SaveAsAction(this));
        mo.putAction(CloseAction.ID, new CloseAction(this));
        mo.putAction(PrintAction.ID, new PrintAction(this));

        mo.putAction(UndoAction.ID, new UndoAction(this));
        mo.putAction(RedoAction.ID, new RedoAction(this));
        mo.putAction(CutAction.ID, new CutAction());
        mo.putAction(CopyAction.ID, new CopyAction());
        mo.putAction(PasteAction.ID, new PasteAction());
        mo.putAction(DeleteAction.ID, new DeleteAction());
        mo.putAction(DuplicateAction.ID, new DuplicateAction());
        mo.putAction(SelectAllAction.ID, new SelectAllAction());
        /*
        model.putAction(MaximizeAction.ID, new MaximizeAction(this));
        model.putAction(MinimizeAction.ID, new MinimizeAction(this));
         */
        mo.putAction(ArrangeAction.VERTICAL_ID, new ArrangeAction(desktopPane, Arrangeable.Arrangement.VERTICAL));
        mo.putAction(ArrangeAction.HORIZONTAL_ID, new ArrangeAction(desktopPane, Arrangeable.Arrangement.HORIZONTAL));
        mo.putAction(ArrangeAction.CASCADE_ID, new ArrangeAction(desktopPane, Arrangeable.Arrangement.CASCADE));
    }

    protected void initViewActions(View p) {
        p.putAction(FocusAction.ID, new FocusAction(p));
    }

    @Override
    public void launch(String[] args) {
        super.launch(args);
    }

    @Override
    public void init() {
        initLookAndFeel();
        super.init();
        prefs = PreferencesUtil.userNodeForPackage((getModel() == null) ? getClass() : getModel().getClass());
        initLabels();

        parentFrame = new JFrame(getName());
        parentFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        desktopPane = new JMDIDesktopPane();
        desktopPane.setTransferHandler(new DropFileTransferHandler());

        scrollPane = new JScrollPane();
        scrollPane.setViewportView(desktopPane);
        toolBarActions = new LinkedList<Action>();


        initApplicationActions();
        getModel().initApplication(this);
        parentFrame.getContentPane().add(
                wrapDesktopPane(scrollPane, toolBarActions));

        parentFrame.addWindowListener(new WindowAdapter() {

            public void windowClosing(final WindowEvent evt) {
                getModel().getAction(ExitAction.ID).actionPerformed(
                        new ActionEvent(parentFrame, ActionEvent.ACTION_PERFORMED, "windowClosing"));
            }
        });
        parentFrame.setJMenuBar(createMenuBar());

        PreferencesUtil.installFramePrefsHandler(prefs, "parentFrame", parentFrame);

        parentFrame.setVisible(true);
    }

    public void configure(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "false");
        System.setProperty("com.apple.macos.useScreenMenuBar", "false");
        System.setProperty("apple.awt.graphics.UseQuartz", "false");
        System.setProperty("swing.aatext", "true");
    }

    protected void initLookAndFeel() {
        try {
            String lafName;
            if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
                JFrame.setDefaultLookAndFeelDecorated(true);
                JDialog.setDefaultLookAndFeelDecorated(true);
                lafName = UIManager.getCrossPlatformLookAndFeelClassName();
            } else {
                lafName = UIManager.getSystemLookAndFeelClassName();
            }
            UIManager.setLookAndFeel(lafName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (UIManager.getString("OptionPane.css") == null) {
            UIManager.put("OptionPane.css", "<head>" +
            "<style type=\"text/css\">" +
            "b { font: 13pt \"Dialog\" }" +
            "p { font: 11pt \"Dialog\"; margin-top: 8px }" +
            "</style>" +
            "</head>");
        }
    }

    public void show(final View p) {
        if (!p.isShowing()) {
            p.setShowing(true);
            final JInternalFrame f = new JInternalFrame();
            f.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
            f.setClosable(true);
            f.setMaximizable(true);
            f.setResizable(true);
            f.setIconifiable(false);
            updateViewTitle(p, f);

            PreferencesUtil.installInternalFramePrefsHandler(prefs, "view", f, desktopPane);
            Point loc = new Point(desktopPane.getInsets().left,desktopPane.getInsets().top);
            boolean moved;
            do {
                moved = false;
                for (Iterator i = views().iterator(); i.hasNext();) {
                    View aView = (View) i.next();
                    if (aView != p && aView.isShowing() &&
                            SwingUtilities.getRootPane(aView.getComponent()).getParent().
                            getLocation().equals(loc)) {
            Point offset = SwingUtilities.convertPoint(SwingUtilities.getRootPane(aView.getComponent()), 0, 0, SwingUtilities.getRootPane(aView.getComponent()).getParent());
                        loc.x += Math.max(offset.x,offset.y);
                        loc.y += Math.max(offset.x,offset.y);
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
                    getModel().getAction(CloseAction.ID).actionPerformed(
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
                    if (name == View.HAS_UNSAVED_CHANGES_PROPERTY ||
                            name == View.URI_PROPERTY) {
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
    protected JMenuBar createMenuBar() {
        JMenuBar mb = new JMenuBar();
        mb.add(createFileMenu());

        JMenu editMenu = null;
        JMenu helpMenu = null;
        String editMenuText = labels.getString("edit.text");
        String helpMenuText = labels.getString("help.text");
        for (JMenu mm : getModel().createMenus(this, null)) {
            if (mm.getText().equals(editMenuText)) {
                editMenu = mm;
            } else if (mm.getText().equals(helpMenuText)) {
                helpMenu = mm;
                continue;
            }
            mb.add(mm);
        }

        // Merge edit menu
        if (editMenu == null) {
            JMenu m = createEditMenu();
            if (m != null) {
                mb.add(m, 1);
            }
        } else {
            JMenu m = createEditMenu();
            if (m != null) {
                editMenu.addSeparator();
                for (Component c : m.getComponents()) {
                    editMenu.add(c);
                }
            }
            mb.add(editMenu);
        }

        mb.add(createWindowMenu());

        // Merge help menu
        if (helpMenu == null) {
            mb.add(createHelpMenu());
        } else {
            JMenu m = createHelpMenu();
            if (m != null) {
                helpMenu.addSeparator();
                for (Component c : m.getComponents()) {
                    helpMenu.add(c);
                }
            }
            mb.add(helpMenu);
        }

        return mb;
    }

    protected JMenu createFileMenu() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        ApplicationModel model = getModel();

        JMenuBar mb = new JMenuBar();
        JMenu m;
        JMenuItem mi;

        m = new JMenu();
        labels.configureMenu(m, "file");
        if (model.getAction(NewFileAction.ID) != null) {
            mi = m.add(model.getAction(NewFileAction.ID));
            mi.setIcon(null);
        }
        if (model.getAction(OpenAction.ID) != null) {
            mi = m.add(model.getAction(OpenAction.ID));
            mi.setIcon(null);
        }
        if (model.getAction(OpenDirectoryAction.ID) != null) {
            mi = m.add(model.getAction(OpenDirectoryAction.ID));
            mi.setIcon(null);
        }
        if (model.getAction(OpenAction.ID) != null || model.getAction(OpenDirectoryAction.ID) != null) {
            m.add(createOpenRecentFileMenu(null));
        }
        if (m.getPopupMenu().getComponentCount() > 0) {
            m.addSeparator();
        }
        if (model.getAction(CloseAction.ID) != null) {
            mi = m.add(model.getAction(CloseAction.ID));
            mi.setIcon(null);
        }
        if (model.getAction(SaveAction.ID) != null) {
            mi = m.add(model.getAction(SaveAction.ID));
            mi.setIcon(null);
        }
        if (model.getAction(SaveAsAction.ID) != null) {
            mi = m.add(model.getAction(SaveAsAction.ID));
            mi.setIcon(null);
        }
        if (model.getAction(ExportAction.ID) != null) {
            mi = m.add(model.getAction(ExportAction.ID));
            mi.setIcon(null);
        }
        if (model.getAction(PrintAction.ID) != null) {
            if (m.getComponentCount() > 0) {
                m.addSeparator();
            }
            mi = m.add(model.getAction(PrintAction.ID));
            mi.setIcon(null);
        }
        if (m.getPopupMenu().getComponentCount() > 0) {
            m.addSeparator();
        }
        mi = m.add(model.getAction(ExitAction.ID));
        mi.setIcon(null);

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

    protected JMenu createWindowMenu() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        ApplicationModel mo = getModel();

        JMenu m;
        JMenuItem mi;

        m = new JMenu();
        JMenu windowMenu = m;
        labels.configureMenu(m, "window");
        m.add(mo.getAction(ArrangeAction.CASCADE_ID));
        m.add(mo.getAction(ArrangeAction.VERTICAL_ID));
        m.add(mo.getAction(ArrangeAction.HORIZONTAL_ID));

        m.addSeparator();
        for (View pr : views()) {
            if (pr.getAction(FocusAction.ID) != null) {
                windowMenu.add(pr.getAction(FocusAction.ID));
            }
        }
        if (toolBarActions.size() > 0) {
            m.addSeparator();
            for (Action a : toolBarActions) {
                JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(a);
                Actions.configureJCheckBoxMenuItem(cbmi, a);

                m.add(cbmi);
            }
        }

        addPropertyChangeListener(new WindowMenuHandler(windowMenu));

        return m;
    }

    protected JMenu createEditMenu() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        ApplicationModel mo = getModel();

        if (mo.getAction(AbstractPreferencesAction.ID) == null) {
            return null;
        }

        JMenu m;
        JMenuItem mi;

        m = new JMenu();
        labels.configureMenu(m, "edit");
        m.add(mo.getAction(AbstractPreferencesAction.ID));
        return m;
    }

    protected JMenu createHelpMenu() {
        ApplicationModel mo = getModel();

        JMenu m;
        JMenuItem mi;

        m = new JMenu();
        labels.configureMenu(m, "help");
        m.add(mo.getAction(AboutAction.ID));
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

            m.add(mo.getAction(ArrangeAction.CASCADE_ID));
            m.add(mo.getAction(ArrangeAction.VERTICAL_ID));
            m.add(mo.getAction(ArrangeAction.HORIZONTAL_ID));

            m.addSeparator();
            for (Iterator i = views().iterator(); i.hasNext();) {
                View pr = (View) i.next();
                if (pr.getAction(FocusAction.ID) != null) {
                    m.add(pr.getAction(FocusAction.ID));
                }
            }
            if (toolBarActions.size() > 0) {
                m.addSeparator();
                for (Action a : toolBarActions) {
                    JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(a);
                    Actions.configureJCheckBoxMenuItem(cbmi, a);
                    m.add(cbmi);
                }
            }
        }
    }

    /** This transfer handler opens a new view for each dropped file. */
    private class DropFileTransferHandler extends TransferHandler {

        @Override
        public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
            for (DataFlavor f : transferFlavors) {
                if (f.isFlavorJavaFileListType()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean importData(JComponent comp, Transferable t) {
            try {
                @SuppressWarnings("unchecked")
                java.util.List<File> files = (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                for (final File f : files) {
                    final View p = createView();
                    p.setEnabled(false);
                    add(p);
                    show(p);
                    p.execute(new Worker() {

                        public Object construct() throws IOException {
                            p.read(f.toURI());
                            return null;
                        }

                        @Override
                        protected void done(Object value) {
                            p.setURI(f.toURI());
                            p.setEnabled(true);
                        }

                        @Override
                        protected void failed(Throwable value) {
                            dispose(p);
                            JOptionPane.showMessageDialog(
                                    getComponent(),
                                    "<html>" + UIManager.getString("OptionPane.css") +
                                    "<b>" + labels.getFormatted("file.open.couldntOpen.message", f.getName()) + "</b><p>" +
                                    value,
                                    "",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });
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
