/*
 * @(#)TransformEdit.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.event;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.utils.util.ResourceBundleUtil;

/**
 * An {@code UndoableEdit} event which can undo a lossless transform of {@link Figure}s by applying
 * the inverse of the transform to the figures.
 *
 * <p>This object is useful for undoing lossless transformations, such as the translation of
 * figures.
 *
 * <p>If a lossy transforms is performed, such as rotation, scaling or shearing, then undos should
 * be performed with {@link TransformRestoreEdit} instead.
 */
public class TransformEdit extends AbstractUndoableEdit {

  private static final long serialVersionUID = 1L;
  private Collection<Figure> figures;
  private AffineTransform tx;

  public TransformEdit(Figure figure, AffineTransform tx) {
    figures = new ArrayList<>();
    figures.add(figure);
    this.tx = (AffineTransform) tx.clone();
  }

  public TransformEdit(Collection<Figure> figures, AffineTransform tx) {
    this.figures = figures;
    this.tx = (AffineTransform) tx.clone();
  }

  @Override
  public String getPresentationName() {
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    return labels.getString("edit.transform.text");
  }

  @Override
  public boolean addEdit(UndoableEdit anEdit) {
    if (anEdit instanceof TransformEdit) {
      TransformEdit that = (TransformEdit) anEdit;
      if (that.figures == this.figures) {
        this.tx.concatenate(that.tx);
        that.die();
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean replaceEdit(UndoableEdit anEdit) {
    if (anEdit instanceof TransformEdit) {
      TransformEdit that = (TransformEdit) anEdit;
      if (that.figures == this.figures) {
        this.tx.preConcatenate(that.tx);
        that.die();
        return true;
      }
    }
    return false;
  }

  @Override
  public void redo() throws CannotRedoException {
    super.redo();
    for (Figure f : figures) {
      f.willChange();
      f.transform(tx);
      f.changed();
    }
  }

  @Override
  public void undo() throws CannotUndoException {
    super.undo();
    try {
      AffineTransform inverse = tx.createInverse();
      for (Figure f : figures) {
        f.willChange();
        f.transform(inverse);
        f.changed();
      }
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String toString() {
    return getClass().getName() + '@' + hashCode() + " tx:" + tx;
  }
}
