/*
 * @(#)AttributeAction.java  3.0  2007-05-12
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
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

import javax.swing.undo.*;
import org.jhotdraw.undo.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import java.awt.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.geom.*;
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
public class AttributeAction extends AbstractSelectedAction {
    protected Map<AttributeKey, Object> attributes;
    
    /** Creates a new instance. */
    /** Creates a new instance. */
    public AttributeAction(DrawingEditor editor, AttributeKey key, Object value) {
        this(editor, key, value, null, null);
    }
    /** Creates a new instance. */
    public AttributeAction(DrawingEditor editor, AttributeKey key, Object value, Icon icon) {
        this(editor, key, value, null, icon);
    }
    /** Creates a new instance. */
    public AttributeAction(DrawingEditor editor, AttributeKey key, Object value, String name) {
        this(editor, key, value, name, null);
    }
    public AttributeAction(DrawingEditor editor, AttributeKey key, Object value, String name, Icon icon) {
        this(editor, key, value, name, icon, null);
    }
    public AttributeAction(DrawingEditor editor, AttributeKey key, Object value, String name, Icon icon, Action compatibleTextAction) {
        super(editor);
        this.attributes = new HashMap<AttributeKey,Object>();
        attributes.put(key, value);
        
        putValue(AbstractAction.NAME, name);
        putValue(AbstractAction.SMALL_ICON, icon);
        setEnabled(true);
    }
    public AttributeAction(DrawingEditor editor, Map<AttributeKey, Object> attributes, String name, Icon icon) {
        super(editor);
        this.attributes = attributes;
        
        putValue(AbstractAction.NAME, name);
        putValue(AbstractAction.SMALL_ICON, icon);
        updateEnabledState();
    }
    
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        for (Map.Entry<AttributeKey, Object> entry : attributes.entrySet()) {
            getEditor().setDefaultAttribute(entry.getKey(), entry.getValue());
        }
        
        final ArrayList<Figure> selectedFigures = new ArrayList(getView().getSelectedFigures());
        final ArrayList<Object> restoreData = new ArrayList<Object>(selectedFigures.size());
        Iterator i = selectedFigures.iterator();
        for (Figure figure : selectedFigures) {
            restoreData.add(figure.getAttributesRestoreData());
            figure.willChange();
            for (Map.Entry<AttributeKey, Object> entry : attributes.entrySet()) {
                entry.getKey().set(figure, entry.getValue());
            }
            figure.changed();
        }
        UndoableEdit edit = new AbstractUndoableEdit() {
            public String getPresentationName() {
                return labels.getString("drawAttributeChange");
            }
            public void undo() {
                super.undo();
                Iterator<Object> iRestore = restoreData.iterator();
                for (Figure figure : selectedFigures) {
                    figure.willChange();
                    figure.restoreAttributesTo(iRestore.next());
                    figure.changed();
                }
            }
            public void redo() {
                super.redo();
                for (Figure figure : selectedFigures) {
                    restoreData.add(figure.getAttributesRestoreData());
                    figure.willChange();
                    for (Map.Entry<AttributeKey, Object> entry : attributes.entrySet()) {
                        entry.getKey().set(figure, entry.getValue());
                    }
                    figure.changed();
                }
            }
        };
        fireUndoableEditHappened(edit);
    }
    protected void updateEnabledState() {
        setEnabled(getEditor().isEnabled());
    }
}
