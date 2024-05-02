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
import org.jhotdraw.util.ActionUtil;
import org.jhotdraw.util.ResourceBundleUtil;

/** AttributeAction. */
public class DrawingAttributeAction extends AbstractDrawingViewAction {

  private static final long serialVersionUID = 1L;
  protected Map<AttributeKey<?>, Object> attributes;

  public <T> DrawingAttributeAction(DrawingEditor editor, AttributeKey<T> key, T value) {
    this(editor, key, value, null, null);
  }

  public <T> DrawingAttributeAction(DrawingEditor editor, AttributeKey<T> key, T value, Icon icon) {
    this(editor, key, value, null, icon);
  }

  public <T> DrawingAttributeAction(
      DrawingEditor editor, AttributeKey<T> key, T value, String name) {
    this(editor, key, value, name, null);
  }

  public <T> DrawingAttributeAction(
      DrawingEditor editor, AttributeKey<T> key, T value, String name, Icon icon) {
    this(editor, key, value, name, icon, null);
  }

  public <T> DrawingAttributeAction(
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
    setEnabled(true);
  }

  public DrawingAttributeAction(
      DrawingEditor editor, Map<AttributeKey<?>, Object> attributes, String name, Icon icon) {
    super(editor);
    this.attributes = attributes;
    putValue(AbstractAction.NAME, name);
    putValue(AbstractAction.SMALL_ICON, icon);
    updateEnabledState();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void actionPerformed(java.awt.event.ActionEvent evt) {
    final ArrayList<Object> restoreData = new ArrayList<>();
    final Drawing drawing = getView().getDrawing();
    restoreData.add(drawing.attr().getAttributesRestoreData());
    drawing.willChange();
    for (Map.Entry<AttributeKey<?>, Object> entry : attributes.entrySet()) {
      drawing.attr().set((AttributeKey<Object>) entry.getKey(), entry.getValue());
    }
    drawing.changed();
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
        drawing.willChange();
        drawing.attr().restoreAttributesTo(iRestore.next());
        drawing.changed();
      }

      @Override
      @SuppressWarnings("unchecked")
      public void redo() {
        super.redo();
        // restoreData.add(drawing.getAttributesRestoreData());
        drawing.willChange();
        for (Map.Entry<AttributeKey<?>, Object> entry : attributes.entrySet()) {
          drawing.attr().set((AttributeKey<Object>) entry.getKey(), entry.getValue());
        }
        drawing.changed();
      }
    };
    fireUndoableEditHappened(edit);
  }
}
