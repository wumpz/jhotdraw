/*
 * @(#)ApplicationModel.java
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

package org.jhotdraw.app;

import java.util.*;
import javax.swing.*;
/**
 * {@code ApplicationModel} provides meta-data for an {@link Application},
 * actions and factory methods for creating {@link View}s and toolbars.
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Framework</em><br>
 * The interfaces and classes listed below together with the {@code Action}
 * classes in the org.jhotddraw.app.action package define the contracts of a
 * framework for document oriented applications:<br>
 * Contract: {@link Application}, {@link ApplicationModel}, {@link View}.
 * <hr>
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public interface ApplicationModel {
    /**
     * Returns the name of the application.
     */
    public String getName();
    /**
     * Returns the version of the application.
     */
    public String getVersion();
    /**
     * Returns the copyright of the application.
     */
    public String getCopyright();
    
    /**
     * Creates a new view for the application.
     */
    public View createView();

    /** Inits the supplied view for the application. */
    public void initView(Application a, View v);

    /** Inits the application model.
     * <p>
     * Typically, the application model creates a number of
     * {@link org.jhotdraw.app.action.AbstractApplicationAction}
     * objects, which can later be retrieved using getAction, and
     * which are linked to menu items and toolbars created by the
     * application model.
     * <p>
     */
    public void initApplication(Application a);
    /**
     * Puts an action with the specified id.
     */
    public void putAction(String id, Action action);
    /**
     * Returns the action with the specified id.
     */
    public Action getAction(String id);
    /**
     * Creates tool bars.
     * <p>
     * Depending on the document interface of the application, this method
     * may be invoked only once for the application, or for each opened view.
     * <p>
     * @param a Application.
     * @param v The view for which the toolbars need to be created, or null
     * if the toolbars are shared by multiple views.
     */
    public List<JToolBar> createToolBars(Application a, View v);
    
    /**
     * Creates menus.
     * <p>
     * Depending on the document interface of the application, this method
     * may be invoked only once for the application, or for each opened view.
     * <p>
     * @param a Application.
     * @param v The view for which the toolbars need to be created, or null
     * if the menus are shared by multiple views.
     */
    public List<JMenu> createMenus(Application a, View v);
}
