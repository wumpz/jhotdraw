/*
 * @(#)ToFrontAction.java  1.0  24. November 2003
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
import javax.swing.undo.*;
import org.jhotdraw.draw.*;

/**
 * ToFrontAction.
 *
 * @author  Werner Randelshofer
 * @version 1.0 24. November 2003  Created.
 */
public class MoveToFrontAction extends AbstractSelectedAction {
       private ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
    
    /** Creates a new instance. */
    public MoveToFrontAction(DrawingEditor editor) {
        super(editor);
        labels.configureAction(this, "moveToFront");
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        final DrawingView view = getView();
        final LinkedList<Figure> figures = new LinkedList<Figure>(view.getSelectedFigures());
        bringToFront(view, figures);
        fireUndoableEditHappened(new AbstractUndoableEdit() {
            public String getPresentationName() {
       return labels.getString("moveToFront");
            }
            public void redo() throws CannotRedoException {
                super.redo();
                MoveToFrontAction.bringToFront(view, figures);
            }
            public void undo() throws CannotUndoException {
                super.undo();
                MoveToBackAction.sendToBack(view, figures);
            }
        }
        );
    }
    public static void bringToFront(DrawingView view, Collection<Figure> figures) {
        Drawing drawing = view.getDrawing();
        Iterator i = drawing.sort(figures).iterator();
        while (i.hasNext()) {
            Figure figure = (Figure) i.next();
            drawing.bringToFront(figure);
        }
    }
    
}
