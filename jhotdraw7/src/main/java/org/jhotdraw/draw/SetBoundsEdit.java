/*
 * @(#)SetBoundsEdit.java  2.0  2006-01-14
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
�
 */


package org.jhotdraw.draw;

import javax.swing.undo.*;
import java.awt.*;
import java.awt.geom.*;
/**
 * SetBoundsEdit.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class SetBoundsEdit extends AbstractUndoableEdit {
    private AbstractFigure owner;
    private Point2D.Double oldAnchor, oldLead;
    private Point2D.Double newAnchor, newLead;
    
    /** Creates a new instance. */
    public SetBoundsEdit(AbstractFigure owner, Point2D.Double oldAnchor, Point2D.Double oldLead, Point2D.Double newAnchor, Point2D.Double newLead) {
        this.owner = owner;
        this.oldAnchor = oldAnchor;
        this.oldLead = oldLead;
        this.newAnchor = newAnchor;
        this.newLead = newLead;
    }
    public String getPresentationName() {
        return "Abmessungen \u00e4ndern";
    }
    
    public boolean addEdit(UndoableEdit anEdit) {
        if (anEdit instanceof SetBoundsEdit) {
            SetBoundsEdit that = (SetBoundsEdit) anEdit;
            if (that.owner == this.owner) {
                this.newAnchor = that.newAnchor;
                this.newLead = that.newLead;
                that.die();
                return true;
            }
        }
        return false;
    }
    public boolean replaceEdit(UndoableEdit anEdit) {
        if (anEdit instanceof SetBoundsEdit) {
            SetBoundsEdit that = (SetBoundsEdit) anEdit;
            if (that.owner == this.owner) {
                that.oldAnchor = this.oldAnchor;
                that.oldLead = this.oldLead;
                this.die();
                return true;
            }
        }
        return false;
    }
    
    public void redo() throws CannotRedoException {
        super.redo();
        owner.willChange();
        owner.setBounds(newAnchor, newLead);
        owner.changed();
    }
    public void undo() throws CannotUndoException {
        super.undo();
        owner.willChange();
        owner.setBounds(oldAnchor, oldLead);
        owner.changed();
    }
}

