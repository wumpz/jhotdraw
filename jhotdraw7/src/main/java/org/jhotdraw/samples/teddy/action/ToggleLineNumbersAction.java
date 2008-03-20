/*
 * @(#)ToggleLineNumbersAction.java  1.0  October 1, 2005
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

package org.jhotdraw.samples.teddy.action;

import org.jhotdraw.app.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.samples.teddy.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * ToggleLineNumbersAction.
 *
 * @author  Werner Randelshofer
 * @version 1.0 October 1, 2005 Created.
 */
public class ToggleLineNumbersAction extends AbstractViewAction {
    public final static String ID = "showLineNumbers";
    private ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.teddy.Labels");
    
    /**
     * Creates a new instance.
     */
    public ToggleLineNumbersAction(Application app) {
        super(app);
        labels.configureAction(this, ID);
        setPropertyName("lineNumbersVisible");
    }
    
    public TeddyView getActiveView() {
        return (TeddyView) super.getActiveView();
    }
    
    public void actionPerformed(ActionEvent e) {
        getActiveView().setLineNumbersVisible(! getActiveView().isLineNumbersVisible());
    }
    
    
    protected void updateView() {
        putValue(
                Actions.SELECTED_KEY,
                getActiveView() != null && getActiveView().isLineNumbersVisible()
                );
    }
}
