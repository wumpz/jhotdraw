/*
 * @(#)BezierNodeEdit.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.event;

import java.awt.geom.Point2D;
import java.util.function.Consumer;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import org.jhotdraw.draw.figure.BezierFigure;
import org.jhotdraw.draw.figure.Figure;

/** An {@code UndoableEdit} event which can undo a change of a node in a {@link BezierFigure}. */
public class TrackingEdit extends AbstractUndoableEdit {
  private final Figure owner;
  private final Point2D.Double oldValue;
  private Point2D.Double newValue;
  private final Consumer<Point2D.Double> writeLocation;

  public TrackingEdit(
      Figure owner,
      Consumer<Point2D.Double> writeLocation,
      Point2D.Double oldValue,
      Point2D.Double newValue) {
    this.owner = owner;
    this.writeLocation = writeLocation;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  @Override
  public void redo() throws CannotRedoException {
    super.redo();
    owner.willChange();
    writeLocation.accept(newValue);
    owner.changed();
  }

  @Override
  public void undo() throws CannotUndoException {
    super.undo();
    owner.willChange();
    writeLocation.accept(oldValue);
    owner.changed();
  }

  @Override
  public boolean addEdit(UndoableEdit anEdit) {
    if (anEdit instanceof TrackingEdit that) {
      if (that.owner == this.owner && that.writeLocation == this.writeLocation) {
        this.newValue = that.newValue;
        return true;
      }
    }
    return false;
  }
}
