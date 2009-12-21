/*
 * @(#)DefaultSDIApplication.java
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
import org.jhotdraw.app.action.window.ToggleVisibleAction;
import org.jhotdraw.app.action.window.WindowMaximizeAction;
import org.jhotdraw.app.action.window.WindowMinimizeAction;
import org.jhotdraw.app.action.file.SaveFileAsAction;
import org.jhotdraw.app.action.file.SaveFileAction;
import org.jhotdraw.app.action.file.LoadDirectoryAction;
import org.jhotdraw.app.action.file.PrintFileAction;
import org.jhotdraw.app.action.file.NewFileAction;
import org.jhotdraw.app.action.file.ClearFileAction;
import org.jhotdraw.app.action.file.OpenFileAction;
import org.jhotdraw.app.action.file.CloseFileAction;
import org.jhotdraw.app.action.file.LoadFileAction;
import org.jhotdraw.app.action.file.OpenDirectoryAction;
import org.jhotdraw.app.action.file.ExportFileAction;
import org.jhotdraw.app.action.app.AboutAction;
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
import org.jhotdraw.app.action.*;
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
import org.jhotdraw.app.action.file.ClearRecentFilesMenuAction;
import org.jhotdraw.app.action.file.NewWindowAction;
import org.jhotdraw.net.URIUtil;

/**
 * {@code DefaultSDIApplication} handles the lifecycle of a {@link View}s
 * using a single document interface (SDI).
 * <p>
 * An application consists of independent {@code JFrame}s for each view.
 * Each JFrame contains a menu bar, toolbars and palette bars for
 * the views.
 * <p>
 * The life cycle of the application is tied to the {@code JFrame}s. Closing the
 * last {@code JFrame} quits the application.

 * DefaultSDIApplication handles the life cycle of a single document window
 * being presented in a JFrame. The JFrame provides all the functionality needed
 * to work with the document, such as a menu bar, tool bars and palette windows.
 * <p>
 * The life cycle of the application is tied to the JFrame. Closing the JFrame
 * quits the application.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DefaultSDIApplication extends AbstractApplication {

    private Preferences prefs;

    /** Creates a new instance. */
    public DefaultSDIApplication() {
    }

    @Override
    public void launch(String[] args) {
        System.setProperty("apple.awt.graphics.UseQuartz", "false");
        super.launch(args);
    }

    @Override
    public void init() {
        super.init();
        initLookAndFeel();
        prefs = PreferencesUtil.userNodeForPackage((getModel() == null) ? getClass() : getModel().getClass());
        initLabels();
        initApplicationActions(getModel());
        getModel().initApplication(this);
    }

    @Override
    public void remove(View p) {
        super.remove(p);
        if (views().size() == 0) {
            stop();
        }
    }

    @Override
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

    @SuppressWarnings("unchecked")
    public void show(final View p) {
        if (!p.isShowing()) {
            p.setShowing(true);
            final JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            updateViewTitle(p, f);

            JPanel panel = (JPanel) wrapViewComponent(p);
            f.add(panel);
            f.setMinimumSize(new Dimension(200, 200));
            f.setPreferredSize(new Dimension(600, 400));
            f.setSize(f.getPreferredSize());
            f.setJMenuBar(createMenuBar(p));

            PreferencesUtil.installFramePrefsHandler(prefs, "view", f);
            Point loc = f.getLocation();
            boolean moved;
            do {
                moved = false;
                for (Iterator i = views().iterator(); i.hasNext();) {
                    View aView = (View) i.next();
                    if (aView != p
                            && SwingUtilities.getWindowAncestor(aView.getComponent()) != null
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

            f.addWindowListener(new WindowAdapter() {

                public void windowClosing(final WindowEvent evt) {
                    getModel().getAction(CloseFileAction.ID).actionPerformed(
                            new ActionEvent(f, ActionEvent.ACTION_PERFORMED,
                            "windowClosing"));
                }

                @Override
                public void windowClosed(final WindowEvent evt) {
                    if (p == getActiveView()) {
                        setActiveView(null);
                    }
                    p.stop();
                }

                public void windowActivated(WindowEvent e) {
                    setActiveView(p);
                }
            });

            p.addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    String name = evt.getPropertyName();
                    if (name.equals(View.HAS_UNSAVED_CHANGES_PROPERTY)
                            || name.equals(View.URI_PROPERTY)
                            || name.equals(View.MULTIPLE_OPEN_ID_PROPERTY)) {
                        updateViewTitle(p, f);
                    }
                }
            });

            f.setVisible(true);
            p.start();
        }
    }

    /**
     * Returns the view component. Eventually wraps it into
     * another component in order to provide additional functionality.
     */
    protected Component wrapViewComponent(View p) {
        JComponent c = p.getComponent();
        if (getModel() != null) {
            LinkedList<Action> toolBarActions = new LinkedList<Action>();

            int id = 0;
            for (JToolBar tb : new ReversedList<JToolBar>(getModel().createToolBars(this, p))) {
                id++;
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(tb, BorderLayout.NORTH);
                panel.add(c, BorderLayout.CENTER);
                c = panel;
                PreferencesUtil.installToolBarPrefsHandler(prefs, "toolbar." + id, tb);
                toolBarActions.addFirst(new ToggleVisibleAction(tb, tb.getName()));
            }
            p.getComponent().putClientProperty("toolBarActions", toolBarActions);
        }
        return c;
    }

    public void hide(View p) {
        if (p.isShowing()) {
            p.setShowing(false);
            JFrame f = (JFrame) SwingUtilities.getWindowAncestor(p.getComponent());
            f.setVisible(false);
            f.remove(p.getComponent());
            f.dispose();
        }
    }

    public void dispose(View p) {
        super.dispose(p);
        if (views().size() == 0) {
            stop();
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
        for (JMenu mm : getModel().createMenus(this, v)) {
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
        addAction(m, SaveFileAction.ID);
        addAction(m, SaveFileAsAction.ID);
        addAction(m, ExportFileAction.ID);
        addAction(m, PrintFileAction.ID);

        maybeAddSeparator(m);
        addAction(m, CloseFileAction.ID);

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
 
    /**
     * Updates the title of a view and displays it in the given frame.
     * 
     * @param view The view.
     * @param f The frame.
     */
    protected void updateViewTitle(View p, JFrame f) {
        URI uri = p.getURI();
        String title;
        if (uri == null) {
            title = labels.getString("unnamedFile");
        } else {
            title = URIUtil.getName(uri);
        }
        if (p.hasUnsavedChanges()) {
            title += "*";
        }
        p.setTitle(labels.getFormatted("frame.title", title, getName(), p.getMultipleOpenId()));
        f.setTitle(p.getTitle());
    }

    public boolean isSharingToolsAmongViews() {
        return false;
    }

    public Component getComponent() {
        View p = getActiveView();
        return (p == null) ? null : p.getComponent();
    }

    public JMenu createWindowMenu(View view) {
        return null;
    }

    /**
     * Creates the window menu.
     * 
     * @param view The View
     * @param viewActions ActionUtil for the view menu
     * @return A JMenu or null, if no view actions are provided
     */
    @SuppressWarnings("unchecked")
    public JMenu createViewMenu(final View p) {
        Object object = p.getComponent().getClientProperty("toolBarActions");
        LinkedList<Action> viewActions = (LinkedList<Action>) object;
        ApplicationModel model = getModel();
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");

        JMenu m, m2;
        JMenuItem mi;
        JCheckBoxMenuItem cbmi;

        m = new JMenu();
        if (viewActions != null && viewActions.size() > 0) {
            m2 = (viewActions.size() == 1) ? m : new JMenu(labels.getString("toolBars"));
            labels.configureMenu(m, "view");
            for (Action a : viewActions) {
                cbmi = new JCheckBoxMenuItem(a);
                ActionUtil.configureJCheckBoxMenuItem(cbmi, a);
                m2.add(cbmi);
            }
            if (m2 != m) {
                m.add(m2);
            }
        }

        return (m.getPopupMenu().getComponentCount() > 0) ? m : null;
    }

    @Override
    public JMenu createHelpMenu(View p) {
        ApplicationModel model = getModel();
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");

        JMenu m;
        JMenuItem mi;

        m = new JMenu();
        labels.configureMenu(m, "help");
        m.add(model.getAction(AboutAction.ID));

        return m;
    }

    protected void initApplicationActions(ApplicationModel m) {
        m.putAction(AboutAction.ID, new AboutAction(this));
        m.putAction(CloseFileAction.ID, new CloseFileAction(this));
        m.putAction(WindowMaximizeAction.ID, new WindowMaximizeAction(this));
        m.putAction(WindowMinimizeAction.ID, new WindowMinimizeAction(this));
        m.putAction(ClearRecentFilesMenuAction.ID, new ClearRecentFilesMenuAction(this));
    }
}
