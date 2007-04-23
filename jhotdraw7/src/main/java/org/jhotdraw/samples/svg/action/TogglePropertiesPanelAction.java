/*
 * @(#)TogglePropertiesPanelAction.java  1.0  22. April 2007
 *
 * Copyright (c) 2007 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.samples.svg.action;

import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.app.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.samples.svg.*;
import org.jhotdraw.util.*;

/**
 * TogglePropertiesPanelAction.
 * 
 * @author Werner Randelshofer
 * @version 1.0 22. April 2007 Created.
 */
public class TogglePropertiesPanelAction extends AbstractProjectAction {
    
    /** Creates a new instance. */
    public TogglePropertiesPanelAction(Application app) {
        super(app);
        setPropertyName("propertiesPanelVisible");
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.svg.Labels");
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
    
    
    public SVGProject getCurrentProject() {
        return (SVGProject) super.getCurrentProject();
    }
    
    public void actionPerformed(ActionEvent e) {
        getCurrentProject().setPropertiesPanelVisible(
                ! getCurrentProject().isPropertiesPanelVisible()
                );
    }
    
}
