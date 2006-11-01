/*
 * @(#)MaximizeAction.java  2.0  2006-05-05
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jhotdraw.app.Application;
/**
 * Maximizes the Frame of the current project.
 * 
 * @author Werner Randelshofer
 * @version 2.0 2005-05-05 Reworked.
 * <br>1.0  2005-06-10 Created.
 */
public class MaximizeAction extends AbstractProjectAction {
    public final static String ID = "maximize";
    
    /** Creates a new instance. */
    public MaximizeAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }
    
    private JFrame getFrame() {
        return (JFrame) SwingUtilities.getWindowAncestor(
                getCurrentProject().getComponent()
                );
    }
    
    public void actionPerformed(ActionEvent evt) {
        JFrame frame = getFrame();
        if (frame != null) {
            frame.setExtendedState(frame.getExtendedState() ^ Frame.MAXIMIZED_BOTH);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}
