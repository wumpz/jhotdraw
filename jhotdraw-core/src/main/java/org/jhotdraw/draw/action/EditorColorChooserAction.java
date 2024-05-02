/*
 * @(#)EditorColorChooserAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.event.FigureSelectionEvent;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * EditorColorChooserAction.
 *
 * <p>The behavior for choosing the initial color of the JColorChooser matches with {@link
 * EditorColorIcon }.
 */
public class EditorColorChooserAction extends AttributeAction {

  private static final long serialVersionUID = 1L;
  protected AttributeKey<Color> key;
  protected static JColorChooser colorChooser;

  public EditorColorChooserAction(DrawingEditor editor, AttributeKey<Color> key) {
    this(editor, key, null, null);
    updateEnabledState();
  }

  public EditorColorChooserAction(DrawingEditor editor, AttributeKey<Color> key, Icon icon) {
    this(editor, key, null, icon);
  }

  public EditorColorChooserAction(DrawingEditor editor, AttributeKey<Color> key, String name) {
    this(editor, key, name, null);
  }

  public EditorColorChooserAction(
      DrawingEditor editor, final AttributeKey<Color> key, String name, Icon icon) {
    this(editor, key, name, icon, new HashMap<AttributeKey<?>, Object>());
  }

  public EditorColorChooserAction(
      DrawingEditor editor,
      final AttributeKey<Color> key,
      String name,
      Icon icon,
      Map<AttributeKey<?>, Object> fixedAttributes) {
    super(editor, fixedAttributes, name, icon);
    this.key = key;
    putValue(AbstractAction.NAME, name);
    putValue(AbstractAction.SMALL_ICON, icon);
    updateEnabledState();
  }

  @Override
  public void actionPerformed(java.awt.event.ActionEvent e) {
    if (colorChooser == null) {
      colorChooser = new JColorChooser();
    }
    Color initialColor = getInitialColor();
    // FIXME - Reuse colorChooser object instead of calling static method here.
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    Color chosenColor = JColorChooser.showDialog(
        (Component) e.getSource(), labels.getString("attribute.color.text"), initialColor);
    if (chosenColor != null) {
      HashMap<AttributeKey<?>, Object> attr = new HashMap<>(attributes);
      attr.put(key, chosenColor);
      applyAttributesTo(attr, getView().getSelectedFigures());
    }
  }

  public void selectionChanged(FigureSelectionEvent evt) {
    // setEnabled(getView().getSelectionCount() > 0);
  }

  protected Color getInitialColor() {
    Color initialColor = getEditor().getDefaultAttribute(key);
    if (initialColor == null) {
      initialColor = Color.red;
    }
    return initialColor;
  }
}
