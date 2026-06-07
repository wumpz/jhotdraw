/*
 * @(#)AlignAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.event.TransformEdit;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.utils.undo.CompositeEdit;
import org.jhotdraw.utils.util.ResourceBundleUtil;

/**
 * Aligns the selected figures.
 *
 * <p>Refactoring: Form Template Method (Kerievsky). The shared loop skeleton has been pulled up
 * into {@link #alignFigures} as a final template method. Each subclass provides only the
 * per-figure offset via {@link #getAlignmentTransform}.
 *
 * <p>XXX - Fire edit events
 */
public abstract class AlignAction extends AbstractSelectedAction {

  private static final long serialVersionUID = 1L;
  protected ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");

  public AlignAction(DrawingEditor editor) {
    super(editor);
    updateEnabledState();
  }

  @Override
  public void updateEnabledState() {
    if (getView() != null) {
      setEnabled(getView().isEnabled() && getView().getSelectionCount() > 1);
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

  /**
   * Template method: iterates over selected figures and applies the transform returned by {@link
   * #getAlignmentTransform} to each transformable figure.
   *
   * <p>Subclasses must not override this method; they specialise only {@link
   * #getAlignmentTransform}.
   */
  protected final void alignFigures(
      Collection<Figure> selectedFigures, Rectangle2D.Double selectionBounds) {
    for (Figure f : getView().getSelectedFigures()) {
      if (f.isTransformable()) {
        f.willChange();
        AffineTransform tx = getAlignmentTransform(f.getBounds(), selectionBounds);
        f.transform(tx);
        f.changed();
        fireUndoableEditHappened(new TransformEdit(f, tx));
      }
    }
  }

  /**
   * Returns the {@link AffineTransform} that moves {@code figureBounds} to its aligned position
   * within {@code selectionBounds}. Implemented by each subclass with a single expression.
   *
   * @param figureBounds the current bounds of the figure being aligned
   * @param selectionBounds the union of all selected figures' bounds
   * @return a pure-translation transform for this figure
   */
  protected abstract AffineTransform getAlignmentTransform(
      Rectangle2D.Double figureBounds, Rectangle2D.Double selectionBounds);

  /** Returns the bounds of the selected figures. */
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
    protected AffineTransform getAlignmentTransform(Rectangle2D.Double b, Rectangle2D.Double sel) {
      AffineTransform tx = new AffineTransform();
      tx.translate(0, sel.y - b.y);
      return tx;
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
    protected AffineTransform getAlignmentTransform(Rectangle2D.Double b, Rectangle2D.Double sel) {
      AffineTransform tx = new AffineTransform();
      tx.translate(sel.x + sel.width - b.x - b.width, 0);
      return tx;
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
    protected AffineTransform getAlignmentTransform(Rectangle2D.Double b, Rectangle2D.Double sel) {
      AffineTransform tx = new AffineTransform();
      tx.translate(sel.x - b.x, 0);
      return tx;
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
    protected AffineTransform getAlignmentTransform(Rectangle2D.Double b, Rectangle2D.Double sel) {
      AffineTransform tx = new AffineTransform();
      tx.translate(0, sel.y + sel.height - b.y - b.height);
      return tx;
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
    protected AffineTransform getAlignmentTransform(Rectangle2D.Double b, Rectangle2D.Double sel) {
      AffineTransform tx = new AffineTransform();
      tx.translate(0, sel.y + sel.height / 2 - b.y - b.height / 2);
      return tx;
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
    protected AffineTransform getAlignmentTransform(Rectangle2D.Double b, Rectangle2D.Double sel) {
      AffineTransform tx = new AffineTransform();
      tx.translate(sel.x + sel.width / 2 - b.x - b.width / 2, 0);
      return tx;
    }
  }
}
