/*
 * @(#)ProjectPropertyAction.java  1.0  June 18, 2006
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.app.action;

import java.awt.event.*;
import java.beans.*;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.Project;

/**
 * ProjectPropertyAction.
 * 
 * @author Werner Randelshofer.
 * @version 1.0 June 18, 2006 Created.
 */
public class ProjectPropertyAction extends AbstractProjectAction {
    private String propertyName;
    private Class[] parameterClass;
    private Object propertyValue;
    private String setterName;
    private String getterName;
    
    private PropertyChangeListener projectListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName() == propertyName) { // Strings get interned
                updateSelectedState();
            }
        }
    };
    
    /** Creates a new instance. */
    public ProjectPropertyAction(Application app, String propertyName, Object propertyValue) {
        this(app, propertyName, propertyValue.getClass(), propertyValue);
    }
    public ProjectPropertyAction(Application app, String propertyName, Class propertyClass, Object propertyValue) {
        super(app);
        this.propertyName = propertyName;
        this.parameterClass = new Class[] { propertyClass };
        this.propertyValue = propertyValue;
        setterName = "set"+Character.toUpperCase(propertyName.charAt(0)) +
                propertyName.substring(1);
        getterName = ((propertyClass == Boolean.TYPE || propertyClass == Boolean.class) ? "is" : "get")+
                Character.toUpperCase(propertyName.charAt(0)) +
                propertyName.substring(1);
        updateSelectedState();
    }
    
    public void actionPerformed(ActionEvent evt) {
        Project p = getActiveProject();
        try {
            p.getClass().getMethod(setterName, parameterClass).invoke(p, new Object[] {propertyValue});
        } catch (Throwable e) {
                InternalError error = new InternalError("Method invocation failed. setter:"+setterName+" object:"+p);
            error.initCause(e);
            throw error;
        }
    }
    
   @Override protected void installProjectListeners(Project p) {
        super.installProjectListeners(p);
        p.addPropertyChangeListener(projectListener);
        updateSelectedState();
    }
    /**
     * Installs listeners on the project object.
     */
   @Override protected void uninstallProjectListeners(Project p) {
        super.uninstallProjectListeners(p);
        p.removePropertyChangeListener(projectListener);
    }
    
    private void updateSelectedState() {
        boolean isSelected = false;
        Project p = getActiveProject();
        if (p != null) {
            try {
                Object value = p.getClass().getMethod(getterName, (Class[]) null).invoke(p);
                isSelected = value == propertyValue ||
                        value != null && propertyValue != null &&
                        value.equals(propertyValue);
            } catch (Throwable e) {
                InternalError error = new InternalError("Method invocation failed. getter:"+getterName+" object:"+p);
                error.initCause(e);
                throw error;
            }
        }
        putValue(Actions.SELECTED_KEY, isSelected);
    }
}
