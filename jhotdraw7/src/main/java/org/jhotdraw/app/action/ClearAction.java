/*
 * @(#)ClearAction.java  1.1  2007-11-25
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
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

import org.jhotdraw.gui.Worker;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;

/**
 * Clears a view.
 *
 * @author Werner Randelshofer
 * @version 1.1 2007-11-25 Call method clear on a worker thread.
 * <br>1.0  2005-10-16 Created.
 */
public class ClearAction extends AbstractSaveBeforeAction {
    public final static String ID = "file.clear";
    
    /** Creates a new instance. */
    public ClearAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, "file.new");
    }
    
    @Override public void doIt(final View view) {
        view.setEnabled(false);
        view.execute(new Worker() {
            public Object construct() {
                view.clear();
                return null;
            }
            public void finished(Object value) {
                view.setEnabled(true);
            }
        });
    }
}
