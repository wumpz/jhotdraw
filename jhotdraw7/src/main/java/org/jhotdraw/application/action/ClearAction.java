/*
 * @(#)ClearAction.java  1.0  2005-10-16
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

import java.io.IOException;
import org.jhotdraw.gui.Worker;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.application.DocumentOrientedApplication;
import org.jhotdraw.application.DocumentView;

/**
 * Clears a View.
 *
 * @author Werner Randelshofer
 * @version 1.0  2005-10-16 Created.
 */
public class ClearAction extends AbstractSaveBeforeAction {
    public final static String ID = "File.clear";
    
    /** Creates a new instance. */
    public ClearAction() {
        initActionProperties(ID);
    }
    
    @Override public void doIt(final DocumentView documentView) {
        documentView.setFile(null);
        documentView.setEnabled(false);
        documentView.execute(new Worker() {
            public Object construct() {
                try {
                    documentView.clear();
                    return null;
                } catch (IOException ex) {
                    return ex;
                }
            }
            public void finished(Object result) {
                documentView.setModified(false);
                documentView.setEnabled(true);
            }
        });
    }
}
