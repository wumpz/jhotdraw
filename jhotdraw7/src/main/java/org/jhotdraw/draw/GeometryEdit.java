/*
 * @(#)GeometryEdit.java  1.0  January 22, 2006
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
 * GeometryEdit.
 *
 * @author Werner Randelshofer
 * @version 1.0 January 22, 2006 Created.
 */
public class GeometryEdit extends AbstractUndoableEdit {
    private Figure owner;
    private Object oldGeometry;
    private Object newGeometry;
    
    /** Creates a new instance. */
    public GeometryEdit(Figure owner, Object oldGeometry, Object newGeometry) {
        this.owner = owner;
        this.oldGeometry = oldGeometry;
        this.newGeometry = newGeometry;
    }

    public String getPresentationName() {
        return "Geometry changed";
    }
    
    public void undo() throws CannotUndoException {
        super.undo();
        owner.willChange();
        owner.restoreTo(oldGeometry);
        owner.changed();
    }

    public void redo() throws CannotRedoException {
        super.redo();
        owner.willChange();
        owner.restoreTo(newGeometry);
        owner.changed();
    }
    
}
