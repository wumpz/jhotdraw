/*
 * @(#)AbstractPreferencesAction.java
 * 
 * Copyright (c) 2009 by the original authors of JHotDraw
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

import org.jhotdraw.app.Application;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * Displays a preferences dialog for the application.
 *
 * @author Werner Randelshofer
 * @version 1.0 2009-09-06 Created.
 */
public abstract class AbstractPreferencesAction extends AbstractApplicationAction {

    public final static String ID = "application.preferences";

    /** Creates a new instance. */
    public AbstractPreferencesAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }
}
