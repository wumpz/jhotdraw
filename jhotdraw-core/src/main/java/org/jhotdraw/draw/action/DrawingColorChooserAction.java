/*
 * @(#)DrawingColorChooserAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * The DrawingColorChooserAction changes a color attribute of the Drawing object
 * in the current view of the DrawingEditor.
 * <p>
 * The behavior for choosing the initial color of the JColorChooser matches with
 * {@link DrawingColorIcon }.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DrawingColorChooserAction extends EditorColorChooserAction {
    private static final long serialVersionUID = 1L;

    /** Creates a new instance. */
    public DrawingColorChooserAction(DrawingEditor editor, AttributeKey<Color> key) {
        this(editor, key, null, null);
    }

    /** Creates a new instance. */
    public DrawingColorChooserAction(DrawingEditor editor, AttributeKey<Color> key, @Nullable Icon icon) {
        this(editor, key, null, icon);
    }

    /** Creates a new instance. */
    public DrawingColorChooserAction(DrawingEditor editor, AttributeKey<Color> key, @Nullable String name) {
        this(editor, key, name, null);
    }

    public DrawingColorChooserAction(DrawingEditor editor, final AttributeKey<Color> key, @Nullable String name, @Nullable Icon icon) {
        this(editor, key, name, icon, new HashMap<AttributeKey<?>, Object>());
    }

    public DrawingColorChooserAction(DrawingEditor editor, final AttributeKey<Color> key, @Nullable String name, @Nullable Icon icon,
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
        ResourceBundleUtil labels =
                ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        Color chosenColor = JColorChooser.showDialog((Component) e.getSource(), labels.getString("attribute.color.text"), initialColor);
        if (chosenColor != null) {
            HashMap<AttributeKey<?>, Object> attr = new HashMap<AttributeKey<?>, Object>(attributes);
            attr.put(key, chosenColor);
            HashSet<Figure> figures = new HashSet<Figure>();
            figures.add(getView().getDrawing());
            applyAttributesTo(attr, figures);
        }
    }

    @Override
    protected Color getInitialColor() {
        Color initialColor = null;

        DrawingView v = getEditor().getActiveView();
        if (v != null) {
            Figure f = v.getDrawing();
            initialColor = f.get(key);
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
