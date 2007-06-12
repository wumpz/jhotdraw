/*
 * @(#)AbstractMDIApplication.java  1.0  June 5, 2006
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
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
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import org.jhotdraw.gui.*;
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
 * A AbstractMDIApplication can handle the life cycle of multiple document windows each
 * being presented in a JInternalFrame of its own.  A parent JFrame provides all
 * the functionality needed to work with documents, such as a menu bar, tool
 * bars and palette windows.
 *
 * @author Werner Randelshofer.
 * @version 1.0 June 5, 2006 Created.
 */
public abstract class AbstractMDIApplication extends AbstractDocumentOrientedApplication {
    private JFrame parentFrame;
    private JScrollPane scrollPane;
    private MDIDesktopPane desktopPane;
    private DocumentView currentView;
    private LinkedList<Action> toolBarActions;
    
    /** Creates a new instance. */
    public AbstractMDIApplication() {
    }
    
    @Override protected ActionMap createActionMap() {
        ActionMap m = new ActionMap();
        
        m.put(AboutAction.ID, new AboutAction());
        m.put(ExitAction.ID, new ExitAction());
        
        m.put(NewAction.ID, new NewAction());
        m.put(OpenAction.ID, new OpenAction());
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
        /*
        m.put(MaximizeAction.ID, new MaximizeAction());
        m.put(MinimizeAction.ID, new MinimizeAction());
         */
        m.put(ArrangeAction.VERTICAL_ID, new ArrangeAction(getDesktopPane(), Arrangeable.Arrangement.VERTICAL));
        m.put(ArrangeAction.HORIZONTAL_ID, new ArrangeAction(getDesktopPane(), Arrangeable.Arrangement.HORIZONTAL));
        m.put(ArrangeAction.CASCADE_ID, new ArrangeAction(getDesktopPane(), Arrangeable.Arrangement.CASCADE));
        
        return m;
    }
    
    protected MDIDesktopPane getDesktopPane() {
        if (desktopPane == null) {
        desktopPane = new MDIDesktopPane();
        }
        return desktopPane;
    }
    
    @Override public void initMainFrame() {
        Preferences prefs = Preferences.userNodeForPackage(getClass());
        
        parentFrame = new JFrame(getResourceMap().getString("Application.name"));
        parentFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(getDesktopPane());
        toolBarActions = new LinkedList<Action>();
        
        
        createActionMap();
        parentFrame.getContentPane().add(
                wrapDesktopPane(scrollPane, toolBarActions)
                );
        
        parentFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent evt) {
                getAction(ExitAction.ID).actionPerformed(
                        new ActionEvent(parentFrame, ActionEvent.ACTION_PERFORMED, "windowClosing")
                        );
            }
        });
        // XXX - Implement toolbar creation here
        parentFrame.setJMenuBar(createMenuBar(null, new LinkedList<javax.swing.Action>()));
        
        PreferencesUtil.installFramePrefsHandler(prefs, "parentFrame", parentFrame);
        
        parentFrame.setVisible(true);
    }
    public static void initAWT(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar","false");
        System.setProperty("com.apple.macos.useScreenMenuBar","false");
        System.setProperty("apple.awt.graphics.UseQuartz","false");
        System.setProperty("swing.aatext","true");
    }
    
    @Override public void add(final DocumentView v) {
        updateName(v);
        v.putAction(FocusAction.ID, new FocusAction(v));
        super.add(v);
    }
    
    @Override public void show(final DocumentView p) {
        ResourceMap labels = getResourceMap();
        updateName(p);
        final JInternalFrame f = new JInternalFrame();
        f.setTitle(labels.getString("MDIWindow.Frame.title",
                p.getName()));
        f.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
        f.setClosable(true);
        f.setMaximizable(true);
        f.setResizable(true);
        f.setIconifiable(false);
        
        Preferences prefs = Preferences.userNodeForPackage(getClass());
        PreferencesUtil.installInternalFramePrefsHandler(prefs, "documentView", f, desktopPane);
        Point loc = f.getLocation();
        boolean moved;
        do {
            moved = false;
            for (Iterator i=getViews().iterator(); i.hasNext(); ) {
                DocumentView aProject = (DocumentView) i.next();
                if (aProject != p &&
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
                setCurrentView(p);
                getAction(CloseAction.ID).actionPerformed(
                        new ActionEvent(f, ActionEvent.ACTION_PERFORMED,
                        "windowClosing")
                        );
            }
        });
        
        p.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name.equals("hasUnsavedChanges")) {
                    ((JInternalFrame) f.getRootPane().getParent()).putClientProperty("windowModified",new Boolean(p.isModified()));
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
                        setCurrentView(p);
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
    
    @Override public void hide(DocumentView p) {
        JInternalFrame f = (JInternalFrame) SwingUtilities.getRootPane(p.getComponent()).getParent();
        f.setVisible(false);
        f.remove(p.getComponent());
        desktopPane.remove(f);
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
    
    public boolean isEditorShared() {
        return true;
    }
    
    public Component getComponent() {
        return parentFrame;
    }
    
    /**
     * Returns the wrapped desktop pane.
     */
    protected Component wrapDesktopPane(Component c, LinkedList<Action> toolBarActions) {
        Preferences prefs = Preferences.userNodeForPackage(getClass());
        int id=0;
        for (JToolBar tb : new ReversedList<JToolBar>(createToolBars(null))) {
            id++;
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(tb, BorderLayout.NORTH);
            panel.add(c, BorderLayout.CENTER);
            c = panel;
            PreferencesUtil.installToolBarPrefsHandler(prefs, "toolbar."+id, tb);
            toolBarActions.addFirst(new ToggleToolBarAction(tb, tb.getName()));
        }
        return c;
    }
    
    @Override protected JMenu createFileMenu(final DocumentView p) {
        ResourceMap labels = getFrameworkResourceMap();
        
        JMenuBar mb = new JMenuBar();
        JMenu m;
        JMenuItem mi;
        final JMenu openRecentMenu;
        
        m = new JMenu();
        m.setName("File.Menu");
        labels.injectComponent(m);
        m.add(getAction(NewAction.ID));
        m.add(getAction(OpenAction.ID));
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
   @Override protected JMenu createWindowMenu(final DocumentView p) {
        ResourceMap labels = getResourceMap();
        
        JMenu m;
        JMenuItem mi;
        
        m = new JMenu();
        final JMenu windowMenu = m;
        m.setName("Window.Menu");
        labels.injectComponent(m);
        m.add(getAction(ArrangeAction.CASCADE_ID));
        m.add(getAction(ArrangeAction.VERTICAL_ID));
        m.add(getAction(ArrangeAction.HORIZONTAL_ID));
        
        m.addSeparator();
        for (DocumentView pr : getViews()) {
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
                System.out.println(this+"."+name);
                if (name == PROP_VIEW_COUNT) {
                    JMenu m = windowMenu;
                    m.removeAll();
                    
                    m.add(getAction(ArrangeAction.CASCADE_ID));
                    m.add(getAction(ArrangeAction.VERTICAL_ID));
                    m.add(getAction(ArrangeAction.HORIZONTAL_ID));
                    
                    m.addSeparator();
                    for (DocumentView v : getViews()) {
                        if (v.getAction(FocusAction.ID) != null) {
                            m.add(v.getAction(FocusAction.ID));
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
