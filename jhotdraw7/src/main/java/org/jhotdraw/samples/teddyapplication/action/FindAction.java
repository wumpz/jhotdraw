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

import org.jhotdraw.application.*;
import org.jhotdraw.application.action.*;
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
public class FindAction extends AbstractDocumentViewAction {
    public final static String ID = org.jhotdraw.application.action.FindAction.ID;
    private FindDialog findDialog;
    
    /**
     * Creates a new instance.
     */
    public FindAction() {
        initActionProperties(ID);
    }
    
    public void actionPerformed(ActionEvent e) {
        if (findDialog == null) {
            findDialog = new FindDialog();
            
            findDialog.addWindowListener(new WindowAdapter() {
                @Override public void windowClosing(WindowEvent evt) {
                    if (findDialog != null) {
                        getApplication().removePalette(findDialog);
                        findDialog.setVisible(false);
                    }
                }
            });
        }
        findDialog.setVisible(true);
        getApplication().addPalette(findDialog);
    }
}
