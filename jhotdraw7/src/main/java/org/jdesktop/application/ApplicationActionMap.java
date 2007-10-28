
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.ActionMap;



/**
 * An {@link javax.swing.ActionMap ActionMap} class where each entry 
 * corresponds to an <tt>&#064;Action</tt> method from a single 
 * <tt>actionsClass</tt> (i.e. a class that contains one or more 
 * <tt>&#064;Actions</tt>).  Each entry's key is the <tt>&#064;Action's</tt>
 * name (the method name by default), and the value is an 
 * {@link ApplicationAction} that calls the <tt>&#064;Actions</tt> method.
 * For example, the code below prints <tt>"Hello World"</tt>:
 * <pre>
 * public class HelloWorldActions {
 *     public &#064;Action void Hello() { System.out.print("Hello "); }
 *     public &#064;Action void World() { System.out.println("World"); }
 * }
 * // ...
 * ApplicationActionMap appAM = new ApplicationActionMap(SimpleActions.class);
 * ActionEvent e = new ActionEvent("no src", ActionEvent.ACTION_PERFORMED, "no cmd");
 * appAM.get("Hello").actionPerformed(e);
 * appAM.get("World").actionPerformed(e);
 * </pre>
 * 
 * <p>
 * If a <tt>ResourceMap</tt> is provided then each
 * <tt>ApplicationAction's</tt> ({@link javax.swing.Action#putValue
 * putValue}, {@link javax.swing.Action#getValue getValue}) properties
 * are initialized from the ResourceMap.  
 * 
 * <p>
 * TBD: explain use of resourcemap including action types, actionsObject, 
 * actionsClass, ProxyActions, 
 * 
 * @see ApplicationAction
 * @see ResourceMap
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */

public class ApplicationActionMap extends ActionMap {
    private final ApplicationContext context;
    private final ResourceMap resourceMap;
    private final Class actionsClass;
    private final Object actionsObject;
    private final List<ApplicationAction> proxyActions;

    public ApplicationActionMap(ApplicationContext context, Class actionsClass, Object actionsObject, ResourceMap resourceMap) {
        if (context == null) {
            throw new IllegalArgumentException("null context");
        }
	if (actionsClass == null) {
	    throw new IllegalArgumentException("null actionsClass");
	}
	if (actionsObject == null) {
	    throw new IllegalArgumentException("null actionsObject");
	}
	if (!(actionsClass.isInstance(actionsObject))) {
	    throw new IllegalArgumentException("actionsObject not an instanceof actionsClass");
	}
        this.context = context;
	this.actionsClass = actionsClass;
	this.actionsObject = actionsObject;
	this.resourceMap = resourceMap;
	this.proxyActions = new ArrayList<ApplicationAction>();
	addAnnotationActions(resourceMap);
	maybeAddActionsPCL();
    }

    public final ApplicationContext getContext() {
        return context;
    }

    public final Class getActionsClass() { 
	return actionsClass; 
    }

    public final Object getActionsObject() {
	return actionsObject;
    }

    /**
     * All of the {@code @ProxyActions} recursively defined by this 
     * {@code ApplicationActionMap} and its parent ancestors.
     * <p>
     * Returns a read-only list of the {@code @ProxyActions} defined
     * by this {@code ApplicationActionMap's} {@code actionClass}
     * and, recursively, by this {@code ApplicationActionMap's} parent.
     * If there are no proxyActions, an empty list is returned.
     *
     * @return a list of all the proxyActions for this {@code ApplicationActionMap}
     */
    public List<ApplicationAction> getProxyActions() {
	// TBD: proxyActions that shadow should be merged
	ArrayList<ApplicationAction> allProxyActions = new ArrayList<ApplicationAction>(proxyActions);
	ActionMap parent = getParent();
	while(parent != null) {
	    if (parent instanceof ApplicationActionMap) {
		allProxyActions.addAll(((ApplicationActionMap)parent).proxyActions);
	    }
	    parent = parent.getParent();
	}
	return Collections.unmodifiableList(allProxyActions);
    }

    private String aString(String s, String emptyValue) {
	return (s.length() == 0) ? emptyValue : s;
    }

    private void putAction(String key, ApplicationAction action) {
	if (get(key) != null) {
	    // TBD log a warning - two actions with the same key
	}
	put(key, action);
    }


    /* Add Actions for each actionsClass method with an @Action
     * annotation and for the class's @ProxyActions annotation
     */
    private void addAnnotationActions(ResourceMap resourceMap) {
	Class<?> actionsClass = getActionsClass();
	// @Action 
	for (Method m : actionsClass.getDeclaredMethods()) {
	    Action action = m.getAnnotation(Action.class);
	    if (action != null) {
		String methodName = m.getName();
		String enabledProperty = aString(action.enabledProperty(), null);
		String selectedProperty = aString(action.selectedProperty(), null);
		String actionName = aString(action.name(), methodName);
		Task.BlockingScope block = action.block();
		ApplicationAction appAction = 
		    new ApplicationAction(this, resourceMap, actionName, m, enabledProperty, selectedProperty, block);
		putAction(actionName, appAction);
	    }
	}
	// @ProxyActions
	ProxyActions proxyActionsAnnotation = actionsClass.getAnnotation(ProxyActions.class);
	if (proxyActionsAnnotation != null) {
	    for(String actionName : proxyActionsAnnotation.value()) {
		ApplicationAction appAction = new ApplicationAction(this, resourceMap, actionName);
		appAction.setEnabled(false); // will track the enabled property of the Action it's bound to
		putAction(actionName, appAction);
		proxyActions.add(appAction);
	    }
	}
    }

    /* If any of the ApplicationActions need to track an 
     * enabled or selected property defined in actionsClass, then add our 
     * PropertyChangeListener.  If none of the @Actions in actionClass
     * provide an enabledProperty or selectedProperty argument, then
     * we don't need to do this.
     */
    private void maybeAddActionsPCL() {
	boolean needsPCL = false;
	Object[] keys = keys();
	if (keys != null) {
	    for (Object key : keys) {
		javax.swing.Action value = get(key);
		if (value instanceof ApplicationAction) {
		    ApplicationAction actionAdapter = (ApplicationAction)value;
		    if ((actionAdapter.getEnabledProperty() != null) || 
			(actionAdapter.getSelectedProperty() != null)) {
			needsPCL = true;
			break;
		    }
		}
	    }
	    if (needsPCL) {
		try {
		    Class actionsClass = getActionsClass();
		    Method m = actionsClass.getMethod("addPropertyChangeListener", PropertyChangeListener.class);
		    m.invoke(getActionsObject(), new ActionsPCL());
		}
		catch (Exception e) {
		    String s = "addPropertyChangeListener undefined " + actionsClass;
		    throw new Error(s, e);
		}
	    }
	}
    }

    /* When the value of an actionsClass @Action enabledProperty or 
     * selectedProperty changes, forward the PropertyChangeEvent to 
     * the ApplicationAction object itself.
     */
    private class ActionsPCL implements PropertyChangeListener {
	public void propertyChange(PropertyChangeEvent event) {
	    String propertyName = event.getPropertyName();
	    Object[] keys = keys();
	    if (keys != null) {
		for (Object key : keys) {
		    javax.swing.Action value = get(key);
		    if (value instanceof ApplicationAction) {
			ApplicationAction appAction = (ApplicationAction)value;
			if (propertyName.equals(appAction.getEnabledProperty())) {
			    appAction.forwardPropertyChangeEvent(event, "enabled");
			}
                        else if (propertyName.equals(appAction.getSelectedProperty())) {
			    appAction.forwardPropertyChangeEvent(event, "selected");
                        }
		    }
		}
	    }
	}
    }
}
