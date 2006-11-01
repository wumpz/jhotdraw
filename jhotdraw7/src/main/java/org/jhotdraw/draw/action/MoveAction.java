/*
 * @(#)MoveAction.java  1.0  17. M�rz 2004
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
import org.jhotdraw.draw.TransformEdit;
import org.jhotdraw.undo.CompositeEdit;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.undo.*;

/**
 * MoveAction.
 *
 * @author  Werner Randelshofer
 * @version 1.0 17. M�rz 2004  Created.
 */
public abstract class MoveAction extends AbstractSelectedAction {
    private int dx, dy;
    
    /** Creates a new instance. */
    public MoveAction(DrawingEditor editor, int dx, int dy) {
        super(editor);
        this.dx = dx;
        this.dy = dy;
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        CompositeEdit edit;
        AffineTransform tx = new AffineTransform();
        tx.translate(dx, dy);
        for (Figure f : getView().getSelectedFigures()) {
            f.willChange();
            f.basicTransform(tx);
            f.changed();
        }
       fireUndoableEditHappened(new TransformEdit(getView().getSelectedFigures(), tx));

    }
    
    public static class East extends MoveAction {
        public East(DrawingEditor editor) {
            super(editor, 1, 0);
            labels.configureAction(this, "moveEast");
        }
    }
    public static class West extends MoveAction {
        public West(DrawingEditor editor) {
            super(editor, -1, 0);
            labels.configureAction(this, "moveWest");
        }
    }
    public static class North extends MoveAction {
        public North(DrawingEditor editor) {
            super(editor, 0, -1);
            labels.configureAction(this, "moveNorth");
        }
    }
    public static class South extends MoveAction {
        public South(DrawingEditor editor) {
            super(editor, 0, 1);
            labels.configureAction(this, "moveSouth");
        }
    }
}
