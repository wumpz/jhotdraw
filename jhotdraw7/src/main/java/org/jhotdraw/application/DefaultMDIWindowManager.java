/*
 * @(#)DefaultMDIWindowManager.java  1.0  22. März 2007
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

import org.jhotdraw.gui.*;
import org.jhotdraw.gui.Arrangeable;
import org.jhotdraw.util.*;
import org.jhotdraw.util.prefs.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import javax.swing.event.*;
import org.jhotdraw.application.action.*;
/**
 * DefaultMDIWindowManager.
 * 
 * 
 * 
 * @author Werner Randelshofer
 * @version 1.0 22. März 2007 Created.
 */
public class DefaultMDIWindowManager extends AbstractWindowManager {
    private JFrame parentFrame;
    private JScrollPane scrollPane;
    private MDIDesktopPane desktopPane;
    private Preferences prefs;
    private Project currentProject;
    private LinkedList<Action> toolBarActions;
    
    protected void initApplicationActions() {
        DocumentOrientedApplication mo = getApplication();
        mo.putAction(AboutAction.ID, new AboutAction());
        mo.putAction(ExitAction.ID, new ExitAction());
        
        mo.putAction(NewAction.ID, new NewAction());
        mo.putAction(OpenAction.ID, new OpenAction());
        mo.putAction(ClearRecentFilesAction.ID, new ClearRecentFilesAction());
        mo.putAction(SaveAction.ID, new SaveAction());
        mo.putAction(SaveAsAction.ID, new SaveAsAction());
        mo.putAction(CloseAction.ID, new CloseAction());
        mo.putAction(PrintAction.ID, new PrintAction());
        
        mo.putAction(UndoAction.ID, new UndoAction());
        mo.putAction(RedoAction.ID, new RedoAction());
        mo.putAction(CutAction.ID, new CutAction());
        mo.putAction(CopyAction.ID, new CopyAction());
        mo.putAction(PasteAction.ID, new PasteAction());
        mo.putAction(DeleteAction.ID, new DeleteAction());
        mo.putAction(DuplicateAction.ID, new DuplicateAction());
        mo.putAction(SelectAllAction.ID, new SelectAllAction());
        mo.putAction(ArrangeAction.VERTICAL_ID, new ArrangeAction((Arrangeable) desktopPane, Arrangeable.Arrangement.VERTICAL));
        mo.putAction(ArrangeAction.HORIZONTAL_ID, new ArrangeAction((Arrangeable) desktopPane, Arrangeable.Arrangement.HORIZONTAL));
        mo.putAction(ArrangeAction.CASCADE_ID, new ArrangeAction((Arrangeable) desktopPane, Arrangeable.Arrangement.CASCADE));
    }
    protected void initProjectActions(Project p) {
        p.putAction(FocusAction.ID, new FocusAction(p));
    }
    public void preLaunch() {
        System.setProperty("apple.laf.useScreenMenuBar","false");
        System.setProperty("com.apple.macos.useScreenMenuBar","false");
        System.setProperty("apple.awt.graphics.UseQuartz","false");
        System.setProperty("swing.aatext","true");
        initLookAndFeel();
        prefs = Preferences.userNodeForPackage((getApplication() == null) ? getClass() : getApplication().getClass());
        initLabels();
    }
    @Override public void preInit() {
        super.preInit();
        initApplicationActions();
        }
    public void preStart() {
        parentFrame = new JFrame(getName());
        parentFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        desktopPane = new MDIDesktopPane();
        
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(desktopPane);
        toolBarActions = new LinkedList<Action>();
        
        
        initApplicationActions();
        parentFrame.getContentPane().add(
                wrapDesktopPane(scrollPane, toolBarActions)
                );
        
        parentFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent evt) {
                getApplication().getAction(ExitAction.ID).actionPerformed(
                        new ActionEvent(parentFrame, ActionEvent.ACTION_PERFORMED, "windowClosing")
                        );
            }
        });
        parentFrame.setJMenuBar(createMenuBar());
        
        PreferencesUtil.installFramePrefsHandler(prefs, "parentFrame", parentFrame);
        
        parentFrame.setVisible(true);
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
    
    public void show(final Project p) {
        if (! p.isShowing()) {
            p.setShowing(true);
            File file = p.getFile();
            final JInternalFrame f = new JInternalFrame();
            String title;
            if (file == null) {
                title = labels.getString("unnamedFile");
            } else {
                title = file.getName();
            }
            f.setTitle(labels.getFormatted("internalFrameTitle", title, getName(), p.getMultipleOpenId()));
            f.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
            f.setClosable(true);
            f.setMaximizable(true);
            f.setResizable(true);
            f.setIconifiable(false);
            
            PreferencesUtil.installInternalFramePrefsHandler(prefs, "project", f, desktopPane);
            Point loc = f.getLocation();
            boolean moved;
            do {
                moved = false;
                for (Iterator i=projects().iterator(); i.hasNext(); ) {
                    Project aProject = (Project) i.next();
                    if (aProject != p && aProject.isShowing() &&
                            SwingUtilities.getRootPane(aProject.getComponent()).getParent().
                            getLocation().equals(loc)) {
                        loc.x += 22;
                        loc.y += 22;
                        moved = true;
                        break;
                    }
                }
            } while (moved);
            f.setLocation(loc);
            
            //paletteHandler.add(f, p);
            
            f.addInternalFrameListener(new InternalFrameAdapter() {
                @Override public void internalFrameClosing(final InternalFrameEvent evt) {
                    setCurrentProject(p);
                    getApplication().getAction(CloseAction.ID).actionPerformed(
                            new ActionEvent(f, ActionEvent.ACTION_PERFORMED,
                            "windowClosing")
                            );
                }
            });
            
            p.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    String name = evt.getPropertyName();
                    if (name.equals("hasUnsavedChanges")) {
                        ((JInternalFrame) f.getRootPane().getParent()).putClientProperty("windowModified",new Boolean(p.hasUnsavedChanges()));
                    } else if (name.equals("file")) {
                        f.setTitle((p.getFile() == null) ? "Unnamed" : p.getFile().getName());
                    }
                }
            });
            
            f.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    String name = evt.getPropertyName();
                    if (name.equals("selected")) {
                        if (evt.getNewValue().equals(Boolean.TRUE)) {
                            setCurrentProject(p);
                        }
                    }
                }
            });
            
            //f.setJMenuBar(createMenuBar(p));
            
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
        }
    }
    
    public void hide(Project p) {
        if (p.isShowing()) {
            JInternalFrame f = (JInternalFrame) SwingUtilities.getRootPane(p.getComponent()).getParent();
            f.setVisible(false);
            f.remove(p.getComponent());
            desktopPane.remove(f);
            f.dispose();
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
    
    public boolean isSharingToolsAmongProjects() {
        return true;
    }
    
    public Component getComponent() {
        return parentFrame;
    }
    
    /**
     * Returns the wrapped desktop pane.
     */
    protected Component wrapDesktopPane(Component c, LinkedList<Action> toolBarActions) {
        if (getApplication() != null) {
            int id=0;
            for (JToolBar tb : new ReversedList<JToolBar>(getApplication().createToolBars(this, null))) {
                id++;
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(tb, BorderLayout.NORTH);
                panel.add(c, BorderLayout.CENTER);
                c = panel;
                PreferencesUtil.installToolBarPrefsHandler(prefs, "toolbar."+id, tb);
                toolBarActions.addFirst(new ToggleToolBarAction(tb, tb.getName()));
            }
            /*
            JToolBar tb = new JToolBar();
            tb.setName(labels.getString("standardToolBarTitle"));
            addStandardActionsTo(tb);
            id++;
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(tb, BorderLayout.NORTH);
            panel.add(c, BorderLayout.CENTER);
            c = panel;
            PreferencesUtil.installToolBarPrefsHandler(prefs, "toolbar."+id, tb);
            toolBarActions.addFirst(new ToggleToolBarAction(tb, tb.getName()));
            panel.putClientProperty("toolBarActions", toolBarActions);
             */
        }
        return c;
    }
    /*
    protected void addStandardActionsTo(JToolBar tb) {
        JButton b;
        DocumentOrientedApplication mo = getApplication();
     
        b = tb.add(mo.getAction(NewAction.ID));
        b.setFocusable(false);
        b = tb.add(mo.getAction(OpenAction.ID));
        b.setFocusable(false);
        b = tb.add(mo.getAction(SaveAction.ID));
        tb.addSeparator();
        b = tb.add(mo.getAction(UndoAction.ID));
        b.setFocusable(false);
        b = tb.add(mo.getAction(RedoAction.ID));
        b.setFocusable(false);
        tb.addSeparator();
        b = tb.add(mo.getAction(CutAction.ID));
        b.setFocusable(false);
        b = tb.add(mo.getAction(CopyAction.ID));
        b.setFocusable(false);
        b = tb.add(mo.getAction(PasteAction.ID));
        b.setFocusable(false);
    }*/
    /**
     * Creates a menu bar.
     */
    protected JMenuBar createMenuBar() {
        JMenuBar mb = new JMenuBar();
        mb.add(createFileMenu());
        for (JMenu mm : getApplication().createMenus(this, null)) {
            mb.add(mm);
        }
        mb.add(createWindowMenu());
        mb.add(createHelpMenu());
        return mb;
    }
    protected JMenu createFileMenu() {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        DocumentOrientedApplication mo = getApplication();
        
        JMenuBar mb = new JMenuBar();
        JMenu m;
        JMenuItem mi;
        final JMenu openRecentMenu;
        
        m = new JMenu();
        labels.configureMenu(m, "file");
        m.add(mo.getAction(NewAction.ID));
        m.add(mo.getAction(OpenAction.ID));
        openRecentMenu = new JMenu();
        labels.configureMenu(openRecentMenu, "openRecent");
        openRecentMenu.add(mo.getAction(ClearRecentFilesAction.ID));
        updateOpenRecentMenu(openRecentMenu);
        m.add(openRecentMenu);
        m.addSeparator();
        m.add(mo.getAction(CloseAction.ID));
        m.add(mo.getAction(SaveAction.ID));
        m.add(mo.getAction(SaveAsAction.ID));
        if (mo.getAction(ExportAction.ID) != null) {
            mi = m.add(mo.getAction(ExportAction.ID));
        }
        if (mo.getAction(PrintAction.ID) != null) {
            m.addSeparator();
            m.add(mo.getAction(PrintAction.ID));
        }
        m.addSeparator();
        m.add(mo.getAction(ExitAction.ID));
        
        addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                DocumentOrientedApplication mo = getApplication();
                if (name == "recentFiles") {
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
                openRecentMenu.add(new OpenRecentAction(f));
            }
            if (recentFiles().size() > 0) {
                openRecentMenu.addSeparator();
            }
            openRecentMenu.add(clearRecentFilesItem);
        }
    }
    protected JMenu createWindowMenu() {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        DocumentOrientedApplication mo = getApplication();
        
        JMenu m;
        JMenuItem mi;
        
        m = new JMenu();
        final JMenu windowMenu = m;
        labels.configureMenu(m, "window");
        m.add(mo.getAction(ArrangeAction.CASCADE_ID));
        m.add(mo.getAction(ArrangeAction.VERTICAL_ID));
        m.add(mo.getAction(ArrangeAction.HORIZONTAL_ID));
        
        m.addSeparator();
        for (Project pr : projects()) {
            if (pr.getAction(FocusAction.ID) != null) {
                windowMenu.add(pr.getAction(FocusAction.ID));
            }
        }
        if (toolBarActions.size() > 0) {
            m.addSeparator();
            for (Action a: toolBarActions) {
                JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(a);
                Actions.configureJCheckBoxMenuItem(cbmi, a);
                
                m.add(cbmi);
            }
        }
        
        addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                DocumentOrientedApplication mo = getApplication();
                if (name == "projectCount") {
                    System.out.println("projectCount changed!!!");
                    JMenu m = windowMenu;
                    m.removeAll();
                    
                    m.add(mo.getAction(ArrangeAction.CASCADE_ID));
                    m.add(mo.getAction(ArrangeAction.VERTICAL_ID));
                    m.add(mo.getAction(ArrangeAction.HORIZONTAL_ID));
                    
                    m.addSeparator();
                    for (Iterator i=projects().iterator(); i.hasNext(); ) {
                        Project pr = (Project) i.next();
                        if (pr.getAction(FocusAction.ID) != null) {
                            m.add(pr.getAction(FocusAction.ID));
                        }
                    }
                    if (toolBarActions.size() > 0) {
                        m.addSeparator();
                        for (Action a: toolBarActions) {
                            JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(a);
                            Actions.configureJCheckBoxMenuItem(cbmi, a);
                            m.add(cbmi);
                        }
                    }
                }
            }
        });
        
        return m;
    }
    protected JMenu createHelpMenu() {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        DocumentOrientedApplication mo = getApplication();
        
        JMenu m;
        JMenuItem mi;
        
        m = new JMenu();
        labels.configureMenu(m, labels.getString("help"));
        m.add(mo.getAction(AboutAction.ID));
        return m;
    }

}

