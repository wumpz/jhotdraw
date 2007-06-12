/*
 * @(#)DefaultSDIApplication.java  1.4  2007-01-11
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.app;

import org.jhotdraw.util.*;
import org.jhotdraw.util.prefs.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import org.jhotdraw.app.action.*;

/**
 * A DefaultSDIApplication can handle the life cycle of a single document window being
 * presented in a JFrame. The JFrame provides all the functionality needed to
 * work with the document, such as a menu bar, tool bars and palette windows.
 *
 *
 * @author Werner Randelshofer
 * @version 1.4 2007-01-11 Removed method addStandardActionsTo.
 * <br>1.3 2006-05-03 Show asterisk in window title, when project has
 * unsaved changes.
 * <br>1.2.1 2006-02-28 Stop application when last project is closed.
 * <br>1.2 2006-02-06 Support for multiple open id added.
 * <br>1.1 2006-02-06 Revised.
 * <br>1.0 October 16, 2005 Created.
 */
public class DefaultSDIApplication extends AbstractApplication {
    private Project currentProject;
    private Preferences prefs;
    
    /** Creates a new instance. */
    public DefaultSDIApplication() {
    }
    
    public void launch(String[] args) {
        System.setProperty("apple.awt.graphics.UseQuartz","false");
        super.launch(args);
    }
    public void init() {
        initLookAndFeel();
        super.init();
        prefs = Preferences.userNodeForPackage((getModel() == null) ? getClass() : getModel().getClass());
        initLabels();
        initApplicationActions();
    }
    
    public void remove(Project p) {
        super.remove(p);
        if (projects().size() == 0) {
            stop();
        }
    }
    
    public void configure(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar","false");
        System.setProperty("com.apple.macos.useScreenMenuBar","false");
        System.setProperty("apple.awt.graphics.UseQuartz","false");
        System.setProperty("swing.aatext","true");
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
            UIManager.put("OptionPane.css", "");
        }
    }
    
    protected void initApplicationActions() {
        ResourceBundleUtil appLabels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        ApplicationModel m = getModel();
        m.putAction(AboutAction.ID, new AboutAction(this));
        m.putAction(ExitAction.ID, new ExitAction(this));
        
        m.putAction(ClearAction.ID, new ClearAction(this));
        m.putAction(NewAction.ID, new NewAction(this));
        appLabels.configureAction(m.getAction(NewAction.ID), "newWindow");
        m.putAction(LoadAction.ID, new LoadAction(this));
        m.putAction(ClearRecentFilesAction.ID, new ClearRecentFilesAction(this));
        m.putAction(SaveAction.ID, new SaveAction(this));
        m.putAction(SaveAsAction.ID, new SaveAsAction(this));
        m.putAction(CloseAction.ID, new CloseAction(this));
        m.putAction(PrintAction.ID, new PrintAction(this));
        
        m.putAction(UndoAction.ID, new UndoAction(this));
        m.putAction(RedoAction.ID, new RedoAction(this));
        m.putAction(CutAction.ID, new CutAction());
        m.putAction(CopyAction.ID, new CopyAction());
        m.putAction(PasteAction.ID, new PasteAction());
        m.putAction(DeleteAction.ID, new DeleteAction());
        m.putAction(DuplicateAction.ID, new DuplicateAction());
        m.putAction(SelectAllAction.ID, new SelectAllAction());
    }
    protected void initProjectActions(Project p) {
        ApplicationModel m = getModel();
        p.putAction(LoadAction.ID, m.getAction(LoadAction.ID));
    }
    
    public void show(final Project p) {
        if (! p.isShowing()) {
            p.setShowing(true);
            File file = p.getFile();
            final JFrame f = new JFrame();
            String title;
            if (file == null) {
                title = labels.getString("unnamedFile");
            } else {
                title = file.getName();
            }
            if (p.hasUnsavedChanges()) {
                title += "*";
            }
            f.setTitle(labels.getFormatted("frameTitle", title, getName(), p.getMultipleOpenId()));
            f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            JPanel panel = (JPanel) wrapProjectComponent(p);
            f.add(panel);
            f.setMinimumSize(new Dimension(200,200));
            f.setPreferredSize(new Dimension(600,400));
            
            f.setJMenuBar(createMenuBar(p, (java.util.List<Action>) panel.getClientProperty("toolBarActions")));
            
            PreferencesUtil.installFramePrefsHandler(prefs, "project", f);
            Point loc = f.getLocation();
            boolean moved;
            do {
                moved = false;
                for (Iterator i=projects().iterator(); i.hasNext(); ) {
                    Project aProject = (Project) i.next();
                    if (aProject != p &&
                            SwingUtilities.getWindowAncestor(aProject.getComponent()) != null &&
                            SwingUtilities.getWindowAncestor(aProject.getComponent()).
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
                    setCurrentProject(p);
                    getModel().getAction(CloseAction.ID).actionPerformed(
                            new ActionEvent(f, ActionEvent.ACTION_PERFORMED,
                            "windowClosing")
                            );
                }
                
                public void windowActivated(WindowEvent e) {
                    setCurrentProject(p);
                }
            });
            
            p.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    String name = evt.getPropertyName();
                    if (name.equals("hasUnsavedChanges") ||
                            name.equals("file") ||
                            name.equals("multipleOpenId")) {
                        File file = p.getFile();
                        String title;
                        if (file == null) {
                            title = labels.getString("unnamedFile");
                        } else {
                            title = file.getName();
                        }
                        if (p.hasUnsavedChanges()) {
                            title += "*";
                        }
                        f.setTitle(labels.getFormatted("frameTitle", title, getName(), p.getMultipleOpenId()));
                    }
                }
            });
            
            f.setVisible(true);
        }
    }
    /**
     * Returns the project component. Eventually wraps it into
     * another component in order to provide additional functionality.
     */
    protected Component wrapProjectComponent(Project p) {
        JComponent c = p.getComponent();
        if (getModel() != null) {
            LinkedList<Action> toolBarActions = new LinkedList();
            
            int id=0;
            for (JToolBar tb : new ReversedList<JToolBar>(getModel().createToolBars(this, p))) {
                id++;
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(tb, BorderLayout.NORTH);
                panel.add(c, BorderLayout.CENTER);
                c = panel;
                PreferencesUtil.installToolBarPrefsHandler(prefs, "toolbar."+id, tb);
                toolBarActions.addFirst(new ToggleVisibleAction(tb, tb.getName()));
            }
            c.putClientProperty("toolBarActions",toolBarActions);
        }
        return c;
    }
    
    
    public void hide(Project p) {
        if (p.isShowing()) {
            p.setShowing(false);
            JFrame f = (JFrame) SwingUtilities.getWindowAncestor(p.getComponent());
            f.setVisible(false);
            f.remove(p.getComponent());
            f.dispose();
        }
    }
    
    public void dispose(Project p) {
        super.dispose(p);
        if (projects().size() == 0) {
            stop();
        }
    }
    
    public Project getCurrentProject() {
        return currentProject;
    }
    public void setCurrentProject(Project newValue) {
        Project oldValue = currentProject;
        currentProject = newValue;
        firePropertyChange("currentProject", oldValue, newValue);
    }
    
    /**
     * The project menu bar is displayed for a project.
     * The default implementation returns a new screen menu bar.
     */
    protected JMenuBar createMenuBar(final Project p, java.util.List<Action> toolBarActions) {
        JMenuBar mb = new JMenuBar();
        mb.add(createFileMenu(p));
        for (JMenu mm : getModel().createMenus(this, p)) {
            mb.add(mm);
        }
        mb.add(createViewMenu(p, toolBarActions));
        mb.add(createHelpMenu(p));
        return mb;
    }
    
    protected JMenu createFileMenu(final Project p) {
        ApplicationModel model = getModel();
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        
        JMenuBar mb = new JMenuBar();
        JMenu m;
        JMenuItem mi;
        final JMenu openRecentMenu;
        
        m = new JMenu();
        labels.configureMenu(m, labels.getString("file"));
        m.add(model.getAction(ClearAction.ID));
        m.add(model.getAction(NewAction.ID));
        m.add(model.getAction(LoadAction.ID));
        openRecentMenu = new JMenu();
        labels.configureMenu(openRecentMenu, "openRecent");
        openRecentMenu.add(model.getAction(ClearRecentFilesAction.ID));
        updateOpenRecentMenu(openRecentMenu);
        m.add(openRecentMenu);
        m.addSeparator();
        m.add(model.getAction(SaveAction.ID));
        m.add(model.getAction(SaveAsAction.ID));
        if (model.getAction(ExportAction.ID) != null) {
            mi = m.add(model.getAction(ExportAction.ID));
        }
        if (model.getAction(PrintAction.ID) != null) {
            m.addSeparator();
            m.add(model.getAction(PrintAction.ID));
        }
        m.addSeparator();
        m.add(model.getAction(ExitAction.ID));
        mb.add(m);
        
        addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name == "projectCount") {
                    if (p == null || projects().contains(p)) {
                    } else {
                        removePropertyChangeListener(this);
                    }
                } else if (name == "recentFiles") {
                    updateOpenRecentMenu(openRecentMenu);
                }
            }
        });
        
        return m;
    }
    private void updateOpenRecentMenu(JMenu openRecentMenu) {
        if (openRecentMenu.getItemCount() > 0) {
            JMenuItem clearRecentFilesItem = (JMenuItem) openRecentMenu.getItem(
                    openRecentMenu.getItemCount() - 1
                    );
            openRecentMenu.removeAll();
            for (File f : recentFiles()) {
                openRecentMenu.add(new LoadRecentAction(DefaultSDIApplication.this, f));
            }
            if (recentFiles().size() > 0) {
                openRecentMenu.addSeparator();
            }
            openRecentMenu.add(clearRecentFilesItem);
        }
    }
    
    public boolean isSharingToolsAmongProjects() {
        return false;
    }
    
    public Component getComponent() {
        Project p = getCurrentProject();
        return (p == null) ? null : p.getComponent();
    }
    protected JMenu createViewMenu(final Project p, java.util.List<Action> toolBarActions) {
        ApplicationModel model = getModel();
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        
        JMenuBar mb = new JMenuBar();
        JMenu m, m2;
        JMenuItem mi;
        JCheckBoxMenuItem cbmi;
        final JMenu openRecentMenu;
        
        m = new JMenu();
        if (toolBarActions != null && toolBarActions.size() > 0) {
            m2 = (toolBarActions.size() == 1) ? m : new JMenu(labels.getString("toolBars"));
            labels.configureMenu(m, labels.getString("view"));
            for (Action a : toolBarActions) {
                cbmi = new JCheckBoxMenuItem(a);
                Actions.configureJCheckBoxMenuItem(cbmi, a);
                m2.add(cbmi);
            }
            m.add(m2);
        }
        
        return m;
    }
    
    protected JMenu createHelpMenu(Project p) {
        ApplicationModel model = getModel();
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        
        JMenu m;
        JMenuItem mi;
        
        m = new JMenu();
        labels.configureMenu(m, labels.getString("help"));
        m.add(model.getAction(AboutAction.ID));
        
        return m;
    }
}
