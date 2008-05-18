/*
 * @(#)DrawingColorChooserAction.java  1.0  2008-05-18
 *
 * Copyright (c) 1996-2008 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
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
 * The DrawingColorChooserAction changes a color attribute of the Drawing object
 * in the current view of the DrawingEditor.
 * <p>
 * The behavior for choosing the initial color of the JColorChooser matches with
 * {@link DrawingColorIcon }.
 * 
 * @author Werner Randelshofer
 * @version 1.0 2008-05-18 Created.
 */
public class DrawingColorChooserAction extends EditorColorChooserAction {
    
    /** Creates a new instance. */
    public DrawingColorChooserAction(DrawingEditor editor, AttributeKey<Color> key) {
        this(editor, key, null, null);
    }
    /** Creates a new instance. */
    public DrawingColorChooserAction(DrawingEditor editor, AttributeKey<Color> key, Icon icon) {
        this(editor, key, null, icon);
    }
    /** Creates a new instance. */
    public DrawingColorChooserAction(DrawingEditor editor, AttributeKey<Color> key, String name) {
        this(editor, key, name, null);
    }
    public DrawingColorChooserAction(DrawingEditor editor, final AttributeKey<Color> key, String name, Icon icon) {
        this(editor, key, name, icon, new HashMap<AttributeKey,Object>());
    }
    public DrawingColorChooserAction(DrawingEditor editor, final AttributeKey<Color> key, String name, Icon icon,
            Map<AttributeKey,Object> fixedAttributes) {
        super(editor, key, name, icon, fixedAttributes);
        if (this.fixedAttributes == null) {
            throw new NullPointerException();
        }
    }
    
    @Override
    public void changeAttribute(Color value) {
        if (fixedAttributes == null) {
            throw new NullPointerException();
        }
        CompositeEdit edit = new CompositeEdit("attributes");
        fireUndoableEditHappened(edit);
        Drawing drawing = getDrawing();
       Figure figure = getView().getDrawing();
            figure.willChange();
            key.basicSet(figure, value);
            for (Map.Entry<AttributeKey,Object> entry : fixedAttributes.entrySet()) {
                entry.getKey().basicSet(figure, entry.getValue());
            }
            figure.changed();
        //getEditor().setDefaultAttribute(key, value);
        fireUndoableEditHappened(edit);
    }
    @Override
    protected Color getInitialColor() {
        Color initialColor = null;
        
        DrawingView v = getEditor().getActiveView();
        if (v != null) {
            Figure f = v.getDrawing();
            initialColor = key.get(f);
        }
        if (initialColor == null) {
            initialColor = super.getInitialColor();
        }
        return initialColor;
    }
    protected void updateEnabledState() {
        if (getView() != null) {
            setEnabled(getView().isEnabled());
        } else {
            setEnabled(false);
        }
    }
    
}
