
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.application;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JToolBar;


/**
 * A View encapsulates a top-level Application GUI component, like a JFrame
 * or an Applet, and its main GUI elements: a menu bar, tool bar, component, 
 * and a status bar.  All of the elements are optional (although a View without
 * a main component would be unusual).  Views have a {@code JRootPane}, which 
 * is the root component for all of the Swing Window types as well as JApplet.
 * Setting a View property, like {@code menuBar} or {@code toolBar}, just 
 * adds a component to the rootPane in a way that's defined by the View subclass.
 * By default the View elements are arranged in a conventional way:
 * <ul>
 * <li> {@code menuBar} - becomes the rootPane's JMenuBar
 * <li> {@code toolBar} - added to {@code BorderLayout.NORTH} of the rootPane's contentPane
 * <li> {@code component} - added to {@code BorderLayout.CENTER} of the rootPane's contentPane
 * <li> {@code statusBar} - added to {@code BorderLayout.SOUTH} of the rootPane's contentPane
 * </ul>
 * <p>
 * To show or hide a View you call the corresponding Application methods.  Here's a simple
 * example:
 * <pre>
 * class MyApplication extends SingleFrameApplication {
 *     @Override protected void startup() {
 *         View view = getMainView();
 *         view.setComponent(createMainComponent());
 *         view.setMenuBar(createMenuBar());
 *         show(view);
 *     }
 * }
 * </pre>
 * <p>
 * The advantage of Views over just configuring a JFrame or JApplet
 * directly, is that a View is more easily moved to an alternative
 * top level container, like a docking framework.  
 * 
 * @see JRootPane
 * @see Application#show(View)
 * @see Application#hide(View)
 */
public class View extends AbstractBean {
    private static final Logger logger = Logger.getLogger(View.class.getName());
    private final Application application;
    private ResourceMap resourceMap = null;
    private JRootPane rootPane = null; 
    private JComponent component = null;
    private JMenuBar menuBar = null;
    private List<JToolBar> toolBars = Collections.emptyList();
    private JComponent toolBarsPanel = null;
    private JComponent statusBar = null;

    /**
     * Construct an empty View object for the specified Application.
     * 
     * @param application the Application responsible for showing/hiding this View
     * @see Application#show(View)
     * @see Application#hide(View)
     */
    public View(Application application) {
        if (application == null) {
            throw new IllegalArgumentException("null application");
        }
        this.application = application;
    }

    /**
     * The {@code Application} that's responsible for showing/hiding this View.
     * 
     * @return the Application that owns this View
     * @see #getContext
     * @see Application#show(View)
     * @see Application#hide(View)
     */
    public final Application getApplication() {
        return application;
    }

    /**
     * The {@code ApplicationContext} for the {@code
     * Application} that's responsible for showing/hiding this View.
     * This method is just shorthand for {@code getApplication().getContext()}.
     * 
     * @return the Application that owns this View
     * @see #getApplication
     * @see Application#show(View)
     * @see Application#hide(View)
     */
    public final ApplicationContext getContext() {
        return getApplication().getContext();
    }

    /**
     * The {@code ResourceMap} for this View.  
     * This method is just shorthand for {@code getContext().getResourceMap(getClass(), View.class)}.
     * 
     * @return The {@code ResourceMap} for this View
     * @see #getContext
     */
    public ResourceMap getResourceMap() {
        if (resourceMap == null) {
            resourceMap = getContext().getResourceMap(getClass(), View.class);
        }
        return resourceMap;
    }

    /**
     * The {@code JRootPane} for this View.  All of the components for this 
     * View must be added to its rootPane.  Most applications will do so 
     * by setting the View's {@code component}, {@code menuBar}, {@code toolBar},
     * and {@code statusBar} properties.
     * 
     * @return The {@code rootPane} for this View
     * @see #setComponent
     * @see #setMenuBar
     * @see #setToolBar
     * @see #setStatusBar
     */
    public JRootPane getRootPane() {
        if (rootPane == null) {
            rootPane = new JRootPane();
            rootPane.setOpaque(true);
        }
        return rootPane;
    }

    private void replaceContentPaneChild(JComponent oldChild, JComponent newChild, String constraint) {
        Container contentPane = getRootPane().getContentPane();
        if (oldChild != null) {
            contentPane.remove(oldChild);
        }
        if (newChild != null) {
            contentPane.add(newChild, constraint);
        }
    }

    /**
     * The main {JComponent} for this View.  
     * 
     * @return The {@code component} for this View
     * @see #setComponent
     */
    public JComponent getComponent() {
        return component;
    }

    /**
     * Set the single main Component for this View.  It's added to the 
     * {@code BorderLayout.CENTER} of the rootPane's contentPane.  If 
     * the component property was already set, the old component is removed 
     * first.
     * <p>
     * This is a bound property.  The default value is null.
     * 
     * @return The {@code component} for this View
     * @see #getComponent
     */
    public void setComponent(JComponent component) {
        JComponent oldValue = this.component;
        this.component = component;
        replaceContentPaneChild(oldValue, this.component, BorderLayout.CENTER);
        firePropertyChange("component", oldValue, this.component);
    }

    /**
     * The main {JMenuBar} for this View.  
     * 
     * @return The {@code menuBar} for this View
     * @see #setMenuBar
     */
    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public void setMenuBar(JMenuBar menuBar) {
        JMenuBar oldValue = getMenuBar();
        this.menuBar = menuBar;
        getRootPane().setJMenuBar(menuBar);
        firePropertyChange("menuBar", oldValue, menuBar);
    }

    public List<JToolBar> getToolBars() {
        return toolBars;
    }

    public void setToolBars(List<JToolBar> toolBars) {
        if (toolBars == null) {
            throw new IllegalArgumentException("null toolbars");
        }
        List<JToolBar> oldValue = getToolBars();
        this.toolBars = Collections.unmodifiableList(new ArrayList(toolBars));
        JComponent oldToolBarsPanel = this.toolBarsPanel;
        JComponent newToolBarsPanel = null;
        if (this.toolBars.size() == 1) {
            newToolBarsPanel = toolBars.get(0);
        }
        else if (this.toolBars.size() > 1) {
            newToolBarsPanel = new JPanel();
            for(JComponent toolBar : this.toolBars) {
                newToolBarsPanel.add(toolBar);
            }
        }
        replaceContentPaneChild(oldToolBarsPanel, newToolBarsPanel, BorderLayout.NORTH);
        firePropertyChange("toolBars", oldValue, this.toolBars);
    }

    public final JToolBar getToolBar() {
        List<JToolBar> toolBars = getToolBars();
        return (toolBars.size() == 0) ? null : toolBars.get(0);
    }

    public final void setToolBar(JToolBar toolBar) {
        JToolBar oldValue = getToolBar();
        List<JToolBar> toolBars = Collections.emptyList();
        if (toolBar != null) {
            toolBars = Collections.singletonList(toolBar);
        }
        setToolBars(toolBars);
        firePropertyChange("toolBar", oldValue, toolBar);
    }

    public JComponent getStatusBar() {
        return statusBar;
    }

    public void setStatusBar(JComponent statusBar) {
        JComponent oldValue = this.statusBar;
        this.statusBar = statusBar;
        replaceContentPaneChild(oldValue, this.statusBar, BorderLayout.SOUTH);        
        firePropertyChange("statusBar", oldValue, this.statusBar);
    }

}

