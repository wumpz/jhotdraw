/*
 * @(#)SelectSameAction.java  1.1  2006-06-05
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

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import java.util.*;
import javax.swing.*;
/**
 * SelectSameAction.
 *
 * @author  Werner Randelshofer
 * @version 1.1 2006-06-05 Optimized performance.
 * <br>1.0 25. November 2003  Created.
 */
public class SelectSameAction extends AbstractSelectedAction {
    
    /** Creates a new instance. */
    public SelectSameAction(DrawingEditor editor) {
        super(editor);
        putValue(AbstractAction.NAME, labels.getString("editSelectSame"));
        //  putValue(AbstractAction.MNEMONIC_KEY, labels.getString("editSelectSameMnem"));
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        selectSame();
    }
    
    public void selectSame() {
        HashSet<Class> selectedClasses = new HashSet<Class>();
        for (Figure selected : getView().getSelectedFigures()) {
            selectedClasses.add(selected.getClass());
        }
        for (Figure f : getDrawing().getFigures()) {
            if (selectedClasses.contains(f.getClass())) {
                getView().addToSelection(f);
            }
        }
    }
}
