/*
 * @(#)BringToFrontAction.java
 *
 * Copyright (c) 2003-2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

/** ToFrontAction. */
public class BringToFrontAction extends AbstractSelectedAction {

  private static final long serialVersionUID = 1L;
  public static final String ID = "edit.bringToFront";

  public BringToFrontAction(DrawingEditor editor) {
    super(editor);
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    labels.configureAction(this, ID);
    updateEnabledState();
  }

  @Override
  public void actionPerformed(java.awt.event.ActionEvent e) {
    final DrawingView view = getView();
    final List<Figure> figures = new ArrayList<>(view.getSelectedFigures());
    bringToFront(view, figures);
    fireUndoableEditHappened(
        new AbstractUndoableEdit() {
          private static final long serialVersionUID = 1L;

          @Override
          public String getPresentationName() {
            ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
            return labels.getTextProperty(ID);
          }

          @Override
          public void redo() throws CannotRedoException {
            super.redo();
            BringToFrontAction.bringToFront(view, figures);
          }

          @Override
          public void undo() throws CannotUndoException {
            super.undo();
            SendToBackAction.sendToBack(view, figures);
          }
        });
  }

  public static void bringToFront(DrawingView view, Collection<Figure> figures) {
    Drawing drawing = view.getDrawing();
    for (Figure figure : drawing.sort(figures)) {
      drawing.bringToFront(figure);
    }
  }
}
