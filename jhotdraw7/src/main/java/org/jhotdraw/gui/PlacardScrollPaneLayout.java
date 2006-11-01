/*
 * @(#)PlacardScrollPaneLayout.java  1.0  June 11, 2006
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

package org.jhotdraw.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
/**
 * PlacardScrollPaneLayout.
 *
 * @author Werner Randelshofer.
 * @version 1.0 June 11, 2006 Created.
 */
public class PlacardScrollPaneLayout extends ScrollPaneLayout {
    /**
     * Creates a new instance.
     */
    public PlacardScrollPaneLayout() {
    }
    
    public void layoutContainer(Container parent) {
        super.layoutContainer(parent);

        if (lowerLeft != null && hsb != null) {
Dimension llp = lowerLeft.getPreferredSize();
//Insets insets = parent.getInsets();
            lowerLeft.setBounds(hsb.getX(),hsb.getY(),llp.width,hsb.getHeight());
hsb.setBounds(hsb.getX()+llp.width, hsb.getY(), hsb.getWidth() - llp.width, hsb.getHeight());
        
        }
    }
    
    
    
    /**
     * The UI resource version of <code>ScrollPaneLayout</code>.
     */
    public static class UIResource extends PlacardScrollPaneLayout implements javax.swing.plaf.UIResource {}
}



