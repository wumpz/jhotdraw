/*
 * @(#)UngroupAction.java  2.0  2007-12-21
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.draw.action;

import org.jhotdraw.draw.*;
import org.jhotdraw.undo.*;
import java.util.*;
import javax.swing.*;
import javax.swing.undo.*;

/**
 * UngroupAction.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2007-12-21 Extends GroupAction. 
 * <br>1.1.1 2006-12-29 Add ungrouped figures at same index to Drawing where
 * the Group was. 
 * <br>1.1 2006-07-12 Changed to support any CompositeFigure.
 * <br>1.0 24. November 2003  Created.
 */
public class UngroupAction extends GroupAction {
    public final static String ID = "selectionUngroup";
    
    /** Creates a new instance. */
    private CompositeFigure prototype;
    
    /** Creates a new instance. */
    public UngroupAction(DrawingEditor editor) {
        super(editor, new GroupFigure(), false);
        labels.configureAction(this, ID);
    }
    public UngroupAction(DrawingEditor editor, CompositeFigure prototype) {
        super(editor, prototype, false);
        labels.configureAction(this, ID);
    }
}
