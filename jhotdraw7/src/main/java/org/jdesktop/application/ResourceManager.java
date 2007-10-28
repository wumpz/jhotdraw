
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


/**
 * The application's {@code ResourceManager} provides 
 * read-only cached access to resources in {@code ResourceBundles} via the 
 * {@link ResourceMap ResourceMap} class.  {@code ResourceManager} is a
 * property of the {@code ApplicationContext} and most applications
 * look up resources relative to it, like this:
 * <pre>
 * ApplicationContext appContext = Application.getInstance().getContext();
 * ResourceMap resourceMap = appContext.getResourceMap(MyClass.class);
 * String msg = resourceMap.getString("msg");
 * Icon icon = resourceMap.getIcon("icon");
 * Color color = resourceMap.getColor("color");
 * </pre>
 * {@link ApplicationContext#getResourceMap(Class) ApplicationContext.getResourceMap()}
 * just delegates to its {@code ResourceManager}.  The {@code ResourceMap}
 * in this example contains resources from the ResourceBundle named
 * {@code MyClass}, and the rest of the 
 * chain contains resources shared by the entire application.
 * <p>
 * Resources for a class are defined by an eponymous {@code ResourceBundle}
 * in a {@code resources} subpackage.  The Application class itself
 * may also provide resources. A complete
 * description of the naming conventions for ResourceBundles is provided
 * by the {@link #getResourceMap(Class) getResourceMap()} method.
 * <p>
 * The mapping from classes and {@code Application} to a list
 * ResourceBundle names is handled by two protected methods: 
 * {@link #getClassBundleNames(Class) getClassBundleNames},
 * {@link #getApplicationBundleNames() getApplicationBundleNames}.
 * Subclasses could override these methods to append additional 
 * ResourceBundle names to the default lists.
 * 
 * @see ApplicationContext#getResourceManager
 * @see ApplicationContext#getResourceMap
 * @see ResourceMap
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class ResourceManager extends AbstractBean {
    private static final Logger logger = Logger.getLogger(ResourceManager.class.getName());
    private final Map<String, ResourceMap> resourceMaps;
    private final ApplicationContext context;
    private List<String> applicationBundleNames = null;
    private ResourceMap appResourceMap = null;

    /**
     * Construct a {@code ResourceManager}.  Typically applications
     * will not create a ResourceManager directly, they'll retrieve
     * the shared one from the {@code ApplicationContext} with:
     * <pre>
     * Application.getInstance().getContext().getResourceManager()
     * </pre>
     * Or just look up {@code ResourceMaps} with the ApplicationContext
     * convenience method:
     * <pre>
     * Application.getInstance().getContext().getResourceMap(MyClass.class)
     * </pre>
     * 
     * FIXME - @param javadoc
     * @see ApplicationContext#getResourceManager
     * @see ApplicationContext#getResourceMap
     */
    protected ResourceManager(ApplicationContext context) {
        if (context == null) {
            throw new IllegalArgumentException("null context");
        }
        this.context = context;
	resourceMaps = new ConcurrentHashMap<String, ResourceMap>();
    }

    // FIXME - documentation
    protected final ApplicationContext getContext() {
        return context;
    }

    /* Returns a read-only list of the ResourceBundle names for all of
     * the classes from startClass to (including) stopClass.  The
     * bundle names for each class are #getClassBundleNames(Class).
     * The list is in priority order: resources defined in bundles
     * earlier in the list shadow resources with the same name that
     * appear bundles that come later.
     */
    private List<String> allBundleNames(Class startClass, Class stopClass) {
	List<String> bundleNames = new ArrayList<String>();
        Class limitClass = stopClass.getSuperclass(); // could be null
	for(Class c = startClass; c != limitClass; c = c.getSuperclass()) {
            bundleNames.addAll(getClassBundleNames(c));
	}
        return Collections.unmodifiableList(bundleNames);
    }

    private String bundlePackageName(String bundleName) {
	int i = bundleName.lastIndexOf(".");
	return (i == -1) ? "" : bundleName.substring(0, i);
    }

    /* Creates a parent chain of ResourceMaps for the specfied
     * ResourceBundle names.  One ResourceMap is created for each
     * subsequence of ResourceBundle names with a common bundle
     * package name, i.e. with a common resourcesDir.  The parent 
     * of the final ResourceMap in the chain is root.
     */
    private ResourceMap createResourceMapChain(ClassLoader cl, ResourceMap root, ListIterator<String> names) {
	if (!names.hasNext()) {
	    return root;
	}
	else {
	    String bundleName0 = names.next();
	    String rmBundlePackage = bundlePackageName(bundleName0);
	    List<String> rmNames = new ArrayList<String>();
	    rmNames.add(bundleName0); 
            while(names.hasNext()) {
                String bundleName = names.next();
                if (rmBundlePackage.equals(bundlePackageName(bundleName))) {
                    rmNames.add(bundleName);
                }
                else {
                    names.previous();
                    break;
                }
            }
	    ResourceMap parent = createResourceMapChain(cl, root, names);
	    return createResourceMap(cl, parent, rmNames);
	}
    }

    /* Lazily creates the Application ResourceMap chain,
     * appResourceMap.  If the Application hasn't been launched yet,
     * i.e. if the ApplicationContext applicationClass property hasn't
     * been set yet, then the ResourceMap just corresponds to
     * Application.class.
     */
    private ResourceMap getApplicationResourceMap() {
	if (appResourceMap == null) {
            List<String> appBundleNames = getApplicationBundleNames();
	    Class appClass = getContext().getApplicationClass();
	    if (appClass == null) {
		logger.warning("getApplicationResourceMap(): no Application class");
		appClass = Application.class;
	    }
	    ClassLoader classLoader = appClass.getClassLoader();
	    appResourceMap = createResourceMapChain(classLoader, null, appBundleNames.listIterator());
	}
	return appResourceMap;
    }

    /* Lazily creates the ResourceMap chain for the the class from 
     * startClass to stopClass.
     */
    private ResourceMap getClassResourceMap(Class startClass, Class stopClass) {
	String classResourceMapKey = startClass.getName() + stopClass.getName();
	ResourceMap classResourceMap = resourceMaps.get(classResourceMapKey);
	if (classResourceMap == null) {
            List<String> classBundleNames = allBundleNames(startClass, stopClass);
            ClassLoader classLoader = startClass.getClassLoader();
            ResourceMap appRM = getResourceMap();
	    classResourceMap = createResourceMapChain(classLoader, appRM, classBundleNames.listIterator());
	    resourceMaps.put(classResourceMapKey, classResourceMap);
	}
	return classResourceMap;
    }
    
    /**
     * Returns a {@link ResourceMap#getParent chain} of {@code ResourceMaps}
     * that encapsulate the {@code ResourceBundles} for each class
     * from {@code startClass} to (including) {@code stopClass}.  The 
     * final link in the chain is Application ResourceMap chain, i.e.
     * the value of {@link #getResourceMap() getResourceMap()}.
     * <p>
     * The ResourceBundle names for the chain of ResourceMaps
     * are defined by  {@link #getClassBundleNames} and 
     * {@link #getApplicationBundleNames}.  Collectively they define the
     * standard location for {@code ResourceBundles} for a particular
     * class as the {@code resources} subpackage.  For example, the
     * ResourceBundle for the single class {@code com.myco.MyScreen}, would
     * be named {@code com.myco.resources.MyScreen}.  Typical
     * ResourceBundles are ".properties" files, so: {@code
     * com/foo/bar/resources/MyScreen.properties}.  The following table
     * is a list of the ResourceMaps and their constituent 
     * ResourceBundles for the same example:
     * <p>
     * <table border="1" cellpadding="4%">
     *   <caption><em>ResourceMap chain for class MyScreen in MyApp</em></caption>
     *     <tr>
     *       <th></th>
     *       <th>ResourceMap</th>
     *       <th>ResourceBundle names</th>
     *       <th>Typical ResourceBundle files</th>
     *     </tr>
     *     <tr>
     *       <td>1</td>
     *       <td>class: com.myco.MyScreen</td>
     *       <td>com.myco.resources.MyScreen</td>
     *       <td>com/myco/resources/MyScreen.properties</td>
     *     </tr>
     *     <tr>
     *       <td>2/td>
     *       <td>application: com.myco.MyApp</td>
     *       <td>com.myco.resources.MyApp</td>
     *       <td>com/myco/resources/MyApp.properties</td>
     *     </tr>
     *     <tr>
     *       <td>3</td>
     *       <td>application: javax.swing.application.Application</td>
     *       <td>javax.swing.application.resources.Application</td>
     *       <td>javax.swing.application.resources.Application.properties</td>
     *     </tr>
     * </table>
     * 
     * <p>
     * None of the ResourceBundles are required to exist.  If more than one 
     * ResourceBundle contains a resource with the same name then
     * the one earlier in the list has precedence
     * <p>
     * ResourceMaps are constructed lazily and cached.  One ResourceMap
     * is constructed for each sequence of classes in the same package.
     * 
     * @param startClass the first class whose ResourceBundles will be included
     * @param stopClass the last class whose ResourceBundles will be included
     * @return a {@code ResourceMap} chain that contains resources loaded from 
     *   {@code ResourceBundles}  found in the resources subpackage for 
     *   each class.
     * @see #getClassBundleNames
     * @see #getApplicationBundleNames
     * @see ResourceMap#getParent
     * @see ResourceMap#getBundleNames
     */
    public ResourceMap getResourceMap(Class startClass, Class stopClass) {
	if (startClass == null) {
	    throw new IllegalArgumentException("null startClass");
	}
	if (stopClass == null) {
	    throw new IllegalArgumentException("null stopClass");
	}
        if (!stopClass.isAssignableFrom(startClass)) {
	    throw new IllegalArgumentException("startClass is not a subclass, or the same as, stopClass");
        }
        return getClassResourceMap(startClass, stopClass);
    }

    /**
     * Return the ResourcedMap chain for the specified class. This is
     * just a convenince method, it's the same as:
     * <code>getResourceMap(cls, cls)</code>.
     *
     * @param cls the class that defines the location of ResourceBundles
     * @return a {@code ResourceMap} that contains resources loaded from 
     *   {@code ResourceBundles}  found in the resources subpackage of the 
     *   specified class's package.
     * @see #getResourceMap(Class, Class)
     */
    public final ResourceMap getResourceMap(Class cls) {
	if (cls == null) {
	    throw new IllegalArgumentException("null class");
	}
        return getResourceMap(cls, cls);
    }

    /**
     * Returns the chain of ResourceMaps that's shared by the entire application,
     * beginning with the resources defined for the application's class, i.e.
     * the value of the ApplicationContext 
     * {@link ApplicationContext#getApplicationClass applicationClass} property.
     * If the {@code applicationClass} property has not been set, e.g. because
     * the application has not been {@link Application#launch launched} yet,
     * then a ResourceMap for just {@code Application.class} is returned.
     * 
     * @return the Application's ResourceMap
     * @see ApplicationContext#getResourceMap()
     * @see ApplicationContext#getApplicationClass
     */
    public ResourceMap getResourceMap() {
	return getApplicationResourceMap();
    }

    /**
     * The names of the ResourceBundles to be shared by the entire
     * application.  The list is in priority order: resources defined
     * by the first ResourceBundle shadow resources with the the same
     * name that come later.
     * <p>
     * The default value for this property is a list of {@link
     * #getClassBundleNames per-class} ResourceBundle names, beginning
     * with the {@code Application's} class and of each of its
     * superclasses, up to {@code Application.class}.
     * For example, if the Application's class was 
     * {@code com.foo.bar.MyApp}, and MyApp was a subclass
     * of {@code SingleFrameApplication.class}, then the 
     * ResourceBundle names would be:
     * <code><ol>
     * <li>com.foo.bar.resources.MyApp</li>
     * <li>javax.swing.application.resources.SingleFrameApplication</li>
     * <li>javax.swing.application.resources.Application</li>
     * </code></ol>
     * <p> 
     * The default value of this property is computed lazily and
     * cached.  If it's reset, then all ResourceMaps cached by
     * {@code getResourceMap} will be updated.
     * 
     * @see #setApplicationBundleNames
     * @see #getResourceMap
     * @see #getClassBundleNames
     * @see ApplicationContext#getApplication
     */
    public List<String> getApplicationBundleNames() {
	/* Lazily compute an initial value for this property, unless the
	 * application's class hasn't been specified yet.  In that case
	 * we just return a placeholder based on Application.class.
	 */
	if (applicationBundleNames == null) {
	    Class appClass = getContext().getApplicationClass();
	    if (appClass == null) {
		return allBundleNames(Application.class, Application.class); // placeholder
	    }
	    else {
		applicationBundleNames = allBundleNames(appClass, Application.class);
	    }
	}
	return applicationBundleNames;
    }

    /**
     * Specify the names of the ResourceBundles to be shared by the entire
     * application.  More information about the property is provided
     * by the {@link #getApplicationBundleNames} method.  
     * 
     * @see #setApplicationBundleNames
     */
    public void setApplicationBundleNames(List<String> bundleNames) {
	if (bundleNames != null) {
	    for(String bundleName : bundleNames) {
		if ((bundleName == null) || (bundleNames.size() == 0)) {
		    throw new IllegalArgumentException("invalid bundle name \"" + bundleName + "\"");
		}
	    }
	}
	Object oldValue = applicationBundleNames;
	if (bundleNames != null) {
	    applicationBundleNames = Collections.unmodifiableList(new ArrayList(bundleNames));
	}
	else {
	    applicationBundleNames = null;
	}
	resourceMaps.clear();
	firePropertyChange("applicationBundleNames", oldValue, applicationBundleNames);
    }

    /* Convert a class name to an eponymous resource bundle in the 
     * resources subpackage.  For example, given a class named
     * com.foo.bar.MyClass, the ResourceBundle name would be
     * "com.foo.bar.resources.MyClass"  If MyClass is an inner class,
     * only its "simple name" is used.  For example, given an
     * inner class named com.foo.bar.OuterClass$InnerClass, the
     * ResourceBundle name would be "com.foo.bar.resources.InnerClass".
     * Although this could result in a collision, creating more
     * complex rules for inner classes would be a burden for
     * developers.
     */
    private String classBundleBaseName(Class cls) {
	String className = cls.getName();
	StringBuffer sb = new StringBuffer();
	int i = className.lastIndexOf('.');
	if (i > 0) {
	    sb.append(className.substring(0, i));
	    sb.append(".resources.");
	    sb.append(cls.getSimpleName());
	}
	else {
	    sb.append("resources.");
	    sb.append(cls.getSimpleName());
	}
	return sb.toString();
    }

    /**
     * Map from a class to a list of the names of the 
     * {@code ResourceBundles} specific to the class.
     * The list is in priority order: resources defined 
     * by the first ResourceBundle shadow resources with the
     * the same name that come later.
     * <p>
     * By default this method returns one ResourceBundle 
     * whose name is the same as the class's name, but in the
     * {@code "resources"} subpackage.
     * <p>
     * For example, given a class named
     * {@code com.foo.bar.MyClass}, the ResourceBundle name would
     * be {@code "com.foo.bar.resources.MyClass"}. If MyClass is
     * an inner class, only its "simple name" is used.  For example,
     * given an inner class named {@code com.foo.bar.OuterClass$InnerClass},
     * the ResourceBundle name would be
     * {@code "com.foo.bar.resources.InnerClass"}.
     * <p>
     * This method is used by the {@code getResourceMap} methods
     * to compute the list of ResourceBundle names
     * for a new {@code ResourceMap}.  ResourceManager subclasses
     * can override this method to add additional class-specific
     * ResourceBundle names to the list.
     * 
     * @param cls the named ResourceBundles are specific to {@code cls}.
     * @return the names of the ResourceBundles to be loaded for {@code cls}
     * @see #getResourceMap
     * @see #getApplicationBundleNames
     */
    protected List<String> getClassBundleNames(Class cls) {
	String bundleName = classBundleBaseName(cls);
	return Collections.singletonList(bundleName);
    }

    /**
     * Called by {@link #getResourceMap} to construct {@code ResourceMaps}.
     * By default this method is effectively just:
     * <pre>
     * return new ResourceMap(parent, classLoader, bundleNames);
     * </pre>
     * Custom ResourceManagers might override this method to construct their
     * own ResourceMap subclasses.
     */
    protected ResourceMap createResourceMap(ClassLoader classLoader, ResourceMap parent, List<String> bundleNames) {
	return new ResourceMap(parent, classLoader, bundleNames);
    }

    /**
     * The value of the special Application ResourceMap resource
     * named "platform".  By default the value of this resource
     * is "osx" if the underlying operating environment is Apple
     * OSX or "default".
     *
     * @return the value of the platform resource
     * @see #setPlatform
     */
    public String getPlatform() {
        return getResourceMap().getString("platform");
    }

    /**
     * Defines the value of the special Application ResourceMap resource
     * named "platform".  This resource can be used to define platform
     * specific resources.  For example:
     * <pre>
     * myLabel.text.osx = A value that's appropriate for OSX
     * myLabel.text.default = A value for other platforms
     * myLabel.text = myLabel.text.${platform}
     * </pre>
     * <p> 
     * By default the value of this resource is "osx" if the
     * underlying operating environment is Apple OSX or "default".
     * To distinguish other platforms one can reset this property
     * based on the value of the {@code "os.name"} system property.
     * <p>
     * This method should be called as early as possible, typically
     * in the Application {@link Application#initialize initialize}
     * method.  
     * 
     * @see #getPlatform
     * @see System#getProperty
     */
    public void setPlatform(String platform) {
        if (platform == null) {
            throw new IllegalArgumentException("null platform");
        }
        getResourceMap().putResource("platform", platform);
    }
}
