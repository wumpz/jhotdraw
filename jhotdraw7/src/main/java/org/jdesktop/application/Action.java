
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.application;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Marks a method that will be used to define a Swing 
 * <code>Action</code> object's <code>actionPerformed</code>
 * method.  It also identifies the resources that 
 * will be used to initialize the Action's properties.
 * Additional <code>&#064;Action</code> parameters can be used
 * to specify the name of the bound properties (from the 
 * same class) that indicate if the Action is to be 
 * enabled/selected, and if the GUI should be blocked
 * while the Action's background {@link Task} is running.
 * 
 * <p>
 * The {@link ApplicationActionMap} class creates an
 * <code>ActionMap</code> that contains one {@link ApplicationAction}
 * for each &#064;Action found in a target or "actions" class.
 * Typically applications will use {@link
 * ApplicationContext#getActionMap(Class, Object) getActionMap} to
 * lazily construct and cache ApplicationActionMaps, rather than
 * constructing them directly.  By default the ApplicationActionMap's
 * {@link ApplicationActionMap#get key} for an &#064;Action is the
 * name of the method. The <code>name</code> parameter can be used to
 * specify a different key.
 * 
 * <p>
 * The <code>ApplicationAction's</code> properties are initialized with
 * resources loaded from a ResourceBundle with the same name as the
 * actions class.  The list of properties initialized this way 
 * is documented by the {@link ApplicationAction ApplicationAction's}
 * constructor.
 * 
 * <p>
 * The method marked with &#064;Action, can have no parameters, 
 * or a single ActionEvent parameter.  The method's return type
 * can be <code>void</code> or {@link Task}.  If the return type
 * is Task, the Task will be executed by the ApplicationAction's 
 * <code>actionPerformed</code> method.
 * 
 * <p>
 * [TBD the block parameter, and the Parameter annotation]
 * 
 * @see ApplicationAction
 * @see ApplicationActionMap
 * @see ApplicationContext
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Action {
    String name() default "";
    String enabledProperty() default "";
    String selectedProperty() default  "";
    Task.BlockingScope block() default Task.BlockingScope.NONE;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @interface Parameter {
	String value() default "";
    }
}

