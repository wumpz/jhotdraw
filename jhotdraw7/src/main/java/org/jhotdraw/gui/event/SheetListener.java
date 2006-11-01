/*
 * @(#)SheetListener.java  1.0  26. September 2005
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

package org.jhotdraw.gui.event;

import java.util.*;
/**
 * SheetListener.
 *
 * @author  Werner Randelshofer
 * @version 1.0 26. September 2005 Created.
 */
public interface SheetListener extends EventListener {
    /**
     * This method is invoked, when the user selected an option on the
     * JOptionPane or the JFileChooser pane on the JSheet.
     */
    public void optionSelected(SheetEvent evt);
}
