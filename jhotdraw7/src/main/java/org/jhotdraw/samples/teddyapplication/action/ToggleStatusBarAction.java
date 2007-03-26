/*
 * @(#)ToggleStatusBarAction.java  1.0  October 1, 2005
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
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * ToggleStatusBarAction.
 *
 * @author  Werner Randelshofer
 * @version 1.0 October 1, 2005 Created.
 */
public class ToggleStatusBarAction extends AbstractProjectAction {
    public final static String ID = "showStatusBar";
    private ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.teddy.Labels");
    /**
     * Creates a new instance.
     */
    public ToggleStatusBarAction() {
        labels.configureAction(this, ID);
        setPropertyName("statusBarVisible");
    }
    
    public TeddyProject getCurrentProject() {
        return (TeddyProject) super.getCurrentProject();
    }
    
    protected void updateProperty() {
        putValue(
               Actions.SELECTED_KEY, 
               getCurrentProject() != null && getCurrentProject().isStatusBarVisible()
               );
    }
    
    public void actionPerformed(ActionEvent e) {
        getCurrentProject().setStatusBarVisible(! getCurrentProject().isStatusBarVisible());
    }
}

