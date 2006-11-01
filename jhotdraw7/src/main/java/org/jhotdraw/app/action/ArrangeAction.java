/*
 * @(#)ArrangeAction.java  1.0  7. Februar 2006
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

package org.jhotdraw.app.action;

import org.jhotdraw.util.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.*;
/**
 * ArrangeAction.
 * <p>
 * FIXME - Register as PropertyChangeListener on Arrangeable.
 *
 * @author Werner Randelshofer
 * @version 1.0 7. Februar 2006 Created.
 */
public class ArrangeAction extends AbstractAction {
    public final static String VERTICAL_ID = "arrangeVertical";
    public final static String HORIZONTAL_ID = "arrangeHorizontal";
    public final static String CASCADE_ID = "arrangeCascade";
    private Arrangeable arrangeable;
    private Arrangeable.Arrangement arrangement;
    
    /** Creates a new instance. */
    public ArrangeAction(Arrangeable arrangeable, Arrangeable.Arrangement arrangement) {
        this.arrangeable = arrangeable;
        this.arrangement = arrangement;
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        String labelID;
        switch (arrangement) {
            case VERTICAL : labelID = VERTICAL_ID; break;
            case HORIZONTAL : labelID = HORIZONTAL_ID; break;
            case CASCADE :
            default :
                labelID = CASCADE_ID; break;
        }
        labels.configureAction(this, labelID);
    }
    
    public void actionPerformed(ActionEvent e) {
            arrangeable.setArrangement(arrangement);
    }
}
