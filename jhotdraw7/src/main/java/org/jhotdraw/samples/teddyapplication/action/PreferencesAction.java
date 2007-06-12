/*
 * @(#)PreferencesAction.java  2007-06-11
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

import application.*;
import org.jhotdraw.application.*;
import org.jhotdraw.application.action.*;
import org.jhotdraw.samples.teddyapplication.*;
import org.jhotdraw.util.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * PreferencesAction shows the find dialog.
 *
 * @author Werner Randelshofer
 * @version 1.0 2007-06-11 Created.
 */
public class PreferencesAction extends AbstractDocumentViewAction {
    public final static String ID = "Preferences";
    private PreferencesDialog preferencesDialog;
    
    /**
     * Creates a new instance.
     */
    public PreferencesAction() {
        initActionProperties(ID);
    }
    
    public ResourceMap getResourceMap() {
        return ApplicationContext.getInstance().getResourceMap(TeddyView.class);
    }
    
    public void actionPerformed(ActionEvent e) {
        if (preferencesDialog == null) {
            preferencesDialog = new PreferencesDialog();
            
            preferencesDialog.addWindowListener(new WindowAdapter() {
                @Override public void windowClosing(WindowEvent evt) {
                    if (preferencesDialog != null) {
                        getApplication().removePalette(preferencesDialog);
                        preferencesDialog.setVisible(false);
                    }
                }
            });
        }
        preferencesDialog.setVisible(true);
        getApplication().addPalette(preferencesDialog);
    }
}
