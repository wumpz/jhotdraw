/*
 * @(#)MinimizeWindowAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.app.action.window;

import javax.annotation.Nullable;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;

/**
 * Minimizes the Frame of the current view.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class MinimizeWindowAction extends AbstractViewAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "window.minimize";

    /** Creates a new instance. */
    public MinimizeWindowAction(Application app, @Nullable View view) {
        super(app, view);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }

    private JFrame getFrame() {
        return (JFrame) SwingUtilities.getWindowAncestor(
                getActiveView().getComponent()
                );
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        JFrame frame = getFrame();
        if (frame != null) {
            frame.setExtendedState(frame.getExtendedState() ^ Frame.ICONIFIED);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}
