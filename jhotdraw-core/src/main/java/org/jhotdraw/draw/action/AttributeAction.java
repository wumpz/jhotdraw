/*
 * @(#)AttributeAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import java.util.*;
import javax.swing.*;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.util.ActionUtil;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * {@code AttributeAction} applies attribute values on the selected figures of the current {@code
 * DrawingView} of a {@code DrawingEditor}.
 */
public class AttributeAction extends AbstractSelectedAction {

  private static final long serialVersionUID = 1L;
  protected Map<AttributeKey<?>, Object> attributes;

  public <T> AttributeAction(DrawingEditor editor, AttributeKey<T> key, T value) {
    this(editor, key, value, null, null);
  }

  public <T> AttributeAction(DrawingEditor editor, AttributeKey<T> key, T value, Icon icon) {
    this(editor, key, value, null, icon);
  }

  public <T> AttributeAction(DrawingEditor editor, AttributeKey<T> key, T value, String name) {
    this(editor, key, value, name, null);
  }

  public <T> AttributeAction(
      DrawingEditor editor, AttributeKey<T> key, T value, String name, Icon icon) {
    this(editor, key, value, name, icon, null);
  }

  public <T> AttributeAction(
      DrawingEditor editor,
      AttributeKey<T> key,
      T value,
      String name,
      Icon icon,
      Action compatibleTextAction) {
    super(editor);
    this.attributes = new HashMap<>();
    attributes.put(key, value);
    putValue(AbstractAction.NAME, name);
    putValue(AbstractAction.SMALL_ICON, icon);
    putValue(ActionUtil.UNDO_PRESENTATION_NAME_KEY, key.getPresentationName());
    updateEnabledState();
  }

  public AttributeAction(
      DrawingEditor editor, Map<AttributeKey<?>, Object> attributes, String name, Icon icon) {
    super(editor);
    this.attributes = (attributes == null) ? new HashMap<>() : attributes;
    putValue(AbstractAction.NAME, name);
    putValue(AbstractAction.SMALL_ICON, icon);
    updateEnabledState();
  }

  @Override
  public void actionPerformed(java.awt.event.ActionEvent evt) {
    applyAttributesTo(attributes, getView().getSelectedFigures());
  }

  /**
   * Applies the specified attributes to the currently selected figures of the drawing.
   *
   * @param a The attributes.
   * @param figures The figures to which the attributes are applied.
   */
  @SuppressWarnings("unchecked")
  public void applyAttributesTo(final Map<AttributeKey<?>, Object> a, Set<Figure> figures) {
    for (Map.Entry<AttributeKey<?>, Object> entry : a.entrySet()) {
      getEditor().setDefaultAttribute((AttributeKey<Object>) entry.getKey(), entry.getValue());
    }
    final ArrayList<Figure> selectedFigures = new ArrayList<>(figures);
    final ArrayList<Object> restoreData = new ArrayList<>(selectedFigures.size());
    for (Figure figure : selectedFigures) {
      restoreData.add(figure.attr().getAttributesRestoreData());
      figure.willChange();
      for (Map.Entry<AttributeKey<?>, Object> entry : a.entrySet()) {
        figure.attr().set((AttributeKey<Object>) entry.getKey(), entry.getValue());
      }
      figure.changed();
    }
    UndoableEdit edit = new AbstractUndoableEdit() {
      private static final long serialVersionUID = 1L;

      @Override
      public String getPresentationName() {
        String name = (String) getValue(ActionUtil.UNDO_PRESENTATION_NAME_KEY);
        if (name == null) {
          name = (String) getValue(AbstractAction.NAME);
        }
        if (name == null) {
          ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
          name = labels.getString("attribute.text");
        }
        return name;
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
          for (Map.Entry<AttributeKey<?>, Object> entry : a.entrySet()) {
            figure.attr().set((AttributeKey<Object>) entry.getKey(), entry.getValue());
          }
          figure.changed();
        }
      }
    };
    getDrawing().fireUndoableEditHappened(edit);
  }

  @Override
  protected void updateEnabledState() {
    if (getEditor() != null) {
      setEnabled(getEditor().isEnabled());
    }
  }
}
