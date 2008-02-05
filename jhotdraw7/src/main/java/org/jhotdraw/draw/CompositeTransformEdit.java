/*
 * @(#)CompositeTransformEdit.java  1.0  2006-01-21
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
import java.awt.geom.*;

/**
 * CompositeTransformEdit.
 * 
 * @author Werner Randelshofer
 * @version 1.0 2006-01-21 Created.
 */
public class CompositeTransformEdit extends AbstractUndoableEdit {
    private AbstractFigure owner;
    private AffineTransform tx;
    /**
     * True if this edit has never received <code>end</code>.
     */
    boolean inProgress;
    
    /** Creates a new instance. */
    public CompositeTransformEdit(AbstractFigure owner, AffineTransform tx) {
        this.owner = owner; 
        this.tx = (AffineTransform) tx.clone();;
        inProgress = true;
    }
    public String getPresentationName() {
        return "Figur transformieren";
    }
    
    public boolean addEdit(UndoableEdit anEdit) {
        if (anEdit == this) {
            end();
            return true;
        } else {
            if (! inProgress) {
                return false;
            } else {
                anEdit.die();
                return true;
            }
        }
    }
    
    public boolean replaceEdit(UndoableEdit anEdit) {
        /*
        if (anEdit instanceof CompositeTransformEdit) {
            CompositeTransformEdit that = (CompositeTransformEdit) anEdit;
            if (that.owner == this.owner) {
                this.tx.concatenate(that.tx);
                that.die();
                return true;
            }
        }*/
        return false;
    }
    public void redo() throws CannotRedoException {
        super.redo();
        owner.willChange();
        owner.transform(tx);
        owner.changed();
    }
    public void undo() throws CannotUndoException {
        super.undo();
        owner.willChange();
        try {
            owner.transform(tx.createInverse());
        } catch (NoninvertibleTransformException ex) {
            ex.printStackTrace();
        }
        owner.changed();
    }
    /**
     * Returns true if this edit is in progress--that is, it has not
     * received end. This generally means that edits are still being
     * added to it.
     *
     * @see	#end
     */
    public boolean isInProgress() {
	return inProgress;
    }
    /**
     * Sets <code>inProgress</code> to false.
     *
     * @see #canUndo
     * @see #canRedo
     */
    public void end() {
        inProgress = false;
    }
    
    /**
     * Returns false if <code>isInProgress</code> or if super
     * returns false.
     *
     * @see	#isInProgress
     */
    public boolean canUndo() {
        return !isInProgress() && super.canUndo();
    }
    
    /**
     * Returns false if <code>isInProgress</code> or if super
     * returns false.
     *
     * @see	#isInProgress
     */
    public boolean canRedo() {
        return !isInProgress() && super.canRedo();
    }
}
