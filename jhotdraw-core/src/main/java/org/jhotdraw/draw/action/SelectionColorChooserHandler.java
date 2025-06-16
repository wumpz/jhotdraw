/**
 * @(#)SelectionColorChooserHandler.java
 *
 * <p>Copyright (c) 2010 The authors and contributors of JHotDraw. You may not use, copy or modify
 * this file, except in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.action;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.Figure;

/** SelectionColorChooserHandler. */
public class SelectionColorChooserHandler extends AbstractSelectedAction implements ChangeListener {

  private static final long serialVersionUID = 1L;
  protected AttributeKey<Color> key;
  protected JColorChooser colorChooser;
  protected JPopupMenu popupMenu;
  protected int isUpdating;

  // protected Map<AttributeKey, Object> attributes;

  public SelectionColorChooserHandler(
      DrawingEditor editor,
      AttributeKey<Color> key,
      JColorChooser colorChooser,
      JPopupMenu popupMenu) {
    super(editor);
    this.key = key;
    this.colorChooser = colorChooser;
    this.popupMenu = popupMenu;
    // colorChooser.addActionListener(this);
    colorChooser.getSelectionModel().addChangeListener(this);
    updateEnabledState();
  }

  @Override
  public void actionPerformed(java.awt.event.ActionEvent evt) {
    /*
    if (evt.getActionCommand() == JColorChooser.APPROVE_SELECTION) {
        applySelectedColorToFigures();
    } else if (evt.getActionCommand() == JColorChooser.CANCEL_SELECTION) {
    }*/
    popupMenu.setVisible(false);
  }

  protected void applySelectedColorToFigures() {
    final ArrayList<Figure> selectedFigures = new ArrayList<>(getView().getSelectedFigures());
    final ArrayList<Object> restoreData = new ArrayList<>(selectedFigures.size());
    Color selectedColor = colorChooser.getColor();
    if (selectedColor != null && selectedColor.getAlpha() == 0) {
      selectedColor = null;
    }
    for (Figure figure : selectedFigures) {
      restoreData.add(figure.attr().getAttributesRestoreData());
      figure.willChange();
      figure.attr().set(key, selectedColor);
      figure.changed();
    }
    getEditor().setDefaultAttribute(key, selectedColor);
    final Color undoValue = selectedColor;
    UndoableEdit edit = new AbstractUndoableEdit() {
      private static final long serialVersionUID = 1L;

      @Override
      public String getPresentationName() {
        return AttributeKeys.FONT_FACE.getPresentationName();
        /*
        String name = (String) getValue(Actions.UNDO_PRESENTATION_NAME_KEY);
        if (name == null) {
        name = (String) getValue(AbstractAction.NAME);
        }
        if (name == null) {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        name = labels.getString("attribute.text");
        }
        return name;*/
      }

      @Override
      public void undo() {
        super.undo();
        Iterator<Object> iRestore = restoreData.iterator();
        for (Figure figure : selectedFigures) {
          figure.willChange();
          figure.attr().restoreAttributesTo(iRestore.next());
          figure.changed();
        }
      }

      @Override
      public void redo() {
        super.redo();
        for (Figure figure : selectedFigures) {
          // restoreData.add(figure.getAttributesRestoreData());
          figure.willChange();
          figure.attr().set(key, undoValue);
          figure.changed();
        }
      }
    };
    fireUndoableEditHappened(edit);
  }

  @Override
  protected void updateEnabledState() {
    setEnabled(getEditor().isEnabled());
    if (getView() != null && colorChooser != null && popupMenu != null) {
      colorChooser.setEnabled(getView().getSelectionCount() > 0);
      popupMenu.setEnabled(getView().getSelectionCount() > 0);
      isUpdating++;
      if (getView().getSelectionCount() > 0 /*&& colorChooser.isShowing()*/) {
        for (Figure f : getView().getSelectedFigures()) {
          Color figureColor = f.attr().get(key);
          colorChooser.setColor(figureColor == null ? new Color(0, true) : figureColor);
          break;
        }
      }
      isUpdating--;
    }
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    if (isUpdating++ == 0) {
      applySelectedColorToFigures();
    }
    isUpdating--;
  }
}
