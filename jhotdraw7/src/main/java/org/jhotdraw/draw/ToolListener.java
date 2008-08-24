/*
 * @(#)ToolListener.java  3.0  2006-02-13
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */


package org.jhotdraw.draw;

import java.util.*;
/**
 * Change event passed to ToolListener's.
 * <p>
 * Design pattern:<br>
 * Name: Observer.<br>
 * Role: Observer.<br>
 * Partners: {@link Tool} as Subject.
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
