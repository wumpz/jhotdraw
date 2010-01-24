/*
 * @(#)UngroupAction.java
 *
 * Copyright (c) 1996-2010 by the original authors of JHotDraw
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

/**
 * UngroupAction.
 *
 * @author  Werner Randelshofer
 * @version $Id$
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
