/*
 * @(#)AttributeChangeEdit.java  2.0  2006-06-07
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

import javax.swing.undo.*;
/**
 * AttributeChangeEdit.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2006-06-07 Reworked.
 * <br>1.1 2006-03-15 Changed constructor.
 * <br>1.0 27. November 2003  Created.
 */
public class AttributeChangeEdit<T> extends AbstractUndoableEdit {
    private Figure owner;
    private AttributeKey<T> name;
    private T oldValue;
    private T newValue;
    
    /** Creates a new instance. */
    public AttributeChangeEdit(Figure owner, AttributeKey<T> name, T oldValue, T newValue) {
        this.owner = owner;
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }    
    @Override
    public String getPresentationName() {
        // FIXME - Localize me
        return "Eigenschaft \u00e4ndern";
    }
    
    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        owner.willChange();
        owner.setAttribute(name, newValue);
        owner.changed();
    }
    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        owner.willChange();
        owner.setAttribute(name, oldValue);
        owner.changed();
    }
}
