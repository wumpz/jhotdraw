/*
 * @(#)AlignAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.event.TransformEdit;
import org.jhotdraw.undo.CompositeEdit;
import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * Aligns the selected figures.
 *
 * XXX - Fire edit events
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AlignAction extends AbstractSelectedAction {
    private static final long serialVersionUID = 1L;

    protected ResourceBundleUtil labels =
            ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");

    /**
     * Creates a new instance.
     */
    public AlignAction(DrawingEditor editor) {
        super(editor);
        updateEnabledState();
    }

    @Override
    public void updateEnabledState() {
        if (getView() != null) {
            setEnabled(getView().isEnabled()
                    && getView().getSelectionCount() > 1);
        } else {
            setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        CompositeEdit edit = new CompositeEdit(labels.getString("edit.align.text"));
        fireUndoableEditHappened(edit);
        alignFigures(getView().getSelectedFigures(), getSelectionBounds());
        fireUndoableEditHappened(edit);
    }

    protected abstract void alignFigures(Collection<Figure> selectedFigures, Rectangle2D.Double selectionBounds);

    /**
     * Returns the bounds of the selected figures.
     */
    protected Rectangle2D.Double getSelectionBounds() {
        Rectangle2D.Double bounds = null;
        for (Figure f : getView().getSelectedFigures()) {
            if (bounds == null) {
                bounds = f.getBounds();
            } else {
                bounds.add(f.getBounds());
            }
        }
        return bounds;
    }

    public static class North extends AlignAction {
    private static final long serialVersionUID = 1L;

        public North(DrawingEditor editor) {
            super(editor);
            labels.configureAction(this, "edit.alignNorth");
        }

        public North(DrawingEditor editor, ResourceBundleUtil labels) {
            super(editor);
            labels.configureAction(this, "edit.alignNorth");
        }

        @Override
        protected void alignFigures(Collection<Figure> selectedFigures, Rectangle2D.Double selectionBounds) {
            double y = selectionBounds.y;
            for (Figure f : getView().getSelectedFigures()) {
                if (f.isTransformable()) {
                    f.willChange();
                    Rectangle2D.Double b = f.getBounds();
                    AffineTransform tx = new AffineTransform();
                    tx.translate(0, y - b.y);
                    f.transform(tx);
                    f.changed();
                    fireUndoableEditHappened(new TransformEdit(f, tx));
                }
            }
        }
    }

    public static class East extends AlignAction {
    private static final long serialVersionUID = 1L;

        public East(DrawingEditor editor) {
            super(editor);
            labels.configureAction(this, "edit.alignEast");
        }

        public East(DrawingEditor editor, ResourceBundleUtil labels) {
            super(editor);
            labels.configureAction(this, "edit.alignEast");
        }

        @Override
        protected void alignFigures(Collection<Figure> selectedFigures, Rectangle2D.Double selectionBounds) {
            double x = selectionBounds.x + selectionBounds.width;
            for (Figure f : getView().getSelectedFigures()) {
                if (f.isTransformable()) {
                    f.willChange();
                    Rectangle2D.Double b = f.getBounds();
                    AffineTransform tx = new AffineTransform();
                    tx.translate(x - b.x - b.width, 0);
                    f.transform(tx);
                    f.changed();
                    fireUndoableEditHappened(new TransformEdit(f, tx));
                }
            }
        }
    }

    public static class West extends AlignAction {
    private static final long serialVersionUID = 1L;

        public West(DrawingEditor editor) {
            super(editor);
            labels.configureAction(this, "edit.alignWest");
        }

        public West(DrawingEditor editor, ResourceBundleUtil labels) {
            super(editor);
            labels.configureAction(this, "edit.alignWest");
        }

        @Override
        protected void alignFigures(Collection<Figure> selectedFigures, Rectangle2D.Double selectionBounds) {
            double x = selectionBounds.x;
            for (Figure f : getView().getSelectedFigures()) {
                if (f.isTransformable()) {
                    f.willChange();
                    Rectangle2D.Double b = f.getBounds();
                    AffineTransform tx = new AffineTransform();
                    tx.translate(x - b.x, 0);
                    f.transform(tx);
                    f.changed();
                    fireUndoableEditHappened(new TransformEdit(f, tx));
                }
            }
        }
    }

    public static class South extends AlignAction {
    private static final long serialVersionUID = 1L;

        public South(DrawingEditor editor) {
            super(editor);
            labels.configureAction(this, "edit.alignSouth");
        }

        public South(DrawingEditor editor, ResourceBundleUtil labels) {
            super(editor);
            labels.configureAction(this, "edit.alignSouth");
        }

        @Override
        protected void alignFigures(Collection<Figure> selectedFigures, Rectangle2D.Double selectionBounds) {
            double y = selectionBounds.y + selectionBounds.height;
            for (Figure f: getView().getSelectedFigures()) {
                if (f.isTransformable()) {
                    f.willChange();
                    Rectangle2D.Double b = f.getBounds();
                    AffineTransform tx = new AffineTransform();
                    tx.translate(0, y - b.y - b.height);
                    f.transform(tx);
                    f.changed();
                    fireUndoableEditHappened(new TransformEdit(f, tx));
                }
            }
        }
    }

    public static class Vertical extends AlignAction {
    private static final long serialVersionUID = 1L;

        public Vertical(DrawingEditor editor) {
            super(editor);
            labels.configureAction(this, "edit.alignVertical");
        }

        public Vertical(DrawingEditor editor, ResourceBundleUtil labels) {
            super(editor);
            labels.configureAction(this, "edit.alignVertical");
        }

        @Override
        protected void alignFigures(Collection<Figure> selectedFigures, Rectangle2D.Double selectionBounds) {
            double y = selectionBounds.y + selectionBounds.height / 2;
            for (Figure f : getView().getSelectedFigures()) {
                if (f.isTransformable()) {
                    f.willChange();
                    Rectangle2D.Double b = f.getBounds();
                    AffineTransform tx = new AffineTransform();
                    tx.translate(0, y - b.y - b.height / 2);
                    f.transform(tx);
                    f.changed();
                    fireUndoableEditHappened(new TransformEdit(f, tx));
                }
            }
        }
    }

    public static class Horizontal extends AlignAction {
    private static final long serialVersionUID = 1L;

        public Horizontal(DrawingEditor editor) {
            super(editor);
            labels.configureAction(this, "edit.alignHorizontal");
        }

        public Horizontal(DrawingEditor editor, ResourceBundleUtil labels) {
            super(editor);
            labels.configureAction(this, "edit.alignHorizontal");
        }

        @Override
        protected void alignFigures(Collection<Figure> selectedFigures, Rectangle2D.Double selectionBounds) {
            double x = selectionBounds.x + selectionBounds.width / 2;
            for (Figure f : getView().getSelectedFigures()) {
                if (f.isTransformable()) {
                    f.willChange();
                    Rectangle2D.Double b = f.getBounds();
                    AffineTransform tx = new AffineTransform();
                    tx.translate(x - b.x - b.width / 2, 0);
                    f.transform(tx);
                    f.changed();
                    fireUndoableEditHappened(new TransformEdit(f, tx));
                }
            }
        }
    }
}