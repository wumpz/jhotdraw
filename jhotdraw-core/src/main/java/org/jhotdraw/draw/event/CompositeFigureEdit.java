/*
 * @(#)CompositeFigureEdit.java
 * 
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.draw.event;

import org.jhotdraw.draw.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.jhotdraw.undo.CompositeEdit;

/**
 * A {@link CompositeEdit} which invokes {@code figure.willChange}
 * and {@code figure.changed} when undoing or redoing a change.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CompositeFigureEdit extends CompositeEdit {
    private static final long serialVersionUID = 1L;

    private Figure figure;

    /**
     * Creates a new {@code CompositeFigureEdit} which uses
     * CompoundEdit.getPresentatioName and is significant.
     *
     * @see javax.swing.undo.CompoundEdit#getPresentationName()
     */
    public CompositeFigureEdit(Figure figure) {
        this.figure = figure;
    }

    /**
     * Creates new CompositeFigureEdit which uses the specified significance.
     *
     * @see javax.swing.undo.CompoundEdit#getPresentationName()
     */
    public CompositeFigureEdit(Figure figure, boolean isSignificant) {
        super(isSignificant);
        this.figure = figure;
    }

    /**
     * Creates new CompositeFigureEdit which uses the specified presentation name.
     *
     * @see javax.swing.undo.CompoundEdit#getPresentationName()
     */
    public CompositeFigureEdit(Figure figure, String presentationName) {
        super(presentationName);
        this.figure = figure;
    }

    /**
     * Creates new CompositeEdit.
     * Which uses the given presentation name.
     * If the presentation name is null, then CompoundEdit.getPresentatioName
     * is used.
     * @see javax.swing.undo.CompoundEdit#getPresentationName()
     */
    public CompositeFigureEdit(Figure figure, String presentationName, boolean isSignificant) {
        super(presentationName, isSignificant);
        this.figure = figure;
    }

    @Override
    public void undo() {
        if (!canUndo()) {
            throw new CannotUndoException();
        }
        figure.willChange();
        super.undo();
        figure.changed();
    }
    @Override
    public void redo() {
        if (!canRedo()) {
            throw new CannotRedoException();
        }
        figure.willChange();
        super.redo();
        figure.changed();
    }
}
