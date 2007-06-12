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

import java.io.IOException;
import org.jhotdraw.gui.Worker;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.application.DocumentOrientedApplication;
import org.jhotdraw.application.DocumentView;

/**
 * Creates a new documentView.
 *
 * @author Werner Randelshofer
 * @version 1.2 2006-02-22 Support for multiple open id added.
 * <br>1.1.1 2005-07-14 Make documentView explicitly visible after creating it.
 * <br>1.1 2005-07-09 Place new documentView relative to current one.
 * <br>1.0  04 January 2005  Created.
 */
public class NewAction extends AbstractApplicationAction {
    public final static String ID = "File.new";
    
    /** Creates a new instance. */
    public NewAction() {
        initActionProperties(ID);
    }
    
    public void actionPerformed(ActionEvent evt) {
        DocumentOrientedApplication application = getApplication();
        final DocumentView newP = application.createView();
        application.add(newP);
        newP.setEnabled(false);
        newP.execute(new Worker() {
            public Object construct() {
                try {
                    newP.clear();
                    return null;
                } catch (IOException ex) {
                    return ex;
                }
            }
            public void finished(Object result) {
                newP.setEnabled(true);
            }
        });
        application.show(newP);
    }
}
