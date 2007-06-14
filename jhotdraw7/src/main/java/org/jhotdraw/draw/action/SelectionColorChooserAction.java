/*
 * @(#)EditorColorChooserAction.java  2.0  2006-06-07
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.draw.action;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.beans.*;
import javax.swing.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.undo.CompositeEdit;
/**
 * This is loke EditorColorChooserAction, but the JColorChooser is initialized with
 * the color of the currently selected Figures.
 * <p>
 * The behavior for choosing the initial color of the JColorChooser matches with
 * {@see SelectionColorIcon }.
 * 
 * @author Werner Randelshofer
 * @version 2.0 2006-06-07 Reworked.
 * <br>1.0 2004-03-02  Created.
 */
public class SelectionColorChooserAction extends EditorColorChooserAction {
    
    /** Creates a new instance. */
    public SelectionColorChooserAction(DrawingEditor editor, AttributeKey<Color> key) {
        this(editor, key, null, null);
    }
    /** Creates a new instance. */
    public SelectionColorChooserAction(DrawingEditor editor, AttributeKey<Color> key, Icon icon) {
        this(editor, key, null, icon);
    }
    /** Creates a new instance. */
    public SelectionColorChooserAction(DrawingEditor editor, AttributeKey<Color> key, String name) {
        this(editor, key, name, null);
    }
    public SelectionColorChooserAction(DrawingEditor editor, final AttributeKey<Color> key, String name, Icon icon) {
        this(editor, key, name, icon, new HashMap<AttributeKey,Object>());
    }
    public SelectionColorChooserAction(DrawingEditor editor, final AttributeKey<Color> key, String name, Icon icon,
            Map<AttributeKey,Object> fixedAttributes) {
        super(editor, key, name, icon, fixedAttributes);
    }
    
    protected Color getInitialColor() {
        Color initialColor = null;
        
        DrawingView v = getEditor().getActiveView();
        if (v != null && v.getSelectedFigures().size() == 1) {
            Figure f = v.getSelectedFigures().iterator().next();
            initialColor = key.get(f);
        }
        if (initialColor == null) {
            initialColor = super.getInitialColor();;
        }
        return initialColor;
    }
}
