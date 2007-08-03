/*
 * @(#)MoveConstrainedAction.java  2.0  2007-07-31
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

import org.jhotdraw.draw.*;
import org.jhotdraw.undo.CompositeEdit;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.undo.*;
import static org.jhotdraw.draw.AttributeKeys.*;

/**
 * Moves the selected figures by one constrained unit.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2007-07-31 Reworked to take advantage of the new
 * Constrainer.moveRectangle method. 
 * <br>1.0 17. March 2004  Created.
 */
public abstract class MoveConstrainedAction extends AbstractSelectedAction {
    
    private Direction dir;
    
    /** Creates a new instance. */
    public MoveConstrainedAction(DrawingEditor editor, Direction dir) {
        super(editor);
        this.dir = dir;
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        Rectangle2D.Double r = null;
        for (Figure f : getView().getSelectedFigures()) {
            if (r == null) {
                r = f.getBounds();
            } else {
                r.add(f.getBounds());
            }
        }
        
        Point2D.Double p0 = new Point2D.Double(r.x, r.y);
        if (getView().getConstrainer() != null) {
            getView().getConstrainer().moveRectangle(r, dir);
        } else {
            switch (dir) {
                case NORTH :
                    r.y -= 1;
                    break;
                case SOUTH :
                    r.y += 1;
                    break;
                case WEST :
                    r.x -= 1;
                    break;
                case EAST :
                    r.x += 1;
                    break;
            }
        }
        
        AffineTransform tx = new AffineTransform();
        tx.translate(r.x - p0.x, r.y - p0.y);
        for (Figure f : getView().getSelectedFigures()) {
            f.willChange();
            f.transform(tx);
            f.changed();
        }
        CompositeEdit edit;
        fireUndoableEditHappened(new TransformEdit(getView().getSelectedFigures(), tx));
    }
    
    
    public static class East extends MoveConstrainedAction {
        public final static String ID = "moveConstrainedEast";
        
        public East(DrawingEditor editor) {
            super(editor, Direction.EAST);
            labels.configureAction(this, ID);
        }
    }
    public static class West extends MoveConstrainedAction {
        public final static String ID = "moveConstrainedWest";
        public West(DrawingEditor editor) {
            super(editor, Direction.WEST);
            labels.configureAction(this, ID);
        }
    }
    public static class North extends MoveConstrainedAction {
        public final static String ID = "moveConstrainedNorth";
        public North(DrawingEditor editor) {
            super(editor, Direction.NORTH);
            labels.configureAction(this, ID);
        }
    }
    public static class South extends MoveConstrainedAction {
        public final static String ID = "moveConstrainedSouth";
        public South(DrawingEditor editor) {
            super(editor, Direction.SOUTH);
            labels.configureAction(this, ID);
        }
    }
}
