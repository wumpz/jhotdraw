/*
 * @(#)SendToBackAction.java
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
import org.jhotdraw.draw.figure.AbstractAttributedFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.utils.util.ResourceBundleUtil;

public class SendToBackAction extends AbstractSelectedAction {

  private static final long serialVersionUID = 1L;
  public static final String ID = "edit.sendToBack";

  public SendToBackAction(DrawingEditor editor) {
    super(editor);
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    labels.configureAction(this, ID);
    updateEnabledState();
  }

  @Override
  public void actionPerformed(java.awt.event.ActionEvent e) {
    final DrawingView view = getView();
    final List<Figure> figures = new ArrayList<>(view.getSelectedFigures());
    final int[] layer = figures.stream().mapToInt(f -> f.getLayer()).toArray();

    sendToBack(view, figures);

    final int[] newLayer = figures.stream().mapToInt(f -> f.getLayer()).toArray();

    fireUndoableEditHappened(new AbstractUndoableEdit() {
      private static final long serialVersionUID = 1L;

      @Override
      public String getPresentationName() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        return labels.getTextProperty(ID);
      }

      @Override
      public void redo() throws CannotRedoException {
        super.redo();
        setLayer(figures, newLayer);
        SendToBackAction.sendToBack(view, figures);
      }

      @Override
      public void undo() throws CannotUndoException {
        super.undo();
        setLayer(figures, layer);
        BringToFrontAction.bringToFront(view, figures);
      }
    });
  }

  public static void setLayer(List<Figure> figures, int[] layer) {
    for (int i = 0; i < layer.length; i++) {
      if (figures.get(i) instanceof AbstractAttributedFigure af) af.setLayer(layer[i]);
    }
  }

  public static void sendToBack(DrawingView view, Collection<Figure> figures) {
    Drawing drawing = view.getDrawing();
    for (Figure figure : figures) {
      if (figure instanceof AbstractAttributedFigure af) {
        af.willChange();
        af.setLayer(drawing.findFigures(figure.getBounds()).stream()
            .mapToInt(f -> f.getLayer() - 1)
            .min()
            .orElse(figure.getLayer()));
        af.changed();
      }
      drawing.sendToBack(figure);
    }
  }
}
