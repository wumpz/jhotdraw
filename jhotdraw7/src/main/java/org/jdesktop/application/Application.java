
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.application;

import java.awt.ActiveEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.PaintEvent;
import java.lang.reflect.Constructor;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


/**
 * The base class for Swing applications. 
 * 
 * <p> 
 * This class defines a simple lifecyle for Swing applications: {@code
 * initialize}, {@code startup}, {@code ready}, and {@code shutdown}.
 * The {@code Application's} {@code startup} method is responsible for
 * creating the initial GUI and making it visible, and the {@code
 * shutdown} method for hiding the GUI and performing any other
 * cleanup actions before the application exits.  The {@code initialize} 
 * method can be used configure system properties that must be set
 * before the GUI is constructed and the {@code ready}
 * method is for applications that want to do a little bit of extra
 * work once the GUI is "ready" to use.  Concrete subclasses must
 * override the {@code startup} method.
 * <p>
 * Applications are started with the static {@code launch} method.
 * Applications use the {@code ApplicationContext} {@link
 * Application#getContext singleton} to find resources,
 * actions, local storage, and so on.
 * <p>
 * All {@code Application} subclasses must override {@code startup}
 * and they should call {@link #exit} (which
 * calls {@code shutdown}) to exit.
 * Here's an example of a complete "Hello World" Application:
 * <pre>
 * public class MyApplication extends Application {
 *     JFrame mainFrame = null;
 *     &#064;Override protected void startup() {
 *         mainFrame = new JFrame("Hello World");
 *         mainFrame.add(new JLabel("Hello World"));
 *         mainFrame.addWindowListener(new MainFrameListener());
 *         mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
 *         mainFrame.pack();
 *         mainFrame.setVisible(true);
 *     }
 *     &#064;Override protected void shutdown() {
 *         mainFrame.setVisible(false);
 *     }
 *     private class MainFrameListener extends WindowAdapter {
 *         public void windowClosing(WindowEvent e) {
 *            exit();
 *         }
 *     }
 *     public static void main(String[] args) {
 *         Application.launch(MyApplication.class, args);
 *     }
 * }
 * </pre>
 * <p>
 * The {@code mainFrame's} {@code defaultCloseOperation} is set
 * to {@code DO_NOTHING_ON_CLOSE} because we're handling attempts
 * to close the window by calling 
 * {@code ApplicationContext} {@link #exit}.
 * <p>
 * Simple single frame applications like the example can be defined
 * more easily with the {@link SingleFrameApplication
 * SingleFrameApplication} {@code Application} subclass.
 * 
 * <p> 
 * All of the Application's methods are called (must be called) on
 * the EDT.
 * 
 * <p>
 * All but the most trivial applications should define a ResourceBundle
 * in the resources subpackage with the same name as the application class (like {@code
 * resources/MyApplication.properties}).  This ResourceBundle contains 
 * resources shared by the entire application and should begin with the
 * following the standard Application resources:
 * <pre>
 * Application.name = A short name, typically just a few words
 * Application.id = Suitable for Application specific identifiers, like file names
 * Application.title = A title suitable for dialogs and frames
 * Application.version = A version string that can be incorporated into messages
 * Application.vendor = A proper name, like Sun Microsystems, Inc.
 * Application.vendorId = suitable for Application-vendor specific identifiers, like file names.
 * Application.homepage = A URL like http://www.javadesktop.org
 * Application.description =  One brief sentence
 * Application.lookAndFeel = either system, default, or a LookAndFeel class name
 * </pre>
 * <p>
 * The {@code Application.lookAndFeel} resource is used to initialize the
 * {@code UIManager lookAndFeel} as follows:
 * <ul>
 * <li>{@code system} - the system (native) look and feel</li>
 * <li>{@code default} - use the JVM default, typically the cross platform look and feel</li>
 * <li>a LookAndFeel class name - use the specified class
 * </ul>
 * 
 * @see SingleFrameApplication
 * @see ApplicationContext
 * @see UIManager#setLookAndFeel
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */

@ProxyActions({"cut", "copy", "paste", "delete"})

public abstract class Application extends AbstractBean {
    private static final Logger logger = Logger.getLogger(Application.class.getName());
    private static Application application = null;
    private final List<ExitListener> exitListeners;
    private final ApplicationContext context;

    /**
     * Not to be called directly, see {@link #launch launch}.
     * <p>
     * Subclasses can provide a no-args construtor
     * to initialize private final state however GUI
     * initialization, and anything else that might refer to 
     * public API, should be done in the {@link #startup startup}
     * method.  
     */
    protected Application() {
	exitListeners = new CopyOnWriteArrayList<ExitListener>();
        context = new ApplicationContext();
    }

    /**
     * Creates an instance of the specified {@code Application}
     * subclass, sets the {@code ApplicationContext} {@code
     * application} property, and then calls the new {@code
     * Application's} {@code startup} method.  The {@code launch} method is
     * typically called from the Application's {@code main}:
     * <pre>
     *     public static void main(String[] args) {
     *         Application.launch(MyApplication.class, args);
     *     }
     * </pre>
     * The {@code applicationClass} constructor and {@code startup} methods
     * run on the event dispatching thread.
     * 
     * @param applicationClass the {@code Application} class to launch
     * @param args {@code main} method arguments
     * @see #shutdown
     * @see ApplicationContext#getApplication
     */
    public static synchronized <T extends Application> void launch(final Class<T> applicationClass, final String[] args) {
	Runnable doCreateAndShowGUI = new Runnable() {
	    public void run() {
		try {
		    application = create(applicationClass);
                    application.initialize(args);
		    application.startup();
                    application.waitForReady();
		}
		catch (Exception e) {
                    String msg = String.format("Application %s failed to launch", applicationClass);
		    logger.log(Level.SEVERE, msg, e);
                    throw(new Error(msg, e));
		}
	    }
	};
	SwingUtilities.invokeLater(doCreateAndShowGUI);
    }

    /* Initializes the ApplicationContext applicationClass and application
     * properties.  
     * 
     * Note that, as of Java SE 5, referring to a class literal
     * doesn't force the class to be loaded.  More info:
     * http://java.sun.com/javase/technologies/compatibility.jsp#literal
     * It's important to perform these initializations early, so that
     * Application static blocks/initializers happen afterwards.
     */
    static <T extends Application> T create(Class<T> applicationClass) throws Exception {

	/* A common mistake for privileged applications that make
	 * network requests (and aren't applets or web started) is to
	 * not configure the http.proxyHost/Port system properties.
	 * We paper over that issue here.
	 */
	try {
	    System.setProperty("java.net.useSystemProxies", "true"); 
	}
	catch (SecurityException ignoreException) {
	    // Unsigned apps can't set this property. 
	}

        /* Construct the Application object.  The following
         * complications, relative to just calling
         * applicationClass.newInstance(), allow a privileged app to
         * have a private static inner Application subclass.
         */
        Constructor<T> ctor = applicationClass.getDeclaredConstructor();
        if (!ctor.isAccessible()) {
            try {
                ctor.setAccessible(true);
            }
            catch (SecurityException ignore) {
                // ctor.newInstance() will throw an IllegalAccessException
            }
        }
        T application = ctor.newInstance();

        /* Initialize the ApplicationContext application properties
         */
        ApplicationContext ctx = application.getContext();
        ctx.setApplicationClass(applicationClass);
        ctx.setApplication(application);

	/* Load the application resource map, notably the 
	 * Application.* properties.
	 */
	ResourceMap appResourceMap = ctx.getResourceMap();

        appResourceMap.putResource("platform", platform());

	/* Initialize the UIManager lookAndFeel property with the
	 * Application.lookAndFeel resource.  If the the resource
	 * isn't defined we default to "system".
	 */
	String key = "Application.lookAndFeel";
	String lnfResource = appResourceMap.getString(key);
	String lnf = (lnfResource == null) ? "system" : lnfResource;
	try {
	    if (lnf.equalsIgnoreCase("system")) {
		String name = UIManager.getSystemLookAndFeelClassName();
		UIManager.setLookAndFeel(name);
	    }
	    else if (!lnf.equalsIgnoreCase("default")) {
		UIManager.setLookAndFeel(lnf);
	    }
	}
	catch(Exception e) {
	    String s = "Couldn't set LookandFeel " + key + " = \"" + lnfResource + "\"";
	    logger.log(Level.WARNING, s, e);
	}

        return application;
    }

    /* Defines the default value for the platform resource, 
     * either "osx" or "default".
     */
    private static String platform() {
        String platform = "default";
        try {
            String osName = System.getProperty("os.name");
            if ((osName != null) && osName.toLowerCase().startsWith("mac os x")) {
                platform = "osx";
            }
        }
        catch (SecurityException ignore) {
        }
        return platform;
    }

    /* Call the ready method when the eventQ is quiet.
     */
    void waitForReady() {
        new DoWaitForEmptyEventQ().execute();
    }

    /**
     * Responsible for initializations that must occur before the 
     * GUI is constructed by {@code startup}.
     * <p>
     * This method is called by the static {@code launch} method, 
     * before {@code startup} is called. Subclasses that want
     * to do any initialization work before {@code startup} must
     * override it.  The {@code initialize} method 
     * runs on the event dispatching thread.  
     * <p>
     * By default initialize() does nothing.
     * 
     * @param args the main method's arguments.
     * @see #launch
     * @see #startup
     * @see #shutdown
     */
    protected void initialize(String[] args) {
    }

    /**
     * Responsible for starting the application; for creating and showing
     * the initial GUI.
     * <p>
     * This method is called by the static {@code launch} method, 
     * subclasses must override it.  It runs on the event dispatching
     * thread.
     * 
     * @see #launch
     * @see #initialize
     * @see #shutdown
     */
    protected abstract void startup();

    /**
     * Called after the startup() method has returned and there 
     * are no more events on the 
     * {@link Toolkit#getSystemEventQueue system event queue}.
     * When this method is called, the application's GUI is ready 
     * to use.
     * <p>
     * It's usually important for an application to start up as 
     * quickly as possible.  Applications can override this method
     * to do some additional start up work, after the GUI is up
     * and ready to use.
     * 
     * @see #launch
     * @see #startup
     * @see #shutdown
     */
    protected void ready() {
    }

    /** 
     * Called when the application {@link #exit exits}.  
     * Subclasses may override this method to do any cleanup
     * tasks that are neccessary before exiting.  Obviously, you'll want to try
     * and do as little as possible at this point.  This method runs
     * on the event dispatching thread.
     * 
     * @see #startup
     * @see #ready
     * @see #exit
     * @see #addExitListener
     */
    protected void shutdown() {
    }

    /* An event that sets a flag when it's dispatched and another
     * flag, see isEventQEmpty(), that indicates if the event queue
     * was empty at dispatch time.
     */
    private static class NotifyingEvent extends PaintEvent implements ActiveEvent {
        private boolean dispatched = false;
        private boolean qEmpty = false;
        NotifyingEvent(Component c) {
            super(c, PaintEvent.UPDATE, null);
        }
        synchronized boolean isDispatched() { return dispatched; }
        synchronized boolean isEventQEmpty() { return qEmpty; }
        public void dispatch() {
            EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
            synchronized(this) {
		qEmpty = (q.peekEvent() == null);
                dispatched = true;
                notifyAll();
            }
        }
    }

    /* Keep queuing up NotifyingEvents until the event queue is
     * empty when the NotifyingEvent is dispatched().
     */
    private void waitForEmptyEventQ() {
        boolean qEmpty = false;
        JPanel placeHolder  = new JPanel();
        EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
        while (!qEmpty) {
            NotifyingEvent e = new NotifyingEvent(placeHolder);
            q.postEvent(e);
            synchronized(e) {
                while (!e.isDispatched()) {
                    try {
                        e.wait();
                    } 
		    catch (InterruptedException ie) {
                    }
                }
                qEmpty = e.isEventQEmpty();
            }
        }
    }

    /* When the event queue is empty, give the app a chance to do
     * something, now that the GUI is "ready".
     */
    private class DoWaitForEmptyEventQ extends Task<Void, Void> {
        DoWaitForEmptyEventQ() { super(Application.this); }
	@Override protected Void doInBackground() {
	    waitForEmptyEventQ();
	    return null;
	}
	@Override protected void finished() { 
	    ready();	
	}
    }


    /**
     * Gracefully shutdown the application, calls {@code exit(null)}
     * This version of exit() is convenient if the decision to exit the
     * application wasn't triggered by an event.
     * 
     * @see #exit(EventObject)
     */
    public final void exit() {
	exit(null);
    }

    /**
     * Gracefully shutdown the application.
     * <p>
     * If none of the {@code ExitListener.canExit()} methods return false,
     * calls the {@code ExitListener.willExit()} methods, then 
     * {@code shutdown()}, and then exits the Application with
     * System.exit(0).  Exceptions thrown while running willExit() or shutdown() 
     * are logged but otherwise ignored. 
     * <p>
     * If the caller is responding to an GUI event, it's helpful to pass the 
     * event along so that ExitListeners' canExit methods that want to popup
     * a dialog know on which screen to show the dialog.  For example:
     * <pre>
     * class ConfirmExit implements Application.ExitListener {
     *     public boolean canExit(EventObject e) {
     *         Object source = (e != null) ? e.getSource() : null;
     *         Component owner = (source instanceof Component) ? (Component)source : null;
     *         int option = JOptionPane.showConfirmDialog(owner, "Really Exit?");
     *         return option == JOptionPane.YES_OPTION;
     *     }
     *     public void willExit(EventObejct e) {} 
     * }
     * myApplication.addExitListener(new ConfirmExit());
     * </pre>
     * The {@code eventObject} argument may be null, e.g. if the exit
     * call was triggered by non-GUI code, and {@code canExit}, {@code
     * willExit} methods must guard against the possibility that the
     * {@code eventObject} argument's {@code source} is not a {@code
     * Component}.
     * 
     * @param event the EventObject that triggered this call or null
     * @see #exit()
     * @see #addExitListener
     * @see #removeExitListener
     * @see #shutdown
     */
    public void exit(EventObject event) {
	for (ExitListener listener : exitListeners) {
	    if (!listener.canExit(event)) {
		return;
	    }
	}
	try {
	    for (ExitListener listener : exitListeners) {
		try {
		    listener.willExit(event);
		}
		catch (Exception e) { 
		    logger.log(Level.WARNING, "ExitListener.willExit() failed", e);
		}
	    }
	    shutdown();
	}
	catch (Exception e) { 
	    logger.log(Level.WARNING, "unexpected error in Application.shutdown()", e);
	}
	finally {
	    System.exit(0);
	}
    }

    /**
     * Give the Application a chance to veto an attempt to exit/quit.
     * An {@code ExitListener's} {@code canExit} method should return
     * false if there are pending decisions that the user must make
     * before the app exits.  A typical {@code ExitListener} would
     * prompt the user with a modal dialog.
     * <p>
     * The {@code eventObject} argument will be the the value passed
     * to {@link #exit(EventObject) exit()}.  It may be null.
     * <p>
     * The {@code willExit} method is called after the exit has
     * been confirmed.  An ExitListener that's going to perform
     * some cleanup work should do so in {@code willExit}.
     * <p>
     * {@code ExitListeners} run on the event dispatching thread.
     * 
     * @param event the EventObject that triggered this call or null
     * @see #exit(EventObject)
     * @see #addExitListener
     * @see #removeExitListener
     */
    public interface ExitListener extends EventListener {
	boolean canExit(EventObject event);
	void willExit(EventObject event);
    }

    /**
     * Add an {@code ExitListener} to the list.
     * 
     * @param listener the {@code ExitListener}
     * @see #removeExitListener
     * @see #getExitListeners
     */
    public void addExitListener(ExitListener listener) {
	exitListeners.add(listener);
    }

    /**
     * Remove an {@code ExitListener} from the list.
     * 
     * @param listener the {@code ExitListener}
     * @see #addExitListener
     * @see #getExitListeners
     */
    public void removeExitListener(ExitListener listener) {
	exitListeners.remove(listener);
    }

    /**
     * All of the {@code ExitListeners} added so far.
     * 
     * @return all of the {@code ExitListeners} added so far.
     */
    public ExitListener[] getExitListeners() {
	int size = exitListeners.size();
	return exitListeners.toArray(new ExitListener[size]);
    }

    /**
     * The default {@code Action} for quitting an application,
     * {@code quit} just exits the application by calling {@code exit(e)}.
     * 
     * @param e the triggering event
     * @see #exit(EventObject)
     */
    @Action public void quit(ActionEvent e) {
	exit(e);
    }

    /**
     * The ApplicationContext singleton for this Application.
     * 
     * @return the Application's ApplicationContext singleton
     */
    public final ApplicationContext getContext() {
        return context;
    }

    /**
     * The {@code Application} singleton, or null if {@code launch} hasn't
     * been called yet.  
     * 
     * @return the launched Application singleton.
     * @see Application#launch
     */
    public static synchronized <T extends Application> T getInstance(Class<T> applicationType) {
	return applicationType.cast(application);
    }

    /**
     * The {@code Application} singleton, or a placeholder if {@code
     * launch} hasn't been called yet.  
     * <p>
     * Typically this method is only called after an Application has
     * been launched however in some situations, like tests, it's useful to be 
     * able to get an {@code Application} object without actually
     * launching.  The <i>placeholder</i> Application object provides
     * access to an {@code ApplicationContext} singleton and has
     * the same semantics as launching an Application defined like this:
     * <pre>
     * public class PlaceholderApplication extends Application {
     *     public void startup() { }
     * }
     * Application.launch(PlaceholderApplication.class);
     * </pre>
     * 
     * @return the Application singleton or a placeholder
     * @see Application#launch
     * @see Application#getInstance(Class)
     */
    public static synchronized Application getInstance() {
        if (application == null) {
            application = new NoApplication();
        }
        return application;
    }

    private static class NoApplication extends Application {
        protected NoApplication() {
            ApplicationContext ctx = getContext();
            ctx.setApplicationClass(getClass());
            ctx.setApplication(this);
            ResourceMap appResourceMap = ctx.getResourceMap();
            appResourceMap.putResource("platform", platform());
        }
        protected void startup() {}
    }


    /* Prototype support for the View type */

    public void show(View view) {
        Window window = (Window)view.getRootPane().getParent();
        if (window != null) {
            window.pack();
            window.setVisible(true);
        }
    }

    public void hide(View view) {
        view.getRootPane().getParent().setVisible(false);
    }
}
