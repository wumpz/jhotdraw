/*
 * @(#)GroupAction.java  2.0  2007-12-21
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
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
import org.jhotdraw.undo.*;
import java.util.*;
import javax.swing.*;
import javax.swing.undo.*;

/**
 * GroupAction.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2007-12-21 Refactored this class, so that it can be used as
 * a base class for UngroupAction. 
 * <br>1.1 2006-07-12 Changed to support any CompositeFigure.
 * <br>1.0.1 2006-07-09 Fixed enabled state.
 * <br>1.0 24. November 2003  Created.
 */
public class GroupAction extends AbstractSelectedAction {

    public final static String ID = "selectionGroup";
    private CompositeFigure prototype;
    /**
     * If this variable is true, this action groups figures.
     * If this variable is false, this action ungroups figures.
     */
    private boolean isGroupingAction;

    /** Creates a new instance. */
    public GroupAction(DrawingEditor editor) {
        this(editor, new GroupFigure(), true);
    }

    public GroupAction(DrawingEditor editor, CompositeFigure prototype) {
        this(editor, prototype, true);
    }

    public GroupAction(DrawingEditor editor, CompositeFigure prototype, boolean isGroupingAction) {
        super(editor);
        this.prototype = prototype;
        this.isGroupingAction = isGroupingAction;
        labels.configureAction(this, ID);
    }

    @Override
    protected void updateEnabledState() {
        if (getView() != null) {
            setEnabled(isGroupingAction ? canGroup() : canUngroup());
        } else {
            setEnabled(false);
        }
    }

    protected boolean canGroup() {
        return getView().getSelectionCount() > 1;
    }

    protected boolean canUngroup() {
        return getView().getSelectionCount() == 1 &&
                getView().getSelectedFigures().iterator().next().getClass().equals(
                prototype.getClass());
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (isGroupingAction) {
            if (canGroup()) {
                final DrawingView view = getView();
                final LinkedList<Figure> ungroupedFigures = new LinkedList<Figure>(view.getSelectedFigures());
                final CompositeFigure group = (CompositeFigure) prototype.clone();
                CompositeEdit edit = new CompositeEdit(labels.getString("selectionGroup")) {

                    public void redo() throws CannotRedoException {
                        super.redo();
                        groupFigures(view, group, ungroupedFigures);
                    }

                    public void undo() throws CannotUndoException {
                        ungroupFigures(view, group);
                        super.undo();
                    }

                    public boolean addEdit(UndoableEdit anEdit) {
                        return super.addEdit(anEdit);
                    }
                };
                fireUndoableEditHappened(edit);
                groupFigures(view, group, ungroupedFigures);
                fireUndoableEditHappened(edit);
            }
        } else {
            if (canUngroup()) {
                final DrawingView view = getView();
                final CompositeFigure group = (CompositeFigure) getView().getSelectedFigures().iterator().next();
                final LinkedList<Figure> ungroupedFigures = new LinkedList<Figure>();
                CompositeEdit edit = new CompositeEdit(labels.getString("selectionUngroup")) {

                    public void redo() throws CannotRedoException {
                        super.redo();
                        ungroupFigures(view, group);
                    }

                    public void undo() throws CannotUndoException {
                        groupFigures(view, group, ungroupedFigures);
                        super.undo();
                    }
                };
                fireUndoableEditHappened(edit);
                ungroupedFigures.addAll(ungroupFigures(view, group));
                fireUndoableEditHappened(edit);
            }
        }
    }

    public Collection<Figure> ungroupFigures(DrawingView view, CompositeFigure group) {
// XXX - This code is redundant with UngroupAction
        LinkedList<Figure> figures = new LinkedList<Figure>(group.getChildren());
        view.clearSelection();
        group.basicRemoveAllChildren();
        view.getDrawing().basicAddAll(view.getDrawing().getChildCount(), figures);
        view.getDrawing().remove(group);
        view.addToSelection(figures);
        return figures;
    }

    public void groupFigures(DrawingView view, CompositeFigure group, Collection<Figure> figures) {
        Collection<Figure> sorted = view.getDrawing().sort(figures);
        view.getDrawing().basicRemoveAll(figures);
        view.clearSelection();
        view.getDrawing().add(group);
        group.willChange();
        for (Figure f : sorted) {
            group.basicAdd(f);
        }
        group.changed();
        view.addToSelection(group);
    }
}
