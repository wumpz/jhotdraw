/*
 * @(#)FindAction.java  1.0  October 8, 2005
 *
 * Copyright (c) 2005 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.samples.teddyapplication.action;

import org.jhotdraw.app.*;
import org.jhotdraw.application.*;
import org.jhotdraw.samples.teddyapplication.*;
import org.jhotdraw.util.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * FindAction shows the find dialog.
 *
 * @author Werner Randelshofer
 * @version 1.0 October 8, 2005 Created.
 */
public class FindAction extends AbstractAction {
    public final static String ID = org.jhotdraw.app.action.FindAction.ID;
    private FindDialog findDialog;
    private Application app;
    private ResourceBundleUtil labels =
            ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.teddy.Labels");
    
    /**
     * Creates a new instance.
     */
    public FindAction() {
        labels.configureAction(this, ID);
    }
    
    public void actionPerformed(ActionEvent e) {
        if (findDialog == null) {
            findDialog = new FindDialog();
            
            if (WindowManager.getInstance() instanceof DefaultOSXWindowManager) {
                findDialog.addWindowListener(new WindowAdapter() {
                    @Override public void windowClosing(WindowEvent evt) {
                        if (findDialog != null) {
                            ((DefaultOSXWindowManager) WindowManager.getInstance()).removePalette(findDialog);
                            findDialog.setVisible(false);
                        }
                    }
                });
            }
        }
        findDialog.setVisible(true);
        if (app instanceof DefaultOSXApplication) {
            ((DefaultOSXApplication) app).addPalette(findDialog);
        }
    }
}
