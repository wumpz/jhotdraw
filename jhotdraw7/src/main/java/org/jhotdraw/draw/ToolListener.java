/*
 * @(#)ToolListener.java  3.0  2006-02-13
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


package org.jhotdraw.draw;

import java.awt.*;
import java.util.*;
/**
 * Change event passed to ToolListener's.
 *
 * @author Werner Randelshofer
 * @version 3.0 2003-02-13 Revised to support multiple views.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface ToolListener extends EventListener {
    /**
     * Informs the listener that a tool has starteds interacting with a 
     * specific drawing view.
     */
    void toolStarted(ToolEvent event);
    /**
     * Informs the listener that a tool has done its interaction.
     * This method can be used to switch back to the default tool.
     */
    void toolDone(ToolEvent event);
    /**
     * Sent when an area of the drawing view needs to be repainted.
     */
    public void areaInvalidated(ToolEvent e);
    
}
