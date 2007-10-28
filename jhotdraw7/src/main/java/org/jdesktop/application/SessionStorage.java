
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.application;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;


/**
 * Support for storing GUI state that persists between Application sessions.  
 * <p> 
 * This class simplifies the common task of saving a little bit of an
 * application's GUI "session" state when the application shuts down,
 * and then restoring that state when the application is restarted.
 * Session state is stored on a per component basis, and only for
 * components with a {@link java.awt.Component#getName name} and for
 * which a {@code SessionState.Property} object has been defined.
 * SessionState Properties that preserve the {@code bounds} {@code Rectangle}
 * for Windows, the {@code dividerLocation} for {@code JSliderPanes} and the
 * {@code selectedIndex} for {@code JTabbedPanes} are defined by default.  The
 * {@code ApplicationContext} {@link
 * ApplicationContext#getSessionStorage getSesssionStorage} method
 * provides a shared {@code SessionStorage} object.
 * <p>
 * A typical Application saves session state in its 
 * {@link Application#shutdown shutdown()} method, and then restores
 * session state in {@link Application#startup startup()}:
 * <pre>
 * public class MyApplication extends Application {
 *     &#064;Override protected void shutdown() {
 *         getContext().getSessionStorage().<b>save</b>(mainFrame, "session.xml");
 *     }
 *     &#064;Override protected void startup() {
 *         ApplicationContext appContext = getContext();
 *         appContext.setVendorId("Sun");
 *         appContext.setApplicationId("SessionStorage1");
 *         // ... create the GUI rooted by JFrame mainFrame
 *         appContext.getSessionStorage().<b>restore</b>(mainFrame, "session.xml");
 *     }
 *     // ...
 * }
 * </pre>
 * In this example, the bounds of {@code mainFrame} as well the
 * session state for any of its {@code JSliderPane} or {@code
 * JTabbedPane} will be saved when the application shuts down, and
 * restored when the applications starts up again.  Note: error
 * handling has been omitted from the example.
 * <p>
 * Session state is stored locally, relative to the user's 
 * home directory, by the {@code LocalStorage}
 * {@link LocalStorage#save save} and {@link LocalStorage#save load}
 * methods.  The {@code startup} method must set the 
 * {@code ApplicationContext} {@code vendorId} and {@code applicationId} 
 * properties to ensure that the correct 
 * {@link LocalStorage#getDirectory local directory} is selected on 
 * all platforms.  For example, on Windows XP, the full pathname 
 * for filename {@code "session.xml"} is typically:
 * <pre>
 * ${userHome}\Application Data\${vendorId}\${applicationId}\session.xml
 * </pre>
 * Where the value of {@code ${userHome}} is the the value of
 * the Java System property  {@code "user.home"}.  On Solaris or
 * Linux the file is:
 * <pre>
 * ${userHome}/.${applicationId}/session.xml
 * </pre>
 * and on OSX:
 * <pre>
 * ${userHome}/Library/Application Support/${applicationId}/session.xml
 * </pre>
 *
 * @see ApplicationContext#getSessionStorage
 * @see LocalStorage
 */
public class SessionStorage {
    private static Logger logger = Logger.getLogger(SessionStorage.class.getName());
    private final Map<Class, Property> propertyMap;
    private final ApplicationContext context;

    /**
     * Constructs a SessionStorage object.  The following {@link
     * Property Property} objects are registered by default:
     * <p>
     * <table border="1" cellpadding="4%">
     *     <tr>
     *       <th>Base Component Type</th>
     *       <th>sessionState Property</th>
     *       <th>sessionState Property Value</th>
     *     </tr>
     *     <tr>
     *       <td>Window</td>
     *       <td>WindowProperty</td>
     *       <td>WindowState</td>
     *     </tr>
     *     <tr>
     *       <td>JTabbedPane</td>
     *       <td>TabbedPaneProperty</td>
     *       <td>TabbedPaneState</td>
     *     </tr>
     *     <tr>
     *       <td>JSplitPane</td>
     *       <td>SplitPaneProperty</td>
     *       <td>SplitPaneState</td>
     *     </tr>
     *     <tr>
     *       <td>JTable</td>
     *       <td>TableProperty</td>
     *       <td>TableState</td>
     *     </tr>
     * </table>
     * <p>
     * Applications typically would not create a {@code SessionStorage}
     * object directly, they'd use the shared ApplicationContext value:
     * <pre>
     * ApplicationContext ctx = Application.getInstance(MyApplication.class).getContext();
     * SessionStorage ss = ctx.getSesssionStorage();
     * </pre>
     * 
     * FIXME - @param javadoc
     * @see ApplicationContext#getSessionStorage
     * @see #getProperty(Class)
     * @see #getProperty(Component)
     */
    protected SessionStorage(ApplicationContext context) {
        if (context == null) {
            throw new IllegalArgumentException("null context");
        }
        this.context = context;
	propertyMap = new HashMap<Class, Property>();
	propertyMap.put(Window.class, new WindowProperty());
	propertyMap.put(JTabbedPane.class, new TabbedPaneProperty());
	propertyMap.put(JSplitPane.class, new SplitPaneProperty());
	propertyMap.put(JTable.class, new TableProperty());
    }

    // FIXME - documentation
    protected final ApplicationContext getContext() {
        return context;
    }

    private void checkSaveRestoreArgs(Component root, String fileName) {
	if (root == null) {
	    throw new IllegalArgumentException("null root");
	}
	if (fileName == null) {
	    throw new IllegalArgumentException("null fileName");
	}
    }

    /* At some point we may replace this with a more complex scheme.
     */
    private String getComponentName(Component c) {
	return c.getName();
    }

    /* Return a string that uniquely identifies this component, or null
     * if Component c doesn't have a name per getComponentName().  The
     * pathname is basically the name of all of the components, starting 
     * with c, separated by "/".  This path is the reverse of what's 
     * typical, the first path element is c's name, rather than the name
     * of c's root Window or Applet.  That way pathnames can be 
     * distinguished without comparing much of the string.  The names
     * of intermediate components *can* be null, we substitute 
     * "[type][z-order]" for the name.  Here's an example:
     * 
     * JFrame myFrame = new JFrame();
     * JPanel p = new JPanel() {};  // anonymous JPanel subclass
     * JButton myButton = new JButton();   
     * myButton.setName("myButton");
     * p.add(myButton);
     * myFrame.add(p);
     * 
     * getComponentPathname(myButton) => 
     * "myButton/AnonymousJPanel0/null.contentPane/null.layeredPane/JRootPane0/myFrame"
     * 
     * Notes about name usage in AWT/Swing: JRootPane (inexplicably) assigns 
     * names to it's children (layeredPane, contentPane, glassPane); 
     * all AWT components lazily compute a name.  If we hadn't assigned the
     * JFrame a name, it's name would have been "frame0".
     */
    private String getComponentPathname(Component c) {
	String name = getComponentName(c);
	if (name == null) {
	    return null;
	}
	StringBuilder path = new StringBuilder(name);
	while((c.getParent() != null) && !(c instanceof Window) && !(c instanceof Applet)) {
	    c = c.getParent();
	    name = getComponentName(c);
	    if (name == null) {
		int n = c.getParent().getComponentZOrder(c);
		if (n >= 0) {
		    Class cls = c.getClass();
		    name = cls.getSimpleName();
		    if (name.length() == 0) {
			name = "Anonymous" + cls.getSuperclass().getSimpleName();
		    }
		    name = name + n;
		}
		else {
		    // Implies that the component tree is changing
		    // while we're computing the path. Punt.
		    logger.warning("Couldn't compute pathname for " + c);
		    return null;
		}
	    }
	    path.append("/").append(name);
	}
	return path.toString();
    }

    /* Recursively walk the component tree, breadth first, storing the
     * state - Property.getSessionState() - of named components under 
     * their pathname (the key) in stateMap.
     * 
     * Note: the breadth first tree-walking code here should remain 
     * structurally identical to restoreTree().
     */
    private void saveTree(List<Component> roots, Map<String, Object> stateMap) {
	List<Component> allChildren = new ArrayList<Component>();
	for (Component root : roots) {
	    if (root != null) {
		Property p = getProperty(root);
		if (p != null) {
		    String pathname = getComponentPathname(root);
		    if (pathname != null) {
			Object state = p.getSessionState(root);
			if (state != null) {
			    stateMap.put(pathname, state);
			}
		    }
		}
	    }
	    if (root instanceof Container) {
		Component[] children = ((Container)root).getComponents();
		if ((children != null) && (children.length > 0)) {
		    Collections.addAll(allChildren, children);
		}
	    }
	}
	if (allChildren.size() > 0) {
	    saveTree(allChildren, stateMap);
	}
    }

    /**
     * Saves the state of each named component in the specified hierarchy to 
     * a file using {@link LocalStorage#save LocalStorage.save(fileName)}.
     * Each component is visited in breadth-first order: if a {@code Property}
     * {@link #getProperty(Component) exists} for that component,
     * and the component has a {@link java.awt.Component#getName name}, then
     * its {@link Property#getSessionState state} is saved.  
     * <p>
     * Component names can be any string however they must be unique
     * relative to the name's of the component's siblings.  Most Swing
     * components do not have a name by default, however there are
     * some exceptions: JRootPane (inexplicably) assigns names to it's
     * children (layeredPane, contentPane, glassPane); and all AWT
     * components lazily compute a name, so JFrame, JDialog, and
     * JWindow also have a name by default.
     * <p>
     * The type of sessionState values (i.e. the type of values
     * returned by {@code Property.getSessionState}) must be one those
     * supported by {@link java.beans.XMLEncoder XMLEncoder} and
     * {@link java.beans.XMLDecoder XMLDecoder}, for example beans
     * (null constructor, read/write properties), primitives, and
     * Collections.  Java bean classes and their properties must be
     * public.  Typically beans defined for this purpose are little
     * more than a handful of simple properties.  The JDK 6
     * &#064;ConstructorProperties annotation can be used to eliminate
     * the need for writing set methods in such beans, e.g.
     * <pre>
     * public class FooBar {
     *     private String foo, bar;
     *     // Defines the mapping from constructor params to properties
     *     &#064;ConstructorProperties({"foo", "bar"})
     *     public FooBar(String foo, String bar) {
     *         this.foo = foo;
     *         this.bar = bar;
     *     }
     *     public String getFoo() { return foo; }  // don't need setFoo
     *     public String getBar() { return bar; }  // don't need setBar
     * }
     * </pre>
     * 
     * @param root the root of the Component hierarchy to be saved.
     * @param fileName the {@code LocalStorage} filename.
     * @see #restore
     * @see ApplicationContext#getLocalStorage
     * @see LocalStorage#save
     * @see #getProperty(Component)
     */
    public void save(Component root, String fileName) throws IOException {
	checkSaveRestoreArgs(root, fileName);
	Map<String, Object> stateMap = new HashMap<String, Object>();
	saveTree(Collections.singletonList(root), stateMap);
	LocalStorage lst = getContext().getLocalStorage();
	lst.save(stateMap, fileName);
    }

    /* Recursively walk the component tree, breadth first, restoring the
     * state - Property.setSessionState() - of named components for which 
     * there's a non-null entry under the component's pathName in 
     * stateMap.
     * 
     * Note: the breadth first tree-walking code here should remain 
     * structurally identical to saveTree().
     */
    private void restoreTree(List<Component> roots, Map<String, Object> stateMap) {
	List<Component> allChildren = new ArrayList<Component>();
	for (Component root : roots) {
	    if (root != null) {
		Property p = getProperty(root);
		if (p != null) {
		    String pathname = getComponentPathname(root);
		    if (pathname != null) {
			Object state = stateMap.get(pathname);
			if (state != null) {
			    p.setSessionState(root, state);
			}
			else {
			    logger.warning("No saved state for " + root);
			}
		    }
		}
	    }
	    if (root instanceof Container) {
		Component[] children = ((Container)root).getComponents();
		if ((children != null) && (children.length > 0)) {
		    Collections.addAll(allChildren, children);
		}
	    }
	}
	if (allChildren.size() > 0) {
	    restoreTree(allChildren, stateMap);
	}
    }

    /**
     * Restores each named component in the specified hierarchy 
     * from the session state loaded from 
     * a file using {@link LocalStorage#save LocalStorage.load(fileName)}.
     * Each component is visited in breadth-first order: if a 
     * {@link #getProperty(Component) Property} exists for that component,
     * and the component has a {@link java.awt.Component#getName name}, then
     * its state is {@link Property#setSessionState restored}. 
     * 
     * @param root the root of the Component hierarchy to be restored.
     * @param fileName the {@code LocalStorage} filename.
     * @see #save
     * @see ApplicationContext#getLocalStorage
     * @see LocalStorage#save
     * @see #getProperty(Component)
     */
    public void restore(Component root, String fileName) throws IOException {
	checkSaveRestoreArgs(root, fileName);
	LocalStorage lst = getContext().getLocalStorage();
	Map<String, Object> stateMap = (Map<String, Object>)(lst.load(fileName));
	if (stateMap != null) {
	    restoreTree(Collections.singletonList(root), stateMap);
	}
    }

    /**
     * Defines the {@code sessionState} property.  The value of this 
     * property is the GUI state that should be preserved across 
     * sessions for the specified component.  The type of sessionState 
     * values just one those supported by 
     * {@link java.beans.XMLEncoder XMLEncoder} and 
     * {@link java.beans.XMLDecoder XMLDecoder}, for example beans
     * (null constructor, read/write properties), primitives, and
     * Collections.
     * 
     * @see #putProperty
     * @see #getProperty(Class)
     * @see #getProperty(Component)
     */
    public interface Property {
	/**
	 * Return the value of the {@code sessionState} property, typically
	 * a Java bean or a Collection the defines the {@code Component} state
	 * that should be preserved across Application sessions.  This 
	 * value will be stored with {@link java.beans.XMLEncoder XMLEncoder},
	 * loaded with {@link java.beans.XMLDecoder XMLDecoder}, and 
	 * passed to {@code setSessionState} to restore the Component's 
	 * state.
	 * 
	 * @param c the Component.
	 * @return the {@code sessionState} object for Component {@code c}.
	 * @see #setSessionState
	 */
        Object getSessionState(Component c);

	/**
	 * Restore Component {@code c's} {@code sessionState} from the specified
	 * object.
	 * 
	 * @param c the Component.
	 * @param state the value of the {@code sessionState} property.
	 * @see #getSessionState
	 */
        void setSessionState(Component c, Object state);
    }

    /**
     * This Java Bean defines the {@code Window} state preserved across
     * sessions: the Window's {@code bounds}, and the bounds of the 
     * Window's {@code GraphicsConfiguration}, i.e. the bounds of the 
     * screen that the Window appears on.  If the Window is actually a 
     * Frame, we also store its extendedState.  {@code WindowState} objects
     * are stored and restored by the {@link WindowProperty WindowProperty}
     * class.  
     *
     * @see WindowProperty
     * @see #save
     * @see #restore
     */
    public static class WindowState {
	private final Rectangle bounds;
	private Rectangle gcBounds = null;
	private int screenCount;
        private int frameState = Frame.NORMAL;
	public WindowState() {
	    bounds = new Rectangle();
	}
	public WindowState(Rectangle bounds, Rectangle gcBounds, int screenCount, int frameState) {
	    if (bounds == null) {
		throw new IllegalArgumentException("null bounds");
	    }
	    if (screenCount < 1) {
		throw new IllegalArgumentException("invalid screenCount");
	    }
	    this.bounds = bounds;
	    this.gcBounds = gcBounds;  // can be null
	    this.screenCount = screenCount;
            this.frameState = frameState;
	}
	public Rectangle getBounds() { 
	    return new Rectangle(bounds);
	}
	public void setBounds(Rectangle bounds) {
	    this.bounds.setBounds(bounds);
	}
	public int getScreenCount() {
	    return screenCount;
	}
	public void setScreenCount(int screenCount) {
	    this.screenCount = screenCount;
	}
	public int getFrameState() {
	    return frameState;
	}
	public void setFrameState(int frameState) {
	    this.frameState = frameState;
	}
	public Rectangle getGraphicsConfigurationBounds() {
	    return (gcBounds == null) ? null : new Rectangle(gcBounds);
	}
	public void setGraphicsConfigurationBounds(Rectangle gcBounds) {
	    this.gcBounds = (gcBounds == null) ? null : new Rectangle(gcBounds);
	}
    }

    /**
     * A {@code sessionState} property for Window.
     * <p>
     * This class defines how the session state for {@code Windows} 
     * is {@link WindowProperty#getSessionState saved} and 
     * and {@link WindowProperty#setSessionState restored} in 
     * terms of a property called {@code sessionState}.  The 
     * Window's {@code bounds Rectangle} is saved and restored
     * if the dimensions of the Window's screen have not changed.
     * <p>
     * {@code WindowProperty} is registered for {@code Window.class} by 
     * default, so this class applies to the AWT {@code Window}, 
     * {@code Dialog}, and {@code Frame} class, as well as their
     * Swing counterparts: {@code JWindow}, {@code JDialog}, and 
     * {@code JFrame}.
     * 
     * @see #save
     * @see #restore
     * @see WindowState
     */
    public static class WindowProperty implements Property {
	private void checkComponent(Component component) {
	    if (component == null) {
		throw new IllegalArgumentException("null component");
	    }
	    if (!(component instanceof Window)) {
		throw new IllegalArgumentException("invalid component");
	    }
	}

	private int getScreenCount() {
	    return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
	}

	/**
	 * Returns a {@link WindowState WindowState} object
	 * for {@code Window c}.
	 * <p>
	 * Throws an {@code IllegalArgumentException} if {@code Component c}
	 * isn't a non-null {@code Window}.
	 * 
	 * @param c the {@code Window} whose bounds will be stored
	 *     in a {@code WindowState} object.
	 * @return the {@code WindowState} object
	 * @see #setSessionState
	 * @see WindowState
	 */
        public Object getSessionState(Component c) {
	    checkComponent(c);
            int frameState = Frame.NORMAL;
            if (c instanceof Frame) {
                frameState = ((Frame)c).getExtendedState();
            }
	    GraphicsConfiguration gc = c.getGraphicsConfiguration();
	    Rectangle gcBounds = (gc == null) ? null : gc.getBounds();
            Rectangle frameBounds = c.getBounds();
            /* If this is a JFrame created by FrameView and it's been maximized,
             * retrieve the frame's normal (not maximized) bounds.  More info:
             * see FrameStateListener#windowStateChanged in FrameView.
             */
            if ((c instanceof JFrame) && (0 != (frameState & Frame.MAXIMIZED_BOTH))) {
                String clientPropertyKey = "WindowState.normalBounds";
                Object r = ((JFrame)c).getRootPane().getClientProperty(clientPropertyKey);
                if (r instanceof Rectangle) {
                    frameBounds = (Rectangle)r;
                }
            }
	    return new WindowState(frameBounds, gcBounds, getScreenCount(), frameState);
	}

	/**
	 * Restore the {@code Window's} bounds if the dimensions of its
	 * screen ({@code GraphicsConfiguration}) haven't changed, the 
	 * number of screens hasn't changed, and the 
	 * {@link Window#isLocationByPlatform isLocationByPlatform}
	 * property, which indicates that native Window manager should
	 * pick the Window's location, is false.  More precisely:
	 * <p>
	 * If {@code state} is non-null, and Window {@code c's} 
	 * {@code GraphicsConfiguration} 
	 * {@link GraphicsConfiguration#getBounds bounds} matches
	 * the {@link WindowState#getGraphicsConfigurationBounds WindowState's value},
	 * and Window {@code c's} 
	 * {@link Window#isLocationByPlatform isLocationByPlatform}
	 * property is false, then set the Window's to the 
	 * {@link WindowState#getBounds saved value}.
	 * <p>
	 * Throws an {@code IllegalArgumentException} if {@code c} is 
	 * not a {@code Window} or if {@code state} is non-null
	 * but not an instance of {@link WindowState}.
	 * 
	 * @param c the Window whose state is to be restored
	 * @param state the {@code WindowState} to be restored
	 * @see #getSessionState
	 * @see WindowState
	 */
        public void setSessionState(Component c, Object state) {
	    checkComponent(c);
	    if ((state != null) && !(state instanceof WindowState)) {
		throw new IllegalArgumentException("invalid state");
	    }
	    Window w = (Window)c;
	    if (!w.isLocationByPlatform() && (state != null)) {
		WindowState windowState = (WindowState)state;
		Rectangle gcBounds0 = windowState.getGraphicsConfigurationBounds();
		int sc0 = windowState.getScreenCount();
		GraphicsConfiguration gc = c.getGraphicsConfiguration();
		Rectangle gcBounds1 = (gc == null) ? null : gc.getBounds();
		int sc1 = getScreenCount();
		if ((gcBounds0 != null) && (gcBounds0.equals(gcBounds1)) && (sc0 == sc1)) {
		    w.setBounds(windowState.getBounds());
		}
                if (w instanceof Frame) {
                    ((Frame)w).setExtendedState(windowState.getFrameState());
                }
	    }
	}
    }

    /**
     * This Java Bean record the {@code selectedIndex} and {@code
     * tabCount} properties of a {@code JTabbedPane}.  A {@code
     * TabbedPaneState} object created by {@link
     * TabbedPaneProperty#getSessionState} and used to restore the
     * selected tab by {@link TabbedPaneProperty#setSessionState}.
     * 
     * @see TabbedPaneProperty
     * @see #save
     * @see #restore
     */
    public static class TabbedPaneState {
	private int selectedIndex;
	private int tabCount;
	public TabbedPaneState() {
	    selectedIndex = -1;
	    tabCount = 0;
	}
	public TabbedPaneState(int selectedIndex, int tabCount) {
	    if (tabCount < 0) {
		throw new IllegalArgumentException("invalid tabCount");
	    }
	    if ((selectedIndex < -1) || (selectedIndex > tabCount)) {
		throw new IllegalArgumentException("invalid selectedIndex");
	    }
	    this.selectedIndex = selectedIndex;
	    this.tabCount = tabCount;
	}
	public int getSelectedIndex() { return selectedIndex; }
	public void setSelectedIndex(int selectedIndex) {
	    if (selectedIndex < -1) {
		throw new IllegalArgumentException("invalid selectedIndex");
	    }
	    this.selectedIndex = selectedIndex;
	}
	public int getTabCount() { return tabCount; }
	public void setTabCount(int tabCount) {
	    if (tabCount < 0) {
		throw new IllegalArgumentException("invalid tabCount");
	    }
	    this.tabCount = tabCount;
	}
    }

    /**
     * A {@code sessionState} property for JTabbedPane.
     * <p>
     * This class defines how the session state for {@code JTabbedPanes} 
     * is {@link WindowProperty#getSessionState saved} and 
     * and {@link WindowProperty#setSessionState restored} in 
     * terms of a property called {@code sessionState}.  The 
     * JTabbedPane's {@code selectedIndex} is saved and restored
     * if the number of tabs ({@code tabCount}) hasn't changed.
     * <p>
     * {@code TabbedPaneProperty} is registered for {@code
     * JTabbedPane.class} by default, so this class applies to
     * JTabbedPane and any subclass of JTabbedPane.  One can 
     * override the default with the {@link #putProperty putProperty}
     * method.
     * 
     * @see TabbedPaneState
     * @see #save
     * @see #restore
     */
    public static class TabbedPaneProperty implements Property {
	private void checkComponent(Component component) {
	    if (component == null) {
		throw new IllegalArgumentException("null component");
	    }
	    if (!(component instanceof JTabbedPane)) {
		throw new IllegalArgumentException("invalid component");
	    }
	}

	/**
	 * Returns a {@link TabbedPaneState TabbedPaneState} object
	 * for {@code JTabbedPane c}.
	 * <p>
	 * Throws an {@code IllegalArgumentException} if {@code Component c}
	 * isn't a non-null {@code JTabbedPane}.
	 * 
	 * @param c the {@code JTabbedPane} whose selectedIndex will
	 *     recoreded in a {@code TabbedPaneState} object.
	 * @return the {@code TabbedPaneState} object
	 * @see #setSessionState
	 * @see TabbedPaneState
	 */
        public Object getSessionState(Component c) {
	    checkComponent(c);
	    JTabbedPane p = (JTabbedPane)c;
	    return new TabbedPaneState(p.getSelectedIndex(), p.getTabCount());
	}

	/**
	 * Restore the {@code JTabbedPane's} {@code selectedIndex}
	 * property if the number of {@link JTabbedPane#getTabCount tabs}
	 * has not changed.  
	 * <p>
	 * Throws an {@code IllegalArgumentException} if {@code c} is 
	 * not a {@code JTabbedPane} or if {@code state} is non-null
	 * but not an instance of {@link TabbedPaneState}.
	 * 
	 * @param c the JTabbedPane whose state is to be restored
	 * @param state the {@code TabbedPaneState} to be restored
	 * @see #getSessionState
	 * @see TabbedPaneState
	 */
	public void setSessionState(Component c, Object state) {
	    checkComponent(c);
	    if ((state != null) && !(state instanceof TabbedPaneState)) {
		throw new IllegalArgumentException("invalid state");
	    }
	    JTabbedPane p = (JTabbedPane)c;
	    TabbedPaneState tps = (TabbedPaneState)state;
	    if (p.getTabCount() == tps.getTabCount()) {
		p.setSelectedIndex(tps.getSelectedIndex());
	    }
	}
    }


    /**
     * This Java Bean records the {@code dividerLocation} and {@code
     * orientation} properties of a {@code JSplitPane}.  A {@code
     * SplitPaneState} object created by {@link
     * SplitPaneProperty#getSessionState} and used to restore the
     * selected tab by {@link SplitPaneProperty#setSessionState}.
     * 
     * @see SplitPaneProperty
     * @see #save
     * @see #restore
     */
    public static class SplitPaneState {
	private int dividerLocation = -1;
	private int orientation = JSplitPane.HORIZONTAL_SPLIT;
	private void checkOrientation(int orientation) {
	    if ((orientation != JSplitPane.HORIZONTAL_SPLIT) &&
		(orientation != JSplitPane.VERTICAL_SPLIT)) {
		throw new IllegalArgumentException("invalid orientation");
	    }
	}
	public SplitPaneState() { }
	public SplitPaneState(int dividerLocation, int orientation) {
	    checkOrientation(orientation);
	    if (dividerLocation < -1) {
		throw new IllegalArgumentException("invalid dividerLocation");
	    }
	    this.dividerLocation = dividerLocation;
	    this.orientation = orientation;
	}
	public int getDividerLocation() { return dividerLocation; }
	public void setDividerLocation(int dividerLocation) {
	    if (dividerLocation < -1) {
		throw new IllegalArgumentException("invalid dividerLocation");
	    }
	    this.dividerLocation = dividerLocation;
	}
	public int getOrientation() { return orientation; }
	public void setOrientation(int orientation) {
	    checkOrientation(orientation);
	    this.orientation = orientation;
	}
    }

    /**
     * A {@code sessionState} property for JSplitPane.
     * <p>
     * This class defines how the session state for {@code JSplitPanes} 
     * is {@link WindowProperty#getSessionState saved} and 
     * and {@link WindowProperty#setSessionState restored} in 
     * terms of a property called {@code sessionState}.  The 
     * JSplitPane's {@code dividerLocation} is saved and restored
     * if its {@code orientation} hasn't changed.
     * <p>
     * {@code SplitPaneProperty} is registered for {@code
     * JSplitPane.class} by default, so this class applies to
     * JSplitPane and any subclass of JSplitPane.  One can 
     * override the default with the {@link #putProperty putProperty}
     * method.
     * 
     * @see SplitPaneState
     * @see #save
     * @see #restore
     */
    public static class SplitPaneProperty implements Property {
	private void checkComponent(Component component) {
	    if (component == null) {
		throw new IllegalArgumentException("null component");
	    }
	    if (!(component instanceof JSplitPane)) {
		throw new IllegalArgumentException("invalid component");
	    }
	}

	/**
	 * Returns a {@link SplitPaneState SplitPaneState} object
	 * for {@code JSplitPane c}.  If the split pane's 
	 * {@code dividerLocation} is -1, indicating that either
	 * the divider hasn't been moved, or it's been reset,
	 * then return null.
	 * <p>
	 * Throws an {@code IllegalArgumentException} if {@code Component c}
	 * isn't a non-null {@code JSplitPane}.
	 * 
	 * @param c the {@code JSplitPane} whose dividerLocation will
	 *     recoreded in a {@code SplitPaneState} object.
	 * @return the {@code SplitPaneState} object
	 * @see #setSessionState
	 * @see SplitPaneState
	 */
        public Object getSessionState(Component c) {
	    checkComponent(c);
	    JSplitPane p = (JSplitPane)c;
	    return new SplitPaneState(p.getDividerLocation(), p.getOrientation());
	}

	/**
	 * Restore the {@code JSplitPane's} {@code dividerLocation}
	 * property if its {@link JSplitPane#getOrientation orientation}
	 * has not changed.  
	 * <p>
	 * Throws an {@code IllegalArgumentException} if {@code c} is 
	 * not a {@code JSplitPane} or if {@code state} is non-null
	 * but not an instance of {@link SplitPaneState}.
	 * 
	 * @param c the JSplitPane whose state is to be restored
	 * @param state the {@code SplitPaneState} to be restored
	 * @see #getSessionState
	 * @see SplitPaneState
	 */
	public void setSessionState(Component c, Object state) {
	    checkComponent(c);
	    if ((state != null) && !(state instanceof SplitPaneState)) {
		throw new IllegalArgumentException("invalid state");
	    }
	    JSplitPane p = (JSplitPane)c;
	    SplitPaneState sps = (SplitPaneState)state;
	    if (p.getOrientation() == sps.getOrientation()) {
		p.setDividerLocation(sps.getDividerLocation());
	    }
	}
    }

    /**
     * This Java Bean records the {@code columnWidths} for all
     * of the columns in a JTable.  A width of -1 is used to 
     * mark {@code TableColumns} that are not resizable.
     * 
     * @see TableProperty
     * @see #save
     * @see #restore
     */
    public static class TableState {
	private int[] columnWidths = new int[0];
	private int[] copyColumnWidths(int[] columnWidths) {
	    if (columnWidths == null) {
		throw new IllegalArgumentException("invalid columnWidths");
	    }
	    int[] copy = new int[columnWidths.length];
	    System.arraycopy(columnWidths, 0, copy, 0, columnWidths.length);
	    return copy;
	}
	public TableState() { }
	public TableState(int[] columnWidths) {
	    this.columnWidths = copyColumnWidths(columnWidths);
	}
	public int[] getColumnWidths() { return copyColumnWidths(columnWidths); }
	public void setColumnWidths(int[] columnWidths) {
	    this.columnWidths = copyColumnWidths(columnWidths);
	}
    }


    /**
     * A {@code sessionState} property for JTable
     * <p>
     * This class defines how the session state for {@code JTables}
     * is {@link WindowProperty#getSessionState saved} and 
     * and {@link WindowProperty#setSessionState restored} in 
     * terms of a property called {@code sessionState}.  
     * We save and restore the width of each resizable
     * {@code TableColumn}, if the number of columns haven't 
     * changed.
     * <p>
     * {@code TableProperty} is registered for {@code
     * JTable.class} by default, so this class applies to
     * JTable and any subclass of JTable.  One can 
     * override the default with the {@link #putProperty putProperty}
     * method.
     * 
     * @see TableState
     * @see #save
     * @see #restore
     */
    public static class TableProperty implements Property {
	private void checkComponent(Component component) {
	    if (component == null) {
		throw new IllegalArgumentException("null component");
	    }
	    if (!(component instanceof JTable)) {
		throw new IllegalArgumentException("invalid component");
	    }
	}

	/**
	 * Returns a {@link TableState TableState} object
	 * for {@code JTable c} or null, if none of the JTable's
	 * columns are {@link TableColumn#getResizable resizable}.
	 * A width of -1 is used to mark {@code TableColumns} 
	 * that are not resizable.
	 * <p>
	 * Throws an {@code IllegalArgumentException} if {@code Component c}
	 * isn't a non-null {@code JTable}.
	 * 
	 * @param c the {@code JTable} whose columnWidths will be 
	 *     saved in a {@code TableState} object.
	 * @return the {@code TableState} object or null
	 * @see #setSessionState
	 * @see TableState
	 */
        public Object getSessionState(Component c) {
	    checkComponent(c);
	    JTable table = (JTable)c;
	    int[] columnWidths = new int[table.getColumnCount()];
	    boolean resizableColumnExists = false;
	    for (int i = 0; i < columnWidths.length; i++) {
		TableColumn tc = table.getColumnModel().getColumn(i);
		columnWidths[i] = (tc.getResizable()) ? tc.getWidth() : -1;
		if (tc.getResizable()) {
		    resizableColumnExists = true;
		}
	    }
	    return (resizableColumnExists) ? new TableState(columnWidths) : null;
	}

	/**
	 * Restore the width of each resizable {@code TableColumn}, if
	 * the number of columns haven't changed.
	 * <p>
	 * Throws an {@code IllegalArgumentException} if {@code c} is 
	 * not a {@code JTable} or if {@code state} is not an instance 
	 * of {@link TableState}.
	 * 
	 * @param c the JTable whose column widths are to be restored
	 * @param state the {@code TableState} to be restored
	 * @see #getSessionState
	 * @see TableState
	 */
	public void setSessionState(Component c, Object state) {
	    checkComponent(c);
	    if (!(state instanceof TableState)) {
		throw new IllegalArgumentException("invalid state");
	    }
	    JTable table = (JTable)c;
	    int[] columnWidths = ((TableState)state).getColumnWidths();
	    if (table.getColumnCount() == columnWidths.length) {
		for (int i = 0; i < columnWidths.length; i++) {
		    if (columnWidths[i] != -1) {
			TableColumn tc = table.getColumnModel().getColumn(i);
			if (tc.getResizable()) {
			    tc.setPreferredWidth(columnWidths[i]);
			}
		    }
		}
	    }
	}
    }

    private void checkClassArg(Class cls) {
	if (cls == null) {
	    throw new IllegalArgumentException("null class");
	}
    }

    /**
     * Returns the {@code Property} object that was 
     * {@link #putProperty registered} for the specified class 
     * or a superclass.  If no Property has been registered, 
     * return null.  To lookup the session state {@code Property} 
     * for a {@code Component} use {@link #getProperty(Component)}.
     * <p>
     * Throws an {@code IllegalArgumentException} if {@code cls} is null.
     * 
     * @param cls the class to which the returned {@code Property} applies
     * @return the {@code Property} registered with {@code putProperty} for
     *   the specified class or the first one registered for a superclass
     *   of {@code cls}.
     * @see #getProperty(Component)
     * @see #putProperty
     * @see #save
     * @see #restore
     */
    public Property getProperty(Class cls) {
	checkClassArg(cls);
	while(cls != null) {
	    Property p = propertyMap.get(cls);
	    if (p != null) { return p; }
	    cls = cls.getSuperclass();
	}
	return null;
    }

    
    /**
     * Register a {@code Property} for the specified class.  One can clear
     * the {@code Property} for a class by setting the entry to null:
     * <pre>
     * sessionStorage.putProperty(myClass.class, null);
     * </pre>
     * <p>
     * Throws an {@code IllegalArgumentException} if {@code cls} is null.
     * 
     * @param cls the class to which {@code property} applies.
     * @param property the {@code Property} object to register or null.
     * @see #getProperty(Component)
     * @see #getProperty(Class)
     * @see #save
     * @see #restore
     */
    public void putProperty (Class cls, Property property) {
	checkClassArg(cls);
	propertyMap.put(cls, property);
    }

    /**
     * If a {@code sessionState Property} object exists for the 
     * specified Component return it, otherwise return null.  This method
     * is used by the {@link #save save} and {@link #restore restore} methods 
     * to lookup the  {@code sessionState Property} object for each component
     * to whose session state is to be saved or restored.
     * <p>
     * The {@code putProperty} method registers a Property object for 
     * a class.  One can specify a Property object for a single Swing
     * component by setting the component's client property, like this:
     * <pre>
     * myJComponent.putClientProperty(SessionState.Property.class, myProperty);
     * </pre>
     * One can also create components that implement the 
     * {@code SessionState.Property} interface directly.
     * 
     * @return if  {@code Component c} implements {@code Session.Property}, then 
     *     {@code c}, if {@code c} is a {@code JComponent} with a 
     *     {@code Property} valued 
     *     {@link javax.swing.JComponent#getClientProperty client property} under
     *     (client property key) {@code SessionState.Property.class}, then
     *     return that, otherwise return the value of 
     *     {@code getProperty(c.getClass())}.
     * <p>
     * Throws an {@code IllegalArgumentException} if {@code Component c} is null.
     * 
     * @see javax.swing.JComponent#putClientProperty
     * @see #getProperty(Class)
     * @see #putProperty
     * @see #save
     * @see #restore
     */
    public final Property getProperty(Component c) {
	if (c == null) {
	    throw new IllegalArgumentException("null component");
	}
	if (c instanceof Property) {
	    return (Property)c;
	}
	else {
	    Property p = null;
	    if (c instanceof JComponent) {
		Object v = ((JComponent)c).getClientProperty(Property.class);
		p = (v instanceof Property) ? (Property)v : null;
	    }
	    return (p != null) ? p : getProperty(c.getClass());
	}
    }
}
