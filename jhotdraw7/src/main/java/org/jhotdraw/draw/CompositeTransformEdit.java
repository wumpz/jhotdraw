/*
 * @(#)CompositeTransformEdit.java  1.0  2006-01-21
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
        owner.basicTransform(tx);
        owner.changed();
    }
    public void undo() throws CannotUndoException {
        super.undo();
        owner.willChange();
        try {
            owner.basicTransform(tx.createInverse());
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
