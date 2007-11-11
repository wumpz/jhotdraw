/*
 * @(#)TogglePropertiesPanelAction.java  1.0  2007-07-28
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.samples.odg.action;

import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.app.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.samples.odg.*;
import org.jhotdraw.util.*;

/**
 * TogglePropertiesPanelAction.
 * 
 * @author Werner Randelshofer
 * @version 1.0 2007-07-28 Created.
 */
public class TogglePropertiesPanelAction extends AbstractProjectAction {
    
    /** Creates a new instance. */
    public TogglePropertiesPanelAction(Application app) {
        super(app);
        setPropertyName("propertiesPanelVisible");
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.odg.Labels");
        putValue(AbstractAction.NAME, labels.getString("propertiesPanel"));
    }
    
    /**
     * This method is invoked, when the property changed and when
     * the project changed.
     */
    protected void updateProperty() {
        putValue(Actions.SELECTED_KEY,
                getCurrentProject() != null &&
                ! getCurrentProject().isPropertiesPanelVisible()
                );
    }
    
    
    public ODGProject getCurrentProject() {
        return (ODGProject) super.getCurrentProject();
    }
    
    public void actionPerformed(ActionEvent e) {
        getCurrentProject().setPropertiesPanelVisible(
                ! getCurrentProject().isPropertiesPanelVisible()
                );
    }
    
}
