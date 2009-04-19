/*
 * @(#)AttributeAction.java  3.0  2007-05-12
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
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

import javax.swing.undo.*;
import org.jhotdraw.app.action.Actions;
import org.jhotdraw.undo.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import java.awt.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * AttributeAction.
 *
 * @author Werner Randelshofer
 * @version 3.0 207-05-12 Method setAttribute in interface Figure does not
 * handle undo/redo anymore, we must do this by ourselves.
 * <br>2.0 2006-06-07 Reworked.
 * <br>1.1 2006-02-27 Support for compatible text action added.
 * <br>1.0 25. November 2003  Created.
 */
public class DrawingAttributeAction extends AbstractDrawingViewAction {

    protected Map<AttributeKey, Object> attributes;

    /** Creates a new instance. */
    /** Creates a new instance. */
    public DrawingAttributeAction(DrawingEditor editor, AttributeKey key, Object value) {
        this(editor, key, value, null, null);
    }

    /** Creates a new instance. */
    public DrawingAttributeAction(DrawingEditor editor, AttributeKey key, Object value, Icon icon) {
        this(editor, key, value, null, icon);
    }

    /** Creates a new instance. */
    public DrawingAttributeAction(DrawingEditor editor, AttributeKey key, Object value, String name) {
        this(editor, key, value, name, null);
    }

    public DrawingAttributeAction(DrawingEditor editor, AttributeKey key, Object value, String name, Icon icon) {
        this(editor, key, value, name, icon, null);
    }

    public DrawingAttributeAction(DrawingEditor editor, AttributeKey key, Object value, String name, Icon icon, Action compatibleTextAction) {
        super(editor);
        this.attributes = new HashMap<AttributeKey, Object>();
        attributes.put(key, value);

        putValue(AbstractAction.NAME, name);
        putValue(AbstractAction.SMALL_ICON, icon);
        setEnabled(true);
    }

    public DrawingAttributeAction(DrawingEditor editor, Map<AttributeKey, Object> attributes, String name, Icon icon) {
        super(editor);
        this.attributes = attributes;

        putValue(AbstractAction.NAME, name);
        putValue(AbstractAction.SMALL_ICON, icon);
        updateEnabledState();
    }

    @SuppressWarnings("unchecked")
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        final ArrayList<Object> restoreData = new ArrayList<Object>();
        final Figure drawing = getView().getDrawing();
        restoreData.add(drawing.getAttributesRestoreData());
        drawing.willChange();
        for (Map.Entry<AttributeKey, Object> entry : attributes.entrySet()) {
            entry.getKey().basicSet(drawing, entry.getValue());
        }
        drawing.changed();

        UndoableEdit edit = new AbstractUndoableEdit() {

            @Override
            public String getPresentationName() {
                String name = (String) getValue(Actions.UNDO_PRESENTATION_NAME_KEY);
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
                drawing.restoreAttributesTo(iRestore.next());
                drawing.changed();
            }

            @Override
    @SuppressWarnings("unchecked")
            public void redo() {
                super.redo();
                restoreData.add(drawing.getAttributesRestoreData());
                drawing.willChange();
                for (Map.Entry<AttributeKey, Object> entry : attributes.entrySet()) {
                    entry.getKey().basicSet(drawing, entry.getValue());
                }
                drawing.changed();
            }
        };
        fireUndoableEditHappened(edit);
    }
}
