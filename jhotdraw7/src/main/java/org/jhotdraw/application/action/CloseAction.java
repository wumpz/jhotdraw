/*
 * @(#)CloseAction.java  1.0  04 January 2005
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.application.action;

import org.jhotdraw.util.*;

import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.application.*;

/**
 * Closes a project.
 *
 * @author  Werner Randelshofer
 * @version 1.0  04 January 2005  Created.
 */
public class CloseAction extends AbstractSaveBeforeAction {
    public final static String ID = "close";
    
    /** Creates a new instance. */
    public CloseAction() {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.application.Labels");
        labels.configureAction(this, ID);
    }

    @Override protected void doIt(Project project) {
        project.getWindowManager().
                dispose(project);
    }
}
