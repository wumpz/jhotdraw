/*
 * @(#)DefaultAttributeAction.java  2.0  2006-06-07
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

import org.jhotdraw.undo.*;
import javax.swing.*;
import java.awt.*;
import java.beans.*;
import java.util.*;
import org.jhotdraw.draw.*;
/**
 * DefaultAttributeAction.
 * <p>
 * XXX - should listen to changes in the default attributes of its DrawingEditor.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2006-06-07 Reworked.
 * <br>1.0 26. November 2003  Created.
 */
public class DefaultAttributeAction extends AbstractSelectedAction {
    private AttributeKey[] keys;
    
    
    /** Creates a new instance. */
    public DefaultAttributeAction(DrawingEditor editor, AttributeKey key) {
        this(editor, key, null, null);
    }
    public DefaultAttributeAction(DrawingEditor editor, AttributeKey[] keys) {
        this(editor, keys, null, null);
    }
    /** Creates a new instance. */
    public DefaultAttributeAction(DrawingEditor editor, AttributeKey key, Icon icon) {
        this(editor, key, null, icon);
    }
    /** Creates a new instance. */
    public DefaultAttributeAction(DrawingEditor editor, AttributeKey key, String name) {
        this(editor, key, name, null);
    }
    public DefaultAttributeAction(DrawingEditor editor, AttributeKey key, String name, Icon icon) {
        this(editor, new AttributeKey[] {key}, name, icon);
    }
    public DefaultAttributeAction(DrawingEditor editor, AttributeKey[] keys, String name, Icon icon) {
        super(editor);
        this.keys = keys;
        putValue(AbstractAction.NAME, name);
        putValue(AbstractAction.SMALL_ICON, icon);
        setEnabled(true);
        editor.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(DefaultAttributeAction.this.keys[0])) {
                    putValue("attribute_"+DefaultAttributeAction.this.keys[0], evt.getNewValue());
                }
            }

        })
        ;
    }
    
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (getView() != null && getView().getSelectionCount() > 0) {
            CompositeEdit edit = new CompositeEdit(labels.getString("drawAttributeChange"));
            fireUndoableEditHappened(edit);
            changeAttribute();
            fireUndoableEditHappened(edit);
        }
    }
    
    public void changeAttribute() {
        Drawing drawing = getDrawing();
        Iterator i = getView().getSelectedFigures().iterator();
        while (i.hasNext()) {
            Figure figure = (Figure) i.next();
            for (int j=0; j < keys.length; j++) {
            figure.setAttribute(keys[j], getEditor().getDefaultAttribute(keys[j]));
            }
        }
    }
    public void selectionChanged(FigureSelectionEvent evt) {
        //setEnabled(getView().getSelectionCount() > 0);
    }
}
