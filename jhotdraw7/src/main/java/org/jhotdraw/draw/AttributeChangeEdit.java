/*
 * @(#)AttributeChangeEdit.java  2.0  2006-06-07
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

import javax.swing.undo.*;
/**
 * AttributeChangeEdit.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2006-06-07 Reworked.
 * <br>1.1 2006-03-15 Changed constructor.
 * <br>1.0 27. November 2003  Created.
 */
public class AttributeChangeEdit extends AbstractUndoableEdit {
    private Figure owner;
    private AttributeKey name;
    private Object oldValue;
    private Object newValue;
    
    /** Creates a new instance. */
    public AttributeChangeEdit(Figure owner, AttributeKey name, Object oldValue, Object newValue) {
        this.owner = owner;
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }    
    public String getPresentationName() {
        // FIXME - Localize me
        return "Eigenschaft \u00e4ndern";
    }
    
    public void redo() throws CannotRedoException {
        super.redo();
        owner.willChange();
        owner.basicSetAttribute(name, newValue);
        owner.changed();
    }
    public void undo() throws CannotUndoException {
        super.undo();
        owner.willChange();
        owner.basicSetAttribute(name, oldValue);
        owner.changed();
    }
}
