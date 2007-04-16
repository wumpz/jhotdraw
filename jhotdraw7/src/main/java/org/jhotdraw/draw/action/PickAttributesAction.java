/*
 * @(#)PickAttributesAction.java  1.1  2007-04-16
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

import org.jhotdraw.util.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import org.jhotdraw.draw.*;
import static org.jhotdraw.draw.AttributeKeys.*;
/**
 * PickAttributesAction.
 *
 * @author Werner Randelshofer
 * @version 1.1 2007-04-16 Added support exclusion of attributes.
 * <br>1.0 25. November 2003  Created.
 */
public class PickAttributesAction extends AbstractSelectedAction {
    private ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
    
    private Set<AttributeKey> excludedAttributes = new HashSet<AttributeKey>(
            Arrays.asList(new AttributeKey[] { TRANSFORM, TEXT }));
    
    /** Creates a new instance. */
    public PickAttributesAction(DrawingEditor editor) {
        super(editor);
        labels.configureAction(this, "attributesPick");
        setEnabled(true);
    }
    
    /**
     * Set of attributes that is excluded when applying default attributes.
     * By default, the TRANSFORM attribute is excluded.
     * @see AttributeKeys.TRANSFORM
     */
    public void setExcludedAttributes(Set<AttributeKey> a) {
        this.excludedAttributes = a;
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        pickAttributes();
    }
    
    public void pickAttributes() {
        DrawingEditor editor = getEditor();
        Collection<Figure> selection = getView().getSelectedFigures();
        if (selection.size() > 0) {
            Figure figure = (Figure) selection.iterator().next();
            for (Map.Entry<AttributeKey, Object> entry : figure.getAttributes().entrySet()) {
                if (! excludedAttributes.contains(entry.getKey())) {
                    editor.setDefaultAttribute(entry.getKey(), entry.getValue());
                }
            }
        }
    }
    public void selectionChanged(FigureSelectionEvent evt) {
        setEnabled(getView().getSelectionCount() == 1);
    }
}
