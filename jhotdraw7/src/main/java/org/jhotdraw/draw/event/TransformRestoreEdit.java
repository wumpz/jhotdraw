/*
 * @(#)TransformRestoreEdit.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.draw.event;

import org.jhotdraw.draw.*;
import javax.swing.undo.*;
import org.jhotdraw.util.*;
/**
 * An {@code UndoableEdit} event which can undo a lossy transform of a single
 * {@link Figure} by restoring the figure using its transform restore data.
 * <p>
 * This object is useful for undoing lossy transformations, such as the
 * rotation, scaling or shearing of a figure.
 * <p>
 * The transform restore data may consume a lot of memory. Undos of lossless
 * transforms, such as translations of a figure, should use {@link TransformEdit}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TransformRestoreEdit extends AbstractUndoableEdit {
    private static final long serialVersionUID = 1L;
    private Figure owner;
    private Object oldTransformRestoreData;
    private Object newTransformRestoreData;
    
    /** Creates a new instance. */
    public TransformRestoreEdit(Figure owner, Object oldTransformRestoreData, Object newTransformRestoreData) {
        this.owner = owner;
        this.oldTransformRestoreData = oldTransformRestoreData;
        this.newTransformRestoreData = newTransformRestoreData;
    }

    @Override
    public String getPresentationName() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        return labels.getString("edit.transform.text");
    }
    
    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        owner.willChange();
        owner.restoreTransformTo(oldTransformRestoreData);
        owner.changed();
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        owner.willChange();
        owner.restoreTransformTo(newTransformRestoreData);
        owner.changed();
    }
    
}
