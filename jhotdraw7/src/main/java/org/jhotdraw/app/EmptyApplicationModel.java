/*
 * @(#)EmptyApplicationModel.java
 *
 * Copyright (c) 2009-2010 by the original authors of JHotDraw
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
 * An {@link ApplicationModel} which neither creates {@code Action}s,
 * nor overrides the menu bars, nor creates tool bars.
 * <p>
 * The {@code createActionMap} method of this model returns an empty ActionMap.
 * <p>
 * The {@code createMenu...} methods of this model return null, resulting in
 * a set of default menu bars created by the {@link Application} which holds
 * this model.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class EmptyApplicationModel
        extends AbstractApplicationModel {

    /** Returns an empty ActionMap. */
    @Override
    public ActionMap createActionMap(Application a, View v) {
        return new ActionMap();
    }

    /** Returns an empty unmodifiable list. */
    @Override
    public List<JToolBar> createToolBars(Application app, View v) {
        return Collections.emptyList();
    }

    /** Returns an empty modifiable list. */
    @Override
    public List<JMenu> createMenus(Application a, View v) {
        LinkedList<JMenu> menus = new LinkedList<JMenu>();
        JMenu m;
        if ((m=createFileMenu(a,v))!=null) {
            menus.add(m);
        }
        if ((m=createEditMenu(a,v))!=null) {
            menus.add(m);
        }
        if ((m=createViewMenu(a,v))!=null) {
            menus.add(m);
        }
        if ((m=createWindowMenu(a,v))!=null) {
            menus.add(m);
        }
        if ((m=createHelpMenu(a,v))!=null) {
            menus.add(m);
        }
        return menus;
    }

    /** Returns null. */
    protected JMenu createFileMenu(Application app, View view) {
        return null;
    }
    /** Returns null. */
    protected JMenu createEditMenu(Application app, View view) {
        return null;
    }
    /** Returns null. */
    protected JMenu createViewMenu(Application app, View view) {
        return null;
    }
    /** Returns null. */
    protected JMenu createWindowMenu(Application app, View view) {
        return null;
    }
    /** Returns null. */
    protected JMenu createHelpMenu(Application app, View view) {
        return null;
    }
}
