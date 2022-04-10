/*
 * @(#)MinimizeWindowAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.action.window;

import java.awt.*;

/**
 * Minimizes the Frame of the current view.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class MinimizeWindowAction extends AbstractMinimizeMaximizeAction {

    public static final String ID = "window.minimize";

    /**
     * Creates a new instance.
     */
    public MinimizeWindowAction(Application app, View view) {
        super(app, view, ID);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        super.actionPerformed(evt,Frame.ICONIFIED);
    }
}
