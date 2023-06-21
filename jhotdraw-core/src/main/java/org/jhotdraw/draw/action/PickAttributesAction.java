/*
 * @(#)PickAttributesAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.util.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.event.FigureSelectionEvent;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

/** PickAttributesAction. */
public class PickAttributesAction extends AbstractSelectedAction {

  private static final long serialVersionUID = 1L;
  private Set<AttributeKey<?>> excludedAttributes =
      new HashSet<>(Arrays.asList(new AttributeKey<?>[] {TRANSFORM, TEXT}));

  public PickAttributesAction(DrawingEditor editor) {
    super(editor);
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
    labels.configureAction(this, "edit.pickAttributes");
    updateEnabledState();
  }

  /**
   * Set of attributes that is excluded when applying default attributes. By default, the TRANSFORM
   * attribute is excluded.
   */
  public void setExcludedAttributes(Set<AttributeKey<?>> a) {
    this.excludedAttributes = a;
  }

  @Override
  public void actionPerformed(java.awt.event.ActionEvent e) {
    pickAttributes();
  }

  @SuppressWarnings("unchecked")
  public void pickAttributes() {
    DrawingEditor editor = getEditor();
    Collection<Figure> selection = getView().getSelectedFigures();
    if (selection.size() > 0) {
      Figure figure = selection.iterator().next();
      for (Map.Entry<AttributeKey<?>, Object> entry : figure.attr().getAttributes().entrySet()) {
        if (!excludedAttributes.contains(entry.getKey())) {
          editor.setDefaultAttribute((AttributeKey<Object>) entry.getKey(), entry.getValue());
        }
      }
    }
  }

  public void selectionChanged(FigureSelectionEvent evt) {
    setEnabled(getView().getSelectionCount() == 1);
  }
}
