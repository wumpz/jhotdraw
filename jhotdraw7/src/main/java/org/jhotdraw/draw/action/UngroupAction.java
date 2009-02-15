/*
 * @(#)UngroupAction.java  2.0  2007-12-21
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
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
    public final static String ID = "edit.ungroupSelection";
    
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
