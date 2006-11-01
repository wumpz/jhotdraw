/*
 * @(#)AttributeAction.java  2.0  2006-06-07
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
import javax.swing.text.*;
import java.util.*;
import java.awt.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.geom.*;
/**
 * AttributeAction.
 * 
 * @author Werner Randelshofer
 * @version 2.0 2006-06-07 Reworked.
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
        setEnabled(true);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (getView() != null && getView().getSelectionCount() > 0) {
            CompositeEdit edit = new CompositeEdit(labels.getString("drawAttributeChange"));
            fireUndoableEditHappened(edit);
            changeAttributes();
            fireUndoableEditHappened(edit);
        }
    }
    
    public void changeAttributes() {
        Drawing drawing = getDrawing();
        for (Map.Entry<AttributeKey, Object> entry : attributes.entrySet()) {
            AttributeKey key = entry.getKey();
            Object value = entry.getValue();
            Iterator i = getView().getSelectedFigures().iterator();
            while (i.hasNext()) {
                Figure figure = (Figure) i.next();
                figure.setAttribute(key, value);
            }
            getEditor().setDefaultAttribute(key, value);
        }
    }
}
