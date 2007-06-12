/*
 * @(#)AbstractSDIApplication.java  1.4  2007-01-11
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

package org.jhotdraw.application;

import application.ResourceMap;
import org.jhotdraw.util.*;
import org.jhotdraw.util.prefs.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import org.jhotdraw.application.action.*;

/**
 * A AbstractSDIApplication can handle the life cycle of a single document window being
 * presented in a JFrame. The JFrame provides all the functionality needed to
 * work with the document, such as a menu bar, tool bars and palette windows.
 *
 * @author Werner Randelshofer
 * @version 1.4 2007-01-11 Removed method addStandardActionsTo.
 * <br>1.3 2006-05-03 Show asterisk in window title, when documentView has
 * unsaved changes.
 * <br>1.2.1 2006-02-28 Stop application when last documentView is closed.
 * <br>1.2 2006-02-06 Support for multiple open id added.
 * <br>1.1 2006-02-06 Revised.
 * <br>1.0 October 16, 2005 Created.
 */
public abstract class AbstractSDIApplication extends AbstractDocumentOrientedApplication {
    private DocumentView currentView;
    
    @Override public void remove(DocumentView p) {
        super.remove(p);
        if (getViews().size() == 0) {
            exit();
        }
    }
    
    public static void initAWT(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar","false");
        System.setProperty("com.apple.macos.useScreenMenuBar","false");
        System.setProperty("apple.awt.graphics.UseQuartz","false");
        System.setProperty("swing.aatext","true");
    }
    
    protected void initLookAndFeel() {
        if (UIManager.getString("OptionPane.css") == null) {
            UIManager.put("OptionPane.css", "");
        }
    }
    
    protected ActionMap createActionMap() {
        ActionMap m = new ActionMap();
        m.put(AboutAction.ID, new AboutAction());
        m.put(ExitAction.ID, new ExitAction());
        
        m.put(ClearAction.ID, new ClearAction());
        m.put(NewAction.ID, new NewAction());
        ((AbstractApplicationAction) m.get(NewAction.ID)).initActionProperties("newWindow");
        m.put(LoadAction.ID, new LoadAction());
        m.put(ClearRecentFilesAction.ID, new ClearRecentFilesAction());
        m.put(SaveAction.ID, new SaveAction());
        m.put(SaveAsAction.ID, new SaveAsAction());
        m.put(CloseAction.ID, new CloseAction());
        m.put(PrintAction.ID, new PrintAction());
        
        m.put(UndoAction.ID, new UndoAction());
        m.put(RedoAction.ID, new RedoAction());
        m.put(CutAction.ID, new CutAction());
        m.put(CopyAction.ID, new CopyAction());
        m.put(PasteAction.ID, new PasteAction());
        m.put(DeleteAction.ID, new DeleteAction());
        m.put(DuplicateAction.ID, new DuplicateAction());
        m.put(SelectAllAction.ID, new SelectAllAction());
        
        return m;
    }
    public void show(final DocumentView p) {
        final ResourceMap labels = getFrameworkResourceMap();
        updateName(p);
        final JFrame f = new JFrame();
        f.setTitle(labels.getString("SDIWindow.Frame.title",
                p.getName()
                ));
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        JPanel panel = (JPanel) wrapProjectComponent(p);
        f.add(panel);
        f.setMinimumSize(new Dimension(200,200));
        f.setPreferredSize(new Dimension(600,400));
        
        f.setJMenuBar(createMenuBar(p, (java.util.List<Action>) panel.getClientProperty("toolBarActions")));
        
        Preferences prefs = Preferences.userNodeForPackage(getClass());
        PreferencesUtil.installFramePrefsHandler(prefs, "documentView", f);
        Point loc = f.getLocation();
        boolean moved;
        do {
            moved = false;
            for (Iterator i=getViews().iterator(); i.hasNext(); ) {
                DocumentView aProject = (DocumentView) i.next();
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
                setCurrentView(p);
                getAction(CloseAction.ID).actionPerformed(
                        new ActionEvent(f, ActionEvent.ACTION_PERFORMED,
                        "windowClosing")
                        );
            }
            
            public void windowActivated(WindowEvent e) {
                setCurrentView(p);
            }
        });
        
        p.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name.equals("modified") ||
                        name.equals("name")) {
                    f.setTitle(labels.getString(
                            (p.isModified()) ? "SDIWindow.Frame.modifiedTitle" : "SDIWindow.Frame.title",
                            p.getName())
                            );
                } else if (name.equals("file")) {
                    updateName(p);
                }
            }
        });
        
        f.setVisible(true);
    }
    /**
     * Returns the documentView component. Eventually wraps it into
     * another component in order to provide additional functionality.
     */
    protected Component wrapProjectComponent(DocumentView p) {
        JComponent c = p.getComponent();
        LinkedList<Action> toolBarActions = new LinkedList();
        Preferences prefs = Preferences.userNodeForPackage(getClass());
        
        int id=0;
        for (JToolBar tb : new ReversedList<JToolBar>(createToolBars(p))) {
            id++;
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(tb, BorderLayout.NORTH);
            panel.add(c, BorderLayout.CENTER);
            c = panel;
            PreferencesUtil.installToolBarPrefsHandler(prefs, "toolbar."+id, tb);
            toolBarActions.addFirst(new ToggleVisibleAction(tb, tb.getName()));
        }
        c.putClientProperty("toolBarActions",toolBarActions);
        return c;
    }
    
    
    public void hide(DocumentView p) {
        JFrame f = (JFrame) SwingUtilities.getWindowAncestor(p.getComponent());
        f.setVisible(false);
        f.remove(p.getComponent());
        f.dispose();
    }
    
    public DocumentView getCurrentView() {
        return currentView;
    }
    public void setCurrentView(DocumentView newValue) {
        DocumentView oldValue = currentView;
        currentView = newValue;
        firePropertyChange(PROP_CURRENT_VIEW, oldValue, newValue);
    }
    
    protected JMenu createFileMenu(final DocumentView p) {
        ResourceMap labels = getFrameworkResourceMap();
        
        JMenuBar mb = new JMenuBar();
        JMenu m;
        JMenuItem mi;
        final JMenu openRecentMenu;
        
        m = new JMenu();
        m.setName("File.Menu");
        labels.injectComponent(m);
        m.add(getAction(ClearAction.ID));
        m.add(getAction(NewAction.ID));
        
        m.add(getAction(LoadAction.ID));
        openRecentMenu = new JMenu();
        openRecentMenu.setName("File.openRecent.Menu");
        labels.injectComponent(openRecentMenu);
        openRecentMenu.add(getAction(ClearRecentFilesAction.ID));
        updateOpenRecentMenu(openRecentMenu);
        m.add(openRecentMenu);
        m.addSeparator();
        m.add(getAction(SaveAction.ID));
        m.add(getAction(SaveAsAction.ID));
        if (getAction(ExportAction.ID) != null) {
            mi = m.add(getAction(ExportAction.ID));
        }
        if (getAction(PrintAction.ID) != null) {
            m.addSeparator();
            m.add(getAction(PrintAction.ID));
        }
        m.addSeparator();
        m.add(getAction(ExitAction.ID));
        mb.add(m);
        
        addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name == "projectCount") {
                    if (p == null || getViews().contains(p)) {
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
    public boolean isEditorShared() {
        return false;
    }
    
    public Component getComponent() {
        DocumentView p = getCurrentView();
        return (p == null) ? null : p.getComponent();
    }
    
    protected JMenu createViewMenu(final DocumentView p, java.util.List<Action> toolBarActions) {
        ResourceMap labels = getResourceMap();
        JMenuBar mb = new JMenuBar();
        JMenu m, m2;
        JMenuItem mi;
        JCheckBoxMenuItem cbmi;
        final JMenu openRecentMenu;
        
        m = new JMenu();
        m.setName("View.Menu");
        labels.injectComponent(m);
        if (toolBarActions != null && toolBarActions.size() > 0) {
            m2 = (toolBarActions.size() == 1) ? m : new JMenu(labels.getString("toolBars"));
            for (Action a : toolBarActions) {
                cbmi = new JCheckBoxMenuItem(a);
                Actions.configureJCheckBoxMenuItem(cbmi, a);
                m2.add(cbmi);
            }
            m.add(m2);
        }
        
        return m;
    }
    
    @Override protected JMenu createHelpMenu(DocumentView p) {
        ResourceMap labels = getFrameworkResourceMap();
        
        JMenu m;
        JMenuItem mi;
        
        m = new JMenu();
        m.setName("Help.Menu");
        labels.injectComponent(m);
        m.add(getAction(AboutAction.ID));
        
        return m;
    }
}
