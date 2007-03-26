/*
 * @(#)NewAction.java  1.2  2006-02-22
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.application.*;

/**
 * Creates a new project.
 *
 * @author Werner Randelshofer
 * @version 1.2 2006-02-22 Support for multiple open id added.
 * <br>1.1.1 2005-07-14 Make project explicitly visible after creating it.
 * <br>1.1 2005-07-09 Place new project relative to current one.
 * <br>1.0  04 January 2005  Created.
 */
public class NewAction extends AbstractApplicationAction {
    public final static String ID = "new";
    
    /** Creates a new instance. */
    public NewAction() {
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.application.Labels");
        labels.configureAction(this, "new");
    }
    
    public void actionPerformed(ActionEvent evt) {
        DocumentOrientedApplication app = getApplication();
        Project newP = app.createProject();
        int multiOpenId = 1;
        for (Project existingP : WindowManager.getInstance().projects()) {
            if (existingP.getFile() == null) {
                multiOpenId = Math.max(multiOpenId, existingP.getMultipleOpenId() + 1);
            }
        }
        newP.setMultipleOpenId(multiOpenId);
        WindowManager.getInstance().add(newP);
        WindowManager.getInstance().show(newP);
    }
}
