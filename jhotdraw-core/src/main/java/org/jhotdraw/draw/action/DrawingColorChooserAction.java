/*
 * @(#)DrawingColorChooserAction.java
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
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * The DrawingColorChooserAction changes a color attribute of the Drawing object in the current view
 * of the DrawingEditor.
 *
 * <p>The behavior for choosing the initial color of the JColorChooser matches with {@link
 * DrawingColorIcon }.
 */
public class DrawingColorChooserAction extends EditorColorChooserAction {

  private static final long serialVersionUID = 1L;

  public DrawingColorChooserAction(DrawingEditor editor, AttributeKey<Color> key) {
    this(editor, key, null, null);
  }

  public DrawingColorChooserAction(DrawingEditor editor, AttributeKey<Color> key, Icon icon) {
    this(editor, key, null, icon);
  }

  public DrawingColorChooserAction(DrawingEditor editor, AttributeKey<Color> key, String name) {
    this(editor, key, name, null);
  }

  public DrawingColorChooserAction(
      DrawingEditor editor, final AttributeKey<Color> key, String name, Icon icon) {
    this(editor, key, name, icon, new HashMap<AttributeKey<?>, Object>());
  }

  public DrawingColorChooserAction(
      DrawingEditor editor,
      final AttributeKey<Color> key,
      String name,
      Icon icon,
      Map<AttributeKey<?>, Object> fixedAttributes) {
    super(editor, key, name, icon, fixedAttributes);
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
      HashSet<Figure> figures = new HashSet<>();
      figures.addAll(getView().getDrawing().getChildren());
      applyAttributesTo(attr, figures);
    }
  }

  @Override
  protected Color getInitialColor() {
    Color initialColor = null;
    DrawingView v = getEditor().getActiveView();
    if (v != null) {
      Drawing f = v.getDrawing();
      initialColor = f.attr().get(key);
    }
    if (initialColor == null) {
      initialColor = super.getInitialColor();
    }
    return initialColor;
  }

  @Override
  protected void updateEnabledState() {
    if (getView() != null) {
      setEnabled(getView().isEnabled());
    } else {
      setEnabled(false);
    }
  }
}
