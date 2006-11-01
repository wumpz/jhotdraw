/*
 * @(#)NonUndoableEdit.java  1.0  5. April 2004
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

package org.jhotdraw.undo;

import javax.swing.undo.*;
/**
 * NonUndoableEdit.
 *
 * @author  Werner Randelshofer
 * @version 1.0 5. April 2004  Created.
 */
public class NonUndoableEdit extends AbstractUndoableEdit {
    
    /** Creates a new instance. */
    public NonUndoableEdit() {
    }
    
    public boolean canUndo() {
        return false;
    }
    public boolean canRedo() {
        return false;
    }
}
